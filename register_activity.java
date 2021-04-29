package com.example.schedulereminderversion1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class register_activity extends AppCompatActivity {
    EditText txtusername,txtpassword,txtconfpassword;
    TextView TVRegErrorMsg;
    Button btnlogin,btnregister;
    String strusername="",strpasswd="",strconfpasswd="";

    //String reg_url = "http://10.0.2.2/callingApp/register.php";
    String reg_url = "http://192.168.0.113/callingApp/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        txtusername = (EditText) findViewById(R.id.etRegRegNo);
        txtpassword = (EditText) findViewById(R.id.etPassword);
        txtconfpassword = (EditText) findViewById(R.id.etCnfrmPasswd);

        TVRegErrorMsg = (TextView) findViewById(R.id.txtRegErrorMsg);

        btnlogin = (Button)findViewById(R.id.btnloginreg);
        btnregister = (Button)findViewById(R.id.btnRegisterreg);

        btnregister.setEnabled(true);
        btnlogin.setEnabled(false); //.setVisibility(View.INVISIBLE);

        if (TVRegErrorMsg != null)
        {
            Log.e("CHKTXT","tvregerrormsg not null" );
        }
        else
        {
            Log.e("CHKTXT","tvregerrormsg  nulllllllllllllllllllllllllllll" );
            TVRegErrorMsg.setText(" ");
            Log.e("TVRegErrorMsg",TVRegErrorMsg.getText().toString() );
        }

    }


    public void onClickregRegister(View view)
    {
        Log.e("onClickregRegister", "first line");

        //TVRegErrorMsg.setText("Password DIFFERENT FROM Confirm Password. Retry.");
        strusername = txtusername.getText().toString().trim();
        strpasswd = txtpassword.getText().toString().trim();
        strconfpasswd = txtconfpassword.getText().toString().trim();

        Log.e("CHKTXT", "before if");
        if (TVRegErrorMsg != null)
        {
            Log.e("CHKTXT","tvregerrormsg not null" );
        }
        Log.e("CHKTXT",TVRegErrorMsg.getText().toString() );
        if (txtpassword.getText().toString().trim().equals(txtconfpassword.getText().toString().trim())) {
            Log.e("CHKTXT", "OK");
            ValidateAndRegister();
        } else {
            Log.e("CHKTXT", "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnot OK");
            TVRegErrorMsg.setText("Password DIFFERENT FROM Confirm Password. Retry.");
        }

    }

    private void ValidateAndRegister()
    {
        class RegisterBackground extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String user_name = params[1];
                String user_pass = params[2];
                try {
                    Log.e("doInBackground", "Inside doInBackground...............");
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                    String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&" +
                            URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(user_pass, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String response = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                    return response;
                    //return "Registration Success";
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String result) {
                //Log.e("onPostExec1", result);
                //TVRegErrorMsg.setText(result);

                if (result.equals("Successfully Registered")) {
                    Log.e("onPostExec1", "INSIDE ONPOST EXECUTE IF STATEMENT AND NOT IN ELSE STATEMEMTN " + result);
                    btnlogin.setEnabled(true); //.setVisibility(View.INVISIBLE);
                    btnregister.setEnabled(false);
                    TVRegErrorMsg.setText("Click LOGIN to use the APP");
                }
                else
                {
                    Log.e("onPostExecELSE", result);
                    btnlogin.setEnabled(false); //.setVisibility(View.INVISIBLE);
                    TVRegErrorMsg.setText(result);

                }
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

        }
        Log.e("onClickregRegister", "RegisterBackground obj");


        RegisterBackground obj = new RegisterBackground();
        obj.execute(reg_url, strusername, strpasswd);
    }

    public void onClickRegLogin(View view)
    {
        Intent intent = new Intent(register_activity.this,MainActivity.class);
        startActivity(intent);
    }
}