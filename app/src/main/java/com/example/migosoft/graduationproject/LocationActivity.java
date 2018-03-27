package com.example.migosoft.graduationproject;

import android.*;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.*;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;


import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.migosoft.graduationproject.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    ImageView forward, backward, right, left, stop;
    Button back, here, targetLoc;
    TextView tvTarLoc;

    double LongTarget;
    double LatTarget;

    double latHere;
    double longHere;

    String myAddressLoc;
    String tarAddress;
    String Target;

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;

    GPSTracker gpsTracker;

    private static LatLng me;
    private static LatLng target;

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().hide();


        forward = (ImageView) findViewById(R.id.iv_forward);
        backward = (ImageView) findViewById(R.id.iv_backward);
        right = (ImageView) findViewById(R.id.iv_right);
        left = (ImageView) findViewById(R.id.iv_left);
        stop = (ImageView) findViewById(R.id.iv_stop);
        tvTarLoc=(TextView)findViewById(R.id.tv_targetLoc);

        /////////////////////////////////////////////////////////////
        Intent myintent = getIntent();
        Bundle b = myintent.getExtras();

        LongTarget = b.getDouble("longitude");
        LatTarget = b.getDouble("latitude");

        myAddressLoc=b.getString("MyAdd");
        tarAddress=b.getString("TarAdd");

        gpsTracker = new GPSTracker(getApplicationContext());

        if (gpsTracker.canGetLocation()) {
            latHere = gpsTracker.getLatitude();
            longHere = gpsTracker.getLongitude();


            //geo:0,0?q=latitude,longitude(name)

        } else {
            gpsTracker.showSettingsAlert();

        }

        Target=getAddress(getApplicationContext(),LatTarget,LongTarget);
        me = new LatLng(latHere,longHere);
        target = new LatLng(LatTarget, LongTarget);

        tvTarLoc.setText("TarAdd: "+Target);

        //////////////////////////////////////////////////////////


        /////////////////////////////////////////////////////////
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Perfect", Toast.LENGTH_SHORT).show();
            initMap();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        //////////Buttons OnClickListeners//////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendPostReqAsyncTask send = new SendPostReqAsyncTask();
                send.execute("http://data.sparkfun.com/input/OwaK9WxmLYTKvQRAJa38?private_key=828m0NXZnvi5YDvy76KN&dir=13");

                Toast.makeText(getApplicationContext(), "Forward", Toast.LENGTH_SHORT).show();

            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPostReqAsyncTask send = new SendPostReqAsyncTask();
                send.execute("http://data.sparkfun.com/input/OwaK9WxmLYTKvQRAJa38?private_key=828m0NXZnvi5YDvy76KN&dir=14");

                Toast.makeText(getApplicationContext(), "Backword", Toast.LENGTH_SHORT).show();

            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPostReqAsyncTask send = new SendPostReqAsyncTask();
                send.execute("http://data.sparkfun.com/input/OwaK9WxmLYTKvQRAJa38?private_key=828m0NXZnvi5YDvy76KN&dir=11");

                Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT).show();

            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPostReqAsyncTask send = new SendPostReqAsyncTask();
                send.execute("http://data.sparkfun.com/input/OwaK9WxmLYTKvQRAJa38?private_key=828m0NXZnvi5YDvy76KN&dir=12");

                Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT).show();

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPostReqAsyncTask send = new SendPostReqAsyncTask();
                send.execute("http://data.sparkfun.com/input/OwaK9WxmLYTKvQRAJa38?private_key=828m0NXZnvi5YDvy76KN&dir=10");

                Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();

            }
        });


        back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPostReqAsyncTask send = new SendPostReqAsyncTask();
                send.execute("http://data.sparkfun.com/input/OwaK9WxmLYTKvQRAJa38?private_key=828m0NXZnvi5YDvy76KN&dir=00");
                Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();

                Intent backIntent = new Intent(LocationActivity.this, MonitoringActivity.class);
                startActivity(backIntent);
                LocationActivity.this.finish();

            }
        });

        here = (Button) findViewById(R.id.btn_here);
        targetLoc = (Button) findViewById(R.id.btn_target);


        here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLocation(latHere, longHere, 15);
            }
        });

        targetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLocation(LatTarget, LongTarget, 15);
            }
        });



    }



    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public boolean googleServicesAvailable() {

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to play services", Toast.LENGTH_SHORT);
        }
        return false;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        goToLocation(LatTarget, LongTarget, 15);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        /*mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();*/
        addMarkerMe(latHere,longHere);
        addMarkerTarget(LatTarget, LongTarget);


        PolylineOptions polylineOptions=new PolylineOptions().add(me).add(target).width(5).color(Color.BLUE)
                .geodesic(true);
        mGoogleMap.addPolyline(polylineOptions);
            //addMarker(mobLat,mobLong);


    }




    private void goToLocation(double lat, double lng, float Zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, Zoom);
        mGoogleMap.moveCamera(update);
    } private void addMarkerMe(double latitude, double longitude) {
        MarkerOptions options = new MarkerOptions()
                .title(myAddressLoc)
                .position(new LatLng(latitude, longitude))
                .snippet(myAddressLoc);
        mGoogleMap.addMarker(options);
    }
    private void addMarkerTarget(double latitude, double longitude) {
        MarkerOptions options = new MarkerOptions()
                .title(Target)
                .position(new LatLng(latitude, longitude))
                .snippet(Target);
        mGoogleMap.addMarker(options);
    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(this, "Can't Get Current location", Toast.LENGTH_SHORT).show();
        } else {


            //goToLocation(location.getLatitude(),location.getLongitude(),15);
            addMarkerTarget(location.getLatitude(), location.getLongitude());
        }
    }

    // get address from latitude and longitude

    //asynctask
    private class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];

            Log.d("url: ", url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                response.getEntity();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }

            return null;
        }
    }

    public String getAddress(Context ctx, double lat, double lng) {
        String fullAdd = null;
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                fullAdd = address.getAddressLine(0);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fullAdd;
    }

}
