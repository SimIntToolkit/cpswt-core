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

package c2w.hla;

import hla.rti.LogicalTime;
import hla.rti.ReceivedInteraction;
import hla.rti.ResignAction;

import org.portico.impl.hla13.types.DoubleTime;

public class SimpleFederate extends SynchronizedFederate
{      
    protected DoubleTime      time = new DoubleTime();    
    protected boolean         running;
    
    String          federation_id;
    String          federate_id;
    boolean         granted;

    // time interpolation
    double prev_fed_time;      // -1 means, there is no useable previous fed time
    long   prev_cpu_time;
    
    double fed_t0 = -1;
    long   cpu_t0 = -1;
    double one_fed_sec_in_cpu_millisec = 1000;
    double time_approx_error = 0;
    
    double cont_fed_time = -1;
    
    double look_ahead;
    
    public boolean         paused = false;
    
    public SimpleFederate(String federation_id, String federate_id,
            double look_ahead ) throws Exception
    {
        this.federation_id = federation_id;
        this.federate_id   = federate_id;
        this.look_ahead    = look_ahead;
        
        resetTimeApprox();
    
        //System.setProperty("jarti.lrc.jsop.host", "10.0.0.1");
        
        System.out.println("creating RtiAmbassador");        
        createRTI();
        
        System.out.println("Joining to federation");
       	joinFederation( federation_id, federate_id );
        System.out.println("Joined");
        
        // PER THE HLA BOOK, ENABLE TIME-CONSTRAINED FIRST, THEN TIME-REGULATING
        enableTimeConstrained();
        enableTimeRegulation( time.getTime(), look_ahead );
        enableAsynchronousDelivery();

        SimEnd.init( getRTI() );
        SimPause.init( getRTI() );
        SimResume.init( getRTI() );
        SimEnd.subscribe( getRTI() );      
        SimPause.subscribe( getRTI() );       
        SimResume.subscribe( getRTI() );
        
        time.setTo( getRTI().queryFederateTime() );
        running = true;
    }
    
    protected void performInitialSynchronization() throws Exception {
    	// ALL PUBLISHING/SUBSCRIBING SHOULD BE COMPLETE, SO "ReadyToPopulate"
        readyToPopulate();

    	// THERE DOESN'T APPEAR TO BE ANY INITIAL PUBLICATIONS OF DATA, SO "ReadyToRun"
        readyToRun();    	
    }
    
    public double getSmallestSendTimeStamp()
    {
        return time.getTime()+look_ahead+(look_ahead/10000.0);
    }
    
    private synchronized void resetTimeApprox()
    {
        fed_t0 = -1;
        prev_fed_time = -1;
        prev_cpu_time = -1;
    }
    
    /**
     * Linear interpolation of federate time based on system time.
     * @return federate time
     */
    public synchronized double approxTime()
    {
        if( fed_t0<0 )
        {
            // no interpolation
            return time.getTime();
        }
        else
        {
            //return time.getTime();
            long cpu_time = System.currentTimeMillis();            
            return fed_t0 + (cpu_time-cpu_t0)/one_fed_sec_in_cpu_millisec;
        }
    }
    
    /*public synchronized double approxTime()
    {
        if( one_fed_sec_in_cpu_nanosec <0 )
        {
            // no interpolation
            return time.getTime();
        }
        else
        {
            long cpu_time = System.nanoTime();            
            return prev_fed_time + (cpu_time - prev_cpu_time) / one_fed_sec_in_cpu_nanosec;            
        }
    }*/
    
    public void advanceTime( double step ) throws Exception
    {
        granted = false;
        
        // send request
        DoubleTime next_time = new DoubleTime(time.getTime()+step);
        getRTI().timeAdvanceRequest(next_time);
        
        // wait for grant
        while( !granted && running )
            getRTI().tick();
    }
    
    public void advanceTimeTo( double target ) throws Exception
    {
        granted = false;
        
        // send request
        DoubleTime next_time = new DoubleTime(target);
        getRTI().timeAdvanceRequest(next_time);
        
        // wait for grant
        while( !granted && running )
            getRTI().tick();
    }
    
    protected void step() throws Exception
    {
        advanceTime(1.0);
    }
    
    protected void uninitialize() throws Exception
    {        
    }
    
    public void execute() throws Exception
    {   
        while( running )
        {
            if( !paused )
            {
                step();
            }
            else
            {
                getRTI().tick();
                Thread.sleep(10);
            }            
        }
        uninitialize();
        
        // "ReadyToResign"
        readyToResign();

        getRTI().resignFederationExecution( ResignAction.DELETE_OBJECTS );
    }
    
    
    /*private synchronized void updateTimeApprox()
    {   
        //last_cpu_time = cpu_time;
        
        double fed_time = time.getTime();
        long   cpu_time = System.currentTimeMillis();
        
        if( cont_fed_time < 0 )
        {
            cont_fed_time = fed_time;
            
        }
        else
        {
            cont_fed_time += one_fed_sec_in_cpu_millisec;
            
            
        }
        
        if( fed_t0 >=0 )
        {
            double target_one_fed_sec_in_cpu_millisec = (cpu_time-cpu_t0) / (fed_time-fed_t0);            
            one_fed_sec_in_cpu_millisec = 0.8 * one_fed_sec_in_cpu_millisec + 0.2 * target_one_fed_sec_in_cpu_millisec;             
        }
        else
        {
            fed_t0 = fed_time;
            cpu_t0 = cpu_time;
        }
    }*/
    
    private synchronized void updateTimeApprox()
    {
        double fed_time = time.getTime();
        long   cpu_time = System.currentTimeMillis();
        
        if( fed_t0 >=0 )
        {
            double target_one_fed_sec_in_cpu_millisec = (cpu_time-cpu_t0) / (fed_time-fed_t0);            
            one_fed_sec_in_cpu_millisec = 0.8 * one_fed_sec_in_cpu_millisec + 0.2 * target_one_fed_sec_in_cpu_millisec;             
        }
        else
        {
            fed_t0 = fed_time;
            cpu_t0 = cpu_time;
        }
    }
    
    /*private synchronized void updateTimeApprox()
    {
        double fed_time = time.getTime();
        long   cpu_time = System.nanoTime();
        
        if( prev_fed_time >=0 )
        {
            long dt1   = cpu_time-prev_cpu_time;
            double dt2 = fed_time-prev_fed_time;
            
            if( dt1>0 && dt2>0 )
            {            
                double a = dt1 / dt2;
                if( one_fed_sec_in_cpu_nanosec > 0.0 )
                    one_fed_sec_in_cpu_nanosec = 0.95 * one_fed_sec_in_cpu_nanosec + 0.05 * a;
                else
                    one_fed_sec_in_cpu_nanosec = a;

                //System.out.println(one_fed_sec_in_cpu_nanosec);
                
                prev_fed_time = fed_time;
                prev_cpu_time = cpu_time;
            }
        }
        else
        {
            prev_fed_time = fed_time;
            prev_cpu_time = cpu_time;        
        }
    }*/
    
    @Override
    public void timeAdvanceGrant( LogicalTime t )
    {      
        time.setTo(t);
        granted = true;
        
        //System.out.println( System.currentTimeMillis() + "\t" + time.getTime() );
        
        double error = Math.abs(approxTime() - time.getTime());        
        time_approx_error = 0.9 * time_approx_error + 0.1 * error;        
        //System.out.println( time_approx_error + "\t" + one_fed_sec_in_cpu_millisec  );

        
        updateTimeApprox();
    }
    
    @Override
    public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag)
    {
        if(  SimEnd.match( interactionClass )  )
        {            
            System.out.println("SimulationEnd interaction received");
            running = false;
        }
        else if(  SimPause.match( interactionClass )  )
        {
            System.out.println("Paused");
            paused = true;
            resetTimeApprox();
        }
        else if(  SimResume.match( interactionClass )  )
        {
            System.out.println("Resumed");
            paused = false;            
        }
    }
}
