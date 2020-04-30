package com.etisalat.MobileAssistant;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import com.viewpagerindicator.CirclePageIndicator;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity implements OnPageChangeListener  
{
	private Intent intent;
    private String jsonResponse;
    private static WebServiceCall wsc;
    private JSONObject jsonobject;
    private CirclePageIndicator titleIndicator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new TestFragmentAdapter(getSupportFragmentManager()));

        //Bind the title indicator to the adapter
        titleIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        titleIndicator.setViewPager(pager);
        
        titleIndicator.setOnPageChangeListener(this);             
        titleIndicator.setCurrentItem(1);
        
        wsc = new WebServiceCall(this);
    }
   
	@Override
	public void onDestroy()
	{
	    super.onDestroy();
	    
	    if(wsc != null)
	    	wsc = null;
	}    
    
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) 
	{
		//System.out.println(" good ::: "+ arg0);
		LinearLayout layout = (LinearLayout)findViewById(R.id.walkthrough);
		
		if( arg0 == 0 )
		{
			layout.setBackgroundResource(R.drawable.back1);
		}
		else if( arg0 == 1 )
		{
			layout.setBackgroundResource(R.drawable.back2);
		}
		else if( arg0 == 2 )
		{
			layout.setBackgroundResource(R.drawable.back3);
		}
		else if( arg0 == 3 )
		{
			layout.setBackgroundResource(R.drawable.back4);
		}
		
	}


	@Override
	public void onPageScrollStateChanged(int arg0) 
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void subscribeClicked(View v)
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
					actionTaken();
				}
			});

		alert.show();
	}
	

	public void actionTaken()
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
        	
            jsonResponse = wsc.doExecute("subscribe");
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
			//e.printStackTrace();
        	System.out.println("Error passing JSON String");
		}
        
        
        System.out.println("\n\n code is :::: " +code);
        System.out.println("\n\n description is :::: "+descr);
        
        //code = 200;
        
        if (code == 200)
        {
        	showSucess();
        }       
        else
        {
        	 String msg = "";
             
        	 if (code == 230)
                 msg = "Please ensure your device is connected to etisalat mobile data";
             else
                 msg = "Your request was not succesful. Please try again later";
             
             showError(msg);
        }
		
    }	
    
    
	public void showSucess()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Congratulations")
		     .setMessage("Your request is being processed please wait for a confirmation SMS thank you.")
		     .setCancelable(false)
		
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{ 
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					intent = new Intent(getBaseContext(),SetupActivity.class);					
					startActivity(intent);
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
	
	
	public void homeClicked(View v)
	{
		//System.out.println("\n\n :::: home clicked :::: ");
		titleIndicator.setCurrentItem(0);
	}

}
