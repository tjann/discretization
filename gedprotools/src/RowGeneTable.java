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

import java.awt.*;
import java.beans.*;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/*
 *	Use a JTable as a renderer for row numbers of a given main table.
 *  This table must be added to the row header of the scrollpane that
 *  contains the main table.
 */
public class RowGeneTable extends JvTable
	implements ChangeListener, PropertyChangeListener, Serializable
{
	private JTable main;
        private Project Proj;

	public RowGeneTable(JTable table, Project P)
	{
                Proj = P;
		main = table;
		main.addPropertyChangeListener( this );

		setFocusable( true );
		setAutoCreateColumnsFromModel( false );
		setModel( main.getModel() );
		setSelectionModel( main.getSelectionModel() );


                
                //column1
		TableColumn column = new TableColumn();
		column.setHeaderValue("Genes");
		addColumn( column );
		column.setCellRenderer(new RowNumberRenderer());
                column.setPreferredWidth(100);
                column.setResizable(true);



		//getColumnModel().getColumn(0).setPreferredWidth(100);
                //getColumnModel().getColumn(0).setResizable(true);


                //column2
                if (Proj.genenames != Proj.targetdescription)
                {
                    column = new TableColumn();
                    column.setHeaderValue("Target Description");
                    addColumn( column );
                    column.setCellRenderer(new RowNumberRenderer());
                    column.setPreferredWidth(300);
                    column.setResizable(true);

                    //getColumnModel().getColumn(1).setPreferredWidth(200);
                    //getColumnModel().getColumn(1).setResizable(true);
                }
                
		setPreferredScrollableViewportSize(getPreferredSize());

                this.getTableHeader().setReorderingAllowed(false);                
                table.getTableHeader().setReorderingAllowed(false);
                

	}


	@Override
	public void addNotify()
	{
		super.addNotify();

		Component c = getParent();

		//  Keep scrolling of the row table in sync with the main table.

		if (c instanceof JViewport)
		{
			JViewport viewport = (JViewport)c;
			viewport.addChangeListener( this );
		}
	}


        public void updateRowHeights()
        {
            try
            {
                for (int row = 0; row < this.getRowCount(); row++)
                {
                    int rowH = this.getRowHeight();

                    Component comp = this.prepareRenderer(this.getCellRenderer(row, 1), row, 1);
                    rowH = Math.max(rowH, comp.getPreferredSize().height);


                    this.setRowHeight(row, rowH);
                }
            }
            catch(ClassCastException e) {}
        }

	/*
	 *  Delegate method to main table
	 */
	@Override
	public int getRowCount()
	{
		return main.getRowCount();
	}

	@Override
	public int getRowHeight(int row)
	{
		return main.getRowHeight(row);
	}

	/*
	 *  This table does not use any data from the main TableModel,
	 *  so just return a value based on the row parameter.
	 */
	@Override
	public Object getValueAt(int row, int column)
	{
            if (column == 0)
		return Proj.genenames[row];            
            return Proj.targetdescription[row];
	}

	/*
	 *  Don't edit data in the main TableModel by mistake
	 */
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
//
//  Implement the ChangeListener
//
	public void stateChanged(ChangeEvent e)
	{
		//  Keep the scrolling of the row table in sync with main table

		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane)viewport.getParent();
		scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	}
//
//  Implement the PropertyChangeListener
//
	public void propertyChange(PropertyChangeEvent e)
	{
		//  Keep the row table in sync with the main table

		if ("selectionModel".equals(e.getPropertyName()))
		{
			setSelectionModel( main.getSelectionModel() );
		}

		if ("model".equals(e.getPropertyName()))
		{
			setModel( main.getModel() );
		}
	}

	/*
	 *  Borrow the renderer from JDK1.4.2 table header
	 */
	private static class RowNumberRenderer extends DefaultTableCellRenderer
	{
		public RowNumberRenderer()
		{
			setHorizontalAlignment(JLabel.LEFT);
		}

		public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (table != null)
			{
				JTableHeader header = table.getTableHeader();

				if (header != null)
				{
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			if (isSelected)
			{
				setFont( getFont().deriveFont(Font.BOLD) );
			}

			setText((value == null) ? "" : value.toString());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			return this;
		}
	}
}
