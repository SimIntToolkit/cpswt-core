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

import java.util.Random;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.vanderbilt.vuisis.cpswt.utils.RandomSingleton;

/**
 * Represents a random duration element in the sequence graph.
 */
public class COARandomDuration extends COADuration {

    @JsonProperty("lowerBound")
	private double lowerBound = 0.0;

    @JsonProperty("upperBound")
	private double upperBound = 0.0;
	private Random rand = null;

	COARandomDuration() {
	    super(COANodeType.RandomDur);
    }

	public COARandomDuration(String nodeName, String uniqueID,
			double lowerBound, double upperBound, Random rand) {
		super(nodeName, uniqueID, 0.0, COANodeType.RandomDur);

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

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }
}
