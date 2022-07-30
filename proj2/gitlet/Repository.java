package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

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
            lastCommit = Commit.getInitCommit();
            currentCommit = Commit.initCurrentCommit();
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
    public static final File CURRENT_REPOSITORY = Utils.join(GITLET_DIR, "currentRepository");
    public static final File STAGING_AREA_DIR = join(GITLET_DIR, "stagingArea");


    private LinkedList<Branch> branches;
    private Branch currentBranch;
    private HashMap<String, Commit.Bolb> stage;


    /* TODO: fill in the rest of this class. */
    public Repository() {
        branches = new LinkedList<>();
        currentBranch = new Branch();
        branches.add(currentBranch);
        stage = null;
    }

    public void init() {
        GITLET_DIR.mkdir();
        STAGING_AREA_DIR.mkdir();
        Utils.writeObject(CURRENT_REPOSITORY, this);
    }

    public void add(String fileName) {
        currentBranch.currentCommit.add(fileName, new File(fileName), currentBranch.lastCommit);
    }
}
