/*
 * @author vraman2811@gmail.com
 * 
 * 
 */

package com.venkat.sensemee;

import com.example.sensemee.R;
import com.venkat.location.TrackLocation;
import com.venkat.sharedVariables.GUIConstants;
import com.venkat.sms.Sms;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;



@SuppressLint("HandlerLeak")
public class Main extends Activity implements OnClickListener {
	
	
     TextView numbr,thrs;
     ImageButton on,off;
     EditText editTextMobNo,editTextThreshold;
     Handler _handler;
     
     //counter that keeps track of the no of times the specified threshold has been exceeded
     volatile int  mcount=0;
    volatile boolean whileFlag=true;
    
 	  final Context context = this;
       
     Sms obmess;
     TrackLocation gpsOb;
     ReadFromMic obMic;
    
     volatile int amp=0; 
    //Properties (MIC)
    public AudioRecord audioRecord; 
    public int mSamplesRead; 
    public int recordingState;
    public int buffersizebytes; 
    public int channelConfiguration = AudioFormat.CHANNEL_IN_MONO; 
    public int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; 
    public static short[] buffer; 
    public static final int SAMPPERSEC = 44100; 
    
   
    //for timer
	boolean timerFlag=true;
	long activatedTime;
	

   
   
     
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		on=(ImageButton)findViewById(R.id.on);
				off=(ImageButton)findViewById(R.id.off);
				editTextMobNo=(EditText)findViewById(R.id.editText1);
		editTextThreshold=(EditText)findViewById(R.id.editText2);
		
		numbr=(TextView)findViewById(R.id.enterno);
		thrs=(TextView)findViewById(R.id.enterth);
				
		on.setVisibility(View.VISIBLE);
		off.setVisibility(View.INVISIBLE);
		
		
		on.setOnClickListener(this);
		off.setOnClickListener(this);
		
	
		gpsOb= new TrackLocation(Main.this);
		obmess = new Sms();
		 _handler = new Handler();
		
	}
	
    /*
     * Stops reading from MIC
     * 
     */
    protected void stop()
    {
  
    	if (null != audioRecord) 
    	{  
    		
			audioRecord.stop();
			audioRecord.release();
			audioRecord = null;	
			whileFlag=timerFlag=true;
			amp=0;
			mcount=0;
			
			
		}
    		
    }
    
    /*
     * Setting up MIC properties and starts reading from MIC
     * 
     */
    
    protected void start()
    {
    	
    	 buffersizebytes = AudioRecord.getMinBufferSize(SAMPPERSEC,
	    		   channelConfiguration,audioEncoding); 
	        
	        buffer = new short[buffersizebytes]; 
	        
	      audioRecord = new AudioRecord(android.media.MediaRecorder.AudioSource.MIC,
       		SAMPPERSEC,channelConfiguration,audioEncoding,buffersizebytes); //constructor 
	       

    	audioRecord.startRecording();
    }

   


    
   
 
 private class ReadFromMic extends AsyncTask<String,String,String> {
		
		
		private ProgressDialog dialogp;
		


		@Override
		protected void onPreExecute() {
		dialogp = new ProgressDialog(context);
			
			
			dialogp.setTitle("Reading through MIC");
			dialogp.setCanceledOnTouchOutside(false);
			dialogp.setMessage("Reading...");
	        dialogp.setProgressStyle(dialogp.STYLE_HORIZONTAL);
			dialogp.setProgress(0);
		
			dialogp.setMax(GUIConstants.COUNT_THRESHOLD);
			dialogp.setIndeterminate(false);
			//dialogp.setSecondaryProgress(0);
			dialogp.show();
			

			
		}

		@Override
		protected void onPostExecute(String responsestring1) {
			boolean successful = false;
			if (dialogp.isShowing()) {
				try {
					stop();
					dialogp.dismiss();
					
					successful=true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
			
				if(successful)
				{
					/*
					 * Fetches location from GPS and WiFi
					 * 
					 */
					 Toast.makeText(context, "Basic alert message sent..", Toast.LENGTH_LONG).show();

					gpsOb.gpsStart();
					gpsOb.wifiStart();
				}
			}
				

			}

		/**
		 * Reads from MIC and stores it in buffer
		 * 
		 */

		@Override
		protected String doInBackground(String... params) {

			 while( whileFlag ) 
             {
             	
             	  try{
                     	
                             mSamplesRead = audioRecord.read(buffer, 0, buffersizebytes );         
                    	        
                             for(int i = 0; i < buffer.length; i++)
                             {
                             	/*
                             	 * NOTE: This cannot be directly taken as amplitude. But for sake of simplicity it has been used. 
                             	 * 
                             	 */
                            	 
                                amp = buffer[i];  
                                
                              //  String val="Amplitude : "+String.valueOf(amp)+"\n"+"Count : "+String.valueOf(mcount);
                             
                                /**
                          	    * The following line results in
                          	    * 
                          	    * 1) Displays amplitude in progress dialog
                          	    * 2) Creates inValidateChildInParent() - CalledFromWrongThreadException.. But no need to worry
                                 * 3) Also there will be some delay in displaying values because audio is read so quickly that updating speed
                          	    *    of UI thread
                          	    * 4) Some values are displayed quick so that we cannot see it.
                          	    *  
                          	    * 
                          	    */
                          	   
                          	   
                          	//  dialogp.setMessage(val);
                          	 // dialogp.setProgress(amp);
                                
                          	
                                
                                
                                
                                if((amp>GUIConstants.AMP_THRESHOLD)||(amp<-(GUIConstants.AMP_THRESHOLD)))
                                {
                             	   if(mcount<(GUIConstants.COUNT_THRESHOLD))
                             	   mcount++;
                             	 
                             	  
                             	   
                               }
                                
                                /**
                          	    * 
                          	    * Timer Logic: In case of fire we'll get high amplitude values continuously within a minute
                          	    * this ensures the same
                          	    */
                          	   
                          	  if(timerFlag)
                          	  {
                          		  activatedTime=System.currentTimeMillis();
                          		  timerFlag=false;
                          	  }
                          	  
                          
                          	 long difference=System.currentTimeMillis()-activatedTime;// finding difference between current and activated time
                          	 
                          	 if(difference<1000)
                          	 {
                          		 mcount=0;
                          	 }
                          	 
                          	  if(difference>GUIConstants.WAIT_TIME)//one minute
                          	  {
                          		  mcount=0;
                          		  timerFlag=true;
                          	  }
                                
                                _handler.post(new Runnable() {
                                    public void run() {
                                        dialogp.setProgress(mcount);
                                      

                                      }
                                  }); 
                                if(mcount>=GUIConstants.COUNT_THRESHOLD)
                                {
                                	/*
                                	 * Message has to be sent immediately because getting location updates might take time 
                                	 * 
                                	 * 
                                	 */
                             	  obmess.send(GUIConstants.mobNo,"Fire.!!Fire.!!Help!!..It's just testing..Location will be updated soon....");
                             	  
                             	  whileFlag=false;
                             	  timerFlag=true;

                             	  break;                         	                                       	                                         	     
                                }
                                
                             
                             }     
                     	
                     } catch( Exception e )
                     {                        
                     }
             
             }

		return null;

		}
	}

	
	@Override
	public void onClick(View e) {
		// TODO Auto-generated method stub
   switch(e.getId())
 {

	 
 case R.id.on:
	 
	 /*
	  * This checks that the input is not null. To make it more robust add constraints and checks accordingly..
	  * 
	  */
	
	 if(editTextThreshold.getText().toString().trim().length()<=0 || editTextMobNo.getText().toString().trim().length()<=0)
	 {
		 Toast.makeText(context, "Kindly enter valid mobile number and threshold value and try again..", Toast.LENGTH_LONG).show();
	 }
	
	 else
	 {
	 GUIConstants.AMP_THRESHOLD=Integer.valueOf(editTextThreshold.getText().toString());
	 GUIConstants.mobNo=editTextMobNo.getText().toString();
	 
	 editTextMobNo.setVisibility(View.INVISIBLE);
	 editTextThreshold.setVisibility(View.INVISIBLE);
	 
		
	   on.setVisibility(View.INVISIBLE);
		off.setVisibility(View.VISIBLE);	 
		
		numbr.setVisibility(View.INVISIBLE);	 
		thrs.setVisibility(View.INVISIBLE);	
		mcount=0;
		amp=0;
		whileFlag=true;
	
		obMic= new ReadFromMic();
		start();
	     obMic.execute();
	 }
	     
		 break;
 case R.id.off:
	 
	  stop();
	  
			
		 
		 editTextMobNo.setVisibility(View.VISIBLE);
		 editTextThreshold.setVisibility(View.VISIBLE);
		on.setVisibility(View.VISIBLE);
		numbr.setVisibility(View.VISIBLE);	 
		thrs.setVisibility(View.VISIBLE);	
		off.setVisibility(View.INVISIBLE);
		
		
		break;
	 
	 
 }
	
	}

	
	
	
}
