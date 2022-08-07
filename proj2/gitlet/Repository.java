package gitlet;


import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    class Branch implements Serializable {
        String name;
        Commit lastCommit;
        Commit currentCommit;

        Branch() {
            name = "master";
//            lastCommit = Commit.getInitCommit();
//            currentCommit = Commit.initCurrentCommit();
        }

//        Branch(String name, Commit commit) {
//            this.name = name;
//            lastCommit = commit;
//            currentCommit = Commit.getInitCommit();
//        }
    }

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGING_AREA_DIR = join(GITLET_DIR, "stagingArea");
    public static final File BOLBS_DIR = join(GITLET_DIR, "bolbs");
    public static final File CURRENT_REPOSITORY = join(GITLET_DIR, "currentRepository");
    public static final File COMMIT_AREA = join(GITLET_DIR, "commitArea");

    public final Commit initCommit;


//    private LinkedList<Branch> branches;
//    private Branch currentBranch;
    private Commit lastCommit;
    private HashMap<String, Integer> everyBlobCount;
    private HashMap<String, String> stage;
    private Set<String> stageRM;


    /* TODO: fill in the rest of this class. */

    /**
     * gitlet init the dir
     */
    public Repository() {
        GITLET_DIR.mkdir();
        STAGING_AREA_DIR.mkdir();
        BOLBS_DIR.mkdir();
        COMMIT_AREA.mkdir();

        initCommit = new Commit();
        lastCommit = initCommit;
//        branches = new LinkedList<>();
//        currentBranch = new Branch();
//        branches.add(currentBranch);
        // when a blob's count == 0, delete it
        everyBlobCount = new HashMap<>();
        stage = new HashMap<>();
        stageRM = new HashSet<>();
    }

    public void store() {
        Utils.writeObject(CURRENT_REPOSITORY, this);
    }

    public void add(String fileName) {
        byte[] content = readContents(join(CWD, fileName));
        String sha1 = sha1(content);
        if (lastCommit.bolbs.containsKey(fileName) && (lastCommit.bolbs.get(fileName).equals(sha1))) {
            if (stage.containsKey(fileName)) {
                String existSha1 = stage.get(fileName);
                restrictedDelete(join(BOLBS_DIR, existSha1));
                stage.remove(fileName);
            }
            return;
        }

        writeContents(join(BOLBS_DIR, sha1), content);
        stage.put(fileName, sha1);
    }

    public void addReverse(String fileName) {
        byte[] content = readContents(join(CWD, fileName));
        String sha1 = sha1(content);
        for (Map.Entry<String, String> entry: lastCommit.bolbs.entrySet()) {
            if (sha1.equals(entry.getKey())) {
                return;
            }
            // overwrite the same fileName
//            if (fileName.equals(entry.getValue())) {
//                lastCommit.bolbs.remove(entry.getKey());
//            }
        }

        for (Map.Entry<String, String> entry: stage.entrySet()) {
            if (sha1.equals(entry.getKey())) {
                return;
            }
            // overwrite the same fileName
            if (fileName.equals(entry.getValue())) {
                stage.remove(entry.getKey());
            }
        }

        writeContents(join(BOLBS_DIR, sha1), content);
        stage.put(sha1, fileName);
    }

    public void commit(String message) {
        HashMap<String, String> addedBolbs = new HashMap<>();
        // add
//        addedBolbs.putAll(lastCommit.bolbs);
        for (Map.Entry<String, String> entry: lastCommit.bolbs.entrySet()) {
            addedBolbs.put(entry.getKey(), entry.getValue());
            Integer count = everyBlobCount.get(entry.getValue());
            everyBlobCount.put(entry.getValue(), count++);
        }
//        addedBolbs.putAll(stage);
        for (Map.Entry<String, String> entry: stage.entrySet()) {
            if (addedBolbs.containsKey(entry.getKey())) {
                String overwriteSha1 = lastCommit.bolbs.get(entry.getKey());
                Integer count = everyBlobCount.get(overwriteSha1);
                everyBlobCount.put(overwriteSha1, count--);
            }
            addedBolbs.put(entry.getKey(), entry.getValue());
            everyBlobCount.put(entry.getValue(), 1);
        }
        // rm
        for (String rmFile: stageRM) {
            String rmSha1 = addedBolbs.get(rmFile);
            addedBolbs.remove(rmFile);
            Integer rmCount = everyBlobCount.get(rmSha1);
            //delete useless blob
            if (--rmCount <= 0) {
                everyBlobCount.remove(rmSha1);
                File deleteFile = join(BOLBS_DIR, rmSha1);
                if (deleteFile.exists()){
                    deleteFile.delete();
                }
            } else {
                everyBlobCount.put(rmSha1, rmCount);
            }
        }


        Commit newCommit = new Commit(new Date(),
                                        message,
                                        addedBolbs,
                                        Utils.sha1(Utils.serialize(lastCommit)));
        lastCommit = newCommit;
        stage = new HashMap<>();
        stageRM = new HashSet<>();
    }

    public boolean rm(String fileName) {
        if (stage.containsKey(fileName)) {
            String rmSha1 = stage.get(fileName);
            restrictedDelete(join(BOLBS_DIR, rmSha1));
            stage.remove(fileName);
            return true;
        }

        if (lastCommit.bolbs.containsKey(fileName)) {
            stageRM.add(fileName);
            return true;
        }

        return false;
    }

    public void log() {
        Commit iterCommit = lastCommit;
        while (true) {
            printLog(iterCommit);
            if (iterCommit.parentSha1.equals("")) {
                break;
            }
            File commitFile = join(COMMIT_AREA, iterCommit.parentSha1);
            iterCommit = readObject(commitFile, Commit.class);
        }
    }

    public void globalLog() {
        Commit iterCommit;
        for (String commitFile: plainFilenamesIn(COMMIT_AREA)) {
            iterCommit = readObject(join(COMMIT_AREA, commitFile), Commit.class);
            printLog(iterCommit);
        }
    }

    public boolean find(String msg) {
        Commit iterCommit;
        boolean res = false;
        for (String commitFile: plainFilenamesIn(COMMIT_AREA)) {
            iterCommit = readObject(join(COMMIT_AREA, commitFile), Commit.class);
            if (msg.equals(iterCommit.message)) {
                System.out.println(commitFile);
                res = true;
            }
        }
        return res;
    }

    private void printLog(Commit commit) {
        String sha1 = Utils.sha1(Utils.serialize(commit));
        System.out.println("===");
        System.out.println("commit " + sha1);
        Formatter fmt = new Formatter();
        Date cal = commit.timeStamp;
        fmt.format("%ta %tb %td %tR:%tS %tY %tz", cal, cal, cal, cal, cal, cal, cal);
        System.out.println("Date: " + fmt);
        System.out.println(commit.message);
        System.out.println();
    }

    public void status() {
        System.out.println("=== Branches ===");
        // TODO print branches
        System.out.println();

        System.out.println("=== Staged Files ===");
        Set<String> sortStageSet = new TreeSet<>(Comparator.reverseOrder());
        sortStageSet.addAll(stage.keySet());
        for (String file: sortStageSet) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Set<String> sortStageRMSet = new TreeSet<>(Comparator.reverseOrder());
        sortStageRMSet.addAll(stageRM);
        for (String file: sortStageRMSet) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        Set<String> sortMNSSet = modificationNotStagedSet();
        for (String file: sortMNSSet) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        Set<String> sortUntrackedSet = new TreeSet<>(Comparator.reverseOrder());
        for (String CWDFIle: plainFilenamesIn(CWD)) {
            if (!lastCommit.bolbs.containsKey(CWDFIle) && !stage.containsKey(CWDFIle)) {
                sortUntrackedSet.add(CWDFIle);
            }
        }
        for (String file: sortUntrackedSet) {
            System.out.println(file);
        }
        System.out.println();
    }

    private Set<String> modificationNotStagedSet() {
        final String modified = " (modified)";
        final String deleted = " (deleted)";

        Set<String> sortSet = new TreeSet<>(Comparator.reverseOrder());
        // 1 & 4
        for (Map.Entry<String, String> entry: lastCommit.bolbs.entrySet()) {
            // 4
            File workFile =  join(CWD, entry.getKey());
            if (!workFile.exists() && !stageRM.contains(entry.getKey())) {
                sortSet.add(entry.getKey() + deleted);
                continue;
            }
            if (stageRM.contains(entry.getKey())) {
                continue;
            }

            // 1
            String workFileSha1 = sha1(readContents(workFile));
            if (!workFileSha1.equals(entry.getValue()) && !stage.containsKey(entry.getKey())) {
                sortSet.add(entry.getKey() + modified);
                continue;
            }
        }

        // 2 & 3
        for (Map.Entry<String, String> entry: stage.entrySet()) {
            // 3
            File workFile =  join(CWD, entry.getKey());
            if (!workFile.exists()) {
                sortSet.add(entry.getKey() + deleted);
                continue;
            }

            // 2
            String workFileSha1 = sha1(readContents(workFile));
            if (!entry.getValue().equals(workFileSha1)) {
                sortSet.add(entry.getKey() + modified);
                continue;
            }
        }

        return sortSet;
    }

    public boolean checkout(String fileName) {
        return helpCheckout(fileName, lastCommit);
    }

    public boolean checkout(String commitSha1, String fileName) {
        File commitFile = join(COMMIT_AREA, commitSha1);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return false;
        }

        Commit commit = readObject(commitFile, Commit.class);
        return helpCheckout(fileName, commit);
    }

    private boolean helpCheckout(String fileName, Commit commit) {
        if (!commit.bolbs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return false;
        }

        File checkoutFile = join(CWD, fileName);
        File blob = join(BOLBS_DIR, commit.bolbs.get(fileName));
        writeContents(checkoutFile, readContents(blob));
        return true;
    }

}
