package com.example.matt.prototest;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.transit.realtime.GtfsRealtime.*;
import com.google.protobuf.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity {

    PBReceiver resultPBReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button GObutton = (Button)findViewById(R.id.goButton);
        final EditText stopIDEdit = (EditText)findViewById(R.id.editText);
        final TextView timesView = (TextView)findViewById(R.id.stopTimesText);

        GObutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent fetchServiceIntent = new Intent(v.getContext(), PBFetchService.class);
                        fetchServiceIntent.putExtra("reciever",resultPBReceiver);
                        startService(fetchServiceIntent);
                        for (FeedEntity entity : realdata.getEntityList()){
                            if(!entity.hasTripUpdate())continue;
                            TripUpdate trip = entity.getTripUpdate();
                            if (!trip.hasTrip())continue;
                            for(TripUpdate.StopTimeUpdate stopTime : trip.getStopTimeUpdateList() ) {
                                if (stopTime.getStopId().equals(stopIDEdit.getText().toString())) {
                                    TripUpdate.StopTimeEvent stopEventArrival = stopTime.getArrival();
                                    long unixSeconds = stopEventArrival.getTime();
                                    Date date = new Date(unixSeconds*1000);
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss z");
                                    String formattedDate = sdf.format(date);
                                    System.out.println(String.valueOf(stopEventArrival.getTime()));
                                }
                            }
                        }
                    }
                }
        );    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class PBReceiver extends ResultReceiver {
        public PBReceiver(Handler handler){
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Handle response from IntentService here
        }
    }
}
