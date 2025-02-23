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
    public static final File REMOTE = join(GITLET_DIR, "remote");
    private static final String MAIN = "master";
    private final TreeMap<String, String> branch;
    private String HEAD;
    private String whereHeadIs;

    public Repository() {
        if (isInitialized()) {
            HEAD = readContentsAsString(HEAD_FILE);
            branch = readObject(BRANCH, TreeMap.class);
            whereHeadIs = readContentsAsString(WHEREHEADIS);
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
        File path = join(frontPath, sha1.substring(0, 2));
        if (!path.exists()) {
            path.mkdir();
        }
        return join(path, sha1.substring(2));
    }

    private static StringBuffer printSignalLog(Commit commit, String sha1) {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append("===");
        logMessage.append("\n");
        logMessage.append("commit ").append(sha1);
        logMessage.append("\n");
        if (commit.getMather() != null) {
            logMessage.append("Merge: " + commit.getFather().substring(0, 7)
                    + " " + commit.getMather().substring(0, 7));
            logMessage.append("\n");
        }
        String formatted = String.format(Locale.US, // 指定英文格式
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
        StringBuffer logMessage;
        if (GLOBALLOG.exists()) {
            logMessage = readObject(GLOBALLOG, StringBuffer.class);
        } else {
            logMessage = new StringBuffer();
        }
        logMessage.append(printSignalLog(commit, sha1(serialize(commit))));
        writeObject(GLOBALLOG, logMessage);
    }

    private void forwordHEAD(Commit newcommit) {
        String sha1 = sha1(serialize(newcommit));
        HEAD = sha1;
        writeContents(HEAD_FILE, HEAD);
        branch.put(whereHeadIs, HEAD);
        writeObject(BRANCH, branch);
    }

    private void createDir() {
        GITLET_DIR.mkdir();
        STAGE_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
    }

    public void init() {
        if (isInitialized()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
        createDir();
        Commit initial = new Commit();
        initial.updateCommit("initial commit", null, null, new Date(0), whereHeadIs);
        initial.saveCommit();
        whereHeadIs = MAIN;
        writeContents(WHEREHEADIS, whereHeadIs);
        forwordHEAD(initial);
    }

    public void add(String fileName) {
        TreeMap<String, String> store;
        if (ADDITION.exists()) {
            store = readObject(ADDITION, TreeMap.class);
        } else {
            store = new TreeMap<>();
        }
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String sha1 = sha1(readContents(file));
        Commit parent = Commit.getCommitFromFile(HEAD);
        if (REMOVAL.exists()) {
            Set<String> removal = readObject(REMOVAL, TreeSet.class);
            if (removal.contains(fileName)) {
                removal.remove(fileName);
                writeObject(REMOVAL, (Serializable) removal);
                System.exit(0);
            }
        }
        if (parent.isTracked(fileName, sha1) && parent.getBlobHash(fileName).equals(sha1)) {
            if (store.containsKey(fileName)) {
                store.remove(fileName);
                writeObject(ADDITION, store);
            }
            return;
        }
        File blobFile = getPath(BLOBS_DIR, sha1);
        if (!blobFile.exists()) {
            blobFile.getParentFile().mkdir();
            writeContents(blobFile, readContents(file));
        }
        store.put(fileName, sha1);
        writeObject(ADDITION, store);
    }

    public void commit(String... messages) {
        String mother = null;
        if (messages.length == 2) {
            mother = messages[1];
        }
        String message = messages[0];
        File parent = getPath(COMMITS_DIR, HEAD);
        Commit newCommit = readObject(parent, Commit.class);
        newCommit.updateCommit(message, HEAD, mother, new Date(), whereHeadIs);
        boolean sign = false;
        if (ADDITION.exists()) {
            TreeMap<String, String> store = readObject(ADDITION, TreeMap.class);
            for (String fileName : store.keySet()) {
                newCommit.put(fileName, store.get(fileName));
            }
            ADDITION.delete();
            sign = true;
        }
        if (REMOVAL.exists()) {
            Set<String> removal = readObject(REMOVAL, TreeSet.class);
            for (String fileName : removal) {
                newCommit.remove(fileName);
            }
            REMOVAL.delete();
            sign = true;
        }
        if (!sign) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        newCommit.saveCommit();
        forwordHEAD(newCommit);
    }

    public void rm(String fileName) {
        boolean sign = false;
        File currentFile = join(CWD, fileName);
        if (ADDITION.exists()) {
            TreeMap<String, String> store = readObject(ADDITION, TreeMap.class);
            Iterator<String> iterator = store.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (key.equals(fileName)) {
                    iterator.remove();
                    sign = true;
                }
            }
            if (sign) {
                writeObject(ADDITION, store);
            }
        }
        Commit headCommit = Commit.getCommitFromFile(HEAD);
        if (headCommit.isTracked(fileName)) {
            sign = true;
            Set<String> removal;
            if (REMOVAL.exists()) {
                removal = readObject(REMOVAL, TreeSet.class);
            } else {
                removal = new TreeSet<>();
            }
            removal.add(fileName);
            writeObject(REMOVAL, (Serializable) removal);
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
            Commit currentCommit = readObject(pathOfCurrentCommit, Commit.class);
            System.out.print(printSignalLog(currentCommit, pointer));
            pointer = currentCommit.getFather();
        }
    }

    public void globalLog() {
        if (!GLOBALLOG.exists()) {
            System.exit(0);
        }
        StringBuffer logMessage = readObject(GLOBALLOG, StringBuffer.class);
        System.out.print(logMessage);
    }

    public void find(String message) {
        TreeMap<String, String> find = readObject(FIND, TreeMap.class);
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
        TreeMap<String, String> branchName = readObject(BRANCH, TreeMap.class);
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
            TreeMap<String, String> store = readObject(ADDITION, TreeMap.class);
            for (String fileName : store.keySet()) {
                System.out.println(fileName);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        if (REMOVAL.exists()) {
            Set<String> removal = readObject(REMOVAL, TreeSet.class);
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
                String sha1 = sha1(readContents(file));
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
        Set<String> fullid = readObject(GETFULLID, TreeSet.class);
        for (String key : fullid) {
            if (key.substring(0, len).equals(id)) {
                return key;
            }
        }
        System.out.println("No commit with that id exists.");
        System.exit(0);
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
        writeContents(join(CWD, filename), readContents(blobFile));
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
        writeObject(BRANCH, branch);
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
        writeObject(BRANCH, branch);
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
        File[] files = CWD.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            Commit nextCommit = Commit.getCommitFromFile(commitId);
            Commit headCommit = Commit.getCommitFromFile(HEAD);
            if (!truelyCheckTracked(file.getName())) {
                if (nextCommit.isTracked(file.getName())) {
                    if (!nextCommit.isTracked(file.getName(), sha1(readContents(file)))) {
                        System.out.println("There is an untracked file in the way;"
                                + " delete it, or add and commit it first.");
                        System.exit(0);
                    } else {
                        continue;
                    }
                }
                if (headCommit.isTracked(file.getName())) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                }
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
            File notTrackedInCheckedOutBranch = join(CWD, key);
            notTrackedInCheckedOutBranch.delete();
        }
        Commit headCommitOfGivenBranch = Commit.getCommitFromFile(commitId);
        for (String key : headCommitOfGivenBranch.getTrackedBlobs().keySet()) {
            File blobFile = getPath(BLOBS_DIR, headCommitOfGivenBranch.getBlobHash(key));
            writeContents(join(CWD, key), readContents(blobFile));
        }
        HEAD = commitId;
        writeContents(HEAD_FILE, HEAD);
        if (whereCurrentHeadIs == null) {
            whereHeadIs = headCommitOfGivenBranch.getBranch();
        } else {
            whereHeadIs = whereCurrentHeadIs;
        }
        writeContents(WHEREHEADIS, whereHeadIs);
        branch.put(whereHeadIs, HEAD);
        writeObject(BRANCH, branch);
    }

    private void beforeMergeCheck(String branchName) {
        if (ADDITION.exists()) {
            TreeMap<String, String> store = readObject(ADDITION, TreeMap.class);
            if (!store.isEmpty()) {
                System.out.println("You have uncommitted changes.");
                System.exit(0);
            }
        }
        if (REMOVAL.exists()) {
            Set<String> removal = readObject(REMOVAL, TreeSet.class);
            if (!removal.isEmpty()) {
                System.out.println("You have uncommitted changes.");
                System.exit(0);
            }
        }
        if (!branch.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(whereHeadIs)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    private Set<String> getParentCommit(String sha1) {
        Set<String> parentCommit = new HashSet<>();
        if (sha1 == null) {
            return parentCommit;
        }
        parentCommit.add(sha1);
        parentCommit.addAll(getParentCommit(Commit.getCommitFromFile(sha1).getFather()));
        parentCommit.addAll(getParentCommit(Commit.getCommitFromFile(sha1).getMather()));
        return parentCommit;
    }

    private Commit getSplitCommit(Commit commit1, Commit commit2) {
        Set<String> parentCommit = getParentCommit(sha1((Object) serialize(commit1)));
        ArrayList<String> pC = new ArrayList<>();
        pC.add(sha1((Object) serialize(commit2)));
        while (!pC.isEmpty()) {
            String sha1 = pC.remove(0);
            if (!parentCommit.contains(sha1)) {
                Commit commit = Commit.getCommitFromFile(sha1);
                if (commit.getFather() != null) {
                    pC.add(commit.getFather());
                }
                if (commit.getMather() != null) {
                    pC.add(commit.getMather());
                }
            } else {
                return Commit.getCommitFromFile(sha1);
            }
        }
        return commit1;
    }

    public void merge(String branchName) {
        boolean sign = false;
        boolean signDoChange = false;
        beforeMergeCheck(branchName);
        checkAnUntrackedFileInTheWay(branch.get(branchName));
        Commit headCommit = Commit.getCommitFromFile(HEAD);
        String shaGivenCommit = branch.get(branchName);
        Commit givenCommit = Commit.getCommitFromFile(shaGivenCommit);
        Commit splitCommit = getSplitCommit(headCommit, givenCommit);
        String splitPoint = sha1((Object) serialize(splitCommit));
        if (splitPoint.equals(shaGivenCommit)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitPoint.equals(HEAD)) {
            checkoutWithBranchName(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        Set<String> newCreatedFile = new TreeSet<>();
        for (String key : splitCommit.getTrackedBlobs().keySet()) {
            newCreatedFile.add(key);
            String sha1InSplit = splitCommit.getBlobHash(key);
            if (headCommit.isTracked(key, sha1InSplit)) { //not changed in the current branch
                if (givenCommit.isTracked(key)) { //undeleted
                    if (!givenCommit.isTracked(key, sha1InSplit)) { //step1 : modified
                        signDoChange = true;
                        checkoutWithIdAndFilename(shaGivenCommit, key);
                        add(key);
                    }
                } else { //deleted step6 : Any files present at the split point.
                    signDoChange = true;
                    rm(key);
                }
            } else if (!headCommit.isTracked(key)) {
                if (givenCommit.isTracked(key) && !givenCommit.isTracked(key, sha1InSplit)) {
                    signDoChange = true;
                    changeFile(key, headCommit, givenCommit);
                    sign = true;
                }
            } else {
                if (!givenCommit.isTracked(key, sha1InSplit)
                        && !headCommit.getBlobHash(key).equals(givenCommit.getBlobHash(key))) {
                    signDoChange = true;
                    changeFile(key, headCommit, givenCommit);
                    sign = true;
                }
            }
        }
        for (String key : headCommit.getTrackedBlobs().keySet()) {
            if (!newCreatedFile.contains(key)
                    && givenCommit.isTracked(key)
                    && !headCommit.getBlobHash(key).equals(givenCommit.getBlobHash(key))) {
                signDoChange = true;
                sign = true;
                newCreatedFile.add(key);
                changeFile(key, headCommit, givenCommit);
            }
        }
        for (String key : givenCommit.getTrackedBlobs().keySet()) {
            if (!newCreatedFile.contains(key)) { //step5 : only in given branch
                signDoChange = true;
                checkoutWithIdAndFilename(shaGivenCommit, key);
                add(key);
            }
        }
        if (signDoChange) {
            commit("Merged " + branchName + " into " + whereHeadIs + ".", shaGivenCommit);
        }
        if (sign) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private void changeFile(String key, Commit headCommit, Commit givenCommit) {
        String content = "";
        content += "<<<<<<< HEAD\n";
        if (headCommit.getBlobHash(key) != null) {
            File file1 = getPath(BLOBS_DIR, headCommit.getBlobHash(key));
            if (file1.exists()) {
                content += readContentsAsString(file1);
            }
        }
        content += "=======\n";
        if (givenCommit.getBlobHash(key) != null) {
            File file2 = getPath(BLOBS_DIR, givenCommit.getBlobHash(key));
            if (file2.exists()) {
                content += readContentsAsString(file2);
            }
        }
        content += ">>>>>>>\n";
        writeContents(join(CWD, key), content);
        add(key);
    }

    private boolean truelyCheckTracked(String fileName) {
        if (ADDITION.exists()) {
            TreeMap<String, String> store = readObject(ADDITION, TreeMap.class);
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
            TreeMap<String, String> store = readObject(ADDITION, TreeMap.class);
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
            TreeMap<String, String> store = readObject(ADDITION, TreeMap.class);
            for (String key : store.keySet()) {
                if (isRemoved(key)) {
                    continue;
                }
                File addedFile = join(CWD, key);
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
            File trackedFile = join(CWD, key);
            if (!trackedFile.exists()) {
                System.out.println(key + " (deleted)");
            }
        }
    }

    private boolean isRemoved(String fileName) {
        if (REMOVAL.exists()) {
            Set<String> removal = readObject(REMOVAL, TreeSet.class);
            return removal.contains(fileName);
        }
        return false;
    }

    public void addRemote(String remoteName, String remotePath) {
        TreeMap<String, File> remote;
        if (REMOTE.exists()) {
            remote = readObject(REMOTE, TreeMap.class);
        } else {
            remote = new TreeMap<>();
        }
        if (remote.containsKey(remoteName)) {
            System.out.println("A remote with that name already exists.");
            System.exit(0);
        }
        String systemSpecificPath = remotePath.replace("/", File.separator);
        File remoteDir = new File(systemSpecificPath);
        remote.put(remoteName, remoteDir);
        writeObject(REMOTE, remote);
    }

    public void rmRemote(String remoteName) {
        if (!REMOTE.exists()) {
            System.out.println("A remote with that name does not exist.");
            System.exit(0);
        }
        TreeMap<String, File> remote = readObject(REMOTE, TreeMap.class);
        if (!remote.containsKey(remoteName)) {
            System.out.println("A remote with that name does not exist.");
            System.exit(0);
        }
        remote.remove(remoteName);
        writeObject(REMOTE, remote);
    }

    public void push(String remoteName, String remoteBranchName) {
        File remoteDir = notFoundRemoteDir(remoteName);
        File remoteHead = join(remoteDir, "HEAD");
        File remoteBranch = join(remoteDir, "branch");
        if (!remoteHead.exists() || !remoteBranch.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        TreeMap<String, String> remoteBranches = readObject(remoteBranch, TreeMap.class);
        String remoteHeadCommit = readContentsAsString(remoteHead);
        if (!remoteBranches.containsKey(remoteBranchName)) {
            remoteBranches.put(remoteBranchName, remoteHeadCommit);
            writeObject(remoteBranch, remoteBranches);
            System.exit(0);
        }
        String remoteBranchCommit = remoteBranches.get(remoteBranchName);
        if (!isInhistory(remoteBranchCommit, HEAD)) {
            System.out.println("Please pull down remote changes before pushing.");
            System.exit(0);
        }
        File remoteCommitPath = join(remoteDir, "commits");
        File remoteBlobPath = join(remoteDir, "blobs");
        moveToRemote(HEAD, remoteCommitPath, remoteBlobPath, remoteDir);
        remoteBranches.put(remoteBranchName, HEAD);
        writeObject(remoteBranch, remoteBranches);
        writeContents(remoteHead, HEAD);
    }

    private boolean isInhistory(String remoteCommitSha1, String localCommitSha1) {
        if (localCommitSha1 == null) {
            return false;
        }
        if (localCommitSha1.equals(remoteCommitSha1)) {
            return true;
        }
        Commit localCommit = readObject(getPath(COMMITS_DIR, localCommitSha1), Commit.class);
        return isInhistory(remoteCommitSha1, localCommit.getFather())
                || isInhistory(remoteCommitSha1, localCommit.getMather());
    }

    private void moveToRemote(String localCommitsha1, File remoteCommitPath,
                              File remoteBlobPath, File remoteGitLatPath) {
        if (localCommitsha1 == null) {
            return;
        }
        File localCommitFile = getPath(COMMITS_DIR, localCommitsha1);
        Commit localCommit = readObject(localCommitFile, Commit.class);
        File commitFileInRemote = getPath(remoteCommitPath, sha1((Object) serialize(localCommit)));
        if (!commitFileInRemote.exists()) {
            File remotedGetFullId = join(remoteGitLatPath, "getfullid");
            moveTo(remoteBlobPath, localCommit, commitFileInRemote, remotedGetFullId, BLOBS_DIR);
        }
        moveToRemote(localCommit.getFather(), remoteCommitPath, remoteBlobPath, remoteGitLatPath);
        moveToRemote(localCommit.getMather(), remoteCommitPath, remoteBlobPath, remoteGitLatPath);
    }

    private void moveTo(File remoteBlobPath, Commit localCommit, File commitFileInRemote,
                        File remotedGetFullId, File blobsDir) {
        Set<String> fullid = readObject(remotedGetFullId, TreeSet.class);
        fullid.add(sha1((Object) serialize(localCommit)));
        writeObject(remotedGetFullId, (Serializable) fullid);
        writeObject(commitFileInRemote, localCommit);
        for (String key : localCommit.getTrackedBlobs().keySet()) {
            File localblobFile = getPath(blobsDir, localCommit.getBlobHash(key));
            File blobFileInRemote = getPath(remoteBlobPath, localCommit.getBlobHash(key));
            if (!blobFileInRemote.exists()) {
                writeContents(blobFileInRemote, readContents(localblobFile));
            }
        }
    }

    public void fetch(String remoteName, String remoteBranchName) {
        File remoteDir = notFoundRemoteDir(remoteName);
        File branches = join(remoteDir, "branch");
        if (!branches.exists()) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }
        TreeMap<String, String> remoteBranches = readObject(branches, TreeMap.class);
        if (!remoteBranches.containsKey(remoteBranchName)) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }
        String remoteBranchCommitSha1 = remoteBranches.get(remoteBranchName);
        File remoteCommitPath = join(remoteDir, "commits");
        File remoteBlobPath = join(remoteDir, "blobs");
        File remoteCurrentCommitPath = getPath(remoteCommitPath, remoteBranchCommitSha1);
        Commit remoteCommit = readObject(remoteCurrentCommitPath, Commit.class);
        String newBranchName = remoteName + "/" + remoteBranchName;
        branch.put(newBranchName, remoteBranchCommitSha1);
        writeObject(BRANCH, branch);
        moveToLocal(remoteBranchCommitSha1, remoteCommitPath, remoteBlobPath);
    }

    private File notFoundRemoteDir(String remoteName) {
        if (!REMOTE.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        TreeMap<String, File> remote = readObject(REMOTE, TreeMap.class);
        if (!remote.containsKey(remoteName)) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        File remoteDir = remote.get(remoteName);
        if (!remoteDir.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        return remoteDir;
    }

    private void moveToLocal(String remoteCommitsha1, File remoteCommitPath, File remoteBlobPath) {
        if (remoteCommitsha1 == null) {
            return;
        }
        File remoteCommitFile = getPath(remoteCommitPath, remoteCommitsha1);
        Commit remoteCommit = readObject(remoteCommitFile, Commit.class);
        File commitFileInLocal = getPath(COMMITS_DIR, sha1((Object) serialize(remoteCommit)));
        if (!commitFileInLocal.exists()) {
            moveTo(BLOBS_DIR, remoteCommit, commitFileInLocal, GETFULLID, remoteBlobPath);
        }
        moveToLocal(remoteCommit.getFather(), remoteCommitPath, remoteBlobPath);
        moveToLocal(remoteCommit.getMather(), remoteCommitPath, remoteBlobPath);
    }

    public void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        merge(remoteName + "/" + remoteBranchName);
    }
}
