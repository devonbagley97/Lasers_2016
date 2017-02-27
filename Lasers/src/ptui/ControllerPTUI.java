package ptui;

import model.LasersModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author Sean Strout @ RIT CS
 * @author Devon Bagley
 * @author Ivan Tsui
 */
public class ControllerPTUI  {
    /** The UI's connection to the model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
    }

    /**
     * Run the main loop.  This is the entry point for the controller
     * @param inputFile The name of the input command file, if specified
     */
    public void run(String inputFile) {

        if(inputFile == null){
            this.model.commands.display(this.model);
            userInput(model);
        }
        else {
            this.model.commands.display(this.model);
            fileInput(this.model,inputFile);
        }
    }

    /**
     * Check what the user has typed into program
     * @param bank bank to edit
     * @param cmd string command from user
     */
    public void help(LasersModel bank, String cmd){
        String[] parts = cmd.split(" ");

        if(parts[0].startsWith("a")){
            if(parts.length == 3){
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                this.model.commands.add(bank,x,y);
            }else{
                System.out.println("Incorrect coordinates");
            }
        }else if(parts[0].startsWith("d")){
            this.model.commands.display(bank);
        }else if(parts[0].startsWith("h")){
            this.model.commands.help();
        }else if(parts[0].startsWith("q")){
            this.model.commands.quit();
        }else if(parts[0].startsWith("r")){
            if(parts.length == 3){
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                this.model.commands.remove(bank,x,y);

            }else{
                System.out.println("Incorrect coordinates");
            }

        }else if(parts[0].startsWith("v")){
            this.model.commands.verify(bank);
        }else if(!(parts[0].equals(""))) {
            System.out.println("Unrecognized command: "+cmd);
        }
    }

    /**
     * when asking for userInput directly, prompts the user to give a
     * command and then runs those commands
     *
     * @param bank the bank object being worked on
     */
    public void userInput(LasersModel bank){
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("> ");
            while(sc.hasNextLine()){
                String cmd = sc.nextLine();

                this.help(bank,cmd);
                System.out.print("> ");
            }
        }
    }

    /**
     * when run in fileInput mode this will read in lines from the given file
     * and run the commands until end of file. then prompts the user for more input
     *
     * @param bank the bank object being worked on
     * @param filename the file with a list of commands
     */
    public void fileInput(LasersModel bank, String filename){
        try(FileInputStream filestream = new FileInputStream(filename)){
            Scanner sc = new Scanner(filestream);
            while(sc.hasNextLine()){
                String cmd = sc.nextLine();
                System.out.println("> "+cmd);
                this.help(bank,cmd);
            }
            userInput(bank);
        } catch(IOException ioe){
            System.out.println("Could not open file " + filename);
        }
    }
}
