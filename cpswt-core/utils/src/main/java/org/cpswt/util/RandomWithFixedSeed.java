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
