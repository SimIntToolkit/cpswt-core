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
 */

package c2w.hla;

import java.sql.*;

public class C2WLogger {
	
	public static String log_fileName = null;
	public static String log_NameBase = null;
	public static String db_url = null;
	public static Connection conn = null;
	
	
	private static void createDB( String dbName ) {
		if(dbName.length() > 64) {
			String truncatedDbName = dbName.substring(0, 64);
			System.err.println("WARNING!! Given database name '" + dbName + "' is longer than allowed 64 characters maximum -- truncating to 64 characters and using '" + truncatedDbName + "' instead.");
			dbName = truncatedDbName;
		}
		
        try{
        	String userName = "root";
            String password = "c2wt";
            db_url = "jdbc:mysql://localhost:3306/";
            Class.forName ("com.mysql.jdbc.Driver").newInstance();
            
            // CREATE DATABASE
            conn = DriverManager.getConnection(db_url, userName, password);
            Statement st = conn.createStatement();
            String cmd = "CREATE DATABASE " + dbName;
//            System.out.println( "cmd = \"" + cmd + "\"" );
            st.executeUpdate( cmd );
            st.close();
            conn.close();
            
            // CREATE TABLES
            db_url += dbName;
            conn = DriverManager.getConnection(db_url, userName, password);
            st = conn.createStatement();
            cmd ="CREATE TABLE ExperimentInfo (id VARCHAR(100), interaction VARCHAR(60), object VARCHAR(60), attribute VARCHAR(60), Publisher VARCHAR(60), Subscriber VARCHAR(60))";
            st.executeUpdate(cmd);
            cmd ="CREATE TABLE SimulationData (id VARCHAR(100), time VARCHAR(60), parameter VARCHAR(60), value VARCHAR(1024), type VARCHAR(60), level VARCHAR(60), logId VARCHAR(60))";
            st.executeUpdate(cmd);
            cmd="CREATE TABLE EventLog (time VARCHAR(60), eventName VARCHAR(100))";
            st.executeUpdate(cmd);
            st.close();
        }catch (Exception e){
            e.printStackTrace();            
        }
        finally{
            if (conn != null){
                try{
                    conn.close ();
                } catch (Exception e) { /* ignore close errors */ }
            }
        }
	}

	public static void init(String dbName){
		try
		{
			String userName = "root";
			String password = "c2wt";
			db_url = "jdbc:mysql://localhost:3306/"+dbName; //normally url="jdbc:mysql://localhost:3306/+dbname
			// Class.forName ("com.mysql.jdbc.Driver").newInstance();;
			// conn = DriverManager.getConnection (db_url, userName, password);
		}
		catch (Exception e)
		{
		    System.err.println ("Cannot connect to database server");
		    e.printStackTrace();            
		}	
	}
	
	public static void init(String url, String dbName){
		try
		{
			String userName = "root";
			String password = "c2wt";
			db_url = "jdbc:mysql://"+url+"/"+dbName; //normally url="jdbc:mysql://localhost:3306/+dbname
			Class.forName ("com.mysql.jdbc.Driver").newInstance();;
			conn = DriverManager.getConnection (db_url, userName, password);	
		}
		catch (Exception e)
		{
		    System.err.println ("Cannot connect to database server");
		    e.printStackTrace();            
		}
	}

	public static void close(){
        if (conn != null){
            try{
                conn.close ();
                System.out.println ("Database connection terminated");
            }
            catch (Exception e) { /* ignore close errors */ }
        }
    }
	
	public static void addLog(
			final String interaction, 
			final String fed, 
			final Boolean publish){
		if(conn==null) return;
		Thread thread = new Thread(new Runnable() {
			public void run() {			 	
				try {
					Statement st = conn.createStatement();
					String values = null;
				 	if(publish)
				 		values = "'"+fed+"_pub_"+interaction+"', '"+interaction+"', '', '', '"+fed+"', '' ";
				 	else
				 		values = "'"+interaction+"_sub_"+fed+"', '"+interaction+"', '', '', '', '"+fed+"'";
				 	st.executeUpdate("INSERT INTO ExperimentInfo VALUES(" + values + ")");
		            st.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});        
		thread.start();
	}

	public static void addLog(
			final String object,
			final String attribute,
			final String fed, 
			final Boolean publish){
		if(conn==null) return;
		Thread thread = new Thread(new Runnable() {
			public void run() {			 	
				try {
					Statement st = conn.createStatement();
					String values = null;
				 	if(publish)
				 		values = "'"+fed+"_pub_"+object+"_"+attribute+"', '', '"+object+"', '"+attribute+"', '"+fed+"', ''";
				 	else
				 		values = "'"+object+"_"+attribute+"_sub_"+fed+"', '', '"+object+"', '"+attribute+"', '', '"+fed+"'";
				 	st.executeUpdate("INSERT INTO ExperimentInfo VALUES(" + values + ")");
		            st.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});        
		thread.start();
	}
	
	public static void addLog(
	 final String id, 
	 final double time, 
	 final String parameter, 
	 final String value, 
	 final String type,
	 final String loglevel,
	 final String logId
	){
		if(conn==null) return;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Statement st = conn.createStatement();
					String strTime = String.format("%.3f", time);
					String values = "'"+id+"', "+strTime+", '"+parameter+"', '"+value+"', '"+type+"', '"+loglevel+"', " + ( logId == null ? "''" : "'" + logId + "'");
				 	st.executeUpdate("INSERT INTO SimulationData VALUES(" + values + ")");	
				 	st.close();
				}catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});        
		thread.start();
	}

	public static void addEventLog(
	 final double time,
	 final String eventName
	){
		if(conn==null) return;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Statement st = conn.createStatement();
					String strTime = String.format("%.3f", time);
					String values = ""+strTime+", '"+eventName+ "'";
				 	st.executeUpdate("INSERT INTO EventLog VALUES(" + values + ")");	
				 	st.close();
				}catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});        
		thread.start();
	}

	public static int getLogLevel(final String loglevel){
		int level = 5;
		if(loglevel.equalsIgnoreCase( "HIGHLY_IMPORTANT" ) ) level = 0;
		else if(loglevel.equalsIgnoreCase( "IMPORTANT" ) ) level = 1;
		else if(loglevel.equalsIgnoreCase( "NORMAL" ) ) level = 2;
		else if (loglevel.equalsIgnoreCase( "DETAILED" ) ) level = 3;
		else if (loglevel.equalsIgnoreCase( "HIGHLY_DETAILED" ) ) level = 4;
		
		return level;
	}
	
	public static void main( String[] args ) {		
	
		createDB(args[0]);
	}
}
