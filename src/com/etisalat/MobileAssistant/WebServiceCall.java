// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.etisalat.MobileAssistant;

import android.content.Context;
import android.os.AsyncTask;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.lcsky.SVProgressHUD;

public class WebServiceCall extends AsyncTask<String, Void, String>
{
    private String newUrl;
    private String osType;
    private String token;
    private String version;
    private Context ctx;

    public WebServiceCall(Context context)
    {
    	// Test URL
    	//newUrl = "http://41.190.16.55:8080/mobile-api/mobile-assistant/";
    	//token = "ha57ghTr$!LkTfrBN202664Q";
        //osType = "android";
        //version = "1.1.2";
    	
        // Live URL
        newUrl = "http://41.190.16.170:8080/mobile-api/mobile-assistant/";
        token = "06f323ff6534b927938134353f5fa8e7";
        osType = "android";
        version = "1.2";
        
        ctx = context;
    }

    public String doExecute(String action) throws InterruptedException, ExecutionException
    {
        newUrl += action;
        return execute(newUrl).get();
    }
             
      
	@Override
    protected void onPreExecute() 
	{
		super.onPreExecute();
		// Showing progress dialog
		SVProgressHUD.showInView(ctx, "Please wait ...", true);
    }
        
    protected String doInBackground(String... params)
    {
        String result = "";
        
        try
        {
        	DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(params[0]);
            httpget.setHeader("application/json","Content-Type");
            httpget.setHeader("application/json","Accept");
            httpget.setHeader("x-emts-app-token",token);
            httpget.setHeader("x-emts-os-type",osType);
            httpget.setHeader("x-emts-app-version",version);
            
            HttpResponse response = httpclient.execute(httpget);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity;
            
            if (status == 200) 
            {
                entity = response.getEntity();
                result = EntityUtils.toString(entity);
            }
            
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            return null;
        }
        finally
        {
        	newUrl = "";
        }
        
        return result;
    }

    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        SVProgressHUD.dismiss(ctx);
    }    

}