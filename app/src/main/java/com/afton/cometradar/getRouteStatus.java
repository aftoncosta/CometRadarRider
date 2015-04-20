package com.afton.cometradar;


import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class getRouteStatus extends AsyncTask<Void, Void, String> {

int size;

    protected String doInBackground(Void... arg0) {

        //System.out.println("GETTING ROUTE STATUS 1");
        getRouteServer route = new getRouteServer();
        route.getRouteData();
        MapsActivity.isOnDuty = route.getDuty();
        MapsActivity.driverName = route.getDriverName();
        //MapsActivity.driverPic = route.getDriverPic();

        //System.out.println("Is on duty?" + MapsActivity.isOnDuty);
        if(MapsActivity.isOnDuty == true) {
            //System.out.println("shazam");
            getShuttleMax shuttleMax = new getShuttleMax(route.getShuttle());
            shuttleMax.getShuttle();
            size = (shuttleMax.getShuttleMax()) - (route.getCap());

            //System.out.println("The size is " +size + " " + shuttleMax.getShuttleMax() +" / " + route.getCap());
            if(size <= 0)
                MapsActivity.isFull = true;
            else
                MapsActivity.isFull = false;

        }
        return "";
    }


}
