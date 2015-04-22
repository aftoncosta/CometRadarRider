package com.afton.cometradar;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.util.List;



public class MapsActivity extends FragmentActivity {

    public GoogleMap mMap;  // Might be null if Google Play services APK is not available.
    Spinner spinner;        // The spinner selector (UI element) for choosing a route
    public static String routeName = "";       // The name of the selected route
    public static boolean isOnDuty = false;
    public static boolean isFull = false;
    public static String driverName = "";
    public static String driverURL = "";
    public static Drawable driverPic = null;
    LatLng cartLocation;    // Location of the cart
    LatLng userLocation;    // Location of user
    public LatLng pickupLocation;
    String eta = "";        // The ETA of the cart to the user
    List <String> routeNames;
    protected ArrayAdapter<CharSequence> adapter;
    Marker pickupMarker;
    Polyline walkingRoute;
    PopupWindow pickupPopUp;
    boolean click = true;
    LinearLayout mainLayout;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        spinner = (Spinner)findViewById(R.id.spinner);


        new GetRouteNames(MapsActivity.this).execute();
        setUpMapIfNeeded();


        // Handler for pickup request
        final Button pickUpButton = (Button) findViewById(R.id.pickUpButton);
        pickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pickUpButton.getText().equals("Pick me up")) {

                    pickUpButton.setText("Cancel Pickup");
                    double pickupLat = userLocation.latitude;
                    double pickupLong = userLocation.longitude;

                    new connectServer(pickupLat,pickupLong,true, MapsActivity.this).execute();

                    ImageView imageView = new ImageView(MapsActivity.this);

                    imageView.setMinimumHeight(700);
                    imageView.setMaxWidth(700);
                    imageView.setImageDrawable(driverPic);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER );

                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setMessage("Your driver, " + driverName + ",\n is on the way!\n");
                    builder.setCancelable(false);
                    builder.setView(imageView);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    pickupLocation = null;
                    pickupMarker.setVisible(false);
                    walkingRoute.remove();
                    pickUpButton.setText("Pick me up");
                }
            }
        });

        // Handler for change in selected route
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                routeName = spinner.getSelectedItem().toString();
                pickupLocation = null;
                new GetRoute(MapsActivity.this).execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // May need this later...
            }
        });
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
        userLocation = new LatLng(32.986200, -96.752814); // default location is center of campus
        if (location != null) {
            userLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }


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

        //This method will take care of is shuttle is on duty, or if its full:
        //Not sure if it updates when we change routes??
         new getRouteStatus().execute();



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
        userLocation = new LatLng(32.986200, -96.752814); // default location is center of campus

        if (location != null) {
            userLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }

        // updates current pickup location marker
        if (pickupLocation != null) {

            pickupMarker = mMap.addMarker(new MarkerOptions()
                    .position(pickupLocation)
                    .title("Your pickup Location")
                    .icon(personIcon));

            pickupMarker.setVisible(true);
        } else {
            pickupMarker = null;
        }
        // updates cart marker
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(cartLocation.latitude, cartLocation.longitude))
                .title(cartStatus)
                .icon(cartImage));

        return userLocation;
    }

    public String getRouteName() {
        return routeName;
    }
}




