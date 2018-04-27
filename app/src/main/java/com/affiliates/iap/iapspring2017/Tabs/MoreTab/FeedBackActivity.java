package com.affiliates.iap.iapspring2017.Tabs.MoreTab;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.DataService;

public class FeedBackActivity extends BaseActivity {
    private EditText mSubject, mSugestion;
    private Toolbar mToolbar;
    private Button mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proyect_feedback);
        bind();
        setToolbar();

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("Sending Feedback");
                DataService.sharedInstance().submitFeedback(mSubject.getText().toString(), mSugestion.getText().toString(), new Callback<String>() {
                    @Override
                    public void success(String data) {
                        hideProgressDialog();
                        createDismissAlertDialog(
                                FeedBackActivity.this,
                                "Success",
                                "Feedback was submitted")
                                .setCancelable(false)
                                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onBackPressed();
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void failure(String message) {
                        hideProgressDialog();
                        showDismissAlertDialog(
                                FeedBackActivity.this,
                                "Error",
                                "Error sending feedback, check network connection"
                        );
                    }
                });
            }
        });
    }

    private void bind(){
        mSubmit = (Button) findViewById(R.id.button5);
        mSugestion = (EditText) findViewById(R.id.suggestion);
        mSubject =(EditText) findViewById(R.id.subject);
    }

    private void setToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
