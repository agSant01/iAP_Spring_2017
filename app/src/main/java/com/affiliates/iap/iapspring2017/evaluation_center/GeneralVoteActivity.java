package com.affiliates.iap.iapspring2017.evaluation_center;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.Client;
import com.affiliates.iap.iapspring2017.services.DataService;

import org.w3c.dom.Text;

public class GeneralVoteActivity extends AppCompatActivity {
    private String name, id;
    private TextView title;
    private Button bestPoster, bestPresentation;
    private ImageView posterImage, presentationImage;
    private AlertDialog submission, noInternet;
    private final static String TAG = "GeneralVoteActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_vote);
        bind();



    }


    private void displaySubmissionDialog(final int type){
        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_submission, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Confirm Submission").setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", null);
        submission = dialogBuilder.create();
        submission.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button confirm = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vote(type);
                        dialog.dismiss();
                    }
                });

            }
        });

        submission.show();
    }
    private void bind(){

        name = getIntent().getStringExtra("posterName");
        id = getIntent().getStringExtra("posterID");
        title = (TextView) findViewById(R.id.poster_desc_title);
        title.setText(name);
        bestPoster = (Button) findViewById(R.id.button_poster);
        posterImage = (ImageView) findViewById(R.id.poster_image);
        if(Constants.getCurrentLoggedInUser().hasVoted(0)) {
            bestPoster.setBackgroundResource(R.drawable.button_oval_shape_grey);
            posterImage.setImageResource(R.drawable.ic_poster_icon);
        }

        bestPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client c = new Client(getBaseContext());
                if(c.isConnectionAvailable())
                    displaySubmissionDialog(0);
                else
                    Snackbar.make(findViewById(R.id.general_voting), "No Internet Connection, Please Connect", Snackbar.LENGTH_SHORT).show();

                Log.v(TAG, "Voted for best Poster");
            }
        });

        presentationImage = (ImageView) findViewById(R.id.presentation_image);
        bestPresentation = (Button) findViewById(R.id.button_presentation);
        if(Constants.getCurrentLoggedInUser().hasVoted(1)) {
            bestPresentation.setBackgroundResource(R.drawable.button_oval_shape_grey);
            presentationImage.setImageResource(R.drawable.ic_thumb_up_grey);
        }
        bestPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
                Client c = new Client(getBaseContext());
                if(c.isConnectionAvailable())
                    displaySubmissionDialog(1);
                else
                    Snackbar.make(findViewById(R.id.general_voting), "No Internet Connection, Please Connect", Snackbar.LENGTH_SHORT).show();
            Log.v(TAG, "Voted for best Presentation");
            }
        });

    }

    private void vote(final int type){
        User user = Constants.getCurrentLoggedInUser();
        DataService.sharedInstance().getUserData(user.getUserID(), new Callback() {
            @Override
            public void success(Object data) {
                Constants.setCurrentLogedInUser((User) data);
                //get the latest status on voted
                Log.v(TAG, "User updated");
                if(type==0)
                    posterImage.setImageResource(R.drawable.ic_poster_icon);
                else
                    presentationImage.setImageResource(R.drawable.ic_thumb_up_grey);

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
