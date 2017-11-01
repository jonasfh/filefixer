/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nb.dogp.filefixer.filehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nb.dogp.filefixer.FXMLDocumentController;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author jonas
 */
public class ExcelSheet implements Exportable{
  private File file;
  private Sheet sheet;
  private List<String[]> data;
  private int columnCount = -1;
  public ExcelSheet(File f, int sheetNum) {
    this.file = f;
    FileInputStream fis = null;
    data = new ArrayList<String[]>();
    try {
      fis = new FileInputStream(f);
      Workbook wb = new HSSFWorkbook(fis);
      this.sheet = wb.getSheetAt(sheetNum);
      for (int i = 0; i < rowCount(); i++) {
        String[] s = getRow(i);
        columnCount = Math.max(s.length, columnCount);
        data.add(getRow(i));
      }
      this.sheet = null;
      wb.close();
      fis.close();
    } catch (FileNotFoundException ex) {
      Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.ALL.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        fis.close();
      } catch (IOException ex) {
        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
  @Override
  public String[] getRow(int i) {
    if(this.sheet == null) {
      return data.get(i);
    }
    Row r = sheet.getRow(i);
    if (r == null) return new String[0];
    String[] res = new String[r.getLastCellNum()];
    for (int j = 0; j < r.getLastCellNum(); j++) {
      Cell c = r.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
      if (c != null) {
        CellType type = c.getCellTypeEnum();
        if (type.equals(CellType.STRING)) {
          res[j] = c.getStringCellValue();
        }
        else if (type.equals(CellType.NUMERIC)) {
          res[j] = Double.toString(c.getNumericCellValue());
        }
      }
      else {
        res[j] = "";
      }
    }
    return res;
  }

  @Override
  public int rowCount() {
    return sheet == null? data.size(): sheet.getLastRowNum();
  }

  @Override
  public int columnCount() {
    if (columnCount >= 0) {
      return columnCount;
    }
    int cols = 0;
    for (int i = 0; i < sheet.getLastRowNum(); i++) {
      Row r = sheet.getRow(i);
      if (r == null) continue;
      for (int j = 0; j < r.getLastCellNum(); j++) {
        if (j > cols) cols = j;
      }
    }
    columnCount = cols;
    return cols;
  }
}
