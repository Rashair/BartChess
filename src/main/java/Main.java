import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.menu.MenuViewController;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    private final int windowWidth = 1600;
    private final int windowHeight = 900;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        var menu = loadMenu();
        if (menu != null) {
            var scene = new Scene(menu, windowWidth, windowHeight);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }

    private Parent loadMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("./view/menu/menuView.fxml")));
            Parent root = loader.load();
            MenuViewController controller = loader.getController();

            return root;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
