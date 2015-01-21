package com.example.matt.prototest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.transit.realtime.GtfsRealtime.*;
import com.google.protobuf.*;

import java.io.*;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button GObutton = (Button)findViewById(R.id.goButton);
        final EditText stopIDEdit = (EditText)findViewById(R.id.editText);

        GObutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            URL url = new URL("http://opendata.hamilton.ca/GTFS-RT/GTFS_TripUpdates.pb");
                            InputStream is = url.openStream();
                            FeedMessage realdata = FeedMessage.parseFrom(is);
                            String times="";
                            for (FeedEntity entity : realdata.getEntityList()){
                                if(!entity.hasTripUpdate())continue;
                                TripUpdate trip = entity.getTripUpdate();
                                if (!trip.hasTrip())continue;
                                TripUpdate.StopTimeUpdate stopTime = trip.getStopTimeUpdate();//TODO find out which id to use for getStopTimeUpdate
                                if (stopTime.getStopId().equals(stopIDEdit.getText().toString())){
                                    TripUpdate.StopTimeEvent stopEventArrival = stopTime.getArrival();
                                    times = times +", " + String.valueOf(stopEventArrival.getTime());
                                }
                            }
                        }
                        catch(Exception e){
                            System.out.println("error");
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
}
