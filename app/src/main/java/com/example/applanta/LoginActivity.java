package com.example.applanta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;


import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 1;
    private String userId;
    private String userName;
    private User currentUser;
    private boolean userExists;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideNavBar();
        overridePendingTransition(R.anim.fade_in_splash, R.anim.fade_out_splash);
        setContentView(R.layout.activity_login);

        //setup Google Sign in options
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //build API client
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    public void onStart(){
        super.onStart();

        //in case there was already a log in
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }

    }


    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            userName = account.getDisplayName();
            userId = account.getId();
            volleyGetUser(userId);
        }else{
            Intent loginIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(loginIntent, RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(signInResult.isSuccess()){
                GoogleSignInAccount account = signInResult.getSignInAccount();
                userName = account.getDisplayName();
                userId = account.getId();
                //get from DB; if get returns "{}" then POST otherwise we good to go
                volleyGetUser(userId);
                if(userExists == false){
                    //call POST to create a new user and get new extras
                    volleyPostUser(userId, userName);
                }
            }else{
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void volleyGetUser(String userId){
        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.api_url) + "users/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("userExists") == true) {
                                currentUser = new User(response);
                                userExists = true;
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.putExtra("user", currentUser);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                                finish();
                            }else{
                                userExists = false;
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d( "volley","Oops: " + error);
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    public void volleyPostUser(String userId,String userName){
        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.api_url) + "users/";

        JSONObject postData = new JSONObject();
        try {
            postData.put("name", userName);
            postData.put("access_token", userId);
            postData.put("refresh_token", "4l8r");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentUser = new User(response);
                            Intent i2 = new Intent(getApplicationContext(), MainActivity.class);
                            i2.putExtra("user", currentUser);
                            startActivity(i2);
                            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                            finish();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d( "volley","Oops: " + error);
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        hideNavBar();
    }

    public void hideNavBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
