package de.dascapschen.android.tabnavexample;

import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.transition.Scene;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity
{
    Toolbar toolbar;

    float currentBottomSheetPosition = 0.f;
    boolean bottomSheetOpen = false;
    boolean animationRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar( toolbar );

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this, getSupportFragmentManager());

        ViewPager pager = findViewById(R.id.viewPager);
        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);

        setupBottomSheetTransition();
    }

    private void setupBottomSheetTransition()
    {
        final ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.sceneRoot);
        final Scene collapsedScene = Scene.getSceneForLayout(sceneRoot, R.layout.bottomsheet_collapsed, this);
        final Scene openScene = Scene.getSceneForLayout(sceneRoot, R.layout.bottomsheet_open, this);

        final Transition transition = new AutoTransition();

        final TimeInterpolator interp = new TimeInterpolator()
        {
            @Override
            public float getInterpolation(float input)
            {
                Log.i("INTERP", String.format("Called with %f", input));
                return currentBottomSheetPosition;
            }
        };

        transition.setInterpolator(interp);
        transition.setDuration(99999999); //TODO: this is hacky af

        final BottomSheetBehavior bsb = BottomSheetBehavior.from(findViewById(R.id.sceneRoot));

        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View view, int i)
            {
                if( i == BottomSheetBehavior.STATE_EXPANDED )
                {
                    //TransitionManager.go(openScene, transition);
                    bottomSheetOpen = true;
                    animationRunning = false;
                    TransitionManager.endTransitions(sceneRoot);
                }
                if( i == BottomSheetBehavior.STATE_COLLAPSED )
                {
                    //TransitionManager.go(collapsedScene, transition);
                    bottomSheetOpen = false;
                    animationRunning = false;
                    TransitionManager.endTransitions(sceneRoot);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v)
            {
                Log.i("SLIDE", String.format("CALLED WITH: %f", v));
                if(bottomSheetOpen)
                {
                    if(!animationRunning)
                    {
                        Log.i("ANIM", "GO");
                        TransitionManager.go(collapsedScene, transition);
                        animationRunning = true;
                    }
                    currentBottomSheetPosition = 1-v;
                }
                else
                {
                    if(!animationRunning)
                    {
                        Log.i("ANIM", "GO");
                        TransitionManager.go(openScene, transition);
                        animationRunning = true;
                    }
                    currentBottomSheetPosition = v;
                }
            }
        });


    }


}
