import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;

/**
 *JavaFX implementation class to view Minesweeper game.
 *@author tbatheja3
 *@version 1.0
 */
public class MinesweeperView extends Application {
        private Difficulty chosenDifficulty;
        private String playerName;
        private boolean debugFlag = false;

        @Override
        public void start(Stage primaryStage) {
                Scene currentScene = startGameScene(primaryStage);
                primaryStage.setTitle("Minesweeper");
                primaryStage.setScene(currentScene);
                primaryStage.show();
        }
        /**
         *Scene represents the initial game screen.
         *@param stage represents the primary stage for scene
         *@return intial game scene
         */
        public Scene startGameScene(Stage stage) {
                BorderPane pane = new BorderPane();

                //Heading
                Text heading = new Text("Let's play Minesweeper!");
                heading.setStyle("-fx-text-fill: blue;"//Color not working
                        + "-fx-font: Courier New;"
                        + "-fx-font-family: Courier New;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 30;");


                //First field
                ComboBox difficulty = new ComboBox();
                difficulty.setPromptText("Choose Difficulty");
                difficulty.getItems().addAll("Easy", "Medium", "Hard");

                //Second Field
                TextField name = new TextField();
                name.setPromptText("Enter Name");
                name.setMaxWidth(150);

                //Third Field
                Button startButton = new Button("Start the Game");

                //Functionality
                startButton.setOnAction(e -> {
                        playerName = name.getText();
                        if ((name.getText() != null)
                                && (!name.getText().isEmpty())
                                && difficulty.getValue() != null
                                && (!difficulty.getValue().toString().isEmpty())) {
                                switch (difficulty.getValue().toString()) {
                                        case "Easy":
                                                chosenDifficulty = Difficulty.EASY;
                                                break;
                                        case "Medium":
                                                chosenDifficulty = Difficulty.MEDIUM;
                                                break;
                                        case "Hard":
                                                chosenDifficulty = Difficulty.HARD;
                                                break;
                                        default:
                                                break;
                                }
                                stage.setScene(playGameScene(stage));
//                                Scene next = playGameScene(stage);
//                                return next;
                        } else {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setTitle("Invalid Input");
                                a.setHeaderText(null);
                                a.setContentText("Both fields are required to proceed");
                                a.showAndWait();
                        }
                });

                VBox vbox = new VBox();
                VBox vbox2 = new VBox();
                vbox.setAlignment(Pos.CENTER);
                vbox2.setAlignment(Pos.CENTER);
                vbox.setSpacing(20);
                vbox2.setPadding(new Insets(50, 50, 50, 50));
                vbox.getChildren().addAll(difficulty, name, startButton); // add here
                vbox2.getChildren().add(heading);
                pane.setTop(vbox2);
                pane.setCenter(vbox);
                return new Scene(pane, 565, 450); // Maybe add dimensions check API
        }
        /**
         *Scene represents the play game screen.
         *@param stage represents the primary stage for scene
         *@return play game scene
         */
        public Scene playGameScene(Stage stage) {

                BorderPane pane = new BorderPane();

                //Setting up control Box
                VBox controlBox = new VBox();
                controlBox.setPadding(new Insets(20, 5, 5, 5));
                controlBox.setStyle("-fx-border-width: 2px; -fx-border-color: red");
                RadioButton flagMode = new RadioButton("Flag Mode");
                RadioButton normalMode = new RadioButton("Normal Mode");
                normalMode.setSelected(true);
                ToggleGroup group = new ToggleGroup();
                flagMode.setToggleGroup(group);
                normalMode.setToggleGroup(group);
                controlBox.getChildren().addAll(flagMode, normalMode);

                GridPane gp = new GridPane();
                MinesweeperGame game = new MinesweeperGame(chosenDifficulty);
                Button[][] buttonTile = new Button[15][15]; //Generic Button type because two diff buttons
                for (int i = 0; i <= 14; i++) {
                        for (int j = 0; j <= 14; j++) {
//                                buttonTile[i][j] = new Button();
                                buttonTile[i][j] = new BoardButton();
                                BoardButton myButton = (BoardButton) buttonTile[i][j]; //final
                                gp.add(myButton, i, j);
                                myButton.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent actionEvent) {
                                                if (flagMode.isSelected()) {
                                                        myButton.setStyle("-fx-border-width: 2; -fx-highlight-fill: 2; -fx-border-color: red");
                                                        //gp.add(new FlagButton(), m, n); // or maybe direct

                                                } else {
                                                        Tile[] check = game.check(GridPane.getRowIndex(myButton),
                                                                GridPane.getColumnIndex(myButton));
                                                        //DEBUG - check is returning null
                                                        if (debugFlag) {
                                                                game.printBoard();
                                                                game.printBoardVisible();
                                                                System.out.println(myButton);
                                                                System.out.println(check);
                                                                System.out.println(GridPane.getColumnIndex(myButton)
                                                                        + " " +  GridPane.getRowIndex(myButton));
                                                        }
                                                        if (game.isWon()) {
                                                                stage.setScene(wonGameScene(stage));
                                                        }
                                                        if (check == null) {
                                                                return;
                                                        } else if (check.length == 1 && check[0].isMine()) {
                                                                stage.setScene(endGameScene(stage));
                                                        } else {
                                                                for (Tile t: check) {
                                                                        //buttonTile[t.getX()][t.getY()] = new TileButton(t);
                                                                        gp.add(new TileButton(t), t.getX(), t.getY());
                                                                }
                                                        }
                                                }
                                        }
                                });

                        }
                }


                //Adding panes to scene
                pane.setCenter(gp);
                pane.setRight(controlBox);
                return new Scene(pane, 565, 450);
        }
        /**
         *Scene represents the lost game screen.
         *@param stage represents the primary stage for scene
         *@return lost game scene
         */
        public Scene endGameScene(Stage stage) {
                BorderPane bp = new BorderPane();

                //First field
                Text lost = new Text("You lost," + " " + playerName);
                lost.setStyle("-fx-text-fill: blue;"//COLOR NOT WORKING - Office Hours
                        + "-fx-font: Courier New;"
                        + "-fx-font-family: Courier New;"
                        + "-fx-font-size: 30;");

                //Second
                Button newGame = new Button("New Game");

                newGame.setOnAction(new ButtonHandler(stage));
                VBox vbox = new VBox();
                vbox.setAlignment(Pos.CENTER);
                vbox.setSpacing(15);
                vbox.getChildren().addAll(lost, newGame); // add here
                bp.setCenter(vbox);
                return new Scene(bp, 565, 450);

        }
        /**
         *Scene represents the won game screen.
         *@param stage represents the primary stage for scene
         *@return won game scene
         */
        public Scene wonGameScene(Stage stage) {
                BorderPane bp1 = new BorderPane();

                //First field
                Text won = new Text("Congratulations," + " " + playerName);
                won.setStyle("-fx-text-fill: blue;"//COLOR NOT WORKING - Office Hours
                        + "-fx-font: Courier New;"
                        + "-fx-font-family: Courier New;"
                        + "-fx-font-size: 30;");

                //Second
                Button newGame = new Button("New Game");

                newGame.setOnAction(new ButtonHandler(stage));
                VBox vbox = new VBox();
                vbox.setAlignment(Pos.CENTER);
                vbox.setSpacing(15);
                vbox.getChildren().addAll(won, newGame); // add here
                bp1.setCenter(vbox);
                return new Scene(bp1, 565, 450);

        }

        private class ButtonHandler implements EventHandler<ActionEvent> {
                private Stage stage;
                ButtonHandler(Stage stage) {
                        this.stage = stage;
                }
                @Override
                public void handle(ActionEvent actionEvent) {
                        stage.setScene(startGameScene(stage));
                }
        }
}

class TileButton extends Button {
        //Constructor
        TileButton(Tile t) {
                super();
                super.setPrefSize(30, 30);
                super.setText(Integer.toString(t.getBorderingMines()));
                switch (super.getText()) {
                        case "0":
                                super.setStyle("-fx-text-fill: black;" + "-fx-background-color: white;" + "-fx-font-weight: bold;");
                                break;
                        case "1":
                                super.setStyle("-fx-text-fill: black;" + "-fx-background-color: #d5d56b;" + "-fx-font-weight: bold;");
//                                super.setStyle("-fx-text-fill: black");// Only one of them works
//                                super.setStyle("-fx-background-color: green");
                                break;
                        case "2":
                                super.setStyle("-fx-text-fill: #070707;" + "-fx-background-color: #5ce25c;" + "-fx-font-weight: bold;");
                                break;
                        case "3":
                                super.setStyle("-fx-text-fill: #090808;" + "-fx-background-color: #77d0f6;" + "-fx-font-weight: bold;");
                                break;
                        case "4":
                                super.setStyle("-fx-text-fill: #090808;" + "-fx-background-color: #dc3e42;" + "-fx-font-weight: bold;");
                                break;
                        default:
                                break;
                }
        }
}

class BoardButton extends Button {
        BoardButton() {
                super();
                super.setPrefSize(30, 30);
                DropShadow shadow = new DropShadow();
                //Adding the shadow when the mouse cursor is on
                super.addEventHandler(MouseEvent.MOUSE_ENTERED,
                        new EventHandler<MouseEvent>() {
                                @Override public void handle(MouseEvent e) {
                                        BoardButton.super.setEffect(shadow); //
                                }
                        });
                //Removing the shadow when the mouse cursor is off
                super.addEventHandler(MouseEvent.MOUSE_EXITED,
                        new EventHandler<MouseEvent>() {
                                @Override public void handle(MouseEvent e) {
  //                                      super.setEffect(null); // Why did not work? OFFICE HOURS
                                        BoardButton.super.setEffect(null); //Is it because inside EventHandler, super does not exist?

                                }
                        }
                );
        }
}
