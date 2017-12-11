package com.example.android.rinkit;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mainListView;

    ArrayList<String> historyList = new ArrayList<String>();    // Used to save the game history

    private ImageButton button_start;
    private Button button_home_penalty_increase;
    private Button button_home_penalty_decrease;

    private Button button_visitor_penalty_increase;
    private Button button_visitor_penalty_decrease;

    private Button button_home_goal_increase;
    private Button button_home_goal_decrease;

    private Button button_visitor_goal_increase;
    private Button button_visitor_goal_decrease;

    private TextView text_visitor_score;
    private TextView text_home_penalty_1;

    private TextView text_home_penalty_2;
    private TextView text_home_score;

    private TextView text_visitor_penalty_1;
    private TextView text_visitor_penalty_2;

    private TextView text_period;

    private TextView text_gameTime;

    private Handler handler = new Handler();

    private int default_gametime = 60*20;       //One period  = 60 seconds * 20 minutes
    private int wholeGameTime = 0;              //Used to tag history
    private int default_penalty_time = 2*60;

    //these needs to be saved/restored during orientation change
    private int home_penalty_last = 1;
    private int visitor_penalty_last = 1;
    private boolean running = false;
    private int home_score = 0;
    private int visitor_score = 0;
    private boolean lastPeriod=false;
    private int gameTime = default_gametime;
    private int home_penalty_time_1 = 0;
    private int home_penalty_time_2 = 0;
    private int visitor_penalty_time_1 = 0;
    private int visitor_penalty_time_2 = 0;
    private int period = 1;
    private ArrayAdapter<String> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainListView = findViewById( R.id.mainListView );

        text_gameTime = findViewById(R.id.text_gametime);

        button_start = findViewById(R.id.button_start);
        button_start.setOnClickListener(this);

        button_home_penalty_increase = findViewById((R.id.button_home_penalty_increase));
        button_home_penalty_increase.setOnClickListener(this);

        button_home_penalty_decrease = findViewById((R.id.button_home_penalty_decrease));
        button_home_penalty_decrease.setOnClickListener(this);

        button_home_goal_increase = findViewById((R.id.button_home_goal_increase));
        button_home_goal_increase.setOnClickListener(this);

        button_home_goal_decrease = findViewById((R.id.button_home_goal_decrease));
        button_home_goal_decrease.setOnClickListener(this);

        button_visitor_penalty_increase = findViewById((R.id.button_visitor_penalty_increase));
        button_visitor_penalty_increase.setOnClickListener(this);

        button_visitor_penalty_decrease = findViewById((R.id.button_visitor_penalty_decrease));
        button_visitor_penalty_decrease.setOnClickListener(this);

        button_visitor_goal_increase = findViewById((R.id.button_visitor_goal_increase));
        button_visitor_goal_increase.setOnClickListener(this);

        button_visitor_goal_decrease = findViewById((R.id.button_visitor_goal_decrease));
        button_visitor_goal_decrease.setOnClickListener(this);

        text_visitor_score = findViewById(R.id.text_visitor_score);
        text_home_penalty_1 = findViewById(R.id.text_home_penalty_1);

        text_home_penalty_2 = findViewById(R.id.text_home_penalty_2);
        text_home_score = findViewById(R.id.text_home_score);

        text_visitor_penalty_1 = findViewById(R.id.text_visitor_penalty_1);
        text_visitor_penalty_2 = findViewById(R.id.text_visitor_penalty_2);

        text_period = findViewById(R.id.text_period);

        listAdapter = new ArrayAdapter<String>(this, R.layout.row, historyList);
        mainListView.setAdapter( listAdapter );

        text_gameTime.setText(String.format("%02d",gameTime / 60) + ":" + String.format("%02d",gameTime % 60));
        text_period.setText(String.valueOf(period));

    }

    //Save all needed values before orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("HOME_PENALTY_LAST", home_penalty_last);
        outState.putInt("VISITOR_PENALTY_LAST", visitor_penalty_last);
        outState.putBoolean("RUNNING", running);
        outState.putInt("HOME_SCORE", home_score);
        outState.putInt("VISITOR_SCORE", visitor_score);
        outState.putBoolean("LASTPERIOD", lastPeriod);
        outState.putInt("GAMETIME", gameTime);
        outState.putInt("HOME_PENALTY_TIME_1", home_penalty_time_1);
        outState.putInt("HOME_PENALTY_TIME_2", home_penalty_time_2);
        outState.putInt("VISITOR_PENALTY_TIME_1", visitor_penalty_time_1);
        outState.putInt("VISITOR_PENALTY_TIME_2", visitor_penalty_time_2);
        outState.putInt("PERIOD", period);
        outState.putStringArrayList("LISTADAPTER", historyList);

    }

    //Restore saved values after orientation change
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        home_penalty_last = savedInstanceState.getInt("HOME_PENALTY_LAST");
        visitor_penalty_last = savedInstanceState.getInt("VISITOR_PENALTY_LAST");
        running= savedInstanceState.getBoolean("RUNNING");
        home_score= savedInstanceState.getInt("HOME_SCORE");
        visitor_score= savedInstanceState.getInt("VISITOR_SCORE");
        lastPeriod = savedInstanceState.getBoolean("LASTPERIOD");
        gameTime= savedInstanceState.getInt("GAMETIME");
        home_penalty_time_1= savedInstanceState.getInt("HOME_PENALTY_TIME_1");
        home_penalty_time_2= savedInstanceState.getInt("HOME_PENALTY_TIME_2");
        visitor_penalty_time_1= savedInstanceState.getInt("VISITOR_PENALTY_TIME_1");
        visitor_penalty_time_2= savedInstanceState.getInt("VISITOR_PENALTY_TIME_2");
        period= savedInstanceState.getInt("PERIOD");
        historyList = savedInstanceState.getStringArrayList("LISTADAPTER");
        setAfterOrientation();

    }

    //Refresh all views after orientation change with saved values
    public void setAfterOrientation(){

        listAdapter = new ArrayAdapter<String>(this, R.layout.row, historyList);
        mainListView.setAdapter( listAdapter );
        text_gameTime.setText(String.format("%02d",gameTime / 60) + ":" + String.format("%02d",gameTime % 60));
        text_period.setText(String.valueOf(period));
        text_home_penalty_1.setText(String.format("%02d", home_penalty_time_1 / 60) + ":" + String.format("%02d", home_penalty_time_1 % 60));
        text_home_penalty_2.setText(String.format("%02d", home_penalty_time_2 / 60) + ":" + String.format("%02d", home_penalty_time_2 % 60));
        text_visitor_penalty_1.setText(String.format("%02d", visitor_penalty_time_1 / 60) + ":" + String.format("%02d", visitor_penalty_time_1 % 60));
        text_visitor_penalty_2.setText(String.format("%02d", visitor_penalty_time_2 / 60) + ":" + String.format("%02d", visitor_penalty_time_2 % 60));
        text_home_score.setText(String.valueOf(home_score));
        text_visitor_score.setText(String.valueOf(visitor_score));
    }

    //Overriding the onClick to handle all of the buttons
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.button_start):

                if (running) {
                    handler.removeCallbacks(runnable);
                    running = false;
                    disable_buttons(false);}

                else if (!running && !lastPeriod) {

                    if (gameTime == 0 && period <= 3) {
                        gameTime = default_gametime;
                        text_gameTime.setText(String.format("%02d",gameTime / 60) + ":" + String.format("%02d",gameTime % 60));
                        if (period == 3) lastPeriod = true;

                    }

                    handler.postDelayed(runnable, 1000);
                    disable_buttons(true);
                }
                    if (gameTime == 0 && lastPeriod){

                        Toast toast = Toast.makeText(this,"Game time over", Toast.LENGTH_SHORT);
                        toast.show();
                    }


                     break;

            case (R.id.button_home_goal_increase):
            case (R.id.button_home_goal_decrease):
            case (R.id.button_visitor_goal_increase):
            case (R.id.button_visitor_goal_decrease):

                setGoals(v);

                break;

            case (R.id.button_home_penalty_increase):
            case (R.id.button_home_penalty_decrease):
            case (R.id.button_visitor_penalty_increase):
            case (R.id.button_visitor_penalty_decrease):

                setPenalties(v);

                break;
        }
    }

    //This runs every 1 second if boolean runnable is TRUE. If running also every other button than play/pause is disabled
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            running = true;
            if (gameTime > 0) {             //Gametime counter
                gameTime = gameTime - 1;
                wholeGameTime = (period - 1) * default_gametime + (default_gametime - gameTime);
                text_gameTime.setText(String.format("%02d",gameTime / 60) + ":" + String.format("%02d",gameTime % 60));
            }
            else if (gameTime == 0) {
                running = false;
                if (period < 3) {
                    period++;
                    text_period.setText(String.valueOf(period));}

            }

            if (home_penalty_time_1 > 0) {             //Home penalty 1 counter
                home_penalty_time_1 --;
                text_home_penalty_1.setText(String.format("%02d",home_penalty_time_1 / 60) + ":" + String.format("%02d",home_penalty_time_1 % 60));
            }

            if (home_penalty_time_2 > 0) {             //Home penalty 2 counter
                home_penalty_time_2 --;
                text_home_penalty_2.setText(String.format("%02d",home_penalty_time_2 / 60) + ":" + String.format("%02d",home_penalty_time_2 % 60));
            }

            if (visitor_penalty_time_1 > 0) {          //Visitor penalty 1 counter
                visitor_penalty_time_1 = visitor_penalty_time_1 - 1;
                text_visitor_penalty_1.setText(String.format("%02d",visitor_penalty_time_1 / 60) + ":" + String.format("%02d",visitor_penalty_time_1 % 60));
            }

            if (visitor_penalty_time_2 > 0) {          //Visitor penalty 2 counter
                visitor_penalty_time_2 = visitor_penalty_time_2 - 1;
                text_visitor_penalty_2.setText(String.format("%02d",visitor_penalty_time_2 / 60) + ":" + String.format("%02d",visitor_penalty_time_2 % 60));
            }


            if(running) {
                handler.postDelayed(this, 1000);
            }
            else if (!running) disable_buttons(false);
        }
    };

    //Call with TRUE/FALSE parameter to disable/enable buttons
    public void disable_buttons(boolean temp) {

        if (temp) {              // if temp is TRUE then disable goal and penalty buttons

            button_home_penalty_increase.setEnabled(false);
            button_home_penalty_decrease.setEnabled(false);
            button_home_goal_increase.setEnabled(false);
            button_home_goal_decrease.setEnabled(false);

            button_visitor_penalty_increase.setEnabled(false);
            button_visitor_penalty_decrease.setEnabled(false);
            button_visitor_goal_increase.setEnabled(false);
            button_visitor_goal_decrease.setEnabled(false);

            button_start.setImageResource(android.R.drawable.ic_media_pause);

        } else if (!temp) {

            button_home_penalty_increase.setEnabled(true);
            button_home_penalty_decrease.setEnabled(true);
            button_home_goal_increase.setEnabled(true);
            button_home_goal_decrease.setEnabled(true);

            button_visitor_penalty_increase.setEnabled(true);
            button_visitor_penalty_decrease.setEnabled(true);
            button_visitor_goal_increase.setEnabled(true);
            button_visitor_goal_decrease.setEnabled(true);

            button_start.setImageResource(android.R.drawable.ic_media_play);

        }
    }

    //Handle all goal buttons
    public void setGoals(View v) {

        switch (v.getId()) {

            case (R.id.button_home_goal_increase):
                home_score++;
                text_home_score.setText(String.valueOf(home_score));
                addHistory("Home goal!");
                break;

            case (R.id.button_home_goal_decrease):
                if (home_score > 0) home_score--;
                text_home_score.setText(String.valueOf(home_score));
                break;

            case (R.id.button_visitor_goal_increase):
                visitor_score++;
                text_visitor_score.setText(String.valueOf(visitor_score));
                addHistory("Visitor goal!");
                break;

            case (R.id.button_visitor_goal_decrease):
                if (visitor_score > 0) visitor_score--;
                text_visitor_score.setText(String.valueOf(visitor_score));
                break;
        }

    }

    //Handle all penalty buttons
    public void setPenalties(View v) {

        switch (v.getId()) {

            case (R.id.button_home_penalty_increase):

                if (home_penalty_time_1 == 0) {
                    home_penalty_time_1 = default_penalty_time;
                    home_penalty_last = 1;
                    text_home_penalty_1.setText(String.format("%02d", home_penalty_time_1 / 60) + ":" + String.format("%02d", home_penalty_time_1 % 60));
                    addHistory("Home penalty");
                } else if (home_penalty_time_2 == 0) {
                    home_penalty_time_2 = default_penalty_time;
                    home_penalty_last = 2;
                    text_home_penalty_2.setText(String.format("%02d", home_penalty_time_2 / 60) + ":" + String.format("%02d", home_penalty_time_2 % 60));
                    addHistory("Home penalty");
                }

                break;

            case (R.id.button_home_penalty_decrease):
                if (home_penalty_last == 1) {
                    home_penalty_time_1 = 0;
                    text_home_penalty_1.setText("00:00");
                } else if (home_penalty_last == 2) {
                    home_penalty_time_2 = 0;
                    home_penalty_last = 1;
                    text_home_penalty_2.setText("00:00");

                }

                break;

            case (R.id.button_visitor_penalty_increase):

                if (visitor_penalty_time_1 == 0) {
                    visitor_penalty_time_1 = default_penalty_time;
                    visitor_penalty_last = 1;
                    text_visitor_penalty_1.setText(String.format("%02d", visitor_penalty_time_1 / 60) + ":" + String.format("%02d", visitor_penalty_time_1 % 60));
                    addHistory("Visitor penalty");
                } else if (visitor_penalty_time_2 == 0) {
                    visitor_penalty_time_2 = default_penalty_time;
                    visitor_penalty_last = 2;
                    text_visitor_penalty_2.setText(String.format("%02d", visitor_penalty_time_2 / 60) + ":" + String.format("%02d", visitor_penalty_time_2 % 60));
                    addHistory("Visitor penalty");
                }

                break;

            case (R.id.button_visitor_penalty_decrease):
                if (visitor_penalty_last == 1) {
                    visitor_penalty_time_1 = 0;
                    text_visitor_penalty_1.setText("00:00");
                } else if (visitor_penalty_last == 2) {
                    visitor_penalty_time_2 = 0;
                    visitor_penalty_last = 1;
                    text_visitor_penalty_2.setText("00:00");
                    break;
                }

        }
    }

    //Add new line to history ArrayList with anything as String parameter
    public void addHistory(String action){

        String completeAction = String.format("%02d",wholeGameTime / 60) + ":" + String.format("%02d",wholeGameTime % 60) + "    " + action;
        listAdapter.add(completeAction);
        mainListView.setSelection(listAdapter.getCount()); //Keep the last addition to list visible always

    }
}