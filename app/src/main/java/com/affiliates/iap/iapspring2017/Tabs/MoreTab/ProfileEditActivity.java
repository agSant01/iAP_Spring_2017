//
//  HomeActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/14/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Tabs.MoreTab;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.Extensions.BaseActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.Models.User.AccountType;
import com.affiliates.iap.iapspring2017.NoConnectionActivity;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.Client;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Services.DataService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends BaseActivity {
    private static final String TAG = "ProfileEditActivity";
    private static final int READ_REQUEST_CODE_IMAGE = 42;
    private static final int READ_REQUEST_CODE_PDF = 40;

    private Client c;
    private CircleImageView mCircleImageView;
    private ImageView mChangeProfilePhoto;
    private Button mResetPassword, mUploadResume;
    private MaterialSpinner mDepartment;
    private AccountType mAccType;
    private Button mSaveChanges;
    /**
     *  If user is an advisor it is used for the research intent
     */
    private EditText mObjective;
    /**
     *  If user is an advisor it is used for the webpage
     */
    private EditText mGradDate;
    private AlertDialog mResetPasswordDialog;
    /**
     *  Only used if user is a company rep.
     */
    private TextView mCompany;
    private Toolbar mToolbar;
    private TextView mEmail;
    private EditText mName;
    private Uri mImage, mPDF;

    private ArrayList<String> deptms;
    private HashMap<String, Integer> months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = new Client(getBaseContext());
        mAccType = ConstantsService.getCurrentLoggedInUser().getAccountType();
        verbose(mAccType + " " + AccountType.Advisor);
        if(mAccType == AccountType.IAPStudent)
            setContentView(R.layout.activity_iapstudent_profile_edit);
        else if (mAccType == AccountType.Advisor)
            setContentView(R.layout.activity_advisor_profile_edit);
        else if (mAccType == AccountType.CompanyUser)
            setContentView(R.layout.activity_company_profile_edit);
        else
            setContentView(R.layout.activity_guest_profile_edit);
        this.bind();
        setToolbar();
        String path = "NA";
        if(!ConstantsService.getCurrentLoggedInUser().getPhotoURL().equals(""))
            path = ConstantsService.getCurrentLoggedInUser().getPhotoURL();
        Picasso.with(getBaseContext())
                .load(path)
                .error(R.drawable.ic_gender_0)
                .placeholder(R.drawable.ic_gender_0)
                .into(mCircleImageView);
        deptms = new ArrayList<String>(){{
            add("Computer Engineering");
            add("Electrical Engineering");
            add("Software Engineering");
            add("Computer Science");
            add("Industrial Engineering");
            add("Civil Engineering");
            add("Chemical Engineering");
            add("BS Surveying and Topology");
            add("Mechanical Engineering");
        }};

        mName.setTextKeepState(!ConstantsService.getCurrentLoggedInUser().getName().contains("NA") ? ConstantsService.getCurrentLoggedInUser().getName() : "");
        mEmail.setTextKeepState(ConstantsService.getCurrentLoggedInUser().getEmail());
        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasswordDialog();
            }
        });
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performImageSearch();
            }
        });

        if( mAccType == AccountType.IAPStudent){
            setIAPStudent();
            mUploadResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performFileSearch();
                }
            });
        } else if( mAccType == AccountType.Advisor){
            setAvisor();
        } else if (mAccType == AccountType.CompanyUser){
            setCompany();
        }

        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog(ProfileEditActivity.this)
                        .setTitle("Save Changes")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveChanges();
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .show();
            }
        });
    }

    private void bind() {
        mCircleImageView = (CircleImageView) findViewById(R.id.profile_image_activity);
        mChangeProfilePhoto = (ImageView) findViewById(R.id.change_pic);
        mUploadResume = (Button) findViewById(R.id.profile_iap_resume);
        mResetPassword = (Button) findViewById(R.id.reset_password);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_poster_desc);
        mDepartment = (MaterialSpinner) findViewById(R.id._department);
        mSaveChanges = (Button) findViewById(R.id.save_changes);
        mObjective = (EditText) findViewById(R.id._objective);
        mCompany = (TextView) findViewById(R.id.company_name);
        mGradDate = (EditText) findViewById(R.id.grad_date);
        mEmail = (TextView) findViewById(R.id.editText4);
        mName = (EditText) findViewById(R.id._name);
    }

    private void setToolbar() {
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

    private void setIAPStudent(){
        mGradDate.setFocusable(false);
        final IAPStudent student = (IAPStudent) ConstantsService.getCurrentLoggedInUser();
        if(!student.getDepartment().equals("NA")){
            deptms.remove(student.getDepartment());
            deptms.add(0, student.getDepartment());
            mDepartment.setItems(deptms);
            mDepartment.setTextKeepState(student.getDepartment());
        } else {
            mDepartment.setItems(deptms);
        }
        mGradDate.setTextKeepState(!student.getGradDate().equals("NA") ? student.getGradDate() : "Jun 20, 2017");
        mObjective.setTextKeepState(!student.getObjective().equals("NA") ? student.getObjective() : "");
        months = new HashMap<String, Integer>(){{
            put("Jan", 0);
            put("Feb", 1);
            put("Mar", 2);
            put("April", 3);
            put("May", 4);
            put("Jun", 5);
            put("Jul", 6);
            put("Aug", 7);
            put("Sept", 8);
            put("Oct", 9);
            put("Nov", 10);
            put("Dec", 11);
        }};

        mGradDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] grad = student.getGradDate().split("[, ]+");
                Calendar calendar = Calendar.getInstance();
                if(grad.length > 1)
                    calendar.set(Integer.parseInt(grad[2]), months.get(grad[0]), Integer.parseInt(grad[1]));

                DatePickerDialog datePickerDialog =
                        DatePickerDialog.newInstance(
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                        for(String s : months.keySet()){
                                            if (months.get(s) == monthOfYear){
                                                mGradDate.setText(s + " " + dayOfMonth + ", " + year);
                                                break;
                                            }
                                        }
                                    }
                                },calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_1);
                datePickerDialog.show(getFragmentManager(), "");
            }
        });
    }

    private void setAvisor(){
        final Advisor advisor = (Advisor) ConstantsService.getCurrentLoggedInUser();
        mObjective.setHint("Research Interest");
        if(!advisor.getDepartment().equals("NA")){
            deptms.remove(advisor.getDepartment());
            deptms.add(0, advisor.getDepartment());
            mDepartment.setItems(deptms);
            mDepartment.setText(advisor.getDepartment());
        } else {
            mDepartment.setItems(deptms);
        }
        mGradDate.setText(!advisor.getWebPage().contains("NA") ? advisor.getWebPage() : "");              // gradDate used for the web page
        mObjective.setText(!advisor.getResearchIntent().contains("NA") ? advisor.getResearchIntent() : "");      // mObjective used for the research intent
        mDepartment.setItems(deptms);
    }

    private void setCompany(){
        CompanyUser c = (CompanyUser) ConstantsService.getCurrentLoggedInUser();
        mCompany.setText(c.getCompanyName());
    }

    private void showResetPasswordDialog() {
        final AlertDialog.Builder dialogBuilder = createAlertDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
        final EditText oldPass = (EditText) dialogView.findViewById(R.id.oldPass);
        final EditText newPass = (EditText) dialogView.findViewById(R.id.password);
        final EditText confirmPass = (EditText) dialogView.findViewById(R.id.confirm_password);
        final ImageView oldPassShow = (ImageView) dialogView.findViewById(R.id.show_old_pass);
        final ImageView passShow = (ImageView) dialogView.findViewById(R.id.show_pass);
        final ImageView confPassShow = (ImageView) dialogView.findViewById(R.id.show_confirm_pass);

        oldPassShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPass.getTransformationMethod() != null) {
                    oldPass.setTransformationMethod(null);
                    oldPassShow.setImageResource(R.drawable.ic_hide_pass);
                } else{
                    oldPass.setTransformationMethod(new PasswordTransformationMethod());
                    oldPassShow.setImageResource(R.drawable.ic_show_password);
                }
            }
        });

        passShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPass.getTransformationMethod() != null) {
                    newPass.setTransformationMethod(null);
                    passShow.setImageResource(R.drawable.ic_hide_pass);
                }else {
                    newPass.setTransformationMethod(new PasswordTransformationMethod());
                    passShow.setImageResource(R.drawable.ic_show_password);
                }
            }
        });

        confPassShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmPass.getTransformationMethod() != null){
                    confirmPass.setTransformationMethod(null);
                    confPassShow.setImageResource(R.drawable.ic_hide_pass);
                }else{
                    confirmPass.setTransformationMethod(new PasswordTransformationMethod());
                    confPassShow.setImageResource(R.drawable.ic_show_password);
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder
                .setTitle("Reset Password")
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", null);

        mResetPasswordDialog = dialogBuilder.create();
        mResetPasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!validatePassword(newPass.getText().toString(), confirmPass.getText().toString())){
                            return;
                        }
                        showProgressDialog("Updating password");
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                pass = oldPass.getText().toString();
                        User.login(email, pass, new Callback<User>() {
                            @Override
                            public void success(User data) {
                                resetPassword(newPass.getText().toString(), confirmPass.getText().toString(), new Callback<String>() {
                                    @Override
                                    public void success(String data) {
                                        hideProgressDialog();
                                        showShortToast(data);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void failure(String message) {
                                        hideProgressDialog();
                                        showShortToast(message);
                                    }
                                });
                            }

                            @Override
                            public void failure(String message) {
                                showShortToast("Sorry, that is not your current password");
                                verbose(message);
                                oldPass.selectAll();
                                oldPass.setSelected(true);
                                hideProgressDialog();
                            }
                        });
                    }
                });
            }
        });
        mResetPasswordDialog.show();
    }

    private void saveChanges(){
        if (!c.isConnectionAvailable()){
            verbose("I");
            startActivity(NoConnectionActivity.class);
            return;
        }
        showProgressDialog("Uploading Changes");
        final User user = ConstantsService.getCurrentLoggedInUser();
        user.setName(mName.getText().toString().isEmpty() ? "NA" : mName.getText().toString());

        if(mAccType == AccountType.IAPStudent){
            IAPStudent s = (IAPStudent) user;
            s.setDepartment(mDepartment.getText().toString());
            s.setGradDate(mGradDate.getText().toString());
            s.setObjective(mObjective.getText().toString());
        } else if (mAccType == AccountType.Advisor){
            Advisor advisor = (Advisor) user;
            advisor.setDepartment(mDepartment.getText().toString().isEmpty() ? "Engineering" : mDepartment.getText().toString());
            advisor.setWebPage(mGradDate.getText().toString().isEmpty() ? "https://uprm.edu" : mGradDate.getText().toString());
            advisor.setResearchIntent(mObjective.getText().toString().isEmpty() ? "To be Defined" : mObjective.getText().toString());
        }

        if(mImage != null){
            Glide.with(this)
                    .load(mImage)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            resource = scaleDown(resource, 256);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.PNG, 10, outputStream);
                            DataService.sharedInstance().uploadUserImage(user, outputStream.toByteArray(), new Callback<User>() {
                                @Override
                                public void success(User data) {
                                    mImage = null;
                                    verbose("uploadUserImage success");
                                    DataService.sharedInstance().updateUserData(user, mPDF, new Callback<User>() {
                                        @Override
                                        public void success(User data) {
                                            verbose("updateUserData success");
                                            hideProgressDialog();
                                            FirebaseAuth.getInstance().getCurrentUser().reload();
                                            ConstantsService.setCurrentLogedInUser(user);
                                            createAlertDialog(ProfileEditActivity.this)
                                                    .setTitle("Successfully Updated Your Profile")
                                                    .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    })
                                                    .setCancelable(false)
                                                    .create()
                                                    .show();
                                        }

                                        @Override
                                        public void failure(String message) {
                                            hideProgressDialog();
                                            String title = "Internal Error";
                                            message = "An unspecified internal error ocurred. Your changes have not been saved. Please try again.";
                                            createAlertDialog(ProfileEditActivity.this)
                                                    .setTitle(title)
                                                    .setMessage(message)
                                                    .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            saveChanges();
                                                        }
                                                    })
                                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {}
                                                    })
                                                    .show();
                                        }
                                    });
                                }

                                @Override
                                public void failure(String message) {
                                    hideProgressDialog();
                                    updatePhotoFailed(message);
                                }
                            });
                        }
                    });
        } else {
            DataService.sharedInstance().updateUserData(user, mPDF, new Callback<User>() {
                @Override
                public void success(User data) {
                    verbose("updateUserData success");
                    hideProgressDialog();
                    FirebaseAuth.getInstance().getCurrentUser().reload();
                    ConstantsService.setCurrentLogedInUser(user);
                    createAlertDialog(ProfileEditActivity.this)
                            .setTitle("Successfully Updated Your Profile")
                            .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }

                @Override
                public void failure(String message) {
                    hideProgressDialog();
                    String title = "Internal Error";
                    message = "An unspecified internal error ocurred. Your changes have not been saved. Please try again.";
                    createAlertDialog(ProfileEditActivity.this)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveChanges();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            })
                            .show();
                }
            });
        }
    }

    private boolean validatePassword(String newPass, String confPass){
        if (newPass.isEmpty()) {
            showShortToast("Please, enter password");
            return false;
        }
        if (confPass.isEmpty()) {
            showShortToast("Please, confirm password");
            return false;
        }
        if (!newPass.equals(confPass)) {
            showShortToast("Sorry, passwords do not match");
            return false;
        }
        return true;
    }

    private void resetPassword(String newPass, String confPass, final Callback callback) {
        if (newPass.isEmpty()) {
            callback.failure("Please, enter password");
            return;
        }
        if (confPass.isEmpty()) {
            callback.failure("Please, confirm password");
            return;
        }
        if (!newPass.equals(confPass)) {
            callback.failure("Sorry, passwords do not match");
            return;
        }

        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    String msg = task.getException().getMessage();
                    if (msg.contains("WEAK_PASSWORD")) {
                        callback.failure("New password too short");
                    }else {
                        callback.failure(msg);
                    }
                    return;
                }
                callback.success("Password updated successfully");
            }
        });
    }

    public void performImageSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME curentRegisteringUserData type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE_IMAGE);
    }

    public void performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME curentRegisteringUserData type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("application/pdf");

        startActivityForResult(intent, READ_REQUEST_CODE_PDF);
    }

    private void updatePhotoFailed(String message){
        String title = "";
        boolean t = true;
        if(message.contains("User is unauthenticated")){
            title = "Authentication Error";
            message = "This operation requires you to be authenticated recently. Please Sign out and Sign in to upload " +
                    "you profile picture. Your changes have not been saved. Please try again.";
        } else if (message.contains("User canceled the operation.")){
            title = "Upload Canceled";
            message = "Profile picture upload was canceled. Your changes have not been saved. Please try again.";
        } else if(message.contains("User is not authorized to perform the desired")){
            title = "Authorization Error";
            message = "You are not authorized to perform this action. Your changes have not been saved.";
            t = false;
        } else {
            title = "Internal Error";
            message = "An unspecified internal error occurred. Your changes have not been saved. Please try again.";
        }

        AlertDialog.Builder alert = createDismissAlertDialog(
                ProfileEditActivity.this,
                title,
                message);
        if(t){
            alert.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveChanges();
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }).show();
        } else {
            alert.setNeutralButton("DISMISS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveChanges();
                }
            });
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());
        return Bitmap.createScaledBitmap(realImage, width, height, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                mImage = resultData.getData();
                verbose("Uri: " + resultData.getData());
                Glide.with(getBaseContext()).load(mImage).sizeMultiplier((float) 1).centerCrop().into(mCircleImageView);
            }
        } else if (requestCode == READ_REQUEST_CODE_PDF && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                mPDF = resultData.getData();
                mUploadResume.setText("Resume To Be Updated");
                mUploadResume.setBackground(getResources().getDrawable(R.drawable.button_oval_shape_white));
                mUploadResume.setTextColor(getResources().getColor(R.color.appGreen));
            }
        }
    }

    @Override
    public void onBackPressed() {
        createAlertDialog(ProfileEditActivity.this)
                .setTitle("Unsaved Changes")
                .setMessage("If you go back unsaved changes will be lost.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProfileEditActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }
}
