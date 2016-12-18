package com.egco428.graduation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private LinearLayout gradLayout;
    private LinearLayout guestLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        //// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        gradLayout = (LinearLayout)findViewById(R.id.gradLayout);
        guestLayout = (LinearLayout)findViewById(R.id.guestLayout);

        //// setonclick ให้ไปยังหน้า login ของ graduate
        gradLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,GradLoginActivity.class);
                startActivity(intent);
            }
        });


        //// setonclick ให้ไปหน้า login ของ guest
        guestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,GuestLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
