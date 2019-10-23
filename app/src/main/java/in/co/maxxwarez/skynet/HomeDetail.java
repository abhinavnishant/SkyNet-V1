package in.co.maxxwarez.skynet;

import android.os.Bundle;
import android.util.Log;
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

public class HomeDetail extends AppCompatActivity {
    private static final String TAG = "SkyNet";
    public FirebaseAuth mAuth;
    public TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String id = getIntent().getExtras().get("buttonID").toString();
        String name = getIntent().getExtras().get("buttonName").toString();
       // String buttonID = getIntent().getExtras().get()
        mTextView = findViewById(R.id.textHome);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user = currentUser.getUid();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final Query query = ref.child("homes").child(id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTextView.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Query query1 = ref.child("Device");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.child("home").getValue().equals(id)){
                        createDevice(data.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createDevice(String key) {
        Log.i(TAG, "createDevice: " + key);
        int id = 0;
        LinearLayout layout = (LinearLayout) findViewById(R.id.homelayout);

        TextView myDevice = new TextView(this);
        myDevice.setText(key);
        myDevice.setClickable(true);

        try {
            id   = Integer.parseInt(key);
        } catch(NumberFormatException nfe) {

        }
        myDevice.setId(id);
        //myDevice.setOnClickListener(handhleOnClickDevice(id, idString));
        layout.addView(myDevice);
    }

}
