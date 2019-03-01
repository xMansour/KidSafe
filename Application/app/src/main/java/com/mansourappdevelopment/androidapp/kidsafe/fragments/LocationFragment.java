package com.mansourappdevelopment.androidapp.kidsafe.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mansourappdevelopment.androidapp.kidsafe.R;
import com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity;
import com.mansourappdevelopment.androidapp.kidsafe.utils.Location;
import com.mansourappdevelopment.androidapp.kidsafe.utils.User;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_EMAIL_EXTRA;
import static com.mansourappdevelopment.androidapp.kidsafe.activities.ParentSignedInActivity.CHILD_NAME_EXTRA;

public class LocationFragment extends Fragment {
    public static final int requestCode = 10;
    public static final String TAG = "LocationFragment";
    private DatabaseReference databaseReference;
    private MapView mapView;
    private IMapController mapController;
    private String childEmail;
    private String childName;
    private Context context;
    private Activity activity;
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

            //mapController.setCenter(locationNewOverlay.getMyLocation());

            //TODO:: attach a listener to act when the location isn't null

            //mapController.animateTo(new GeoPoint(30.3198639, 31.3095743));


            getData();
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
                    Location childLocation = child.getLocation();
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

    private void addMarkerForParent(Location location){

    }

    private void addOverlays() {

/*
        MyLocationNewOverlay locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        locationNewOverlay.enableMyLocation();
*/

        CompassOverlay compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), mapView);
        compassOverlay.enableCompass();

        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(context, mapView);
        rotationGestureOverlay.setEnabled(true);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(displayMetrics.widthPixels / 2, 10);

        MinimapOverlay minimapOverlay = new MinimapOverlay(context, mapView.getTileRequestCompleteHandler());
        minimapOverlay.setWidth(displayMetrics.widthPixels / 5);
        minimapOverlay.setHeight(displayMetrics.heightPixels / 5);

        //mapView.getOverlays().add(locationNewOverlay);
        mapView.getOverlays().add(compassOverlay);
        mapView.getOverlays().add(rotationGestureOverlay);
        mapView.getOverlays().add(scaleBarOverlay);
        mapView.getOverlays().add(minimapOverlay);
    }
}
