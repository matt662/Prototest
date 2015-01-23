package com.example.matt.prototest;

import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button GObutton = (Button)findViewById(R.id.goButton);
        final EditText stopCodeEdit = (EditText)findViewById(R.id.editText);
        GObutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        String stopId; //not what is posted on bus stop sign
                        stopId = translateStopId(stopCodeEdit.getText().toString()); //stop code is what is posted on bus stop sign
                        Intent fetchTimesIntent = new Intent(v.getContext(), FetchTimesService.class);
                        fetchTimesIntent.putExtra("receiver",new timesReceiver(new Handler()));
                        fetchTimesIntent.putExtra("stopID",stopId);
                        startService(fetchTimesIntent);

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

    private String translateStopId(String stopCode){
        String stopId = "";

        try {
            InputStreamReader is = new InputStreamReader(getAssets().open("stops.txt"));
            BufferedReader reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] RowData = line.split(",");
                if (stopCode.equals(RowData[2]))stopId = RowData[4];
            }
            is.close();
        }
        catch (IOException ex) {
            // handle exception
        }
        return stopId;
    }

    class timesReceiver extends ResultReceiver {
        public timesReceiver(Handler handler){
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode,resultData);
            final TextView timesView = (TextView)findViewById(R.id.stopTimesText);
            String times;
            if (resultCode == FetchTimesService.TIMES_UPDATE){
                times = resultData.getString("stopTimes");
                timesView.setText(times);
            }
        }
    }
}
