/*
 * @author vraman2811@gmail.com
 * 
 * 
 */

package com.venkat.sharedVariables;

public class GUIConstants {
	
	private GUIConstants() {
		super();
	}
	
	public static String  message,mobNo;
	
/*
 * The actual values can be found out only when calibrated with a sensor
 * 
 */
	
	public static int AMP_THRESHOLD;// Min-Max range is -32768 to 32767..For simulation 1000,10000,20000 will be good
	public final static int COUNT_THRESHOLD=30000;// To keep track of how many times the AMP_THRESHOLD is being exceeded
	
	public final static long WAIT_TIME=60000;// (60 secs) used in timers
	public final static String ENABLE_GPS="Activate GPS";
	public final static String ENABLE_NETWORK_PROVIDER="Activate permission for network access through Network Provider and WiFi";
	

}
