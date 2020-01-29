package com.example.backgroundlocationservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.jaredrummler.android.device.DeviceName;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {
    static MainActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    public  static MainActivity getInstance(){
        return  instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        //txt_location = findViewById(R.id.txtLocation);



        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        updateLocation();
                        FragmentManager fm = getSupportFragmentManager();
                        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
                        if (fragment == null) {
                            fragment = new SupportMapFragment();
                            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
                        }
                        fragment.getMapAsync(MainActivity.this);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private void updateLocation() {

        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,getPendingIntent());
    }

    private PendingIntent getPendingIntent(){
        Intent intent = new Intent(this,MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(10);
        locationRequest.setSmallestDisplacement(1f);


    }

    public void updateTextView(final Location location){
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // txt_location.setText(value);
                double lat=location.getLatitude();
                double lng=location.getLongitude();
                LatLng latLng = new LatLng(lat,lng);

                moveCamera(latLng);
                SharedPreferences preferences = getSharedPreferences("my_pref",MODE_PRIVATE);
                String deviceName = preferences.getString("name","null");
                String mobile = preferences.getString("mobile","null");
                String pass = preferences.getString("pass","null");
                addLocation(String.valueOf(lat),String.valueOf(lng),deviceName,mobile,pass);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }

    void moveCamera(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17)                   // Sets the zoom
                //.bearing(90)                // Sets the orientation of the camera to east
                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    void addLocation(String lat,String lng,String id,String mobile,String pass){
        Api service = NetworkClient.getRetrofitClient().create(Api.class);
        Call<String> call;
        call = service.addLocation(lat,lng,id,mobile,pass);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("respone",String.valueOf(response.body()));

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("error",t.toString());

            }
        });

    }
}
