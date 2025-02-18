package gitlet;

import java.io.File;
import java.util.*;
import java.io.Serializable;

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
    ;
    /**
     * The message of this Commit.
     */
    private String message;
    private String father;
    private String mather;
    private Date timestamp;
    private TreeMap<String, String> trackedBlobs = new TreeMap<>();

    public Commit() {
        father = null;
        mather = null;
    }

    public void updateCommit(String message, String father, String mather, Date timestamp) {
        this.message = message;
        this.father = father;
        this.mather = mather;
        this.timestamp = timestamp;
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

    public Commit getCommitFromFile(String parentHas1) {
        File parentFile = Utils.join(Repository.COMMITS_DIR, parentHas1.substring(0, 2), parentHas1.substring(2));
        if (parentFile.exists()) {
            return Utils.readObject(parentFile, Commit.class);
        }
        return null;
    }

    public void saveCommit() {
        String has1 = Utils.sha1((Object) Utils.serialize(this));
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
}
