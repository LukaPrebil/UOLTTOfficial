package com.uoltt.lukaprebilgrintal.uolttofficial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {

    public void switchActivity(){
        //start background service
        Intent mServiceIntent = new Intent(this, BackgroundOps.class);
        this.startService(mServiceIntent);

        Intent intent = new Intent(LoginActivity.this, ImageCanvas.class);
        startActivity(intent);
    }

    public void loginButton(View view){
        Object lock = new Object();
        EditText tokenField = (EditText) findViewById(R.id.tokenField);
        String token = tokenField.getText().toString();
        new CheckTokenValidity().execute(token);
        try{
            synchronized (lock){
                lock.wait(500);
            }

            if(UserData.tokenValidity){

                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Username", UserData.username);
                editor.putString("Squadron", UserData.squadronName);
                editor.putString("Token",    UserData.token);
                editor.apply();
                switchActivity();
            } else {
                String invalidToken = "No user with this token exists.";
                Snackbar snackbar = Snackbar.make(findViewById(R.id.login_layout),
                                                  invalidToken, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);

        if (prefs.contains("Token") && prefs.contains("Squadron") && prefs.contains("Token")){
            UserData.username = prefs.getString("Username", "USRNAMEError");
            UserData.squadronName = prefs.getString("Squadron", "SQDRNError");
            UserData.token = prefs.getString("Token", "TKNError");
            switchActivity();
        }
    }
}
