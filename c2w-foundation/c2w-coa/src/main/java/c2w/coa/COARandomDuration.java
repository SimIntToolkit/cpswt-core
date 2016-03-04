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

package c2w.coa;

import java.util.Random;

import c2w.util.RandomSingleton;

/**
 * Represents a random duration element in the sequence graph.
 * 
 * @author Himanshu Neema
 */
public class COARandomDuration extends COANode {

	private double _lowerBound = 0.0;

	private double _upperBound = 0.0;

	private boolean _isTimerOn = false;

	private double _duration = 0.0;

	private double _endTime = -1.0;

	private Random _rand = null;

	public COARandomDuration(String nodeName, String uniqueID,
			double lowerBound, double upperBound, Random rand) {
		super(nodeName, uniqueID, NODE_TYPE.NODE_RANDOM_DURATION);
		if (lowerBound < 0 || upperBound < 0 || upperBound < lowerBound) {
			throw new IllegalArgumentException(
					"Error! Incorrect bounds on duration provided");
		}
		this._lowerBound = lowerBound;
		this._upperBound = upperBound;

		if (lowerBound == upperBound) {
			this._duration = lowerBound;
		} else {
			if (rand == null) {
				this._rand = RandomSingleton.instance();
			} else {
				this._rand = rand;
			}

			this._duration = this._lowerBound + this._rand.nextDouble()
					* (this._upperBound - this._lowerBound);
		}
	}

	@Override
	public String toString() {
		return super.toString() + ", RandomDuration: " + _duration + "["
				+ this._lowerBound + "," + this._upperBound + "], TimerON: "
				+ _isTimerOn;
	}

	public double getDuration() {
		return _duration;
	}

	public void startTimer(double currentTime) {
		if (currentTime < 0) {
			throw new IllegalArgumentException(
					"Error! Negative currentTime given for: " + this);
		}
		this._endTime = currentTime + this._duration;
		this._isTimerOn = true;
	}

	public double getEndTime() {
		return _endTime;
	}

	public boolean getIsTimerOn() {
		return _isTimerOn;
	}
}
