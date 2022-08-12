package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author Yyy
 */
public class Commit implements Serializable {
    /**
     * add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */

    private Date timeStamp;
    private String message;
    private HashMap<String, String> bolbs;
    private String parentSha1;
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

    public void setSecParentSha1(String sha1) {
        this.secParentSha1 = sha1;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getBolbs() {
        return bolbs;
    }

    public String getParentSha1() {
        return parentSha1;
    }

    public String getSecParentSha1() {
        return secParentSha1;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBolbs(HashMap<String, String> bolbs) {
        this.bolbs = bolbs;
    }

    public void setParentSha1(String parentSha1) {
        this.parentSha1 = parentSha1;
    }
}
