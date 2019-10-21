package in.co.maxxwarez.skynet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DeviceDetail extends AppCompatActivity implements View.OnClickListener {
    public FirebaseAuth mAuth;
    private TextView mTextView;
    private TextView hTextView;
    private Button mButton;
    private static final String TAG = "SkyNet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mTextView = findViewById(R.id.deviceDetail);
        hTextView = findViewById(R.id.textHome);
        mButton = findViewById(R.id.addHome);
        String id = getIntent().getExtras().get("buttonID").toString();
        String name = getIntent().getExtras().get("buttonName").toString();
        mTextView.setText(name);
        Log.i(TAG, "My Logger onCreate: " + id + " " + name);
        getDeviceHome(id);
        getDeviceDetails(id);

    }

    private void getDeviceHome(String id) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Device").child(id);
        final Query query = ref.child("home");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "My Logger getDeviceDetails 1-1: " + dataSnapshot);
                if (dataSnapshot.exists()) {
                    DatabaseReference refHome =FirebaseDatabase.getInstance().getReference().child("homes").child(dataSnapshot.getValue().toString());
                    Query query1 = refHome;
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                hTextView.setText(dataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    mButton.setVisibility(View.GONE);
                    hTextView.setVisibility(View.VISIBLE);



                }
                else{
                    mButton.setVisibility(View.VISIBLE);
                    hTextView.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void getDeviceDetails(String id) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Device").child(id).child("Info");
        Query query = ref.child("type");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "My Logger getDeviceDetails 1: " + dataSnapshot);
                if (dataSnapshot.exists()) {

                    Object deviceType = dataSnapshot.getValue();
                    String device = deviceType.toString();

                    Log.i(TAG, "My Logger getDeviceDetails 2: " + device);
                    getDeviceInfo(device);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDeviceInfo(final String deviceType) {
        //String deviceType = type.toString();
        Log.i(TAG, "My Logger getDeviceInfo Type: " + deviceType);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //String key = ref.child("deviceID");
        Query query = ref.child("DeviceTypes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Object obj = dataSnapshot.getClass();
                Log.i(TAG, "My Logger getDeviceInfo 1: " + dataSnapshot);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot device : dataSnapshot.getChildren()) {
                        String key = device.getKey();
                        if (key.contentEquals(deviceType)) {
                            Object obj = device.child("A").getValue();
                            Log.i(TAG, "My Logger onDataChange: " + obj);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void updateHome(FirebaseUser currentUser) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        Query query = ref.child("home");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, " My Logger onDataChange 1: " + dataSnapshot);
                    for (DataSnapshot home : dataSnapshot.getChildren()) {
                        String buttonName = (String) home.child("Name").getValue();
                        String buttonID = (String) home.child("ID").getValue();
                        Log.i(TAG, "My Logger onDataChange 2: " + buttonName + " " + buttonID);
                        createHome(buttonID, buttonName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        updateHome(mAuth.getCurrentUser());
        mButton.setVisibility(View.GONE);

    }

    protected void createHome(String buttonID, String buttonName) {

        int id = 0;
        LinearLayout layout = (LinearLayout) findViewById(R.id.homelayout);

        Button myHome = new Button(this);
        myHome.setText(buttonName);
        try {
            id = Integer.parseInt(buttonID);
        } catch (NumberFormatException nfe) {

        }
        myHome.setId(id);
        myHome.setOnClickListener(handleOnClickHome(id, buttonID, buttonName));
        layout.addView(myHome);
    }


    View.OnClickListener handleOnClickHome(final int id, final String buttonID, final String buttonName) {
        Log.i(TAG, "My Logger handleOnClickHome: " + buttonID + buttonName);
        return new View.OnClickListener() {
            public void onClick(View v) {
                homeClick(id, buttonID, buttonName);
            }
        };
    }


    private void homeClick(int id, String buttonID, String buttonName) {
        Log.i(TAG, "My Logger homeClick 1: " + buttonID);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent = new Intent(this, DeviceDetail.class);
        Log.i(TAG, "My Logger createActivity: " + buttonID + " " + buttonName);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Device").child(mTextView.getText().toString());
        ref.child("home").setValue(buttonID);
        Log.i(TAG, "My Logger homeClick 2: " + mTextView.getText() + " " + buttonID);
        LinearLayout layout = (LinearLayout) findViewById(R.id.homelayout);
        layout.removeAllViews();
    }
}
