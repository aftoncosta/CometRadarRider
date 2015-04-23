package com.afton.cometradar;


import android.os.AsyncTask;

public class getRouteStatus extends AsyncTask<Void, Void, String> {

    int size;

    protected String doInBackground(Void... arg0) {
;
        getRouteServer route = new getRouteServer();
        route.getRouteData();
        MapsActivity.isOnDuty = route.getDuty();
        MapsActivity.driverName = route.getDriverName();
        if(MapsActivity.isOnDuty == true) {
            getShuttleMax shuttleMax = new getShuttleMax(route.getShuttle());
            shuttleMax.getShuttle();
            size = (shuttleMax.getShuttleMax()) - (route.getCap());

            if(size <= 0)
                MapsActivity.isFull = true;
            else
                MapsActivity.isFull = false;

        }
        return "";
    }


}
