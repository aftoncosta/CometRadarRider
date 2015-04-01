package com.afton.cometradar;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class GetETA extends AsyncTask<Void, Void, Void>{
    String jsonString = "";
    MapsActivity ma;

    public GetETA(MapsActivity mAct) {
        super();
        ma = mAct;
    }

    @Override
    protected Void doInBackground(Void... params) {
        URL url = null;

        try {
            url = getURL();

            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int bytesRead = 0;
            while((bytesRead = bis.read(buffer)) > 0) {
                String text = new String(buffer, 0, bytesRead);
                sb.append(text);
            }
            bis.close();
            jsonString = sb.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private URL getURL(){
        try {

            double originLat = ma.userLocation.latitude;
            double originLong = ma.userLocation.longitude;
            ma.routeName = ma.spinner.getSelectedItem().toString();

            //////////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////// DATA TO BE GRABBED FROM DB /////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////
            // TODO: The String variable "ma.routeName" has the name of the route. Use this to grab from DB //
            //////////////////////////////////////////////////////////////////////////////////////////////////

            ma.cartLocation = new LatLng(32.9855582, -96.7499986);

            //////////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////

            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                    + originLat + ","
                    + originLong + "&destination="
                    + ma.cartLocation.latitude + ","
                    + ma.cartLocation.longitude + "&sensor=false&key=AIzaSyB2T0ODhKgWpFWJEyBmDkaYqU0GNGm1HYE";

            return new URL(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused){
        JSONObject jsonObject;
        JSONObject durationObject;
        JSONArray routesArray;
        try {
            jsonObject = new JSONObject(jsonString);
            routesArray = jsonObject.getJSONArray("routes");
            JSONObject route = routesArray.getJSONObject(0);
            routesArray = route.getJSONArray("legs");
            durationObject = routesArray.getJSONObject(0);

            String duration = durationObject.getJSONObject("duration").getString("text");

            ma.eta = duration;
            ma.updateUserAndCartLocations();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}