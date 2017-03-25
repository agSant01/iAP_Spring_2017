package com.affiliates.iap.iapspring2017.sing_in;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.MainActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.myUtils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailConfirmation extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);
        Button done = (Button) findViewById(R.id.doneButton);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        registerUser();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailConfirmation.this, SignInActivity.class));
            }
        });

    }

    private void registerUser(){
        progressDialog.setMessage("Registering...");
        progressDialog.show();
        System.out.println(Utils.tmpEmail +", " + Utils.tempName);
        firebaseAuth.createUserWithEmailAndPassword(Utils.tmpEmail, Utils.tmpPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();


            }
        });
    }
}
