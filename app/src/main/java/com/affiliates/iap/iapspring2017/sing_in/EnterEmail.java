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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                onBackPressed();
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
                    Intent intent = new Intent(EnterEmail.this,Password.class);
                    intent.putExtra("Email", email.getText().toString());
                    intent.putExtra("AccountType", getIntent().getStringExtra("AccountType"));
                    intent.putExtra("Name", getIntent().getStringExtra("Name"));
                    intent.putExtra("UserType", getIntent().getStringExtra("UserType"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                } else{
                    switch(error){
                        case 1:
                            Toast.makeText(getApplicationContext(), "Please, enter your email", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(getApplicationContext(), "Sorry, that email is not valid", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

            }
        });
    }

    private int checkEmail(String email){

        if(email.length()==0) return 1;

        String regex = "^(.+)@(.+).(.+)$";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher((CharSequence) email);

        if(!email.contains(".") || !email.contains("@") || email.length()<5 || !matcher.matches())
            return 2;

        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
