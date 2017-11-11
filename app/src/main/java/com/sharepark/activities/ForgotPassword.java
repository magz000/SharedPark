package com.sharepark.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharepark.R;
import com.sharepark.util.AppController;
import com.sharepark.util.Encrypt;
import com.sharepark.util.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends AppCompatActivity {

    Dialog dialog;

    String id, code, eCode;

    EditText enter;
    String email;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);


        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();
        enter = (EditText) findViewById(R.id.email);
        email = enter.getText().toString();

        id = preferences.getString("id", "");
        code = preferences.getString("code", "");

        Button sendPassword = (Button) findViewById(R.id.send_password);
        if (sendPassword != null) {
            sendPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submit();
                }
            });
        }

        progressDialog = new ProgressDialog(ForgotPassword.this);
        progressDialog.setMessage("Checking webservice...");
        progressDialog.setCancelable(false);
    }

    public void checkWebService() {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "http://pickapark.com.ph/istip.php", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        try {
                            editor.putString("webservice", json.getString("site") + "/parker/");
                            editor.putString("webservice_root", json.getString("site") + "/");
                            editor.commit();
                            sendEmail();

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
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue((jsonRequest));
    }


    public void submit() {

        if (!enter.getText().toString().equals("")) {
            checkWebService();
            progressDialog.show();

        } else {
            Toast.makeText(ForgotPassword.this, "Input email", Toast.LENGTH_SHORT).show();
        }


    }

    public void sendEmail() {

        progressDialog.setMessage("Sending email");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(ForgotPassword.this, "Email not sent", Toast.LENGTH_SHORT).show();
            }
        });
        String p = Encrypt.protectString("tag=resetpassword&email=" + enter.getText().toString());
        String tags = "login.php?p=" + p;
        Log.e("Res Password", preferences.getString("webservice", WebServiceHelper.web_tip)+tags);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, preferences.getString("webservice", WebServiceHelper.web_tip) + tags, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        String result = null;
                        try {
                            result = response.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        assert result != null;
                        if (result.equals("passwordsent")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgotPassword.this, R.style.MyAlertDialogStyle);
                            alertDialogBuilder.setTitle("Success");
                            alertDialogBuilder.setMessage("Your new password is sent to " + enter.getText().toString());

                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    finish();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        } else if (result.equals("noEmail")) {
                            Toast.makeText(ForgotPassword.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPassword.this, "Oops, something went wrong", Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPassword.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
        AppController.getInstance().addToRequestQueue((jsonRequest));
    }

    public void onBackPressed() {
        finish();
    }


}
