//
//  MainActivity.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/14/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.BaseActivity;
import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.Models.Advisor;
import com.affiliates.iap.iapspring2017.Models.CompanyUser;
import com.affiliates.iap.iapspring2017.Models.IAPStudent;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.Models.User.AccountType;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends BaseActivity {
    private static final String TAG = "ProfileEditActivity";
    private static final int READ_REQUEST_CODE_IMAGE = 42;
    private static final int READ_REQUEST_CODE_PDF = 40;

    private CircleImageView mCircleImageView;
    private ImageView mChangeProfilePhoto;
    private Button mResetPassword;
    private Button mUploadResume;
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
    private Uri mImage;
    private Uri mPDF;

    private ArrayList<String> deptms;
    private HashMap<String, Integer> months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccType = Constants.getCurrentLoggedInUser().getAccountType();
        Log.v(TAG, mAccType + " " + AccountType.Advisor);
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
        if(!Constants.getCurrentLoggedInUser().getPhotoURL().equals(""))
            path = Constants.getCurrentLoggedInUser().getPhotoURL();
        Picasso.with(getBaseContext()).load(path)
                .error(R.drawable.ic_gender_0).placeholder(R.drawable.ic_gender_0)
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

        mName.setTextKeepState(!Constants.getCurrentLoggedInUser().getName().contains("NA") ? Constants.getCurrentLoggedInUser().getName() : "");
        mName.clearFocus();
        mEmail.setText(Constants.getCurrentLoggedInUser().getEmail());
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
                new AlertDialog.Builder(ProfileEditActivity.this).setMessage("Save Changes")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveChanges();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).create().show();
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
        final IAPStudent student = (IAPStudent) Constants.getCurrentLoggedInUser();
        if(!student.getDepartment().equals("NA")){
            deptms.remove(student.getDepartment());
            deptms.add(0, student.getDepartment());
            mDepartment.setItems(deptms);
            mDepartment.setText(student.getDepartment());
        } else {
            mDepartment.setItems(deptms);
        }
        mGradDate.setText(!student.getGradDate().equals("NA") ? student.getGradDate() : "Jun 20, 2017");
        mObjective.setText(!student.getObjective().equals("NA") ? student.getObjective() : "");
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

        mGradDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.v(TAG, "JHV");
                if(hasFocus){
                    String[] grad = student.getGradDate().split("[, ]+");
                    Calendar calendar = Calendar.getInstance();
                    if(grad.length > 1)
                        calendar.set(Integer.parseInt(grad[2]), months.get(grad[0]), Integer.parseInt(grad[1]));

                    DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
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
                    mGradDate.clearFocus();
                }
            }
        });
    }

    private void setAvisor(){
        final Advisor advisor = (Advisor) Constants.getCurrentLoggedInUser();
        mObjective.setHint("Research Intent");
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
        CompanyUser c = (CompanyUser) Constants.getCurrentLoggedInUser();
        mCompany.setText(c.getCompanyName());
    }

    private void showResetPasswordDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
        final EditText oldPass = (EditText) dialogView.findViewById(R.id.oldPass);
        final EditText newPass = (EditText) dialogView.findViewById(R.id.password);
        final EditText confirmPass = (EditText) dialogView.findViewById(R.id.confirm_password);
        ImageView oldPassShow = (ImageView) dialogView.findViewById(R.id.show_old_pass);
        ImageView passShow = (ImageView) dialogView.findViewById(R.id.show_pass);
        ImageView confPassShow = (ImageView) dialogView.findViewById(R.id.show_confirm_pass);

        oldPassShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPass.getTransformationMethod() != null)
                    oldPass.setTransformationMethod(null);
                else
                    oldPass.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        passShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPass.getTransformationMethod() != null)
                    newPass.setTransformationMethod(null);
                else
                    newPass.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        confPassShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmPass.getTransformationMethod() != null)
                    confirmPass.setTransformationMethod(null);
                else
                    confirmPass.setTransformationMethod(new PasswordTransformationMethod());
            }
        });



        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Reset Password").setPositiveButton("Confirm", null)
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
                                        Toast.makeText(getBaseContext(), data, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void failure(String message) {
                                        hideProgressDialog();
                                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void failure(String message) {
                                Toast.makeText(getBaseContext(), "Sorry, that is not your current password", Toast.LENGTH_SHORT).show();
                                Log.v(TAG, message);
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
        showProgressDialog("Uploading Changes");
        final User user = Constants.getCurrentLoggedInUser();
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
            //b is the Bitmap
            Bitmap b = null;
            ByteBuffer buffer = null;
            try {
                b = getBitmapFromUri(mImage);
                buffer = ByteBuffer.allocate(b.getByteCount());
            } catch (IOException e) {
                e.printStackTrace();
            }
            b.copyPixelsToBuffer(buffer); //Move the byte curentRegisteringUserData to the buffer
            byte[] array = buffer.array(); //Get the underlying array containing the curentRegisteringUserData.
            DataService.sharedInstance().uploadUserImage(user, mImage, new Callback<User>() {
                @Override
                public void success(User data) {
                    mImage = null;
                    Log.v(TAG, "uploadUserImage success");
                    DataService.sharedInstance().updateUserData(user, mPDF, new Callback<User>() {
                        @Override
                        public void success(User data) {
                            Log.v(TAG, "updateUserData success");
                            hideProgressDialog();
                            Constants.setCurrentLogedInUser(user);
                            new AlertDialog.Builder(ProfileEditActivity.this)
                                    .setTitle("Successfully Updated Your Profile")
                                    .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).create().show();
                        }

                        @Override
                        public void failure(String message) {
                            hideProgressDialog();
                            String title = "Internal Error";
                            message = "An unspecified internal error ocurred. Your changes have not been saved. Please try again.";
                            new AlertDialog.Builder(getBaseContext()).setTitle(title).setMessage(message)
                                   .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveChanges();
                                    }
                                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                }).create().show();
                        }
                    });
                }

                @Override
                public void failure(String message) {
                    hideProgressDialog();
                    updatePhotoFailed(message);
                }
            });
        } else {
            DataService.sharedInstance().updateUserData(user, mPDF, new Callback<User>() {
                @Override
                public void success(User data) {
                    Log.v(TAG, "updateUserData success");
                    hideProgressDialog();
                    Constants.setCurrentLogedInUser(user);
                    new AlertDialog.Builder(ProfileEditActivity.this)
                                    .setMessage("Successfully Updated Your Profile")
                                    .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).create().show();
                }

                @Override
                public void failure(String message) {
                    hideProgressDialog();
                    String title = "Internal Error";
                    message = "An unspecified internal error ocurred. Your changes have not been saved. Please try again.";
                    new AlertDialog.Builder(getBaseContext()).setTitle(title).setMessage(message)
                            .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveChanges();
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    }).create().show();
                }
            });
        }
    }

    private boolean validatePassword(String newPass, String confPass){
        if (newPass.isEmpty()) {
            Toast.makeText(getBaseContext(),"Please, enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confPass.isEmpty()) {
            Toast.makeText(getBaseContext(),"Please, confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPass.equals(confPass)) {
            Toast.makeText(getBaseContext(),"Sorry, passwords do not match", Toast.LENGTH_SHORT).show();
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
            message = "An unspecified internal error ocurred. Your changes have not been saved. Please try again.";
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(getBaseContext()).setTitle(title).setMessage(message);
        if(t){
            alert.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveChanges();
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }).create().show();
        } else {
            alert.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveChanges();
                }
            });
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return scaleDown(image, 2048);
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
                Log.i(TAG, "Uri: " + resultData.getData());
                try {
                    mCircleImageView.setImageBitmap(getBitmapFromUri(mImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        new AlertDialog.Builder(ProfileEditActivity.this).setTitle("Unsaved Changes")
                .setMessage("If you go back unsaved changes will be lost.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ProfileEditActivity.super.onBackPressed();
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }).create().show();
    }
}
