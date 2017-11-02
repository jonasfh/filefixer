/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nb.dogp.filefixer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jonas
 */
public class ExportSetup implements Serializable {
  private String startExportScript = "";
  private int startRow = -1;
  private String endExportScript = "";
  private String title = "";
  List<ColumnSetup> columns = new ArrayList<>();
  
  public ExportSetup(int startRow, String title) {
    this.startRow = startRow;
    this.title = title;
  }
  
  public void addColumnSetup(
    int columnNo, int columnOrder, String columnLabel, String columnScript
  ) {
    columns.add(
      new ColumnSetup(columnNo, columnOrder, columnLabel, columnScript)
    );
  }
  
  private class ColumnSetup implements Comparable<ColumnSetup>, Serializable{
    private int columnNo = -1;
    private int columnOrder = -1;
    private String columnLabel = "";
    private String columnScript = "";
    public ColumnSetup(
      int columnNo, int columnOrder, String columnLabel, String columnScript
    ) {
      this.columnNo = columnNo;
      this.columnOrder = columnOrder;
      this.columnLabel = columnLabel;
      this.columnScript = columnScript;
    }

    /**
     * @return the columnNo
     */
    public int getColumnNo() {
      return columnNo;
    }

    /**
     * @param columnNo the columnNo to set
     */
    public void setColumnNo(int columnNo) {
      this.columnNo = columnNo;
    }

    /**
     * @return the columnOrder
     */
    public int getColumnOrder() {
      return columnOrder;
    }

    /**
     * @param columnOrder the columnOrder to set
     */
    public void setColumnOrder(int columnOrder) {
      this.columnOrder = columnOrder;
    }

    /**
     * @return the columnLabel
     */
    public String getColumnLabel() {
      return columnLabel;
    }

    /**
     * @param columnLabel the columnLabel to set
     */
    public void setColumnLabel(String columnLabel) {
      this.columnLabel = columnLabel;
    }

    /**
     * @return the columnScript
     */
    public String getColumnScript() {
      return columnScript;
    }

    /**
     * @param columnScript the columnScript to set
     */
    public void setColumnScript(String columnScript) {
      this.columnScript = columnScript;
    }

    @Override
    public int compareTo(ColumnSetup o) {
      return this.getColumnOrder() - o.getColumnOrder();
    }
    
  }

  /**
   * @return the startExportScript
   */
  public String getStartExportScript() {
    return startExportScript;
  }

  /**
   * @param startExportScript the startExportScript to set
   */
  public void setStartExportScript(String startExportScript) {
    this.startExportScript = startExportScript;
  }

  /**
   * @return the startRow
   */
  public int getStartRow() {
    return startRow;
  }

  /**
   * @param startRow the startRow to set
   */
  public void setStartRow(int startRow) {
    this.startRow = startRow;
  }

  /**
   * @return the endExportScript
   */
  public String getEndExportScript() {
    return endExportScript;
  }

  /**
   * @param endExportScript the endExportScript to set
   */
  public void setEndExportScript(String endExportScript) {
    this.endExportScript = endExportScript;
  }
}

