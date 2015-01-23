package com.example.matt.prototest;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.transit.realtime.GtfsRealtime.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class FetchTimesService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public static final int TIMES_UPDATE = 1000;
    public FetchTimesService() {
        super("FetchTimesService");
    }
    private String times="";

    @Override
    protected void onHandleIntent(Intent intent) {
        FeedMessage realData;
        String stopId = intent.getStringExtra("stopID");
        try {
            URL url = new URL("http://opendata.hamilton.ca/GTFS-RT/GTFS_TripUpdates.pb");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            System.out.println("Opened Connection");
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            System.out.println("Created input stream");
            realData = FeedMessage.parseFrom(is);
            System.out.println("parsed data");
            is.close();
            for (FeedEntity entity : realData.getEntityList()){
                if(!entity.hasTripUpdate())continue;
                TripUpdate trip = entity.getTripUpdate();
                if (!trip.hasTrip())continue;
                for(TripUpdate.StopTimeUpdate stopTime : trip.getStopTimeUpdateList() ) {
                    if (stopTime.getStopId().equals(stopId)){
                        TripUpdate.StopTimeEvent stopEventArrival = stopTime.getArrival();
                        long unixSeconds = stopEventArrival.getTime();
                        Date date = new Date(unixSeconds*1000);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                        String formattedDate = sdf.format(date);
                        System.out.println(String.valueOf(stopEventArrival.getTime()));
                        times = times + formattedDate +"\n";
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        Bundle resultData = new Bundle();
        resultData.putString("stopTimes",times);
        receiver.send(TIMES_UPDATE,resultData);
    }
}
