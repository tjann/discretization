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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Implements entropy based discretisation algotithm by Fayyad and Irani.<p>
 * <p/>
 * U.M. Fayyad and K.B Irani "Multi-interval discretizationn of contnous-valued attributes for classification learning",
 * In <i>Proc. Thirtheenth International Joint Conference on Artificial Intelligence</i>, Chambery, France. Morgan
 * Kauffman, pp. 1022-1027, 1993.  <p>
 * <p/>
 * Discretization process maps a continuous value into a fixed set of partitions. Partitioning is uniquely described by
 * a set of partition boundaries. A first partition corresponds to interval from negative infinity to first partition
 * point (inclusively). Second partition streaches from first boundary point (exclusively) to second boundary point
 * (inclusively). And so on. Last partition streaches from last boundary point (exclusively) to positive infinity.<p>
 * <p/>
 * This disretization algorithm is intended for discetization of attributes as a preprocessing step for a
 * classification. It requires knowledge of a class for each case (nstance) used to build discretizer. A discretizer is
 * build for each attribute separately.<p>
 * <p/>
 * To create a discretizer use method <code>buildDiscretizer()</code>. To applay the discretizer use method
 * <code>discretize()</code>.
 */
public final class EntropyDiscretizer implements Serializable {
    /**
     * Represent partition boundaries for discretization.
     */
    private double[] partitionBoundaries;

    /**
     * Partition names. Number of elements is one more that in <code>partitionBoundaries</code>.
     */
    private String[] partitionNames;

    private int nbClasses = -1;


    /**
     * Performs construction of the discretizer for a set of cases (class + continous attribute pairs).
     *
     * @param cases training data for creation of a discretizer.
     */
    public void buildDiscretizer(final Case[] cases) {

        if (cases == null) {
            throw new IllegalArgumentException("Argument cases cannot be null.");
        }

        if (cases.length <= 1) {
            throw new IllegalArgumentException("Number of elements cases in argument cases must be greater than 1.");
        }

        final Case[] cases_ = new Case[cases.length];
        System.arraycopy(cases, 0, cases_, 0, cases_.length);


        // Find number of classes
        final int n = maxClassId(cases_) + 1;
        if (n <= 1) {
            throw new IllegalArgumentException("Number of classes in argument cases must be greater than 1.");
        }
        nbClasses = n;

        final int[] classCount = classCount(cases_);

        // Sort cases by attribute
        Arrays.sort(cases_, new ValueClassCaseComparator());

        // Recursively partition s
        final Subset s = new Subset(cases_, classCount);
        final ArrayList p = s.partitions();

        partitionBoundaries = new double[p.size()];
        for (int i = 0; i < partitionBoundaries.length; i++) {
            partitionBoundaries[i] = ((Double) p.get(i)).doubleValue();
        }

        // Create partition names
        partitionNames = new String[partitionBoundaries.length + 1];
        if (partitionNames.length == 1) {
            partitionNames[0] = "(-inf,+inf)";
        } else {
            partitionNames[0] = "(-inf," + partitionBoundaries[0] + "]";
            for (int i = 1; i < partitionBoundaries.length; i++) {
                partitionNames[i] = "(" + partitionBoundaries[i - 1] + "-" +
                        +partitionBoundaries[i] + "]";
            }
            partitionNames[partitionBoundaries.length] =
                    "(" + partitionBoundaries[partitionBoundaries.length - 1] + ",+inf)";
        }
    }

    /**
     * Discretize single continous value. A discretizer must be build before calling this method.
     *
     * @param v value to discretize.
     * @return label of a discretizer value.
     */
    public int discretize(final double v) {
        for (int i = 0; i < partitionBoundaries.length; i++) {
            if (v < partitionBoundaries[i]) {
                return i;
            }
        }

        return partitionBoundaries.length;
    }

    /**
     * Return array representing partition boundaries. The first partition corresponds to interval from negative
     * infinity to first partition point (inclusively). The second partition streaches from first boundary point
     * (exclusively) to second boundary point (inclusively). And so on. The last partition streaches from last boundary
     * point (exclusively) to positive infinity.
     *
     * @return Array representing partition boundaries.
     */
    public double[] getPartitionBoundaries() {
        final double[] p = new double[partitionBoundaries.length];
        System.arraycopy(partitionBoundaries, 0, p, 0, p.length);
        return p;
    }

    /**
     * Return symbolic names describing discretization partitions. Number of partitions is one more than number of
     * partition boundaries.
     *
     * @return Names describing discretization partitions.
     * @see #getPartitionBoundaries()
     */
    public String[] getPartitionNames() {
        final String[] names = new String[partitionNames.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = partitionNames[i];
        }

        return names;
    }


    /**
     * Largest class ID in case set <code>s</code>. Class IDs must be greater or equal 0.
     *
     * @param s
     * @return Largest class ID.
     */
    private static int maxClassId(final Case[] s) {
        int maxClassId = -1;
        for (int i = 0; i < s.length; i++) {
            final int classId = s[i].classId;
            if (classId < 0) {
                throw new IllegalArgumentException("Class ID for case " + i +
                        " is less then zero (" + classId + ").");
            }

            if (classId > maxClassId) {
                maxClassId = classId;
            }
        }

        return maxClassId;
    }


    /*
     *
     */
    private int[] classCount(final Case[] s) {
        final int[] classCount = new int[nbClasses];
        for (int i = 0; i < s.length; i++) {
            ++classCount[s[i].classId];
        }

        return classCount;
    }


    /**
     * Enables ordering of cases by attribute value. If two attributeValues are equal cases are ordered by class
     * indexes.
     */
    private static final class ValueClassCaseComparator implements Comparator {
        public int compare(final Object o1, final Object o2) {
            final Case c1 = (Case) o1;
            final Case c2 = (Case) o2;
            if (c1.attributeValue > c2.attributeValue) {
                return 1;
            } else if (c1.attributeValue < c2.attributeValue) {
                return -1;
            } else if (c1.classId > c1.classId) {
                return 1;
            } else if (c1.classId < c1.classId) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
