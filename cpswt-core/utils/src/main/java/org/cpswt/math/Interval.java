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
 * @author Gyorgy Balogh
 */

package org.cpswt.math;

/**
 * This class implements the interval arithmetics of real numbers.
 * An interval represents an unknown number of this interval.
 * When performing arithmetic operations, the smallest interval
 * is returned that could possibly contain all elements that
 * could result.  
 */
public final class Interval
{
	/**
	 * The lower and upper bounds of the interval (inclusively)
	 */
	public double min, max;

	/**
	 * Constructs an empty interval, with 0.0 for min and max.
	 */
	public Interval()
	{
		min = 0.0;
		max = 0.0;
	}
    
    /**
     * Returns true if the intervall is a point
     */
    public boolean isPoint()
    {
        return (max == min);
    }

	/**
	 * Constructs an interval
	 * @param min The lower bound of the interval
	 * @param max The upper bound of the interval
	 * @throws IllegalArgumentException if <code>max &le; min</code>.
	 */
	public Interval(double min, double max)
	{
		if( max < min )
			throw new IllegalArgumentException("Min must be smaller than or equal to max");
			
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Copy constructor
	 */
	public Interval(Interval interval)
	{
		min = interval.min;
		max = interval.max;
	}
	
	/**
	 * Returns the length of this interval (max-min)
	 */
	public double length()
	{
		return max - min; 
	}
	
	/**
	 * Returns the value halfway between min and max
	 */
	public double midValue()
	{
		return (min + max) / 2.0;
	}
	
	/**
	 * Adds the specified interval to this interval.
	 */
	public void add(Interval interval)
	{
		min += interval.min;
		max += interval.max;
	}

	/**
	 * Adds a scalar to the interval
	 */
	public void add(double value)
	{
		min += value;
		max += value;
	}

	/**
	 * Returns the sum of two intervals.
	 */	
	public static Interval sum(Interval first, Interval second)
	{
		return new Interval(first.min + second.min, first.max + second.max);
	}
	
	/**
	 * Returns the sum of an interval and a scalar
	 */	
	public static Interval sum(Interval first, double second)
	{
		return new Interval(first.min + second,
			first.max + second);
	}

	/**
	 * Assume that this interval is a sum of intervals, including
	 * the specified part interval. This method calculates the
	 * sum without the specified part interval. So basically, this 
	 * operation is a substraction, but min,max values interchanged.
	 */
	public void reverseAdd(Interval part)
	{
		min -= part.min;
		max -= part.max;
	}
	
	/**
	 * Substracts the specified interval to this interval.
	 */
	public void substract(Interval interval)
	{
		min -= interval.max;
		max -= interval.min;
	}

	/**
	 * Substracts a scalar to the interval
	 */
	public void substract(double value)
	{
		min -= value;
		max -= value;
	}

	/**
	 * Returns the difference of two intervals.
	 */	
	public static Interval difference(Interval first, Interval second)
	{
		return new Interval(first.min - second.max, first.max - second.min);
	}
	
	/**
	 * Returns the difference of an interval and a scalar
	 */	
	public static Interval difference(Interval first, double second)
	{
		return new Interval(first.min - second, first.max - second);
	}
	
	/**
	 * Returns the difference of a scalar and an interval
	 */	
	public static Interval difference(double first, Interval second)
	{
		return new Interval(first - second.max, first - second.min);
	}	

	/**
	 * Sets the value of this interval (copies)
	 */
	public void set(Interval interval)
	{
		min = interval.min;
		max = interval.max;
	}

	/**
	 * Sets the value of this interval
	 */
	public void set(double min, double max)
	{
		if( max < min )
			throw new IllegalArgumentException("Min must be smaller than or equal to max");

		this.min = min;
		this.max = max;
	}

	/**
	 * Returns true if there exists a number that is in both intervals.
	 */
	public boolean intersects(Interval interval)
	{
		return interval.min <= max && min <= interval.max; 
	}

	/**
	 * Calculates the intersection of the two interval. It is the caller
	 * responsibility to make sure that the two intervals have an intersection.
	 */
	public void intersection(Interval interval)
	{
		if( min < interval.min )
			min = interval.min;
		if( max > interval.max )
			max = interval.max;
	}

	/**
	 * Returns true if the specified value is in the interval
	 */
	public boolean contains(double value)
	{
		return min <= value && value <= max;
	}

	/**
	 * Returns true if this interval contains the specified interval.
	 */
	public boolean contains(Interval interval)
	{
		return min <= interval.min && interval.max <= max;		
	}

	/**
	 * Returns true if this interval properly contains the 
	 * specified interval, that is the endpoints are not the same.
	 */
	public boolean properlyContains(Interval interval)
	{
		return min < interval.min && interval.max < max;
	}

	/**
	 * Returns true if the half open [-,-) interval contains the
	 * specified value.
	 */
	public boolean leftContains(double value)
	{
		return min <= value && value < max;
	}
	
	/**
	 * Computes the product of two intervals. The smallest interval is
	 * returned that contains all products of elements of the two intervals.
	 */
	public void multiply(Interval interval)
	{
		if( 0.0 <= interval.min )
		{
			if( 0.0 <= min )			// [5,10] * [5,10] 
			{
				min *= interval.min;
				max *= interval.max;
			}
			else if( max <= 0.0 )		// [-10,-5] * [5,10]
			{
				min *= interval.max;
				max *= interval.min;
			}
			else						// [-5,10] * [5,10]
			{
				min *= interval.max;
				max *= interval.max;
			}
		}
		else if( interval.max <= 0.0 )
		{
			double oldMin = min;
			
			if( 0.0 <= min )			// [5,10] * [-10,-5] 
			{
				min = max * interval.min;
				max = oldMin * interval.max;
			}
			else if( max <= 0.0 )		// [-10,-5] * [-10,-5]
			{
				min = max * interval.max;
				max = oldMin * interval.min;
			}
			else						// [-5,10] * [-10,-5]
			{
				min = max * interval.min;
				max = oldMin * interval.min;
			}
		}
		else
		{
			if( 0.0 <= min )			// [5,10] * [-5,10] 
			{
				min = max * interval.min;
				max *= interval.max;
			}
			else if( max <= 0.0 )		// [-10,-5] * [-5,10]
			{
				max = min * interval.min;
				min *= interval.max;
			}
			else						// [-5,10] * [-5,10]
			{
				double a = max * interval.min;
				double b = min * interval.min;

				if( (min *= interval.max) > a )
					min = a;

				if( (max *= interval.max) < b )
					max = b;
			}
		}
	}
		
	/**
	 * Multiplies this interval by a scalar
	 */
	public void multiply(double d)
	{
		if( d >= 0.0 )
		{
			min *= d;
			max *= d;
		}
		else
		{
			double oldMin = min;
			min = d * max;
			max = d * oldMin;
		}
	}

	/**
	 * Returns the product of two intervals.
	 */
	public static Interval product(Interval first, Interval second)
	{
		Interval interval = new Interval(first);
		interval.multiply(second);
		return interval;
	} 

	/**
	 * Returns the product of an interval and a scalar
	 */
	public static Interval product(Interval first, double second)
	{
		Interval interval = new Interval(first);
		interval.multiply(second);
		return interval;
	} 

	/**
	 * Computes the quotient: this interval divided by the specified interval. 
	 * The smallest interval is returned that contains all quotients of elements 
	 * of the two intervals.
	 */
	public void divide(Interval interval)
	{
		if( 0.0 < interval.min )
		{
			if( 0.0 <= min )			// [5,10] * [5,10] 
			{
				min /= interval.max;
				max /= interval.min;
			}
			else if( max <= 0.0 )		// [-10,-5] * [5,10]
			{
				min /= interval.min;
				max /= interval.max;
			}
			else						// [-5,10] * [5,10]
			{
				min /= interval.min;
				max /= interval.min;
			}
		}
		else if( interval.max < 0.0 )
		{
			double oldMin = min;
			
			if( 0.0 <= min )			// [5,10] * [-10,-5] 
			{
				min = max / interval.max;
				max = oldMin / interval.min;
			}
			else if( max <= 0.0 )		// [-10,-5] * [-10,-5]
			{
				min = max / interval.min;
				max = oldMin / interval.max;
			}
			else						// [-5,10] * [-10,-5]
			{
				min = max / interval.max;
				max = oldMin / interval.max;
			}
		}
		else
		{
			if( 0.0 <= min )			// [5,10] * [-5,10] 
			{
				min = max / interval.min;
				max = Double.POSITIVE_INFINITY;
			}
			else if( max <= 0.0 )		// [-10,-5] * [-5,10]
			{
				min = Double.NEGATIVE_INFINITY;
				max = min / interval.min;
			}
			else						// [-5,10] * [-5,10]
			{
				min = Double.NEGATIVE_INFINITY;
				max = Double.POSITIVE_INFINITY;
			}
		}
	}

	/**
	 * Divides this interval by a scalar
	 */
	public void divide(double value)
	{
		if( value > 0 )
		{
			min /= value;
			max /= value;
		}
		else if( value < 0 )
		{
			double oldMin = min;
			min = max / value;
			max = oldMin / value;
		}
		else
		{
			min = Double.NEGATIVE_INFINITY;
			max = Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * Returns the quotient of two intervals.
	 */
	public static Interval quotient(Interval first, Interval second)
	{
		Interval interval = new Interval(first);
		interval.divide(second);
		return interval;
	} 

	/**
	 * Returns the quotient of this interval and a scalar
	 */
	public static Interval quotient(Interval first, double second)
	{
		Interval interval = new Interval(first);
		interval.divide(second);
		return interval;
	} 

	/**
	 * Takes the absolute value of this interval
	 */
	public void abs()
	{
		if( max <= 0.0 )
		{
			double oldMin = min;
			min = -max;
			max = -oldMin;
		}
		else if( min < 0.0 )
		{
			if( -min > max )
				max = -min;
			
			min = 0.0;
		}
	}

	/**
	 * Returns the absolute value of the specified interval
	 */
	public static Interval abs(Interval interval)
	{
		Interval a = new Interval(interval);
		a.abs();
		return a;
	}

	/**
	 * Takes the square of this interval
	 */
	public void squared()
	{
		if( 0.0 <= min )
		{
			min *= min;
			max *= max;
		}
		else if( max <= 0.0 )
		{
			double oldMin = min;
			min = max * max;
			max = oldMin * oldMin;
		}		
		else
		{
			min *= min;
			max *= max;
			
			if( min > max )
				max = min;
			
			min = 0.0;
		}
	}

	/**
	 * Returns the square of the specified interval.
	 */
	public static Interval square(Interval interval)
	{
		Interval a = new Interval(interval);
		a.squared();
		return a;
	}

	/**
	 * Computes the square root of this interval.
	 */
	public void sqrt()
	{
		min = Math.sqrt(min);
		max = Math.sqrt(max); 
	}

	/**
	 * Returns the square root of the interval
	 */
	public static Interval sqrt(Interval interval)
	{
		Interval a = new Interval(interval);
		a.sqrt();
		return a;
	}

	/**
	 * Calculates maximum of this interval and the specified interval.
	 */
	public void maximum(Interval interval)
	{
		if( interval.max > max )
			max = interval.max;
		
		if( interval.min > min )
			min = interval.min;
	}

	/**
	 * Returns the maximum of two intervals.
	 */
	public static Interval maximum(Interval first, Interval second)
	{
		Interval interval = new Interval(first);
		interval.maximum(second);
		return interval;
	}

	/**
	 * Calculates minimum of this interval and the specified interval.
	 */
	public void minimum(Interval interval)
	{
		if( interval.min < min )
			min = interval.min;

		if( interval.max < max )
			max = interval.max;
	}
    
    /**
     * Calculates minimum of this interval and a number.
     */
    public void minimum( double val )
    {
        if( val < max )
        {
            max = val;        
            if( val < min )
                min = val;
        }
    }

	/**
	 * Returns the minimum of two intervals.
	 */
	public static Interval minimum(Interval first, Interval second)
	{
		Interval interval = new Interval(first);
		interval.minimum(second);
		return interval;
	}
   

	/**
	 * Returns the textual representation of this interval
	 */
	public String toString()
	{
		return "[" + min + "," + max + "]";
	}
	
	public static void main(String[] args)
	{
		for(double min1 = -20.5; min1 <= 20; ++min1)
		for(double max1 = min1; max1 <= 20; ++max1)
		for(double min2 = -20.5; min2 <= 20; ++min2)
		for(double max2 = min2; max2 <= 10; ++max2)
		{
			Interval i1 = new Interval(min1, max1);
			Interval i2 = new Interval(min2, max2);
			
			Interval p = Interval.quotient(i1, i2);
			
			if( min1 / min2 < p.min || min1 / min2 > p.max  
				|| min1 / max2 < p.min || min1 / max2 > p.max 
				|| max1 / min2 < p.min || max1 / min2 > p.max  
				|| max1 / max2 < p.min || max1 / max2 > p.max )
				System.out.println("error");
		}
		System.out.println("good");
	}
}
