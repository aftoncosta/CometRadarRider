package com.afton.cometradar;

import android.os.AsyncTask;
import android.os.Build;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SetPickupSQL extends AsyncTask<Void, Void, String>  {
        double lat = 0;
        double lon = 0;
        String ip= "104.197.3.201";

    public SetPickupSQL(double a, double b) {
        lat = a;
        lon = b;
    }


    /*
        Background task that runs to connect to database and get route data
     */
    protected String doInBackground(Void... arg0) {

        String temp = "INSERT INTO pickup_request (pickup_request.route_name, pickup_request.lat, pickup_request.long) VALUES ('" + MapsActivity.routeName + "', '" + lat + "', '" + lon + "');";
        String query = temp.replace(" ", "%20");

        String url = "http://" + ip + ":3000/pickup?route=" + MapsActivity.routeName + "&lat=" + lat + "&long=" + lon;

        try {
            URL obj = new URL(url);
            System.out.println("url swag: " + obj.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                System.out.println("CLOSE");
                con.setRequestProperty("Connection", "close");
            }
            con.setRequestMethod("GET");

            System.out.println("\nSending 'GET' request to URL : " + url);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            //print result
            System.out.println("RESPONSE FROM SERVER: " + response.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
}


