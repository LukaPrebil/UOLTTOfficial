package com.uoltt.lukaprebilgrintal.uolttofficial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {

    public void switchActivity(){
        //start background service
        Intent mServiceIntent = new Intent(this, BackgroundOps.class);
        this.startService(mServiceIntent);
        //take it forward
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

                SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Username", UserData.username);
                editor.putString("Squadron", UserData.squadName);
                editor.putInt("SquadronID",  UserData.squadronID);
                editor.putString("Token",    UserData.token);
                editor.putBoolean("LoggedIn", true);
                editor.apply();
                switchActivity();
            } else {
                String invalidToken = "No user with this token exists.";
                Snackbar snackbar = Snackbar.make(findViewById(R.id.login_layout),
                                                  invalidToken, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } catch (Exception e){
            System.err.println("error in loginactivity");
            System.err.println(e.getMessage());
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //Check if user is logged in and take him forward
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
        if (prefs.getBoolean("LoggedIn", /*defaults if not found to*/false)){
            UserData.username = prefs.getString("Username", "USRNAMEError");
            UserData.squadronName = prefs.getString("Squadron", "SQDRNError");
            UserData.squadronID = prefs.getInt("SquadronID", -1);
            UserData.token = prefs.getString("Token", "TKNError");
            switchActivity();
        }
        else {
            EditText tokenField = (EditText) findViewById(R.id.tokenField);
            tokenField.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        switch (keyCode)
                        {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                Object lock = new Object();
                                EditText tokenField = (EditText) findViewById(R.id.tokenField);
                                String token = tokenField.getText().toString();
                                new CheckTokenValidity().execute(token);
                                try{
                                    synchronized (lock){
                                        lock.wait(500);
                                    }

                                    if(UserData.tokenValidity){

                                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("Username", UserData.username);
                                        editor.putString("Squadron", UserData.squadName);
                                        editor.putInt("SquadronID",  UserData.squadronID);
                                        editor.putString("Token",    UserData.token);
                                        editor.putBoolean("LoggedIn", true);
                                        editor.apply();
                                        switchActivity();
                                    } else {
                                        String invalidToken = "No user with this token exists.";
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.login_layout),
                                                invalidToken, Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                } catch (Exception e){
                                    System.err.println("error in loginactivity");
                                    System.err.println(e.getMessage());
                                }
                                return true;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });
        }
    }
}
