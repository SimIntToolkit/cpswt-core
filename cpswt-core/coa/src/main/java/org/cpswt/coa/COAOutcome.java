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

package org.cpswt.coa;

//import org.cpswt.hla.InteractionRoot;

/**
 * Represents an Outcome element in the sequence graph.
 * 
 * @author Himanshu Neema
 */
public class COAOutcome extends COANode {

	private String _interactionClassName;

	private int _interactionClassHandle = 0;

	private double _awaitStartTime = -1;

	private boolean _isTimerOn = false;

	private Object _lastArrivedIntr = null;

	public COAOutcome(String nodeName, String uniqueID,
			String interactionClassName) {
		super(nodeName, uniqueID, NODE_TYPE.NODE_OUTCOME);

		this._interactionClassName = interactionClassName;
	}

	@Override
	public String toString() {
		return super.toString() + ", Interaction: " + _interactionClassName
				+ ", TimerON: " + _isTimerOn + ", AwaitStartTime: "
				+ _awaitStartTime;
	}

	public String getInteractionClassName() {
		return _interactionClassName;
	}

	public int getInteractionClassHandle() {
		return _interactionClassHandle;
	}

	public double getAwaitStartTime() {
		return _awaitStartTime;
	}

	public boolean getIsTimerOn() {
		return _isTimerOn;
	}

	public Object getLastArrivedInteraction() {
		return _lastArrivedIntr;
	}

	public void setLastArrivedInteraction(Object interactionRoot) {
		if (interactionRoot == null) {
			throw new IllegalArgumentException(
					"Error! NULL interaction was set in the COA Outcome!");
		}

		this._lastArrivedIntr = interactionRoot;
	}

	public void setInteractionClassHandle(int interactionClassHandle) {
		if (interactionClassHandle < 0) {
			throw new IllegalArgumentException(
					"Error! Negative values given while configuring: " + this);
		}
		this._interactionClassHandle = interactionClassHandle;
	}

	public void startTimer(double currentTime) {
		if (currentTime < 0) {
			throw new IllegalArgumentException(
					"Error! Negative values given while setting awaitStartTime for: "
							+ this);
		}
		this._awaitStartTime = currentTime;
		this._isTimerOn = true;
	}
}
