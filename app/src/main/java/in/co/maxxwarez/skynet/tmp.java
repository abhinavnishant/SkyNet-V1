package in.co.maxxwarez.skynet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static in.co.maxxwarez.skynet.R.id.addOperator;

public class tmp extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SkyNet";
    private FirebaseAuth mAuth;
    public String homeID;
    final List<Object> ipList = new ArrayList<>();
    private String deviceID;
    private TextView mAddInput;
    private String addInput;
    private String addInputType;
    private TextView mAddValue;
    private int mOperatorType;
    private TextView operatorType;
    private Boolean swState;
    private TextView mAddOP;
    private String addOP;
    private Boolean addState;
    private TextView mAddState;
    private String sourceDevice;
    private String destDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_config);
        //setContentView(mAddInput);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        deviceID = (String) getIntent().getExtras().get("deviceID");
        homeID = getIntent().getExtras().getString("homeID");
        mAddInput = findViewById(R.id.addInput);
        mAddValue = findViewById(R.id.addValue);
        operatorType = findViewById(addOperator);
        mAddOP = findViewById(R.id.addOutput);
        mAddState = findViewById(R.id.addState);
        //mAddInput.setText("test");
        Device device = new Device();
       // String res = device.getId();
       // Log.i(TAG, "Config TMP "  +  res) ;
        final ArrayList<String> result = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Device").child(deviceID);
        getSRList();
    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.addInput) {

        }

    }
    public void getSRList() {
        final ArrayList<Data> mdata = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Device").child("123456").child("data");
        Query query = ref.child("Device").child("123456");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Data data = dataSnapshot.getValue(Data.class);
                        mdata.add(data);
                        Log.i(TAG, "Config Data Value " + mdata.toArray() +" " + data.a1 );

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
