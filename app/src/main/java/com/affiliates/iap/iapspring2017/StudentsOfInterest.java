package com.affiliates.iap.iapspring2017;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class StudentsOfInterest extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisor_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Intent in = getIntent();


    }

    @Override
    public void onStop() {
        System.out.println("onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume()");
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestroy()");
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
