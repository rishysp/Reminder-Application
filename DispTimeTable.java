package com.example.schedulereminderversion1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;



import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;


public class DispTimeTable extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    ListView listView;
    Spinner SpinSelectedDay;
    Button buttonDisplayTimeTable;
    TextView TVStudentName, TVDegreeType, TVDeptSemSec, TVSemSec;

    AlertDialog alertDialog;

    ArrayList<String> holder = new ArrayList<>();
    //private static final String apiurl = "http://10.0.2.2/callingApp/get_timetable_data.php";
    private static final String apiurl = "http://192.168.0.113/callingApp/get_timetable_data.php";

    String userRegisterNumber;
    String strSelectedDay, strStudentName, strDegreeType, strDeptSemSec, strSemSec ;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp_time_table);

        userRegisterNumber = getIntent().getExtras().getString("RegNum");  // THIS VALUE COMES FROM MAINACTIVITY COS WE HAVE TO USE THE SAME LOGIN USER REGISTER NUMBER

        SpinSelectedDay = (Spinner) findViewById(R.id.spinSelectDay);
        TVStudentName = (TextView) findViewById(R.id.txtName);
        TVDegreeType = (TextView) findViewById(R.id.txtDegreeType);
        TVDeptSemSec = (TextView) findViewById(R.id.txtDeptSemSec);
        TVSemSec = (TextView) findViewById(R.id.txtSemSec);
        listView = (ListView) findViewById(R.id.lvDisplayData);

        strStudentName = "";
        strDegreeType = "";
        strDeptSemSec = "";
        strSemSec = "";

        //SpinSelectedDay.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        //Creating the ArrayAdapter instance having the day name list
//        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, DayNames);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.DayNames, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        SpinSelectedDay.setAdapter(arrayAdapter);

        SpinSelectedDay.setOnItemSelectedListener(this);
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Schedule Remainder");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            }
        });


    }


    public void onClickDisplayTimeTable(View view) {

        if (strSelectedDay == "") {
            alertDialog.setMessage("If Day not selected, <MONDAY> will be default day");
            alertDialog.show();
            strSelectedDay = "Monday";
        }

        class dbManager extends AsyncTask<String, Void, String>
        {

            @Override
            protected String doInBackground(String... params) {

                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                    String data = URLEncoder.encode("login_userRegisterNumber", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                            URLEncoder.encode("selected_day", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder stringBuilder = new StringBuilder();
                    String response = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        //response += line;
                        stringBuilder.append(line + "\n");
                    }
                    bufferedReader.close();
                    inputStream.close();
                    response = stringBuilder.toString();
                    httpURLConnection.disconnect();
                    return response;

                } catch (Exception ex) {
                    return ex.getMessage();
                }
                //return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String data) {
                try {
//                    JSONArray jsonArray = new JSONArray(data);
//                    JSONObject jsonObject;
                    strStudentName = "";
                    strDegreeType = "";
                    strDeptSemSec = "";
                    strSemSec = "";
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    holder.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        //jsonObject = jsonArray.getJSONObject(i);
                        String DispHour = object.getString("hour");
                        String DispSubject = object.getString("subject");
                        if (i == 0) {
                            Log.e("insideFORIF", "insideFORIF");
                            strStudentName = object.getString("studentname");
                            strDegreeType = object.getString("degreetype");
                            strDeptSemSec = object.getString("department");
                            strSemSec = strSemSec + "Semester := " + object.getString("semester");
                            strSemSec = strSemSec + "   AND Section := " + object.getString( "section");
                        }
                        holder.add("At " + DispHour + " AM " + " ==>  " + DispSubject);
                        //holder.add(DispSubject);
                    }

                    TVStudentName.setText(strStudentName);
                    TVDegreeType.setText(strDegreeType) ;
                    TVDeptSemSec.setText(strDeptSemSec);
                    TVSemSec.setText(strSemSec);

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, holder);
                    listView.setAdapter(arrayAdapter);




                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            Log.e("permission", "Permission already granted.");
                        } else {
                            requestPermission();
                        }
                    }

                    if(checkPermission())
                    {
                        //Get the default SmsManager//
                        SmsManager smsManager = SmsManager.getDefault();

                        //Send the SMS//
                        //String phoneNum = "9840435552";
                        try {
                            JSONObject smsobject = new JSONObject(data);
                            JSONObject smsjsonObject = new JSONObject(data);
                            JSONArray smsjsonArray = smsjsonObject.getJSONArray("server_response");
                            for (int i = 0; i < smsjsonArray.length(); i++)
                            {
                                JSONObject obj = smsjsonArray.getJSONObject(i);
                                String smsMobileNum = obj.getString("mobile");
                                Log.e("insideFORIF", smsMobileNum);
                                String smsDispHour = obj.getString("hour");
                                String smsDispSubject = obj.getString("subject");
                                String smstext = "Today at " + smsDispHour + " AM " + "you have " + smsDispSubject;
                                smsManager.sendTextMessage(smsMobileNum, null, smstext, null, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



            private boolean checkPermission() {
                int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);
                return result == PackageManager.PERMISSION_GRANTED;
            }

            private void requestPermission() {
                ActivityCompat.requestPermissions(DispTimeTable.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);

            }

            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                switch (requestCode) {
                    case PERMISSION_REQUEST_CODE:
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext() , "Permission accepted", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(),  "Permission denied", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }







        }

        dbManager obj = new dbManager();
        obj.execute(apiurl, userRegisterNumber, strSelectedDay);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String choice = parent.getItemAtPosition(position).toString();
        strSelectedDay = choice;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}