/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nb.dogp.filefixer.filehandler;

/**
 *
 * @author jonas
 */
public interface Exportable {
  public String[] getRow(int i);
  public int rowCount();
  public int columnCount();
}
