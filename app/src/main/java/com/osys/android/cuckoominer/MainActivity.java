package com.osys.android.cuckoominer;

/**
 * This application is based on the SimpleMiner.java by John Tromp.
 *
 * Cuckoo Cycle, a memory-hard proof-of-work
 * Copyright (c) 2013-2016 John Tromp
 */

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextView myView, mySolnsView, myCoresView, myEasynessView, myHeaderView, txtTimer;
    Button myStartButton, myStopButton;
    Performance perf;

    long startTime=0L,timeInMilliseconds=0L;

    Handler customHandler = new Handler();
    LinearLayout container;

    Runnable updateTimeThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds=SystemClock.uptimeMillis()-startTime;
            customHandler.postDelayed(this,0);
            txtTimer.setText("" + String.format("%2d",timeInMilliseconds));

            mySolnsView.setText("" + perf.solutionsFound);
            myHeaderView.setText("" + perf.headersTried);

            myView.setText("" + String.format( "%.3f", (double)perf.solutionsFound/(double)(timeInMilliseconds/1000)) + " gps");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);

        Log.i("CUCKOOMINER", "MainActivity - onCreate");
        myView = (TextView)findViewById(R.id.gps);
        mySolnsView = (TextView)findViewById(R.id.cyclesFoundValue);
        myStartButton = (Button)findViewById(R.id.startButton);
        myStopButton = (Button)findViewById(R.id.stopButton);

        myCoresView = (TextView)findViewById(R.id.coresValue);
        myEasynessView = (TextView)findViewById(R.id.easynessValue);
        myHeaderView = (TextView)findViewById(R.id.noHeadersValue);

        myCoresView.setText("" + SolutionManager.getInstance().NUMBER_OF_CORES);
        myCoresView.setText("" + SolutionManager.getInstance().easipct);

        perf = new Performance();
        myStopButton.setEnabled(false);
        myStopButton.setBackgroundColor(Color.rgb(129,129,129));
        myStartButton.setBackgroundColor(Color.rgb(54,128,0));
        myStartButton.setTextColor(Color.rgb(50,255,0));

        txtTimer = (TextView)findViewById(R.id.chronoValue);
        container = (LinearLayout)findViewById(R.id.container);

        myStartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startTime=SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimeThread,0);

                Log.i("CUCKOOMINER", "MainActivity - buttonClickCuckooSolver");
                myStartButton.setEnabled(false);
                myStopButton.setEnabled(true);

                myStopButton.setBackgroundColor(Color.rgb(128,0,0));
                myStopButton.setTextColor(Color.rgb(223,0,0));

                myStartButton.setBackgroundColor(Color.rgb(129,129,129));
                myStartButton.setTextColor(Color.rgb(73,73,75));

                SolutionManager.findSolutions(myView,perf);
            }
        });

        myStopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                customHandler.removeCallbacks(updateTimeThread);
                Log.i("CUCKOOMINER", "MainActivity - buttonClickCuckooSolver");
                myStartButton.setEnabled(true);
                myStopButton.setEnabled(false);

                myStopButton.setBackgroundColor(Color.rgb(129,129,129));
                myStopButton.setTextColor(Color.rgb(73,73,75));

                myStartButton.setBackgroundColor(Color.rgb(54,128,0));
                myStartButton.setTextColor(Color.rgb(50,255,0));

                SolutionManager.cancelAll();

            }
        });

    }

}
