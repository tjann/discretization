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

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Galo
 */
public class Colorcode implements Serializable{

    public Color borderVertexShow = Color.BLACK;
    public Color borderVertexHide = new Color(240, 240, 240, 0);
    public Color fillVertexShow = Color.GREEN;
    public Color fillVertexShowPicked = Color.YELLOW;
    public Color fillVertexHide = new Color(250, 250, 250, 0);
    public Color paintEdgeType1 = new Color(255, 50, 50, 1);
    public Color paintEdgeType2 = new Color(255, 50, 50, 1);
    public Color paintEdgeType3 = new Color(255, 50, 50, 1);
    public Color paintEdgeTypeMinus1 = new Color(50, 50, 255, 1);
    public Color paintEdgeTypeMinus2 = new Color(50, 50, 255, 1);
    public Color paintEdgeTypeMinus3 = new Color(50, 50, 255, 1);
    public Color paintEdgeHide = new Color(240, 240, 240, 0);

    public Color heatMapAboveAverage = new Color(255, 0, 0, 1);
    public Color heatMapBelowAverage = new Color(0, 255, 0, 1);



    public Color dup (Color c)
    {
        return new Color(c.getRed(), c.getGreen(), c.getBlue());
    }

    public Color darknizer (Color c, float v)
    {
        return new Color((int)(c.getRed()*v), (int)(c.getGreen()*v), (int)(c.getBlue()*v));
    }

    public Color opaquecolor(Color c, float v)
    {
        int r, g, b;
        if (v>0)
        {
            r = (int) (c.getRed() / v);
            g = (int) (c.getGreen() / v);
            b = (int) (c.getBlue() / v);
        }
        else
        {
            if (v < -1)
                v = -1;
            r = (int) (c.getRed() / -v);
            g = (int) (c.getGreen() / -v);
            b = (int) (c.getBlue() / -v);
        }

        if (r > 255)
            r = 255;
        if (g > 255)
            g = 255;
        if (b > 255)
            b = 255;

        return new Color(r, g, b);
    }

    public Color negative(Color c)
    {
        int r = Math.abs(255-c.getRed());
        int g = Math.abs(255-c.getGreen());
        int b = Math.abs(255-c.getBlue());

        return new Color(r, g, b);

    }
}
