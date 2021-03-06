package dorian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    // View objects **************** //

    public static Stage main_stage, note_stage;
    static StackPane splash_layout;
    public static Scene splash_scene, note_scene;
    static BorderPane note_layout;

    // ***************************** //

    @Override
    public void start(Stage primaryStage) throws Exception {
        main_stage = primaryStage;
        displaySplashScreen();
        main_stage.show();
    }

    public static void displaySplashScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/SplashScreen.fxml"));
        splash_layout = loader.load();
        splash_scene = new Scene(splash_layout);
        main_stage.setScene(splash_scene);
        main_stage.initStyle(StageStyle.TRANSPARENT);
        main_stage.show();
    }

    public static void displayNotesPage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/Notes.fxml"));
        note_layout = loader.load();
        note_scene = new Scene(note_layout);
        note_stage = new Stage();
        note_stage.setScene(note_scene);
        note_stage.setMaximized(true);
        note_stage.setTitle("Project Kofi-Hey");
        note_stage.show();
        main_stage.close();
    }

    public static void displaySignOut() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/SplashScreen.fxml"));
        splash_layout = loader.load();
        splash_scene = new Scene(splash_layout);
        Stage signout_stage = new Stage();
        signout_stage.setScene(splash_scene);
        signout_stage.initStyle(StageStyle.TRANSPARENT);
        signout_stage.show();
    }
}
