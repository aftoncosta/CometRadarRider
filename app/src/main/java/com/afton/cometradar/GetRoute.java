package com.afton.cometradar;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetRoute extends AsyncTask<Void, Void, String> {

    String jsonString = "";
    ArrayList<LatLng> poly = new ArrayList<LatLng>();
    MapsActivity ma;

    public GetRoute(MapsActivity mAct) {
        super();
        ma = mAct;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        URL url = null;
        System.out.println("SPINNER SHIT");

        getSpinnerName test = new getSpinnerName();
        test.getRoutes();

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

            ////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////// DATA TO BE GRABBED FROM DB ///////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////
            // TODO: The String variable "ma.routeName" has the name of the route... use this to grab from DB //
            ////////////////////////////////////////////////////////////////////////////////////////////////////

            GetRouteSQL data = new GetRouteSQL();
            data.getRouteData();

            System.out.println("DIS BOY" + data.getOriginLat());
            double originLat = Double.parseDouble(data.getOriginLat());
            double originLong = Double.parseDouble(data.getOriginLong());
            double destinationLat = Double.parseDouble(data.getDestLat());
            double destinationLong = Double.parseDouble(data.getDestLong());
            double[] waypointsLat = new double[data.getSize()];
            double[] waypointsLong = new double[data.getSize()];

            String[] temp = new String[data.getSize()];
                    temp = data.getWpLat();

            String[] temp1 = new String[data.getSize()];
                    temp1 = data.getWpLong();


            System.out.println("DATA SIZE" + data.getSize());
            for(int i = 0 ; i < data.getSize() ; i++) {
                waypointsLat[i] = Double.parseDouble(temp[i]);
                waypointsLong[i] = Double.parseDouble(temp1[i]);

                System.out.println("WAYPOINTS LAT: " + waypointsLat[i]);
                System.out.println("WAYPOINTS LON: " + waypointsLong[i]);

            }

            System.out.println("ALl data assigned");

            ////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////

            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                    + originLat + ","
                    + originLong + "&destination="
                    + destinationLat + ","
                    + destinationLong + "&waypoints=";


            for (int i = 0; i < waypointsLat.length; i++)
                url += waypointsLat[i] + "," + waypointsLong[i] + "|";

            url += "&sensor=false&key=AIzaSyB2T0ODhKgWpFWJEyBmDkaYqU0GNGm1HYE";

            System.out.println("SWAG = " + url);

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

        ma.mMap.clear();
        BitmapDescriptor mapOverlay = BitmapDescriptorFactory.fromResource(R.mipmap.mapoverlay); // campus map overlay

        // Adds a ground overlay
        LatLngBounds bounds = new LatLngBounds(new LatLng(32.976600, -96.761700), new LatLng(32.995650, -96.739400)); // get a bounds
        ma.mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(mapOverlay)
                .positionFromBounds(bounds));

        ma.mMap.addPolyline(new PolylineOptions()
                .addAll(poly)
                .width(8)
                .color(Color.BLUE));


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