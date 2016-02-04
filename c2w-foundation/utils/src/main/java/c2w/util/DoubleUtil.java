/*
 * Copyright (c) 2008, Institute for Software Integrated Systems, Vanderbilt University
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE VANDERBILT UNIVERSITY BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE VANDERBILT
 * UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE VANDERBILT UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE VANDERBILT UNIVERSITY HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 * @author Himanshu Neema
 */

package c2w.util;

/**
 * Some utility methods for double numbers. Comparisons done up to fourth
 * decimal place.
 * 
 * @author Himanshu Neema
 */
public class DoubleUtil {

    private static double EPSILON = 0.0001;

    public static boolean isAGreaterThanB(double A, double B) {
        double aMinusB = A - B;
        if (aMinusB > EPSILON) {
            return true;
        }
        return false;
    }

    public static boolean isALessThanB(double A, double B) {
        double bMinusA = B - A;
        if (bMinusA > EPSILON) {
            return true;
        }
        return false;
    }

    public static boolean isAEqualToB(double A, double B) {
        double absAMinusB = StrictMath.abs(A - B);
        if (absAMinusB <= EPSILON) {
            return true;
        }
        return false;
    }

    public static boolean isAGreaterThanOrEqualToB(double A, double B) {
        double aMinusB = A - B;
        double sign = StrictMath.signum(aMinusB);
        if (sign >= 0) {
            return true;
        }
        return false;
    }

    public static boolean isALessThanOrEqualToB(double A, double B) {
        double aMinusB = A - B;
        double sign = StrictMath.signum(aMinusB);
        if (sign <= 0) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        double d1 = 1.0050002;
        double d2 = 1.0050001;
        System.out.println("isAGreaterThanB(" + d1 + ", " + d2 + ") = "
                + isAGreaterThanB(d1, d2));
        System.out.println("isAGreaterThanB(" + d1 * 10000 + ", " + d2 * 10000
                + ") = " + isAGreaterThanB(d1 * 10000, d2 * 10000));
        System.out.println("isALessThanB(" + d2 + ", " + d1 + ") = "
                + isALessThanB(d2, d1));
        System.out.println("isALessThanB(" + d2 * 10000 + ", " + d1 * 10000
                + ") = " + isALessThanB(d2 * 10000, d1 * 10000));
        System.out.println("isAGreaterThanOrEqualToB(" + d1 + ", " + d2
                + ") = " + isAGreaterThanOrEqualToB(d1, d2));
        System.out.println("isALessThanOrEqualToB(" + d2 + ", " + d1 + ") = "
                + isALessThanOrEqualToB(d2, d1));
        System.out.println("isAEqualToB(" + d1 + ", " + d2 + ") = "
                + isAEqualToB(d1, d2));
        System.out.println("isAEqualToB(" + d2 + ", " + d1 + ") = "
                + isAEqualToB(d2, d1));
        System.out.println("isAEqualToB(" + d1 * 10000 + ", " + d2 * 10000
                + ") = " + isAEqualToB(d1 * 10000, d2 * 10000));
    }
}
