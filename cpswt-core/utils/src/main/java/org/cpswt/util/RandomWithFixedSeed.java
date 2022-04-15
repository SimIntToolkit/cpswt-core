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

import java.util.Random;

/**
 * RandomWithFixedSeed uses a given seed value for generating random numbers.
 * The seed value is pre-determined for an experiment and is used for elements
 * that require repeatability. A good example may be duration element of
 * sequence models, which may need to have same value (though random) in
 * multiple runs of the same experiment.
 */
public class RandomWithFixedSeed {

	private static Random _rand = null;

	public static void init(long seed) {
		if (_rand == null) {
			_rand = new Random(seed);
		}
	}

	/**
	 * Gives back the instance of the Random number generator (that was earlier
	 * created with a given seed value).
	 */
	public static Random instance() {
		return _rand;
	}

	public static void main(String[] args) {
		RandomWithFixedSeed.init(25);
		Random rand1 = RandomWithFixedSeed.instance();
		String strRandomVals1 = "";
		for (int i = 0; i < 1000; i++) {
			int val = 1 + rand1.nextInt(100);
			strRandomVals1 += val + ",";
		}
		System.out.println(strRandomVals1);
		System.out.println();

		RandomWithFixedSeed.init(25);
		Random rand2 = RandomWithFixedSeed.instance();
		String strRandomVals2 = "";
		for (int i = 0; i < 1000; i++) {
			int val = 1 + rand2.nextInt(100);
			strRandomVals2 += val + ",";
		}
		System.out.println(strRandomVals2);

		if (strRandomVals1.compareTo(strRandomVals2) == 0) {
			System.out
					.println("\nSAME RANDOM NUMBERS GENERATED WITH SAME SEED VALUE!");
		} else {
			System.out
					.println("\nDIFFERENT RANDOM NUMBERS GENERATED WITH SAME SEED VALUE!");
		}
	}
}
