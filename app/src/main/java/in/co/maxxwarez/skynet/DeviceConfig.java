package in.co.maxxwarez.skynet;

import android.content.DialogInterface;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import static in.co.maxxwarez.skynet.R.id.addConfig;
import static in.co.maxxwarez.skynet.R.id.addOperator;

public class DeviceConfig extends AppCompatActivity implements View.OnClickListener {
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


    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.addInput) {

            final String[] list = {"Your Devices", "External Sources", "Fixed Parameters"};
           // alertBuilderIP(list, "Select Source");
            getSRList(new MyCallback() {

                @Override
                public void onCallback(String[] value) {
                    Log.i(TAG, "Result Value " + value);
                    //alertBuilderIP(value,"Sensor List", "sensor");
                }
            }, "s");
        }

        else if (i == R.id.addValue) {
            if(addInputType == "sensor"){
                addSensorValue();
            }
            if(addInputType == "switch")
                addSwitchValue();
        }

        else if (i == R.id.addOutput) {
            getDeviceListOP(new MyCallback() {

                @Override
                public void onCallback(String[] value) {

                    alertBuilderOP(value,"Device List");
                }
            });
        }

        else if (i == R.id.addState) {
            addSwitchState();
            Log.i(TAG, "Config: Add State");
        }

        else if (i == addOperator) {
            if(addInputType == "switch"){
                operatorType.setText("Equals");
            }
            if(addInputType == "sensor"){
                final String[] list = {"Equals", "Less Than", "Greater Than", "Between"};
                alertBuilderOperator(list, "Select Operator");
            }
        }
        
        else if (i == addConfig){
            addConfig();
        }
    }

    private void addConfig() {
        //TODO Add Update to Firebase DB
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String logicID = ref.child("logics").push().getKey();
        ref.child("logics").child(logicID).child("test").setValue("test");
        Log.i(TAG, "Config Push "  + logicID + " " + addInputType) ;
        if(addInputType == "switch"){
            logicID = ref.child("logic").push().getKey();
            ref.child("Device").child(sourceDevice).child("State").child(addInput).child("logic").child(logicID).setValue(true);
            Log.i(TAG, "Config Logic Value SW: " + destDevice + " " + addOP + " " + addInput + " " + logicID);
        }
        if(addInputType == "sensor"){
            ref.child("Device").child(sourceDevice).child("Data").child(addInput).child("logic").child(logicID);
            Log.i(TAG, "Config Logic Value SR: " + destDevice + " " + addOP + " "  + " " + logicID);
        }


    }

    private void addSwitchValue() {
        final String[] list = {"Off", "On"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select State");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    swState = false;
                    mAddValue.setText("OFF");
                }
                if(which == 1){
                    swState = true;
                    mAddValue.setText("ON");
                }
            }
        });
        builder.show();
    }

    private void addSwitchState() {
        final String[] list = {"Off", "On"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select State");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    addState = false;
                    mAddState.setText("OFF");
                }
                if(which == 1){
                    addState = true;
                    mAddState.setText("ON");
                }
            }
        });
        builder.show();
    }

    public void getSwitchValue(final MyCallback myCallback, String s) {
        final ArrayList<String> result = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Device").child(s).child("State").child(addInput);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                    result.add(String.valueOf(dataSnapshots.getValue()));

                }
                String frnames[]=result.toArray(new String[result.size()]);
                myCallback.onCallback(frnames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void addSensorValue(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Title");
        alert.setMessage("Message :");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        getSensorValue(new MyCallback() {

            @Override
            public void onCallback(String[] value) {

                input.setText(value[1]);
            }
        }, deviceID);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                mAddValue.setText(value);
                Log.i(TAG, "Config Pin Value : " + value);
                return;
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        Log.d(TAG, "Config Pin Value : " + value);
                        // TODO Auto-generated method stub
                        return;
                    }
                });
        alert.show();
    }

    public void prepareBuilder(String source, final int sequence) {
        getDeviceList(new MyCallback() {

            @Override
            public void onCallback(String[] value) {
                Log.i(TAG, "Config: Value " + value + " " + sequence);

                //alertBuilder(value,"");
            }
        });
    }

    private void alertBuilderOperator(final String[] list, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    mOperatorType = 0;
                }
                if(which==1){
                    mOperatorType = -1;
                }
                if(which == 2){
                    mOperatorType = 1;
                }
                if(which == 3){
                    mOperatorType = 2;
                }
                operatorType.setText(list[which]);

            }
        });
        builder.show();
    }

    private void alertBuilderIP(final String[] list, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDeviceList(new MyCallback() {

                    @Override
                    public void onCallback(String[] value) {

                        alertBuilderDevice(value,"Device List");
                    }
                });

            }
        });
        builder.show();
    }

    private void alertBuilderOP(final String[] list, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                destDevice = list[which];
                getSWList(new MyCallback() {

                    @Override
                    public void onCallback(String[] value) {

                        alertBuilderOPSW(value,"Switch List", "switch");
                    }
                }, list[which]);

            }
        });
        builder.show();
    }

    private void alertBuilderDevice(final String[] list, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                alertBuilderSource(list[which]);
                sourceDevice = list[which];
                /*getIPList(new MyCallback() {

                    @Override
                    public void onCallback(String[] value) {

                        alertBuilderIP(value,"IP List");
                    }
                }, list[which]);*/

            }
        });
        builder.show();
    }

    private void alertBuilderSource(final String s) {
        final String[] list = {"Sensor", "Switch"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Channel");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    getSRList(new MyCallback() {

                        @Override
                        public void onCallback(String[] value) {

                            alertBuilderIP(value,"Sensor List", "sensor");
                        }
                    }, s);
                }
                if(which == 1){
                    getSWList(new MyCallback() {

                        @Override
                        public void onCallback(String[] value) {

                            alertBuilderIP(value,"Switch List", "switch");
                        }
                    }, s);

                }

            }
        });
        builder.show();
    }

    private void alertBuilderIP(final String[] list, String title, final String ipType) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Config: Selection " + list[which]);
                addInput = list[which];
                addInputType = ipType;
                mAddInput.setText(list[which] );


            }
        });
        builder.show();
    }
    private void alertBuilderOPSW(final String[] list, String title, final String ipType) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Config: Selection " + list[which]);
                mAddOP.setText(list[which] );
                addOP = list[which];
            }
        });
        builder.show();
    }
    public interface MyCallback{
        void onCallback(String[] value);
    }

    public void getDeviceList(final MyCallback myCallback) {
        final ArrayList<String> result = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Device").orderByChild("home").equalTo(homeID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                    result.add(dataSnapshots.getKey());
                }
                String frnames[]=result.toArray(new String[result.size()]);
                myCallback.onCallback(frnames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getDeviceListOP(final MyCallback myCallback) {
        final ArrayList<String> result = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Device").orderByChild("home").equalTo(homeID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                    result.add(dataSnapshots.getKey());
                }
                String frnames[]=result.toArray(new String[result.size()]);
                myCallback.onCallback(frnames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getSWList(final MyCallback myCallback, String s) {
        final ArrayList<String> result = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Device").child(s).child("State");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                    result.add(dataSnapshots.getKey());

                }
                String frnames[]=result.toArray(new String[result.size()]);
                myCallback.onCallback(frnames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getSRList(final MyCallback myCallback, String s) {
        final ArrayList<String> result = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Device").child(deviceID).child("Data");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                    result.add(dataSnapshots.getKey());

                }
                String frnames[]=result.toArray(new String[result.size()]);
                myCallback.onCallback(frnames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getSensorValue(final MyCallback myCallback, String s) {
        final ArrayList<String> result = new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Device").child(s).child("Data").child(addInput);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                    result.add(String.valueOf(dataSnapshots.getValue()));

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
