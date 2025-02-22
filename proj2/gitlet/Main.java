package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Peter
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        Repository repo = new Repository();
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                if (isGitletDirectory()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    return;
                }
                validateNumArgs(args, 1);
                repo.init();
                break;
            case "add":
                validateInitalizedDirectory();
                validateNumArgs(args, 2);
                repo.add(args[1]);
                break;
            case "commit":
                validateInitalizedDirectory();
                if (args.length == 1 || args[1].equals("")) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                validateNumArgs(args, 2);
                repo.commit(args[1]);
                break;
            case "rm":
                validateInitalizedDirectory();
                validateNumArgs(args, 2);
                repo.rm(args[1]);
                break;
            case "log":
                validateInitalizedDirectory();
                validateNumArgs(args, 1);
                repo.log();
                break;
            case "global-log":
                validateInitalizedDirectory();
                validateNumArgs(args, 1);
                repo.globalLog();
                break;
            case "find":
                validateInitalizedDirectory();
                validateNumArgs(args, 2);
                repo.find(args[1]);
                break;
            case "status":
                validateInitalizedDirectory();
                validateNumArgs(args, 1);
                repo.status();
                break;
            case "checkout":
                validateInitalizedDirectory();
                if (args[1].equals("--")) {
                    validateNumArgs(args, 3);
                    repo.checkoutWithFilename(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    validateNumArgs(args, 4);
                    repo.checkoutWithIdAndFilename(args[1], args[3]);
                } else {
                    validateNumArgs(args, 2);
                    repo.checkoutWithBranchName(args[1]);
                }
                break;
            case "branch":
                validateInitalizedDirectory();
                validateNumArgs(args, 2);
                repo.branch(args[1]);
                break;
            case "rm-branch":
                validateInitalizedDirectory();
                validateNumArgs(args, 2);
                repo.rmBranch(args[1]);
                break;
            case "reset":
                validateInitalizedDirectory();
                validateNumArgs(args, 2);
                repo.reset(args[1]);
                break;
            case "merge":
                validateInitalizedDirectory();
                validateNumArgs(args, 2);
                repo.merge(args[1]);
                break;
            case "add-remote":
                validateInitalizedDirectory();
                validateNumArgs(args, 3);
                repo.addRemote(args[1], args[2]);
                break;
            case "rm-remote":
                validateInitalizedDirectory();
                validateNumArgs(args, 2);
                repo.rmRemote(args[1]);
                break;
            case "push":
                validateInitalizedDirectory();
                validateNumArgs(args, 3);
                repo.push(args[1], args[2]);
                break;
            case "fetch":
                validateInitalizedDirectory();
                validateNumArgs(args, 3);
                repo.fetch(args[1], args[2]);
                break;
            case "pull":
                validateInitalizedDirectory();
                validateNumArgs(args, 3);
                repo.pull(args[1], args[2]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    private static void validateNumArgs(String[] args, int num) {
        if (args.length != num) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void validateInitalizedDirectory() {
        if (!isGitletDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static boolean isGitletDirectory() {
        return Repository.isInitialized();
    }
}
