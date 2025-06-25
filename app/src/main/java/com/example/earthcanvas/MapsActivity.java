/**
 * Quick, Draw! Dataset by Google, licensed under CC BY 4.0.
 * https://creativecommons.org/licenses/by/4.0/
 *
 * © Google LLC, licensed under CC BY 4.0.
 */

/**
 * 使用するデータセット: Quick, Draw! Dataset
 * 提供元: Google, Inc.
 * データセットURL: https://quickdraw.withgoogle.com/data
 * ライセンス: Creative Commons Attribution 4.0 International (CC BY 4.0)
 *
 * このプログラムは、Google, Inc. が提供する Quick, Draw! データセットを使用しています。
 */

package com.example.earthcanvas;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static GoogleMap mMap;
    public Context context;
    private gps gps;
    private FusedLocationProviderClient fusedLocationClient;
    private final String TAG = this.getClass().getSimpleName();
    private static final int PIXEL_WIDTH = 28;
    private AI abcClassifier;
    private static ToggleButton toggleButton;
    static boolean flg = true;
    static double nowLatitude;
    static double nowLongitude;
    static double nextLatitude;
    static double nextLongitude;
    static double nextNextLatitude;
    static double nextNextLongitude;
    static boolean start = true;
    Bitmap scaledBitmap;
    String predict;
    String getdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //初期値の設定のために必要
        start = true;

        //intentでデータを取得
        Intent intent = getIntent();
        getdata = intent.getStringExtra("SEND_DATA");
        System.out.println("★★★" + getdata);

        context = this;
        abcClassifier = new AI(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //権限の確認
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 権限がない場合、許可ダイアログ表示
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            System.out.println("permissions");

        } else {
            //
            gps = new gps(fusedLocationClient, context, mMap);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        toggleButton = (ToggleButton) findViewById(R.id.toggleGPS);
        mapFragment.getMapAsync((OnMapReadyCallback) context);

        //お題の表示
        TextView textView = findViewById(R.id.theme);
        textView.setText("お題は" + getdata + "です！");



        //AI判定ボタンを押したときの処理
        findViewById(R.id.buttonComp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClicked();
            }
        });

        //消すボタン
        findViewById(R.id.buttonClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
            }
        });

        //スタート画面に戻る
        findViewById(R.id.startReturn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //マップを戻す
        findViewById(R.id.map_re).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapDef();
            }
        });

        //白にする
        findViewById(R.id.white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(MAP_TYPE_NONE);
            }
        });
    }

    //権限を許可した場合gpsを起動する
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2000) {
            //許可された場合
            gps = new gps(fusedLocationClient, context, mMap);
        }
    }


    //google map処理
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("a");

        View view = (View) findViewById(R.id.map);
        mMap = googleMap;
        mMap.setMapType(MAP_TYPE_NORMAL);
    }


    /**
     * 絵を描く処理(もともとマーカーだったところ)
     */
    static void onCatch(double latitude, double longitude) {

        nowLatitude = Math.floor(latitude * 100000) / 100000;
        nowLongitude = Math.floor(longitude * 100000) / 100000;

        if (start == true) {
            LatLng loc = new LatLng(nowLatitude, nowLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
            start = false;

            nextLatitude = nowLatitude;
            nextNextLatitude = nowLatitude;
            nextLongitude = nowLongitude;
            nextNextLongitude = nowLongitude;
        }

        //トグル処理
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flg = true;
                } else {
                    flg = false;
                    // ToggleがOFFのときの処理(GPSのOFF)
                }
            }
        });

        if (flg) {

            // 現在値を取得
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(new LatLng(nextNextLatitude, nextNextLongitude))
                    .add(new LatLng(nextLatitude, nextLongitude))
                    .add(new LatLng(nowLatitude, nowLongitude))

                    .width(50);

            Polyline polyline = mMap.addPolyline(polylineOptions);

            nextNextLatitude = nextLatitude;
            nextNextLongitude = nextLongitude;
            nextLatitude = nowLatitude;
            nextLongitude = nowLongitude;


        } else {
            nextNextLatitude = nextLatitude;
            nextNextLongitude = nextLongitude;
            nextLatitude = nowLatitude;
            nextLongitude = nowLongitude;
        }
    }


    //previewを表示する処理
    private void onButtonClicked() {

        mMap.snapshot(bitmap -> {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);

            //AIで画像を判定
            resultAi(bitmap);

            System.out.println("■■■" + predict);
            System.out.println("■■■" + getdata);

            if (!predict.equals(getdata)) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder
                        .setTitle("お題の" + getdata + "じゃないよ！")
                        .setView(imageView)
                        .setNeutralButton("閉じる", (dialog,which)->{
                            mMap.setMapType(MAP_TYPE_NORMAL);
                        })
                        .create().show();
            } else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder
                        .setTitle("お題の" + getdata + "です！")
                        .setView(imageView)
                        .setNeutralButton("閉じる", (dialog,which)->{
                            mMap.setMapType(MAP_TYPE_NORMAL);
                        })
                        .create().show();
            }
        });
    }

    //AI判定
    void resultAi(Bitmap bitmap) {
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, PIXEL_WIDTH, PIXEL_WIDTH, false);

        predict = abcClassifier.doClassify(scaledBitmap);
    }

    //画像を元に戻す
    void mapDef() {
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_def));
            if (!success) {
                System.out.println("背景色が取得できて正誤ません");
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        onMapReady(mMap);
    }
}