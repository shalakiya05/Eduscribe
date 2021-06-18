package com.jaktech.eduscribe;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.cardemulation.OffHostApduService;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaktech.eduscribe.Double.DoubleClick;
import com.jaktech.eduscribe.Double.DoubleClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DobActivity extends AppCompatActivity implements  TextToSpeech.OnInitListener{

    private TextView dob,regno;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private int d,m;
    int co=0;
    public String ip;
    private String textView,day,month,year;
    String status;
    private String [] y = {"1990","1991","1992","1993","1994","1995","1996","1997","1998","1999","2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015"};
    private String [] da = {"1","2","3","4","5","6","7","8","9","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","i","ii","iii","iv","v","vi","vii","viii","ix","x","xi","xii",
            "xiii","xiv","xv","xvi","xvii","xviii","xix","xx","xxi","xxii","xxiii","xxiv","xxv",
            "xxvi","xxvii","xxviii","xxix","xxx","xxxi"};
    private String [] mo = {"1","2","3","4","5","6","7","8","9","01","02","03","04","05","06","07","08","09","10","11","12","i","ii","iii","iv","v","vi","vii","viii","ix","x","xi","xii"};
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dob);
        //speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
        Intent Extra = getIntent();
        status = Extra.getStringExtra("status");
        ip = Extra.getStringExtra("ip");
        textView= Extra.getStringExtra("Email");
        regno =findViewById(R.id.regno);
        regno.setText(textView);
        dob = (TextView)findViewById(R.id.dob);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},200);
        textToSpeech=new TextToSpeech(this,this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


    }
    public static void exitApplication(Context context)
    {
        Intent intent = new Intent(context, DobActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(intent);
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 25) {
                textToSpeech.speak("I am going to close",TextToSpeech.QUEUE_FLUSH,null);
                if(android.os.Build.VERSION.SDK_INT >= 21)
                {
                    finishAndRemoveTask();
                }
                else
                {
                    finishAffinity();
                }
                DobActivity.exitApplication(context);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }


    @Override
    public void onInit(int i) {
        if(i==TextToSpeech.SUCCESS)
        {
            textToSpeech.setLanguage(Locale.ENGLISH);
            textToSpeech.setPitch(1);
            textToSpeech.setSpeechRate(1);
        }
    }

    public void listenForDay()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your day of birth",TextToSpeech.QUEUE_FLUSH,null);
            }
        },3000);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE,true);
        //speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"need to speek");
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

                //Toast.makeText(MainActivity0.this,"'Ready?",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int errorCode) {
                String errorMessage = getErrorText(errorCode);
                textToSpeech.speak(errorMessage,TextToSpeech.QUEUE_ADD,null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listenForDay();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                // ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                day = data.get(0).toString();
                String regex = "[1-9]|[12][0-9]|3[01]";
                //Creating a pattern object
                Pattern pattern;
                Matcher matcher;
                if (!day.equals("")) {
                    if(day.equals("six")){
                        day = "6";
                    }
                    if(day.equals("xx")){
                        day = "20";
                    }
                    if(day.equals("tu") || day.equals("Tu")){
                        day = "2";
                    }
                    pattern = Pattern.compile(regex);
                    //Creating a Matcher object
                    matcher = pattern.matcher(day);
                    if(matcher.matches()){
                        if (day.length() <= 2) {
                            d = Integer.parseInt(day);
                            if (d > 0 && d < 10) {
                                day = String.format("%02d", d);
                                listenForMonth();
                            } else {
                                listenForMonth();
                            }
                        } else {
                            textToSpeech.speak("Tell valid day", TextToSpeech.QUEUE_ADD, null);
                            listenForDay();
                        }
                    }
                    else {
                        textToSpeech.speak("Tell valid day", TextToSpeech.QUEUE_ADD, null);
                        listenForDay();
                    }
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 1000);
               /* AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

                amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                amanager.setStreamMute(AudioManager.STREAM_RING, false);
                amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);*/

            }
            public String getErrorText(int errorCode) {
                String message;
                switch (errorCode) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Client side error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Check your network connection and try again";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "error from server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech input";
                        break;
                    default:
                        message = "Didn't understand, please try again.";
                        break;
                }
                return message;
            }


            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        /*AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC,true);
        amanager.setStreamVolume(AudioManager.STREAM_SYSTEM,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        amanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                speechRecognizer.startListening(speechIntent);
            }
        },6000);

    }
    public void listenForMonth()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your month of birth",TextToSpeech.QUEUE_FLUSH,null);
            }
        },3000);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE,true);
        //speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"need to speek");
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

                //Toast.makeText(MainActivity0.this,"'Ready?",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int errorCode) {
                String errorMessage = getErrorText(errorCode);
                textToSpeech.speak(errorMessage,TextToSpeech.QUEUE_ADD,null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listenForMonth();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                // ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                month=data.get(0).toString();
                String regex = "[1-9]|1[0-2]";
                //Creating a pattern object
                Pattern pattern;
                Matcher matcher;
                if (!month.equals("")) {
                    if(month.equals("six")){
                        month = "6";
                    }
                    if(month.equals("xx")){
                        month= "20";
                    }
                    if(month.equals("tu") || month.equals("Tu")){
                        month= "2";
                    }
                    pattern = Pattern.compile(regex);
                    //Creating a Matcher object
                    matcher = pattern.matcher(month);
                    if(matcher.matches()){
                        if (month.length() <= 2) {
                            m = Integer.parseInt(month);
                            if (m > 0 && m < 10) {
                                month = String.format("%02d", m);
                                listenForYear();
                            } else {
                                listenForYear();
                            }
                        } else {
                            textToSpeech.speak("Tell valid month", TextToSpeech.QUEUE_ADD, null);
                            listenForMonth();
                        }
                    }
                    else {
                        textToSpeech.speak("Tell valid month", TextToSpeech.QUEUE_ADD, null);
                        listenForMonth();
                    }
                }
               /* AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

                amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                amanager.setStreamMute(AudioManager.STREAM_RING, false);
                amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);*/

            }
            public String getErrorText(int errorCode) {
                String message;
                switch (errorCode) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Client side error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Check your network connection and try again";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "error from server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech input";
                        break;
                    default:
                        message = "Didn't understand, please try again.";
                        break;
                }
                return message;
            }


            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        /*AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC,true);
        amanager.setStreamVolume(AudioManager.STREAM_SYSTEM,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        amanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                speechRecognizer.startListening(speechIntent);
            }
        },6000);

    }
    public void listenForYear()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your year of birth",TextToSpeech.QUEUE_FLUSH,null);
            }
        },3000);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE,true);
        //speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"need to speek");
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

                //Toast.makeText(MainActivity0.this,"'Ready?",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int errorCode) {
                String errorMessage = getErrorText(errorCode);
                textToSpeech.speak(errorMessage,TextToSpeech.QUEUE_ADD,null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listenForYear();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                // ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                year=data.get(0).toString();
                if(!year.equals(""))
                {
                    year = year.replaceAll("\\s", "");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (year.length() == 4) {
                                if (Arrays.asList(y).contains(year)) {
                                    dob.setText(day + "-" + month + "-" + year);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(status.equals("student")) {
                                                loginStudent();
                                            }
                                            else if(status.equals("teacher")) {
                                                loginTeacher();
                                            }
                                            else if(status.equals("admin")) {
                                                loginAdmin();
                                            }
                                        }
                                    },3000);

                                } else {
                                    textToSpeech.speak("Tell valid year and enter 4 digits", TextToSpeech.QUEUE_ADD, null);
                                    listenForYear();
                                }
                            }
                            else {
                                textToSpeech.speak("Tell 4 digits", TextToSpeech.QUEUE_ADD, null);
                                listenForYear();
                            }
                        }
                    },1000);

                }

               /* AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

                amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                amanager.setStreamMute(AudioManager.STREAM_RING, false);
                amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);*/

            }
            public String getErrorText(int errorCode) {
                String message;
                switch (errorCode) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Client side error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Check your network connection and try again";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "error from server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech input";
                        break;
                    default:
                        message = "Didn't understand, please try again.";
                        break;
                }
                return message;
            }


            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        /*AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC,true);
        amanager.setStreamVolume(AudioManager.STREAM_SYSTEM,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        amanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                speechRecognizer.startListening(speechIntent);
            }
        },6000);
    }

    public void loginStudent() {
        String url = "http://"+ip+"/student/login-reattempt";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject array = new JSONObject(response);
                    if (array.get("status").equals("success")) {
                        //Todo next activity
                        Log.i("tag", array.toString());
                        textToSpeech.speak("Login success", TextToSpeech.QUEUE_ADD, null);
                        //Toast.makeText(DobActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        home();
                    } else if (array.get("response").equals("You are logged in on another device")) {
                        textToSpeech.speak("You are logged in another device, logout from that device and try again", TextToSpeech.QUEUE_ADD, null);
                    } else {
                        textToSpeech.speak("Login Failed", TextToSpeech.QUEUE_ADD, null);
                        Toast.makeText(DobActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        reset();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("regno", regno.getText().toString());
                map.put("dob", dob.getText().toString());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }
        public void loginTeacher()
        {
            String url = "http://"+ip+"/teacher/login-reattempt";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            //Todo next activity
                            Log.i("tag", array.toString());
                            textToSpeech.speak("Login success", TextToSpeech.QUEUE_ADD, null);
                            Toast.makeText(DobActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            home();
                        } else if (array.get("response").equals("You are logged in on another device")) {
                            textToSpeech.speak("You are logged in another device, logout from that device and try again", TextToSpeech.QUEUE_ADD, null);
                        } else {
                            textToSpeech.speak("Login Failed", TextToSpeech.QUEUE_ADD, null);
                            Toast.makeText(DobActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            reset();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("regId", regno.getText().toString());
                    map.put("dob", dob.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
    }
    public void loginAdmin()
    {
        String url = "http://"+ip+"/admin/login-reattempt";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject array = new JSONObject(response);
                    if (array.get("status").equals("success")) {
                        //Todo next activity
                        Log.i("tag", array.toString());
                        textToSpeech.speak("Login success", TextToSpeech.QUEUE_ADD, null);
                        Toast.makeText(DobActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        home();
                    } else if (array.get("response").equals("You are logged in on another device")) {
                        textToSpeech.speak("You are logged in another device, logout from that device and try again", TextToSpeech.QUEUE_ADD, null);
                    } else {
                        textToSpeech.speak("Login Failed", TextToSpeech.QUEUE_ADD, null);
                        Toast.makeText(DobActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        reset();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("admin_id", regno.getText().toString());
                map.put("dob", dob.getText().toString());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

    public void home(){
        Intent i = new Intent(DobActivity.this,HomeActivity.class);
        startActivity(i);

    }
    public void reset(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Do you want to reset passsword or register",TextToSpeech.QUEUE_ADD,null);
            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("If you want to reset password say reset",TextToSpeech.QUEUE_ADD,null);
            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("If you want to register say register",TextToSpeech.QUEUE_ADD,null);
            }
        },3000);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,150000);
        speechIntent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE,true);
        //speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"need to speek");
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                //Toast.makeText(MainActivity0.this,"login or register?",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int errorCode) {
                String errorMessage = getErrorText(errorCode);
                textToSpeech.speak(errorMessage,TextToSpeech.QUEUE_ADD,null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reset();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String a=data.get(0).toString();
                while(co<=1){
                    if(!a.equals(""))
                    {
                        if(a.equals("reset")) {
                            textToSpeech.speak("Wait for a minute", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent i = new Intent(DobActivity.this, ResetActivity.class);
                                    i.putExtra("regno", regno.getText().toString());
                                    i.putExtra("status", status);
                                    i.putExtra("ip",ip);
                                    startActivity(i);
                                }
                            },3000);
                            break;
                        }
                        else if(a.equals("register")){
                            textToSpeech.speak("registration started,Wait for a minute", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(DobActivity.this, RegisterActivity.class);
                                    i.putExtra("status", status);
                                    i.putExtra("ip",ip);
                                    startActivity(i);

                                }
                            },3000);
                            break;
                        }
                        else {
                            co++;
                            if(co<2){
                                textToSpeech.speak("Please enter reset or register", TextToSpeech.QUEUE_ADD, null);
                                reset();
                            }
                            else{
                                textToSpeech.speak("Please close the app and try again", TextToSpeech.QUEUE_ADD, null);
                            }
                            break;
                        }
                    }
                }

               /* AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

                amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                amanager.setStreamMute(AudioManager.STREAM_RING, false);
                amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);*/

            }
            public String getErrorText(int errorCode) {
                String message;
                switch (errorCode) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Client side error";
                        break;
                    case
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Network error";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "error from server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech input";
                        break;
                    default:
                        message = "Didn't understand, please try again.";
                        break;
                }
                return message;
            }


            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
       /* AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC,true);
        amanager.setStreamVolume(AudioManager.STREAM_SYSTEM,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        amanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                speechRecognizer.startListening(speechIntent);
            }
        },10000);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ContextCompat.checkSelfPermission(this,permissions[0])== PackageManager.PERMISSION_GRANTED)
        {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initialPlaylist();
                    //listenForDay();
                }
            },100);
        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions,200);
        }
    }
    private void initialPlaylist() {
        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE != orientation) {
            textToSpeech.speak("Please Change phone from portrait mode to Landscape mode for better experience",TextToSpeech.QUEUE_FLUSH,null);
        }
        else{
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            listenForDay();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation=newConfig.orientation;
        if(orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            textToSpeech.speak("Good",TextToSpeech.QUEUE_FLUSH,null);
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            listenForDay();
        }
        else {
            textToSpeech.speak("Please Change phone from portrait mode to Landscape mode for better experience",TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    @Override
    protected void onDestroy() {
        if(textToSpeech!=null)
        {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}