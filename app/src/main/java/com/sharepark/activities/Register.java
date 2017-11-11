package com.sharepark.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharepark.R;
import com.sharepark.util.AppController;
import com.sharepark.util.Encrypt;
import com.sharepark.util.WebServiceHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class Register extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText email, password, confirm_password, fName,lName, contact;
    private static EditText date;
    private Button register;
    private static int age;
    private RelativeLayout load;
    private CheckBox iagree;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ProgressDialog progressDialog;
    private String sEmail, sPassword, sContact, sConfirm, sfName,slName, sDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();

        date = (EditText) findViewById(R.id.date_of_birth);
        contact = (EditText) findViewById(R.id.contact_number);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        fName = (EditText) findViewById(R.id.first_name);
        lName = (EditText) findViewById(R.id.last_name);


        load = (RelativeLayout) findViewById(R.id.load);
        load.setVisibility(View.GONE);


        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        iagree = (CheckBox) findViewById(R.id.checkBoxAgree);
        register = (Button) findViewById(R.id.register);
        register.setEnabled(false);
        iagree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    iagree.setChecked(true);
                    register.setEnabled(true);
                    register.setBackgroundResource(R.drawable.rounded_button_solid);
                    register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            register();
                        }
                    });
                } else {
                    iagree.setChecked(false);
                    register.setEnabled(false);
                    register.setBackgroundResource(R.drawable.rounded_button_gray);
                }
            }
        });

        TextView terms = (TextView) findViewById(R.id.termsandagreement);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Terms.class));
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Register.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String dateOutput = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        Calendar now = Calendar.getInstance();
        int c = now.get(Calendar.YEAR);
        age = c - year;
        if (age > 17) {
            date.setText(dateOutput);
        } else {
            Toast.makeText(Register.this, "You do not meet the required age", Toast.LENGTH_SHORT).show();
        }
    }


    public void register() {
        sEmail = email.getText().toString();
        sPassword = password.getText().toString();
        sConfirm = confirm_password.getText().toString();
        sfName = fName.getText().toString();
        slName = lName.getText().toString();
        sContact = contact.getText().toString();
        sDate = date.getText().toString();

        //form validation
        if (sEmail.equals("") || sPassword.equals("") || sConfirm.equals("") ||sfName.equals("") || sContact.equals("") || sDate.equals("")) {
            Toast.makeText(Register.this, "Please fill-up all fields", Toast.LENGTH_SHORT).show();
        }  else if (sPassword.length() < 6) {
            Toast.makeText(Register.this, "Password must be atleast 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            if (sPassword.equals(sConfirm)) {
                checkWebService();
            } else {
                Toast.makeText(Register.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            }

        }
        // password confirmation

    }

    public void checkWebService() {
        progressDialog.show();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "http://pickapark.com.ph/istip.php", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        try {
                            editor.putString("webservice", json.getString("site") + "/parker/");
                            editor.putString("webservice_root", json.getString("site") + "/");
                            editor.commit();
                            String p = Encrypt.protectString("tag=register&email=" + sEmail +
                                    "&password=" + sPassword +
                                    "&fname=" + sfName +
                                    "&lname=" + slName +
                                    "&contact=" + sContact +
                                    "&birthday=" + sDate);

                            String tags = "login.php?p=" + p;
                            Log.d("Parameters:", p);

                            JsonObjectRequest jsonRequest = new JsonObjectRequest
                                    (Request.Method.GET, preferences.getString("webservice", WebServiceHelper.web_tip) + tags, null, new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject json) {
                                            progressDialog.dismiss();
                                            load.setVisibility(View.GONE);
                                            Toast exis = Toast.makeText(Register.this, "Email already exists", Toast.LENGTH_SHORT);
                                            Toast fail = Toast.makeText(Register.this, "Registration Failed", Toast.LENGTH_SHORT);
                                            Toast conn = Toast.makeText(Register.this, "Can't connect to server, please check your connection.", Toast.LENGTH_SHORT);
                                            Toast some = Toast.makeText(Register.this, "Something went wrong.", Toast.LENGTH_SHORT);
                                            try {
                                                String result = json.getString("result");

                                                if (result.equals("success")) {
                                                  //  succ.show();
                                                    Intent i = new Intent(Register.this, Login.class);
                                                    i.putExtra("RegEmail", email.getText().toString());
                                                    startActivity(i);
                                                    finish();
                                                } else if (result.equals("existing")) {
                                                    exis.show();
                                                } else if (result.equals("failed")) {
                                                    fail.show();
                                                } else {
                                                    some.show();
                                                }


                                            } catch (JSONException e) {
                                                Log.e("JSON Parser", "Error parsing data " + e.toString());
                                            } catch (NullPointerException n) {
                                                conn.show();
                                            }

                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO Auto-generated method stub
                                            Toast.makeText(Register.this, "Oops, something bad happened", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                            AppController.getInstance().addToRequestQueue((jsonRequest));
                            load.setVisibility(View.VISIBLE);

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
                        Toast.makeText(getApplicationContext(), "Oops, something bad happened", Toast.LENGTH_SHORT).show();

                    }
                });
        AppController.getInstance().addToRequestQueue((jsonRequest));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        builder.setMessage("Do you want to cancel registration?")
                .setTitle("Cancel Registration")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
