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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
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
      dataGrid.setStyle("overflow:hidden;");
      dataGrid.setMaxWidth(GridPane.USE_PREF_SIZE);
      dataGrid.setMinWidth(GridPane.USE_PREF_SIZE);
      Text t = new Text();
      dataGrid.add(t, 0, toprow);

      for (int i = 0; i < cols; i++) {
        t = new Text(String.valueOf((char)(i+65)));
        t.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        dataGrid.add(t, i+1, toprow);
      }

      cols = 0;
      for (int i = 0; i < s.getLastRowNum(); i++) {
        toprow++;
        t = new Text(String.valueOf(i+1));
        t.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        dataGrid.add(t, 0, toprow);
        Row r = s.getRow(i);
        if (r == null) continue;
        for (int j = 0; j < r.getLastCellNum(); j++) {
          Cell c = r.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
          t = new Text();
          t.getStyleClass().add("data");
          t.setStyle("overflow:hidden;font-size:12;display:block;width:70px;");
          if (c != null) {
            CellType type = c.getCellTypeEnum();
            if (type.equals(CellType.STRING)) {
              t.setText(c.getStringCellValue());
            }
            else if (type.equals(CellType.NUMERIC)) {
              t.setText(Double.toString(c.getNumericCellValue()));
            }
          }
          dataGrid.add(t, j+1, toprow);
        }
      }


//      String name = r.getCell(0).getStringCellValue();
//      String shortName = r.getCell(1).getStringCellValue();
//      String charName = r.getCell(2).getStringCellValue();
//      String value = r.getCell(3).getStringCellValue();
//      if ("".equals(value)) {
//          continue;
//      }
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
