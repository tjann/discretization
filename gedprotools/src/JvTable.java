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

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author Galo
 */
public class JvTable extends JTable
{
    private Font originalFont;
    private int originalRowHeight;
    //private int originalColumnWidth[];
    private float zoomFactor = 1.0f;


    public JvTable()
    {
        super();
        for (int i=0; i<super.getColumnModel().getColumnCount() ; i++)
        {
            super.getColumnModel().getColumn(i).setResizable(false);
        }
    }

    public JvTable(TableModel dataModel)
    {
        super(dataModel);
        for (int i=0; i<super.getColumnModel().getColumnCount() ; i++)
        {
            super.getColumnModel().getColumn(i).setResizable(false);
        }
    }

    @Override
    public void setFont(Font font)
    {
        originalFont = font;
        // When setFont() is first called, zoomFactor is 0.
        if (zoomFactor != 0.0 && zoomFactor != 1.0)
        {
            float scaledSize = originalFont.getSize2D() * zoomFactor;
            font = originalFont.deriveFont(scaledSize);
        }

        super.setFont(font);
    }

    @Override
    public void setRowHeight(int rowHeight)
    {
        originalRowHeight = rowHeight;
        // When setRowHeight() is first called, zoomFactor is 0.
        if (zoomFactor != 0.0 && zoomFactor != 1.0)
            rowHeight = (int) Math.ceil(originalRowHeight * zoomFactor);

        super.setRowHeight(rowHeight);
    }

    public float getZoom()
    {
        return zoomFactor;
    }

    public void setZoom(float zoomFactor)
    {
        if (this.zoomFactor == zoomFactor)
            return;

        if (originalFont == null)
            originalFont = getFont();
        if (originalRowHeight == 0)
            originalRowHeight = getRowHeight();



        float oldZoomFactor = this.zoomFactor;
        this.zoomFactor = zoomFactor;
        Font font = originalFont;
        if (zoomFactor != 1.0)
        {
            float scaledSize = originalFont.getSize2D() * zoomFactor;
            font = originalFont.deriveFont(scaledSize);
        }
        super.setFont(font);
        super.setRowHeight((int) Math.ceil(originalRowHeight * zoomFactor));
        getTableHeader().setFont(font);
        firePropertyChange("zoom", oldZoomFactor, zoomFactor);

        TableColumn tableColumn;
        Component component;
        int rendererWidth;
        boolean first = false;
        /*
        if (originalColumnWidth == null)
        {
            originalColumnWidth = new int [super.getColumnModel().getColumnCount()];
            first = true;
        }*/
        
        for (int i=0; i<super.getColumnModel().getColumnCount() ; i++)
        {
            tableColumn = super.getColumnModel().getColumn(i);
            //if (first)
            //    originalColumnWidth[i] = tableColumn.getWidth();

            //component = prepareRenderer(super.getCellRenderer(0, i), 0, i);
            //rendererWidth = component.getPreferredSize().width;
            //tableColumn.setPreferredWidth(Math.min(rendererWidth+getIntercellSpacing().width,tableColumn.getPreferredWidth() ));

            tableColumn.setPreferredWidth((int) Math.ceil(tableColumn.getWidth()/oldZoomFactor*zoomFactor ) );

        }

    }

    /*
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row,   int column)
    {
            Component component = super.prepareRenderer(renderer, row, column);
            int rendererWidth = component.getPreferredSize().width;
            TableColumn tableColumn = getColumnModel().getColumn(column);
            tableColumn.setPreferredWidth(Math.max(rendererWidth +
                    getIntercellSpacing().width,
                    tableColumn.getPreferredWidth()));
            component.setFont(this.getFont());
            return  component;
    }
     */

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
        Component comp = super.prepareRenderer(renderer, row, column);
        comp.setFont(this.getFont());
        return comp;
    }

    @Override
    public Component prepareEditor(TableCellEditor editor, int row, int column)
    {
        Component comp = super.prepareEditor(editor, row, column);
        comp.setFont(this.getFont());
        return comp;
    }
}
