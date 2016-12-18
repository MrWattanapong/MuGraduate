package com.egco428.graduation;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GuestLoginActivity extends AppCompatActivity {

    private EditText userGuest;
    private EditText codeGuest;
    private Button addbtn;

    private FirebaseDatabase database;
    private DatabaseReference Myref;
    private ArrayList<Graduate> gradList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_login);

        View decorView = getWindow().getDecorView();
        //// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        userGuest = (EditText)findViewById(R.id.userGusetTxt);
        codeGuest = (EditText)findViewById(R.id.codeGuestTxt);
        addbtn = (Button)findViewById(R.id.addBtn);

        database = FirebaseDatabase.getInstance();
        Myref = database.getReference(databaseconfig.Grad_Table);
        gradList = new ArrayList<>();
        getUserPass();

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend();
            }
        });
    }

    public void getLocation(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }

    public void addFriend(){
        boolean check = false;
        for(int i=0 ; i < gradList.size() ; i++){
            if (userGuest.getText().toString().equals(gradList.get(i).getUsername()) && codeGuest.getText().toString().equals(gradList.get(i).getRandomnum())){
                check = true;
            }
        }
        if (check){
            Toast.makeText(GuestLoginActivity.this, "Good.", Toast.LENGTH_SHORT).show();


        }else {
            Toast.makeText(GuestLoginActivity.this, "Invalid username/code.", Toast.LENGTH_SHORT).show();
        }
    }

    //// ดึงข้อมูลใน firebase มาเก็บใน arraylist
    public void getUserPass(){
        Myref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Graduate dataFirebase = dataSnapshot.getValue(Graduate.class);
                gradList.add(dataFirebase);
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
}
