package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.interfaces.OnGeoFenceSettingListener;
import com.mansourappdevelopment.androidapp.kidsafe.services.GeoFencingForegroundService;
import com.mansourappdevelopment.androidapp.kidsafe.models.Location;
import com.mansourappdevelopment.androidapp.kidsafe.models.User;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Constant;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_EMAIL_EXTRA;
import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_NAME_EXTRA;

public class LocationFragment extends Fragment implements OnGeoFenceSettingListener {
    public static final int requestCode = 10;
    public static final String TAG = "LocationFragmentTAG";
    public static final int REQUEST_CODE = 922;
    public static final int LOCATION_UPDATE_INTERVAL = 1;    //every second
    public static final int LOCATION_UPDATE_DISPLACEMENT = 1;  //every meter
    private DatabaseReference databaseReference;
    private MapView mapView;
    private FloatingActionButton fabGeoFence;
    private IMapController mapController;
    private String childEmail;
    private String childName;
    private Context context;
    private Activity activity;
    private MyLocationNewOverlay locationNewOverlay;
    private Location userLocation;
    //TODO:: add a childPic;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.mapView);
        fabGeoFence = (FloatingActionButton) view.findViewById(R.id.fabGeoFence);
        fabGeoFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                GeoFenceSettingFragment geoFenceSettingFragment = new GeoFenceSettingFragment();
                geoFenceSettingFragment.setTargetFragment(LocationFragment.this, REQUEST_CODE);
                geoFenceSettingFragment.setCancelable(false);
                geoFenceSettingFragment.show(fragmentManager, "GeoFence Dialog Fragment");
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        context = getContext();
        activity = getActivity();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
        } else {

            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setBuiltInZoomControls(false);
            mapView.setMultiTouchControls(true);

            mapController = mapView.getController();
            mapController.setZoom(18);

            //Log.i(TAG, "onViewCreated: " + locationNewOverlay.getMyLocation());
            //Log.i(TAG, "onViewCreated: " + locationNewOverlay.isMyLocationEnabled());
            //Log.i(TAG, "onViewCreated: " + locationNewOverlay.getMyLocationProvider().getLastKnownLocation());

            Log.i(TAG, "onViewCreated: executed");
            locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
            locationNewOverlay.enableMyLocation();
            Log.i(TAG, "onViewCreated: not null");
            mapController.setCenter(locationNewOverlay.getMyLocation());

            //TODO:: attach a listener to act when the location isn't null

            //mapController.animateTo(new GeoPoint(30.3198639, 31.3095743));

            getData();
            getUserLocation();
            getChildLocation();
        }
    }


    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Configuration.getInstance().save(context, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void getData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            childEmail = bundle.getString(CHILD_EMAIL_EXTRA);
            childName = bundle.getString(CHILD_NAME_EXTRA);
        }
    }

    private void getChildLocation() {
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                    User child = nodeShot.getValue(User.class);
                    Location childLocation = child.getLocation();//TODO:: handle if null
                    addMarkerForChild(childLocation);
                    Log.i(TAG, "onDataChange: location changed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void addMarkerForChild(Location location) {
        mapView.getOverlays().clear();

        GeoPoint childGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

        Marker childMarker = new Marker(mapView);
        childMarker.setPosition(childGeoPoint);
        childMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        childMarker.setTitle(childName);
        //childMarker.setTextIcon(childName);
        childMarker.setIcon(getResources().getDrawable(R.drawable.ic_location));      //TODO:: should add the child's picture instead.
        mapView.getOverlays().add(childMarker);
        mapController.setCenter(childGeoPoint);
        addOverlays();
    }

    private void addMarkerForParent(Location location) {
        /*GeoPoint parentGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

        Marker parentMarker = new Marker(mapView);
        parentMarker.setPosition(parentGeoPoint);
        parentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        parentMarker.setTitle("You");
        parentMarker.setIcon(getResources().getDrawable(R.drawable.ic_parent));      //TODO:: should add the child's picture instead.
        mapView.getOverlays().add(parentMarker);
        mapController.setCenter(parentGeoPoint);*/

    }

    private void addOverlays() {


        //CompassOverlay compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), mapView);
        //compassOverlay.enableCompass();

        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(context, mapView);
        rotationGestureOverlay.setEnabled(true);

        //DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        //ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        //scaleBarOverlay.setCentred(true);
        //scaleBarOverlay.setScaleBarOffset(displayMetrics.widthPixels / 2, 10);

        //MinimapOverlay minimapOverlay = new MinimapOverlay(context, mapView.getTileRequestCompleteHandler());
        //minimapOverlay.setWidth(displayMetrics.widthPixels / 5);
        //minimapOverlay.setHeight(displayMetrics.heightPixels / 5);

        //mapView.getOverlays().add(locationNewOverlay);
        //mapView.getOverlays().add(compassOverlay);
        mapView.getOverlays().add(rotationGestureOverlay);
        //mapView.getOverlays().add(scaleBarOverlay);
        //mapView.getOverlays().add(minimapOverlay);
    }

    @Override
    public void onGeoFenceSet(final String geoFenceCenter, final double geoFenceDiameter) {
        Toast.makeText(context, "Center: " + geoFenceCenter + " Diameter: " + geoFenceDiameter, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onGeoFenceSet: locationOverlay: " + locationNewOverlay.toString());
        Log.i(TAG, "onGeoFenceSet: location: " + locationNewOverlay.getMyLocation());
        Query query = databaseReference.child("childs").orderByChild("email").equalTo(childEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot nodeShot = dataSnapshot.getChildren().iterator().next();
                    User child = nodeShot.getValue(User.class);
                    Location childLocation = child.getLocation();
                    String key = nodeShot.getKey();

                    if (geoFenceCenter.equals("You")) {
                        double fenceCenterLatitude = userLocation.getLatitude();
                        double fenceCenterLongitude = userLocation.getLongitude();
                        double childLatitude = childLocation.getLatitude();
                        double childLongitude = childLocation.getLongitude();
                        Location location = new Location(childLatitude, childLongitude, geoFenceDiameter, fenceCenterLatitude, fenceCenterLongitude, false, true);
                        databaseReference.child("childs").child(key).child("location").setValue(location);


                    } else {
                        double childLatitude = childLocation.getLatitude();
                        double childLongitude = childLocation.getLongitude();
                        Location location = new Location(childLatitude, childLongitude, geoFenceDiameter, childLatitude, childLongitude, false, true);
                        databaseReference.child("childs").child(key).child("location").setValue(location);

                    }

                    //TODO:: stop the service if we re-started it
                    Intent serviceIntent = new Intent(getActivity(), GeoFencingForegroundService.class);
                    serviceIntent.putExtra(Constant.CHILD_EMAIL_EXTRA, childEmail);
                    serviceIntent.putExtra(Constant.CHILD_NAME_EXTRA, childName);

                    ContextCompat.startForegroundService(getContext(), serviceIntent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onCancelFence() {
        Toast.makeText(context, getString(R.string.canceled), Toast.LENGTH_SHORT).show();

    }

    private void getUserLocation() {
        //TODO:: NEED TO OPEN THE GPS AUTOMATICALLY
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                if (location != null) {
                    userLocation = new Location(location.getLatitude(), location.getLongitude());
                    Log.i(TAG, "onLocationChanged: location lat: " + location.getLatitude() + "location long: " + location.getLongitude());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //these two statements will be only executed when the permission is granted.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISPLACEMENT, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISPLACEMENT, locationListener);
            return;
        }

    }
}
