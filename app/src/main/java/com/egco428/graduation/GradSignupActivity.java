package com.egco428.graduation;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Random;

public class GradSignupActivity extends AppCompatActivity {

    private EditText userRegisTxt;
    private EditText passRegisTxt;
    private EditText conpassRegisTxt;
    private EditText firstnameTxt;
    private EditText lastnameTxt;
    private EditText mobileTxt;
    private Button submitBtn;
    private TextInputLayout passInput;
    private TextInputLayout conInput;

    private FirebaseDatabase database;
    private DatabaseReference Myref;
    private ArrayList<Graduate> gradList;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grad_signup);

        View decorView = getWindow().getDecorView();
        //// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        userRegisTxt = (EditText) findViewById(R.id.usernameTxt);
        passRegisTxt = (EditText) findViewById(R.id.passTxt);
        conpassRegisTxt = (EditText) findViewById(R.id.confirmpassTxt);
        firstnameTxt = (EditText) findViewById(R.id.firstnameTxt);
        lastnameTxt = (EditText) findViewById(R.id.lastnameTxt);
        mobileTxt = (EditText) findViewById(R.id.telTxt);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        passInput = (TextInputLayout)findViewById(R.id.passInput);
        conInput = (TextInputLayout)findViewById(R.id.confirmpassInput);

        database = FirebaseDatabase.getInstance();
        Myref = database.getReference(databaseconfig.Grad_Table);
        gradList = new ArrayList<>();

        //// เมื่อกดปุ่ม submit แล้วจะเช็คค่าในหน้า sign up ว่า ครบถ้วน และถูกต้องไหม
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(passRegisTxt.getText().toString().equals(conpassRegisTxt.getText().toString()))) {
                    passInput.setError("Password doesn't match");
                    conInput.setError("Password doesn't match");

                } else if (userRegisTxt.getText().toString().equals("") ||
                        passRegisTxt.getText().toString().equals("") ||
                        conpassRegisTxt.getText().toString().equals("") ||
                        firstnameTxt.getText().toString().equals("") ||
                        lastnameTxt.getText().toString().equals("") ||
                        mobileTxt.getText().toString().equals("")) {
                    userRegisTxt.setError("Require");
                    passRegisTxt.setError("Require");
                    conpassRegisTxt.setError("Require");
                    firstnameTxt.setError("Require");
                    lastnameTxt.setError("Require");
                    mobileTxt.setError("Require");
                } else {
                    //// เช็ค username ว่าซ้ำกับใน firebase ไหม
                    boolean same = false;
                    for (i = 0; i < gradList.size(); i++) {
                        if (userRegisTxt.getText().toString().equals(gradList.get(i).getUsername())) {
                            same = true;
                        }
                    }
                    if (same) {
                        Toast.makeText(GradSignupActivity.this, "Username is already exists!, Please try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(GradSignupActivity.this, "Sign Up success!!.", Toast.LENGTH_SHORT).show();

                        Random r = new Random();
                        int n = r.nextInt(9999);
                        String num = n+"";

                        String username = userRegisTxt.getText().toString();
                        String password = passRegisTxt.getText().toString();
                        String confirmpass = conpassRegisTxt.getText().toString();
                        String firstname = firstnameTxt.getText().toString();
                        String lastname = lastnameTxt.getText().toString();
                        String mobile = mobileTxt.getText().toString();

                        Graduate objData = new Graduate(username, password, confirmpass, firstname, lastname, mobile,num);
                        Myref.push().setValue(objData);
                        finish();
                    }
                }
            }
        });
        getUserPass();
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
