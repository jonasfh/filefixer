/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nb.dogp.filefixer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JMenuItem;
import nb.dogp.filefixer.filehandler.ExcelSheet;
import nb.dogp.filefixer.filehandler.Exportable;

/**
 *
 * @author jonas
 */
public class FXMLDocumentController implements Initializable  {
    
  @FXML
  private JMenuItem fileOpenMenu;
  @FXML
  private GridPane dataGrid;
  @FXML
  private TextField exportTitle;
  @FXML
  private ComboBox outputFormatCombo;
  @FXML
  private ComboBox exportSetupsCombo;
  @FXML
  private Stage stage;
  @FXML
  private TextArea startRowScript;
  @FXML
  private TextArea endRowScript;
  @FXML 
  private TextField startRowIndex;

  private FileChooser fileChooser= new FileChooser();
  private Exportable exportFile;
  private LinkedHashMap<String, ExportSetup> exportSetups = new LinkedHashMap<>();
  private File exportSetupFile = new File("export.setup");
  private HashMap<String, TextInputControl>[] columnSetups;
  

  @FXML
  private void handleFileImport(ActionEvent event) {
    fileChooser.setTitle("Find file to fix");
    File file = fileChooser.showOpenDialog(this.stage);
    if (file != null) {
      FileFixer.prefs.put("folder", file.getParentFile().getAbsolutePath());
      if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
        this.exportFile = new ExcelSheet(file, 0);
      }
    }
    if (this.exportFile != null) {
      display(this.exportFile);
    }
  }
  
  @FXML
  private void saveCurrentExportSetup(ActionEvent event) {
    if(columnSetups == null) return;
    String name = exportSetupsCombo.getValue().toString();
    ExportSetup es = new ExportSetup(
      Integer.parseInt(startRowIndex.getText()),
      name
    );
    es.setStartExportScript(startRowScript.getText());
    es.setEndExportScript(endRowScript.getText());
    
    for (int i = 0; i < columnSetups.length; i++) {
      if (columnSetups[i].get("column_order").getText() == "") {
        continue;
      }
      es.addColumnSetup(
        i, 
        Integer.parseInt(columnSetups[i].get("column_order").getText()),
        columnSetups[i].get("column_label").getText(),
        columnSetups[i].get("column_script").getText()
      );
    }
//    exportSetupsCombo.getSelectedIn
  }
  @FXML
  private void saveExportSetups(ActionEvent event) {

    try {
       ObjectOutputStream out = new ObjectOutputStream(
        new FileOutputStream(exportSetupFile)
       );
       out.writeObject(exportSetups);
       out.close();
       System.out.println(exportSetupFile.getAbsolutePath());
    } catch (IOException i) {
       i.printStackTrace();
    }  
  }
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Init filechooser with default folder
    String folder = FileFixer.prefs.get("folder", "");
    if (!folder.equals("")) {
      fileChooser.setInitialDirectory(new File(folder));
    }
    outputFormatCombo.setItems(
      FXCollections.observableArrayList(
        "JSON - format"
      )
    );
    if (exportSetupFile.exists()) {
      try {
        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(exportSetupFile));
        exportSetups = (LinkedHashMap<String, ExportSetup>) oin.readObject();
        oin.close();
      } catch (FileNotFoundException ex) {
        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ClassNotFoundException ex) {
        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    Runtime.getRuntime().addShutdownHook(
      new Thread(
        new Runnable() {
          public void run() {
            saveExportSetups(null);
          }
        }
      )
    );
  }

  void setStage(Stage stage) {
    this.stage = stage;
  }

  private void display(Exportable exportFile) {
    int cols = exportFile.columnCount();
    int toprow = 0;
    dataGrid.setGridLinesVisible(true);
    dataGrid.setHgap(5);
    dataGrid.setVgap(5);
    // set grid size constraints
    ColumnConstraints cc = new ColumnConstraints (60);
    dataGrid.getColumnConstraints().add(cc);
    for (int i = 0; i < cols + 1; i++) {
      cc = new ColumnConstraints (130);
      dataGrid.getColumnConstraints().add(cc);
    }
    Label l = null;

    columnSetups = new HashMap[cols + 1];
    for (int i = 0; i < cols + 1; i++) {
      columnSetups[i] = new HashMap<>();
      l = new Label("Column order:");
      dataGrid.add(l, i + 1, toprow);
      TextField tf = new TextField();
      dataGrid.add(tf, i + 1, toprow + 1);
      columnSetups[i].put("column_order", tf);
      l = new Label("Column label:");
      dataGrid.add(l, i + 1, toprow + 2);
      tf = new TextField();
      dataGrid.add(tf, i + 1, toprow + 3);
      columnSetups[i].put("column_label", tf);
      l = new Label("Column script:");
      dataGrid.add(l, i + 1, toprow + 4);
      TextArea ta = new TextArea();
      ta.setWrapText(true);
      ta.setPrefRowCount(5);
      dataGrid.add(ta, i + 1, toprow + 5);
      columnSetups[i].put("column_script", ta);
    }
    toprow += 6;
    l = new Label();
    dataGrid.add(l, 0, toprow);
    for (int i = 0; i < cols + 1; i++) {
      l = new Label(String.valueOf((char)(i+65)));
      l.setFont(Font.font("Arial", FontWeight.BOLD, 20));
      l.setAlignment(Pos.CENTER);
      dataGrid.add(l, i + 1, toprow);
    }
    cols = 0;
    for (int i = 0; i < exportFile.rowCount(); i++) {
      toprow++;
      l = new Label(String.valueOf(i + 1));
      l.setFont(Font.font("Arial", FontWeight.BOLD, 20));
      l.setAlignment(Pos.CENTER);
      dataGrid.add(l, 0, toprow);
      String[] r = exportFile.getRow(i);
      if (r == null) continue;
      for (int j = 0; j < r.length; j++) {
        l = new Label(r[j]);
        dataGrid.add(l, j+1, toprow);
      }
    }
  }
}
