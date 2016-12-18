package com.egco428.graduation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
public class GradLoginActivity extends AppCompatActivity {
    private EditText userLoginTxt;
    private EditText passLoginTxt;
    private TextView signup;
    private Button loginBtn;
    private FirebaseDatabase database;
    private DatabaseReference Myref;
    private ArrayList<Graduate> gradList;
    public static final String Firstname = "Firstname";
    public static final String Username = "Username";
    public static final String Code = "Code";
    Graduate graduate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grad_login);
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        String Uname = userDetails.getString("Username", "0");
        if (Uname != "0"){
            Intent intent = new Intent(this,GradloginSuccessActivity.class);
            startActivity(intent);
        }
        View decorView = getWindow().getDecorView();
        //// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        database = FirebaseDatabase.getInstance();
        Myref = database.getReference(databaseconfig.Grad_Table);
        gradList = new ArrayList<>();
        getUserPass();
        userLoginTxt = (EditText)findViewById(R.id.userLoginTxt);
        passLoginTxt = (EditText)findViewById(R.id.passLoginTxt);
        signup = (TextView)findViewById(R.id.signupTxt);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        //// setonclick
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GradLoginActivity.this,GradSignupActivity.class);
                startActivity(intent);
            }
        });
        //// setonclick
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkuser();
                Intent intent = new Intent(GradLoginActivity.this,GradloginSuccessActivity.class);
            }
        });
    }
    //// check username password ว่าตรงกับใน firebase ไหม
    public void checkuser(){
        boolean correct = false;
        for(int i=0 ; i < gradList.size() ; i++){
            if(userLoginTxt.getText().toString().equals(gradList.get(i).getUsername()) && passLoginTxt.getText().toString().equals(gradList.get(i).getPassword())) {
                graduate = gradList.get(i);
                correct = true;
            }
        }
        if(correct){
            Toast.makeText(GradLoginActivity.this, "Login success.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GradLoginActivity.this, GradloginSuccessActivity.class);
//            intent.putExtra(Firstname,graduate.getFirstname());
//            intent.putExtra(Username,graduate.getUsername());
//            intent.putExtra(Code,graduate.getRandomnum());
            SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
            SharedPreferences.Editor edit = userDetails.edit();
            edit.clear();
            edit.putString("Firstname", graduate.getFirstname());
            edit.putString("Username", graduate.getUsername());
            edit.putString("Code", graduate.getRandomnum());
            edit.commit();
            startActivityForResult(intent,0);
            userLoginTxt.setText("");
            passLoginTxt.setText("");
        }
        else{
            Toast.makeText(GradLoginActivity.this, "Login fail.", Toast.LENGTH_SHORT).show();
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