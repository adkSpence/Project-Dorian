package dorian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    // View objects **************** //

    static Stage main_stage;
    static StackPane splash_layout;
    static Scene splash_scene;

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
}
