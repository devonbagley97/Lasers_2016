package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Scanner;

/**
 * Represents model of bank
 * @author Devon Bagley
 * @author Ivan Tsui
 */
public class LasersModel extends Observable {

    //the 2 dimensional safe
    public static String safe[][];
    //the x dimension
    public static int x;
    //the y dimension
    public static int y;

    public static Commands commands;

    public String inputFile;

    public LasersModel(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new File(filename));

        // Set the values for the row and column
        this.x = in.nextInt();
        this.y = in.nextInt();

        // Set the safe
        this.safe = new String[this.x][this.y];
        for(int row = 0; row < this.x; row++){
            for(int col = 0; col < this.y; col++){
                this.safe[row][col] = in.next();
            }
        }
        in.close();
        this.commands = new Commands();
        inputFile = filename;
    }

    /**
     * toString creates the bank by going to each row/column pair and creating a string representation.
     *
     * @return String
     */
    @Override
    public String toString(){

        // Makes and prints exact number of horizontal dividers
        String horDiv = "  " + new String(new char[2 * this.x - 1]).replace("\0", "-") + " \n";

        // First, print the numbers of the row at the top
        String rowNum = "  ";
        for(int r = 0; r < this.x; r++){
            rowNum += r + " ";
        }

        // Make the actual safe
        String tiles = "";
        for(int r = 0; r < this.x; r++){
            tiles += r + "|";
            for(int c = 0; c < this.y; c++){
                tiles += this.safe[r][c] + " ";

            }
            if(r != this.x - 1) {
                tiles += "\n";
            }
        }

        return rowNum + "\n" + horDiv + tiles;
    }

}
