package com.centrica.maraudersmap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by karthi on 04-09-2016.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passEditText;
    private int progressStatus = 0;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Address the email and password field
        handler = new Handler();
        emailEditText = (EditText) findViewById(R.id.username);
        passEditText = (EditText) findViewById(R.id.password);
    }

    public void checkLogin(View arg0) {

        final String email = emailEditText.getText().toString();
        if (!isValidEmail(email)) {
            //Set error message for email field
            emailEditText.setError("Invalid Email address");
        }

        final String pass = passEditText.getText().toString();
        if (!isValidPassword(pass)) {
            //Set error message for password field
            passEditText.setError("Invalid Password");
        }

        if (isValidEmail(email) && isValidPassword(pass)) {

            final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setTitle("Please wait.");
            pd.setMessage("Loading...");
            pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
            pd.setIndeterminate(false);
            pd.show();
            progressStatus = 0;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(progressStatus < 100){
                        // Update the progress status
                        progressStatus +=1;
                        // Try to sleep the thread for 20 milliseconds
                        try{
                            Thread.sleep(20);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        // Update the progress bar
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Update the progress status
                                pd.setProgress(progressStatus);
                                // If task execution completed
                                if(progressStatus == 100){
                                    // Dismiss/hide the progress dialog
                                    pd.dismiss();
                                    Intent mainMenu = new Intent(LoginActivity.this,MapsActivity.class);
                                    startActivity(mainMenu);
                                    finish();
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }

    // validating email id
    private boolean isValidEmail(String email) {

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        if (email != null ) {

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
        return false;
    }

    // validating password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 4) {
            return true;
        }
        return false;
    }
}
