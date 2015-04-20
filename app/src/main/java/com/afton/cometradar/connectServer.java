package com.afton.cometradar;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.ArrayList;
import java.util.List;


public class connectServer extends AsyncTask<Void, Void, String>  {

    String jsonString = "";
    ArrayList<LatLng> poly = new ArrayList<LatLng>();
    double pickupLat;
    double pickupLong;
    boolean isPickup = false;
    String ip = "10.0.2.2";
    MapsActivity ma;

    public connectServer(double a, double b, boolean c, MapsActivity _ma){
        pickupLat = a;
        pickupLong = b;
        isPickup = c;
        ma = _ma;
    }

    protected String doInBackground(Void... arg0) {

        if(isPickup == true){
            sendPickup();
            isPickup = false;
            getRouteJSON();
        }
        return "";
    }

    public void sendPickup(){

        String url = "http://10.0.2.2:3000/pickup?route=" + ma.routeName + "&lat=" + pickupLat + "&lon=" + pickupLong;
        url = url.replace(" ", "%20");

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                con.setRequestProperty("Connection", "close");
            }
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());

            String[] loc = response.toString().split(",");
            System.out.println(loc[0]);
            String lat = loc[0];
            lat = lat.substring(0, lat.length() - 1);
            String lon = loc[1];

            ma.pickupLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected String getRouteJSON() {
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

            return jsonString;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private URL getURL(){
        try {

            ma.routeName = ma.spinner.getSelectedItem().toString();

            //System.out.println("DIS BOY" + data.getOriginLat());
            double originLat = ma.userLocation.latitude;
            double originLong = ma.userLocation.longitude;
            double destinationLat = ma.pickupLocation.latitude;
            double destinationLong = ma.pickupLocation.longitude;



            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                    + originLat + ","
                    + originLong + "&destination="
                    + destinationLat + ","
                    + destinationLong + "&mode=walking";

            url += "&sensor=false&key=AIzaSyB2T0ODhKgWpFWJEyBmDkaYqU0GNGm1HYE";

            //System.out.println("SWAG = " + url);

            return new URL(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPostExecute(String result){
        JSONObject jsonObject;
        JSONObject polyArray;
        JSONArray routesArray;
        try {
            //Grabbing the Polyline points String. This does pull the correct value.
            //Parsing is correct.
            jsonObject = new JSONObject(jsonString);
            routesArray = jsonObject.getJSONArray("routes");
            JSONObject route = routesArray.getJSONObject(0);
            polyArray = route.getJSONObject("overview_polyline");
            String polyPoints = polyArray.getString("points");

            //Passing the Polyline points from the JSON file I get from the Google Directions URL into a decoder.
            poly = (ArrayList)decodePoly(polyPoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ma.walkingRoute = ma.mMap.addPolyline(new PolylineOptions()
                .addAll(poly)
                .width(8)
                .color(Color.GREEN));
    }

    private List<LatLng> decodePoly(String encoded) {

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));

            poly.add(p);
        }

        return poly;
    }
}

