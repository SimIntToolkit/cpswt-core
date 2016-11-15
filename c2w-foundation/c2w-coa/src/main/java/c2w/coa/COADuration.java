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

package c2w.coa;

/**
 * Represents a duration element in the sequence graph.
 */
public class COADuration extends COANode {

	protected double _duration = 0.0;
	protected double _endTime = -1.0;
	protected boolean _isTimerOn = false;

	public COADuration(String nodeName, String uniqueID, double duration) {
		this(nodeName, uniqueID, duration, NODE_TYPE.NODE_DURATION);
	}

	protected COADuration(String nodeName, String uniqueID, double duration, NODE_TYPE nodeType) {
		super(nodeName, uniqueID, nodeType);
		if (duration < 0) {
			throw new IllegalArgumentException(
					"Error! Negative duration not permitted");
		}
		this._duration = duration;
	}

	@Override
	public String toString() {
		return super.toString() + ", Duration: " + _duration + ", TimerON: "
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
