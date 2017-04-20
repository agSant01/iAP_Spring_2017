package com.affiliates.iap.iapspring2017.sing_in.intro_screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

import com.affiliates.iap.iapspring2017.Constants;
import com.affiliates.iap.iapspring2017.MainActivity;
import com.affiliates.iap.iapspring2017.Models.User;
import com.affiliates.iap.iapspring2017.R;
import com.affiliates.iap.iapspring2017.activities.ForgotPasswordActivity;
import com.affiliates.iap.iapspring2017.interfaces.Callback;
import com.affiliates.iap.iapspring2017.services.AccountAdministration;
import com.affiliates.iap.iapspring2017.services.Client;
import com.affiliates.iap.iapspring2017.sing_in.AccountType;
import com.affiliates.iap.iapspring2017.sing_in.SignInActivity;

public class SignInFragment extends Fragment{
    private static final String TAG = "SignIn";

    private static final int REQUEST_EXIT = 5;

    private AccountAdministration mAdmin;
    private EditText mPasswordField;
    private EditText mEmailField;
    private Client mClient;
    private Button mSubmit;
    private TextView mForgotPaass;
    private ImageView mShowPass;
    private TextView mRegister;
    private IntroScreensManager mSectionsPagerAdapter;
    private TabLayout mTabLayout;
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
            Intent intent = new Intent(getActivity(), AccountType.class);
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

      mForgotPaass.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivityForResult(new Intent(getActivity(), ForgotPasswordActivity.class), REQUEST_EXIT);
            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
         }
      });

      mSubmit.setOnClickListener(
            new View.OnClickListener()  {
               @Override
               public void onClick(View view) {
                  String email = mEmailField.getText().toString();
                  String password = mPasswordField.getText().toString();
                  if(!mClient.isConnectionAvailable()){
                     try {
                        Thread.sleep(100);
                     } catch (InterruptedException e) {
                        e.printStackTrace();
                     }
                      ((SignInActivity) getActivity()).hideProgressDialog();
                     Toast.makeText(getContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                  }else if (email.length() > 0 && password.length() > 0 ){
                     System.out.println( "DATA: " + email +"   " + password);
                      ((SignInActivity) getActivity()).showProgressDialog();

                     User.login(email, password, new Callback<User>(){
                        @Override
                        public void success(User user) {
                           Constants.setCurrentLogedInUser(user);
                           mAdmin.saveUserID(user.getUserID());
                           System.out.println("DATA -> " + Constants.getCurrentLoggedInUser().getName());
                           Intent in = new Intent(getActivity(), MainActivity.class);

                            ((SignInActivity) getActivity()).hideProgressDialog();
                            startActivity(in);
                            getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            getActivity().finish();
                            getActivity().finishAffinity();
                        }

                        @Override
                        public void failure(String message) {
                           String s = "";
                           if(message.contains("password is invalid")){
                              s = "Incorrect Password";
                           } else if (message.contains("There is no user record corresponding to this identifier.")){
                              s = "Incorrect Email";
                           } else if (message.contains("badly formatted")){
                              s = "Invalid Email";
                           } else{
                              Log.v(TAG, message);
                           }
                            ((SignInActivity)getActivity()).hideProgressDialog();
                           Log.e(TAG, message);
                           Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
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
        mForgotPaass = (TextView) mRootView.findViewById(R.id.forgot_password);
        mRegister = (TextView) mRootView.findViewById(R.id.register);
    }
}
