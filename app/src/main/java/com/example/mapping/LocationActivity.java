package com.example.mapping;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationActivity extends AppCompatActivity {

    //https://developer.android.com/training/location/retrieve-current
    //https://stackoverflow.com/questions/6775257/android-location-providers-gps-or-network-provider#:~:text=There%20are%203%20location%20providers,to%20return%20a%20location%20fix.
    TextView lat_txt, lng_txt;
    ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        lat_txt = findViewById(R.id.lat_txt);
        lng_txt = findViewById(R.id.lng_txt);
        progressBar = findViewById(R.id.progressBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    public void getloc(View view) {
        progressBar.setVisibility(View.VISIBLE);
        permissionMethod();

    }

    private void permissionMethod() {
        if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            //getPersentLoc();
            getPersentLoc2();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //getPersentLoc();
                    getPersentLoc2();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getPersentLoc2() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    Location location = task.getResult();
                    if (location != null) {
                        lat_txt.setText("" + location.getLatitude());
                        lng_txt.setText("" + location.getLongitude());
                        Log.d("Lat1 ",""+location.getLatitude());
                        Log.d("Lng1 ",""+location.getLongitude());
                    } else {
                        LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create()
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setMaxWaitTime(100)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                Location location1=locationResult.getLastLocation();
                                lat_txt.setText(""+location1.getLatitude());
                                lng_txt.setText(""+location1.getLongitude());

                                Log.d("Lat2 ",""+location1.getLatitude());
                                Log.d("Lng2 ",""+location1.getLongitude());
                            }
                        };

                        if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());



                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
        else
        {
            Toast.makeText(this, "Enable Gps", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void getPersentLoc() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    progressBar.setVisibility(View.GONE);
                    if(location!=null)
                    {
                        Log.d("TAG", "onSuccess: "+location.getLatitude()+location.getLongitude());
                        lat_txt.setText(""+location.getLatitude());
                        lng_txt.setText(""+location.getLongitude());
                    }
                    else
                    {
                        lat_txt.setText("null");
                        lng_txt.setText("null");

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: "+e.getLocalizedMessage());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LocationActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
        {
            Toast.makeText(this, "Please on the Gps", Toast.LENGTH_SHORT).show();
        }
    }
}