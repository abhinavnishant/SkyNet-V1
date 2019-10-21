package in.co.maxxwarez.skynet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;


public class DeviceSetup extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SkyNet";
    protected TextView mSSID;
    protected WebView webView;
    protected View mView;
    protected Button btn_refresh;
    protected Button btn_connect;
    private Object HttpURLConnection;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_setup);
        Intent intent = getIntent();
        //mSSID.setText(R.string.no_device_found);
        webView = findViewById(R.id.autoconfig);
        mSSID = findViewById(R.id.ssid);
        btn_refresh = findViewById(R.id.refresh);
        btn_connect = findViewById(R.id.connect);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        //mSSID.setText(getCurrentSSID());
        btn_refresh.setVisibility(View.GONE);
        btn_connect.setVisibility(View.GONE);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "My Logger On Destroy .....");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "My Logger On Pause .....");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "My Logger On Restart .....");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "My Logger On Resume .....");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "My Logger On Stop .....");
    }

    @Override
    protected void onStart() {
        super.onStart();
        String ssID = "";
        if (getConnState() == true) {
            ssID = getCurrentSSID();

            if (ssID.contains("SkyNet-AutoConfig")) {
                loadPage();
                showButton();
            } else {
                connectToWifi();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loadPage();
                showButton();
            }
        } else {
            connectToWifi();
            getCurrentSSID();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadPage();
            showButton();
        }
    }


    private void loadPage() {

        webView.loadUrl("http://192.168.4.1/");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                view.loadUrl(request);
                return false;
            }
        });

    }


    public void connectToWifi() {
        Log.i(TAG, "connectToWifi: ");

        try {
            WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            WifiConfiguration wc = new WifiConfiguration();
            wc.SSID = "\"SkyNet-AutoConfig\"";
            //wc.preSharedKey = "\"PASSWORD\"";
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            int netId = wifiManager.addNetwork(wc);
            wifiManager.setWifiEnabled(true);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();

            boolean connectionState = getConnState();
            if (connectionState == true) {
                String ssID = getCurrentSSID();
                if (ssID.contains("SkyNet-AutoConfig")) {
                    loadPage();
                    showButton();
                } else {
                    showButton();
                }
            } else {
                showButton();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showButton() {
        String connID = getCurrentSSID();
        if (connID.contains("SkyNet-AutoConfig")) {
            btn_refresh.setVisibility(View.VISIBLE);
            btn_connect.setVisibility(View.GONE);
        } else {
            btn_refresh.setVisibility(View.GONE);
            btn_connect.setVisibility(View.VISIBLE);
        }

    }


    public boolean getConnState() {
        boolean state = false;

        WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo.isConnected()) {
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                state = true;
            }
        }

        return state;
    }


    public String getCurrentSSID() {
        String ssid = "Not Connected";
        WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
                mSSID.setText(ssid);
            }
        }
        mSSID.setText(ssid);
        return ssid;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.refresh) {
            Log.d(TAG, "My Logger onClick: Refresh");
            //loadPage();
            registerDevice("13304107");
        }
        if (i == R.id.connect) {
            //connectToWifi();
            Log.d(TAG, "My Logger onClick: Connect");
            registerDevice("13304107");

        }

    }


    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast() {
            //new GetData().execute();
            getData();

            Log.d(TAG, "My Logger WebAppInterface: ");
        }
    }

    public void getData(){
        java.net.HttpURLConnection urlConnection = null;
        String result = "";
        try {
            URL url = new URL("http://192.168.4.1/c");
            urlConnection = (HttpURLConnection) url.openConnection();

            int code = urlConnection.getResponseCode();
            Log.d(TAG, "My Logger ChipID 1 " + code);

            if (code == 200) {
                Log.d(TAG, "My Logger ChipID 122 " + code);
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                if (in != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null)
                        result += line;
                }
                in.close();
                //registerDevice(result);
            }
            Log.d(TAG, "My Logger ChipID 1 " + result);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
            Log.d(TAG, "My Logger ChipID 2 " + result);
            registerDevice("13304107");
        }
        //registerDevice(result);


    }

    class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            java.net.HttpURLConnection urlConnection = null;
            String result = "";
            try {
                URL url = new URL("http://192.168.4.1/c");
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();

                Log.d(TAG, "My Logger ChipID 1 " + code);
                if (code == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null)
                            result += line;
                    }
                    in.close();
                    registerDevice(result);
                }
                Log.d(TAG, "My Logger ChipID 1 " + result);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
                Log.d(TAG, "My Logger ChipID 2 " + result);
            }
            //registerDevice(result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != "") {
                Log.d(TAG, "My Logger Inside Post Execure" + result);
                registerDevice(result);
            }


            Log.d(TAG, "My Logger Inside Post Execure" + result);

        }


    }

    private void registerDevice(String chipID) {
        Log.d(TAG, "My Logger registerDevice is" + chipID);

        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "My Logger: " + mAuth);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "My Logger2: " + currentUser);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //Query query = ref.child("users").child(currentUser.getUid());
        ref.child("users").child(currentUser.getUid()).child("deviceID").child(chipID).child("ID").setValue(chipID);
      //  DatabaseReference pushUserDeviceMap = ref.child("userDeviceMap");

        ref.child("devicUsermap").child(chipID).setValue(currentUser.getUid());

         Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }


}
