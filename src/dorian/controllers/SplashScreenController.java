package dorian.controllers;

import dorian.database.SQLStatements;
import com.jfoenix.controls.JFXButton;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {

    double x, y = 0;

    @FXML
    StackPane splash_stage;

    @FXML
    private JFXButton btn_start;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SQLStatements sqlStatements = new SQLStatements();

        String table_query = "CREATE TABLE IF NOT EXISTS Credentials (\n" +
                " Username String(100) primary key, \n" +
                " Password String(50) NOT NULL, \n" +
                " Question String(50) NOT NULL, \n" +
                " Answer String(50) NOT NULL);";

        // Creates the Credentials Table
        try{
            try{
                PreparedStatement preparedStatement = SQLStatements.connection.prepareStatement(table_query);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void pressed(MouseEvent event){
        x = event.getX();
        y = event.getY();
    }

    @FXML
    private void dragged(MouseEvent event){
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    public void displayLoginView() throws IOException {

        // Load login scene
        Parent root = FXMLLoader.load(getClass().getResource("../views/LoginView.fxml"));
        Scene login_scene = btn_start.getScene();

        root.translateYProperty().set(login_scene.getHeight());
        splash_stage.getChildren().addAll(root);

        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), keyValue);
        timeline.getKeyFrames().addAll(keyFrame);
        timeline.play();
    }
}