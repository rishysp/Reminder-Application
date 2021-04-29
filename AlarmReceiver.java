package com.example.schedulereminderversion1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String sendSMSURL = "http://192.168.0.113/callingApp/SMSDayHour.php";

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        Toast.makeText(context, "showing alarm", Toast.LENGTH_SHORT).show();

        String strClassDay;
        String strClassHour;


        strClassDay = intent.getStringExtra("ClassDay");
        strClassHour = intent.getStringExtra("ClassHour");
        Log.e("BR =>", "strClassDay = " + strClassDay);
        Log.e("BR =>", "strClassHour = " + strClassHour);

        SendSMSForGivenTime(strClassDay, strClassHour, context);

/*
        SmsManager smsManager = SmsManager.getDefault();

        String smsMobileNum = "9840435552";
        String smstext = "Today at 8 you have OS class";
        Log.e("sms", smsMobileNum + "    " + smstext);
        smsManager.sendTextMessage(smsMobileNum, null, smstext, null, null);
*/
    }

    private  void SendSMSForGivenTime(String xparamClassDay, String xparamClassHour, Context context)
    {

        // CALL ASYNCTASK FUNCTION
        class sendSMSBackgroundTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                try {
                    Log.e("BR-DO-IN =>", "URL = " + params[0]);
                    Log.e("BR-DO-IN =>", "param class day = " + params[1]);
                    Log.e("BR-DO-IN =>", "param class hour = " + params[2]);
                    URL url = new URL(params[0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    Log.e("BR-DO-IN =>", "BEFORE OUTPUT STREAM " + params[2]);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    Log.e("BR-DO-IN =>", "AFTER OUTPUT STREAM " + params[2]);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                    String data = URLEncoder.encode("SMS_TTDay", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                            URLEncoder.encode("SMS_TTHour", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8");
                    //SMS_TTHour stands for SMS TimeTable Hour AND SMS_TTDay stands for SMS TimeTable Day
                    Log.e("BR-DO-IN =>", "data                  = " + data);
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();
                    Log.e("BR-DO-IN =>", "BEFORE input STREAM " + params[2]);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    Log.e("BR-DO-IN =>", "AFTER input STREAM " + params[2]);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder stringBuilder = new StringBuilder();
                    String response = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        //response += line;
                        Log.e("BR-WHILELOOP=> ", "LINE VALUE " + line);
                        stringBuilder.append(line + "\n");
                    }
                    bufferedReader.close();
                    inputStream.close();
                    response = stringBuilder.toString();
                    httpURLConnection.disconnect();
                    Log.e("BR-DO-IN =>", "Return Response JSON ====   " + response);
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


            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected void onPostExecute(String data) {
                Calendar calendar = Calendar.getInstance();
                SmsManager smsManager = SmsManager.getDefault();

                ArrayList<String> ArrayAlarmTime = new ArrayList<String>();
                String strDay;
                strDay = "";
                //List<String> ArrayAlarmTime = new ArrayList<String>();
                //JSONObject jsonObject = null;
                try {

                    JSONObject jsonObjectSMS = new JSONObject(data);
                    JSONArray jsonArraySMS = jsonObjectSMS.getJSONArray("sms_response");
                    for (int i = 0; i < jsonArraySMS.length(); i++) {
                        JSONObject objSMS = jsonArraySMS.getJSONObject(i);
                        String smsMobileNum = objSMS.getString("mobile");
                        Log.e("insideFORIF", smsMobileNum);
                        String smsDispHour = objSMS.getString("hour");
                        String smsDispSubject = objSMS.getString("subject");
                        String smstext = "Today at " + smsDispHour + " AM " + "you have " + smsDispSubject;
                        Log.e("BR-post-exec =>", "smstext ====>>>>>>   " + smstext);
                        smsManager.sendTextMessage(smsMobileNum, null, smstext, null, null);
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
        xSelectedDay =new SimpleDateFormat("EEEE", Locale.ENGLISH).format(((Date) dt).getTime());

        sendSMSBackgroundTask objNEW = new sendSMSBackgroundTask();
        objNEW.execute(sendSMSURL, xparamClassDay, xparamClassHour);

    }
}
