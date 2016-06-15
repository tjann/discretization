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



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;


/**
 *
 * @author Galo
 */
public class Project implements Serializable{
    File datasets [];
    float datamatrix [][][];
    float dataMatrixStat[][][];
    float dataMatrixStatCol[][][];
    float dataMatrixStatMatrix[][];
    boolean timeseries[];
    String [] genenames;
    boolean hastargetdescription = false;
    public String [] targetdescription;
    int numberofdatasets;
    int filas;
    Vector<String>[] D_columnas;
    File f = null;

    Colorcode colours = new Colorcode();

    

   
    int timewindow = 0;
    int maxwindow;
   

  

   
    

    boolean heatMapOn = false;


      
    
    public boolean filter = false;
    int heatMapScope = 0;
   


    public void updateStat()
    {
        dataMatrixStat = new float[numberofdatasets][filas][4];
        dataMatrixStatCol = new float[numberofdatasets][][];
        dataMatrixStatMatrix = new float[numberofdatasets][4];
        for (int k=0; k<datamatrix.length; k++)
            dataMatrixStatCol[k] = new float[datamatrix[k][0].length][4];

        
        float min, max, average, desvest;
        int numbersamples;

        //por filas
        for (int k=0; k<numberofdatasets;k++)
            for (int i=0; i<datamatrix[k].length; i++)
            {
                min = Integer.MAX_VALUE;
                max = Integer.MIN_VALUE;
                average = 0;
                desvest = 0;
                numbersamples = 0;
                for (int j=0; j<datamatrix[k][0].length; j++)
                {
                    if (datamatrix[k][i][j] != 999)
                    {
                        if (datamatrix[k][i][j] < min)
                            min = datamatrix[k][i][j];
                        if (datamatrix[k][i][j] > max)
                            max = datamatrix[k][i][j];
                        average += datamatrix[k][i][j];

                        numbersamples++;
                    }
                }
                average = average / (float)numbersamples;
                dataMatrixStat[k][i][0] = min;
                dataMatrixStat[k][i][1] = max;
                dataMatrixStat[k][i][2] = average;


                for (int j=0; j<datamatrix[k][0].length; j++)
                    if (datamatrix[k][i][j] != 999)
                        desvest += (datamatrix[k][i][j]-average)*(datamatrix[k][i][j]-average);

                desvest = (float)Math.sqrt(desvest / (float)(numbersamples));

                dataMatrixStat[k][i][3] = desvest;

            }

            //por columnas
            for (int k=0; k<numberofdatasets;k++)
                for (int j=0; j<datamatrix[k][0].length; j++)
                {
                    min = Integer.MAX_VALUE;
                    max = Integer.MIN_VALUE;
                    average = 0;
                    desvest = 0;
                    numbersamples = 0;
                    for (int i=0; i<datamatrix[k].length; i++)
                    {
                        if (datamatrix[k][i][j] != 999)
                        {
                            if (datamatrix[k][i][j] < min)
                                min = datamatrix[k][i][j];
                            if (datamatrix[k][i][j] > max)
                                max = datamatrix[k][i][j];
                            average += datamatrix[k][i][j];

                            numbersamples++;
                        }
                    }
                    average = average / (float)numbersamples;
                    dataMatrixStatCol[k][j][0] = min;
                    dataMatrixStatCol[k][j][1] = max;
                    dataMatrixStatCol[k][j][2] = average;


                    for (int i=0; i<datamatrix[k].length; i++)
                        if (datamatrix[k][i][j] != 999)
                            desvest += (datamatrix[k][i][j]-average)*(datamatrix[k][i][j]-average);

                    desvest = (float)Math.sqrt(desvest / (float)(numbersamples));

                    dataMatrixStatCol[k][j][3] = desvest;

                }

            // por matrix
            for (int k=0; k<numberofdatasets;k++)
            {
                min = Integer.MAX_VALUE;
                max = Integer.MIN_VALUE;
                average = 0;
                desvest = 0;
                numbersamples = 0;
                for (int i=0; i<datamatrix[k].length; i++)
                {

                    for (int j=0; j<datamatrix[k][0].length; j++)
                    {
                        if (datamatrix[k][i][j] != 999)
                        {
                            if (datamatrix[k][i][j] < min)
                                min = datamatrix[k][i][j];
                            if (datamatrix[k][i][j] > max)
                                max = datamatrix[k][i][j];
                            average += datamatrix[k][i][j];

                            numbersamples++;
                        }
                    }

                }
                average = average / (float)numbersamples;
                dataMatrixStatMatrix[k][0] = min;
                dataMatrixStatMatrix[k][1] = max;
                dataMatrixStatMatrix[k][2] = average;

                for (int i=0; i<datamatrix[k].length; i++)
                    for (int j=0; j<datamatrix[k][0].length; j++)
                        if (datamatrix[k][i][j] != 999)
                            desvest += (datamatrix[k][i][j]-average)*(datamatrix[k][i][j]-average);

                desvest = (float)Math.sqrt(desvest / (float)(numbersamples));

                dataMatrixStatMatrix[k][3] = desvest;
            }
    }

    public Project(String inputFileName[],  boolean time[], boolean hasRowName, boolean hasColumnName, boolean hasTargetDescription) throws FileNotFoundException, IOException, datasetsDiferentsSizeWithoutNamesException, ArrayIndexOutOfBoundsException
     {
         hastargetdescription = hasTargetDescription;
         timeseries = time;
         int min_columns=Integer.MAX_VALUE;
         numberofdatasets = inputFileName.length;
         D_columnas = new Vector[numberofdatasets];
         float data[][][] = new float[numberofdatasets][][];
         Vector <String>names[] = new Vector [numberofdatasets];
         Vector <String>target[] = new Vector [numberofdatasets];
         boolean allequal = true;
         int sizeTemp = 0;
         for (int i=0; i<numberofdatasets; i++)
         {
             names[i] = new Vector(0);
             target[i] = new Vector(0);
             D_columnas[i] = new Vector(0);
             data[i] = load(inputFileName[i], names[i], D_columnas[i], hasRowName, hasColumnName, target[i], hasTargetDescription);
             if (timeseries[i] && D_columnas[i].size()<min_columns)
                 min_columns = D_columnas[i].size();

             if (i==0)
                 sizeTemp = data[i].length;
             allequal = allequal && (sizeTemp == data[i].length);
         }

         if ((min_columns == Integer.MAX_VALUE) || (min_columns-4)<=0)
             maxwindow = 0;
         else
             maxwindow = min_columns - 4;

         if (!hasRowName && !allequal)
             throw new datasetsDiferentsSizeWithoutNamesException();


         if(!hasRowName)
         {
             datamatrix = data;
             genenames = new String[1];
             genenames = names[0].toArray(genenames);
             targetdescription = genenames;
             filas = genenames.length;
         }
         else
         {
             //find the datasets with less genes and put it first            
             int mingenes = names[0].size();
             int minindex = 0;
             for(int i=1; i<names.length; i++)
                 if (names[0].size()<mingenes)
                 {
                     mingenes = names[0].size();
                     minindex = i;
                 }

             float datatemp [][];
             datatemp = data[0];
             data[0] = data[minindex];
             data[minindex] = datatemp;

             //now filters the common genes
             Vector <int[]> indexToCopy = new Vector(0);
             for (int i=0; i<data[0].length; i++)
             {
                 int indexi[] = new int[numberofdatasets];
                 boolean contains = true;
                 int k = 0;
                 while (k<numberofdatasets && contains)
                 {
                     contains = names[k].contains(names[0].get(i)) && contains;
                     indexi[k] = names[k].indexOf(names[0].get(i));
                     k++;
                 }
                 if (contains)
                    indexToCopy.add(indexi);
             }

             filas = indexToCopy.size();
             genenames = new String[filas];
             targetdescription = new String[filas];
             for (int i=0; i<filas;i++)
             {
                 genenames[i] = names[0].get(indexToCopy.get(i)[0]);
                 if (hasTargetDescription)                     
                     targetdescription[i] = target[0].get(indexToCopy.get(i)[0]);
             }
             if (!hasTargetDescription)
                     targetdescription = genenames;

             datamatrix = new float[numberofdatasets][][];
             for (int k=0; k<numberofdatasets; k++)
             {
                 datamatrix[k] = new float[filas][D_columnas[k].size()];
                 for (int i=0; i<filas; i++)
                     for (int j=0; j<D_columnas[k].size(); j++)
                         datamatrix[k][i][j] = data[k][indexToCopy.get(i)[k]][j];
             }

         }
         //desde aca
         /*
         if (hasTargetDescription)
             for (int i=0; i<targetdescription.length; i++)
             {
                    String tmp = "<html><p>";
                    int L=50;
                    boolean cortito = false;

                    if (L >=targetdescription[i].length())
                    {
                        tmp = tmp+targetdescription[i];
                        cortito = true;
                    }

                    int Pi = 0, Ki =0;

                    while (!cortito && Pi<targetdescription[i].length())
                    {
                        if (Ki>=L && targetdescription[i].charAt(Pi) == ' ')
                        {
                            if (Pi==Ki)
                                tmp = tmp +targetdescription[i].substring(Pi-Ki, Pi);
                            else
                                tmp = tmp + "<br>"+targetdescription[i].substring(Pi-Ki, Pi);
                            Ki = 0;
                        }
                        else
                            Ki++;

                        Pi++;
                    }
                    if (Ki>0 && !cortito)
                        tmp = tmp + "<br>"+targetdescription[i].substring(Pi-Ki, Pi);

                    targetdescription[i] = tmp+"</p></html>";
             }
          */
         //hasta aca es el cortado del target description

         updateStat();
     }



     private float [][] load (String file, Vector<String> GeneNames, Vector<String> ColumnNames, boolean hasRowName, boolean hasColumnName, Vector<String> TargetDescription, boolean hasTargetDescription) throws FileNotFoundException, IOException
     {
         ArrayList<Vector <String>> temp = new ArrayList(0);
         CSVFileReader CSVreader = new CSVFileReader(file,',');
         Vector <String> rows;
         float data [][];
         int fil, col;

         if (hasColumnName)
             ColumnNames.addAll(CSVreader.readFields());
         
         if (hasRowName && hasColumnName)
         {
             ColumnNames.remove(0);
             if (hasTargetDescription)
                 ColumnNames.remove(0);
         }

         rows = CSVreader.readFields();
         while (rows != null)
         {
             temp.add(rows);
             rows = CSVreader.readFields();
         }
         fil = temp.size();
         col = temp.get(0).size();
         int realcol;
         if (hasRowName && !hasTargetDescription){
            data = new float[fil][col-1];
            realcol = col-1;
         }
         else if (hasRowName && hasTargetDescription)
         {
             data = new float[fil][col-2];
             realcol = col-2;
         }
         else
         {
            realcol = col;
            data = new float[fil][col];
         }
         
         
         for (int i=0; i<fil; i++){
            if (hasRowName)
            {
                   GeneNames.add(i, temp.get(i).get(0));
                   if (hasTargetDescription)
                       TargetDescription.add(i, temp.get(i).get(1));
            }
            for (int j=0; j<col; j++){
                if (hasRowName && !hasTargetDescription && j>0)
                    data[i][j-1] = Float.parseFloat((String)((Vector)temp.get(i)).get(j));
                if (hasRowName && hasTargetDescription && j>1)
                    data[i][j-2] = Float.parseFloat((String)((Vector)temp.get(i)).get(j));
                else if (!hasRowName)
                    data[i][j] = Float.parseFloat((String)((Vector)temp.get(i)).get(j));
            }
         }

         if (!hasRowName)
         {             
             for (int i=0;i<fil; i++)
                 GeneNames.add(i,"Gene"+(i+1));
         }

         if (!hasColumnName)
         {             
             for (int i=0; i<realcol; i++)
                 ColumnNames.add(i, ""+(i+1));
         }

         return data;

     }


    public Project(String inputFileName[], String inputNameGenes, boolean time[]) throws FileNotFoundException, IOException, NumberFormatException{


        timeseries = time;
        int min_columns=Integer.MAX_VALUE;
        ArrayList temp = new ArrayList(0);
        CSVFileReader CSVreader = new CSVFileReader(inputFileName[0],',');
        Vector <String> rows;
        File f1 = new File(inputNameGenes);
        BufferedReader entrada = new BufferedReader( new FileReader( f1 ) );
        numberofdatasets = inputFileName.length;
        D_columnas = new Vector[numberofdatasets];
        
        datamatrix = new float[numberofdatasets][][];
        D_columnas[0] = CSVreader.readFields();
        if (timeseries[0])        
            min_columns = D_columnas[0].size();
            
        rows = CSVreader.readFields();
        while (rows != null)
        {
            temp.add(rows);
            rows = CSVreader.readFields();
        }
        filas = temp.size();
        
        datamatrix[0] = new float[filas][D_columnas[0].size()];
        genenames = new String[filas];
        for (int i=0; i<filas; i++){
            genenames[i]=entrada.readLine();
            for (int j=0; j<D_columnas[0].size(); j++){
                datamatrix[0][i][j] = Float.parseFloat((String)((Vector)temp.get(i)).get(j));
            }
        }

        for (int k=1; k<inputFileName.length; k++){
            temp = new ArrayList(0);
            CSVreader = new CSVFileReader(inputFileName[k],',');

            D_columnas[k] = CSVreader.readFields();

            if (timeseries[k] && D_columnas[k].size()<min_columns)
            {
                min_columns = D_columnas[k].size();
            }

            rows = CSVreader.readFields();
            while (rows != null)
            {
                temp.add(rows);
                rows = CSVreader.readFields();
            }

            
            datamatrix[k] = new float[filas][D_columnas[k].size()];

            for (int i=0; i<filas; i++){
                for (int j=0; j<D_columnas[k].size(); j++){
                    datamatrix[k][i][j] = Float.parseFloat((String)((Vector)temp.get(i)).get(j));
                }
            }

        }

        if ((min_columns == Integer.MAX_VALUE) || (min_columns-4)<=0)
            maxwindow = 0;
        else                    
            maxwindow = min_columns - 4;


    }

}
