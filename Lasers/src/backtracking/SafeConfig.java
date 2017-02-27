package backtracking;

import model.Commands;
import model.LasersModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the model
 * package and/or incorporate it into another class.
 *
 * @author Sean Strout @ RIT CS
 * @author Devon Bagley
 * @author Ivan Tsui
 */
public class SafeConfig implements Configuration {
    // the safe grid
    public String[][] safe;
    // the x boundary
    public int x;
    // the y boundary
    public int y;
    // the current x
    public int currentX;
    // the current y
    public int currentY;
    // input file name
    private String filename;

    /**
     * The initial SafeConfig based on the input file
     * @param filename input file name
     * @throws FileNotFoundException
     */
    public SafeConfig(String filename) throws FileNotFoundException{
        this.filename = filename;
        Scanner in = new Scanner(new File(filename));
        this.x = in.nextInt();
        this.y = in.nextInt();
        this.safe = new String[this.x][this.y];
        for (int r = 0; r < this.x; r++) {
            for (int c = 0; c < this.y; c++) {
                this.safe[r][c] = in.next();
            }
        }
        in.close();
        this.currentX = 0;
        this.currentY = -1;

    }

    /**
     * Deep copy of the given SafeConfig
     * @param other given SafeConfig
     */
    public SafeConfig(SafeConfig other){
        this.x = other.x;
        this.y = other.y;
        this.currentX = other.currentX;
        this.currentY = other.currentY;
        this.filename = other.filename;

        this.safe = new String[this.x][this.y];

        for(int r = 0; r < this.x; r++){
            for(int c = 0; c < this.y; c++){
                this.safe[r][c] = other.safe[r][c];
            }
        }
    }

    /**
     * Add places a laser in the bank at the specified coordinates
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private void add( int x, int y){
        int xx = x;
        int yy = y;

        if((x<this.x && x>=0 && y < this.y && y>=0)){

            if (this.safe[x][y].equals(".") || this.safe[x][y].equals("*")) {

                this.safe[x][y] = "L";

                // Adds beams south
                while (xx < this.x - 1 && (this.safe[xx + 1][y].equals("*") || this.safe[xx + 1][y].equals("."))) {
                    this.safe[xx + 1][y] = "*";
                    xx++;
                }
                xx = x;

                // Adds beams north
                while (xx > 0 && (this.safe[xx - 1][y].equals("*") || this.safe[xx - 1][y].equals("."))) {
                    this.safe[xx - 1][y] = "*";
                    xx--;
                }

                // Adds beams east
                while (yy < this.y - 1 && (this.safe[x][yy + 1].equals("*") || this.safe[x][yy + 1].equals("."))) {
                    this.safe[x][yy + 1] = "*";
                    yy++;
                }
                yy = y;

                // Adds beams west
                while (yy > 0 && (this.safe[x][yy - 1].equals("*") || this.safe[x][yy - 1].equals("."))) {
                    this.safe[x][yy - 1] = "*";
                    yy--;
                }
            }
        }
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        Collection<Configuration> child = new ArrayList<>();
        SafeConfig safe1 = new SafeConfig(this);

        if(this.currentY == this.y-1 && this.currentX != this.x-1){ //if at end of row move down one and start from beginning
            safe1.currentX +=1;
            safe1.currentY = 0;
            if(safe1.safe[safe1.currentX][safe1.currentY].equals("1")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("2")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("3")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("0")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("4")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("X")){ // if the next cell is a pillar add as a successor
                child.add(safe1);
            }
            else{
                SafeConfig safe2 = new SafeConfig(this); // else the next successors is a L or a */.
                safe2.currentX +=1;
                safe2.currentY = 0;
                safe2.add(safe2.currentX, safe2.currentY);
                child.add(safe2);
                child.add(safe1);
            }

        }else{
            safe1.currentY +=1;
            if(safe1.safe[safe1.currentX][safe1.currentY].equals("1")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("2")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("3")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("0")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("4")
                    || safe1.safe[safe1.currentX][safe1.currentY].equals("X")){
                child.add(safe1);
            }
            else{
                SafeConfig safe2 = new SafeConfig(this);
                safe2.currentY +=1;
                safe2.add(safe2.currentX, safe2.currentY);
                child.add(safe2);
                child.add(safe1);
            }
        }
        return child;
    }

    /**
     * Checks pillar at given coordinates for adjacent lasers and if laser count matches pillar's # of outlets
     * @param count pillar's # of outlets
     * @param r row coordinate
     * @param c column coordinate
     * @return true if valid pillar
     */
    public boolean checkPillar(int count, int r, int c){
        int lasers = 0;

        // Checks for laser south
        if(r < this.x-1){

            if(this.safe[r+1][c].equals("L")){
                lasers++;
                if(lasers > count){
                    return false;
                }
            }
        }

        // Checks for laser north
        if(r > 0){
            if(this.safe[r-1][c].equals("L")){
                lasers++;
                if(lasers > count){
                    return false;
                }
            }
        }

        //Checks for laser east
        if(c < this.y-1){
            if(this.safe[r][c+1].equals("L")){
                lasers++;
                if(lasers > count){
                    return false;
                }
            }
        }

        // Checks  for another laser west
        if(c > 0){
            if(this.safe[r][c-1].equals("L")){
                lasers++;
                if(lasers > count){
                    return false;
                }
            }
        }
        if(lasers != count){
            return false;
        }
        return true;
    }

    @Override
    public boolean isValid() {

        if(this.currentX == this.x-1 && this.currentY == this.y-1){ // if at the last cell in the grid
            for(int r =0; r < this.currentX; r++){
                for(int c =0; c < this.currentY; c++){ // check the safe for empty spaces and pillars
                    if(this.safe[r][c].equals(".")){
                        return false;
                    }else if(this.safe[r][c].equals("0") ||
                            this.safe[r][c].equals("1") ||
                            this.safe[r][c].equals("2") ||
                            this.safe[r][c].equals("3") ||
                            this.safe[r][c].equals("4")){
                        int value = Integer.parseInt(this.safe[r][c]); // if a pillar check the pillars lasers
                        if(!checkPillar(value, r, c)){
                            return false;
                        }
                    }
                }
            }
            if(!this.isGoal()){ // if in last cell and not the goal return false
                return false;
            }
        }
        if(this.safe[this.currentX][this.currentY].equals("L")) {   // if current cell is a laser
            int xx = this.currentX;
            int yy = this.currentY;

            // Checks for another laser south
            while (xx < this.x - 1) {
                if (this.safe[xx + 1][yy].equals("L")) {
                    return false;
                }
                else if(this.safe[xx + 1][yy].equals("0") ||
                        this.safe[xx + 1][yy].equals("1") ||
                        this.safe[xx + 1][yy].equals("2") ||
                        this.safe[xx + 1][yy].equals("3") ||
                        this.safe[xx + 1][yy].equals("4") ||
                        this.safe[xx + 1][yy].equals("X")){
                    break;
                }
                xx++;
            }
            xx = this.currentX;

            // Checks for another laser north
            while (xx > 0) {
                if (this.safe[xx - 1][yy].equals("L")) {
                    return false;
                }
                else if(this.safe[xx - 1][yy].equals("0") ||
                        this.safe[xx - 1][yy].equals("1") ||
                        this.safe[xx - 1][yy].equals("2") ||
                        this.safe[xx - 1][yy].equals("3") ||
                        this.safe[xx - 1][yy].equals("4") ||
                        this.safe[xx - 1][yy].equals("X")){
                    break;
                }
                xx--;
            }
            xx = this.currentX;

            // Checks for another laser east
            while (yy < this.y - 1) {
                if (this.safe[xx][yy + 1].equals("L")) {
                    return false;
                }
                else if(this.safe[xx][yy + 1].equals("0") ||
                        this.safe[xx][yy + 1].equals("1") ||
                        this.safe[xx][yy + 1].equals("2") ||
                        this.safe[xx][yy + 1].equals("3") ||
                        this.safe[xx][yy + 1].equals("4") ||
                        this.safe[xx][yy + 1].equals("X")){
                    break;
                }
                yy++;
            }
            yy = this.currentY;

            // Checks  for another laser west
            while (yy > 0) {
                if (this.safe[xx][yy - 1].equals("L")) {
                    return false;
                }
                else if(this.safe[xx][yy - 1].equals("0") ||
                        this.safe[xx][yy - 1].equals("1") ||
                        this.safe[xx][yy - 1].equals("2") ||
                        this.safe[xx][yy - 1].equals("3") ||
                        this.safe[xx][yy - 1].equals("4") ||
                        this.safe[xx][yy - 1].equals("X")){
                    break;
                }
                yy--;
            }
        }

        return true;
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


    @Override
    public boolean isGoal() {

        if(this.currentX == this.x-1 || this.currentY == this.y-1){

            try {
                LasersModel other = new LasersModel(this.filename);

                for(int r = 0; r < this.x; r++){
                    for(int c = 0; c < this.y; c++){
                        other.safe[r][c] = this.safe[r][c];
                    }
                }
                other.commands.verify(other);
                return other.commands.valid;
            }catch(FileNotFoundException e){}
        }

        return false;
    }
}
