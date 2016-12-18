package com.egco428.graduation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
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

    public boolean waiting = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradlogin_success);

        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
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

        name = userDetails.getString(Firstname, "0");
        username = userDetails.getString(Username, "0");
        code = userDetails.getString(Code, "0");

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
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
        String newLati = newLat + "";
        String newLong = newLon + "";
        //Add to Firebase
        GradPosition gradPo = new GradPosition(username, newLati, newLong);
        Myref2.child(username).setValue(gradPo);
    }

    public void logout(View v){
        //Remove userdetails stored files(Logout)
        SharedPreferences preferences = getSharedPreferences("userdetails", 0);
        preferences.edit().remove("Firstname").apply();
        preferences.edit().remove("Username").apply();
        preferences.edit().remove("Code").apply();
        String Uname = preferences.getString("Username", "0");
        if (Uname.equals("0") ){
            Intent intent = new Intent(this,GradLoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
            {
                //getPoint
                if (event.getPointerCount()==3){
                    Log.d("Controlls","Pressed "+event.getPointerCount()+" go back");
                    waiting =false;
                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                }
                break;
            }
            //I decided to waiting first for the case 3 pointers resting 2 fingers on screen
            case MotionEvent.ACTION_POINTER_UP:
            {
                if (event.getPointerCount()==2 &&waiting){
                    Log.d("Controlls","Pressed "+event.getPointerCount()+" pop up menu");
                }
                break;
            }
        }
        return true;
    }
}
