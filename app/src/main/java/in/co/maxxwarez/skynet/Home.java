package in.co.maxxwarez.skynet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static in.co.maxxwarez.skynet.R.string.signed_in_name;

public class Home extends AppCompatActivity implements View.OnClickListener{
    private TextView mStatusTextView;
    private DatabaseReference mDatabase;
    private TextView mSignedInName;
    private TextView mSetUpNewDevice;
    private FirebaseAuth mAuth;
    private static final String TAG = "SkyNet";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mSignedInName = findViewById(R.id.signed_in_name);
        mSetUpNewDevice = findViewById(R.id.new_device_setup);
        mStatusTextView =findViewById(R.id.no_device_found);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mSignedInName.setText(getString(signed_in_name, currentUser.getDisplayName()));
        mSetUpNewDevice.setVisibility(View.GONE);
        Log.i(TAG, "My Logger -1 onCreate: ");
        updateUI(currentUser);
    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
    }

    public void checkDevice(final FirebaseUser user){
        Log.i(TAG, "My Datalogger 0checkDevice: ");
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("users").child(user.getUid()).child("home");
        //Query query2 = ref.child("users").child(user.getUid()).child("homeID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot users : dataSnapshot.getChildren()){
                        //String userID =  getString(signed_in_name, user.getDisplayName());
                        mStatusTextView.setText(getString(signed_in_name,user.getDisplayName()));
                        Log.i(TAG, "My Logger 1 onDataChange: ");
                        //mSetUpNewDevice.setVisibility(View.VISIBLE);
                        goHome();

                       // mSetUpNewDevice.setText("Y");


                    }
                }
                else
                {
                    mStatusTextView.setText(R.string.no_home_found);
                    mSetUpNewDevice.setVisibility(View.VISIBLE);
                    mSetUpNewDevice.setText("Set Home");


                    //ref.child("homeUserBinding").child(user.getUid()).setValue("Home1");
                    //ref.child("users").child(user.getUid()).child("email").setValue(user.getEmail());
                }
            }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void goHome(){
        Intent intent = new Intent(this, myHome.class);
        startActivity(intent);
    }

    private void updateUI(final FirebaseUser user){


        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("users").child(user.getUid()).child("deviceID");
        //Query query2 = ref.child("users").child(user.getUid()).child("homeID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot users : dataSnapshot.getChildren()){

                        mStatusTextView.setText(getString(signed_in_name,user.getDisplayName()));
                        mSetUpNewDevice.setVisibility(View.GONE);
                        checkDevice(user);
                    }
                }
                else
                {
                    mStatusTextView.setText(R.string.no_device_found);
                    mSetUpNewDevice.setVisibility(View.VISIBLE);

                    ref.child("users").child(user.getUid()).child("name").setValue(user.getDisplayName());
                    ref.child("users").child(user.getUid()).child("email").setValue(user.getEmail());
                }
            }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        String value = mSetUpNewDevice.getText().toString();
        if (value.contentEquals("Set Home")){
            Intent intent = new Intent(this, SetupHome.class);
            startActivity(intent);
        }

        if(value.contentEquals("Setup a New Device")){
            Intent intent = new Intent(this, DeviceSetup.class);
            startActivity(intent);
        }

        }

}
