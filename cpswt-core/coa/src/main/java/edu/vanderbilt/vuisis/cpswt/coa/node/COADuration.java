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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a duration element in the sequence graph.
 */
public class COADuration extends COANode {

    @JsonProperty("time")
	protected double duration = 0.0;

	protected double endTime = -1.0;
	protected boolean isEndTimeSet = false;

	COADuration() {
	    super(COANodeType.Dur);
    }

    COADuration(COANodeType nodeType) {
	    super(nodeType);
    }

	public COADuration(String nodeName, String uniqueID, double duration) {
		this(nodeName, uniqueID, duration, COANodeType.Dur);
	}

	protected COADuration(String nodeName, String uniqueID, double duration, COANodeType nodeType) {
		super(nodeName, uniqueID, nodeType);
		if (duration < 0) {
			throw new IllegalArgumentException("Error! Negative duration not permitted");
		}
		this.duration = duration;
	}

	@Override
	public String toString() {
		return super.toString() + ", Duration: " + duration + ", TimerON: "
				+ isEndTimeSet;
	}

	@Override
	public void initializeNode() {
		super.initializeNode();
		isEndTimeSet = false;
	}
	public double getDuration() {
		return duration;
	}
    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setEndTime(double currentTime) {
		if (currentTime < 0) {
			throw new IllegalArgumentException("Error! Negative currentTime given for: " + this);
		}
		this.endTime = currentTime + this.duration;
		this.isEndTimeSet = true;
	}

	public double getEndTime() {
		return endTime;
	}

	public boolean isEndTimeSet() {
		return isEndTimeSet;
	}
}
