package dorian.controllers;

import dorian.database.SQLStatements;
import com.jfoenix.controls.*;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML
    private JFXTextField txt_username;

    @FXML
    private JFXPasswordField txt_password;

    @FXML
    private JFXPasswordField txt_conf_pass;

    @FXML
    private JFXComboBox<String> cmb_security;

    @FXML
    private JFXPasswordField txt_answer;

    @FXML
    private JFXCheckBox chb_agree;

    @FXML
    private JFXButton btn_signup;

    @FXML
    private JFXButton btn_login;

    @FXML
    private AnchorPane signup_stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setting up combo box
        cmb_security.getItems().addAll(
                "Pet Name",
                "Best Friend's Name",
                "Girl Friend's Name");
        cmb_security.setEditable(true);
    }

    @FXML
    private void signUp(){

        try {
            if (!(txt_username.getText().isEmpty() || txt_password.getText().isEmpty() || txt_conf_pass.getText().isEmpty() || cmb_security.getValue().isEmpty() || txt_answer.getText().isEmpty()) && txt_conf_pass.getText().equals(txt_password.getText())) {

                if(txt_password.getText().length() >= 8 && txt_password.getText().length() <= 25) {

                String query = "INSERT INTO credentials (Username, Password, Question, Answer) " +
                        "VALUES (?, ?, ?, ?);";

                PreparedStatement preparedStatement = SQLStatements.connection.prepareStatement(query);
                preparedStatement.setString(1, txt_username.getText());
                preparedStatement.setString(2, txt_password.getText());
                preparedStatement.setString(3, cmb_security.getValue());
                preparedStatement.setString(4, txt_answer.getText());
                preparedStatement.executeUpdate();

                TrayNotification trayNotification = new TrayNotification();
                trayNotification.setMessage("Hello " + txt_username.getText().substring(0, 1).toUpperCase() + txt_username.getText().substring(1) + ". All the best");
                trayNotification.setAnimationType(AnimationType.POPUP);
                trayNotification.setRectangleFill(Paint.valueOf("#00bcd4"));
                trayNotification.setTitle("Signed up successfully");
                trayNotification.setImage(LoginController.image);
                trayNotification.showAndDismiss(Duration.seconds(2));

                if(!trayNotification.isTrayShowing()){
                    try {
                        displayLoginView();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                }
                else {
                    TrayNotification trayNotification = new TrayNotification();
                    trayNotification.setMessage("Password characters should be between 8 and 25 characters");
                    trayNotification.setAnimationType(AnimationType.POPUP);
                    trayNotification.setRectangleFill(Paint.valueOf("#b71c1c"));
                    trayNotification.setTitle("Error Signing Up");
                    trayNotification.setImage(LoginController.error_image);
                    trayNotification.showAndDismiss(Duration.seconds(2));
                }
            }
            else {
                TrayNotification trayNotification = new TrayNotification();
                trayNotification.setMessage("Hello!!! Please kindly check the details provided again");
                trayNotification.setAnimationType(AnimationType.POPUP);
                trayNotification.setRectangleFill(Paint.valueOf("#b71c1c"));
                trayNotification.setTitle("Error Signing Up");
                trayNotification.setImage(LoginController.error_image);
                trayNotification.showAndDismiss(Duration.seconds(2));
                trayNotification.setOnDismiss(e -> {

                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void displayLoginView() throws IOException {

        // Load login scene
        Parent signup_layout = FXMLLoader.load(getClass().getResource("../views/LoginView.fxml"));
        Scene login_scene = btn_login.getScene();

        signup_layout.translateXProperty().set(login_scene.getWidth());
        signup_stage.getChildren().addAll(signup_layout);

        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(signup_layout.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), keyValue);
        timeline.getKeyFrames().addAll(keyFrame);
        timeline.play();
    }

    @FXML
    private void goAhead(){
        if(chb_agree.isSelected()){
            btn_signup.setDisable(false);
        }
        else{
            btn_signup.setDisable(true);
        }
    }

    @FXML
    private void goAhead2(KeyEvent event){
        if(event.getCode() == (KeyCode.ENTER) && chb_agree.isSelected()){
            btn_signup.setDisable(false);
        }
        else{
            btn_signup.setDisable(true);
        }
    }
}
