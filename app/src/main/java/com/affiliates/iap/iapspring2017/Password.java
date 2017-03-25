package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.myUtils.Utils;
import com.affiliates.iap.iapspring2017.sing_in.EmailConfirmation;
import com.affiliates.iap.iapspring2017.sing_in.EnterEmail;

public class Password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Button back, next;
        final EditText password = (EditText) findViewById(R.id.passwordField),
                confirm = (EditText) findViewById(R.id.confirmPasswordField);
        back = (Button) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Password.this, EnterEmail.class));

            }
        });

        next = (Button) findViewById(R.id.nextButton);
       next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int error = checkPassword(password.getText().toString(), confirm.getText().toString());
                if(error==0) {
                    Utils.tmpPass = password.getText().toString();
                    startActivity(new Intent(Password.this, EmailConfirmation.class));
                }
                else
                    switch (error){
                        case 1: Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show(); break;
                        case 2: Toast.makeText(getApplicationContext(), "Password must have at least 6 characters", Toast.LENGTH_SHORT).show(); break;
                    }

            }
        });
    }

    public int checkPassword(String pass, String confirm){
        if(!pass.contentEquals(confirm)) return 1;
        if(pass.length()<6) return 2;
        return 0;
    }
}
