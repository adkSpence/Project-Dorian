package dorian.controllers;

import dorian.Main;
import dorian.database.SQLStatements;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.sql.PreparedStatement;

public class LoginController {

    double x, y = 0;

    @FXML
    private JFXTextField txt_username;

    @FXML
    private JFXTextField txt_reset_username;

    @FXML
    private JFXPasswordField txt_reset_answer;

    @FXML
    private JFXTextField txt_reset_question;

    @FXML
    private AnchorPane login_stage;

    @FXML
    private ProgressIndicator pi_loggedin;

    @FXML
    private JFXPasswordField txt_password;

    @FXML
    private JFXButton btn_reset_password;

    @FXML
    private JFXPasswordField txt_reset_password;

    @FXML
    private JFXPasswordField txt_confirm_reset;

    @FXML
    private JFXButton btn_update;

    @FXML
    private JFXButton btn_val_user;

    @FXML
    private JFXButton btn_sign_up;

    @FXML
    private JFXButton btn_reset;

    static Image image = new Image("dorian/views/assets/checked.png");
    static Image error_image = new Image("dorian/views/assets/error.png");

    public static String username;

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
    private void isTyping(){
        pi_loggedin.setProgress(-1.0f);
        pi_loggedin.setVisible(true);
    }

    @FXML
    private void displaySignupView() throws IOException {

        // Load login scene
        Parent signup_layout = FXMLLoader.load(getClass().getResource("../views/SignupView.fxml"));
        Scene login_scene = btn_sign_up.getScene();

        signup_layout.translateXProperty().set(login_scene.getWidth());
        login_stage.getChildren().addAll(signup_layout);

        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(signup_layout.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), keyValue);
        timeline.getKeyFrames().addAll(keyFrame);
        timeline.play();
    }

    @FXML
    private void displayResetView() throws Exception {
        Parent reset_layout = FXMLLoader.load(getClass().getResource("../views/ForgotPassword.fxml"));
        Scene login_scene = btn_reset_password.getScene();

        reset_layout.translateXProperty().set(login_scene.getWidth());
        login_stage.getChildren().addAll(reset_layout);

        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(reset_layout.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), keyValue);
        timeline.getKeyFrames().addAll(keyFrame);
        timeline.play();
    }

    @FXML
    private void validateLogin() {
        if(SQLStatements.loginDetails(txt_username.getText(), txt_password.getText())){

            username = txt_username.getText();

            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Hello " + txt_username.getText().substring(0, 1).toUpperCase() + txt_username.getText().substring(1) + ". Make hay while the sun shines");
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#00bcd4"));
            trayNotification.setTitle("Logged in successfully");
            trayNotification.setImage(image);
            trayNotification.showAndDismiss(Duration.seconds(2));

            if(!trayNotification.isTrayShowing()){
                try {
                    Main.displayNotesPage();
                    Stage stage = (Stage) login_stage.getScene().getWindow();
                    stage.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }


            pi_loggedin.setProgress(1.0f);
            pi_loggedin.setVisible(true);
        }
        else{
            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Hello!!! Please check the details again");
            trayNotification.setImage(error_image);
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#b71c1c"));
            trayNotification.setTitle("Error Logging In");
            trayNotification.showAndDismiss(Duration.seconds(5));
            pi_loggedin.setVisible(false);
        }
    }

    @FXML
    private void resetPassword(){

        if(txt_reset_answer.getText().equals(SQLStatements.answer));
        txt_reset_password.setVisible(true);
        txt_confirm_reset.setVisible(true);

        btn_reset.setDisable(true);
        btn_update.setDisable(false);
    }

    @FXML
    private void validateUser() {
        if(SQLStatements.verifyUser(txt_reset_username.getText())){

            txt_reset_question.setText(SQLStatements.question);
            System.out.println();
            txt_reset_answer.setVisible(true);
            btn_val_user.setDisable(true);
            btn_reset.setDisable(false);

        }
        else{
            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("There is no username of the sort");
            trayNotification.setImage(error_image);
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#b71c1c"));
            trayNotification.setTitle("Error Retrieving User Data");
            trayNotification.showAndDismiss(Duration.seconds(5));
        }
    }

    @FXML
    private void updatePassword() {
        if(txt_reset_password.getText().length() >= 8 && txt_confirm_reset.getText().length() <= 25) {

            String query = "UPDATE credentials SET password = ? WHERE username = ?";

            try{
                PreparedStatement preparedStatement = SQLStatements.connection.prepareStatement(query);
                preparedStatement.setString(1, txt_reset_password.getText());
                preparedStatement.setString(2, txt_reset_username.getText());
                preparedStatement.executeUpdate();

                TrayNotification trayNotification = new TrayNotification();
                trayNotification.setMessage("Reset Done. New Password Created");
                trayNotification.setAnimationType(AnimationType.POPUP);
                trayNotification.setRectangleFill(Paint.valueOf("#00bcd4"));
                trayNotification.setTitle("Signed up successfully");
                trayNotification.setImage(LoginController.image);
                trayNotification.showAndDismiss(Duration.seconds(2));

                if(!trayNotification.isTrayShowing()) {
                    try {
                        Parent reset_layout = FXMLLoader.load(getClass().getResource("../views/LoginView.fxml"));
                        Scene login_scene = btn_update.getScene();

                        reset_layout.translateXProperty().set(login_scene.getWidth());
                        login_stage.getChildren().addAll(reset_layout);

                        Timeline timeline = new Timeline();
                        KeyValue keyValue = new KeyValue(reset_layout.translateXProperty(), 0, Interpolator.EASE_IN);
                        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), keyValue);
                        timeline.getKeyFrames().addAll(keyFrame);
                        timeline.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            catch (Exception ex){
                ex.printStackTrace();
            }

        }
    }
}
