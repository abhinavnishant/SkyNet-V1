package in.co.maxxwarez.skynet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class DeviceDetail extends AppCompatActivity implements View.OnClickListener {
    public FirebaseAuth mAuth;
    private TextView mTextView;
    private TextView hTextView;
    private Button mButton;
    private Button cButton;
    private static final String TAG = "SkyNet";
    private String homeID;


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
        cButton = findViewById(R.id.addConfig);
        String id = getIntent().getExtras().get("buttonID").toString();
        String name = getIntent().getExtras().get("buttonName").toString();
        mTextView.setText(name);
        getDeviceHome(id);
        getDeviceDetails(id);
        getDeviceInfo(id);
        getDeviceStates(id);

    }

    private void getDeviceInfo(String id) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Device").child(id);
        final Query query = ref.child("Data");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ip : dataSnapshot.getChildren()){
                    Log.i(TAG, "onDataChange: " + ip.getKey() + " " + ip.getValue());
                    createIPRows(ip.getKey(), ip.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDeviceStates(String id) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Device").child(id);
        final Query query = ref.child("State");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ip : dataSnapshot.getChildren()){
                    Log.i(TAG, "onDataChangeState: " + ip.getKey() + " " + ip.getValue());
                   createIPRows(ip.getKey(), ip.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createIPRows(String key, String value) {
        TableLayout tl =  findViewById(R.id.table_ip);
        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView tv = new TextView(this);
        TextView qty = new TextView(this);
        tv.setText(key);
        qty.setText(value);
        row.addView(tv);
        row.addView(qty);
        qty.setClickable(true);
        //ahmqty.setOnClickListener(l);
        tl.addView(row);
    }

    private void getDeviceHome(String id) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Device").child(id);
        final Query query = ref.child("home");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DatabaseReference refHome =FirebaseDatabase.getInstance().getReference().child("homes").child(dataSnapshot.getValue().toString());
                    Query query1 = refHome;
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                hTextView.setText(dataSnapshot.getValue().toString());
                                homeID = dataSnapshot.getKey();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    mButton.setVisibility(View.GONE);
                    hTextView.setVisibility(View.VISIBLE);
                    cButton.setVisibility(View.VISIBLE);



                }
                else{
                    mButton.setVisibility(View.VISIBLE);
                    hTextView.setVisibility(View.GONE);
                    cButton.setVisibility(View.GONE);

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

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Object deviceType = dataSnapshot.getValue();
                    String device = deviceType.toString();
                    getDeviceInfo_Old(device);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDeviceInfo_Old(final String deviceType) {
        //String deviceType = type.toString();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //String key = ref.child("deviceID");
        Query query = ref.child("DeviceTypes");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Object obj = dataSnapshot.getClass();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot device : dataSnapshot.getChildren()) {
                        String key = device.getKey();
                        if (key.contentEquals(deviceType)) {
                            Object obj = device.child("A").getValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected  void updateHome(FirebaseUser currentUser){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        Query query = ref.child("home");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    for (DataSnapshot home : dataSnapshot.getChildren()){
                        String buttonID = home.getKey();
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



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.addHome) {
            updateHome(mAuth.getCurrentUser());
            mButton.setVisibility(View.GONE);
            cButton.setVisibility(View.VISIBLE);

        }
        if (i == R.id.addConfig) {
           Intent intent = new Intent(this, DeviceConfig.class);
           intent.putExtra("deviceID", getIntent().getExtras().get("buttonName").toString());
           intent.putExtra("homeID", homeID);
           startActivity(intent);
        }


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
        return new View.OnClickListener() {
            public void onClick(View v) {
                homeClick(id, buttonID, buttonName);
            }
        };
    }


    private void homeClick(int id, String buttonID, String buttonName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent = new Intent(this, DeviceDetail.class);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Device").child(mTextView.getText().toString());
        ref.child("home").setValue(buttonID);
        LinearLayout layout = (LinearLayout) findViewById(R.id.homelayout);
        layout.removeAllViews();
    }
}
