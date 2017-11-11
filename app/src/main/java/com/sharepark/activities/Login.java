package com.sharepark.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sharepark.R;
import com.sharepark.util.AppController;
import com.sharepark.util.Encrypt;
import com.sharepark.util.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class Login extends AppCompatActivity {

    Dialog dialog;

    private static final int REQUEST_LOCATION = 0;


    private EditText mPasswordView, mEmailView;
    private String fb_email, fb_profile_pic, fb_lname, fb_fname;
    CallbackManager callbackManager;
    RelativeLayout load;
    String emailString;
    private ProgressDialog progressDialog;
    private String email;
    private String password;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private AccessToken tok;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login);
        facebookSDKInitialize();
        getKeyHash();

        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        assert loginButton != null;
        loginButton.setReadPermissions("email");
        getLoginDetails(loginButton);

        mEmailView = (AppCompatEditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        Button mEmailSignInButton = (Button) findViewById(R.id.login);
        assert mEmailSignInButton != null;
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);


        load = (RelativeLayout) findViewById(R.id.load);
        assert load != null;
        load.setVisibility(View.GONE);

        TextView register = (TextView) findViewById(R.id.register);
        assert register != null;
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));

            }
        });

        TextView forgot_password = (TextView) findViewById(R.id.forgot_password);
        assert forgot_password != null;
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));

            }
        });

        try {
            Boolean session = preferences.getBoolean("session", false);
            if (session) {
                startActivity(new Intent(getApplication(), MainActivity.class));
                finish();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            emailString = bundle.getString("RegEmail");
            mEmailView.setText(emailString);
            showEmailSent();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       // client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString());
    }

    /*
    Register a callback function with LoginButton to respond to the login result.
            */
    protected void getLoginDetails(LoginButton login_button) {

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {

                GraphRequest request = GraphRequest.newMeRequest(login_result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        try {
                            Bundle bFacebookData = getFacebookData(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // code for cancellation

            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
                try {
                    Log.v("LoginActivity", exception.getCause().toString());
                } catch (NullPointerException ignored) {

                }
            }
        });
    }

    private Bundle getFacebookData(JSONObject object) throws JSONException {

        Bundle bundle = new Bundle();

        String id = object.getString("id");

        fb_profile_pic = "https://graph.facebook.com/" + id + "/picture?type=large";
        Log.d("profile_pic", fb_profile_pic);
        bundle.putString("profile_pic", fb_profile_pic);

        bundle.putString("idFacebook", id);
        if (object.has("first_name"))
            bundle.putString("first_name", object.getString("first_name"));
        Log.d("first_name", object.getString("first_name"));
        if (object.has("last_name"))
            bundle.putString("last_name", object.getString("last_name"));
        Log.d("last_name", object.getString("last_name"));
        if (object.has("email")) {
            tok = AccessToken.getCurrentAccessToken();
            bundle.putString("email", object.getString("email"));
            Log.d("email", object.getString("email"));
            fb_email = object.getString("email");
            fb_fname = object.getString("first_name");
            fb_lname = object.getString("last_name");
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, "http://pickapark.com.ph/istip.php", null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject json) {
                            progressDialog.dismiss();
                            try {
                                editor.putString("webservice", json.getString("site") + "/parker/");
                                editor.putString("webservice_root", json.getString("site") + "/");
                                editor.commit();
                                Log.d("FB ID", tok.getUserId());
                                String p = Encrypt.protectString("tag=loginfacebook&fb_id=" + tok.getUserId() + "&email=" + fb_email + "&fname=" + fb_fname + "&lname" + fb_lname);
                                String tags = "login.php?p=" + p;
                                sendData(tags);

                                sendData(tags);

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Something went wrong. Try Again", Toast.LENGTH_SHORT).show();
                                Log.e("JSON Parser", "Error parsing data " + e.toString());
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Oops, something bad happened", Toast.LENGTH_SHORT).show();

                        }
                    });
            AppController.getInstance().addToRequestQueue((jsonRequest));

            progressDialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Can't Validate Email", Toast.LENGTH_SHORT).show();
        }
        if (object.has("gender"))
            bundle.putString("gender", object.getString("gender"));
        if (object.has("birthday"))
            bundle.putString("birthday", object.getString("birthday"));
        Log.d("birthday", object.getString("birthday"));
        if (object.has("location"))
            bundle.putString("location", object.getJSONObject("location").getString("name"));
        Log.d("location", object.getJSONObject("location").getString("name"));

        return bundle;


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(getApplication());
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(getApplication());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }


    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    private void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Keyhash:",
                        "" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please input Email", Toast.LENGTH_SHORT).show();
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            checkWebService();
            progressDialog.show();


        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    public void checkWebService() {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "http://pickapark.com.ph/istip.php", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        progressDialog.dismiss();

                        try {
                            editor.putString("webservice", json.getString("site") + "/parker/");
                            editor.putString("webservice_root", json.getString("site") + "/");
                            editor.commit();
                            String p = Encrypt.protectString("tag=login&email=" + email + "&password=" + password);
                            String tags = "login.php?p=" + p;
                            Log.e("link", tags);
                            sendData(tags);


                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Something went wrong. Try Again", Toast.LENGTH_SHORT).show();
                            Log.e("JSON Parser", "Error parsing data " + e.toString());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Oops, something bad happened", Toast.LENGTH_SHORT).show();

                    }
                });
        AppController.getInstance().addToRequestQueue((jsonRequest));
    }


    public void sendData(final String tags) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, preferences.getString("webservice", WebServiceHelper.web_tip) + tags, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        Log.d("login_link: ", preferences.getString("webservice", WebServiceHelper.web_tip) + tags);
                        progressDialog.dismiss();
                        final Toast t2 = Toast.makeText(getApplicationContext(), "Username or Password is wrong", Toast.LENGTH_SHORT);

                        String id, email, fname, lname, contact, birthday, result, sStatus, sCode, image, cars, logintype, fb_id, with_beta;
                        try {
                            result = json.getString("result");
                            if (result.equals("success")) {
                                load.setVisibility(View.GONE);
                                logintype = json.getString("loginType");
                                JSONObject json_user = json.getJSONObject("user");

                                id = json_user.getString("c_id");
                                email = json_user.getString("email");
                                fname = json_user.getString("fname");
                                lname = json_user.getString("lname");
                                contact = json_user.getString("contact");
                                birthday = json_user.getString("birthday");
                                sStatus = json_user.getString("status");
                                sCode = json_user.getString("code");
                                image = json_user.getString("image");
                                fb_id = json_user.getString("fb_id");
                                with_beta = json_user.getString("with_beta");

                                //startActivity(new Intent(Login.this,MainMenu.class));
                                finish();

                                if (logintype.equals("facebook")) {
                                    image = fb_profile_pic = "https://graph.facebook.com/" + fb_id + "/picture?type=large";
                                    editor.putString("prof_pic", image);
                                } else if (logintype.equals("normal")) {
                                    editor.putBoolean("facebook_login", false);
                                    if (fb_id.equals("null")) {
                                        editor.putString("prof_pic", image);
                                    } else {
                                        image = fb_profile_pic = "https://graph.facebook.com/" + fb_id + "/picture?type=large";
                                        editor.putString("prof_pic", image);
                                    }
                                }
                                editor.putString("id", id);
                                editor.putString("fb_id", fb_id);
                                editor.putString("email", email);
                                editor.putString("fname", fname);
                                editor.putString("lname", lname);
                                editor.putString("contact", contact);
                                editor.putString("birthday", birthday);
                                editor.putString("code", sCode);
                                editor.putString("prof_pic", image);
                                editor.putBoolean("showTutorial", true);
                                editor.putBoolean("map_legend", true);
                                editor.putString("with_beta", with_beta);
                                editor.commit();

                                Log.e("Beta", with_beta);
                                Log.e("Name", fname + " " + lname);
                                Log.e("Email", email);

                                // t.show();

                                if (sStatus.equals("Not Verified")) {
                                    startActivity(new Intent(Login.this, Verification.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    editor.putBoolean("session", true);
                                    editor.commit();
                                    finish();
                                }

                            } else if (result.equals("noAccount")) {
                                load.setVisibility(View.GONE);
                                /*Toast.makeText(getApplicationContext(), "Oops, sign up first using your email", Toast.LENGTH_SHORT).show();
                                LoginManager.getInstance().logOut();*/
                                JSONObject json_user = json.getJSONObject("user");
                                cars = json.getString("cars");
                                id = json_user.getString("c_id");
                                email = json_user.getString("email");
                                fname = json_user.getString("fname");
                                lname = json_user.getString("lname");
                                fb_id = json_user.getString("fb_id");
                                with_beta = json_user.getString("with_beta");
                                image = fb_profile_pic = "https://graph.facebook.com/" + fb_id + "/picture?type=large";


                                editor.putString("id", id);
                                editor.putString("fb_id", fb_id);
                                editor.putString("email", email);
                                editor.putString("fname", fname);
                                editor.putString("lname", lname);
                                editor.putString("prof_pic", image);
                                editor.putString("with_beta", with_beta);
                                editor.commit();

                                if (cars.equals("noCars")) {
                                    //startActivity(new Intent(Login.this, StepsAddCar.class));
                                    finish();
                                } else {
                                    //startActivity(new Intent(Login.this, MainActivity.class));
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    editor.putBoolean("session", true);
                                    editor.commit();
                                    finish();
                                }


                            } else {
                                load.setVisibility(View.GONE);
                                t2.show();
                                //LoginManager.getInstance().logOut();

                            }
                        } catch (Exception e) {
                            load.setVisibility(View.GONE);
                            //LoginManager.getInstance().logOut();
                            Toast.makeText(getApplicationContext(), "Something went wrong. Try Again", Toast.LENGTH_SHORT).show();
                            Log.e("JSON Parser", "Error parsing data " + e.toString());
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progressDialog.dismiss();
                        error.printStackTrace();
                        LoginManager.getInstance().logOut();
                        Toast.makeText(getApplicationContext(), "Oops, something bad happened", Toast.LENGTH_SHORT).show();

                    }
                });
        AppController.getInstance().addToRequestQueue((jsonRequest));

    }

    public void showEmailSent() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_email_sent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView email = (TextView) dialog.findViewById(R.id.email);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_okay);
        // if button is clicked, close the custom dialog
        email.setText(emailString);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            showRationale();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.equals(true)) {
                Toast.makeText(getApplicationContext(), "Thanks for trusting", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showRationale() {

    }

    /*@Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.pickapark.activities/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }*/

    /*@Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.pickapark.activities/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }*/
}
