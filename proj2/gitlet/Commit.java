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

    public Date timeStamp;
    public String message;
    public HashMap<String, String> bolbs;
    public String parentSha1;
    private String secParentSha1;

    /**
     * make init commit
     */
    public Commit() {
        this.timeStamp = new Date(0);
        this.message = "initial commit";
        this.bolbs = new HashMap<>();
        this.parentSha1 = "";
        this.secParentSha1 = null;
        String sha1 = Utils.sha1(Utils.serialize(this));
        Utils.writeObject(Utils.join(Repository.COMMIT_AREA, sha1), this);
    }

    public Commit(Date timeStamp, String message, HashMap<String, String> bolbs, String parentSha1) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.bolbs = bolbs;
        this.parentSha1 = parentSha1;
        this.secParentSha1 = null;
        String sha1 = Utils.sha1(Utils.serialize(this));
        Utils.writeObject(Utils.join(Repository.COMMIT_AREA, sha1), this);
    }
    /* TODO: fill in the rest of this class. */

    public void setSecParentSha1(String sha1) {
        this.secParentSha1 = sha1;
    }

}
