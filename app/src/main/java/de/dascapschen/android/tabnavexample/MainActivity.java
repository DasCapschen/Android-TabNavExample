package de.dascapschen.android.tabnavexample;

import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
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

    Scene bottomSheetCollapsedScene;
    Scene bottomSheetOpenScene;
    ViewGroup sceneRoot;
    AutoTransition bottomSheetTransition;
    TimeInterpolator bottomSheetInterpolator;
    BottomSheetBehavior bottomSheet;

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
        sceneRoot = (ViewGroup) findViewById(R.id.sceneRoot);
        bottomSheetCollapsedScene = Scene.getSceneForLayout(sceneRoot, R.layout.bottomsheet_collapsed, this);
        bottomSheetOpenScene = Scene.getSceneForLayout(sceneRoot, R.layout.bottomsheet_open, this);

        bottomSheetTransition = new AutoTransition();

        bottomSheetInterpolator = new TimeInterpolator()
        {
            @Override
            public float getInterpolation(float input)
            {
                //Log.i("INTERP", String.format("Called with %f", input));
                return currentBottomSheetPosition;
            }
        };

        bottomSheetTransition.setInterpolator(bottomSheetInterpolator);
        bottomSheetTransition.setDuration(999999999); //TODO: this is hacky af

        //run all animations at the same time ; fixes closing anim not working
        //because auto anim is a transition SET
        //first fade out removed items, then translate items, then fade in new items = 3 anims
        bottomSheetTransition.setOrdering( TransitionSet.ORDERING_TOGETHER );

        bottomSheet = BottomSheetBehavior.from(findViewById(R.id.sceneRoot));

        bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i)
            {
                String state = "";
                switch( i )
                {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetOpen = false;
                        animationRunning = false;
                        TransitionManager.endTransitions(sceneRoot);
                        state = "collapsed";
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheetOpen = true;
                        animationRunning = false;
                        TransitionManager.endTransitions(sceneRoot);
                        state = "expanded";
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        state = "dragging";
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        state = "half expanded";
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        state = "settling";
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        state = "hidden";
                        break;
                }

                Log.i("STATE", "CHANGED TO: " + state);
            }

            @Override
            public void onSlide(@NonNull View view, float v)
            {
                Log.i("SLIDE", String.format("CALLED WITH: %f", v));
                if(bottomSheetOpen)
                {
                    if(!animationRunning)
                    {
                        //Log.i("ANIM", "GO");
                        TransitionManager.go(bottomSheetCollapsedScene, bottomSheetTransition);
                        animationRunning = true;
                    }
                    currentBottomSheetPosition = 1-v;
                }
                else
                {
                    if(!animationRunning)
                    {
                        //Log.i("ANIM", "GO");
                        TransitionManager.go(bottomSheetOpenScene, bottomSheetTransition);
                        animationRunning = true;
                    }
                    currentBottomSheetPosition = v;
                }
            }
        });
    }

    public void bottomSheetTapped(View view)
    {
        if(!bottomSheetOpen)
        {
            bottomSheet.setState( BottomSheetBehavior.STATE_EXPANDED );
        }
    }

    public void btnCloseBottomSheetTapped(View btn)
    {
        bottomSheet.setState( BottomSheetBehavior.STATE_COLLAPSED );
    }

}
