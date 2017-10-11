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
 */

package org.cpswt.coa.node;

import java.util.Random;

import org.cpswt.coa.enums.COANodeType;
import org.cpswt.utils.RandomSingleton;

/**
 * Represents a random duration element in the sequence graph.
 */
public class COARandomDuration extends COADuration {

	private double lowerBound = 0.0;
	private double upperBound = 0.0;
	private Random rand = null;

	public COARandomDuration(String nodeName, String uniqueID,
			double lowerBound, double upperBound, Random rand) {
		super(nodeName, uniqueID, 0.0, COANodeType.RandomDuration);

		if (lowerBound < 0 || upperBound < 0 || upperBound < lowerBound) {
			throw new IllegalArgumentException(
					"Error! Incorrect bounds on duration provided");
		}

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		if (lowerBound == upperBound) {
			this.duration = lowerBound;
		} else {
			if (rand == null) {
				this.rand = RandomSingleton.instance();
			} else {
				this.rand = rand;
			}

			this.duration = this.lowerBound + this.rand.nextDouble()
					* (this.upperBound - this.lowerBound);
		}
	}

	@Override
	public String toString() {
		return super.toString() + ", RandomDuration: " + this.duration + "["
				+ this.lowerBound + "," + this.upperBound + "], TimerON: "
				+ this.isTimerOn;
	}
}
