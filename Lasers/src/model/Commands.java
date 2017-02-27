package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * The class for editing model's safe
 * @author Devon Bagley
 * @author Ivan Tsui
 */
public class Commands extends Observable {
    public String status = "";
    public int x = -1;
    public int y = -1;
    public boolean valid = true;

    /**
     * Announce to gui there are changes
     */
    public void announceChange() {
        setChanged();
        notifyObservers();
    }

    /**
     * Add places a laser in the bank at the specified coordinates
     *
     * @param bank the bank object being worked on
     * @param x the x coordinate
     * @param y the y coordinate
     */

    public void add(LasersModel bank, int x, int y){
        int xx = x;
        int yy = y;
        if(!(x<bank.x && x>=0 && y < bank.y && y>=0)){
            this.status = "Error adding laser at: (" + x + ", " + y +")";
        }else {

            if (bank.safe[x][y].equals(".") || bank.safe[x][y].equals("*")) {

                bank.safe[x][y] = "L";
                this.status = "Laser added at: (" + x + ", " + y + ")";

                // Adds beams south
                while (xx < bank.x - 1 && (bank.safe[xx + 1][y].equals("*") || bank.safe[xx + 1][y].equals("."))) {
                    bank.safe[xx + 1][y] = "*";
                    xx++;
                }
                xx = x;

                // Adds beams north
                while (xx > 0 && (bank.safe[xx - 1][y].equals("*") || bank.safe[xx - 1][y].equals("."))) {
                    bank.safe[xx - 1][y] = "*";
                    xx--;
                }

                // Adds beams east
                while (yy < bank.y - 1 && (bank.safe[x][yy + 1].equals("*") || bank.safe[x][yy + 1].equals("."))) {
                    bank.safe[x][yy + 1] = "*";
                    yy++;
                }
                yy = y;

                // Adds beams west
                while (yy > 0 && (bank.safe[x][yy - 1].equals("*") || bank.safe[x][yy - 1].equals("."))) {
                    bank.safe[x][yy - 1] = "*";
                    yy--;
                }
            } else {

                this.status = "Error adding laser at: (" + x + ", " + y + ")";

            }
        }

        this.announceChange();
    }

    /**
     * display prints out the bank to be seen by the user
     *
     * @param bank the bank object being worked on
     */
    public void display(LasersModel bank){
        System.out.println(this.status + "\n" + bank.toString());
        this.status = "";
    }

    /**
     * Help displays all of the commands a user can enter
     */
    public void help(){

        System.out.println("a|add rc: Add laser to (r, c)");
        System.out.println("d|display: Display safe");
        System.out.println("h|help: Print this help message");
        System.out.println("q|quit: Exit program");
        System.out.println("r|remove r c: Remove laser from (r,c)");
        System.out.println("v|verify: Verify safe correctness");

    }

    /**
     * quit tells the program to exit
     */
    public void quit(){     System.exit(0);    }

    /**
     * Checks pillar at given coordinates for adjacent lasers and if laser count matches pillar's # of outlets
     * @param bank bank to check if pillar
     * @param count pillar's # of outlets
     * @param r row coordinate
     * @param c column coordinate
     * @return true if valid pillar
     */
    public void checkPillar(LasersModel bank,int count, int r, int c){
        int lasers = 0;

        // Checks for another laser south
        if(r < bank.x-1){

            if(!(valid)){
                this.status = "Error verifying at: (" + r + ", " + c + ")";
                this.x = r;
                this.y = c;
            }
            if(bank.safe[r+1][c].equals("L")){
                lasers++;
                if(lasers > count){
                    valid = false;
                    this.status = "Error verifying at: (" + r + ", " + c + ")";
                    this.x = r;
                    this.y = c;
                }
            }
        }

        // Checks for another laser north
        if(r > 0){
            if(!(valid)){
                this.status = "Error verifying at: (" + r + ", " + c + ")";
                this.x = r;
                this.y = c;
            }
            if(bank.safe[r-1][c].equals("L")){
                lasers++;
                if(lasers > count){
                    valid = false;
                    this.status = "Error verifying at: (" + r + ", " + c + ")";
                    this.x = r;
                    this.y = c;
                }
            }
        }

        // Checks for another laser east
        if(c < bank.y-1){
            if(!(valid)){
                this.status = "Error verifying at: (" + r + ", " + c + ")";
                this.x = r;
                this.y = c;
            }
            if(bank.safe[r][c+1].equals("L")){
                lasers++;
                if(lasers > count){
                    valid = false;
                    this.status = "Error verifying at: (" + r + ", " + c + ")";
                    this.x = r;
                    this.y = c;
                }
            }
        }

        // Checks  for another laser west
        if(c > 0){
            if(!(valid)){
                this.status = "Error verifying at: (" + r + ", " + c + ")";
                this.x = r;
                this.y = c;
            }
            if(bank.safe[r][c-1].equals("L")){
                lasers++;
                if(lasers > count){
                    valid = false;
                    this.status = "Error verifying at: (" + r + ", " + c + ")";
                    this.x = r;
                    this.y = c;
                }
            }
        }
        if(lasers != count){
            this.valid = false;
            this.status = "Error verifying at: (" + r + ", " + c + ")";
            this.x = r;
            this.y = c;
        }
    }


    /** verify that the bank is currently a valid configuration
     * based on the rules of the lasers and pillars
     *
     * @param bank the bank object being worked on
     */

    public void verify(LasersModel bank){
        valid = true;
        outerloop:
        for(int r = 0; r < bank.x; r++){
            for(int c = 0; c < bank.y; c++){

                if(bank.safe[r][c].equals(".")){
                    valid = false;
                    if(!(valid)){
                        this.status = "Error verifying at: (" + r + ", " + c + ")";
                        this.x = r;
                        this.y = c;
                        break outerloop;
                    }
                }
                else if(bank.safe[r][c].equals("L")){
                    int xx = r;
                    int yy = c;

                    if(xx < bank.x-1) { // check for pillar south
                        if (bank.safe[xx + 1][yy].equals("0") ||
                                bank.safe[xx + 1][yy].equals("1") ||
                                bank.safe[xx + 1][yy].equals("2") ||
                                bank.safe[xx + 1][yy].equals("3") ||
                                bank.safe[xx + 1][yy].equals("4") ||
                                bank.safe[xx + 1][yy].equals("X")) {
                            if (!bank.safe[xx + 1][yy].equals("X")) {
                                int count = Integer.parseInt(bank.safe[xx + 1][yy]);
                                checkPillar(bank, count, xx + 1, yy);
                                if (!valid) {
                                    break outerloop;
                                }
                            }
                        }
                    }
                    if(xx > 0) { // check for pillar north
                        if (bank.safe[xx - 1][yy].equals("0") ||
                                bank.safe[xx - 1][yy].equals("1") ||
                                bank.safe[xx - 1][yy].equals("2") ||
                                bank.safe[xx - 1][yy].equals("3") ||
                                bank.safe[xx - 1][yy].equals("4") ||
                                bank.safe[xx - 1][yy].equals("X")) {
                            if (!bank.safe[xx - 1][yy].equals("X")) {
                                int count = Integer.parseInt(bank.safe[xx - 1][yy]);
                                checkPillar(bank, count, xx - 1, yy);
                                if (!valid) {
                                    break outerloop;
                                }
                            }
                        }
                    }

                    if(yy < bank.y-1) { // check for pillar East
                        if (bank.safe[xx][yy + 1].equals("0") ||
                                bank.safe[xx][yy + 1].equals("1") ||
                                bank.safe[xx][yy + 1].equals("2") ||
                                bank.safe[xx][yy + 1].equals("3") ||
                                bank.safe[xx][yy + 1].equals("4") ||
                                bank.safe[xx][yy + 1].equals("X")) {
                            if (!bank.safe[xx][yy + 1].equals("X")) {
                                int count = Integer.parseInt(bank.safe[xx][yy + 1]);
                                checkPillar(bank, count, xx, yy + 1);

                                if (!this.valid) {
                                    break outerloop;
                                }
                            }
                        }
                    }

                    if(yy > 0) { // check for pillar west
                        if (bank.safe[xx][yy - 1].equals("0") ||
                                bank.safe[xx][yy - 1].equals("1") ||
                                bank.safe[xx][yy - 1].equals("2") ||
                                bank.safe[xx][yy - 1].equals("3") ||
                                bank.safe[xx][yy - 1].equals("4") ||
                                bank.safe[xx][yy - 1].equals("X")) {
                            if (!bank.safe[xx][yy - 1].equals("X")) {
                                int count = Integer.parseInt(bank.safe[xx][yy - 1]);
                                checkPillar(bank, count, xx, yy - 1);

                                if (!this.valid) {
                                    break outerloop;
                                }
                            }
                        }
                    }

                    if(!(valid)){
                        this.status = "Error verifying at: (" + r + ", " + c + ")";
                        this.x = r;
                        this.y = c;
                        break outerloop;
                    }
                    // Checks for another laser south
                    while(xx < bank.x-1){
                        if(!(valid)){
                            this.status = "Error verifying at: (" + r + ", " + c + ")";
                            this.x = r;
                            this.y = c;
                            break outerloop;
                        }
                        if(bank.safe[xx + 1][yy].equals("0") ||
                                bank.safe[xx + 1][yy].equals("1") ||
                                bank.safe[xx + 1][yy].equals("2") ||
                                bank.safe[xx + 1][yy].equals("3") ||
                                bank.safe[xx + 1][yy].equals("4") ||
                                bank.safe[xx + 1][yy].equals("X")){
                            break;
                        }

                        if(bank.safe[xx+1][yy].equals("L")){
                            valid = false;
                            this.status = "Error verifying at: (" + r + ", " + c + ")";
                            this.x = r;
                            this.y = c;
                            break outerloop;
                        }
                        if(!(bank.safe[xx+1][yy].equals("*"))){
                            break;
                        }
                        xx++;
                    }
                    xx = r;

                    // Checks for another laser north
                    while(xx > 0){
                        if(!(valid)){
                            this.status = "Error verifying at: (" + r + ", " + c + ")";
                            this.x = r;
                            this.y = c;
                            break outerloop;
                        }
                        if(bank.safe[xx - 1][yy].equals("0") ||
                                bank.safe[xx - 1][yy].equals("1") ||
                                bank.safe[xx - 1][yy].equals("2") ||
                                bank.safe[xx - 1][yy].equals("3") ||
                                bank.safe[xx - 1][yy].equals("4") ||
                                bank.safe[xx - 1][yy].equals("X")){
                            break;
                        }

                        if(bank.safe[xx-1][yy].equals("L")){
                            valid = false;
                            this.status = "Error verifying at: (" + r + ", " + c + ")";
                            this.x = r;
                            this.y = c;
                            break outerloop;
                        }
                        if(!(bank.safe[xx-1][yy].equals("*"))){
                            break;
                        }
                        xx--;
                    }
                    xx = r;

                    // Checks for another laser east
                    while(yy < bank.y-1){
                        if(!(valid)){
                            this.status = "Error verifying at: (" + r + ", " + c + ")";
                            this.x = r;
                            this.y = c;
                            break outerloop;
                        }
                        if(bank.safe[xx][yy + 1].equals("0") ||
                                bank.safe[xx][yy + 1].equals("1") ||
                                bank.safe[xx][yy + 1].equals("2") ||
                                bank.safe[xx][yy + 1].equals("3") ||
                                bank.safe[xx][yy + 1].equals("4") ||
                                bank.safe[xx][yy + 1].equals("X")){
                            break;
                        }
                        if(bank.safe[xx][yy+1].equals("L")){
                            valid = false;
                            this.status = "Error verifying at: (" + r + ", " + c + ")";
                            this.x = r;
                            this.y = c;
                            break outerloop;
                        }
                        if(!(bank.safe[xx][yy+1].equals("*"))){
                            break;
                        }
                        yy++;
                    }
                    yy = c;

                    // Checks  for another laser west
                    while(yy > 0){
                        if(!(valid)){
                            this.status = "Error verifying at: (" + r + ", " + c + ")";
                            this.x = r;
                            this.y = c;
                            break outerloop;
                        }
                        if(bank.safe[xx][yy - 1].equals("0") ||
                                bank.safe[xx][yy - 1].equals("1") ||
                                bank.safe[xx][yy - 1].equals("2") ||
                                bank.safe[xx][yy - 1].equals("3") ||
                                bank.safe[xx][yy - 1].equals("4") ||
                                bank.safe[xx][yy - 1].equals("X")){
                            break;
                        }
                        if(bank.safe[xx][yy-1].equals("L")){
                            valid = false;this.status = "Error verifying at: (" + r + ", " + c + ")";
                            this.x = r;
                            this.y = c;
                            break outerloop;
                        }
                        if(!(bank.safe[xx][yy-1].equals("*"))){
                            break;
                        }
                        yy--;
                        if(!(valid)){
                            this.status = "Error verifying at: (" + r + ", " + c + ")";
                            this.x = r;
                            this.y = c;
                            break outerloop;
                        }
                    }
                    if(!(valid)){
                        this.status = "Error verifying at: (" + r + ", " + c + ")";
                        this.x = r;
                        this.y = c;
                        break outerloop;
                    }

                }else if(bank.safe[r][c].equals("0") || bank.safe[r][c].equals("1") || bank.safe[r][c].equals("2") || bank.safe[r][c].equals("3") ||  bank.safe[r][c].equals("4")){
                    int count = Integer.parseInt(bank.safe[r][c]);
                    checkPillar(bank,count,r,c);
                    if(!valid){
                        break outerloop;
                    }
                }
            }
        }
        if(valid) {
            this.status = "Safe is fully verified!";
        }
        announceChange();
    }

    /**
     * A helper boolean function to see if the bank at a
     * coordinate is a Beam
     *
     * @param bank the bank object being worked on
     * @param x the x coordinate
     * @param y the y coordinate
     * @return boolean
     */
    public boolean isBeam(LasersModel bank,int x, int y){
        return bank.safe[x][y].equals("*");
    }

    /**
     * A helper boolean function to see if the bank at a
     * coordinate is a Laser
     *
     * @param bank the bank object being worked on
     * @param x the x coordinate
     * @param y the y coordinate
     * @return boolean
     */
    public boolean isL(LasersModel bank, int x, int y){
        return bank.safe[x][y].equals("L");
    }

    /**
     * Removes a laser and its beam from the bank at the specified coordinates
     *
     * @param bank the bank object being worked on
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void remove(LasersModel bank, int x, int y) {
        if (!(x<bank.x && x>=0 && y < bank.y && y>=0)) {
            this.status = "Laser removed at: (" + x + ", " + y + ")";
        } else {

            if (bank.safe[x][y].equals("L")) {

                bank.safe[x][y] = ".";
                this.status = "Laser removed at: (" + x + ", " + y + ")";

                // values for immediate cardinal directions of the current tile
                int N = x - 1;
                int S = x + 1;
                int W = y - 1;
                int E = y + 1;

                boolean vertL = false;

                // removes beams north
                while (N >= 0) {
                    if (isL(bank, N, y)) {
                        N++;
                        vertL = true;
                        while (N <= bank.x - 1 && (bank.safe[N][y].equals("*") || bank.safe[N][y].equals("."))) {
                            bank.safe[N][y] = "*";
                            N++;
                        }
                        break;
                    }
                    if (!(isBeam(bank, N, y))) {
                        break;
                    }

                    bank.safe[N][y] = ".";
                    while (E <= bank.y - 1) {
                        if (isL(bank, N, E)) {
                            bank.safe[N][y] = "*";
                            break;
                        }
                        if (!(isBeam(bank, N, E))) {
                            break;
                        } else {
                            E++;
                        }
                    }
                    E = y + 1;
                    while (W >= 0) {
                        if (isL(bank, N, W)) {
                            bank.safe[N][y] = "*";
                            break;
                        }
                        if (!(isBeam(bank, N, W))) {
                            break;
                        } else {
                            W--;
                        }
                    }
                    W = y - 1;
                    N--;
                }

                N = x - 1;
                S = x + 1;
                W = y - 1;
                E = y + 1;

                // Removes beams south
                while (S <= bank.x - 1) {
                    if (vertL) {
                        break;
                    }

                    if (isL(bank, S, y)) {
                        S--;
                        while (S >= 0 && (bank.safe[S][y].equals("*") || bank.safe[S][y].equals("."))) {
                            bank.safe[S][y] = "*";
                            S--;
                        }
                        break;
                    }
                    if (!(isBeam(bank, S, y))) {
                        break;
                    }
                    bank.safe[S][y] = ".";
                    while (E <= bank.y - 1) {
                        if (isL(bank, S, E)) {
                            bank.safe[S][y] = "*";
                            break;
                        }
                        if (!(isBeam(bank, S, E))) {
                            break;
                        } else {
                            E++;
                        }
                    }
                    E = y + 1;
                    while (W >= 0) {
                        if (isL(bank, S, W)) {
                            bank.safe[S][y] = "*";
                            break;
                        }
                        if (!(isBeam(bank, S, W))) {
                            break;
                        } else {
                            W--;
                        }
                    }
                    W = y - 1;
                    S++;
                }

                N = x - 1;
                S = x + 1;
                W = y - 1;
                E = y + 1;

                boolean hortL = false;
                // Removes beams East
                while (E <= bank.y - 1) {
                    if (isL(bank, x, E)) {
                        E--;
                        hortL = true;
                        while (E >= 0 && (bank.safe[x][E].equals("*") || bank.safe[x][E].equals("."))) {
                            bank.safe[x][E] = "*";
                            E--;
                        }
                        break;
                    }
                    if (!(isBeam(bank, x, E))) {
                        break;
                    }

                    bank.safe[x][E] = ".";
                    while (S <= bank.x - 1) {
                        if (isL(bank, S, E)) {
                            bank.safe[x][E] = "*";
                            break;
                        }
                        if (!(isBeam(bank, S, E))) {
                            break;
                        } else {
                            S++;
                        }
                    }
                    S = x + 1;
                    while (N >= 0) {
                        if (isL(bank, N, E)) {
                            bank.safe[x][E] = "*";
                            break;
                        }
                        if (!(isBeam(bank, N, E))) {
                            break;
                        } else {
                            N--;
                        }
                    }
                    N = x - 1;
                    E++;
                }
                N = x - 1;
                S = x + 1;
                W = y - 1;
                E = y + 1;

                // Removes beams West
                while (W >= 0) {
                    if (hortL) {
                        break;
                    }

                    if (isL(bank, x, W)) {
                        W++;
                        while (W <= bank.y - 1 && (bank.safe[x][W].equals("*") || bank.safe[x][W].equals("."))) {
                            bank.safe[x][W] = "*";
                            W++;
                        }
                        break;
                    }

                    if (!(isBeam(bank, x, W))) {
                        break;
                    }

                    bank.safe[x][W] = ".";
                    while (S <= bank.x - 1) {
                        if (isL(bank, S, W)) {
                            bank.safe[x][W] = "*";
                            break;
                        }
                        if (!(isBeam(bank, S, W))) {
                            break;
                        } else {
                            S++;
                        }
                    }
                    S = x + 1;
                    while (N >= 0) {
                        if (isL(bank, N, W)) {
                            bank.safe[x][W] = "*";
                            break;
                        }
                        if (!(isBeam(bank, N, W))) {
                            break;
                        } else {
                            N--;
                        }
                    }
                    N = x - 1;
                    W--;
                }
            } else {

                this.status = "Error removing laser at: (" + x + ", " + y + ")";
            }
        }
        announceChange();
    }
}
