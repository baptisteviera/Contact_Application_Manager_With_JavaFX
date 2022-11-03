
package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Constants;

public class App extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/contact_list_manager.fxml"));
        Parent root = loader.load();
        String url = "file:resources/icons/address_book.png";
		stage.getIcons().add(new Image(url)); 
        stage.setTitle("Annuaire");
        stage.setScene(new Scene(root,Constants.WIDTH, Constants.HEIGHT,
				Color.BLACK));
        stage.show();
    }
}
