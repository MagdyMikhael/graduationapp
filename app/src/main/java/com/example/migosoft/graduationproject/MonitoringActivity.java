package com.example.migosoft.graduationproject;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MonitoringActivity extends AppCompatActivity  {

    TextView tvBPM ,tvTemp ,tvSpeed,tvMyLoc;
    Button btnToLocation;
    String BPM;
    String latitude;
    String longitude;
    String speed;
    String Temperature;
    String myAddress;
    String tarAddress;
    GPSTracker tracker;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        getSupportActionBar().hide();

        tvBPM=(TextView)findViewById(R.id.tv_BPM);
        tvTemp=(TextView)findViewById(R.id.tv_Temp);
        tvSpeed=(TextView)findViewById(R.id.tv_Speed);
        tvMyLoc=(TextView)findViewById(R.id.tv_myLoc) ;
        tracker=new GPSTracker(getApplicationContext());

        ServerExcute();

        btnToLocation=(Button)findViewById(R.id.btn_Location);
        btnToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((latitude==null) ||(longitude==null)||(latitude.equals(""))||(longitude.equals(""))){        //||(myAddress==null)
                    Toast.makeText(getApplicationContext(),"Error in getting Location",Toast.LENGTH_SHORT).show();
                }else{
                    Intent myintent = new Intent(MonitoringActivity.this,LocationActivity.class);

                    Bundle MyBox=new Bundle();
                    MyBox.putDouble("latitude",Double.parseDouble(latitude));
                    MyBox.putDouble("longitude",Double.parseDouble(longitude));
                    MyBox.putString("MyAdd",myAddress);
                    MyBox.putString("TarAdd",tarAddress);
                    myintent.putExtras(MyBox);

                    startActivity(myintent);
                    finish();

                }

            }
        });

        Addresses();
    }
    public void ServerExcute(){
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("object").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext())
                {

                    BPM = (String)((DataSnapshot)i.next()).getValue();
                    latitude = (String) ((DataSnapshot)i.next()).getValue();
                    longitude = (String) ((DataSnapshot)i.next()).getValue();
                    speed = (String)((DataSnapshot)i.next()).getValue();
                    Temperature = (String)((DataSnapshot)i.next()).getValue();


                }
                tvBPM.setText(BPM);
                tvTemp.setText(Temperature);
                tvSpeed.setText(speed);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void Addresses(){
        myAddress=getAddress(getApplicationContext(),tracker.getLatitude(),tracker.getLongitude());
        tvMyLoc.setText("MyAdd: "+myAddress);
        try {
            double lat=Double.parseDouble(latitude);
            double lng=Double.parseDouble(longitude);
            tarAddress=getAddress(getApplicationContext(),lat,lng);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    void finishActivity(){
        MonitoringActivity.this.finish();;
    }



}


