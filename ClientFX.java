import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientFX extends Application {

    private String ip;
    private Integer port;

    private Stage myStage;
    private Client conn;
    private TextArea messages = new TextArea();
    private Label warnLabel;
    private Button rock, paper, scissors, lizard, spock;
    private Label waitOther;
    private Label pointInfo;

    private Button startNew;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.myStage = primaryStage;
        connectScene();

    }


    @Override
    public void stop() throws Exception{
        conn.closeConn();
    }

    private Client createClient() {
        return new Client(this.ip, this.port,
                data -> {                                       // this is callback for changing textfield
                    Platform.runLater(()->{ messages.appendText(data.toString() + "\n"); }); },
                data -> {                                       // this is callback for changing other GUI elements, the control callback
                    Platform.runLater(()->{
                        String s = data.toString();
                        if(s.contains("#playerNum2")){
                            gameScene();
                        }
                        if(s.contains("#stopWait")){
                            enableButton();
                        }
                        if(s.contains("#gameOver")){
                            disableButton();
                            this.startNew.setDisable(false);
                        }
                        if(s.contains("#point:")){              // refresh the points of each player
                            s = s.replace("#point:","");    // get rid of useless part of this string
                            this.pointInfo.setText(s);
                        }
                    });
                }

                );
    }


    private void connectScene(){

        Label ipLabel = new Label("IP  :");
        Label portLabel = new Label("Port:");
        TextField ipTextField = new TextField("127.0.0.1");
        TextField portTextField = new TextField("5555");
        Button startConnect = new Button("Connect!");
        this.warnLabel = new Label();

        startConnect.setOnAction(event -> {
            if((ipTextField.getText()=="")||(portTextField.getText()=="")){
                warnLabel.setText("Please input valid IP and Port!");
                return;
            }

            this.ip = ipTextField.getText();
            this.port = Integer.valueOf(portTextField.getText());
            waitScene();                                                 // switch scene
        });

        GridPane connectGrid = new GridPane();
        connectGrid.setPadding(new Insets(70));
        connectGrid.add(ipLabel, 0, 0);
        connectGrid.add(ipTextField, 1, 0);
        connectGrid.add(portLabel, 0, 1);
        connectGrid.add(portTextField, 1,1);

        connectGrid.add(startConnect, 0, 2);
        connectGrid.add(warnLabel, 1,2);

        Scene scene = new Scene(connectGrid, 400, 200);
        this.myStage.setScene(scene);
        this.myStage.setTitle("Rock, Paper, Scissors, Lizard, Spock");
        this.myStage.show();

    }

    private void waitScene(){
        this.conn = createClient();
        try {this.conn.startConn();}
        catch (Exception e){

        }
        Label lb = new Label("waiting for others to connect...");
        VBox vb = new VBox(lb);
        vb.setPadding(new Insets(50));
        Scene scene = new Scene(vb, 400, 200);

        this.myStage.setScene(scene);
        this.myStage.setTitle("Rock, Paper, Scissors, Lizard, Spock");
        this.myStage.show();
    }

    private void gameScene(){

        this.waitOther = new Label("");
        this.pointInfo = new Label("");

        Image rockImg = new Image("/img/rock.png");
        ImageView rockView = new ImageView(rockImg);
        rockView.setFitHeight(30);
        rockView.setFitWidth(30);
        rockView.setPreserveRatio(true);

        Image paperImg = new Image("/img/paper.png");
        ImageView paperView = new ImageView(paperImg);
        paperView.setFitWidth(30);
        paperView.setFitHeight(30);
        paperView.setPreserveRatio(true);

        Image scissorsImg = new Image("/img/scissors.png");
        ImageView scissorsView = new ImageView(scissorsImg);
        scissorsView.setFitWidth(30);
        scissorsView.setFitHeight(30);
        scissorsView.setPreserveRatio(true);

        Image lizardImg = new Image("/img/lizard.png");
        ImageView lizardView = new ImageView(lizardImg);
        lizardView.setFitWidth(30);
        lizardView.setFitHeight(30);
        lizardView.setPreserveRatio(true);

        Image spockImg = new Image("/img/spock.png");
        ImageView spockView = new ImageView(spockImg);
        spockView.setFitWidth(30);
        spockView.setFitHeight(30);
        spockView.setPreserveRatio(true);


        this.rock = new Button();
        this.rock.setGraphic(rockView);
        this.rock.setOnAction(event -> playButtonFunc(0));

        this.paper = new Button();
        this.paper.setGraphic(paperView);
        this.paper.setOnAction(event -> playButtonFunc(1));

        this.scissors = new Button();
        this.scissors.setGraphic(scissorsView);
        this.scissors.setOnAction(event -> playButtonFunc(2));

        this.lizard = new Button();
        this.lizard.setGraphic(lizardView);
        this.lizard.setOnAction(event -> playButtonFunc(3));

        this.spock = new Button();
        this.spock.setGraphic(spockView);
        this.spock.setOnAction(event -> playButtonFunc(4));

        HBox hb1 = new HBox(5, rock, paper, scissors, lizard, spock);


        this.startNew = new Button("New Game");
        this.startNew.setDisable(true);
        this.startNew.setOnAction(event -> {
            this.startNew.setDisable(true);

            try {
                conn.send("#startNewGame");
            }
            catch(Exception e) {

                System.out.println(e);
            }
            this.messages.clear();

        });

        Button closeBtn = new Button("Quit!");
        closeBtn.setOnAction(event -> {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            stage.close();
        });

        HBox hb2= new HBox(20, this.startNew, closeBtn);

        messages.setPrefHeight(300);
        VBox root = new VBox(20,messages, hb1, waitOther, pointInfo, hb2);
        root.setPrefSize(350, 450);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root);
        this.myStage.setScene(scene);
        this.myStage.setTitle("Rock, Paper, Scissors, Lizard, Spock");
        this.myStage.show();
    }

    private void disableButton(){
        this.waitOther.setText("wait other player to play");

        this.rock.setDisable(true);
        this.paper.setDisable(true);
        this.scissors.setDisable(true);
        this.lizard.setDisable(true);
        this.spock.setDisable(true);
    }

    private void enableButton(){
        this.waitOther.setText("");

        this.rock.setDisable(false);
        this.paper.setDisable(false);
        this.scissors.setDisable(false);
        this.lizard.setDisable(false);
        this.spock.setDisable(false);
    }

    private void playButtonFunc(Integer index){
        ArrayList<String> playMap = new ArrayList<>(Arrays.asList("rock", "paper", "scissors", "lizard", "spock"));
        String message = playMap.get(index);
        this.disableButton();

        try {
            conn.send(message);
        }
        catch(Exception e) {

            System.out.println(e);
        }

    }


}
