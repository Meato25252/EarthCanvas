package com.example.earthcanvas;

// ... 省略 ...

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;

public class gps {
    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    private GoogleMap mMap;
    double latitude;
    double longitude;

    boolean bool;

    //gpsコンストラクタ
    gps(FusedLocationProviderClient fusedLocationClient,Context context,GoogleMap mMap) {

        this.fusedLocationClient = fusedLocationClient;
        this.context=context;
        this.mMap=mMap;
        bool=true;

        // 位置情報取得開始
        startUpdateLocation();
    }


    /**
     * 位置情報取得開始メソッド
     */
    void startUpdateLocation() {

        // 位置情報の取得方法を設定
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);       // 位置情報更新間隔の希望
        locationRequest.setFastestInterval(5000); // 位置情報更新間隔の最速値
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // この位置情報要求の優先度

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;これ消してみた
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new MyLocationCallback(), null);
    }

    /**
     * 位置情報受取コールバッククラス
     */
    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            // 現在値を取得
            Location location = locationResult.getLastLocation();

            latitude = location.getLatitude();
            longitude=location.getLongitude();

            System.out.println("現在地"+latitude);
            System.out.println("現在地"+longitude);

            MapsActivity.onCatch(latitude,longitude);

        }
    }
}