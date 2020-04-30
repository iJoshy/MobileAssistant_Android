package com.etisalat.MobileAssistant;

import org.json.JSONException;
import org.json.JSONObject;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.etisalat.thirdparty.EventsData;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SettingsActivity extends ActionBarActivity 
{
	private Intent intent;
	private int delayTime = 10;
	private EditText  editDelayText;
	private BootstrapButton btnSubmit;
	
	private BootstrapButton btnControl;
	private Button rdControl;
	
	private BootstrapButton btnTime;
	private Button rdTime;
	
	private BootstrapButton btnLive;
	private Button rdLive;
	
	private String jsonResponse;
    private static WebServiceCall wsc;
    private JSONObject jsonobject;
    private String asstNoString;
    private String asstTypeString;
    private String asstTimeString;
    private TextView asstTimeText;
    private String enabledDisabled;
    private TextView ibItem1;
    private EditText  editAsstNoEditText;
    private FrameLayout timerFrame;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private EventsData events;
	
	@SuppressLint("InflateParams") @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		//ActionBar actionBar = getSupportActionBar();
		//actionBar.show();
		
		preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);  
	    editor = preferences.edit();
	    
		getWindow().setSoftInputMode(3);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#719e18")));
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.ab_custom2, null);
        actionbar.setCustomView(customView);

        ibItem1 = (TextView) customView.findViewById(R.id.leftBtn2);
        ibItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	//System.out.println(" :::: left clicked ::::");
            	disableClicked();
            }
        });

        TextView ibItem2 = (TextView) customView.findViewById(R.id.rightBtn2);
        ibItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	//System.out.println(" :::: right clicked ::::");
            	optoutClicked();
            }
        });
        
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "etisalat.TTF");
        
        ((TextView)findViewById(R.id.leftBtn2)).setTypeface(typeface);
        ((TextView)findViewById(R.id.title2)).setTypeface(typeface);
        ((TextView)findViewById(R.id.rightBtn2)).setTypeface(typeface);
        
        editAsstNoEditText = (EditText)findViewById(R.id.asstNo2);
        editAsstNoEditText.setTypeface(typeface);
        editAsstNoEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        
        TextView  asstNoText = (TextView)findViewById(R.id.txtAsstNo2);
        asstNoText.setTypeface(typeface);
        asstNoText.setText("enter mobile assistant number");
        
        TextView  asstTypeText = (TextView)findViewById(R.id.txtAssttype2);
        asstTypeText.setTypeface(typeface);
        asstTypeText.setText("select mobile assistant type");
        
        asstTimeText = (TextView)findViewById(R.id.txtTimetype2);
        asstTimeText.setTypeface(typeface);
        asstTimeText.setText("select delay time");
        asstTimeText.setVisibility(View.INVISIBLE);     
        
        editDelayText = (EditText)findViewById(R.id.asstTimer2);
        editDelayText.setTypeface(typeface);
        editDelayText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		
        btnControl = (BootstrapButton)findViewById(R.id.asstTypeControlled2);
        rdControl = (Button)findViewById(R.id.controlBtn2);
        
        btnTime = (BootstrapButton)findViewById(R.id.asstTypeTime2);
        rdTime = (Button)findViewById(R.id.timedBtn2);
        
        btnLive = (BootstrapButton)findViewById(R.id.asstTypeLive2);
        rdLive = (Button)findViewById(R.id.liveBtn2);
        
        timerFrame = (FrameLayout)findViewById(R.id.setupTimer2);
        timerFrame.setVisibility(View.INVISIBLE);
        
        btnSubmit = (BootstrapButton)findViewById(R.id.btnBig2);
        
        /*********************
         *   automata
         *********************/
        
        /*
        asstNoString = "08099440449";
        asstTypeString = "401";
        asstTimeString = "15";
        enabledDisabled = "0";
	    */
        
        asstNoString  = preferences.getString("ASSISTANT_NUMBER", "0");
	    asstTimeString  = preferences.getString("ASSISTANT_TIME", "0");	    
	    asstTypeString  = preferences.getString("ASSISTANT_TYPE", "0");
	    enabledDisabled  = preferences.getString("ENABLED_DISABLED", "0");
        
	    
        editAsstNoEditText.setText(asstNoString);
        
	    if (asstTypeString.equals("402")) 
	    {
	    	controlledClicked(getCurrentFocus());
	    }
	    else if (asstTypeString.equals("401")) 
	    {
	    	showTimer();
	    	delayTime = Integer.parseInt(asstTimeString);
	    	timerFrame.setVisibility(View.VISIBLE);
	    	editDelayText.setText(delayTime + " : seconds");
	    }
	    else if (asstTypeString.equals("400")) 
	    {
	    	liveClicked(getCurrentFocus());
	    }
	    
	    
	    if (enabledDisabled.equals("0")) 
	    {
	    	ibItem1.setText("Activate");
	    	deActivateComponents();
		}
	    else if (enabledDisabled.equals("1")) 
	    {
	    	ibItem1.setText("Suspend");	    	
		}
	    
        System.out.println("\n\n assistant_msisdn is :::: "+asstNoString);
        System.out.println("\n\n assistant_type is :::: "+asstTypeString);
        System.out.println("\n\n assistant_time is :::: "+asstTimeString);
        System.out.println("\n\n enableDisable is 2 :::: "+enabledDisabled);
         
	}
	
	
	@Override
	public void onBackPressed() 
	{
		
	}
	
	
	@Override
	public void onDestroy()
	{
	    super.onDestroy();
	    
	    if(wsc != null)
	    	wsc = null;
	}
	
	
	public void deActivateComponents()
	{
		editAsstNoEditText.setEnabled(false);
		
		btnControl.setClickable(false);
		rdControl.setClickable(false);
		
		btnTime.setClickable(false);
		rdTime.setClickable(false);
		
		btnLive.setClickable(false);
		rdLive.setClickable(false);
		
		timerFrame.setClickable(false);
		btnSubmit.setEnabled(false);
		btnSubmit.setAlpha(0.5f);
	}
	
	
	public void activateComponents()
	{
		editAsstNoEditText.setEnabled(true);
		
		btnControl.setClickable(true);
		rdControl.setClickable(true);
		
		btnTime.setClickable(true);
		rdTime.setClickable(true);
		
		btnLive.setClickable(true);
		rdLive.setClickable(true);
		
		timerFrame.setClickable(true);
		btnSubmit.setEnabled(true);
		btnSubmit.setAlpha(1.0f);
	}
		
	
	public void leftClicked(View V)
	{
		//System.out.println(" :::: left clicked ::::");
		
		if(delayTime > 10 )
		{
			delayTime -= 1;
		}
		
		editDelayText.setText(delayTime + " : seconds");
		
	}
	
	public void rightClicked(View V)
	{
		//System.out.println(" :::: right clicked ::::");
		
		if(delayTime < 60 )
		{
			delayTime += 1;
		}
		
		editDelayText.setText(delayTime + " : seconds");
		
	}
	
	public void controlledClicked(View V)
	{
		//System.out.println(" :::: controlled clicked ::::");	
		
		rdControl.setBackgroundResource(R.drawable.checked);
		rdTime.setBackgroundResource(R.drawable.unchecked);
		rdLive.setBackgroundResource(R.drawable.unchecked);
		
		asstTimeText.setVisibility(View.INVISIBLE);
		timerFrame.setVisibility(View.INVISIBLE);
		
		asstTypeString = "402";
	}
	
	public void showTimer()
	{
		//System.out.println(" :::: time clicked ::::");
		
		rdControl.setBackgroundResource(R.drawable.unchecked);
		rdTime.setBackgroundResource(R.drawable.checked);
		rdLive.setBackgroundResource(R.drawable.unchecked);
		
		asstTimeText.setVisibility(View.VISIBLE);
		timerFrame.setVisibility(View.VISIBLE);
		
		asstTypeString = "401";
	}
	
	public void timedClicked(View V)
	{
		if (!asstTypeString.equals("401")) 
		{
			showTimer();
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
			alert.setMessage("\nThe default delay time is 10 seconds. select your prefered delay time below\n")
			     .setCancelable(false)
		       
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						//showTimer();
					}
				});

			alert.show();
		}
		
	}	
	
	public void liveClicked(View V)
	{
		//System.out.println(" :::: live clicked ::::");
		
		rdControl.setBackgroundResource(R.drawable.unchecked);
		rdTime.setBackgroundResource(R.drawable.unchecked);
		rdLive.setBackgroundResource(R.drawable.checked);
		
		asstTimeText.setVisibility(View.INVISIBLE);
		timerFrame.setVisibility(View.INVISIBLE);
		
		asstTypeString = "400";
	}
	
	
	public void disableClicked()
	{
		String title = "";
		
		if (enabledDisabled.equals("0"))
	    {
	        title = "This action will activate your Mobile Assistant subscription. Click ok to confirm.";
	    }
	    else if (enabledDisabled.equals("1"))
	    {
	        title = "This action will suspend your Mobile Assistant subscription. Click ok to confirm.";
	    }
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Notice")
		     .setMessage(title)
		     .setCancelable(false)
		
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		    {
	           public void onClick(DialogInterface dialog, int id) 
	           {
	                //do things
	           }
	        })
	       
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					if (enabledDisabled.equals("0"))
				    {
				        processing(1);
				    }
				    else if (enabledDisabled.equals("1"))
				    {
				    	processing(2);
				    }
				}
			});

		alert.show();
	}
	
	public void optoutClicked()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Notice")
		     .setMessage("This action will deactivate your Mobile Assistant subscription. Click ok to confirm.")
		     .setCancelable(false)
		
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		    {
	           public void onClick(DialogInterface dialog, int id) 
	           {
	                //do things
	           }
	        })
	       
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					processing(3);
				}
			});

		alert.show();
	}
	
	public void submitClicked(View v)
	{
		
		EditText  editAsstNoEditText = (EditText)findViewById(R.id.asstNo2);
		asstNoString = editAsstNoEditText.getText().toString();
		asstTimeString = Integer.toString(delayTime);
		
		if (asstNoString == "" || asstNoString == null) 
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
			alert.setTitle("Notice")
			     .setMessage("Please enter your assistants mobile number to setup.")
			     .setCancelable(false)
			
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						//
					}
				});

			alert.show();
		}
		else
		{
			processing(4);
		}
		
	}

	public void processing(int action)
	{
		
        jsonResponse = null;
        
        try
        {
        	
        	if (wsc != null) 
        	{
        		if (wsc.getStatus() == AsyncTask.Status.RUNNING)
            		wsc.cancel(true);
			}
       	 
       	 	wsc = new WebServiceCall(this);
       	 
        	String txnType = "";
        	
            if (action == 1) 
            	txnType = "enable";
            else if (action == 2)
            	txnType = "disable";
            else if (action == 3)
            	txnType = "unsubscribe";
            else if (action == 4)
            	txnType = "setup?asst-msisdn=" + asstNoString + "&asst-type=" + asstTypeString + "&timeout=" + asstTimeString;

            jsonResponse = wsc.doExecute(txnType);
            
            System.out.println("response ::: "+jsonResponse);
            		
            processResult(action);         
        }
        catch(Exception e)
		{
			e.printStackTrace();
		}
        
	}
	
	
    public void processResult(int type)
    {
    	
    	System.out.println("processResult :::: "+jsonResponse);
        
    	int code = 0;
    	String descr = "";
    	
        try 
		{
        	
			jsonobject = new JSONObject(jsonResponse);		
			
			code = jsonobject.getInt("code");
	        descr = jsonobject.getString("description");
        	
            code = 200;
            descr = "Successful";
            
		}
        catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        System.out.println("\n\n code is :::: " +code);
        System.out.println("\n\n description is :::: "+descr);
          
        if (code == 200)
        {
        	String msg = "";
        	String til = "";
        	
        	if (type == 1)
        	{
        		msg = "Your Mobile Assistant service has been successfully activated.";
        		til = "Congratulations";
        		updateDBandStatus(til, msg, 0);
			}
        	else if (type == 2)
        	{
        		msg = "Your Mobile Assistant service has been deactivated.";
        		til = "Notice";
        		updateDBandStatus(til, msg, 1);
			}
        	else if (type == 3)
        	{
        		msg = "You have unsubscribed from the Mobile Assistant service.";
        		til = "Notice";
        		showSucess(til, msg, 3);
			}
        	else if (type == 4)
        	{
        		msg = "You have successfully updated your Mobile Assistant profile.";
        		til = "Congratulations";
        		updateDB(til, msg, 4);
			}
        	
        	
        }       
        else
        {
        	 String msg = "";
             
        	 if (code == 201)
        	 { 
        		 if (type == 3) 
        			 msg = "As a postpaid subscriber, Please visit any experience center closest to you.";
        		 else
        			 msg = "Your request was not succesful. Please try again later"; 
        	 }
        	 else if (code == 230)
                 msg = "Please ensure your device is connected to etisalat mobile data";
             else
                 msg = "Your request was not succesful. Please try again later";
             
             showError(msg);
        }
		
    }	
  
    
    public void updateDBandStatus(String til, String msg, int type)
	{
    	
    	if (type == 0)
    	{
    		type = 1;
    		ibItem1.setText("Suspend");
    		enabledDisabled = Integer.toString(type);
    		activateComponents();    		
		}
    	else if (type == 1)
    	{
    		type = 0;
    		ibItem1.setText("Activate");
    		enabledDisabled = Integer.toString(type);
    		deActivateComponents();    		
		}
    	
    	events = new EventsData(this);
    	
		try
		{			
			SQLiteDatabase db = events.getWritableDatabase();
						
			String update2Table = "UPDATE USER SET ASSISTANT_TYPE = '"+asstTypeString+"', ASSISTANT_NUMBER = '"+asstNoString+"', ASSISTANT_TIME = '"+asstTimeString+"', ENABLED_DISABLED = '"+enabledDisabled+"'";
			db.execSQL("DELETE FROM USER");
			db.execSQL(update2Table);
			
			db.close();

			editor.putString("ASSISTANT_TYPE", asstTypeString);
			editor.putString("ASSISTANT_NUMBER", asstNoString);
			editor.putString("ASSISTANT_TIME", asstTimeString);
			editor.putString("ENABLED_DISABLED", enabledDisabled);
			editor.commit();		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			events.close();
		}
		
		showSucess(til, msg, type);
		
	}
    
    
    public void updateDB(String til, String msg, int type)
	{
    	
    	events = new EventsData(this);
    	
		try
		{			
			SQLiteDatabase db = events.getWritableDatabase();
						
			String update2Table = "UPDATE USER SET ASSISTANT_TYPE = '"+asstTypeString+"', ASSISTANT_NUMBER = '"+asstNoString+"', ASSISTANT_TIME = '"+asstTimeString+"', ENABLED_DISABLED = '"+enabledDisabled+"'";
			db.execSQL("DELETE FROM USER");
			db.execSQL(update2Table);
			
			db.close();

			editor.putString("ASSISTANT_TYPE", asstTypeString);
			editor.putString("ASSISTANT_NUMBER", asstNoString);
			editor.putString("ASSISTANT_TIME", asstTimeString);
			editor.putString("ENABLED_DISABLED", enabledDisabled);
			editor.commit();		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			events.close();
		}
		
		showSucess(til, msg, type);
		
	}
    
    
    public void deleteRecordDB(String til, String msg, int type)
	{
    	events = new EventsData(this);
    	
    	try
		{			
			SQLiteDatabase db = events.getReadableDatabase();
						
			String createTable = "DROP TABLE IF EXISTS USER";
			db.execSQL(createTable);
			
			db.close();		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    	finally
		{
			events.close();
		}
    	
    	showSucess(til, msg, type);
    	
	}
    
    
	public void showSucess(String til, String msg, final int type)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle(til)
		     .setMessage(msg)
		     .setCancelable(false)
		
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					if (type == 3) 
					{
						intent = new Intent(getBaseContext(),MainActivity.class);						
						startActivity(intent);
					}
				}
			});

		alert.show();
	}
	
	
	public void showError(String msg)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Sorry !")
		     .setMessage(msg)
		     .setCancelable(false)
		     
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					//
				}
			});

		alert.show();
	}
	
}
