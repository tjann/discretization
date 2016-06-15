package GEDPROTOOLS;

/**
 *  JBNC - Bayesian Network Classifiers Toolbox <p>
 *
 *  Latest release available at http://sourceforge.net/projects/jbnc/ <p>
 *
 *  Copyright (C) 1999-2003 Jarek Sacha <p>
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


import java.util.ArrayList;

/**
 * Recursive implementation of entropy discretization algorithm:<p>
 *
 * U.M. Fayyad and
 * K.B Irani "Multi-interval discretizationn of continous-valued attributes
 * for classification learning", In <i>Proc. Thirtheenth International
 * Joint Conference on Artificial Intelligence</i>, Chambery, France.
 * Morgan Kauffman, pp. 1022-1027, 1993.
 *
 * @author  Jarek Sacha
 * @version $ Revision: $
 */

final class Subset {

    private static final boolean USE_AVERAGE_BOUNDARY = false;
    private static final double INV_LOG_2 = 1.0 / Math.log(2);

    private static final class IndexEntropyPair {
        public int index;
        public double partitionEntropy;
        public double entropyS1;
        public double entropyS2;
        public int nbActiveClassesS1;
        public int nbActiveClassesS2;
    }


    private final Case[] cases;
    private final int[] classCount;

    public Subset(final Case[] cases, final int[] classCount) {
        this.cases = cases;
        this.classCount = classCount;
    }

    /**
     * Recursively partition subset <case>s</case>. It assumes that
     * <case>s</case> are sorted by attribute value.
     *
     * @return LIst of partition boundaries. Elements are of type java.lang.Double.
     */
    public ArrayList partitions() {

        // Best partition index for s
        final IndexEntropyPair bestPartition = bestPartition();

        final ArrayList part = new ArrayList();
        if (bestPartition != null) {
            final double partitionValue;
            if (USE_AVERAGE_BOUNDARY) {
                partitionValue =
                        (cases[bestPartition.index].attributeValue
                        + cases[bestPartition.index + 1].attributeValue) / 2;
            } else {
                partitionValue = cases[bestPartition.index].attributeValue;
            }
            part.add(new Double(partitionValue));

            // Partition lower subset
            {
                final Case[] s1 = new Case[bestPartition.index + 1];
                final int[] classCountS1 = new int[classCount.length];
                for (int i = 0; i <= bestPartition.index; ++i) {
                    final Case thisCase = cases[i];
                    ++classCountS1[thisCase.classId];
                    s1[i] = thisCase;
                }
                final Subset subset1 = new Subset(s1, classCountS1);
                final ArrayList part1 = subset1.partitions();
                if (part1 != null) {
                    part.addAll(0, part1);
                }
            }

            // Partition upper subset
            {
                final Case[] s2 = new Case[cases.length - bestPartition.index - 1];
                final int[] classCountS2 = new int[classCount.length];
                for (int i = bestPartition.index + 1; i < cases.length; ++i) {
                    final Case thisCase = cases[i];
                    ++classCountS2[thisCase.classId];
                    s2[i - bestPartition.index - 1] = thisCase;
                }
                final Subset subset2 = new Subset(s2, classCountS2);
                final ArrayList part2 = subset2.partitions();
                if (part2 != null) {
                    part.addAll(part.size(), part2);
                }
            }
        }

        return part;
    }


    final private IndexEntropyPair bestPartition() {

        // Find candidate boundary points
        final ArrayList boundaryIndexes = boundaryIndexes();

        // Select best partition (boundary point)
        IndexEntropyPair bestIep = new IndexEntropyPair();
        bestIep.partitionEntropy = Double.POSITIVE_INFINITY;
        for (int i = 0; i < boundaryIndexes.size(); i++) {
            final IndexEntropyPair iep = (IndexEntropyPair) boundaryIndexes.get(i);
            if (iep.partitionEntropy < bestIep.partitionEntropy) {
                bestIep = iep;
            }
        }

        // Verify that partitioning is acceptable
        final double ent = entropy(classCount, cases.length);
        final double gain = ent - bestIep.partitionEntropy;

        final double ent1 = bestIep.entropyS1;
        final double ent2 = bestIep.entropyS2;

        final int k = numberOfActiveClasses(classCount);
        final int k1 = bestIep.nbActiveClassesS1;
        final int k2 = bestIep.nbActiveClassesS2;

        final double delta = Math.log(Math.pow(3, k) - 2) / Math.log(2) -
                (k * ent - k1 * ent1 - k2 * ent2);

        final double n = cases.length;
        final double cond = Math.log(n - 1) / Math.log(2) / n + delta / n;

        if (gain > cond) {
            // Accept partition
            return bestIep;
        } else {
            // Reject partiution
            return null;
        }


    }

    static final private int numberOfActiveClasses(final int[] classCount) {
        int nbActiveClasses = 0;
        for (int i = 0; i < classCount.length; i++) {
            if (classCount[i] > 0) {
                ++nbActiveClasses;
            }
        }

        return nbActiveClasses;
    }

    /**
     * Find boundary points. Input array of cases will be resorted by attribute
     * value. A boundary point is eaxh index were at the next index both class
     * id and attribute value are different. Number of boundary points is
     * always less or equal number of cases minus one.
     *
     * @return indexes of bounrady points in the resorted list of cases.
     */
    final private ArrayList boundaryIndexes() {

        final int[] classCountS1 = new int[classCount.length];
        final int[] classCountS2 = new int[classCount.length];
        System.arraycopy(classCount, 0, classCountS2, 0, classCountS1.length);

        Case thisCase = cases[0];
        ++classCountS1[thisCase.classId];
        --classCountS2[thisCase.classId];

        final ArrayList boundaryCases = new ArrayList();
        for (int i = 1; i < cases.length; ++i) {
            final Case nextCase = cases[i];
            // Check boundary candidates
            if (thisCase.attributeValue != nextCase.attributeValue) {
                final IndexEntropyPair iep = new IndexEntropyPair();
                iep.index = i - 1;
                iep.entropyS1 = entropy(classCountS1, i);
                iep.entropyS2 = entropy(classCountS2, cases.length - i);


                final int nbS1Cases = i;
                final int nbS2Cases = cases.length - nbS1Cases;
                iep.partitionEntropy = nbS1Cases / (double) cases.length * iep.entropyS1
                        + nbS2Cases / (double) cases.length * iep.entropyS2;

                iep.nbActiveClassesS1 = numberOfActiveClasses(classCountS1);
                iep.nbActiveClassesS2 = numberOfActiveClasses(classCountS2);

                boundaryCases.add(iep);
            }
            thisCase = nextCase;
            ++classCountS1[thisCase.classId];
            --classCountS2[thisCase.classId];
        }

        return boundaryCases;
    }

    /*
     *
     */
    final private static double entropy(final int[] classCount, final int nbCases) {
        double ent = 0;
        for (int i = 0; i < classCount.length; ++i) {
            final int count = classCount[i];
            if (count > 0) {
                final double p = count / (double) nbCases;
                ent -= p * Math.log(p);
            }
        }
        ent *= INV_LOG_2;

        return ent;
    }
}
