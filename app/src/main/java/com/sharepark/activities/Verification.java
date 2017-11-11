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
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;


public class Verification extends AppCompatActivity{

    private Dialog dialog;
    private String id;
    private String code;
    private EditText enter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification);

        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();

        enter = (EditText)findViewById(R.id.enteredCode);

        id = preferences.getString("id","");
        code = preferences.getString("code", "");


        TextView resendEmail = (TextView) findViewById(R.id.resendEmail);
        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pdialog = new ProgressDialog(Verification.this);
                pdialog.setMessage("Sending email");
                pdialog.show();
                pdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(Verification.this, "Email n", Toast.LENGTH_SHORT).show();
                    }
                });
                String p = Encrypt.protectString("tag=resend_email&c_id="+id);
                String tags = preferences.getString("webservice", WebServiceHelper.web_tip)+"verify.php?p="+p;
                Log.e("link",tags);

                JsonObjectRequest jsonRequest = new JsonObjectRequest
                        (Request.Method.GET, tags, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                pdialog.hide();
                                try {
                                    String result = response.getString("result");
                                    if(result.equals("emailSent")){
                                        Toast.makeText(Verification.this, "Email sent", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

                                pdialog.hide();
                            }
                        });
                AppController.getInstance().addToRequestQueue((jsonRequest));
            }
        });


    }

    public void submit(View v)
    {
        String eCode = enter.getText().toString();

        if(code.equals(eCode)){
            final ProgressDialog pdialog = new ProgressDialog(Verification.this);
            pdialog.setMessage(getString(R.string.Verifying));
            pdialog.show();
            pdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(Verification.this, "Email not sent", Toast.LENGTH_SHORT).show();
                }
            });
            String p = Encrypt.protectString("tag=verify&c_id="+id);
            String tags = preferences.getString("webservice", WebServiceHelper.web_tip)+"verify.php?p="+p;
            Log.e("link",tags);

            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, tags, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            pdialog.dismiss();
                            try {
                                String result = response.getString("result");
                                if(result.equals("success")){
                                    Toast.makeText(Verification.this, "Email Validated", Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(Verification.this,StepsAddCar.class));
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Verification.this, "Oops, something bad happened", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                            pdialog.dismiss();
                        }
                    });
            AppController.getInstance().addToRequestQueue((jsonRequest));
        }else{
            Toast.makeText(this,"Invalid Code",Toast.LENGTH_SHORT).show();
            enter.setText("");
        }



    }


    public void onBackPressed()
	{
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        builder.setMessage("Are you sure you want to cancel Verification?")
                .setTitle("Cancel")
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
