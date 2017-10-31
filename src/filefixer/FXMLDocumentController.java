/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filefixer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JMenuItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author jonas
 */
public class FXMLDocumentController implements Initializable  {
    
  @FXML
  private JMenuItem fileOpenMenu;
  @FXML
  private GridPane dataGrid;
  private FileChooser fileChooser= new FileChooser();
  private Stage stage;


  @FXML
  private void handleFileImport(ActionEvent event) {
    fileChooser.setTitle("Find file to fix");
    File file = fileChooser.showOpenDialog(this.stage);
    if (file != null) {
      FileFixer.prefs.put("folder", file.getParentFile().getAbsolutePath());
      if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
        readXLSX(file);
      }
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Init filechooser with default folder
    String folder = FileFixer.prefs.get("folder", "");
    if (!folder.equals("")) {
      fileChooser.setInitialDirectory(new File(folder));
    }
  }

  void setStage(Stage stage) {
    this.stage = stage;
  }

  private boolean readXLSX(File selectedFile) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(selectedFile);
      Workbook wb = new HSSFWorkbook(fis);
      Sheet s = wb.getSheetAt(0);
      int cols = 0;
      for (int i = 0; i < s.getLastRowNum(); i++) {
        Row r = s.getRow(i);
        if (r == null) continue;
        for (int j = 0; j < r.getLastCellNum(); j++) {
          if (j > cols) cols = j;
        }
      }
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


      for (int i = 0; i < cols + 1; i++) {
        l = new Label("Column order:");
        dataGrid.add(l, i + 1, toprow);
        TextField tf = new TextField();
        dataGrid.add(tf, i + 1, toprow + 1);
        l = new Label("Column label:");
        dataGrid.add(l, i + 1, toprow + 2);
        tf = new TextField();
        dataGrid.add(tf, i + 1, toprow + 3);
        l = new Label("Column script:");
        dataGrid.add(l, i + 1, toprow + 4);
        TextArea ta = new TextArea();
        ta.setWrapText(true);
        ta.setPrefRowCount(5);
        dataGrid.add(ta, i + 1, toprow + 5);
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
      for (int i = 0; i < s.getLastRowNum(); i++) {
        toprow++;
        l = new Label(String.valueOf(i + 1));
        l.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        l.setAlignment(Pos.CENTER);
//        l.setTextAlignment(TextAlignment.RIGHT);
        dataGrid.add(l, 0, toprow);
        Row r = s.getRow(i);
        if (r == null) continue;
        for (int j = 0; j < r.getLastCellNum(); j++) {
          Cell c = r.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
          l = new Label();
          if (c != null) {
            CellType type = c.getCellTypeEnum();
            if (type.equals(CellType.STRING)) {
              l.setText(c.getStringCellValue());
            }
            else if (type.equals(CellType.NUMERIC)) {
              l.setText(Double.toString(c.getNumericCellValue()));
            }
          }
          dataGrid.add(l, j+1, toprow);
        }
      }
      wb.close();
      fis.close();
      return true;
    } catch (FileNotFoundException ex) {
      Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        fis.close();
      } catch (IOException ex) {
        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return false;
  }
}
