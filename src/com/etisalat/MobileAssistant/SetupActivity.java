package com.etisalat.MobileAssistant;

import com.etisalat.thirdparty.EventsData;
import org.json.JSONException;
import org.json.JSONObject;

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


public class SetupActivity extends ActionBarActivity 
{
	private Intent intent;
	private int delayTime = 10;
	private EditText  editDelayText;
	private Button btnControl;
	private Button btnTime;
	private Button btnLive;
	private String jsonResponse;
    private static WebServiceCall wsc;
    private JSONObject jsonobject;
    private String asstNoString;
    private String asstTypeString;
    private String asstTimeString;
	private String enabledDisabled;
	private FrameLayout timerFrame;
	private TextView asstTimeText;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private EventsData events;
	
	
	@SuppressLint("InflateParams") @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		
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
        View customView = li.inflate(R.layout.ab_custom, null);
        actionbar.setCustomView(customView);

        TextView ibItem1 = (TextView) customView.findViewById(R.id.leftBtn);
        ibItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	//System.out.println(" :::: left clicked ::::");
            	subscribeClicked();
            }
        });

        TextView ibItem2 = (TextView) customView.findViewById(R.id.rightBtn);
        ibItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	//System.out.println(" :::: right clicked ::::");
            	optoutClicked();
            }
        });
        
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "etisalat.TTF");
        
        ((TextView)findViewById(R.id.leftBtn)).setTypeface(typeface);
        ((TextView)findViewById(R.id.title)).setTypeface(typeface);
        ((TextView)findViewById(R.id.rightBtn)).setTypeface(typeface);
        
        EditText  editAsstNoEditText = (EditText)findViewById(R.id.asstNo);
        editAsstNoEditText.setTypeface(typeface);
        editAsstNoEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        
        TextView  asstNoText = (TextView)findViewById(R.id.txtAsstNo);
        asstNoText.setTypeface(typeface);
        asstNoText.setText("enter mobile assistant number");
        
        TextView  asstTypeText = (TextView)findViewById(R.id.txtAssttype);
        asstTypeText.setTypeface(typeface);
        asstTypeText.setText("select mobile assistant type");
        
        asstTimeText = (TextView)findViewById(R.id.txtTimetype);
        asstTimeText.setTypeface(typeface);
        asstTimeText.setText("select delay time");
        asstTimeText.setVisibility(View.INVISIBLE);
        
        editDelayText = (EditText)findViewById(R.id.asstTimer);
        editDelayText.setTypeface(typeface);
        editDelayText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        
        btnControl = (Button)findViewById(R.id.controlBtn);
        btnTime = (Button)findViewById(R.id.timedBtn);
        btnLive = (Button)findViewById(R.id.liveBtn);
        
        timerFrame = (FrameLayout)findViewById(R.id.setupTimer);
        timerFrame.setVisibility(View.INVISIBLE);
        
        asstTypeString = "400";
        asstTimeString = "10";
        editDelayText.setText(asstTimeString + " : seconds");
        enabledDisabled = "1";
        
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

		btnControl.setBackgroundResource(R.drawable.checked);
		btnTime.setBackgroundResource(R.drawable.unchecked);
		btnLive.setBackgroundResource(R.drawable.unchecked);
		
		asstTimeText.setVisibility(View.INVISIBLE);
		timerFrame.setVisibility(View.INVISIBLE);
		
		asstTypeString = "402";
	}
	
	public void timedClicked(View V)
	{
		//System.out.println(" :::: time clicked ::::");
		
		btnControl.setBackgroundResource(R.drawable.unchecked);		
		btnTime.setBackgroundResource(R.drawable.checked);		
		btnLive.setBackgroundResource(R.drawable.unchecked);
		
		asstTimeText.setVisibility(View.VISIBLE);
		timerFrame.setVisibility(View.VISIBLE);
		
		if (!asstTypeString.equals("401")) 
		{
			showTimerAlert();
		}
		
		
		asstTypeString = "401";
	}
	
	
	public void showTimerAlert()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setMessage("\nThe default delay time is 10 seconds. Select your prefered delay time below\n")
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
	
	
	public void liveClicked(View V)
	{
		//System.out.println(" :::: live clicked ::::");

		btnControl.setBackgroundResource(R.drawable.unchecked);
		btnTime.setBackgroundResource(R.drawable.unchecked);
		btnLive.setBackgroundResource(R.drawable.checked);
		
		asstTimeText.setVisibility(View.INVISIBLE);
		timerFrame.setVisibility(View.INVISIBLE);
		
		asstTypeString = "400";
	}
	
	
	public void subscribeClicked()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Notice")
		     .setMessage("You will be charged N750 for this service. Do you wish to continue ?")
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
					processing(1);
				}
			});

		alert.show();
	}
	
	public void optoutClicked()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Notice")
		     .setMessage("This action will deactivate your Mobile Assistant subscription. click ok to confirm.")
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
					processing(2);
				}
			});

		alert.show();
	}
	
	public void addClicked(View v)
	{
		
		EditText  editAsstNoEditText = (EditText)findViewById(R.id.asstNo);
		asstNoString = editAsstNoEditText.getText().toString();
		asstTimeString = Integer.toString(delayTime);
		
		System.out.println("\n\n assistant_msisdn is :::: "+asstNoString);
        System.out.println("\n\n assistant_type is :::: "+asstTypeString);
        System.out.println("\n\n assistant_time is :::: "+asstTimeString);
        System.out.println("\n\n enableDisable is :::: "+enabledDisabled);
		
		if (asstNoString.length() == 0) 
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
			processing(3);
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
            	txnType = "subscribe";
            else if (action == 2)
            	txnType = "unsubscribe";
            else if (action == 3)
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
        	
            //code = 200;
            //descr = "Successful";
            
		}
        catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        System.out.println("\n\n code is :::: " +code);
        System.out.println("\n\n description is :::: "+descr);
        
        //code = 200;
        
        if (code == 200)
        {
        	String msg = "";
        	String til = "";
        	
        	if (type == 1)
        	{
        		msg = "Your request is being processed please wait for a confirmation SMS thank you.";
        		til = "Congratulations";
        		showSucess(til, msg, type);
			}
        	else if (type == 2)
        	{        			
        		msg = "You have been deactivated from the Mobile Assistant service.";
        		til = "Notice";
        		
        		deleteRecordDB(til, msg, type);
        		
			}
        	else if (type == 3)
        	{        		       		
        		msg = "Your Mobile Assistant service is now fully activated.";
        		til = "Congratulations";
        		
        		saveToDB(til, msg, type);
			}
        	
        }       
        else
        {
        	 String msg = "";
             
        	 if (code == 201)
                 msg = "Mobile Assistant cost N750, check your balance and subscribe again";
             else if (code == 230)
                 msg = "Please ensure your device is connected to etisalat mobile data";
             else
                 msg = "Your request was not succesful. Please try again later";
             
             showError(msg);
        }
		
    }	
    
    
    public void saveToDB(String til, String msg, int type)
	{
    	events = new EventsData(this);
    	
		try
		{			
			SQLiteDatabase db = events.getReadableDatabase();
						
			String insert2Table = "INSERT OR REPLACE INTO USER (ASSISTANT_TYPE, ASSISTANT_NUMBER, ASSISTANT_TIME, ENABLED_DISABLED) VALUES ('"+asstTypeString+"', '"+asstNoString+"', '"+asstTimeString+"', '"+enabledDisabled+"')";
			db.execSQL(insert2Table);
			
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
			SQLiteDatabase db = events.getWritableDatabase();
						
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
					if (type == 2) 
					{
						intent = new Intent(getBaseContext(),MainActivity.class);						
						startActivity(intent);
					}
					else if (type == 3) 
					{
						intent = new Intent(getBaseContext(),SettingsActivity.class);						
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
