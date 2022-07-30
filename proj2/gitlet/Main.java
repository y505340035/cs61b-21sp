package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
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
        switch(firstArg) {
            case "init":
                if (Repository.GITLET_DIR.exists()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    return;
                }
                new Repository().init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if (Repository.CURRENT_REPOSITORY.exists()) {
                    String fileName = args[1];
                    File file = new File(fileName);
                    if (!file.exists()) {
                        System.out.println("File does not exist.");
                        return;
                    }

                    Repository repo = Utils.readObject(Repository.CURRENT_REPOSITORY, Repository.class);
                    repo.add(args[1]);
                }
                break;
            // TODO: FILL THE REST IN
            default:
                System.out.println("No command with that name exists.");
        }
    }
}
