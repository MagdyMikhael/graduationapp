package com.example.migosoft.graduationproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class WelcomePage extends AppCompatActivity {
    ImageView app;
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        getSupportActionBar().hide();

        app=(ImageView)findViewById(R.id.imageView);

        SendPostReqAsyncTask send=new SendPostReqAsyncTask();
        send.execute("http://data.sparkfun.com/input/OwaK9WxmLYTKvQRAJa38?private_key=828m0NXZnvi5YDvy76KN&dir=00");

        Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(),MonitoringActivity.class);
                startActivity(intent);
                finish();

            }
        },1000);
    }
    private class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url =params[0];

            Log.d("url: ",url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                response.getEntity();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

            }

            return null;
        }
    }
}
