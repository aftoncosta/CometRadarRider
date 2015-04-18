package com.afton.cometradar;

import android.os.AsyncTask;
import android.os.Build;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class connectServer extends AsyncTask<Void, Void, String>  {

    double pickupLat;
    double pickupLong;
    boolean isPickup = false;

    public connectServer(double a, double b, boolean c){
        pickupLat = a;
        pickupLong = b;
        isPickup = c;
    }

    protected String doInBackground(Void... arg0) {

        if(isPickup == true){
            sendPickup();
            isPickup = false;
        }




        return "";
    }

    public void sendPickup(){

        String url = "http://10.0.2.2:3000/pickup?route=" + MapsActivity.routeName + "&lat=" + pickupLat + "&long=" + pickupLong;
        url = url.replace(" ", "%20");

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

            int ind1 = response.toString().indexOf("<div id=\"id\">");
            //print result
            System.out.println("RESPONSE FROM SERVER: " + response.toString().substring(ind1 + 13, response.toString().length()-6));

        }catch(Exception e){
            e.printStackTrace();
        }
    }


}

