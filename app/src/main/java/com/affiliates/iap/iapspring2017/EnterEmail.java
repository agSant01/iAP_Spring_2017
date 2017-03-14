package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.affiliates.iap.iapspring2017.Utils.Utils;

public class EnterEmail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_email);
        final EditText email = (EditText) findViewById(R.id.emailField);
        Button back = (Button) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterEmail.this, NameActivity.class);
                startActivity(intent);
            }
        });

        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isValidEmail(email.getText().toString())) {
                    Intent intent = new Intent(EnterEmail.this, EnterPassword.class);
                    startActivity(intent);
                }
                else System.out.println("Email is not valid");
            }
        });
    }
}
