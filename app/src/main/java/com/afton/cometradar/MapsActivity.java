package com.afton.cometradar;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;



public class MapsActivity extends FragmentActivity {

    public GoogleMap mMap;  // Might be null if Google Play services APK is not available.
    Spinner spinner;        // The spinner selector (UI element) for choosing a route
    public static String routeName = "";       // The name of the selected route
    public static boolean isOnDuty = false;
    public static boolean isFull = false;
    LatLng cartLocation;    // Location of the cart
    LatLng userLocation;    // Location of user
    String eta = "";        // The ETA of the cart to the user
    List <String> routeNames;
    protected ArrayAdapter<CharSequence> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        new PopulateRoutesTask().execute();
        setUpMapIfNeeded();

        // Handler for pickup request
        final Button pickUpButton = (Button) findViewById(R.id.pickUpButton);
        pickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (pickUpButton.getText().equals("Pick me up")) {
                pickUpButton.setText("Cancel Pickup");

                //Send to database
                double pickupLat = userLocation.latitude;
                double pickupLong = userLocation.longitude;

                //System.out.print("LAT LONG TEST = " + pickupLat + pickupLong);
                new connectServer(pickupLat,pickupLong,true).execute();
            }
            else
                pickUpButton.setText("Pick me up");
            }
        });


    }

    class PopulateRoutesTask extends AsyncTask<String, String, String> {

        String jsonString = "";

        public PopulateRoutesTask() {
            super();
        }

        @Override
        protected String doInBackground(String... params) {
            String getRoutes = "http://104.197.3.201:3000/route-names";
            URL url = null;

            try {
                url = new URL(getRoutes);

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

        @Override
        protected void onPostExecute(String result) {

            JSONArray routesArray;
            ArrayList<String> routes = new ArrayList<String>();

            try {
                routesArray = new JSONArray(jsonString);
                for(int i = 0; i < routesArray.length(); i++){
                    routes.add(routesArray.getJSONObject(i).getString("route_name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, routes);

            spinner = (Spinner)findViewById(R.id.spinner);
            spinner.setAdapter(adapter);

            // Handler for change in selected route
            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    routeName = spinner.getSelectedItem().toString();
                    System.out.println("the route name should be " + routeName);

                    new GetRoute(MapsActivity.this).execute();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // May need this later...
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     **/
    private void setUpMapIfNeeded() {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        BitmapDescriptor mapOverlay = BitmapDescriptorFactory.fromResource(R.mipmap.mapoverlay); // campus map overlay

        // Adds a ground overlay
        LatLngBounds bounds = new LatLngBounds(new LatLng(32.976600, -96.761700), new LatLng(32.995650, -96.739400)); // get a bounds
        mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(mapOverlay)
                .positionFromBounds(bounds));


        // Initialize user location
        mMap.setMyLocationEnabled(true);
        Location location = mMap.getMyLocation();
        userLocation = new LatLng(32.985700, -96.752514); // default location is center of campus
        if (location != null) {
            userLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }


        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////// DATA TO BE SENT TO DB /////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////// TODO: store "userLocation" as the driver's location for the route "routeName" ///////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        // Moves camera to current location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));

        (new UpdateLocationsThread(MapsActivity.this)).start();

    }

    protected LatLng updateUserAndCartLocations(){

        BitmapDescriptor personIcon = BitmapDescriptorFactory.fromResource(R.mipmap.person); // user location image
        BitmapDescriptor greenCartIcon = BitmapDescriptorFactory.fromResource(R.mipmap.cart_green); // available cart image
        BitmapDescriptor redCartIcon = BitmapDescriptorFactory.fromResource(R.mipmap.cart_red); // full cart image
        BitmapDescriptor blackCartIcon = BitmapDescriptorFactory.fromResource(R.mipmap.cart); // off-duty cart image
        BitmapDescriptor cartImage;
        String cartStatus;

        /////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////// DATA TO BE GRABBED FROM DB ////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////
        // TODO: The String variable "routeName" has the name of the route... use this to grab from DB //
        /////////////////////////////////////////////////////////////////////////////////////////////////

        //This method will take care of is shuttle is on duty, or if its full:
        //Not sure if it updates when we change routes??
         new getRouteStatus().execute();

         // is the cart on duty?

        //Get the current capacity, and if grater than or equal to max then we are full
          // is the cart full?

        /////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////


        if (isOnDuty) {
            cartStatus = "On-Duty: " + eta;
            cartImage = greenCartIcon;
            if (isFull) {
                cartStatus = "FULL";
                cartImage = redCartIcon;
            }
        } else {
            cartStatus = "Off-Duty";
            cartImage = blackCartIcon;
        }

        // update user location
        Location location = mMap.getMyLocation();
        userLocation = new LatLng(32.985700, -96.752514); // default location is center of campus

        if (location != null) {
            userLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }


        //////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////// DATA TO BE SENT TO DB /////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////// TODO: store "userLocation" as the rider's location for the route "routeName" ////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////

        // updates current user location marker
        mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("Pickup Location")
                .icon(personIcon));

        // updates cart marker
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(cartLocation.latitude, cartLocation.longitude))
                .title(cartStatus)
                .icon(cartImage));

        System.out.println("CART LOCATION: " + cartLocation.latitude + ", " + cartLocation.longitude);
        return userLocation;
    }

    public String getRouteName() {
        System.out.println("the route name should be " + routeName);
        return routeName;
    }
}




