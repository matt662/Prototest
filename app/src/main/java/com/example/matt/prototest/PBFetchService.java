package com.example.matt.prototest;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.google.transit.realtime.GtfsRealtime.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PBFetchService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PBFetchService() {
        super("PBFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            URL url = new URL("http://opendata.hamilton.ca/GTFS-RT/GTFS_TripUpdates.pb");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            System.out.println("Opened Connection");
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            System.out.println("Created input stream");
            //AssetManager assetManager = getAssets();
            //InputStream is = assetManager.open("GTFS_TripUpdates.pb");
            FeedMessage realdata = FeedMessage.parseFrom(is);
            System.out.println("parsed data");
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
