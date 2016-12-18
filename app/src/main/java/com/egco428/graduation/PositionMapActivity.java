package com.egco428.graduation;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;


public class PositionMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String firstname;
    String latitude;
    String longtitude;
    double double_lat, double_long;
    public static final String Username = "UsernameGr";
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    boolean trigger;


    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_map);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.11
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        mMap.setMyLocationEnabled(true);

        username = getIntent().getStringExtra(Username);
        Log.d("Intent user ", getIntent().getStringExtra(Username));
        //Declared Firebase node
        DatabaseReference mUsersRef = mRootRef.child("GradPosition");
        final DatabaseReference mDetailsRef = mRootRef.child("graduate");
        mDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DataSnapshot", dataSnapshot.toString());

                firstname = getFirstname(dataSnapshot);

                //Declare more Shared Preference values separate between firstname and username
                SharedPreferences userDetails = getSharedPreferences("history", MODE_PRIVATE);
                SharedPreferences usern = getSharedPreferences("username", MODE_PRIVATE);
                //get size to prep. for adding new Variable to two Sharedpref
                int number = userDetails.getAll().size();
                Log.d("SharedPref number", String.valueOf(number));
                SharedPreferences.Editor edit = userDetails.edit();
                SharedPreferences.Editor edit1 = usern.edit();
                Log.d("Test",firstname);
                /*if duplicate skip submit variable*/
                for (int round = 0; round < number; round++) {
                    String Uname = userDetails.getString("user" + (round+1), "Select");
                    Log.d("aasdasdzaa",Uname+" "+firstname);
                    if (Objects.equals(Uname, firstname)){
                        trigger =true;
                        break;
                    }
                }
                if(!trigger){
                    Log.d("map comp ",firstname+" "+username);
                    edit.putString("user" + (number+1), firstname);
                    edit1.putString(firstname,username);
                    edit.apply();
                    edit1.apply();
                }
                /*Deleted code in error case*/
//                edit.remove("user1").apply();

                /*Prep. for spinner*/
                String Uname = userDetails.getString("user" + number, "0");
                if (!Uname.equals("0")) {
                    Log.d("SharedPref username", Uname);
                } else {
                    Log.d("SharedPref false", userDetails.getAll().size() + "");
                }
                Log.d("SharedPref number", String.valueOf(number));
                String array_spinner[];
                array_spinner=new String[number];
                for (int a=0;a<number;a++){
                    array_spinner[a]=userDetails.getString("user" + (a+1), "0");
                }
/*Check two variable details*/
                Map<String,?> keys = userDetails.getAll();

                for(Map.Entry<String,?> entry : keys.entrySet()){
                    Log.d("map values",entry.getKey() + ": " +
                            entry.getValue().toString());
                }

                Map<String,?> data = usern.getAll();
                for(Map.Entry<String,?> entry : data.entrySet()){
                    Log.d("map data",entry.getKey() + ": " +
                            entry.getValue().toString());
                }

                Spinner s = (Spinner) findViewById(R.id.selector);

                s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        final SharedPreferences usern = getSharedPreferences("username", MODE_PRIVATE);
                        String selectedItem = adapterView.getItemAtPosition(i).toString();
                        firstname = selectedItem;
                        DatabaseReference refTemp = FirebaseDatabase.getInstance().getReference();
                        /*Get value database's target for one time only*/
                        refTemp.child("GradPosition").child(usern.getString(firstname,"0")).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    /*seperate array for child (JSON like)*/
                                    for (DataSnapshot i:dataSnapshot.getChildren()){
                                        if (Objects.equals(i.getKey(), "latitude")){
                                            latitude =i.getValue().toString();
                                        }
                                        if(Objects.equals(i.getKey(), "longitude")){
                                            longtitude=i.getValue().toString();
                                        }
                                    }
                                }
                                Log.d("Firebase Success",firstname+" "+latitude+" "+longtitude);
                                username = usern.getString(firstname,"0");
                                addmarker();
                                watchinglist(username);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                ArrayAdapter adapter = new ArrayAdapter(PositionMapActivity.this, android.R.layout.simple_spinner_item, array_spinner);
                s.setAdapter(adapter);

//                Toast.makeText(PositionMapActivity.this, firstname, Toast.LENGTH_SHORT).show();

            }
//           //*try to get Firstname with only value knows*/*/
            private String getFirstname(DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    for (DataSnapshot j : i.getChildren()) {
                        if (j.getKey().toString().equals("username")) {
                            if (j.getValue().equals(username)) {
                                for (DataSnapshot k : i.getChildren()) {
                                    if (k.getKey().toString().equals("firstname")) {
                                        return k.getValue().toString();
                                    }
                                }
                            }

                        }
                    }
                }
                return null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Snapshot: ", databaseError.toString());

            }
        });


        watchinglist(username);
    }

    /*Seperate onDataChange listener in case different Graduate*/
    public void watchinglist(final String username){
        DatabaseReference mUsersRef = mRootRef.child("GradPosition");
        Log.d("Firebase", "Check intent " + username);
        DatabaseReference mNew = mUsersRef.child(username);
        mNew.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Firebase","Startinging ondatachange on "+username);
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    if (Objects.equals(i.getKey(), "latitude")) {
                        latitude = i.getValue().toString();
                    } else if (Objects.equals(i.getKey(), "longitude")) {
                        longtitude = i.getValue().toString();
                    }
                    Log.d("Firebase", latitude+" "+longtitude);

                }
                addmarker();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }
    public void addmarker(){
        mMap.clear();
        double_lat = Double.parseDouble(latitude);
        double_long = Double.parseDouble(longtitude);

        Log.d("Map", double_lat + "");
        Log.d("Map", double_long + "");
        LatLng mahidol = new LatLng(double_lat, double_long);
        mMap.addMarker(new MarkerOptions().position(mahidol).title(firstname));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mahidol));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }
}
