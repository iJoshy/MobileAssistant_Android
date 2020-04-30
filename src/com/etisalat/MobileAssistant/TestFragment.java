package com.etisalat.MobileAssistant;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class TestFragment extends Fragment 
{
    private static final String KEY_CONTENT = "TestFragment:Content";

    public static TestFragment newInstance(String content) 
    {
        TestFragment fragment = new TestFragment();

        StringBuilder builder = new StringBuilder();
        builder.append(content);
        
        fragment.mContent = builder.toString();

        return fragment;
    }

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) 
        {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @SuppressWarnings("deprecation")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	
    	Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "etisalat.TTF");
    	
        TextView text = new TextView(getActivity());
        text.setGravity(Gravity.CENTER);
        text.setText(mContent);
        text.setTypeface(font);
        text.setTextSize(15);
        text.setPadding(40, 40, 40, 40);             	     

        LinearLayout layout = new LinearLayout(getActivity());
        
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        
        layoutParam.setMargins(20, 130, 20, 20); 
        //layoutParam.weight=(float) 0.85;
        
       	text.setLayoutParams(layoutParam);
        
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER);
        layout.addView(text);
        
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) 
    {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
