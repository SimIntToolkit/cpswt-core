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
 * @author Gyorgy Balogh
 */

package org.cpswt.math;

import java.io.Serializable;

public class Vec3D implements Serializable
{
    public static final Vec3D XAXIS = new Vec3D(1,0,0);
    public static final Vec3D YAXIS = new Vec3D(0,1,0);
    public static final Vec3D ZAXIS = new Vec3D(0,0,1);
    
    public double x;
    public double y;
    public double z;
    
    public Vec3D()
    {
        x = 0;
        y = 0;
        z = 0;
    }
    
    public Vec3D( double x, double y, double z )
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vec3D( double alpha, double len )
    {
        x = len * Math.cos(alpha);
        y = len * Math.sin(alpha);
        z = 0;
    }
        
    public Vec3D( Vec3D v )
    {
        x = v.x;
        y = v.y;
        z = v.z;
    }
    
    /*public Vec3D( org.cpswt.platform.Vec3D v )
    {
    	x = v.x;
    	y = v.y;
    	z = v.z;    	
    }*/
    
    public void copy( Vec3D v )
    {
        x = v.x;
        y = v.y;
        z = v.z;        
    }
    
    /*public void copy( org.cpswt.platform.Vec3D v )
    {
        x = v.x;
        y = v.y;
        z = v.z;        
    }*/
    
    public String toString()
    {
        return x + "\t" + y + "\t" + z;
    }
    
    /*public org.cpswt.platform.Vec3D toPlatformVec3D()
    {
    	return new org.cpswt.platform.Vec3D(x,y,z);
    }*/
    
    public double getAzimuth()
    {
        double a = Math.atan2(y,x);
        if( a<0 )
            a+=2*Math.PI;
        return a;
    }
    
    public double getXZAzimuth()
    {
        double a = Math.atan2(z,x);
        if( a<0 )
            a+=2*Math.PI;
        return a;
    }
    
    public double getXZAzimuthInDegrees()
    {
        return getXZAzimuth() / Math.PI * 180;        
    }
    
    public double getAzimuthInDegrees()
    {
        return getAzimuth() / Math.PI * 180;
    }
    
    public double getElevation()
    {
        return Math.asin(z/length());
    }
    
    public double getElevationInDegrees()
    {
        return getElevation() / Math.PI * 180;        
    }
        
    public Vec3D add( Vec3D b )
    {
        return new Vec3D(x+b.x, y+b.y, z+b.z);       
    }
        
    public Vec3D sub( Vec3D b )
    {
        return new Vec3D(x-b.x, y-b.y, z-b.z );
    }
        
    public static double calcAngle( Vec3D a, Vec3D b )
    {
        double x = a.dot(b) / (a.length()*b.length());
        if( x>1 )
            x = 1;
        if( x<-1 )
            x = -1;
        return Math.acos(x);
    }
    
    /**
     * Works only if the altitude is not changing (i.e., fixed z)
     */
    public static double calcAngle2D(Vec3D a, Vec3D b)
    {
        double cosAB = a.dot(b) / (a.length()*b.length());
        Vec3D crossAB = a.cross(b);
        if(cosAB > 1) {
            cosAB = 1;
        } else if(cosAB < -1) {
            cosAB = -1;
        }
        double angle = Math.acos(cosAB);
        if(crossAB.z < 0) {
            angle = 2 * Math.PI  - angle;
        }
        return angle;
    }
    
    public double length()
    {
        return Math.sqrt(x*x+y*y+z*z);
    }
    
    public void norm()
    {
        double l = length();
        x /= l;
        y /= l;
        z /= l;
    }            
    
    public double dot( Vec3D b )
    {
        return x*b.x + y*b.y + z*b.z;
    }
    
    public Vec3D cross( Vec3D b )
    {
        return new Vec3D( y*b.z - z*b.y, z*b.x - x*b.z, x*b.y - y*b.x );    
    }
    
    public Vec3D scale( double a )
    {               
        return new Vec3D(a*x, a*y, a*z );
    }
           
    public double distance( Vec3D b )
    {
        return Math.sqrt((b.x-x)*(b.x-x)+(b.y-y)*(b.y-y)+(b.z-z)*(b.z-z));   
    }
    
    public double distance2D( Vec3D b )
    {
        return Math.sqrt((b.x-x)*(b.x-x)+(b.y-y)*(b.y-y));   
    }
        
    public Vec3D rotateAroundZ( double a )
    {
        return new Vec3D(x*Math.cos(a)+y*Math.sin(a),-x*Math.sin(a)+y*Math.cos(a),z);         
    }
    
    public Vec3D rotateAroundY( double a )
    {
        return new Vec3D(x*Math.cos(a)+z*Math.sin(a),y,-x*Math.sin(a)+z*Math.cos(a));         
    }
    
    public Vec3D eulerRotate( double alpha, double beta, double gamma )
    {
        double m00 = Math.cos(alpha)*Math.cos(beta)*Math.cos(gamma)-Math.sin(alpha)*Math.sin(gamma);
        double m10 = Math.sin(alpha)*Math.cos(beta)*Math.cos(gamma)+Math.cos(alpha)*Math.sin(gamma); 
        double m20 = -Math.sin(beta)*Math.cos(gamma); 
        double m01 = -Math.cos(alpha)*Math.cos(beta)*Math.sin(gamma)-Math.sin(alpha)*Math.cos(gamma); 
        double m11 = -Math.sin(alpha)*Math.cos(beta)*Math.sin(gamma)+Math.cos(alpha)*Math.cos(gamma);
        double m21 = Math.sin(beta)*Math.sin(gamma);
        double m02 = Math.cos(alpha)*Math.sin(beta);
        double m12 = Math.sin(alpha)*Math.sin(beta);
        double m22 = Math.cos(beta);
        
        return new Vec3D( m00*x+m10*y+m20*z, m01*x+m11*y+m21*z, m02*x+m12*y+m22*z ); 
    }
    
    public Vec3D rollPitchYawRotation( double alpha, double beta, double gamma )
    {
        double m00 = Math.cos(alpha)*Math.cos(beta);
        double m10 = Math.cos(alpha)*Math.sin(beta)*Math.sin(gamma)-Math.sin(alpha)*Math.cos(gamma);
        double m20 = Math.cos(alpha)*Math.sin(beta)*Math.cos(gamma)+Math.sin(alpha)*Math.sin(gamma);

        double m01 = Math.sin(alpha)*Math.cos(beta);
        double m11 = Math.sin(alpha)*Math.sin(beta)*Math.sin(gamma)+Math.cos(alpha)*Math.cos(gamma);
        double m21 = Math.sin(alpha)*Math.sin(beta)*Math.cos(gamma)-Math.cos(alpha)*Math.sin(gamma);

        double m02 = -Math.sin(beta);
        double m12 = Math.cos(beta)*Math.sin(gamma);
        double m22 = Math.cos(beta)*Math.cos(gamma);
        
        return new Vec3D( m00*x+m10*y+m20*z, m01*x+m11*y+m21*z, m02*x+m12*y+m22*z );
    }
}
