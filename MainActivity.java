package com.example.schedulereminderversion1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
//import java.util.ArrayList;
//AppCompatActivity
public class MainActivity extends Activity {
    EditText regnumber, passwd;
    TextView TVErrMsg;
    Button login, register;
    String register_number, password, xSuccess;
    AlertDialog alertDialog;
    Context ctx;

    private static final String reg_url = "http://10.0.2.2/callingApp/register.php";
    //private static final String login_url = "http://10.0.2.2/callingApp/login.php";
    private static final String login_url = "http://192.168.0.113/callingApp/login.php";
    //    String password;
//    private static String register_number = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        regnumber = (EditText) findViewById(R.id.etRegisterNumber);
        passwd = (EditText) findViewById(R.id.etPwd);
        login = (Button) findViewById(R.id.button);
        register = (Button) findViewById(R.id.button2);
        TVErrMsg = (TextView) findViewById(R.id.txtErrorMsg);
        TVErrMsg.setText(" ");

//        AlertDialog alertDialog;
//        alertDialog = new AlertDialog.Builder(ctx).create();
//        alertDialog.setTitle("Login Information....");
        //alertDialog = new AlertDialog.Builder(ctx).create();
        //alertDialog.setTitle("Login Information....");

    }

    public void onClicklogin(View view) {
        register_number = regnumber.getText().toString();
        password = passwd.getText().toString();
        String method = "login";
        Log.e("InsideonClicklogin", "Inside onClicklogin before LoginBackground...............");
//        Background background = new Background((Context) this);
//        background.execute(method,register_number,password);
        if(register_number.equals("Admin") && password.equals("Admin"))
        {
            Intent intent = new Intent(MainActivity.this, updateTimeTable.class);
            startActivity(intent);
        }
        else {

            class LoginBackground extends AsyncTask<String, Void, String> {

                /*
                             protected void onPreExecute()
                             {
                                 Log.e("onPreExecute","Inside onClicklogin inside onPreExecute...............");
                                 alertDialog = new AlertDialog.Builder(ctx).create();
                                 alertDialog.setTitle("Login Information....");
                             }
                */
                @Override
                protected String doInBackground(String... params) {
                    Log.e("doInBackground", "Inside doInBackgroundr...............");
                    String login_name = params[1];
                    String login_pass = params[2];
                    try {
                        Log.e("doInBackground", "Inside doInBackgroundr...............");
                        URL url = new URL(params[0]);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        OutputStream OS = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                        String data = URLEncoder.encode("login_name", "UTF-8") + "=" + URLEncoder.encode(login_name, "UTF-8") + "&" +
                                URLEncoder.encode("login_pass", "UTF-8") + "=" + URLEncoder.encode(login_pass, "UTF-8");
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
                    Log.e("onPostExec TRY", result);
                    //xSuccess = result;
                    //alertDialog.setMessage(result);
                    //alertDialog.show();
                    //Log.e("xSuccess ","xSuccess       " + xSuccess);
                    if (result.equals("Login Success...."))    //if (result == "Login Success....")
                    {
                        Log.e("ifSUCCESS", "INSIDE       onPostExecute       INSIDE IF SUCCESS...............");
                        Intent intent = new Intent(MainActivity.this, DispTimeTable.class);
                        intent.putExtra("RegNum", register_number);

                        startActivity(intent);
                    } else {
                        Log.e("else part", "INSIDE       onPostExecute       INSIDE IF else part...............");
                        TVErrMsg.setText("Not a Registered User. Click on Register");
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    }
                    //Intent intent = new Intent(ctx, DispTimeTable.class);
                    //intent.putExtra("RegNum", MainActivity.register_number);

                    //startActivity(intent);
                }
            }
            Log.e("obj.execute", "before obj.execute...............");


            LoginBackground obj = new LoginBackground();
            obj.execute(login_url, register_number, password);

            if (xSuccess == "Login Success....") {
                Log.e("ifSUCCESS", "INSIDE IF SUCCESS...............");
                Intent intent = new Intent(MainActivity.this, DispTimeTable.class);
                intent.putExtra("RegNum", register_number);

                startActivity(intent);
            } else {
                Toast.makeText(this, xSuccess, Toast.LENGTH_LONG).show();
            }
        }

    }

    public void onClickRegister(View view) {
        Intent intent = new Intent(MainActivity.this, register_activity.class);
        startActivity(intent);
    }
}
