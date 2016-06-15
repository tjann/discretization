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

import java.util.*; 


/**
 *
 * @author Administrator
 */
public class EntropyBasedDicretizerOptimal2Clases {

    private  float D_ged[][][] = null;
    private  String genes[] = null;
    private threshold D_WTHRM[][][][] = null;
    private  float D_MEDG[][][] = null;
    private  int filas, D_columnas[];
    private  int window = 0;
    
  

    private  float D_OrdGEDM[][][][];
    private  float D_OrdGEDMDiscrete[][][][];
    private  boolean D_activegenes[][][];
    private int inf,sup;
    
    private  int numberofdatasets = 1;
    private int classlabels[];
    

    public String[] getNames(){
        return genes;
    }

   
    

    public EntropyBasedDicretizerOptimal2Clases(){
        D_ged = null;
        genes = null;
        D_WTHRM = null;
        D_MEDG = null;
      
        D_OrdGEDM = null;
    }

    public EntropyBasedDicretizerOptimal2Clases(int i, int s){
        inf = i;
        sup = s;       
    }

    public void load(float ged[][], int labels[])
    {
        numberofdatasets = 1;
        D_columnas = new int[numberofdatasets];
        D_ged = new float[numberofdatasets][][];

        filas = ged.length;
        D_columnas[0] = ged[0].length;
        D_ged[0] = new float[filas][D_columnas[0]];
        classlabels = new int[labels.length];
        for (int j=0; j<D_columnas[0]; j++){
                classlabels[j] = labels[j];
            }


        for (int i=0; i<filas; i++){

            for (int j=0; j<D_columnas[0]; j++){
                D_ged[0][i][j] = ged[i][j];
            }
        }



        D_MEDG = new float[numberofdatasets][window+1][filas];
        D_activegenes = new boolean[numberofdatasets][window+1][filas];

        

        inf = 0;
        sup = 2;
        
        D_OrdGEDM = new float[numberofdatasets][window+1][filas][];
        D_OrdGEDMDiscrete = new float[numberofdatasets][window+1][filas][];

        for (int k=0; k<numberofdatasets; k++){
            for (int w=0; w<window+1; w++){
                    OrdExpressionDataByGene(D_OrdGEDM[k],D_ged[k], w, D_columnas[k]-w);
                    OrdExpressionDataByGeneforDiscretization(D_OrdGEDMDiscrete[k],D_ged[k], w, D_columnas[k]-w, D_columnas[k]);
                    MeanExpressionDataByGene(D_OrdGEDMDiscrete[k], D_MEDG[k], w,D_columnas[k]-w, D_activegenes[k]);

            }
        }


        D_WTHRM = new threshold[numberofdatasets][][][];
        
        D_WTHRM[0] = new threshold[window+1][2][filas];

        for (int w=0; w<window+1; w++){
            ThresholdMatrixComputation(D_ged[0], D_WTHRM[0],D_OrdGEDM[0], D_MEDG[0],  w, D_columnas[0], D_columnas[0]-w, inf, sup);
        }


    }
    
private void MeanExpressionDataByGene(float OrdGEDMDiscrete[][][], float MEDG[][], int w, int NumOfSamples, boolean activegenes[][])
    /* This procedure calculates the mean gene expression value for each gene. */
    /* The information is stored in the vector MEDGM.                          */
    {
            int sample,gene;
            float m1, m2, obj, var1, var2, prom1, prom2;
            int pos;
            for (gene=0; gene<filas; gene++)
                    {
                        activegenes[w][gene] = true;
                        

                    }
    }
    

    
    
   

   

    private void OrdExpressionDataByGene(float OrdGEDM[][][], float ged[][], int w, int NumOfSamples)
    /* This procedure rearranges the gene expression values of each gene          */
    /* from lower to higher value. The reordering is saved in the matrix OrdGEDM. */
    {
            
            /* Initialization phase: copy GEDM into OrdeGEDM */
            for (int i=0; i<filas;i++)
            {
                OrdGEDM[w][i] = new float[NumOfSamples];
                for (int j=0; j<NumOfSamples;j++)
                {
                    OrdGEDM[w][i][j] = ged[i][j];
                }
            }
            for (int i=0; i<filas;i++)
                 Arrays.sort(OrdGEDM[w][i]);

            return;
    }

    private void OrdExpressionDataByGeneforDiscretization(float OrdGEDMDiscrete[][][], float ged[][], int w, int NumOfSamples, int columnas)
    /* This procedure rearranges the gene expression values of each gene          */
    /* from lower to higher value. The reordering is saved in the matrix OrdGEDM. */
    {

            /* Initialization phase: copy GEDM into OrdeGEDM */
            for (int i=0; i<filas;i++)
            {
                OrdGEDMDiscrete[w][i] = new float[NumOfSamples];
                for (int j=w; j<columnas;j++)
                {
                    OrdGEDMDiscrete[w][i][j-w] = ged[i][j];
                }
            }
            for (int i=0; i<filas;i++)
                 Arrays.sort(OrdGEDMDiscrete[w][i]);

            return;
    }



    private int SV(float ged[][], int kGene, float t, int sample){
        if (ged[kGene][sample]<t)
            return 1;
        else
            return 2;
    }

    private float PartitionEntropy(float ged[][], int pGene, float MEDG[], int columnas, int NumOfSamples, int kGene, float t)
    /* This procedure calculates the entropy of a sample set partition */
    {
      int sample, posS1, negS1, posS2, negS2, cardS1, cardS2;
      float entS1 = 0, entS2 = 0, pEntropy = 0;

      posS1 = 0; negS1 = 0; cardS1 = 0;
      posS2 = 0; negS2 = 0; cardS2 = 0;

      for (sample=0; sample<NumOfSamples; sample++)
            {
               float mean = MEDG[pGene];
               int w = columnas-NumOfSamples;               
               if (SV(ged, kGene,t,sample)==1)
               { /* the sample belong to S1 */
                       cardS1++;
                       if (classlabels[sample]== 0)
                       { /* pGene is upregulated*/
                                    posS1++;
                       }
                       else if (classlabels[sample]== 1)
                       {/* pGene is downregulated */
                                    negS1++;
                       }
               }
               else
               { /* then, the sample belong to S2 */
                       cardS2++;
                       if (classlabels[sample]== 1)
                       { /* pGene is upregulated*/
                                    posS2++;
                       }
                       else if (classlabels[sample]== 0)
                       {/* pGene is downregulated */
                                    negS2++;
                       }
               }
            }

      /* Entropy calculated using equation (3.1), page 55, Mitchell's Machine Learning Book */

      if (cardS1 == 0)
      {
        if (posS2 == 0)
            {
              pEntropy = (float) (-(negS2 / (float) NumOfSamples) * (Math.log(negS2 / (float) NumOfSamples) / Math.log(2)));
            }
        if (negS2 == 0)
            {
              pEntropy = (float) (-(posS2 / (float) NumOfSamples) * (Math.log(posS2 / (float) NumOfSamples) / Math.log(2)));
            }
            if ((posS2 > 0)||(negS2 > 0))
            {
              pEntropy = (float) (-(posS2 / (float) NumOfSamples) * (Math.log(posS2 / (float) NumOfSamples) / Math.log(2)) - (negS2 / (float) NumOfSamples) * (Math.log(negS2 / (float) NumOfSamples) / Math.log(2)));
            }
      }
      if (cardS2 == 0)
      {
        if (posS1 == 0)
            {
              pEntropy = (float) (-(negS1 / (float) NumOfSamples) * (Math.log(negS1 / (float) NumOfSamples) / Math.log(2)));
            }
        if (negS1 == 0)
            {
              pEntropy = (float) (-(posS1 / (float) NumOfSamples) * (Math.log(posS1 / (float) NumOfSamples) / Math.log(2)));
            }
            if ((posS1 > 0)&&(negS1 > 0))
            {
          pEntropy = (float) (-(posS1 / (float) NumOfSamples) * (Math.log(posS1 / (float) NumOfSamples) / Math.log(2)) - (negS1 / (float) NumOfSamples) * (Math.log(negS1 / (float) NumOfSamples) / Math.log(2)));
        }
      }
      if ((cardS1 > 0)&&(cardS2 > 0))
      {
        if (posS1 == 0)
            {
              entS1 = (float) (-(negS1 / (float) NumOfSamples) * (Math.log(negS1 / (float) NumOfSamples) / Math.log(2)));
            }
        if (negS1 == 0)
            {
              entS1 = (float) (-(posS1 / (float) NumOfSamples) * (Math.log(posS1 / (float) NumOfSamples) / Math.log(2)));
            }
            if ((posS1 > 0)&&(negS1 > 0))
            {
          entS1 = (float) (-(posS1 / (float) NumOfSamples) * (Math.log(posS1 / (float) NumOfSamples) / Math.log(2)) - (negS1 / (float) NumOfSamples) * (Math.log(negS1 / (float) NumOfSamples) / Math.log(2)));
        }
        if (posS2 == 0)
            {
              entS2 = (float) (-(negS2 / (float) NumOfSamples) * (Math.log(negS2 / (float) NumOfSamples) / Math.log(2)));
            }
        if (negS2 == 0)
            {
              entS2 = (float) (-(posS2 / (float) NumOfSamples) * (Math.log(posS2 / (float) NumOfSamples) / Math.log(2)));
            }
            if ((posS2 > 0)&&(negS2 > 0))
            {
              entS2 = (float) (-(posS2 / (float) NumOfSamples) * (Math.log(posS2 / (float) NumOfSamples) / Math.log(2)) - (negS2 / (float) NumOfSamples) * (Math.log(negS2 / (float) NumOfSamples) / Math.log(2)));
            }
        /* The next equation is based on page 23 of Kohani's Thesis */
        pEntropy = (cardS1*entS1)/(float)NumOfSamples+(cardS2*entS2)/(float)NumOfSamples;
      }
      return(pEntropy);
    }

    

    private threshold BestThresholdCalculation(float ged[][], float OrdGEDM[][][], float MEDG[][], int w, int pGene, int kGene, int columnas, int NumOfSamples)
    {
      float entropy, BestEntropy, t, BestThreshold;
      int sample;

        /* Initialization: calculation of partition entropy */

        BestThreshold = OrdGEDM[w][kGene][0];
        //BestThreshold = GEDM[kGene][0];
        //Split(kGene,BestThreshold,SV,NumOfSamples);
        BestEntropy = PartitionEntropy(ged, pGene, MEDG[w],columnas, NumOfSamples, kGene, BestThreshold);
        t = BestThreshold;
        /* Searching the best threshold */

        for (sample=1; sample<NumOfSamples; sample++)
        {
          if (OrdGEDM[w][kGene][sample] > t)
	  {
                t = OrdGEDM[w][kGene][sample];
                //t = GEDM[kGene][sample];
                //Split(kGene,t,SV,NumOfSamples);
                entropy = PartitionEntropy(ged, pGene, MEDG[w],columnas, NumOfSamples, kGene, t);
                if (entropy < BestEntropy)
                { /* Then, the entropy of the partition using t is better than current BestEntropy */
                  /* the ideal entropy is 0 */
                    BestEntropy = entropy;
                    BestThreshold = t;
                }
          }
        }
        boolean active = true;
        
        //System.out.print(s1-s2+"\n");
        return(new threshold(BestThreshold, active));
    }

    private void ThresholdMatrixComputation( float ged[][], threshold WTHRM[][][], float OrdGEDM[][][], float MEDG[][], int w, int columnas, int NumOfSamples, int inf, int sup)
    /* This procedure calculates all gene thresholds and this data is saved in the matrix THRM */
    {
            int pGene,kGene;

            for (pGene=inf; pGene<sup; pGene++)
                    {
                     for (kGene=0; kGene<filas; kGene++)
                     {
                      WTHRM[w][pGene-inf][kGene] = BestThresholdCalculation(ged, OrdGEDM, MEDG, w, pGene,kGene,columnas, NumOfSamples);
                     }
                    }
    }

    private float DGEDM(float ged[][], threshold WTHRM[][][], float MEDG[][], int w, int pGene, int i, int j, int inf, int sup){
        float r = 0;
        
        if (i!=pGene){
            if (ged[i][j] < WTHRM[w][pGene-inf][i].t){
                r = -1;
            }
            else
            {
                r = 1;
            }
        }
        else
        {
            
            r = classlabels[j];

        }
        return r;
    }

   
 public float discretize(int row, int col)
 {
     int ind2, ind1;
     ind2 = row;
     if (ind2 == 0)
         ind1 = 1;
     else
         ind1 = 0;



     return DGEDM(D_ged[0], D_WTHRM[0], D_MEDG[0],0,ind1,ind2,col, inf, sup);
 }
   



}
