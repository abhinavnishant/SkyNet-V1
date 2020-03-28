package in.co.maxxwarez.skynet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DeviceConfig extends AppCompatActivity implements View.OnClickListener{
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
    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.addInput) {
            Log.i(TAG, "Config: Add Input");
            String[] colors = {"red", "green", "blue", "black"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pick a color");
            builder.setItems(colors, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                }
            });
            builder.show();
        } else if (i == R.id.addValue) {
            Log.i(TAG, "Config: Add Value");
        } else if (i == R.id.addOutput) {
            Log.i(TAG, "Config: Add Output");
        } else if (i == R.id.addState) {
            Log.i(TAG, "Config: Add State");

        }

    }

}
