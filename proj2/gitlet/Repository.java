package gitlet;


import java.io.*;
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

        writeContents(join(STAGING_AREA_DIR, sha1), content);
        stage.put(fileName, sha1);
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

    public boolean commit(String message) {
        if (stage.size() == 0 && stageRM.size() == 0) {
            System.out.println("No changes added to the commit.");
            return false;
        }

        HashMap<String, String> addedBolbs = new HashMap<>();
        // add
//        addedBolbs.putAll(lastCommit.bolbs);
        for (Map.Entry<String, String> entry: HEAD.bolbs.entrySet()) {
            addedBolbs.put(entry.getKey(), entry.getValue());
        }
//        addedBolbs.putAll(stage);
        for (Map.Entry<String, String> entry: stage.entrySet()) {
//            if (addedBolbs.containsKey(entry.getKey())) {
//                String overwriteSha1 = HEAD.bolbs.get(entry.getKey());
//            }
            addedBolbs.put(entry.getKey(), entry.getValue());
            File stageFile = join(STAGING_AREA_DIR, entry.getValue());
            if (stageFile.exists()) {
                writeContents(join(BOLBS_DIR, entry.getValue()), readContents(stageFile));
            }
        }
        // rm
        for (String rmFile: stageRM) {
            addedBolbs.remove(rmFile);
        }


        Commit newCommit = new Commit(new Date(),
                                        message,
                                        addedBolbs,
                                        Utils.sha1(Utils.serialize(HEAD)));
        HEAD = newCommit;
        branches.put(currentBranch, newCommit);
        checkoutBranchCleanStage();

        return true;
    }

    public boolean rm(String fileName) {
        if (stage.containsKey(fileName)) {
            String rmSha1 = stage.get(fileName);
            join(STAGING_AREA_DIR, rmSha1).delete();
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
        Formatter fmt = new Formatter(Locale.ENGLISH);
        Date cal = commit.timeStamp;
        fmt.format("%ta %tb %td %tR:%tS %tY %tz", cal, cal, cal, cal, cal, cal, cal);
        System.out.println("Date: " + fmt);
        System.out.println(commit.message);
        System.out.println();
    }

    public void status() {
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
            if (CWDFile.exists()) {
//                System.out.println(CWDFile);
                String CWDSha1 = sha1(readContents(CWDFile));
                if (CWDSha1.equals(entry.getValue())) {
                    continue;
                }
            }
            writeContents(CWDFile, readContents(bolbFile));
        }
        checkoutBranchCleanStage();
    }

    private void checkoutBranchCleanStage() {
//        for (Map.Entry<String, String> entry: stage.entrySet()) {
//            File deleteFile = join(BOLBS_DIR, entry.getValue());
//            deleteFile.delete();
//        }
        for (Map.Entry<String, String> entry: stage.entrySet()) {
            File deleteFile = join(STAGING_AREA_DIR, entry.getValue());
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

    public void merge(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        if (stage.size() != 0 || stageRM.size() != 0) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        if (branchName.equals(currentBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }


        Commit branchCommit = branches.get(branchName);
        Commit currentBranchCommit = branches.get(currentBranch);
        Commit LCA = findLatestCommonAncestor(currentBranchCommit, branchCommit);
        if (sha1(serialize(LCA)).equals(sha1(serialize(branchCommit)))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (sha1(serialize(LCA)).equals(sha1(serialize(currentBranchCommit)))) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        boolean isConflict = processMerge(currentBranchCommit, branchCommit, LCA);
        commit("Merged " + branchName + " into " + currentBranch + ".");

        if (isConflict) {
            System.out.println("Encountered a merge conflict.");
        }



    }

    private Commit findLatestCommonAncestor(Commit A, Commit B) {
        Set<String> AFather = new HashSet<>();
        while (!A.parentSha1.equals("")) {
            AFather.add(sha1(serialize(A)));
            A = getCommit(A.parentSha1);
        }
        AFather.add(sha1(serialize(A)));

        while (!B.parentSha1.equals("")) {
            String bSha1 = sha1(serialize(B));
            if (AFather.contains(bSha1)) {
                File readFile = join(COMMIT_AREA, bSha1);
                return readObject(readFile, Commit.class);
            }
            B = getCommit(B.parentSha1);
        }

        return null;
    }

    private boolean processMerge(Commit currentBranchCommit, Commit givenBranchCommit, Commit ancestorCommit) {
        HashMap<String, String> branchBlobs = new HashMap<>();
        branchBlobs.putAll(givenBranchCommit.bolbs);
        HashMap<String, String> currentBlobs = new HashMap<>();
        currentBlobs.putAll(currentBranchCommit.bolbs);
        boolean isConflict = false;

        for(Map.Entry<String, String> entry: ancestorCommit.bolbs.entrySet()) {
            String fileName = entry.getKey();
            String bSha1 = branchBlobs.get(fileName);
            String cSha1 = currentBlobs.get(fileName);

            if (branchBlobs.containsKey(fileName)) {
                if (!entry.getValue().equals(bSha1)) {
                    if (currentBlobs.containsKey(fileName)) {
                        if (!entry.getValue().equals(cSha1)) {
                            if (!currentBlobs.get(fileName).equals(bSha1)) {
                                conflict(cSha1, bSha1, fileName);
                                isConflict = true;
                            }
                        } else {
//                            writeContents(join(STAGING_AREA_DIR, bSha1), readContents(join(BOLBS_DIR, bSha1)));
                            stage.put(fileName, bSha1);
                            writeContents(join(CWD, fileName), readContents(join(BOLBS_DIR, bSha1)));
                        }
                    } else {
                        conflict(null, branchBlobs.get(fileName), fileName);
                        isConflict = true;
                    }
                }
            } else {
                if (currentBlobs.containsKey(fileName)) {
                    if (!entry.getValue().equals(cSha1)) {
                        conflict(cSha1, null, fileName);
                        isConflict = true;
                    } else {
                        rm(fileName);
                    }
                }
            }

            branchBlobs.remove(fileName);
            currentBlobs.remove(fileName);
        }

        for (Map.Entry<String, String> entry: branchBlobs.entrySet()) {
            String fileName = entry.getKey();
            String bSha1 = entry.getValue();
            if (currentBlobs.containsKey(fileName)) {
                if (bSha1.equals(currentBlobs.get(fileName))) {
                    stage.put(fileName, bSha1);
                    writeContents(join(CWD, fileName), readContents(join(BOLBS_DIR, bSha1)));
                } else {
                    conflict(null, bSha1, fileName);
                    isConflict = true;
                }
            } else {
                stage.put(fileName, bSha1);
                writeContents(join(CWD, fileName), readContents(join(BOLBS_DIR, bSha1)));
            }
        }

        return isConflict;
    }

    private void conflict(String currentFileSha1, String givenFileSha1, String fileName) {
        final String headFiled = "<<<<<<< HEAD\n";
        final String currentFiled = "=======\n";
        final String givenFiled = ">>>>>>>";

//        byte[] currentContent;
//        byte[] givenContent;
//
//        if (currentFileSha1 == null) {
//            currentContent = serialize(currentFiled);
//        } else {
//            File currentFile = join(BOLBS_DIR, currentFileSha1);
//            currentContent = concat(readContents(currentFile), serialize(currentFiled));
//        }
//
//        if (givenFileSha1 == null) {
//            givenContent = serialize(givenFiled);
//        } else {
//            File givenFile = join(BOLBS_DIR, givenFileSha1);
//            givenContent = concat(readContents(givenFile), serialize(givenFiled));
//        }
//
//        byte[] content = concat(currentContent, givenContent);
//        String sha1 = sha1(content);
//
//        writeContents(join(STAGING_AREA_DIR, sha1), content);
//        stage.put(fileName, sha1);
//        writeContents(join(CWD, fileName), content);

        File mergeFile = join(CWD, fileName);
        RandomAccessFile raf = null;

        String contents = headFiled +
                readContentsAsString(mergeFile) +
                currentFiled +
                readContentsAsString(join(BOLBS_DIR, givenFileSha1)) +
                givenFiled;
        String sha11 = sha1(contents);
        writeContents(join(STAGING_AREA_DIR, sha11), contents);
        writeContents(mergeFile, contents);
        stage.put(fileName, sha11);
        return;


//        try {
//            if (!mergeFile.exists()) {
//                mergeFile.createNewFile();
//            }
//            raf = new RandomAccessFile(mergeFile,"rw");
//            raf.seek(raf.length());
////            if (currentFileSha1 != null) {
////                File currentFile = join(BOLBS_DIR, currentFileSha1);
////                raf.write(readContents(currentFile));
////            }
////            raf.writeBytes("\n");
//            raf.writeBytes(currentFiled);
//            if (givenFileSha1 != null) {
//                File givenFile = join(BOLBS_DIR, givenFileSha1);
//                raf.write(readContents(givenFile));
//            }
//            raf.writeBytes(givenFiled);
//
//            raf.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        byte[] content = readContents(mergeFile);
//        String sha1 = sha1(content);
//        writeContents(join(STAGING_AREA_DIR, sha1), content);
//        stage.put(fileName, sha1);

    }

    private byte[] concat(byte[] A, byte[] B) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(A);
            baos.write(B);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] C = baos.toByteArray();

        return C;
    }

}
