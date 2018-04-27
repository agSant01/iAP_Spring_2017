package com.affiliates.iap.iapspring2017.Authentication.IntroScreens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.affiliates.iap.iapspring2017.Authentication.CreateAccount.EnterEmail;
import com.affiliates.iap.iapspring2017.Authentication.ForgotPasswordActivity;
import com.affiliates.iap.iapspring2017.Authentication.SignInActivity;
import com.affiliates.iap.iapspring2017.Interfaces.Callback;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.Services.AccountAdministration;
import com.affiliates.iap.iapspring2017.Services.Client;
import com.affiliates.iap.iapspring2017.Services.ConstantsService;
import com.affiliates.iap.iapspring2017.Tabs.TabController.HomeActivity;

public class SignInFragment extends Fragment{
    private static final String TAG = "SignInFragment";

    private EditText mPasswordField, mEmailField;
    private TextView mForgotPass, mRegister;
    private AccountAdministration mAdmin;
    private ImageView mShowPass;
    private Client mClient;
    private Button mSubmit;
    private View mRootView;

    public static SignInFragment newInstance(){
        return new SignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        mRootView = inflater.inflate(R.layout.activity_sign_in, container, false);
        mClient = new Client(getContext());
        mAdmin = new AccountAdministration(getContext());
        bind();
        mRegister.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
           /// Intent intent = new Intent(getActivity(), AccountType.class);
            Intent intent = new Intent(getActivity(), EnterEmail.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
         }
      });

      mShowPass.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if(mPasswordField.getTransformationMethod() != null) {
               mPasswordField.setTransformationMethod(null);
               mShowPass.setImageResource(R.drawable.ic_hide_pass);
            }else{
               mPasswordField.setTransformationMethod(new PasswordTransformationMethod());
               mShowPass.setImageResource(R.drawable.ic_show_password);
            }
         }
      });

      mForgotPass.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
         }
      });

      mSubmit.setOnClickListener(
            new View.OnClickListener()  {
               @Override
               public void onClick(View view) {
                  String email = mEmailField.getText().toString();
                  String password = mPasswordField.getText().toString();
                  if(!mClient.isConnectionAvailable()) {
                      new AlertDialog.Builder(getActivity())
                              .setTitle("Network error")
                              .setMessage("Please connect to network befoore attempting to sign in.")
                              .setPositiveButton("Dismiss", null).create().show();
                      return;
                  }
                  if (email.length() > 0 && password.length() > 0 ){
                     System.out.println( "DATA: " + email +"   " + password);
                      ((SignInActivity) getActivity()).showProgressDialog();

                     User.login(email, password, new Callback<User>(){
                        @Override
                        public void success(User user) {
                           ConstantsService.setCurrentLogedInUser(user);
                           mAdmin.saveUserID(user.getUserID());
                           System.out.println("DATA -> " + ConstantsService.getCurrentLoggedInUser().getName());
                           Intent in = new Intent(getActivity(), HomeActivity.class);

                            ((SignInActivity) getActivity()).hideProgressDialog();
                            startActivity(in);
                            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            getActivity().finish();
                            getActivity().finishAffinity();
                        }

                        @Override
                        public void failure(String message) {
                           if(message.contains("password is invalid")){
                               new AlertDialog.Builder(getActivity())
                                       .setTitle("Wrong Password")
                                       .setMessage("Please enter the correct password.")
                                       .setPositiveButton("Dismiss", null).create().show();
                           } else if (message.contains("There is no user record corresponding to this identifier.")){
                               new AlertDialog.Builder(getActivity())
                                       .setTitle("User Not Found")
                                       .setMessage("There is no user record corresponding to this email. Please register.")
                                       .setPositiveButton("Dismiss", null).create().show();
                           } else if (message.contains("badly formatted")){
                               Log.e(TAG, message);
                               new AlertDialog.Builder(getActivity())
                                       .setTitle("Invalid Email")
                                       .setMessage("Please enter a valid email.")
                                       .setPositiveButton("Dismiss", null).create().show();
                           } else if (message.contains("Network Error")){
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Network error")
                                        .setMessage("Please connect to network befoore attempting to sign in.")
                                        .setPositiveButton("Dismiss", null).create().show();
                            } else{
                               new AlertDialog.Builder(getActivity())
                                       .setTitle("Unspecified Error")
                                       .setMessage(message)
                                       .setPositiveButton("Dismiss", null).create().show();
                              Log.v(TAG, message);
                           }
                           ((SignInActivity)getActivity()).hideProgressDialog();
                           Log.e(TAG, message);
                        }
                     });
                  }else if (email.length() == 0 && password.length() == 0) {
                     Toast.makeText(getContext(), "Please, enter credentials", Toast.LENGTH_SHORT).show();
                  }
                  else if (email.length() == 0){
                     Toast.makeText(getContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                  } else if(password.length() == 0){
                     Toast.makeText(getContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
                  }
               }
            });

        return mRootView;
    }

    private void bind(){
        mPasswordField = (EditText) mRootView.findViewById(R.id.password_box);
        mEmailField = (EditText) mRootView.findViewById(R.id.email_box);
        mSubmit = (Button) mRootView.findViewById(R.id.sign_in_button);
        mShowPass = (ImageView) mRootView.findViewById(R.id.show_pass);
        mForgotPass = (TextView) mRootView.findViewById(R.id.forgot_password);
        mRegister = (TextView) mRootView.findViewById(R.id.register);
    }
}
