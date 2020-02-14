package in.co.maxxwarez.skynet;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DeviceConfig extends AppCompatActivity {
    private static final String TAG = "SkyNet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_config);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String deviceID = (String) getIntent().getExtras().get("deviceID");
        final String homeID = getIntent().getExtras().getString("homeID");
        Log.i(TAG, "onCreate: " + deviceID + " " + homeID);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final Query query = ref.child("Device");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot device : dataSnapshot.getChildren()){
                   // if(device.child("home").equals(homeID)){
                        Log.i(TAG, "onDataChange: " + device.getKey() + device.child("home").getValue());
                  //  }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
