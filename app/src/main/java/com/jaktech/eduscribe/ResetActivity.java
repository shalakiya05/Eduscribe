package com.jaktech.eduscribe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private String regno,ano,pass,pwd,pwd1,status;
    private TextView rno,ano1,ps;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    public String ip,ano2;
    int i;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        //speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},200);
        textToSpeech=new TextToSpeech(this,this);
        Intent Extra = getIntent();
        regno = Extra.getStringExtra("regno");
        status = Extra.getStringExtra("status");
        ip = Extra.getStringExtra("ip");
        rno=(TextView)findViewById(R.id.regno);
        rno.setText(regno);
        ano1=(TextView)findViewById(R.id.aadhar);
        ps=(TextView)findViewById(R.id.passwd);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


    }
    public static void exitApplication(Context context)
    {
        Intent intent = new Intent(context, ResetActivity.class);

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
                ResetActivity.exitApplication(context);
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

    public void aadharcheck()
    {
        if(status.equals("student")) {
            String url = "http://"+ip+"/student/reset-password";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("response").equals("aadhar didn't match")) {
                            textToSpeech.speak("aadhar number didn't match with your registred aadhar", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    listenForAadhar();
                                }
                            }, 5000);
                        } else {
                            listenForPassword();
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
                    map.put("regno", regno);
                    map.put("aadhar", ano);
                    map.put("password", ps.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("teacher")) {
            String url = "http://"+ip+"/teacher/reset-password";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("response").equals("aadhar didn't match")) {
                            textToSpeech.speak("aadhar number didn't match with your registred aadhar", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    listenForAadhar();
                                }
                            }, 5000);
                        } else {
                            listenForPassword();
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
                    map.put("regId", regno);
                    map.put("aadhar", ano);
                    map.put("password", ps.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("admin")) {
            String url = "http://"+ip+"/admin/reset-password";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("response").equals("aadhar didn't match")) {
                            textToSpeech.speak("aadhar number didn't match with your registred aadhar", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    listenForAadhar();
                                }
                            }, 5000);
                        } else {
                            listenForPassword();
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
                    map.put("admin_id", regno);
                    map.put("aadhar", ano);
                    map.put("password", ps.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
    }

    public void reset() {
        if (status.equals("student")) {
            String url = "http://"+ip+"/student/reset-password";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            Log.i("tag", array.toString());
                            textToSpeech.speak("Password successfully reset", TextToSpeech.QUEUE_ADD, null);
                            textToSpeech.speak("Login started, wait for a minute", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(ResetActivity.this, MainActivity.class);
                                    i.putExtra("status", status);
                                    startActivity(i);
                                }
                            }, 7000);
                        } else {
                            textToSpeech.speak("Invalid please try again", TextToSpeech.QUEUE_ADD, null);
                            startActivity(new Intent(ResetActivity.this, ResetActivity.class));
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
                    map.put("regno", regno);
                    map.put("aadhar", ano);
                    map.put("password", ps.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("teacher")){
            String url = "http://"+ip+"/teacher/reset-password";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success"))
                        {
                            Log.i("tag",array.toString());
                            textToSpeech.speak("Password successfully reset",TextToSpeech.QUEUE_ADD,null);
                            textToSpeech.speak("Login started, wait for a minute",TextToSpeech.QUEUE_ADD,null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(ResetActivity.this,MainActivity.class);
                                    i.putExtra("status", status);
                                    i.putExtra("ip",ip);
                                    startActivity(i);
                                }
                            },7000);
                        }
                        else {
                            textToSpeech.speak("Invalid please try again",TextToSpeech.QUEUE_ADD,null);
                            startActivity(new Intent(ResetActivity.this,ResetActivity.class));
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
                    map.put("regId", regno);
                    map.put("aadhar", ano);
                    map.put("password", ps.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("admin")){
            String url = "http://"+ip+"/admin/reset-password";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success"))
                        {
                            Log.i("tag",array.toString());
                            textToSpeech.speak("Password successfully reset",TextToSpeech.QUEUE_ADD,null);
                            textToSpeech.speak("Login started, wait for a minute",TextToSpeech.QUEUE_ADD,null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(ResetActivity.this,MainActivity.class);
                                    i.putExtra("status", status);
                                    i.putExtra("ip",ip);
                                    startActivity(i);
                                }
                            },7000);
                        }
                        else {
                            textToSpeech.speak("Invalid please try again",TextToSpeech.QUEUE_ADD,null);
                            startActivity(new Intent(ResetActivity.this,ResetActivity.class));
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
                    map.put("admin_id", regno);
                    map.put("aadhar", ano);
                    map.put("password", ps.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
    }




    public void listenForAadhar()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your aadhar number",TextToSpeech.QUEUE_FLUSH,null);
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
                        listenForAadhar();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String regex="\\d{12}";
                //ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                ano=data.get(0).toString();
                if(!ano.equals("")) {
                    ano = ano.replaceAll("\\s+","");
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(ano);
                    if(matcher.matches()){
                        ano = ano.replaceAll("....","$0 ");
                        ano1.setText(ano);
                        spell(ano);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmAadhar();
                            }
                        }, 6000);
                    }
                    else {
                        textToSpeech.speak("Enter a valid number", TextToSpeech.QUEUE_ADD, null);
                        listenForAadhar();
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

    public void confirmAadhar()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Do you want to re enter or continue ",TextToSpeech.QUEUE_ADD,null);
            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("say aadhar to reenter your aadhar number",TextToSpeech.QUEUE_ADD,null);
            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("say continue to continue",TextToSpeech.QUEUE_ADD,null);
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
                        confirmAadhar();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String a = data.get(0).toString();
                if(!a.equals("")) {
                    a = a.toLowerCase();
                    a = a.replaceAll("\\s+", "");

                    if (a.equals("aadhar")||a.equals("adhar")) {
                        textToSpeech.speak("Spell your aadhar number digit by digit", TextToSpeech.QUEUE_ADD, null);
                        ano1.setText("");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                promptAadhar();
                            }
                        },2000);

                    }

                    else if(a.equals("continue")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ano1.setText(ano1.getText().toString());
                                aadharcheck();
                            }
                        },1000);

                    }
                    else {
                        textToSpeech.speak("Invalid command",TextToSpeech.QUEUE_ADD,null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmAadhar();
                            }
                        },1000);
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
        },13000);
    }

    public void promptAadhar()
    {

        //textToSpeech.speak("SPELL",TextToSpeech.QUEUE_ADD,null);

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
                        promptAadhar();
                    }
                },4000);


            }

            @Override
            public void onResults(Bundle results) {
                String regex = "^[0-9]{1}$";
                Pattern pattern;
                Matcher matcher;
                ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                ano2 = data.get(0).toString();
                if(!ano2.equals("")) {
                    ano2 = ano2.toLowerCase();
                    if (ano2.equals("tu")) {
                        ano2 = "2";
                    }
                    if (ano2.equals("six") || ano2.equals("sex")) {
                        ano2 = "6";
                    }
                    if (ano2.equals("tree") || ano2.equals("free") || ano2.equals("three")) {
                        ano2 = "3";
                    }
                    if (ano2.equals("four") || ano2.equals("phore") || ano2.equals("pour") || ano2.equals("for") || ano2.equals("our") || ano2.equals("aur")) {
                        ano2 = "4";
                    }
                    if (ano2.equals("zero")) {
                        ano2 = "0";
                    }
                    if (ano2.equals("seven") || ano2.equals("saaven") || ano2.equals("saavn")) {
                        ano2 = "7";
                    }
                    pattern = Pattern.compile(regex);
                    //Creating a Matcher object
                    matcher = pattern.matcher(ano2);


                    while (i <= 12) {
                        if (matcher.matches()) {
                            ano1.setText(ano1.getText().toString() + ano2);
                            i++;
                            if (i <= 12) {
                                //Toast.makeText(RegisterActivity.this,i,Toast.LENGTH_LONG).show();
                                promptAadhar();
                                break;
                            }
                        }
                        else {
                            textToSpeech.speak("Tell digit" + i + "properly", TextToSpeech.QUEUE_ADD, null);
                            i--;
                            promptAadhar();
                            i++;
                            //Toast.makeText(RegisterActivity.this, ano2, Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    if (i == 13) {
                        spell(ano1.getText().toString());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aadharcheck();
                            }
                        }, 6000);
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
        },2000);

    }



    public void listenForPassword()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your new password",TextToSpeech.QUEUE_FLUSH,null);
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
                        listenForPassword();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                pass=data.get(0).toString();
                if(!pass.equals(""))
                {
                    pass  = Character.toLowerCase(pass.charAt(0)) +
                            (pass.length() > 1 ? pass.substring(1) : "");

                    pass = pass.replaceAll("\\s+","");
                    if(pass.length()>=4 && pass.length()<=8){
                        textToSpeech.speak("Your password is"+pass,TextToSpeech.QUEUE_ADD,null);
                        spell(pass);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmpwd();
                            }
                        },4000);

                    }
                    else{
                        textToSpeech.speak("password length should be between 4 to 8 characters",TextToSpeech.QUEUE_ADD,null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listenForPassword();
                            }
                        },3000);

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
    public void confirmpwd()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Do you want to re enter or continue ",TextToSpeech.QUEUE_ADD,null);
            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("If you want to reenter your password say password",TextToSpeech.QUEUE_ADD,null);
            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("otherwise say continue",TextToSpeech.QUEUE_ADD,null);
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
                        confirmpwd();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String a = data.get(0).toString();
                if(!a.equals("")) {
                    a = a.toLowerCase();
                    a = a.replaceAll("\\s+", "");
                    if (a.equals("password")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                ps.setText("");
                                promptPass();
                            }
                        }, 2000);
                    } else if (a.equals("continue")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                reset();
                            }
                        },1000);
                    }
                    else {
                        textToSpeech.speak("Invalid command",TextToSpeech.QUEUE_ADD,null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmpwd();
                            }
                        },1000);
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
        },10000);
    }

    public void promptPass()
    {

        //textToSpeech.speak("SPELL",TextToSpeech.QUEUE_ADD,null);

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
                        promptPass();
                    }
                },4000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                pwd = data.get(0).toString();
                String regex = "^[a-zA-z]*$";
                Pattern pattern;
                Matcher matcher;
                StringBuilder output = new StringBuilder(100);
                //String regex = "^[0-9]{1}$";
                //Pattern pattern;
                //Matcher matcher;
                //ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //regno2 = data.get(0).toString();
                if (!pwd.equals("")) {
                    // regno2 = regno2.substring(0, 1).toUpperCase() + regno2.substring(1);
                    pwd = pwd.toLowerCase();
                    if (pwd.equals("yeah") || pwd.equals("yea") || pwd.equals("yeh") || pwd.equals("yah") || pwd.equals("ye") || pwd.equals("ae")) {
                        pwd = "a";
                    }
                    if(pwd.equals("bee")|| pwd.equals("be")){
                        pwd = "b";
                    }
                    if (pwd.equals("see") || pwd.equals("sea")) {
                        pwd = "c";
                    }
                    if (pwd.equals("ee")) {
                        pwd = "e";
                    }
                    if (pwd.equals("ji")) {
                        pwd = "g";
                    }
                    if (pwd.equals("haj") || pwd.equals("sach")) {
                        pwd = "h";
                    }
                    if (pwd.equals("jay") || pwd.equals("jey")) {
                        pwd = "j";
                    }
                    if (pwd.equals("k") || pwd.equals("ke") || pwd.equals("kay")) {
                        pwd = "k";
                    }
                    if (pwd.equals("yell") || pwd.equals("el") || pwd.equals("yel")) {
                        pwd = "l";
                    }
                    if (pwd.equals("yam") || pwd.equals("yum") || pwd.equals("em")) {
                        pwd = "m";
                    }
                    if (pwd.equals("en") || pwd.equals("yen") || pwd.equals("yan")) {
                        pwd = "n";
                    }
                    if (pwd.equals("ov") || pwd.equals("woh") || pwd.equals("oh") || pwd.equals("ohh") || pwd.equals("oo")) {
                        pwd = "o";
                    }
                    if (pwd.equals("queue") || pwd.equals("queen") || pwd.equals("kyon") || pwd.equals("kyu") || pwd.equals("qu") || pwd.equals("qew") || pwd.equals("que")) {
                        pwd = "q";
                    }
                    if (pwd.equals("our") || pwd.equals("aur") || pwd.equals("are")) {
                        pwd = "r";
                    }
                    if (pwd.equals("yes") || pwd.equals("yas") || pwd.equals("yaas") || pwd.equals("yash") || pwd.equals("yush") || pwd.equals("sh")) {
                        pwd = "s";
                    }
                    if (pwd.equals("tea") || pwd.equals("tee")) {
                        pwd = "t";
                    }
                    if (pwd.equals("you") || pwd.equals("yu")) {
                        pwd = "u";
                    }
                    if (pwd.equals("we")) {
                        pwd = "v";
                    }
                    if (pwd.equals("double")) {
                        pwd = "w";
                    }
                    if (pwd.equals("ex") || pwd.equals("yex") || pwd.equals("ishq")) {
                        pwd = "x";
                    }
                    if (pwd.equals("why") || pwd.equals("oye") || pwd.equals("oii")) {
                        pwd = "y";
                    }
                    if (pwd.equals("set") || pwd.equals("jet") || pwd.equals("izzat") || pwd.equals("izzet") || pwd.equals("zet") || pwd.equals("zee")) {
                        pwd = "z";
                    }


                         /*if (regno2.equals("tree") || regno2.equals("free") || regno2.equals("three")) {
                            regno2 = "3";
                        }
                        if (regno2.equals("four") || regno2.equals("phore") || regno2.equals("pour") || regno2.equals("for") || regno2.equals("our") || regno2.equals("aur")) {
                            regno2 = "4";
                        }
                        if (regno2.equals("zero")) {
                            regno2 = "0";
                        }
                        if (regno2.equals("seven") || regno2.equals("saaven") || regno2.equals("saavn")) {
                            regno2 = "7";
                        }*/
                    pattern = Pattern.compile(regex);
                    //Creating a Matcher object
                    matcher = pattern.matcher(pwd);

                    while (!pwd.equals("finish")) {

                        if (matcher.matches()) {
                            if (!pwd.equals("finish")) {
                                output.append(pwd);
                                ps.setText(ps.getText().toString() + output.toString());
                                promptPass();
                                break;
                            }
                        } else {
                            textToSpeech.speak("Tell a character", TextToSpeech.QUEUE_ADD, null);
                            promptPass();
                            break;
                        }

                    }
                    if (pwd.equals("finish")) {

                        spell(ps.getText().toString());
                        pwd1 = ps.getText().toString();
                        pwd1 = pwd1.toLowerCase();
                        ps.setText("");
                        ps.setText(pwd1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmpwd();
                            }

                        }, 4000);
                    }
                }



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
        },2000);

    }

    public void spell(String number){
        for(int i=0;i<number.length();i++){
            textToSpeech.speak(Character.toString(number.charAt(i)),TextToSpeech.QUEUE_ADD,null);
        }
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
            listenForAadhar();
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
            listenForAadhar();
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