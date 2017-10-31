    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nb.dogp.filefixer;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jonas
 */
public class FileFixer extends Application {

  public static Preferences prefs = Preferences.systemNodeForPackage(FileFixer.class);

  private Stage stage;

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(
      getClass().getResource("FXMLDocument.fxml")
    );
    Parent root = loader.load();
    FXMLDocumentController c = (FXMLDocumentController) loader.getController();
    c.setStage(stage);
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    this.stage = stage;
    Runtime.getRuntime().addShutdownHook(
      new Thread(
        new Runnable() {
          @Override
          public void run() {
            try {
              // Save preferences before exiting
              prefs.flush();
            } catch (BackingStoreException ex) {
              Logger.getLogger(FileFixer.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
      )
    );

  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * @return the stage
   */
  public Stage getStage() {
    return stage;
  }
}
