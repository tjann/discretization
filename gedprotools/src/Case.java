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


/**
 * Represents attribute instance: a pair of an attribute value and a class to
 * which this attribute value belongs.
 */
public final class Case {
    final int classId;
    final double attributeValue;

    /**
     * Constructor.
     * @param classId Class to which this attribute value belongs.
     * @param attributeValue Attribute value.
     */
    public Case(final int classId, final double attributeValue) {
        this.classId = classId;
        this.attributeValue = attributeValue;
    }

    public int hashCode() {
        return classId;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Object
     * of class Case are equal iff all member fields are equal.
     *
     * @param obj  the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(final Object obj) {
        if (obj instanceof Case) {
            final Case c = (Case) obj;
            return c.classId == classId && c.attributeValue == attributeValue;
        } else {
            return false;
        }
    }
}
