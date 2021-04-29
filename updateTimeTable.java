package com.example.schedulereminderversion1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class updateTimeTable extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener
{
    Spinner SpinnerDept,SpinnerSem,SpinnerSec,SpinnerDay,SpinnerHour;
    EditText SubjectUpdated;
    Button updateButton;
    String selectedDept,selectedSem,selectedSec,selectedDay,selectedHour,subjectupdatedto;
    private static final String updurl = "http://192.168.0.113/callingApp/updatesubject.php";
    AlarmManager alarmManager;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String distinctTimeURL = "http://192.168.0.113/callingApp/DistinctHour.php";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_time_table);
        SpinnerDept = (Spinner) findViewById(R.id.DeptSpinner);
        SpinnerSem = (Spinner)findViewById(R.id.Semesterspinner);
        SpinnerSec = (Spinner)findViewById(R.id.Sectionspinner);
        SpinnerDay = (Spinner)findViewById(R.id.Dayspinner);
        SpinnerHour = (Spinner)findViewById(R.id.Hourspinner);
        SubjectUpdated = (EditText)findViewById(R.id.etSubjectChange);
        updateButton = (Button)findViewById(R.id.btnupdate);


        ArrayAdapter<CharSequence> arrayAdapterDept = ArrayAdapter.createFromResource(this,R.array.DeptNames, android.R.layout.simple_spinner_item);
        arrayAdapterDept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        SpinnerDept.setAdapter(arrayAdapterDept);

        SpinnerDept.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> arrayAdapterSem = ArrayAdapter.createFromResource(this,R.array.SemesterNames, android.R.layout.simple_spinner_item);
        arrayAdapterSem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        SpinnerSem.setAdapter(arrayAdapterSem);

        SpinnerSem.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> arrayAdapterSec = ArrayAdapter.createFromResource(this,R.array.SectionNames, android.R.layout.simple_spinner_item);
        arrayAdapterSec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        SpinnerSec.setAdapter(arrayAdapterSec);

        SpinnerSec.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> arrayAdapterDay = ArrayAdapter.createFromResource(this,R.array.DayNames, android.R.layout.simple_spinner_item);
        arrayAdapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        SpinnerDay.setAdapter(arrayAdapterDay);

        SpinnerDay.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> arrayAdapterHour = ArrayAdapter.createFromResource(this,R.array.HourNames, android.R.layout.simple_spinner_item);
        arrayAdapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        SpinnerHour.setAdapter(arrayAdapterHour);

        SpinnerHour.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
      if(adapterView.getId()==R.id.DeptSpinner)
      {
          selectedDept = adapterView.getSelectedItem().toString();
      }
      else if(adapterView.getId() == R.id.Semesterspinner)
      {
          selectedSem = adapterView.getSelectedItem().toString();
      }
      else if(adapterView.getId() == R.id.Sectionspinner)
      {
          selectedSec = adapterView.getSelectedItem().toString();
      }
      else if(adapterView.getId() == R.id.Dayspinner)
      {
          selectedDay = adapterView.getSelectedItem().toString();
      }
      else if(adapterView.getId() == R.id.Hourspinner)
      {
          selectedHour = adapterView.getSelectedItem().toString();
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    public void onclickUpdate(View view)
    {
        subjectupdatedto = SubjectUpdated.getText().toString();
        String method = "Update";
        if(subjectupdatedto.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please enter a valid class!",Toast.LENGTH_LONG).show();
        }
        else
        {
            class updateBackground extends AsyncTask<String, Void,String>
            {

                @Override
                protected String doInBackground(String... params) {
                    //String login_name = params[1];
                    //String login_pass = params[2];
                    try {
                        Log.e("doInBackground", "Inside doInBackgroundr...............");
                        URL url = new URL(params[0]);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        OutputStream OS = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                        String data = URLEncoder.encode("degreetype", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                                URLEncoder.encode("department", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8")+ "&" +
                                URLEncoder.encode("semester", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8")+ "&" +
                                URLEncoder.encode("section", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8")+ "&" +
                                URLEncoder.encode("day", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8")+ "&" +
                                URLEncoder.encode("hour", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8")+ "&" +
                                URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8");
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

                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }

            }
            Log.e("onClickregRegister", "RegisterBackground obj");


            updateBackground obj = new updateBackground();
            obj.execute(updurl, "UG", selectedDept,selectedSem,selectedSec,selectedDay,selectedHour,subjectupdatedto);


        }


    }
    public void btnAlarmSetWakeup(View view)
    {
        Intent intent = new Intent(updateTimeTable.this,SendingSMS.class);
        startActivity(intent);
    }
}
