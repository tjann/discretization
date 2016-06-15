/**
 *  GEDPROTOOLS - Gene Expression Data pre PROcessing TOOLS <p>
 *
 *  Latest release available at http://lidecc.cs.uns.edu.ar/files/gedprotools.zip <p>
 *
 *  Copyright (C) 2015 - Cristian A. Gallo - Julieta Dussaut <p>
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



public class MatrixStandardization {

	/** PUBLIC ENUMERATION
	 * 
	 * Indicates the type of operation that it is possible to perform:
	 * 		RowStandardization.
	 * 		ColumnStandardization.
	 * 		MultConstat.
	 *		AddConstant.
	 *		Log.
	 *
	 */
	public static enum Operations
	{
		RowStandardization,
		ColumnStandardization,
		MultConstant,
		AddConstant,
		Log
	}
	
	/** PUBLIC STATIC METHOD
	 * 
	 * RowStandardization: performs the row standardization on the given matrix.
	 * @param ged: float matrix.
	 * @return float matrix.
	 * 
	 */
    public static float[][] RowStandardization(float [][] ged){
    	float [][] result = new float [ged.length][ged[0].length];
    	float [] desvio = new float [ged.length];
    	float [] media = new float [ged.length];

        for (int i=0; i<ged.length; i++)
        {
            desvio[i] = 0;
            media[i] = 0;
            //Calculates the row average.
            for (int j=0; j<ged[i].length; j++)
            {
                media[i] = media[i]+ged[i][j];
            }
            media[i] = media[i]/ged[i].length;
            //Calculates the row deviation.
            for (int j=0; j<ged[i].length; j++)
            {
                desvio[i] = desvio[i]+ (float)Math.pow(ged[i][j]-media[i], 2);
            }
            desvio[i] = (float)Math.sqrt(desvio[i]/ged[i].length);
            //Row standardization
            for (int j=0; j<ged[i].length; j++)
            {
                 result[i][j] = (ged[i][j]-media[i])/desvio[i];
            }
        }
        return result;
    }

    /** PUBLIC STATIC METHOD
     * 
     * ColumnStandardization: performs the column standardization on the given matrix
     * @param ged: float matrix
     * @return float matrix
     * 
     */
    public static float[][] ColumnStandardization(float [][] ged){
    	float [][] result = new float [ged.length][ged[0].length];
    	float [] desvio = new float [ged[0].length];
    	float [] media = new float [ged[0].length];

        for (int j=0; j<ged[0].length; j++)
        {
            desvio[j] = 0;
            media[j] = 0;
            //Calculates the column average
            for (int i=0; i<ged.length; i++)
            {
                media[j] = media[j]+ged[i][j];
            }
            media[j] = media[j]/ged.length;
            //Calculates the column deviation.
            for (int i=0; i<ged.length; i++)
            {
                desvio[j] = desvio[j] + (float)Math.pow(ged[i][j]-media[j], 2);
            }
            desvio[j] = (float)Math.sqrt(desvio[j]/ged.length);
            //Column Standardization
            for (int i=0; i<ged.length; i++)
            {
                result[i][j] = (ged[i][j] - media[j])/desvio[j];
            }

        }
        return result;
    }

    /** PUBLIC STATIC METHOD
     * 
     * MultConstat: multiplies the given matrix for a given constant 
     * @param ged: float matrix
     * @param a: float constant
     * @return float matrix
     * 
     */
    public static float[][] MultConstant(float [][] ged, float a){
    	float [][] result = new float [ged.length][ged[0].length];
        for (int i=0; i<ged.length; i++)
        {
            for (int j=0; j<ged[i].length; j++){
                result[i][j] = ged[i][j]*a;
            }
        }
        return result;

    }
    
    /** PUBLIC STATIC METHOD
     * 
     * AddConstant: adds a given constant to a given matrix.
     * @param ged: float matrix.
     * @param a: float constant.
     * @return float matrix.
     * 
     */
    public static float[][] AddConstant(float [][] ged, float a){
    	float [][] result = new float [ged.length][ged[0].length];
        for (int i=0; i<ged.length; i++)
        {
            for (int j=0; j<ged[i].length; j++){
                result[i][j] = ged[i][j] + a;
            }
        }
        return result;

    }

    /** PUBLIC STATIC METHOD
     * 
     * Log: performs the logarithmic operation over the given matrix.
     * @param ged: float matrix.
     * @return float matrix.
     * 
     */
    public static float[][] Log(float [][] ged){
    	float [][] result = new float [ged.length][ged[0].length];
        for (int i=0; i<ged.length; i++)
        {
            for (int j=0; j<ged[i].length; j++){
                result[i][j] = (float)(Math.log10(ged[i][j])/Math.log10(2));
            }
        }
        return result;

    }

}
