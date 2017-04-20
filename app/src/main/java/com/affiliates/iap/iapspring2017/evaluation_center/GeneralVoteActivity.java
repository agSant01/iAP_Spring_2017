package com.affiliates.iap.iapspring2017.evaluation_center;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.Models.Vote;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.Client;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.firebase.auth.FirebaseAuth;

public class GeneralVoteActivity extends AppCompatActivity {
    private final static String TAG = "GeneralVoteActivity";

    private String name, id;
    private TextView title;
    private Button bestPoster, bestPresentation;
    private ImageView posterImage, presentationImage;
    private AlertDialog submission, noInternet;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_vote);
        bind();
        setToolBar();

    }


    private void displaySubmissionDialog(final int type) {
        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        dialogBuilder.setTitle("Confirm Submission").setMessage("You can only vote for this category once. " +
                "Are you sure this is your favorite?").setPositiveButton("Confirm", null)
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

    private void bind() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        name = getIntent().getStringExtra("posterName");
        id = getIntent().getStringExtra("posterID");
        title = (TextView) findViewById(R.id.poster_desc_title);
        title.setText(name);
        bestPoster = (Button) findViewById(R.id.button_poster);
        posterImage = (ImageView) findViewById(R.id.poster_image);
        if (Constants.getCurrentLoggedInUser().hasVoted(0)) {
            bestPoster.setBackgroundResource(R.drawable.button_oval_shape_grey);
            posterImage.setImageResource(R.drawable.ic_poster_icon);
        }

        bestPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteProcess(0);
                Log.v(TAG, "Voted for best Poster");
            }
        });

        presentationImage = (ImageView) findViewById(R.id.presentation_image);
        bestPresentation = (Button) findViewById(R.id.button_presentation);
        if (Constants.getCurrentLoggedInUser().hasVoted(1)) {
            bestPresentation.setBackgroundResource(R.drawable.button_oval_shape_grey);
            presentationImage.setImageResource(R.drawable.ic_thumb_up_grey);
        }
        bestPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteProcess(1);
                Log.v(TAG, "Voted for best Presentation");
            }
        });

    }

    private void setToolBar() {
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

    private void voteProcess(int type) {
        Client c = new Client(getBaseContext());
        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            if (c.isConnectionAvailable())
                if (Constants.getCurrentLoggedInUser().hasVoted(type))
                    Toast.makeText(getBaseContext(), "Already voted for this category", Toast.LENGTH_SHORT).show();
                else
                    displaySubmissionDialog(type);
            else
                Toast.makeText(getBaseContext(), "No Internet Connection, Please Connect", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Sorry, you need to verify your email first.", Toast.LENGTH_SHORT).show();

        }
    }

    private void vote(final int type) {
        User user = Constants.getCurrentLoggedInUser();
        DataService.sharedInstance().getUserData(user.getUserID(), new Callback<User>() {
            @Override
            public void success(User data) {
                Constants.setCurrentLogedInUser(data);
                //get the latest status on voted
                Log.v(TAG, "User updated");
                if (type == 0)
                    posterImage.setImageResource(R.drawable.ic_poster_icon);
                else
                    presentationImage.setImageResource(R.drawable.ic_thumb_up_grey);

            }

            @Override
            public void failure(String message) {

            }
        });
        if (!user.hasVoted(type)) {
            DataService.sharedInstance().submitGeneralVote(id, type, new Callback<Vote>() {
                @Override
                public void success(Vote data) {
                    Log.v(TAG, "Voting completed");
                    onBackPressed();
                }

                @Override
                public void failure(String message) {
                }
            });
        } else {

            Log.v(TAG, "User already Voted");
            String voteType = "";
            switch (type) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
