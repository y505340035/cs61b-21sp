package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Yyy
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
//    class Bolb implements Serializable {
//        private String name;
//        private int version;
//        private File file;
//
//        Bolb(String n, int v, File f) {
//            name = n;
//            version = v;
//            file = f;
//        }
//    }

    public Date timeStamp;
    public String message;
    public HashMap<String, String> bolbs;
    public String parentSha1;

    /**
     * make init commit
     */
    public Commit() {
        this.timeStamp = new Date(0);
        this.message = "initial commit";
        this.bolbs = new HashMap<>();
        this.parentSha1 = "";
        String sha1 = Utils.sha1(Utils.serialize(this));
        Utils.writeObject(Utils.join(Repository.COMMIT_AREA, sha1), this);
    }

    public Commit(Date timeStamp, String message, HashMap<String, String> bolbs, String parentSha1) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.bolbs = bolbs;
        this.parentSha1 = parentSha1;
        String sha1 = Utils.sha1(Utils.serialize(this));
        Utils.writeObject(Utils.join(Repository.COMMIT_AREA, sha1), this);
    }
    /* TODO: fill in the rest of this class. */

    public void add(String name, File file, Commit lastCommit) {
//        int version = 1;
//        if (lastCommit.bolbs.containsKey(name)) {
//            Bolb oldBolb = lastCommit.bolbs.get(name);
//            String newSha1 = Utils.sha1(Utils.readContents(file));
//            String oldSha1 = Utils.sha1(Utils.readContents(oldBolb.file));
//            if (oldSha1.equals(newSha1)) {
//                return;
//            } else {
//                version = oldBolb.version;
//            }
//        }
//        File newFile = Utils.join(Repository.STAGING_AREA_DIR, name);
//        // if parent dir do not exist, then create it
//        File fileParent = newFile.getParentFile();
//        if(!fileParent.exists()) {
//            fileParent.mkdirs();
//        }
//
//        Utils.writeContents(newFile, Utils.readContents(file));
//        bolbs.put(name, new Bolb(name, version, newFile));
    }
}
