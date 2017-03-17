package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.sing_in.SignInActivity;

public class LoginOrRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);
       TextView login = (TextView) findViewById(R.id.loginButton),
                register = (TextView) findViewById((R.id.registerButton));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrRegister.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrRegister.this, NameActivity.class);
                startActivity(intent);
            }
        });

    }
}
