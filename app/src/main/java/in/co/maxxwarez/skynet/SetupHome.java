package in.co.maxxwarez.skynet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SetupHome extends AppCompatActivity implements View.OnClickListener{

    private TextView mtextView;
    private Button  mSetHome;
    private FirebaseAuth mAuth;
    private static final String TAG = "SkyNet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mtextView = findViewById(R.id.editHome);
        mSetHome = findViewById(R.id.buttonHome);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mSetHome.setVisibility(View.VISIBLE);

    }

    class updateFirebase extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String homeValue = mtextView.getText().toString();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            String homeID = ref.child("home").push().getKey();
            ref.child("users").child(currentUser.getUid()).child("home").child(homeID).child("Name").setValue(homeValue);
            ref.child("users").child(currentUser.getUid()).child("home").child(homeID).child("ID").setValue(homeID);
            ref.child("homes").child(homeID).setValue(homeValue);
            return null;
        }
    }


    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: ");
        updateFirebase Fbase = new updateFirebase();
        Fbase.execute();

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
