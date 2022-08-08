package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Yyy
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }

        String firstArg = args[0];
        Repository repo;
        switch(firstArg) {
            case "init":
                if (Repository.GITLET_DIR.exists()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    return;
                }
                repo = new Repository();
                break;
            case "add":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length < 2) {
                    System.out.println("Incorrect operands.");
                    return;
                }

                String fileName = args[1];
                File file = new File(fileName);
                if (!file.exists()) {
                    System.out.println("File does not exist.");
                    return;
                }

                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                repo.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length < 2) {
                    System.out.println("Please enter a commit message.");
                    return;
                }

                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                repo.commit(args[1]);
                break;
            case "rm":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length < 2) {
                    System.out.println("Incorrect operands.");
                    return;
                }

                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                if (!repo.rm(args[1])) {
                    System.out.println("No reason to remove the file.");
                }
                break;
            case "log":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }
                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                repo.log();
                break;
            case "global-log":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }
                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                repo.globalLog();
                break;
            case "find":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length < 2) {
                    System.out.println("Incorrect operands.");
                    return;
                }

                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                if (!repo.find(args[1])) {
                    System.out.println("Found no commit with that message.");
                }
                break;
            case "status":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }

                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                repo.status();
                break;
            case "checkout":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }

                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                switch (args.length) {
//                    case 1:
//                        System.out.println("Incorrect operands.");
//                        break;
                    case 2:
                        repo.checkoutBranch(args[1]);
                        break;
                    // java gitlet.Main checkout -- [file name]
                    case 3:
                        if (!args[1].equals("--")) {
                            return;
                        }
                        repo.checkout(args[2]);
                        break;
                    // java gitlet.Main checkout [commit id] -- [file name]
                    case 4:
                        if (!args[2].equals("--")) {
                            return;
                        }
                        repo.checkout(args[1], args[3]);
                        break;
                }
                break;
            case "branch":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length < 2) {
                    System.out.println("Incorrect operands.");
                    return;
                }

                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                if (!repo.newBranch(args[1])) {
                    System.out.println("A branch with that name already exists.");
                }
                break;
            case "rm-branch":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length < 2) {
                    System.out.println("Incorrect operands.");
                    return;
                }

                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                repo.rmBranch(args[1]);
                break;
            case "reset":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    return;
                }
                if (args.length < 2) {
                    System.out.println("Incorrect operands.");
                    return;
                }

                repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                repo.reset(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                return;
        }
        repo.store();
    }
}
