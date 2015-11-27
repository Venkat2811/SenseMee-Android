/*
 * @author vraman2811@gmail.com
 * 
 * 
 */

package com.venkat.location;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.venkat.sharedVariables.GUIConstants;
import com.venkat.sms.Sms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class TrackLocation extends Service {
	
	private final Context context;
	 
	boolean isGPSEnabled = false;
	
	boolean isNetworkEnabled = false; 
	 Location networkLocation,GPSLocation;
	 AlertDialog.Builder alertDialog;
	 
	 MyGPSLocationListener GPSLocationListener;
	 MyWiFiLocationListener WiFiLocationListener;
	 BackgroundTaskGetGPS GPStask;
	 BackgroundTaskGetWIFI WiFiTask;
    
	 
	 
	 double networkLatitude,networkLongitude,GPSLatitude,GPSLongitude;
	
	public TrackLocation(Context context)
	{ 
		this.context=context; 
	}
	
	  //gps
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
     
    protected LocationManager GPSLocationManager,WiFiLocationManager;

   
	
	
	
	 
	
	 
	 /**
	  * 
	  * For convenience and stabiltiy of application toast has been used instead of this
	  * 
	  * This can also be used where user will be directly taken to settings page and application has to be restatred again
	  * 
	  * 
	 
	 public void showSettingsAlert(final String  title,String message)
	 {
		  alertDialog = new AlertDialog.Builder(context);
		 
		 alertDialog.setTitle(title);
		
		 alertDialog.setMessage(message);
		 alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent;
				 intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				
				
				context.startActivity(intent);
				
			}
		});
		 
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			dialog.cancel();	
			}
		}); 
		
		alertDialog.show();
		 
	 }
	 **/
	 

	
	 public void gpsStart()
	    {
		  GPStask=new  BackgroundTaskGetGPS ();
			GPStask.execute();
	    }
	 
	 public void gpsStop()
	 {
		
		 if(GPSLocationManager!=null)
		 {
			 GPSLocationManager.removeUpdates(GPSLocationListener);
			 GPSLocationManager=null;
	    	 GPStask.cancel(true);
	    	
		 }
		 
	 }
	 
	 public void wifiStart()
	 {
		 WiFiTask=new  BackgroundTaskGetWIFI();
			WiFiTask.execute();
	 }
	 
	 public void wifiStop()
	 {
		 if(WiFiLocationManager!=null)
		 {
			 WiFiLocationManager.removeUpdates(WiFiLocationListener);
			 WiFiLocationManager=null;
	    	 WiFiTask.cancel(true);
	    	
		 }
		 
	 }
	 
	 
	 
	 private class MyGPSLocationListener implements LocationListener {
		 
	        public void onLocationChanged(Location location) {
	            
	           if(isGPSEnabled)
	           {
	        	   GPSLatitude=location.getLatitude();
	        	   GPSLongitude=location.getLongitude();
	        	   
	           }
	         
	        }
	 
	        public void onStatusChanged(String s, int i, Bundle b) {
	          
	                   
	        }
	 
	        public void onProviderDisabled(String s) {
	            
	                  
	 }
	 
	        public void onProviderEnabled(String s) {
	          
	            
	        }
	 
	    }
	 
	 private class MyWiFiLocationListener implements LocationListener {
		 
	        public void onLocationChanged(Location location) {
	            
	         
	           if(isNetworkEnabled)
	           {
	        	   networkLatitude=location.getLatitude();
	        	   networkLongitude=location.getLongitude();
	           }
	        	
	        }
	 
	        public void onStatusChanged(String s, int i, Bundle b) {
	          
	                   
	        }
	 
	        public void onProviderDisabled(String s) {
	            
	                  
	 }
	 
	        public void onProviderEnabled(String s) {
	          
	            
	        }
	 
	    }
	 

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
private class BackgroundTaskGetGPS extends AsyncTask<String,String,String> {
		
		
	 private String getAddress(double latitude,double longitude)
	 {
		 Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
		 List<Address> addresses = null;
		 String address="";

		    try {
		        addresses = geocoder.getFromLocation(
		               latitude,
		               longitude,
		                // In this sample, get just a single address.
		                1);
		    } catch (IOException ioException) {
		        // Catch network or other I/O problems.
		        Log.e("", "Service not available", ioException);
		        address="Sorry..Cannot get address. You can find it with co-ordinates";
		        return address;
		    } catch (IllegalArgumentException illegalArgumentException) {
		        // Catch invalid latitude or longitude values.
		        Log.e("", "Invalid latitude and long" , illegalArgumentException);
		        address="Sorry..Cannot get address. You can find it with co-ordinates";
		        return address;
		    }

		    // Handle case where no address was found.
		    if (addresses == null || addresses.size()  == 0) {
		       
		            Log.e("", "No address found");
		            address="Sorry..Cannot get address. You can find it with co-ordinates";
			        return address;
		        
		    } else {
		        Address returnedAddress = addresses.get(0);
		        StringBuilder strReturnedAddress = new StringBuilder("\n");
		 	   for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
		 	    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
		 	   }
		 	   address=strReturnedAddress.toString();
		        Log.i("", "Address found");
		        
		    }

	       return address;
	 }
	
	
	private ProgressDialog dialogp;
	private  Sms obmess=new Sms();
	
	
	 //for timer
		boolean timerFlag=true;
		long activatedTime;

	@Override
	protected void onPreExecute() {
		
		 try
		 {
			 GPSLocationListener=new MyGPSLocationListener();
			 GPSLocationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
			 isGPSEnabled = GPSLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			 
			 
			 if(!isGPSEnabled)
			 {
				 /**
				  * Remove the below comment line to automatically invoke settings tab
				  */
				// showSettingsAlert(GUIConstants.ENABLE_GPS, "Your GPS is turned off. Would you like it to turn it on..?");
				 Toast.makeText(context, "Your GPS is turned off.. Kindly Turn it on and try again..", Toast.LENGTH_LONG).show();
				 String Message= String.format("\n Sorry.. Cannot fetch GPS location as permission is not given by user.");  
					obmess.send(GUIConstants.mobNo,Message);
					
			 }
			 else
			 {
				 dialogp = new ProgressDialog(context);
					
					
					dialogp.setTitle("GPS Location");
					dialogp.setMessage("Searching....");
					dialogp.setCanceledOnTouchOutside(false);
					//dialogp.setCancelable(false);
					dialogp.show();
				 	
					 if(isGPSEnabled)
					 {
						 GPSLocationManager.requestLocationUpdates(
								 LocationManager.GPS_PROVIDER,
								 MINIMUM_TIME_BETWEEN_UPDATES,
								 MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,GPSLocationListener);
					 }
					 
					 if(GPSLocationManager!=null)
					 {
						GPSLocation = GPSLocationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
					 
						if(GPSLocation!=null)
						{
							GPSLatitude = GPSLocation.getLatitude();
							GPSLongitude = GPSLocation.getLongitude();
						}
					 }
			
			 
			 }	 
		 }catch(Exception e)
		 {
			 
		 }

		
	}

	@Override
	protected void onPostExecute(String responsestring1) {
		try {
		boolean successful = false;
		if (dialogp.isShowing()) {
			
				
				dialogp.dismiss();
				successful=true;
			
		
			if(successful)
			{
				String GPSMessage;
				if(GPSLatitude==0.0)
				{
				 Toast.makeText(context, "Getting GPS Location Timed Out..", Toast.LENGTH_LONG).show();
				 GPSMessage= String.format("Unfortunately getting GPS location timed out.. :(");
				 obmess.send(GUIConstants.mobNo,GPSMessage);
				}
				else
				{
				 GPSMessage= String.format("New GPS location\n Latitude: %1$s \n Longitude: %2$s", GPSLatitude,GPSLongitude);

				String GPSAddr="GPS Location's Address(approx):"+getAddress(Double.parseDouble(String.valueOf(GPSLatitude)), Double.parseDouble(String.valueOf(GPSLongitude)));
				obmess.send(GUIConstants.mobNo,GPSMessage);
				obmess.send(GUIConstants.mobNo,GPSAddr);
				 Toast.makeText(context, GPSMessage+"\n"+GPSAddr, Toast.LENGTH_LONG).show();
				}
				
				
				gpsStop();
				
			}
		}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		}

		

		@Override
		protected String doInBackground(String... params) {


		try
		{
			while(GPSLatitude==0.0)
			{
	
				if(timerFlag)
           	  {
           		  activatedTime=System.currentTimeMillis();
           		  timerFlag=false;
           	  }
           	  
           
           	 long difference=System.currentTimeMillis()-activatedTime;// finding difference between current and activated time
           	 
           	 
           	  if(difference>GUIConstants.WAIT_TIME)//one minute
           	  {
           		 
           		  timerFlag=true;
           		  break;
           	  }
           	   
	
			}
		}catch(Exception e)
		{
			
		}
			return null;

		}
	}

private class BackgroundTaskGetWIFI extends AsyncTask<String,String,String> {
	
 private String getAddress(double latitude,double longitude)
	 {
	 Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
	 List<Address> addresses = null;
	 String address="";

	    try {
	        addresses = geocoder.getFromLocation(
	               latitude,
	               longitude,
	                // In this sample, get just a single address.
	                1);
	    } catch (IOException ioException) {
	        // Catch network or other I/O problems.
	        Log.e("", "Service not available", ioException);
	        address="Sorry..Cannot get address. You can find it with co-ordinates";
	        return address;
	    } catch (IllegalArgumentException illegalArgumentException) {
	        // Catch invalid latitude or longitude values.
	        Log.e("", "Invalid latitude and long" , illegalArgumentException);
	        address="Sorry..Cannot get address. You can find it with co-ordinates";
	        return address;
	    }

	    // Handle case where no address was found.
	    if (addresses == null || addresses.size()  == 0) {
	       
	            Log.e("", "No address found");
	            address="Sorry..Cannot get address. You can find it with co-ordinates";
		        return address;
	        
	    } else {
	        Address returnedAddress = addresses.get(0);
	        StringBuilder strReturnedAddress = new StringBuilder("\n");
	 	   for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
	 	    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
	 	   }
	 	   address=strReturnedAddress.toString();
	        Log.i("", "Address found");
	        
	    }

       return address;
	 }
	
	
	private ProgressDialog dialogp;
	private  Sms obmess=new Sms();
	
	 
	 //for timer
		boolean timerFlag=true;
		long activatedTime;

	@Override
	protected void onPreExecute() {
		
		try
		 {
			WiFiLocationListener=new MyWiFiLocationListener();
			 WiFiLocationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
			 
			 isNetworkEnabled = WiFiLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			 
			 
			 if(!isNetworkEnabled)
			 {
				 /**
				  * Remove the below comment line to automatically invoke settings tab
				  */
				 
				 //showSettingsAlert(GUIConstants.ENABLE_NETWORK_PROVIDER, "Your Network provider settings is turned off. Would you like it to turn it on..?");
				 Toast.makeText(context, "Your Network's location access permission is turned off.. Kindly Turn it on and try again..", Toast.LENGTH_LONG).show();
				 String Message= String.format("Sorry.. Cannot fetch Network location as permission is not given by user.");  
					obmess.send(GUIConstants.mobNo,Message);
			 }
			 else
			 {
				 dialogp = new ProgressDialog(context);
					
					
					dialogp.setTitle("Network Location");
					dialogp.setMessage("Searching...");
					dialogp.setCanceledOnTouchOutside(false);
					//dialogp.setCancelable(false);
					dialogp.show();
				
				 if(isNetworkEnabled){
					 
					 WiFiLocationManager.requestLocationUpdates(
							 LocationManager.NETWORK_PROVIDER,
							 MINIMUM_TIME_BETWEEN_UPDATES,
							 MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,WiFiLocationListener);
				 }
				 
				 if(WiFiLocationManager!=null)
				 {
					networkLocation = WiFiLocationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
				 
					if(networkLocation!=null)
					{
						networkLatitude = networkLocation.getLatitude();
						networkLongitude = networkLocation.getLongitude();
					}
				 }		 
			 }	 
		 }catch(Exception e)
		 {
			 
		 }
		

		
	}

	@Override
	protected void onPostExecute(String responsestring1) {
		try {
		
		boolean successful = false;
		if (dialogp.isShowing()) {
				dialogp.dismiss();
				successful=true;
		if(successful)
			{ 
				String WiFiMessage;
				
				if(networkLatitude==0.0)
				{
				 Toast.makeText(context, "Getting Network Location Timed Out..", Toast.LENGTH_LONG).show();
				 WiFiMessage= String.format("Unfortunately getting Network location timed out.. :(");
				
				}
				else	
				{
				WiFiMessage= String.format("Network location \n Latitude: %1$s \n Longitude: %2$s", networkLatitude,networkLongitude);
				
				String WiFiAddr="Network Location's Address(approx):"+getAddress(Double.parseDouble(String.valueOf(networkLatitude)), Double.parseDouble(String.valueOf(networkLongitude)));
				
			
				Toast.makeText(context, WiFiMessage+"\n"+WiFiAddr, Toast.LENGTH_LONG).show();
				 obmess.send(GUIConstants.mobNo,WiFiMessage);
				obmess.send(GUIConstants.mobNo,WiFiAddr);
				}
				
				;
				
				wifiStop();
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}

		

		@Override
		protected String doInBackground(String... params) {

			try
			{
				while(networkLatitude==0.0)
				{				
					if(timerFlag)
               	  {
               		  activatedTime=System.currentTimeMillis();
               		  timerFlag=false;
               	  }
               	  
               
               	 long difference=System.currentTimeMillis()-activatedTime;// finding difference between current and activated time
               	 
               	 
               	  if(difference>GUIConstants.WAIT_TIME)//one minute
               	  {
               		 
               		  timerFlag=true;
               		  break;
               	  }
               	   
				}
			}catch(Exception e)
			{
				
			}

				return null;
		}
	}





	
	    
	

}
