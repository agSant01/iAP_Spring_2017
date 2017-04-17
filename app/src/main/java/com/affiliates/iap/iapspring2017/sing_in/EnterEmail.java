package com.affiliates.iap.iapspring2017.sing_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

public class EnterEmail extends BaseActivity {
    private static final String TAG = "EnterEmail";

    private EditText mEmail;
    private TextView mBack, mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        bind();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                final String type = getIntent().getStringExtra("AccountType");

                if( !(email.contains("@") && ( email.contains(".com") || email.contains(".edu")) && email.length() > 5)) {
                    Toast.makeText(getApplicationContext(), "Sorry, invalid email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (email.length() == 0){
                    Toast.makeText(getApplicationContext(), "Please, enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Intent in = new Intent(EnterEmail.this, PasswordActivity.class);
                in.putExtra("Email", email);
                in.putExtra("AccountType", type);
                if(type.equals(User.AccountType.UPRMAccount.toString())){
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }else{
                    final String key = emailToKey(email);
                    DataService.sharedInstance().verifyUser(User.AccountType.valueOf(type), email, new Callback<User>() {
                        @Override
                        public void success(User data) {
                            in.putExtra("key", key);
                            if(type.equals("IAPStudent"))
                                in.putExtra("name", keyToName(key));
                            startActivity(in);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }

                        @Override
                        public void failure(String message) {
                            Toast.makeText(getApplicationContext(), "Sorry, email not registered", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void bind(){
        mEmail = (EditText) findViewById(R.id.edit_email);
        mBack = (TextView) findViewById(R.id.backButton);
        mNext = (TextView) findViewById(R.id.nextButton);
    }

    private String emailToKey(String email){
        int i, split = email.indexOf("@");
        email = email.substring(0, split);
        String[] k = email.split("[.]+");
        String str = "";

        for(i = 0; i < k.length-1; i++)
            str += k[i] + "_";
        return str + k[i];
    }

    private String keyToName(String key){
        String ntr = "";
        int split = key.indexOf("_");
        ntr += Character.toUpperCase(key.charAt(0));
        ntr += key.substring(1,split) + " ";
        ntr += Character.toUpperCase(key.charAt(split+1));
        ntr += key.substring(split+2);
        return ntr.replaceAll("[1234567890]+", "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
