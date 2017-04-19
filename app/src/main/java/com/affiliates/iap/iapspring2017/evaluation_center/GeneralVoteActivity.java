package com.affiliates.iap.iapspring2017.evaluation_center;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;

public class GeneralVoteActivity extends AppCompatActivity {
    private String name, id;
    private TextView title;
    private Button bestPoster, bestPresentation,cancel;

    private final static String TAG = "GeneralVoteActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_vote);
        bind();



    }

    private void bind(){
        name = getIntent().getStringExtra("posterName");
        id = getIntent().getStringExtra("posterID");
        title = (TextView) findViewById(R.id.poster_desc_title);
        title.setText(name);
        bestPoster = (Button) findViewById(R.id.button_poster);
        bestPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote(0);
                Log.v(TAG, "Voted for best Poster");
            }
        });
        bestPresentation = (Button) findViewById(R.id.button_presentation);
        bestPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote(1);
                Log.v(TAG, "Voted for best Presentation");
            }
        });

    }

    private void vote(int type){
        User user = Constants.getCurrentLoggedInUser();
        DataService.sharedInstance().getUserData(user.getUserID(), new Callback() {
            @Override
            public void success(Object data) {
                Constants.setCurrentLogedInUser((User) data);
                //get the latest status on voted
                Log.v(TAG, "User updated");


            }

            @Override
            public void failure(String message) {

            }
        });
        if(!user.hasVoted(type)){
            DataService.sharedInstance().submitGeneralVote(id, type, new Callback() {
                @Override
                public void success(Object data) {
                    Log.v(TAG, "Voting completed");
                    GeneralVoteActivity.super.onBackPressed();
                    finish();

                }

                @Override
                public void failure(String message) {

                }
            });
        }
        else{

            Log.v(TAG, "User already Voted");
            String voteType ="";
            switch(type){
                case 0:
                    voteType = "Presentation";
                    break;
                case 1:
                    voteType = "Poster";
                    break;
                default:
                    break;

            }
            Toast.makeText(getApplicationContext(), "You've already voted for Best " + voteType, Toast.LENGTH_SHORT).show();


        }

    }


}
