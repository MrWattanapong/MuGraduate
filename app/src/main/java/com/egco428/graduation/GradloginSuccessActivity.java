package com.egco428.graduation;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GradloginSuccessActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;

    private TextView nameTxt;
    private TextView inviteuser;
    private TextView invitecode;

    private FirebaseDatabase database;
    private DatabaseReference Myref2;
    private ArrayList<GradPosition> gradList2;

    public static final String Firstname = "Firstname";
    public static final String Username = "Username";
    public static final String Code = "Code";

    private String name;
    private String username;
    private String code;
    private double Lat;
    private double Lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradlogin_success);

        View decorView = getWindow().getDecorView();
        //// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Create Google API Client instance
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        inviteuser = (TextView) findViewById(R.id.inviteuserTxt);
        invitecode = (TextView) findViewById(R.id.invitecodeTxt);
        nameTxt = (TextView) findViewById(R.id.nameTxt);

        database = FirebaseDatabase.getInstance();
        Myref2 = database.getReference(databaseconfig.Position_Table);
        gradList2 = new ArrayList<>();

        name = getIntent().getStringExtra(Firstname);
        username = getIntent().getStringExtra(Username);
        code = getIntent().getStringExtra(Code);

        nameTxt.setText(name);
        inviteuser.setText("Username : " + username);
        invitecode.setText("Code : " + code);

        getUserPass();

    }


    //// ดึงข้อมูลใน firebase มาเก็บใน arraylist
    public void getUserPass() {
        Myref2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GradPosition dataFirebase2 = dataSnapshot.getValue(GradPosition.class);
                gradList2.add(dataFirebase2);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Connect to Google API Client
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            // Disconnect Google API Client if available and connected
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Do something when connected with Google API Client

        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            // Do something when Location Provider not available
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do something when Google API Client connection was suspended

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Do something when Google API Client connection failed

    }

    @Override
    public void onLocationChanged(Location location) {
        // Do something when got new current location
        Lat = location.getLatitude();
        Lon = location.getLongitude();

        //get data
        double newLat = Lat;
        double newLon = Lon;

        String newLati = newLat+"";
        String newLong = newLon+"";

        //Add to Firebase
        GradPosition gradPo = new GradPosition(username, newLati, newLong);
        Myref2.child(username).setValue(gradPo);
    }
}