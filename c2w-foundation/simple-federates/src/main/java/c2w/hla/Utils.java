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

package c2w.hla;





public class Utils 
{	
	public static byte[] encodeInt( int x )
	{
		/*return new byte[] {(byte)((x >>> 24) & 0xFF),
						   (byte)((x >>> 16) & 0xFF),
						   (byte)((x >>> 8 ) & 0xFF),
						   (byte)((x >>> 0 ) & 0xFF)};*/
		
		return Integer.toString(x).getBytes();
	}
	
	public static int decodeInt( byte[] x )
	{
		//return ((x[0] << 24) + (x[1] << 16) + (x[2] << 8) + (x[3] << 0));
		
		return Integer.parseInt(new String(x));
	}
	
	public static byte[] encode(Object x)
	{
		return x.toString().getBytes();
	}
	
	public static Object decode(byte[] x, Class<?> c)
	{
		if( c == int.class )
			return Integer.parseInt(new String(x));
		else
			return null;
	}
	
	public static void main(String[] args) throws Exception
	{
		
        // Create the nodes
        /*PreferenceNode one = new PreferenceNode("one", "One", null,
            FieldEditorPageOne.class.getName());
        PreferenceNode two = new PreferenceNode("two", "Two", null,
            FieldEditorPageTwo.class.getName());*/

        // Add the nodes
        //mgr.addToRoot(one);
        //mgr.addToRoot(two);
		
		//BufferedReader r = new BufferedReader( new InputStreamReader(Utils.class.getResourceAsStream("1.txt")));	  
    			

	}
}
