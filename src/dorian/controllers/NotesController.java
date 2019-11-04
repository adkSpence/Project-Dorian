package dorian.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import dorian.Main;
import dorian.database.SQLStatements;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class NotesController implements Initializable {

    @FXML
    private JFXTabPane tabpane_main;

    @FXML
    private Tab tab_encrypt;

    @FXML
    private TextArea ta_plaintext;

    @FXML
    private JFXButton btn_update;

    @FXML
    private JFXButton btn_reset;

    @FXML
    private JFXButton btn_encrypt;

    @FXML
    private JFXTextField enc_txt_entry;

    @FXML
    private Spinner<Integer> enc_secret_key;

    @FXML
    private TextArea ta_cipher_text;

    @FXML
    private JFXButton btn_edit;

    @FXML
    private JFXButton btn_delete;

    @FXML
    private JFXButton btn_dec_reset;

    @FXML
    private JFXTextField dec_txt_entry;

    @FXML
    private Spinner<Integer> dec_secret_key;

    @FXML
    private JFXCheckBox cb_allow;

    @FXML
    private TextArea ta_dec_plaintext;

    @FXML
    private JFXButton btn_logout;

    private byte[] secret_key, coded_text, raw_key;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SQLStatements sqlStatements = new SQLStatements();
        Blowfish.generateSymmetricKey();

        // Initializing spinner values for secret key on both encryption and decryption tabs
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99999);
        SpinnerValueFactory<Integer> dec_valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99999);
        enc_secret_key.setValueFactory(valueFactory);
        dec_secret_key.setValueFactory(dec_valueFactory);

        String table_query = "CREATE TABLE IF NOT EXISTS Entries (\n" +
                " Username String NOT NULL, \n" +
                " Entry_Title String NOT NULL, \n" +
                " Secret_Key BLOB NOT NULL, \n" +
                " Raw_Key BLOB NOT NULL, \n" +
                " Encrypted_Message BLOB NOT NULL);";

        // Creates the Credentials Table
        try{
            PreparedStatement preparedStatement;
            try{
                preparedStatement = SQLStatements.connection.prepareStatement(table_query);
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
    void encryptPlainText() throws Exception {

        // Inserting into the Entries table
        if(!(enc_txt_entry.getText().isEmpty() || ta_plaintext.getText().isEmpty())){

            // Encrypting plain text of message title, secret key and message itself
            coded_text = Blowfish.encryptMessage(ta_plaintext.getText());
            secret_key = Blowfish.encryptMessage(String.valueOf(enc_secret_key.getValue()));
            raw_key = Blowfish.getRaw();

            ta_cipher_text.setText(String.valueOf(coded_text));

            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Entry text content and secret key encrypted!");
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#00e676"));
            trayNotification.setTitle("Encrypted Successfully");
            //trayNotification.setImage(image);
            trayNotification.showAndDismiss(Duration.seconds(3));

            try{
                PreparedStatement preparedStatement = null;

                String query = "INSERT INTO Entries (Username, Entry_Title, Secret_Key, Raw_Key, Encrypted_Message)" +
                        " " +
                        "VALUES (?, ?, ?, ?, ?);";

                preparedStatement = SQLStatements.connection.prepareStatement(query);
                preparedStatement.setString(1, LoginController.username);
                preparedStatement.setString(2, enc_txt_entry.getText().toLowerCase());
                preparedStatement.setBytes(3, secret_key);
                preparedStatement.setBytes(4, raw_key);
                preparedStatement.setBytes(5, coded_text);
                //preparedStatement.setInt(5, Integer.valueOf(shared_secret));

                preparedStatement.executeUpdate();
            }
            catch (Exception e){ }
        }

        else {
            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Entry content could not be encrypted. Fill in input fields");
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#d50000"));
            trayNotification.setTitle("Error Encrypting...");
            trayNotification.setImage(LoginController.error_image);
            trayNotification.showAndDismiss(Duration.seconds(3));
        }
    }

    @FXML
    void allowModification() {
        if(cb_allow.isSelected()){
            btn_edit.setDisable(false);
            btn_delete.setDisable(false);
        }
        else{
            btn_delete.setDisable(true);
            btn_edit.setDisable(true);
        }
    }

    @FXML
    void allowModification2(KeyEvent event) {

        if(event.getCode() == (KeyCode.ENTER) && cb_allow.isSelected()){
            btn_edit.setDisable(false);
            btn_delete.setDisable(false);
        }
        else{
            btn_edit.setDisable(true);
            btn_delete.setDisable(true);
        }
    }

    @FXML
    void decryptCodedText() {

        if(!(dec_txt_entry.getText().isEmpty())) {
            // Selecting cipher text based on entry title
            try {
                String query = "SELECT * FROM Entries WHERE Username=? AND Entry_Title=?";
                PreparedStatement preparedStatement = SQLStatements.connection.prepareStatement(query);
                preparedStatement.setString(1, LoginController.username);
                preparedStatement.setString(2, dec_txt_entry.getText().toLowerCase());

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {

                    // Saving encrypted message and secret key to variables coded_text & secret_key as bytes
                    coded_text = resultSet.getBytes("Encrypted_Message");
                    secret_key = resultSet.getBytes("Secret_Key");
                    raw_key = resultSet.getBytes("Raw_Key");

                /*
                Need to decrypt secret key first then
                convert to integer and compare to see if keys are right before decrypting
                */
                    String decoded_key = Blowfish.decryptMessage(raw_key, secret_key);

                    if (Integer.valueOf(decoded_key).equals(dec_secret_key.getValue())) {
                        // Retrieve encrypted message as bytes from the database
                        String decoded_message = Blowfish.decryptMessage(raw_key, coded_text);

                        ta_dec_plaintext.setText(decoded_message);
                        cb_allow.setDisable(false);

                        TrayNotification trayNotification = new TrayNotification();
                        trayNotification.setMessage("Plaintext restored!");
                        trayNotification.setAnimationType(AnimationType.POPUP);
                        trayNotification.setRectangleFill(Paint.valueOf("#00e676"));
                        trayNotification.setTitle("Decrypted Successfully");
                        //trayNotification.setImage(dec_image);
                        trayNotification.showAndDismiss(Duration.seconds(3));
                    }

                }

                preparedStatement.close();
                resultSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void deleteEntry() {
        try {
            String query = "DELETE FROM Entries WHERE Username = ? AND (Entry_Title = ? AND Secret_Key = ?)";
            PreparedStatement preparedStatement = SQLStatements.connection.prepareStatement(query);
            preparedStatement.setString(1, LoginController.username);
            preparedStatement.setString(2, dec_txt_entry.getText().toLowerCase());
            preparedStatement.setBytes(3, secret_key);

            preparedStatement.executeUpdate();

            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Entry deleted. It can not be retrieved!!!");
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#37474f"));
            trayNotification.setTitle("Delete Successful");
            //trayNotification.setImage(delete_image);
            trayNotification.showAndDismiss(Duration.seconds(3));

            reset2();
        }
        catch (Exception e) {

        }
    }

    @FXML
    void editMessage() {
        /*
        Function called when edit button on Decrypt Tab is pressed
        Tab pane's focus is shifted to Encrypt Tab
         */
        //ta_plaintext.setText(decoded_message);
        enc_txt_entry.setText("");
        tabpane_main.getSelectionModel().select(tab_encrypt);
        enc_txt_entry.setText(dec_txt_entry.getText());
        enc_secret_key.getValueFactory().setValue(dec_secret_key.getValue());

        // After making editable, disable secret key and entry title fields
        enc_txt_entry.setEditable(false);
        enc_secret_key.setDisable(true);
        btn_encrypt.setDisable(true);
        btn_update.setDisable(false);
        btn_reset.setDisable(false);

        // Resetting Decrypt Message Tab back to normal
        reset2();
    }

    @FXML
    private void reset(){
        enc_secret_key.getValueFactory().setValue(1);
        enc_secret_key.setDisable(false);
        enc_secret_key.setEditable(true);
        enc_txt_entry.setText("");
        enc_txt_entry.setEditable(true);
        ta_plaintext.setText("");
        ta_cipher_text.setText("");

        btn_encrypt.setDisable(false);
        btn_update.setDisable(true);
        btn_reset.setDisable(true);
    }

    @FXML
    private void reset2(){
        dec_txt_entry.setText("");
        dec_secret_key.getValueFactory().setValue(1);
        ta_dec_plaintext.setText("");
        cb_allow.setSelected(false);
        cb_allow.setDisable(true);

        if(!(cb_allow.isSelected())) {
            btn_edit.setDisable(true);
            btn_delete.setDisable(true);
        }
    }

    @FXML
    private void signOut() {
        try{
            Main.displaySignOut();
            Main.note_stage.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void updateEntry() {

    }

}
