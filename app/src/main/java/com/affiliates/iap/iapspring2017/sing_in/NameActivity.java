package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.R;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        final EditText name = (EditText) findViewById(R.id.name_field);
        TextView back = (TextView) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView next = (TextView) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int error = checkName(name.getText().toString());
                if(error==0) {
                    Intent intent = new Intent(NameActivity.this, EnterEmail.class);
                    intent.putExtra("Name", name.getText().toString());
                    intent.putExtra("AccountType", getIntent().getStringExtra("AccountType"));
                    intent.putExtra("UserType", getIntent().getStringExtra("UserType"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }
                else{
                    switch(error) {
                        case 1:
                            Toast.makeText(getApplicationContext(), "Please, enter your name", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(getApplicationContext(), "sorry, that name is not valid", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }

    private int checkName(String name){
        if(name.isEmpty()) return 1;
        for(int index=0; index<name.length(); index++){
            if( !Character.isLetter(name.charAt(index)) && name.charAt(index)!=' ') return 2;
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
