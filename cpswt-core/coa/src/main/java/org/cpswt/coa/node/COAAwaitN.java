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
 *
 * @author Himanshu Neema
 */

package org.cpswt.coa.node;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an AwaitN COA element in the sequence graph.
 */
public class COAAwaitN extends COANode {

	@JsonProperty("minBranchesToAwait")
	private int numBranchesToFinish = 0;
	private int numBranchesFinished = 0;

	COAAwaitN() {
		super(COANodeType.AwaitN);
	}

	public COAAwaitN(String nodeName, String uniqueID, int numBranchesToFinish) {
		super(nodeName, uniqueID, COANodeType.AwaitN);

		this.numBranchesToFinish = numBranchesToFinish;
	}

	@Override
	public String toString() {
		return super.toString() + ", No. of branches to finish: "
				+ numBranchesToFinish + ", No. of branches already finished: "
				+ numBranchesFinished;
	}

	public int getNumBranchesToFinish() {
		return numBranchesToFinish;
	}
	public void setNumBranchesToFinish(int numBranchesToFinish) {
		this.numBranchesToFinish = numBranchesToFinish;
	}

	public void incrementBranchesFinished() {
		numBranchesFinished++;
	}
	public int getNumBranchesFinished() {
		return numBranchesFinished;
	}
	public boolean getIsRequiredNumOfBranchesFinished() {
		return numBranchesFinished >= numBranchesToFinish;
	}
}
