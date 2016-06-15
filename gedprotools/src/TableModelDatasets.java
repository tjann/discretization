/**
 *  GEDPROTOOLS - Gene Expression Data pre PROcessing TOOLS <p>
 *
 *  Latest release available at http://lidecc.cs.uns.edu.ar/files/gedprotools.zip <p>
 *
 *  Copyright (C) 2015 - Cristian A. Gallo <p>
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version. <p>
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  more details. <p>
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 *  Place - Suite 330, Boston, MA 02111-1307, USA. <br>
 *  http://www.fsf.org/licenses/gpl.txt
 */


package GEDPROTOOLS;

import java.io.Serializable;
import javax.swing.table.AbstractTableModel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Galo
 */


public class TableModelDatasets extends AbstractTableModel implements Serializable{
    
    
    private int data;
    private Project P;
    

    public TableModelDatasets (Project Pro, int i)
    {
        data = i;
        P = Pro;
    }

    public int getColumnCount() {
        return P.D_columnas[data].size();
    }

    public int getRowCount() {
        return P.filas;
    }

    @Override
    public String getColumnName(int col) {
        return P.D_columnas[data].get(col);
    }


    public Object getValueAt(int row, int col) {
        if (P.heatMapOn)
        {
            float darker;
            if (P.heatMapScope==0)
            {
                if (P.datamatrix[data][row][col] >= P.dataMatrixStat[data][row][2])
                {
                    darker = (P.datamatrix[data][row][col] - P.dataMatrixStat[data][row][2]) / (P.dataMatrixStat[data][row][1] - P.dataMatrixStat[data][row][2]);
                    return P.colours.darknizer(P.colours.heatMapAboveAverage, darker);
                }
                else
                {
                    darker = (P.dataMatrixStat[data][row][2] - P.datamatrix[data][row][col]) / (P.dataMatrixStat[data][row][2] - P.dataMatrixStat[data][row][0]);
                    return P.colours.darknizer(P.colours.heatMapBelowAverage, darker);
                }
            }
            else if (P.heatMapScope==1)
            {
                if (P.datamatrix[data][row][col] >= P.dataMatrixStatCol[data][col][2])
                {
                    darker = (P.datamatrix[data][row][col] - P.dataMatrixStatCol[data][col][2]) / (P.dataMatrixStatCol[data][col][1] - P.dataMatrixStatCol[data][col][2]);
                    return P.colours.darknizer(P.colours.heatMapAboveAverage, darker);
                }
                else
                {
                    darker = (P.dataMatrixStatCol[data][col][2] - P.datamatrix[data][row][col]) / (P.dataMatrixStatCol[data][col][2] - P.dataMatrixStatCol[data][col][0]);
                    return P.colours.darknizer(P.colours.heatMapBelowAverage, darker);
                }

            }
            else
            {
                if (P.datamatrix[data][row][col] >= P.dataMatrixStatMatrix[data][2])
                {
                    darker = (P.datamatrix[data][row][col] - P.dataMatrixStatMatrix[data][2]) / (P.dataMatrixStatMatrix[data][1] - P.dataMatrixStatMatrix[data][2]);
                    return P.colours.darknizer(P.colours.heatMapAboveAverage, darker);
                }
                else
                {
                    darker = (P.dataMatrixStatMatrix[data][2] - P.datamatrix[data][row][col]) / (P.dataMatrixStatMatrix[data][2] - P.dataMatrixStatMatrix[data][0]);
                    return P.colours.darknizer(P.colours.heatMapBelowAverage, darker);
                }
            }

        }
        return P.datamatrix[data][row][col];
    }

    public Class getColumnClass(int c) {
        if (P.heatMapOn)
        {
            return P.colours.heatMapAboveAverage.getClass();
        }
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (P.heatMapOn)
            return false;
        return true;
        
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        P.datamatrix[data][row][col] = (Float)value;
        fireTableCellUpdated(row, col);
        P.updateStat();
    }
    
}


