/*
 * Certain portions of this software are Copyright (C) 2006-present
 * Vanderbilt University, Institute for Software Integrated Systems.
 *
 * Certain portions of this software are contributed as a public service by
 * The National Institute of Standards and Technology (NIST) and are not
 * subject to U.S. Copyright.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above Vanderbilt University copyright notice, NIST contribution
 * notice and this permission and disclaimer notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. THE AUTHORS OR COPYRIGHT HOLDERS SHALL NOT HAVE
 * ANY OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 */

package org.cpswt.util;

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
