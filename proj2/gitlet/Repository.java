package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author Peter
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGE_DIR = join(GITLET_DIR, "stage");
    public static final File ADDITION = join(STAGE_DIR, "addition");
    public static final File REMOVAL = join(STAGE_DIR, "removal");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File MASTER_FILE = join(GITLET_DIR, "master");
    public static final File LOG = join(GITLET_DIR, "log");
    public static final File BRANCH = join(GITLET_DIR, "branch");
    public static final File WHEREHEADIS = join(GITLET_DIR, "whereheadis");
    public static final File GETFULLID = join(GITLET_DIR,"getfullid");
    public static final File FIND = join(GITLET_DIR,"find");

    //    public static final File FIND = join(GITLET_DIR,"find");
    private String HEAD, master;
    private String whereHeadIs;

    public Repository() {
        if (isInitialized()) {
            HEAD = Utils.readContentsAsString(HEAD_FILE);
            master = Utils.readContentsAsString(MASTER_FILE);
            whereHeadIs = Utils.readContentsAsString(WHEREHEADIS);
        } else {
            HEAD = null;
            master = null;
            whereHeadIs = null;
        }
    }

    public static boolean isInitialized() {
        return GITLET_DIR.exists() && GITLET_DIR.isDirectory();
    }

    public static File getPath(File frontPath, String sha1) {
        File path = Utils.join(frontPath, sha1.substring(0, 2));
        if (!path.exists()) {
            path.mkdir();
        }
        return Utils.join(path, sha1.substring(2));
    }

    public void init() {
        GITLET_DIR.mkdir();
        STAGE_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        Commit initial = new Commit();
        addNewCommitToGlobalLog(initial);
        initial.updateCommit("initial commit", null, null, new Date(0));
        initial.saveCommit();
        HEAD = sha1((Object) serialize(initial));
        master = HEAD;
        saveHead();
        saveMaster();
        //TODO:branch things
        Set<String> branchName = new TreeSet<>();
        branchName.add("master");
        Utils.writeObject(BRANCH, (Serializable) branchName);
        whereHeadIs = "master";
        Utils.writeContents(WHEREHEADIS, whereHeadIs);
    }

    private void saveHead() {
        Utils.writeContents(HEAD_FILE, HEAD);
    }

    private void saveMaster() {
        Utils.writeContents(MASTER_FILE, master);
    }

    public void add(String fileName) {
        TreeMap<String, String> store;
        if (ADDITION.exists()) {
            store = Utils.readObject(ADDITION, TreeMap.class);
        } else {
            store = new TreeMap<>();
        }
        File file = Utils.join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String sha1 = sha1((Object) readContents(file));
        Commit parent = Utils.readObject(getPath(COMMITS_DIR, HEAD), Commit.class);
        if (parent.isTracked(fileName) && parent.getBlobHash(fileName).equals(sha1)) {
            return;
        }
        File blobFile = getPath(BLOBS_DIR, sha1);
        if (!blobFile.exists()) {
            blobFile.getParentFile().mkdir();
            Utils.writeContents(blobFile, (Object) readContents(file));
        }
        store.put(fileName, sha1);
        Utils.writeObject(ADDITION, store);
    }

    public void commit(String message) {
        // TODO: implement removal stage
        File parent = getPath(COMMITS_DIR, HEAD);
        Commit newCommit = Utils.readObject(parent, Commit.class);
        newCommit.updateCommit(message, HEAD, null, new Date());
        if (ADDITION.exists()) {
            TreeMap<String, String> store = Utils.readObject(ADDITION, TreeMap.class);
            for (String fileName : store.keySet()) {
                newCommit.put(fileName, store.get(fileName));
            }
            ADDITION.delete();
        } else {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        newCommit.saveCommit();
        addNewCommitToGlobalLog(newCommit);
        HEAD = sha1((Object) serialize(newCommit));
        saveHead();
        master = HEAD;
        saveMaster();
    }

    public void rm(String fileName) {
        boolean sign = false;
        File currentFile = Utils.join(CWD, fileName);
        String sha1 = sha1((Object) readContents(currentFile));
        if (ADDITION.exists()) {
            TreeMap<String, String> store = Utils.readObject(ADDITION, TreeMap.class);
            for (String key : store.keySet()) {
                if (key.equals(fileName) && store.get(key).equals(sha1)) {
                    store.remove(key);
                    sign = true;
                }
            }
            if (sign) {
                Utils.writeContents(ADDITION, store);
            }
        }
        File pathOfHeadCommit = getPath(COMMITS_DIR, HEAD);
        Commit headCommit = Utils.readObject(pathOfHeadCommit, Commit.class);
        if (headCommit.isTracked(fileName) && headCommit.getBlobHash(fileName).equals(sha1)) {
            sign = true;
            Set<String> removal;
            if (REMOVAL.exists()) {
                removal = Utils.readObject(REMOVAL, HashSet.class);
            } else {
                removal = new HashSet<>();
            }
            removal.add(fileName);
            Utils.writeContents(REMOVAL, removal);
            if (currentFile.exists()) {
                currentFile.delete();
            }
        }
        if (!sign) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    public void log() {
        String pointer = HEAD;
        while (pointer != null) {
            File pathOfCurrentCommit = getPath(COMMITS_DIR, pointer);
            Commit currentCommit = Utils.readObject(pathOfCurrentCommit, Commit.class);
            System.out.print(printSignalLog(currentCommit, pointer));
            pointer = currentCommit.getFather();
        }
    }

    private StringBuffer printSignalLog(Commit commit, String sha1) {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append("===");
        logMessage.append("\n");
        logMessage.append("commit ").append(sha1);
        logMessage.append("\n");
        if (commit.getMather() != null) {
            logMessage.append("Merge: " + commit.getFather().substring(0, 8) + " " + commit.getMather().substring(0, 8));
            logMessage.append("\n");
        }
        String formatted = String.format(
                Locale.US, // 指定英文格式
                "%1$ta %1$tb %1$te %1$tT %1$tY %1$tz", // 格式化模板
                commit.getTimestamp() // 要格式化的时间
        );
        logMessage.append("Date: " + formatted);
        logMessage.append("\n");
        logMessage.append(commit.getMessage());
        logMessage.append("\n");
        logMessage.append("\n");
        return logMessage;
    }

    public void globalLog() {
        if (!LOG.exists()) {
            System.exit(0);
        }
        StringBuffer logmessage = Utils.readObject(LOG, StringBuffer.class);
        System.out.print(logmessage);
    }

    private void addNewCommitToGlobalLog(Commit commit) {
        StringBuffer logmessage;
        if (LOG.exists()) {
            logmessage = Utils.readObject(LOG, StringBuffer.class);
        } else {
            logmessage = new StringBuffer();
        }
        logmessage.append(printSignalLog(commit, sha1((Object) serialize(commit))));
        Utils.writeObject(LOG, logmessage);
    }

    public void find(String message) {
        TreeMap<String,String> find = Utils.readObject(FIND,TreeMap.class);
        for(String key:find.keySet()){
            if(key.equals(message)){
                System.out.println(find.get(key));
                return ;
            }
        }
        System.out.println("Found no commit with that message.");
        System.exit(0);
    }

    public void status() {
        System.out.println("=== Branches ===");
        Set<String> branchName = Utils.readObject(BRANCH, TreeSet.class);
        for (String name : branchName) {
            if (Objects.equals(name, whereHeadIs)) {
                System.out.println("*" + name);
            } else {
                System.out.println(name);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        if (ADDITION.exists()) {
            TreeMap<String, String> store = Utils.readObject(ADDITION, TreeMap.class);
            for (String fileName : store.keySet()) {
                System.out.println(fileName);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files");
        if (REMOVAL.exists()) {
            Set<String> removal = Utils.readObject(REMOVAL, HashSet.class);
            for (String fileName : removal) {
                System.out.println(fileName);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        //TODO:EC
        System.out.println();
        System.out.println("=== Untrached Files ===");
        //TODO:EC
        System.out.println();
    }

    public String getFullId(String id) {
        int len = id.length();
        if (id.length() == 40) {
            return id;
        }
        Set<String> fullid = Utils.readObject(GETFULLID,TreeSet.class);
        for(String key : fullid){
            if(key.substring(0,len).equals(id)){
                return key;
            }
        }
        return null;
    }

    public void checkoutWithFilename(String filename) {
        checkoutWithIdAndFilename(HEAD, filename);
    }

    public void checkoutWithIdAndFilename(String id, String filename) {
        id = getFullId(id);
        if(id == null){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File pathOfWantedCommit = getPath(COMMITS_DIR, id);
        if (!pathOfWantedCommit.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File pathOfHeadCommit = getPath(COMMITS_DIR, id);
        Commit currentCommit = Utils.readObject(pathOfHeadCommit, Commit.class);
        String sha1 = currentCommit.getBlobHash(filename);
        if (sha1 == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File blobFile = getPath(BLOBS_DIR, sha1);
        Utils.writeContents(Utils.join(CWD, filename), (Object) Utils.readContents(blobFile));
    }

    public void checkoutWithBranchName(String branckName) {

    }
}
