package com.codelogic.sockethook;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.codelogic.sockethook.network.monitor.MonitorSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            // Init Socket Hook
            // See the hook result in logcat.
            Socket.setSocketImplFactory(new MonitorSocketFactory(getApplicationContext()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mURL = new URL("http://www.google.com.hk/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
                    HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();
                    InputStream is = conn.getInputStream();

                    WebResourceResponse response = new WebResourceResponse("image/png", "UTF-8", is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        WebView webView = (WebView)findViewById(R.id.webview);
        webView.loadUrl("http://www.qq.com/");
    }
}
