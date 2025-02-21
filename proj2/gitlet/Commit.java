package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents a gitlet commit object.
 * does at a high level.
 *
 * @author peter
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    private final TreeMap<String, String> trackedBlobs = new TreeMap<>();
    /**
     * The message of this Commit.
     */
    private String message;
    private String father;
    private String mather;
    private Date timestamp;
    private String branch;

    public Commit() {
        father = null;
        mather = null;
    }

    public static Commit getCommitFromFile(String parentHas1) {
        File parentFile = Repository.getPath(Repository.COMMITS_DIR, parentHas1);
        if (parentFile.exists()) {
            return Utils.readObject(parentFile, Commit.class);
        }
        throw new IllegalArgumentException("No commit with that id exists.");
    }

    public void updateCommit(String messageToUpdate, String fatherToUpdate, String matherToUpdate, Date timestampToUpdate, String branchToUpdate) {
        this.message = messageToUpdate;
        this.father = fatherToUpdate;
        this.mather = matherToUpdate;
        this.timestamp = timestampToUpdate;
        this.branch = branchToUpdate;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getFather() {
        return father;
    }

    public String getMather() {
        return mather;
    }

    public String getBranch() {
        return branch;
    }

    public void saveCommit() {
        String has1 = Utils.sha1(Utils.serialize(this));
        File commitFile = Repository.getPath(Repository.COMMITS_DIR, has1);
        Utils.writeObject(commitFile, this);
        if (Repository.FIND.exists()) {
            TreeMap<String, String> find = Utils.readObject(Repository.FIND, TreeMap.class);
            if (find.containsKey(message)) {
                String newValue = find.get(message);
                newValue += "\n" + has1;
                find.put(message, newValue);
            } else {
                find.put(message, has1);
            }
            Utils.writeObject(Repository.FIND, find);
        } else {
            TreeMap<String, String> find = new TreeMap<>();
            find.put(message, has1);
            Utils.writeObject(Repository.FIND, find);
        }
        if (Repository.GETFULLID.exists()) {
            Set<String> fullid = Utils.readObject(Repository.GETFULLID, TreeSet.class);
            fullid.add(has1);
            Utils.writeObject(Repository.GETFULLID, (Serializable) fullid);
        } else {
            Set<String> fullid = new TreeSet<>();
            fullid.add(has1);
            Utils.writeObject(Repository.GETFULLID, (Serializable) fullid);
        }
        Repository.addNewCommitToGlobalLog(this);
    }

    public boolean isTracked(String fileName, String sha1) {
        return trackedBlobs.containsKey(fileName) && this.getBlobHash(fileName).equals(sha1);
    }

    public boolean isTracked(String fileName) {
        return trackedBlobs.containsKey(fileName);
    }

    public String getBlobHash(String fileName) {
        return trackedBlobs.get(fileName);
    }

    public void put(String fileName, String sha1) {
        trackedBlobs.put(fileName, sha1);
    }

    public TreeMap<String, String> getTrackedBlobs() {
        return trackedBlobs;
    }

    public void remove(String fileName) {
        trackedBlobs.remove(fileName);
    }
}
