package de.dascapschen.android.tabnavexample;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter
{
    private final Context context;

    public MyPagerAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int index)
    {
        switch(index)
        {
            case 0:
                return new MainFragment();
            case 1:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int index)
    {
        switch(index)
        {
            case 0:
                return context.getString(R.string.main_fragment_title);
            case 1:
                return context.getString(R.string.settings_fragment_title);
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return 2;
    }
}
