package com.affiliates.iap.iapspring2017.EvaluationCenter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.Models.Vote;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.Client;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Services.DataService;
import com.google.firebase.auth.FirebaseAuth;

public class GeneralVoteActivity extends BaseActivity {
    private String name, id;
    private TextView title;
    private Button bestPoster, bestPresentation;
    private ImageView posterImage, presentationImage;
    private AlertDialog submission;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_vote);
        bind();
        setToolBar();
    }

    private void bind() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        name = getIntent().getStringExtra("posterName");
        id = getIntent().getStringExtra("posterID");
        title = (TextView) findViewById(R.id.poster_desc_title);
        title.setText(name);
        bestPoster = (Button) findViewById(R.id.button_poster);
        posterImage = (ImageView) findViewById(R.id.poster_image);
        if (ConstantsService.getCurrentLoggedInUser().hasVoted(0)) {
            bestPoster.setBackgroundResource(R.drawable.button_oval_shape_grey);
            bestPoster.setText("Voted");
            posterImage.setImageResource(R.drawable.ic_poster_icon);
        }

        bestPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteProcess(0);
                verbose("Voted for best Poster");
            }
        });

        presentationImage = (ImageView) findViewById(R.id.presentation_image);
        bestPresentation = (Button) findViewById(R.id.button_presentation);
        if (ConstantsService.getCurrentLoggedInUser().hasVoted(1)) {
            bestPresentation.setBackgroundResource(R.drawable.button_oval_shape_grey);
            bestPresentation.setText("Voted");
            presentationImage.setImageResource(R.drawable.ic_oral_presentation_grey);
        }
        bestPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteProcess(1);
                verbose("Voted for best Presentation");
            }
        });

    }

    private void displaySubmissionDialog(final int type) {
        final AlertDialog.Builder dialogBuilder = createAlertDialog(this);
        dialogBuilder
                .setTitle("Confirm Submission")
                .setMessage("You can only vote for this category once. " +
                "Are you sure this is your favorite?")
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", null);
        submission = dialogBuilder.create();
        submission.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button confirm = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog("Submitting Vote");
                        vote(type);
                        dialog.dismiss();
                    }
                });

            }
        });
        submission.show();
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
                if (ConstantsService.getCurrentLoggedInUser().hasVoted(type))
                    showShortToast("Already voted for this category");
                else
                    displaySubmissionDialog(type);
            else
                showDismissAlertDialog(
                        GeneralVoteActivity.this,
                        "Network Error",
                        "Please verify your network connection"
                );
        } else {
            showShortToast("Sorry, you need to verify your email first.");
        }
    }

    private void vote(final int type) {
        User user = ConstantsService.getCurrentLoggedInUser();
        DataService.sharedInstance().getUserData(user.getUserID(), new Callback<User>() {
            @Override
            public void success(User data) {
                ConstantsService.setCurrentLogedInUser(data);
            }

            @Override
            public void failure(String message) {}
        });
        if (!user.hasVoted(type)) {
            DataService.sharedInstance().submitGeneralVote(id, type, new Callback<Vote>() {
                @Override
                public void success(Vote data) {
                    verbose("Voting completed");
                    hideProgressDialog();
                    new AlertDialog.Builder(GeneralVoteActivity.this)
                            .setTitle("Submission successful")
                            .setCancelable(false)
                            .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            })
                            .show();
                }

                @Override
                public void failure(String message) {}
            });
        } else {
            verbose("User already Voted");
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
            showLongToast("You've already voted for Best " + voteType);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.go_back_out, R.anim.go_back_in);
        finish();
    }
}
