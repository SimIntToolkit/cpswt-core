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

package edu.vanderbilt.vuisis.cpswt.coa.node;

//import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.vanderbilt.vuisis.cpswt.coa.ArrivedInteraction;
import edu.vanderbilt.vuisis.cpswt.hla.InteractionRoot;

/**
 * Represents an Outcome element in the sequence graph.
 */
public class COAOutcome extends COANode {

    @JsonProperty("interactionName")
	private String interactionClassName;

	private double awaitStartTime = -1;
	private boolean isTimerOn = false;
	private ArrivedInteraction lastArrivedInteraction = null;

	COAOutcome() {
	    super(COANodeType.Outcome);
    }

	public COAOutcome(String nodeName, String uniqueID, String interactionClassName) {
		super(nodeName, uniqueID, COANodeType.Outcome);

		this.interactionClassName = interactionClassName;
	}

	@Override
	public String toString() {
		return super.toString() + ", Interaction: " + interactionClassName
				+ ", TimerON: " + isTimerOn + ", AwaitStartTime: "
				+ awaitStartTime;
	}

	public String getInteractionClassName() {
		return interactionClassName;
	}

	public double getAwaitStartTime() {
		return awaitStartTime;
	}
	public boolean getIsTimerOn() {
		return isTimerOn;
	}

	public ArrivedInteraction getLastArrivedInteraction() {
		return lastArrivedInteraction;
	}

	public InteractionRoot getLastInteraction() {
		return lastArrivedInteraction.getInteractionRoot();
	}

	public void setLastArrivedInteraction(ArrivedInteraction arrivedInteraction) {
		if (arrivedInteraction == null) {
			throw new IllegalArgumentException(
					"Error! NULL interaction was set in the COA Outcome!");
		}

		this.lastArrivedInteraction = arrivedInteraction;
	}

	public void startTimer(double currentTime) {
		if (currentTime < 0) {
			throw new IllegalArgumentException(
					"Error! Negative values given while setting awaitStartTime for: "
							+ this);
		}
		this.awaitStartTime = currentTime;
		this.isTimerOn = true;
	}
}
