package com.etisalat.MobileAssistant;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TestFragmentAdapter extends FragmentPagerAdapter  
{
    protected static final String[] CONTENT = new String[] 
    { "A service that enables a user - in this case an Executive - to pair another number (an Assistant) such that when the Executive number is called it rings on both the Executive and the Assistant’s number.", 
      "This is the default service mode. It is set up in such a way that when the call is rejected by the Executive, it starts ringing on the Assistant’s number.",
      "This is set up in such a way that once the Executive’s phone rings unanswered after the configured time – for example 10 seconds - the Assistant’s number will start to ring.",
      "This is set up in such a way that when the Executive number is called it rings on the Executive and Assistant phones simultaneously; therefore, whoever answers the call first attends to the caller.", };

    private int mCount = CONTENT.length;

    public TestFragmentAdapter(FragmentManager fm) 
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) 
    {
        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() 
    {
        return mCount;
    }

    public void setCount(int count) 
    {
        if (count > 0 && count <= 10) 
        {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}