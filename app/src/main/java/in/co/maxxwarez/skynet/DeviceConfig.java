package in.co.maxxwarez.skynet;

import android.content.DialogInterface;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeviceConfig extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SkyNet";
    private FirebaseAuth mAuth;
    public String homeID;
    final List<Object> ipList = new ArrayList<>();
    private String deviceID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_config);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        deviceID = (String) getIntent().getExtras().get("deviceID");
        homeID = getIntent().getExtras().getString("homeID");


    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.addInput) {
            final String[] list = {"Your Devices", "External Sources", "Fixed Parameters"};
            alertBuilder(list, "Select Source");

        } else if (i == R.id.addValue) {
            Log.i(TAG, "Config: Add Value");
        } else if (i == R.id.addOutput) {
            Log.i(TAG, "Config: Add Output");
        } else if (i == R.id.addState) {
            Log.i(TAG, "Config: Add State");

        }
    }

    public void prepareBuilder(String source, final int sequence) {
        getChioceList(new MyCallback() {

            @Override
            public void onCallback(String[] value) {
                Log.i(TAG, "Config: Value " + value + " " + sequence);

                alertBuilder(value,"");
            }
        }, source, sequence);
    }

    private void alertBuilder(final String[] list, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prepareBuilder(list[which], which);
            }
        });
        builder.show();
    }

    public interface MyCallback{
        void onCallback(String[] value);
    }


    public void getChioceList(final MyCallback myCallback, final String source, int sequence) {
        final ArrayList<String> result = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Device").orderByChild("home").equalTo(homeID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                    result.add(dataSnapshots.getKey());
                    //str[0] = (String[]) result.toArray();
                    //ipList.add(dataSnapshots.getKey());


                }
                String frnames[]=result.toArray(new String[result.size()]);
                myCallback.onCallback(frnames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
