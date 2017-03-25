package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.myUtils.Utils;

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
                System.out.println("It's here");
                int error = checkEmail(email.getText().toString());
                Log.v("EnterEmail", ""+error);
                if(error==0){
                    Utils.tmpEmail = email.getText().toString();
                    Intent intent = new Intent(EnterEmail.this,EmailConfirmation.class);
                    startActivity(intent);
                }
               else{
                    switch(error){
                        case 1: Toast.makeText(getApplicationContext(), "Enter your email", Toast.LENGTH_SHORT).show(); break;
                        case 2: Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show(); break;

                    }
                }

            }
        });
    }

    private int checkEmail(String email){
        if(email.length()==0) return 1;
        if(!email.contains(".") || !email.contains("@")) return 2;
        return 0;
    }
}
