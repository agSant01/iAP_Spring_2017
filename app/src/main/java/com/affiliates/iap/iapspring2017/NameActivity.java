package com.affiliates.iap.iapspring2017;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.affiliates.iap.iapspring2017.R;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        final EditText name = (EditText) findViewById(R.id.nameField);
        Button back = (Button) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NameActivity.this, LoginOrRegister.class);
                startActivity(intent);
            }
        });

        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().isEmpty()) {
                    Intent intent = new Intent(NameActivity.this, EnterEmail.class);
                    startActivity(intent);
                }
                else System.out.println("Please enter your name");
            }
        });
    }
}
