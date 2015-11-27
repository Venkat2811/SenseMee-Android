/*
 * @author vraman2811@gmail.com
 * 
 * 
 */

package com.venkat.sms;


import android.telephony.SmsManager;

public class Sms {

	public void send(String num,String loc) 
	{     
		
		String text=loc;
	
			sendMes(num,text);
	
	}
	public void sendMes(String phNo,String mess)
	{
		
	       SmsManager sms = SmsManager.getDefault();
	      sms.sendTextMessage(phNo, null, mess, null, null);
		
	}
	

}
