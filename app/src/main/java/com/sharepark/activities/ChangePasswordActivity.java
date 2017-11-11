package com.sharepark.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharepark.R;
import com.sharepark.util.AppController;
import com.sharepark.util.WebServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText currentPassword, newPassword, confirmPassword;
    Button submit;
    ProgressDialog pdialog;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Toolbar toolbarChangePassActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        toolbarChangePassActivity = (Toolbar) findViewById(R.id.toolbar);
        assert toolbarChangePassActivity != null;
        toolbarChangePassActivity.setNavigationIcon(R.drawable.md_nav_back);
        toolbarChangePassActivity.setTitle("Change Password");
        toolbarChangePassActivity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ChangePasswordActivity.this);
            }
        });
        setSupportActionBar(toolbarChangePassActivity);


        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();

        currentPassword = (EditText) findViewById(R.id.current_pass);
        newPassword = (EditText) findViewById(R.id.new_pass);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (newPassword.getText().toString().length() < 6) {
                    newPassword.setError("Must be atleast 6 characters");
                }


            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String new_password;
                String current_password;
                new_password = newPassword.getText().toString();
                current_password = currentPassword.getText().toString();
                if (!new_password.equals(current_password)) {
                    confirmPassword.setError("Password Mismatch");
                }

            }
        });


        pdialog = new ProgressDialog(ChangePasswordActivity.this);
        pdialog.setMessage("Updating password...");
        pdialog.setCancelable(false);
        submit = (Button) findViewById(R.id.send_password);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPassword.getText().toString().equals("") || newPassword.getText().toString().equals("") || confirmPassword.getText().toString().equals("")) {
                    Toast.makeText(ChangePasswordActivity.this, "Please fill up the fields", Toast.LENGTH_SHORT).show();
                } else {
                    changePass();
                    pdialog.show();

                }
            }
        });

    }

    private void changePass() {
        String new_password;
        String current_password;
        new_password = newPassword.getText().toString();
        current_password = currentPassword.getText().toString();
        String id = preferences.getString("id", "");
        Log.e("Changepass Tag:", preferences.getString("webservice", WebServiceHelper.web_tip) + "login.php?tag=changePass&c_id=" + id + "&new_password=" + new_password + "&current_password=" + current_password);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, WebServiceHelper.web_tip + "login.php?tag=changePass&c_id=" + id + "&new_password=" + new_password + "&current_password=" + current_password, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pdialog.dismiss();
                        try {
                            String result = response.getString("result");
                            if (result.equals("success")) {
                                Toast.makeText(ChangePasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (result.equals("wrongpassword")) {
                                Toast.makeText(ChangePasswordActivity.this, "Current Password is Wrong", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ChangePasswordActivity.this, "Oops, something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        pdialog.dismiss();
                        Toast.makeText(ChangePasswordActivity.this, "Oops, something went wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                });
        AppController.getInstance().addToRequestQueue((jsonRequest));
    }


}
