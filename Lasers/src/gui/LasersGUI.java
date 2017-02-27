package gui;

import backtracking.Backtracker;
import backtracking.Configuration;
import backtracking.SafeConfig;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import model.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the model
 * and receives updates from it.
 *
 * @author Sean Strout @ RIT CS
 * @author Devon Bagley
 * @author Ivan Tsui
 */
public class LasersGUI extends Application implements Observer {
    /** The UI's connection to the model */
    private LasersModel model;

    /** this can be removed - it is used to demonstrates the button toggle */
    private static boolean status = true;

    // the UI's center grid of buttons
    GridPane centerGrid;

    // The message at top of borderpane
    Label text;

    @Override
    public void init() throws Exception {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        try {
            Parameters params = getParameters();
            String filename = params.getRaw().get(0);
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
        this.model.commands.addObserver(this);
    }

    /**
     * This is a private demo method that shows how to create a button
     * and attach a foreground image with a background image that
     * toggles from yellow to red each time it is pressed.
     *
     * @param stage the stage to add components into
     */
    private void panes(Stage stage) {
        // Make border pane
        BorderPane border = new BorderPane();
        border.setPrefSize(400, 400);

        // TOP of border pane
        this.text = new Label(model.inputFile + " loaded");
        this.text.setFont(new Font("Serif", 20));
        border.setAlignment(text, Pos.CENTER);
        border.setTop(text);
        this.centerGrid = makeCenterGrid();

        border.setCenter(this.centerGrid);

        // BOTTOM of border pane
        GridPane bottomGrid = new GridPane();
        bottomGrid.setAlignment(Pos.CENTER);
        bottomGrid.setPadding(new Insets(0, 0, 10, 0));

        // Make Check button
        Button check = new Button("Check");
        check.setMinWidth(20);
        check.setMinHeight(10);
        check.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                model.commands.verify(model);

                if(model.commands.x != -1){
                    Button btn = (Button)getBtn(centerGrid, model.commands.x , model.commands.y);
                    btn.setStyle("-fx-background-color: red");
                    model.commands.x = -1;
                    model.commands.y = -1;
                }

            }
        });
        bottomGrid.add(check, 0, 0);

        // Make Hint button
        Button hint = new Button("Hint");
        hint.setMinWidth(20);
        hint.setMinHeight(10);
        hint.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                try {
                    String filename = model.inputFile;
                    Configuration init = new SafeConfig(filename);
                    Backtracker bt = new Backtracker(false);
                    String[][] copy = new String[model.x][model.y];
                    for(int r = 0; r < model.x; r++){
                        for(int c = 0; c < model.y; c++){
                            copy[r][c] = model.safe[r][c];
                        }
                    }

                    List<Configuration> path = bt.solveWithPath(init);

                    for(int r = 0; r < model.x; r++){
                        for(int c = 0; c < model.y; c++){
                            model.safe[r][c] = copy[r][c];
                        }
                    }

                    outerloop:
                    for(int i=path.size()-1; i > -1; i--){

                        for(int r = 0; r < model.x; r++){
                            for(int c = 0; c < model.y; c++){
                                SafeConfig current = (SafeConfig)path.get(i);
                                SafeConfig sol = (SafeConfig)path.get(0);

                                if(current.safe[r][c].equals("L") && !(model.safe[r][c].equals("L"))){
                                    model.commands.add(model,r,c);
                                    model.commands.status = "Hint: added laser to ("+r+", "+c+")";
                                    centerGrid = makeCenterGrid();
                                    border.setCenter(centerGrid);
                                    model.commands.announceChange();
                                    break outerloop;

                                }else if(model.safe[r][c].equals("L") && (!(sol.safe[r][c].equals("L")))){
                                    model.commands.status = "Hint: no next step!";
                                    centerGrid = makeCenterGrid();
                                    border.setCenter(centerGrid);
                                    model.commands.announceChange();
                                    break outerloop;

                                }else {
                                    model.commands.status = "Hint: no next step!";
                                }
                            }
                        }
                    }
                    centerGrid = makeCenterGrid();
                    border.setCenter(centerGrid);
                    model.commands.announceChange();
                }
                catch(FileNotFoundException e){}

            }
        });
        bottomGrid.add(hint, 1, 0);

        // Make Solve button
        Button solve = new Button("Solve");
        solve.setMinWidth(20);
        solve.setMinHeight(10);
        solve.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                try {
                    Configuration init = new SafeConfig(model.inputFile);
                    Backtracker bt = new Backtracker(false);
                    model = new LasersModel(model.inputFile);
                    Optional<Configuration> sol = bt.solve(init);
                    Configuration config = sol.get();
                    SafeConfig s = (SafeConfig)config;

                    for(int r = 0; r < model.x; r++){
                        for(int c = 0; c < model.y; c++){
                           model.safe[r][c] =  s.safe[r][c];
                        }
                    }
                    model.commands.status = model.inputFile+ " solved!";
                    centerGrid = makeCenterGrid();
                    border.setCenter(centerGrid);
                    model.commands.announceChange();
                }
                catch(FileNotFoundException e){}
            }
        });
        bottomGrid.add(solve, 2, 0);

        // Make Restart button
        Button restart = new Button("Restart");
        restart.setMinWidth(20);
        restart.setMinHeight(10);
        restart.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                try {
                    String input = model.inputFile;
                    model = new LasersModel(input);
                    centerGrid = makeCenterGrid();
                    border.setCenter(centerGrid);
                    model.commands.status = model.inputFile.toString()+ " has been reset";
                    model.commands.announceChange();
                }
                catch(FileNotFoundException e){}
            }
        });
        bottomGrid.add(restart, 3, 0);

        // Make Load button
        Button load = new Button("Load");
        load.setMinWidth(20);
        load.setMinHeight(10);
        load.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.setInitialDirectory(new java.io.File("."));
                File selectedFile = fileChooser.showOpenDialog(stage);
                try {
                    if(selectedFile != null) {
                        model = new LasersModel(selectedFile.toString());
                        centerGrid = makeCenterGrid();
                        border.setCenter(centerGrid);
                        model.commands.status = model.inputFile.toString() + " loaded";
                        model.commands.announceChange();
                    }
                }
                catch(FileNotFoundException e){}
            }
        });
        bottomGrid.add(load, 4, 0);

        border.setBottom(bottomGrid);

        Scene scene = new Scene(border);
        stage.setScene(scene);
    }

    /**
     * Method for making center grid of buttons
     * @return gridpane of buttons
     */
    public GridPane makeCenterGrid() {
        centerGrid = new GridPane();
        centerGrid.setAlignment(Pos.CENTER);
        centerGrid.setHgap(3);
        centerGrid.setVgap(3);
        centerGrid.setPadding(new Insets(10, 10, 20, 10));
        for (int row = 0; row < this.model.x; row++) {
            for (int col = 0; col < this.model.y; col++) {
                Button button = new Button();
                button.setStyle("-fx-background-color: gray");
                button.setMinSize(30, 30);
                button.setMaxSize(30, 30);
                final int rowFinal = row;
                final int colFinal = col;
                if (this.model.safe[row][col].equals("1") ||
                        this.model.safe[row][col].equals("2") ||
                        this.model.safe[row][col].equals("3") ||
                        this.model.safe[row][col].equals("4") ||
                        this.model.safe[row][col].equals("0") ||
                        this.model.safe[row][col].equals("X")) {
                    String pillar = this.model.safe[row][col];
                    switch (pillar) {
                        case "0": {
                            Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar0.png"));
                            ImageView pillarIcon = new ImageView(pillarImg);
                            button.setGraphic(pillarIcon);
                            break;
                        }

                        case "1": {
                            Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar1.png"));
                            ImageView pillarIcon = new ImageView(pillarImg);
                            button.setGraphic(pillarIcon);
                            break;
                        }

                        case "2": {
                            Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar2.png"));
                            ImageView pillarIcon = new ImageView(pillarImg);
                            button.setGraphic(pillarIcon);
                            break;
                        }

                        case "3": {
                            Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar3.png"));
                            ImageView pillarIcon = new ImageView(pillarImg);
                            button.setGraphic(pillarIcon);
                            break;
                        }

                        case "4": {
                            Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar4.png"));
                            ImageView pillarIcon = new ImageView(pillarImg);
                            button.setGraphic(pillarIcon);
                            break;
                        }

                        case "X": {
                            Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillarX.png"));
                            ImageView pillarIcon = new ImageView(pillarImg);
                            button.setGraphic(pillarIcon);
                            break;
                        }

                    }
                }
                button.setOnAction(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent event){
                        if(model.safe[rowFinal][colFinal].equals(".") || model.safe[rowFinal][colFinal].equals("*")){
                            model.commands.add(model,rowFinal,colFinal);
                        }
                        else if(model.safe[rowFinal][colFinal].equals("L")){
                            model.commands.remove(model, rowFinal, colFinal);
                        }
                        else if(model.safe[rowFinal][colFinal].equals("1") ||
                                model.safe[rowFinal][colFinal].equals("2") ||
                                model.safe[rowFinal][colFinal].equals("3") ||
                                model.safe[rowFinal][colFinal].equals("4") ||
                                model.safe[rowFinal][colFinal].equals("0") ||
                                model.safe[rowFinal][colFinal].equals("X")){
                            model.commands.status = "Error adding laser at: (" + rowFinal + ", " + colFinal +")";
                            model.commands.announceChange();
                        }
                        model.commands.announceChange();
                    }
                });
                centerGrid.add(button, col, row);
            }
        }
        this.model.addObserver(this);
        this.model.commands.addObserver(this);
        return centerGrid;
    }

    /**
     * Helper method used to get specific button in grid
     * @param gridPane center pane of buttons
     * @param row
     * @param col
     * @return the button we want to look at
     */
    private Node getBtn(GridPane gridPane, int row, int col) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col &&
                    GridPane.getRowIndex(node) == row) {
                return (Button)node;
            }
        }
        return null;
    }

    /**
     * The initialization of gui
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {    panes(stage);    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        init(primaryStage);  // do all your UI initialization here

        primaryStage.setTitle("Lasers");
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        text.setText(this.model.commands.status);

        // update cells
        for(int row=0; row < this.model.x; row++){
            for(int col=0; col < this.model.y; col++){
                if(model.safe[row][col].equals("L")){
                    Image laserImg = new Image(getClass().getResourceAsStream("resources/laser.png"));
                    ImageView laserIcon = new ImageView(laserImg);
                    Button laser =  (Button)getBtn(this.centerGrid, row, col);
                    laser.setGraphic(laserIcon);
                    laser.setStyle("-fx-background-color: yellow");
                }
                else if(model.safe[row][col].equals("*")){
                    Image beamImg = new Image(getClass().getResourceAsStream("resources/beam.png"));
                    ImageView beamIcon = new ImageView(beamImg);
                    Button beam =  (Button)getBtn(this.centerGrid, row, col);
                    beam.setGraphic(beamIcon);
                }
                else if(model.safe[row][col].equals("0")){
                    Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar0.png"));
                    ImageView pillarIcon = new ImageView(pillarImg);
                    Button pillar =  (Button)getBtn(this.centerGrid, row, col);
                    pillar.setGraphic(pillarIcon);
                    pillar.setStyle("-fx-background-color: gray");
                }
                else if(model.safe[row][col].equals("1")){
                    Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar1.png"));
                    ImageView pillarIcon = new ImageView(pillarImg);
                    Button pillar =  (Button)getBtn(this.centerGrid, row, col);
                    pillar.setGraphic(pillarIcon);
                    pillar.setStyle("-fx-background-color: gray");
                }
                else if(model.safe[row][col].equals("2")){
                    Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar2.png"));
                    ImageView pillarIcon = new ImageView(pillarImg);
                    Button pillar =  (Button)getBtn(this.centerGrid, row, col);
                    pillar.setGraphic(pillarIcon);
                    pillar.setStyle("-fx-background-color: gray");
                }
                else if(model.safe[row][col].equals("3")){
                    Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar3.png"));
                    ImageView pillarIcon = new ImageView(pillarImg);
                    Button pillar =  (Button)getBtn(centerGrid, row, col);
                    pillar.setGraphic(pillarIcon);
                    pillar.setStyle("-fx-background-color: gray");
                }
                else if(model.safe[row][col].equals("4")){
                    Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillar4.png"));
                    ImageView pillarIcon = new ImageView(pillarImg);
                    Button pillar =  (Button)getBtn(centerGrid, row, col);
                    pillar.setGraphic(pillarIcon);
                    pillar.setStyle("-fx-background-color: gray");
                }
                else if(model.safe[row][col].equals("X")){
                    Image pillarImg = new Image(getClass().getResourceAsStream("resources/pillarX.png"));
                    ImageView pillarIcon = new ImageView(pillarImg);
                    Button pillar =  (Button)getBtn(centerGrid, row, col);
                    pillar.setGraphic(pillarIcon);
                    pillar.setStyle("-fx-background-color: gray");
                }
                else if(model.safe[row][col].equals(".")){
                    Button empty = (Button)getBtn(centerGrid, row, col);
                    empty.setGraphic(null);
                    empty.setStyle("-fx-background-color: gray");
                }
            }
        }
    }
}
