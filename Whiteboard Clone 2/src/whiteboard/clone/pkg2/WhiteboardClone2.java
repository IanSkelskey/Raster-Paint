package whiteboard.clone.pkg2;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Ian A Skelskey
 *
 * Raster Paint Application Version 1.0
 *
 * I created this as a preliminary exercise for making a vector-based graphical
 * whiteboard application for education.
 *
 */
public class WhiteboardClone2 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("WhiteboardFXML.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Whiteboard Clone");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
