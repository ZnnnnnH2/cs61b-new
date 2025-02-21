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
    public static final File GLOBALLOG = join(GITLET_DIR, "log");
    public static final File BRANCH = join(GITLET_DIR, "branch");
    public static final File WHEREHEADIS = join(GITLET_DIR, "whereheadis");
    public static final File GETFULLID = join(GITLET_DIR, "getfullid");
    public static final File FIND = join(GITLET_DIR, "find");
    private static final String MAIN = "master";
    private final TreeMap<String, String> branch;
    //    public static final File FIND = join(GITLET_DIR,"find");
    private String HEAD;
    private String whereHeadIs;

    public Repository() {
        if (isInitialized()) {
            HEAD = Utils.readContentsAsString(HEAD_FILE);
            branch = Utils.readObject(BRANCH, TreeMap.class);
            whereHeadIs = Utils.readContentsAsString(WHEREHEADIS);
        } else {
            HEAD = null;
            branch = new TreeMap<>();
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

    private static StringBuffer printSignalLog(Commit commit, String sha1) {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append("===");
        logMessage.append("\n");
        logMessage.append("commit ").append(sha1);
        logMessage.append("\n");
        if (commit.getMather() != null) {
            logMessage.append("Merge: " + commit.getFather().substring(0, 8)
                    + " " + commit.getMather().substring(0, 8));
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

    public static void addNewCommitToGlobalLog(Commit commit) {
        StringBuffer logMssage;
        if (GLOBALLOG.exists()) {
            logMssage = Utils.readObject(GLOBALLOG, StringBuffer.class);
        } else {
            logMssage = new StringBuffer();
        }
        logMssage.append(printSignalLog(commit, sha1(serialize(commit))));
        Utils.writeObject(GLOBALLOG, logMssage);
    }

    private void forwordHEAD(Commit newcommit) {
        String sha1 = sha1(serialize(newcommit));
        HEAD = sha1;
        Utils.writeContents(HEAD_FILE, HEAD);
        branch.put(whereHeadIs, HEAD);
        Utils.writeObject(BRANCH, branch);
    }

    private void createDir() {
        GITLET_DIR.mkdir();
        STAGE_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
    }

    public void init() {
        if (isInitialized()) {
            System.out.println("A Gitlet version-control system " +
                    "already exists in the current directory.");
            System.exit(0);
        }
        createDir();
        Commit initial = new Commit();
        initial.updateCommit("initial commit", null, null, new Date(0), whereHeadIs);
        initial.saveCommit();
        whereHeadIs = MAIN;
        Utils.writeContents(WHEREHEADIS, whereHeadIs);
        forwordHEAD(initial);
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
        String sha1 = sha1(readContents(file));
        Commit parent = Utils.readObject(getPath(COMMITS_DIR, HEAD), Commit.class);
        if (parent.isTracked(fileName, sha1) && parent.getBlobHash(fileName).equals(sha1)) {
            if (store.containsKey(fileName)) {
                store.remove(fileName);
                Utils.writeObject(ADDITION, store);
            }
            return;
        }
        if (REMOVAL.exists()) {
            Set<String> removal = Utils.readObject(REMOVAL, TreeSet.class);
            if (removal.contains(fileName)) {
                removal.remove(fileName);
                Utils.writeObject(REMOVAL, (Serializable) removal);
                System.exit(0);
            }
        }
        File blobFile = getPath(BLOBS_DIR, sha1);
        if (!blobFile.exists()) {
            blobFile.getParentFile().mkdir();
            Utils.writeContents(blobFile, readContents(file));
        }
        store.put(fileName, sha1);
        Utils.writeObject(ADDITION, store);
    }

    public void commit(String message) {
        File parent = getPath(COMMITS_DIR, HEAD);
        Commit newCommit = Utils.readObject(parent, Commit.class);
        newCommit.updateCommit(message, HEAD, null, new Date(), whereHeadIs);
        if (ADDITION.exists()) {
            TreeMap<String, String> store = Utils.readObject(ADDITION, TreeMap.class);
            for (String fileName : store.keySet()) {
                newCommit.put(fileName, store.get(fileName));
            }
            ADDITION.delete();
        } else if (REMOVAL.exists()) {
            Set<String> removal = Utils.readObject(REMOVAL, TreeSet.class);
            for (String fileName : removal) {
                newCommit.remove(fileName);
            }
            REMOVAL.delete();
        } else {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        newCommit.saveCommit();
        forwordHEAD(newCommit);
    }

    public void rm(String fileName) {
        boolean sign = false;
        File currentFile = Utils.join(CWD, fileName);
        if (ADDITION.exists()) {
            TreeMap<String, String> store = Utils.readObject(ADDITION, TreeMap.class);
            Iterator<String> iterator = store.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (key.equals(fileName)) {
                    iterator.remove();
                    sign = true;
                }
            }
            if (sign) {
                Utils.writeObject(ADDITION, store);
            }
        }
        Commit headCommit = Commit.getCommitFromFile(HEAD);
        if (headCommit.isTracked(fileName)) {
            sign = true;
            Set<String> removal;
            if (REMOVAL.exists()) {
                removal = Utils.readObject(REMOVAL, TreeSet.class);
            } else {
                removal = new TreeSet<>();
            }
            removal.add(fileName);
            Utils.writeObject(REMOVAL, (Serializable) removal);
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

    public void globalLog() {
        if (!GLOBALLOG.exists()) {
            System.exit(0);
        }
        StringBuffer logMessage = Utils.readObject(GLOBALLOG, StringBuffer.class);
        System.out.print(logMessage);
    }

    public void find(String message) {
        TreeMap<String, String> find = Utils.readObject(FIND, TreeMap.class);
        for (String key : find.keySet()) {
            if (key.equals(message)) {
                System.out.println(find.get(key));
                return;
            }
        }
        System.out.println("Found no commit with that message.");
        System.exit(0);
    }

    public void status() {
        System.out.println("=== Branches ===");
        TreeMap<String, String> branchName = Utils.readObject(BRANCH, TreeMap.class);
        for (String name : branchName.keySet()) {
            if (name.equals(whereHeadIs)) {
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
        System.out.println("=== Removed Files ===");
        if (REMOVAL.exists()) {
            Set<String> removal = Utils.readObject(REMOVAL, TreeSet.class);
            for (String fileName : removal) {
                System.out.println(fileName);
            }
        }
        System.out.println();
        File[] fileInCWD = CWD.listFiles();
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (File file : fileInCWD) {
            if (file.isDirectory()) {
                continue;
            }
            if (truelyCheckTracked(file.getName())) {
                String sha1 = sha1(Utils.readContents(file));
                if (isModified(file.getName(), sha1)) {
                    System.out.println(file.getName() + " (modified)");
                }
            }
        }
        deletedFile();
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (File file : fileInCWD) {
            if (file.isDirectory()) {
                continue;
            }
            if (!truelyCheckTracked(file.getName())) {
                System.out.println(file.getName());
            }
        }
        System.out.println();
    }

    public String getFullId(String id) {
        int len = id.length();
        Set<String> fullid = Utils.readObject(GETFULLID, TreeSet.class);
        for (String key : fullid) {
            if (key.substring(0, len).equals(id)) {
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
        File pathOfWantedCommit = getPath(COMMITS_DIR, id);
        if (!pathOfWantedCommit.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit currentCommit = Commit.getCommitFromFile(id);
        String sha1 = currentCommit.getBlobHash(filename);
        if (sha1 == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File blobFile = getPath(BLOBS_DIR, sha1);
        Utils.writeContents(Utils.join(CWD, filename), Utils.readContents(blobFile));
    }

    public void checkoutWithBranchName(String branchName) {
        if (!branch.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (branchName.equals(whereHeadIs)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        switchToACommit(branch.get(branchName), branchName);
    }

    public void branch(String branchName) {
        if (branch.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branch.put(branchName, HEAD);
        Utils.writeObject(BRANCH, branch);
    }

    public void rmBranch(String branchName) {
        if (!branch.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(whereHeadIs)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branch.remove(branchName);
        Utils.writeObject(BRANCH, branch);
    }

    public void reset(String commitId) {
        commitId = getFullId(commitId);
        if (commitId == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        switchToACommit(commitId, null);
    }

    private void checkAnUntrackedFileInTheWay(String commitId) {
        Commit hCCB = Commit.getCommitFromFile(HEAD);
        for (String key : hCCB.getTrackedBlobs().keySet()) {
            File nTICOB = Utils.join(CWD, key);
            if (nTICOB.exists()
                    && !hCCB.getBlobHash(key).equals(sha1(Utils.readContents(nTICOB)))) {
                System.out.println("There is an untracked file in the way;" +
                        " delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        Commit hCOJB = Commit.getCommitFromFile(commitId);
        for (String key : hCOJB.getTrackedBlobs().keySet()) {
            File blobFile = getPath(BLOBS_DIR, hCOJB.getBlobHash(key));
            File thisFile = Utils.join(CWD, key);
            if (thisFile.exists()
                    && !hCOJB.getBlobHash(key).equals(sha1(Utils.readContents(thisFile)))) {
                System.out.println("There is an untracked file in the way;" +
                        " delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private void switchToACommit(String commitId, String whereCurrentHeadIs) {
        commitId = getFullId(commitId);
        checkAnUntrackedFileInTheWay(commitId);
        if (ADDITION.exists()) {
            ADDITION.delete();
        }
        if (REMOVAL.exists()) {
            REMOVAL.delete();
        }
        Commit headCommitOfCurrentBranch = Commit.getCommitFromFile(HEAD);
        for (String key : headCommitOfCurrentBranch.getTrackedBlobs().keySet()) {
            File notTrackedInCheckedOutBranch = Utils.join(CWD, key);
            notTrackedInCheckedOutBranch.delete();
        }
        Commit headCommitOfGivenBranch = Commit.getCommitFromFile(commitId);
        for (String key : headCommitOfGivenBranch.getTrackedBlobs().keySet()) {
            File blobFile = getPath(BLOBS_DIR, headCommitOfGivenBranch.getBlobHash(key));
            Utils.writeContents(Utils.join(CWD, key), Utils.readContents(blobFile));
        }
        HEAD = commitId;
        Utils.writeContents(HEAD_FILE, HEAD);
        if (whereCurrentHeadIs == null) {
            whereHeadIs = headCommitOfGivenBranch.getBranch();
        } else {
            whereHeadIs = whereCurrentHeadIs;
        }
        Utils.writeContents(WHEREHEADIS, whereHeadIs);
        branch.put(whereHeadIs, HEAD);
        Utils.writeObject(BRANCH, branch);
    }

    public void merge(String branchName) {

    }

    private boolean truelyCheckTracked(String fileName) {
        if (ADDITION.exists()) {
            TreeMap<String, String> store = Utils.readObject(ADDITION, TreeMap.class);
            for (String key : store.keySet()) {
                if (store.containsKey(fileName)) {
                    return true;
                }
            }
        }
        Commit headCommit = Commit.getCommitFromFile(HEAD);
        return headCommit.isTracked(fileName);
    }

    private boolean isModified(String fileName, String sha1) {
        if (ADDITION.exists()) {
            TreeMap<String, String> store = Utils.readObject(ADDITION, TreeMap.class);
            if (store.containsKey(fileName)) {
                return !store.get(fileName).equals(sha1);
            }
        }
        Commit headCommit = Commit.getCommitFromFile(HEAD);
        return !headCommit.isTracked(fileName, sha1);
    }

    private void deletedFile() {
        Set<String> f = new HashSet<>();
        if (ADDITION.exists()) {
            TreeMap<String, String> store = Utils.readObject(ADDITION, TreeMap.class);
            for (String key : store.keySet()) {
                if(isRemoved(key)){
                    continue;
                }
                File addedFile = Utils.join(CWD, key);
                if (!addedFile.exists()) {
                    System.out.println(key + " (deleted)");
                    f.add(key);
                }
            }
        }
        Commit headCommit = Commit.getCommitFromFile(HEAD);
        for (String key : headCommit.getTrackedBlobs().keySet()) {
            if (f.contains(key) || isRemoved(key)) {
                continue;
            }
            File trackedFile = Utils.join(CWD, key);
            if (!trackedFile.exists()) {
                System.out.println(key + " (deleted)");
            }
        }
    }

    private boolean isRemoved(String fileName){
        if (REMOVAL.exists()) {
            Set<String> removal = Utils.readObject(REMOVAL, TreeSet.class);
            return removal.contains(fileName);
        }
        return false;
    }
}
