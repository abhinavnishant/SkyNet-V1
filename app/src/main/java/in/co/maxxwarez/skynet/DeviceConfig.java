package in.co.maxxwarez.skynet;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DeviceConfig extends AppCompatActivity {
    private static final String TAG = "SkyNet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_config);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String id = (String) getIntent().getExtras().get("deviceID");
        Log.i(TAG, "onCreate: " + id);
    }

}
