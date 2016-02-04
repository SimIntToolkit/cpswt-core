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

package c2w.util;

import java.io.FileOutputStream;
import java.io.PrintStream;


public class KMLGen
{
    public static void testGen1( int num_of_marks, double x ) throws Exception
    {
        PrintStream o = new PrintStream( new FileOutputStream("C:/temp/test3.kml"));
        
        o.println( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        o.println( "<kml xmlns=\"http://earth.google.com/kml/2.2\">" );
        o.println( "<Document>" );
        o.println( "  <name>test3.kml</name>" );
        o.println( "  <Folder>" );
        o.println( "    <name>waypoints</name>" );
        o.println( "    <open>1</open>" );

        for( int i=0; i<num_of_marks; ++i )
        {
            o.println( "    <Placemark>" );
            o.println( "      <name>almafa"+i+"</name>" );
            o.println( "      <Point>" );
            double x1 = -86.87892642935459 + x;
            double y1 = 36.09307416414438 + i/1000.0;
            o.println( "        <coordinates>"+x1+","+y1+",0</coordinates>" );
            o.println( "      </Point>" );
            o.println( "    </Placemark>" );
        }
        
        o.println( "  </Folder>" );
        o.println( "</Document>" );
        o.println( "</kml>" );
    }
    
    
    
    public static void main( String[] args ) throws Exception
    {
        for( int i=0; i<50; ++i )
        {
            testGen1( 10, i/10000.0 );
            Thread.sleep(1000);
        }
    }

}
