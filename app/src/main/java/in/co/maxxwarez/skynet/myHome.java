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

public class myHome extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private static final String TAG = "SkyNet";
    private TextView mHome;
    private TextView mDevice;
    private String homeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "My Logger onCreate: 2");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHome = findViewById(R.id.textHome);
        mDevice = findViewById(R.id.textDevice);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateHome(currentUser);
        updateDevice(currentUser);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void createHome(String buttonID, String buttonName) {


        int id = 0;
        LinearLayout layout = (LinearLayout) findViewById(R.id.homelayout);

        Button myHome = new Button(this);
        myHome.setText(buttonName);
        myHome.setTag(buttonID);
        try {
            id   = Integer.parseInt(buttonName);
        } catch(NumberFormatException nfe) {

        }
        myHome.setId(id);
        myHome.setOnClickListener(handleOnClickHome(id, buttonName, buttonID));
        layout.addView(myHome);



    }

    protected void createDevice(String idString) {
        int id = 0;
        LinearLayout layout = (LinearLayout) findViewById(R.id.devicelayout);

        Button myDevice = new Button(this);
        myDevice.setText(idString);
        try {
             id   = Integer.parseInt(idString);
        } catch(NumberFormatException nfe) {

        }
        myDevice.setId(id);
        myDevice.setOnClickListener(handleOnClickDevice(id, idString));
        layout.addView(myDevice);

    }

    protected void createAssignedDevice(String idString) {
        int id = 0;
        LinearLayout layout = (LinearLayout) findViewById(R.id.assignedlayout);

        Button myDevice = new Button(this);
        myDevice.setText(idString);
        try {
            id   = Integer.parseInt(idString);
        } catch(NumberFormatException nfe) {

        }
        myDevice.setId(id);
        myDevice.setOnClickListener(handleOnClickDevice(id, idString));
        layout.addView(myDevice);

    }


    protected  void updateHome(FirebaseUser currentUser){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        Query query = ref.child("home");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    for (DataSnapshot home : dataSnapshot.getChildren()){
                        String buttonID = home.getKey();
                        homeID = home.getKey();
                        String buttonName = (String) home.getValue();
                        createHome(buttonID, buttonName);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    protected  void updateDevice(FirebaseUser currentUser){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("deviceID");
        Query query = ref;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){

                    for (final DataSnapshot device : dataSnapshot.getChildren()){
                        final String key = device.getKey();
                        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Device").child(key).child("home");
                        Query query1 = ref2;
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    createAssignedDevice(key);
                                }
                                else{
                                    createDevice(key);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                mDevice.setText("Your Un-Assigned Devices");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    View.OnClickListener handleOnClickDevice(final int buttonID, final String buttonName) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                deviceClick (buttonID, buttonName);
            }
        };
    }

    View.OnClickListener handleOnClickHome(final int id, final String buttonName, final String buttonID){
        return new View.OnClickListener() {
            public void onClick(View v) {
                homeClick (id, buttonName, buttonID);
            }
        };
    }
 private void deviceClick(int buttonID, String buttonName){
     Intent intent = new Intent(this, DeviceDetail.class);
     intent.putExtra("buttonID", buttonID);
     intent.putExtra("buttonName", buttonName);
     intent.putExtra("homeID", homeID );
     startActivity(intent);

 }

    private void homeClick(int id, String buttonName, String buttonID){
        Intent intent = new Intent(this, HomeDetail.class);
        intent.putExtra("buttonID", buttonID);
        intent.putExtra("buttonName", buttonName);
        intent.putExtra("id", id);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {

    }
}
