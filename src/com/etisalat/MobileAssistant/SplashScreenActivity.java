package com.etisalat.MobileAssistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.etisalat.thirdparty.EventsData;
import org.json.JSONException;
import org.json.JSONObject;

// Referenced classes of package com.example.mobileassistant:
// WebServiceCall, MainActivity, SettingsActivity, SetupActivity

public class SplashScreenActivity extends Activity
{
    private String asstNo;
    private String asstType;
    private String asstTime;
    private String enableDisable;
    private String descr;
    private int code;
    private Intent intent;
    private String jsonResponse;
    private String mobile;
    private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private EventsData events;
    private static WebServiceCall wsc;
    private JSONObject jsonobject;
    private DialogInterface dialog = null;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_splash_screen);		
			
		preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);  
	    editor = preferences.edit();	    	    	    
	    
	}
    
    
	@Override
	protected void onResume()
	{
		super.onResume();
		
		viewDidAppear();
		
		//intent = new Intent(getBaseContext(),SettingsActivity.class);				
	    //startActivity(intent);
	}
	
	
	@Override
	public void onDestroy()
	{
	    super.onDestroy();
	    
	    if(wsc != null)
	    	wsc = null;
	}
	
    public void viewDidAppear()
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
        	 
        	jsonResponse = wsc.doExecute("query");
            System.out.println("response ::: "+jsonResponse);
            		            
            processResult();         
        }
        catch(Exception e)
		{
			e.printStackTrace();
		}
    	
    }
    
    public void processResult()
    {
    	
    	System.out.println("processResult Y :::: "+jsonResponse);
        		
        try 
		{
        	
			jsonobject = new JSONObject(jsonResponse);
			
			code = jsonobject.getInt("code");
	        descr = jsonobject.getString("description");
	        mobile = jsonobject.getString("msisdn");
	        asstNo = jsonobject.getString("asst_msisdn");
	        asstType = jsonobject.getString("asst_type");
	        asstTime = jsonobject.getString("timeout");
	        
	        
            code = 200;
            descr = "Successful";
            mobile = "08099440203";
            asstNo = "08099440449";
            asstType = "Control";
            asstTime = "15";
            enableDisable = "true";
            
            
	        if (code == 200)
	        {
	        	enableDisable = jsonobject.getString("enabled");
	        	
	        	if(enableDisable.equals("true"))
	            {
	            	enableDisable = "1";
	            }
	            else if(enableDisable.equals("false"))
	            {
	            	enableDisable = "0";
	            }
	        }
            
		}
        catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
        	System.out.println("Error passing JSON String");
		}
        
        
        if (asstType == "" || asstType == null) 
        {
        	asstType = "";
            asstNo = "";
            asstTime = "";
            enableDisable = "";
        }
        else 
        {
            if (asstNo.startsWith("234"))
            {
                asstNo = "0" + asstNo.substring(3);
            }
            
            if(asstType.equals("Live"))
            {
            	asstType = "400";
            }
            else if(asstType.equals("Timed"))
            {
                asstType = "401";
            }
            else if(asstType.equals("Control"))
            {
            	asstType = "402";
            }
            
            if (asstTime == "" || asstTime == null) 
            {
                asstTime = "10";
            }                        
            
		}
        
        System.out.println("\n\n code is :::: " +code);
        System.out.println("\n\n description is :::: "+descr);
        System.out.println("\n\n msisdn is :::: "+mobile);
        System.out.println("\n\n assistant_msisdn is :::: "+asstNo);
        System.out.println("\n\n assistant_type is :::: "+asstType);
        System.out.println("\n\n assistant_time is :::: "+asstTime);
        System.out.println("\n\n enableDisable is :::: "+enableDisable);
          
        if (code == 200)
        {
            EligibleSubscribedSetup();
        }
        else if (code == 304)
        {
            EligibleSubscribed();
        }
        else
        {
            CheckEligiblitiy();
        }
		
    }
    
    
    public void EligibleSubscribedSetup()
    {
    	events = new EventsData(this);
    	
		try
		{			
			SQLiteDatabase db = events.getWritableDatabase();
						
			String createTable = "CREATE TABLE IF NOT EXISTS USER (ASSISTANT_TYPE TEXT, ASSISTANT_NUMBER TEXT, ASSISTANT_TIME TEXT, ENABLED_DISABLED TEXT)";
			db.execSQL(createTable);
			
			String insert2Table = "INSERT OR REPLACE INTO USER (ASSISTANT_TYPE, ASSISTANT_NUMBER, ASSISTANT_TIME, ENABLED_DISABLED) VALUES ('"+asstType+"', '"+asstNo+"', '"+asstTime+"', '"+enableDisable+"')";
			db.execSQL(insert2Table);
			
			db.close();

			editor.putString("ASSISTANT_TYPE", asstType);
			editor.putString("ASSISTANT_NUMBER", asstNo);
			editor.putString("ASSISTANT_TIME", asstTime);
			editor.putString("ENABLED_DISABLED", enableDisable);
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
		
		showCreatedSucess();
		
    }
    
    
    public void showCreatedSucess()
    {
    	    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Welcome");
		alert.setMessage("You are an active mobile assistant service subscriber. You may now update your settings.\n");
		alert.setCancelable(false);

		dialog = alert.show();
		
		Handler handler = null;
	    handler = new Handler(); 
	    handler.postDelayed(new Runnable()
	    { 
	         public void run()
	         {
	        	 dialog.cancel();
	        	 dialog.dismiss();
	             
	             intent = new Intent(getBaseContext(),SettingsActivity.class);				
			     startActivity(intent);
	         }
	    }, 5000);
		
    }
        
    public void EligibleSubscribed()
    {
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Welcome");
		alert.setMessage("You have already subscribed for the mobile assistant service. You can now setup your assistant.\n");
		alert.setCancelable(false);

	    dialog = alert.show();
		
		Handler handler = null;
	    handler = new Handler(); 
	    handler.postDelayed(new Runnable()
	    { 
	         public void run()
	         {
	        	 dialog.cancel();
	        	 dialog.dismiss();
	             
	             intent = new Intent(getBaseContext(),SetupActivity.class);				
			     startActivity(intent);
	         }
	    }, 5000);
		
    }
    
    public void CheckEligiblitiy()
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
        		 
             jsonResponse = wsc.doExecute("verify");
             System.out.println("response ::: "+jsonResponse);
             		
             processResult2();
        	          
         }
         catch(Exception e)
 		{
 			e.printStackTrace();
 		}
    }
    
    
    public void processResult2()
    {
    	
    	System.out.println("processResult :::: "+jsonResponse);
        		
        try 
		{
        	
			jsonobject = new JSONObject(jsonResponse);
	
			code = jsonobject.getInt("code");
	        descr = jsonobject.getString("description");
            
		}
        catch (JSONException e) 
		{
			// TODO Auto-generated catch block
        	System.out.println("Error passing JSON String");
            //e.printStackTrace();
		}
        
        //code = 302;
        
        System.out.println("\n\n code is :::: " +code);
        System.out.println("\n\n description is :::: "+descr);
          
        if (code == 302)
        {
        	showSucessWithDuration();
        }
        else
        {
        	 String msg = "";
             
             if (code == 230) 
            	 msg = "Please ensure your device is connected to etisalat mobile data";
             else if (code == 303) 
            	 msg = "Eligible packages for Mobile Assistant are Easybusiness, Easyflex 5000, Classic & Easylife Hybrid (PostPaid)";
             else
                 msg = "Your request was not succesful. Please try again later";
             
             showError(msg);
        }
		
    }
    
	public void showSucessWithDuration()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Welcome")
		     .setMessage("You're eligible for the mobile assistant service. Simply subscribe and add an assistant.\n")
		     .setCancelable(false);
		
		dialog = alert.show();
		
		Handler handler = null;
	    handler = new Handler(); 
	    handler.postDelayed(new Runnable()
	    { 
	         public void run()
	         {
	        	 dialog.cancel();
	        	 dialog.dismiss();
	             
	             createDB();
	         }
	    }, 5000);
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
	
	
	public void createDB()
	{
		
		events = new EventsData(this);
		
		try
		{			
			SQLiteDatabase db = events.getWritableDatabase();
						
			String createTable = "CREATE TABLE IF NOT EXISTS USER (ASSISTANT_TYPE TEXT, ASSISTANT_NUMBER TEXT, ASSISTANT_TIME TEXT, ENABLED_DISABLED TEXT)";
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
				
		intent = new Intent(getBaseContext(),MainActivity.class);				
		startActivity(intent);
		
	}
    
    
}

