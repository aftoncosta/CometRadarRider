package com.afton.cometradar;

import android.os.AsyncTask;
import android.os.Build;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GetETA extends AsyncTask<Void, Void, Void>{
    String jsonString = "";
    MapsActivity ma;
    String ip = "104.197.3.201";

    public GetETA(MapsActivity mAct) {
        super();
        ma = mAct;
    }

    @Override
    protected Void doInBackground(Void... params) {

        URL urlD = null;

        try {
            urlD = getURL();

            BufferedInputStream bis = new BufferedInputStream(urlD.openStream());
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
        catch (NullPointerException e1){
            e1.printStackTrace();
        }
        return null;
    }

    private URL getURL(){
        try {

            double originLat = ma.userLocation.latitude;
            double originLong = ma.userLocation.longitude;
            //System.out.println("ROUTE NAME IS " + ma.spinner.getSelectedItem());

            if(ma.spinner.getSelectedItem() != null) {
                System.out.println("Filalfel");
                ma.routeName = ma.spinner.getSelectedItem().toString();
            }
            //ma.routeName = "University Village Phase 5 and 6";

            //////////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////// DATA TO BE GRABBED FROM DB /////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////
            // TODO: The String variable "ma.routeName" has the name of the route. Use this to grab from DB //
            //////////////////////////////////////////////////////////////////////////////////////////////////

            ma.cartLocation = getCartLocation();//new LatLng(32.9855582, -96.7499986);

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
        catch (NullPointerException e1) {
            e1.printStackTrace();
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

    protected LatLng getCartLocation(){
        LatLng location = ma.cartLocation;

        String temp = "SELECT currentLat, currentLong FROM bsxpccom_cometradar.current_route WHERE route_name = '" + ma.routeName + "';";
        String query = temp.replace(" ", "%20");

        String url = "http://" + ip + ":3000/doQuery?string=" + query ;

        try {
            URL obj = new URL(url);
            System.out.println("url eta swag: " + obj.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                System.out.println("eta CLOSE");
                con.setRequestProperty("Connection", "close");
            }
            con.setRequestMethod("GET");

            //System.out.println("\nSending 'GET' request to URL : " + url);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            String answer = response.toString();
            JSONArray jsonA = new JSONArray(answer);
            System.out.println(jsonA.toString());
            JSONObject jsonO = jsonA.getJSONObject(0);
            System.out.println(jsonO.toString());

            System.out.println("eta RESPONSE FROM SERVER: " + jsonO.getString("currentLat"));
            //System.out.println("RESPONSE FROM SERVER: " + answer);
            //parseData(answer);
            location = new LatLng(Double.parseDouble(jsonO.getString("currentLat")), Double.parseDouble(jsonO.getString("currentLong")));

        }catch(Exception e){
            e.printStackTrace();
        }
        return location;
    }
}