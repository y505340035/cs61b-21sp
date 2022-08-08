package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Yyy
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGING_AREA_DIR = join(GITLET_DIR, "stagingArea");
    public static final File BOLBS_DIR = join(GITLET_DIR, "bolbs");
    public static final File CURRENT_REPOSITORY = join(GITLET_DIR, "currentRepository");
    public static final File COMMIT_AREA = join(GITLET_DIR, "commitArea");

    public final Commit initCommit;

    private Commit HEAD;
    private String currentBranch;
    private HashMap<String, Commit> branches;
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
        HEAD = initCommit;
        //init branches
        branches = new HashMap<>();
        currentBranch = "master";
        branches.put(currentBranch, HEAD);
        // when a blob's count == 0, delete it
        everyBlobCount = new HashMap<>();
        stage = new HashMap<>();
        stageRM = new HashSet<>();
    }

    private Commit getCommit(String sha1) {
        return readObject(join(COMMIT_AREA, sha1), Commit.class);
    }

    public void store() {
        Utils.writeObject(CURRENT_REPOSITORY, this);
    }

    public void add(String fileName) {
        byte[] content = readContents(join(CWD, fileName));
        String sha1 = sha1(content);
        stageRM.remove(fileName);
        if (HEAD.bolbs.containsKey(fileName) && (HEAD.bolbs.get(fileName).equals(sha1))) {
            if (stage.containsKey(fileName)) {
                String existSha1 = stage.get(fileName);
                restrictedDelete(join(BOLBS_DIR, existSha1));
                stage.remove(fileName);
            }
            return;
        }

        writeContents(join(BOLBS_DIR, sha1), content);
        stage.put(fileName, sha1);
        everyBlobCount.put(sha1, 1);
    }

    public void addReverse(String fileName) {
        byte[] content = readContents(join(CWD, fileName));
        String sha1 = sha1(content);
        for (Map.Entry<String, String> entry: HEAD.bolbs.entrySet()) {
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
        if (stage.size() == 0 && stageRM.size() == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }

        HashMap<String, String> addedBolbs = new HashMap<>();
        // add
//        addedBolbs.putAll(lastCommit.bolbs);
        for (Map.Entry<String, String> entry: HEAD.bolbs.entrySet()) {
            addedBolbs.put(entry.getKey(), entry.getValue());
            Integer count = everyBlobCount.get(entry.getValue());
            everyBlobCount.put(entry.getValue(), count++);
        }
//        addedBolbs.putAll(stage);
        for (Map.Entry<String, String> entry: stage.entrySet()) {
            if (addedBolbs.containsKey(entry.getKey())) {
                String overwriteSha1 = HEAD.bolbs.get(entry.getKey());
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
//            if (--rmCount <= 0) {
//                everyBlobCount.remove(rmSha1);
//                File deleteFile = join(BOLBS_DIR, rmSha1);
//                if (deleteFile.exists()){
//                    deleteFile.delete();
//                }
//            } else {
//                everyBlobCount.put(rmSha1, rmCount);
//            }
        }


        Commit newCommit = new Commit(new Date(),
                                        message,
                                        addedBolbs,
                                        Utils.sha1(Utils.serialize(HEAD)));
        HEAD = newCommit;
        branches.put(currentBranch, newCommit);
        stage = new HashMap<>();
        stageRM = new HashSet<>();
    }

    public boolean rm(String fileName) {
        if (stage.containsKey(fileName)) {
            String rmSha1 = stage.get(fileName);
            join(BOLBS_DIR, rmSha1).delete();
            everyBlobCount.remove(rmSha1);
            stage.remove(fileName);
            return true;
        }

        if (HEAD.bolbs.containsKey(fileName)) {
            stageRM.add(fileName);
            File rmFile = join(CWD, fileName);
            if (rmFile.exists()) {
                rmFile.delete();
            }
            return true;
        }

        return false;
    }

    public void log() {
        Commit iterCommit = HEAD;
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
        // TODO delete it!
//        System.out.println(everyBlobCount.toString());
        System.out.println("=== Branches ===");
        System.out.println("*" + currentBranch);
        Set<String> sortBranchesSet = new TreeSet<>(Comparator.reverseOrder());
        sortBranchesSet.addAll(branches.keySet());
        for (String branch: sortBranchesSet) {
            if (!branch.equals(currentBranch)) {
                System.out.println(branch);
            }
        }
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
            if (!HEAD.bolbs.containsKey(CWDFIle) && !stage.containsKey(CWDFIle)) {
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
        for (Map.Entry<String, String> entry: HEAD.bolbs.entrySet()) {
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
        return helpCheckout(fileName, HEAD);
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

    public boolean checkoutBranch(String branch) {
        if (!branches.containsKey(branch)) {
            System.out.println("No such branch exists.");
            return false;
        }
        if (branch.equals(currentBranch)) {
            System.out.println("No need to checkout the current branch.");
            return false;
        }
        for (String file:plainFilenamesIn(CWD)) {
            if (!HEAD.bolbs.containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return false;
            }
        }

        currentBranch = branch;
        HEAD = branches.get(currentBranch);
        helpReset(HEAD);
        return true;
    }

    private void helpReset(Commit commit) {
        for (String file:plainFilenamesIn(CWD)) {
            if (!commit.bolbs.containsKey(file)) {
                File deleteFile = join(CWD, file);
                deleteFile.delete();
            }
        }
        for (Map.Entry<String, String> entry: commit.bolbs.entrySet()) {
            File CWDFile = join(CWD, entry.getKey());
            File bolbFile = join(BOLBS_DIR, entry.getValue());
            if (CWDFile.exists() && CWDFile.isFile()) {
//                System.out.println(CWDFile);
                String CWDSha1 = sha1(readContents(CWDFile));
                if (CWDSha1.equals(entry.getValue())) {
                    continue;
                }
            }
//            if (bolbFile.isFile()) {
//                System.out.println("file!!!!!!!!!!!!!!!");
//            } else {
//                System.out.println("nooooooooooooooooooooooooooo");
//            }
            writeContents(CWDFile, readContents(bolbFile));
        }
        checkoutBranchCleanStage();
    }

    private void checkoutBranchCleanStage() {
        for (Map.Entry<String, String> entry: stage.entrySet()) {
            everyBlobCount.remove(entry.getValue());
            File deleteFile = join(BOLBS_DIR, entry.getValue());
            deleteFile.delete();
        }
        stage = new HashMap<>();
        stageRM = new HashSet<>();
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

    public boolean newBranch(String branch) {
        if (branches.containsKey(branch)) {
            return false;
        }

        branches.put(branch, HEAD);
        return true;
    }

    public void rmBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchName.equals(currentBranch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }

        branches.remove(branchName);
    }

    public void reset(String commitSha1) {
        File commitFile = join(COMMIT_AREA, commitSha1);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = getCommit(commitSha1);
        for (String file:plainFilenamesIn(CWD)) {
            if (!HEAD.bolbs.containsKey(file) && !commit.bolbs.containsValue(sha1(readContents(join(CWD, file))))) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        helpReset(commit);
        HEAD = commit;
        branches.put(currentBranch, HEAD);
    }

}
