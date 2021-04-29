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

public class SendingSMS extends AppCompatActivity {

    AlarmManager alarmManager;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String distinctTimeURL = "http://192.168.0.113/callingApp/DistinctHour.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_s_m_s);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean InvokeSendingSMS(View view)
    {
        Log.e("setupAlarm","setupAlarm...............");

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                Log.e("permission", "Permission already granted.");
            }
            else {
                requestPermission();
            }
        }

        if(checkPermission())
        {
            setupAlarm();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }

        //Calendar calendar = Calendar.getInstance();

        return false;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);

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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setupAlarm()
    {

        Log.e("setupAlarm", "I N S I D E   S E T U P   A L A R M ()...");
        class SetAlarmForDISTINCTTimeFromDB extends AsyncTask<String, Void, String>
        {

            @Override
            protected String doInBackground(String... params) {
                try {
                    //Log.e("InsidedoInBackground","Inside doInBackground...............");
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream OS = httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                    String data = URLEncoder.encode("selectedday","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");
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
                    Log.e("doinbackground", response);
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


            //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected void onPostExecute(String data) {


                Calendar calendar = Calendar.getInstance();

                ArrayList<String> ArrayAlarmTime = new ArrayList<String>();
                String strDay;
                strDay = "";
                //List<String> ArrayAlarmTime = new ArrayList<String>();
                //JSONObject jsonObject = null;
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("disthour_resp");
                    ArrayAlarmTime.clear();
                    Log.e("onPostExecute", "JSON ARRAY LENGTH    " + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        ArrayAlarmTime.add(object.getString("hour"));
                        strDay = object.getString("day");
                        Log.e("onPostExecute", "JSON ARRAY LOOP    " + strDay);
                    }
                    Log.e("onPostExecute", "ALARM ARRAY LENGTH    " + ArrayAlarmTime.size());
                    //String ArrayAlarmTime[] = new String[]{
                    //        "10:42 PM",
                    //        "10:43 PM",
                    //        "10:44 PM"
                    //};


                    for (int f = 0; f < ArrayAlarmTime.size(); f++) {
                        Log.e("onPostExecute", "INSIDE FOR LOOP OF ARRAY ALARM TIME    " + ArrayAlarmTime.get(f));
                        int xHour = Integer.parseInt(ArrayAlarmTime.get(f).toString().substring(0, ArrayAlarmTime.get(f).toString().indexOf(":")));
                        Log.e("onPostExecute", "hour    " + xHour);

                        String xTempMinutes = ArrayAlarmTime.get(f).toString().substring(ArrayAlarmTime.get(f).toString().indexOf(":") + 1);
                        int xMinutes = Integer.parseInt(xTempMinutes.substring(0, xTempMinutes.indexOf(" ")));
                        String xAMPM = ArrayAlarmTime.get(f).toString().substring(ArrayAlarmTime.get(f).toString().indexOf(" ") + 1);

                        calendar.set(Calendar.HOUR, xHour);
                        calendar.set(Calendar.MINUTE, xMinutes);
                        calendar.set(Calendar.SECOND, 0);
                        if (xAMPM.equals("AM")) {
                            calendar.set(calendar.AM_PM, calendar.AM);
                        } else
                        {
                            calendar.set(calendar.AM_PM, calendar.PM);
                        }
/*
                        //String xAMPM = ArrayAlarmTime.get(f).toString().substring(-3)(ArrayAlarmTime.get(f).toString().length(-2) - 2);
                        //member.substr(member.length -2)
                        calendar.set(calendar.AM_PM, xAMPM);

                        if (xHour > 6 && xHour < 12)
                        {
                            calendar.set(calendar.AM_PM, calendar.AM);
                        } else if (xHour > 0 && xHour <= 6) {
                            calendar.set(calendar.AM_PM, calendar.PM);
                        } else {
                            calendar.set(calendar.AM_PM, calendar.PM);
                        }
*/
                        Log.e("HOUR      ", String.valueOf(xHour));
                        Log.e("MINUTES   ", String.valueOf(xMinutes));
                        //Log.e("AMPM   ", xAMPM);

                        String TimeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
                        Log.e("TimeText    ", TimeText);
                        long timeInMillis = calendar.getTimeInMillis();
                        Log.e("millis    ", String.valueOf(timeInMillis));
                        Log.e("SYSmillis", "Current SYstem time " + System.currentTimeMillis());

                        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Intent intentAlarmReceiver = new Intent(getApplicationContext(), AlarmReceiver.class);
                        intentAlarmReceiver.putExtra("ClassHour", ArrayAlarmTime.get(f));
                        intentAlarmReceiver.putExtra("ClassDay", strDay);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), f, intentAlarmReceiver, 0);
                        //pi=PendingIntent.getBroadcast(AlarmR.this, f,intent, 0);

                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

                        //intentArray.add(pi);

                        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, 2000, pendingIntent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        String xSelectedDay = "";
        Calendar cal = Calendar.getInstance();
        Date dt = cal.getTime();
        // full name form of the day
        xSelectedDay =new SimpleDateFormat("EEEE", Locale.ENGLISH).format(dt.getTime());

        SetAlarmForDISTINCTTimeFromDB objNEW = new SetAlarmForDISTINCTTimeFromDB();
        objNEW.execute(distinctTimeURL,xSelectedDay);

    }


}



