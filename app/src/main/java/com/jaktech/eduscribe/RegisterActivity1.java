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
import android.widget.Toast;

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
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity1 extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private String name,password,dob,gender,std,ano2;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private TextView phno;
    private TextView ano1;
    private String ano;
    private TextView addr;
    private TextView pin;
    private String phone,phone1,regno,add1,add2,pin1,status,pin2;
    public String ip;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Context context;
    int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},200);
        textToSpeech=new TextToSpeech(this,this);

        Intent Extra = getIntent();
        regno = Extra.getStringExtra("regno");
        name = Extra.getStringExtra("name");
        password = Extra.getStringExtra("password");
        dob = Extra.getStringExtra("dob");
        gender = Extra.getStringExtra("gender");
        std = Extra.getStringExtra("std");
        status = Extra.getStringExtra("status");
        ip = Extra.getStringExtra("ip");
        phno=(TextView)findViewById(R.id.phone);
        ano1=(TextView)findViewById(R.id.aadhar);
        addr=(TextView)findViewById(R.id.addr1);
        pin=(TextView)findViewById(R.id.pincode);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    public static void exitApplication(Context context)
    {
        Intent intent = new Intent(context, RegisterActivity1.class);

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
                RegisterActivity1.exitApplication(context);
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
    public void listenForPhone()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your phone number",TextToSpeech.QUEUE_FLUSH,null);
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
                        listenForPhone();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                String regex="\\d{10}";
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                phone=data.get(0).toString();
                if(!phone.equals(""))
                {
                    phone = phone.replaceAll("\\s+","");
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(phone);
                    if(matcher.matches()){
                        phno.setText(phone);
                        spell(phone);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmPhone();
                            }
                        }, 6000);
                    }
                    else {
                        textToSpeech.speak("Enter a valid phone number", TextToSpeech.QUEUE_ADD, null);
                        listenForPhone();
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


    public void confirmPhone()
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
                textToSpeech.speak("say phone to reenter your phone number",TextToSpeech.QUEUE_ADD,null);
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
                        confirmPhone();
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

                    if (a.equals("phone")) {


                                textToSpeech.speak("Spell your phone number digit by digit", TextToSpeech.QUEUE_ADD, null);
                                phno.setText("");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        promptPhone();
                                    }
                                },2000);

                        }


                    else if(a.equals("continue")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                phno.setText(phno.getText().toString());
                                phnocheck();
                            }
                        },1000);

                    }
                    else {
                        textToSpeech.speak("Invalid command",TextToSpeech.QUEUE_ADD,null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmPhone();
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

    public void promptPhone()
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
                        promptPhone();
                    }
                },4000);


            }

            @Override
            public void onResults(Bundle results) {
                String regex = "^[0-9]{1}$";
                Pattern pattern;
                Matcher matcher;
                ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                phone1 = data.get(0).toString();
                if(!phone1.equals("")) {
                    phone1 = phone1.toLowerCase();
                    if (phone1.equals("tu")) {
                        phone1 = "2";
                    }
                    if (phone1.equals("six") || phone1.equals("sex")) {
                        phone1 = "6";
                    }
                    if (phone1.equals("tree") || phone1.equals("free") || phone1.equals("three")) {
                        phone1 = "3";
                    }
                    if (phone1.equals("four") || phone1.equals("phore") || phone1.equals("pour") || phone1.equals("for") || phone1.equals("our") || phone1.equals("aur")) {
                        phone1 = "4";
                    }
                    if (phone1.equals("zero")) {
                        phone1 = "0";
                    }
                    if (phone1.equals("seven") || phone1.equals("saaven") || phone1.equals("saavn")) {
                        phone1 = "7";
                    }
                    pattern = Pattern.compile(regex);
                    //Creating a Matcher object
                    matcher = pattern.matcher(phone1);


                    while (i <=10) {
                        if (matcher.matches()) {
                            phno.setText(phno.getText().toString() + phone1);
                            i++;
                            if (i <= 10) {
                                //Toast.makeText(RegisterActivity.this,i,Toast.LENGTH_LONG).show();
                                promptPhone();
                                break;
                            }
                        }
                        else {
                            textToSpeech.speak("Tell digit" + i + "properly", TextToSpeech.QUEUE_ADD, null);
                            i--;
                            promptPhone();
                            i++;
                            //Toast.makeText(RegisterActivity.this, phone1, Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    if (i == 11) {
                        spell(phno.getText().toString());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                phnocheck();
                            }
                        }, 7000);
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
                        }, 8000);
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

                        String aadhar  = ano1.getText().toString().replaceAll("....","$0 ");
                        ano1.setText(aadhar);
                        spell(aadhar);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aadharcheck();
                            }
                        }, 8000);
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


    public void listenForAddress1()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your door number",TextToSpeech.QUEUE_FLUSH,null);
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
                        listenForAddress1();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                add1=data.get(0).toString();
                if(!add1.equals("")) {
                    add1=add1.replaceAll("\\s","");
                    add1=add1.replaceAll("by","/");
                    add1=add1.replaceAll("buy","/");
                    add1=add1.replaceAll("boy","/");
                    listenForAddress2();
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

    public void listenForAddress2()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your street name",TextToSpeech.QUEUE_FLUSH,null);
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
                        listenForAddress2();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                add2=data.get(0).toString();
                if(!add2.equals("")) {
                    char[] charArray = add2.toCharArray();
                    boolean foundSpace = true;

                    for(int i = 0; i < charArray.length; i++) {

                        // if the array element is a letter
                        if(Character.isLetter(charArray[i])) {

                            // check space is present before the letter
                            if(foundSpace) {

                                // change the letter into uppercase
                                charArray[i] = Character.toUpperCase(charArray[i]);
                                foundSpace = false;
                            }
                        }

                        else {
                            // if the new character is not character
                            foundSpace = true;
                        }
                    }

                    // convert the char array to the string
                    add2 = String.valueOf(charArray);
                    addr.setText(add1+", "+add2);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listenForPincode();
                        }
                    }, 2000);

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

    public void listenForPincode()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your area pincode",TextToSpeech.QUEUE_FLUSH,null);
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
                        listenForPincode();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String regex="\\d{6}";
                //ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                pin1=data.get(0).toString();
                if(!pin1.equals("")) {
                    pin1 = pin1.replaceAll("\\s+","");
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(pin1);
                    if(matcher.matches()){
                        pin.setText(pin1);
                        spell(pin1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               confirmpin();
                            }
                        }, 6000);

                    }
                    else {
                        textToSpeech.speak("Enter a valid pincode", TextToSpeech.QUEUE_ADD, null);
                        listenForPincode();
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

    public void confirmpin()
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
                textToSpeech.speak("say pincode to reenter your pincode",TextToSpeech.QUEUE_ADD,null);
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
                        confirmpin();
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

                    if (a.equals("pincode")) {
                                textToSpeech.speak("Spell your pincode digit by digit", TextToSpeech.QUEUE_ADD, null);
                                pin.setText("");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        promptpin();
                                    }
                                },2000);
                        }


                    else if(a.equals("continue")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pin.setText(pin.getText().toString());
                                register();
                                //Toast.makeText(RegisterActivity1.this,pin.getText().toString(),Toast.LENGTH_LONG).show();

                            }
                        },1000);

                    }
                    else {
                        textToSpeech.speak("Invalid command",TextToSpeech.QUEUE_ADD,null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmpin();
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

    public void promptpin()
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
                        promptpin();
                    }
                },4000);


            }

            @Override
            public void onResults(Bundle results) {
                String regex = "^[0-9]{1}$";
                Pattern pattern;
                Matcher matcher;
                ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                pin2 = data.get(0).toString();
                if(!pin2.equals("")) {
                    pin2 = pin2.toLowerCase();
                    if (pin2.equals("tu")) {
                        pin2 = "2";
                    }
                    if (pin2.equals("six") || pin2.equals("sex")) {
                        pin2 = "6";
                    }
                    if (pin2.equals("tree") || pin2.equals("free") || pin2.equals("three")) {
                        pin2 = "3";
                    }
                    if (pin2.equals("four") || pin2.equals("phore") || pin2.equals("pour") || pin2.equals("for") || pin2.equals("our") || pin2.equals("aur")) {
                        pin2 = "4";
                    }
                    if (pin2.equals("zero")) {
                        pin2 = "0";
                    }
                    if (pin2.equals("seven") || pin2.equals("saaven") || pin2.equals("saavn")) {
                        pin2 = "7";
                    }
                    pattern = Pattern.compile(regex);
                    //Creating a Matcher object
                    matcher = pattern.matcher(pin2);


                    while (i <= 6) {
                        if (matcher.matches()) {
                            pin.setText(pin.getText().toString() + pin2);
                            i++;
                            if (i <= 6) {
                                //Toast.makeText(RegisterActivity.this,i,Toast.LENGTH_LONG).show();
                                promptpin();
                                break;
                            }
                        }
                        else {
                            textToSpeech.speak("Tell digit" + i + "properly", TextToSpeech.QUEUE_ADD, null);
                            i--;
                            promptpin();
                            i++;
                            //Toast.makeText(RegisterActivity.this, pin2, Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    if (i == 7) {
                        spell(pin.getText().toString());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                register();
                            }
                        }, 5000);
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


    public void aadharcheck()
    {
        if(status.equals("student")) {
            String url = "http://"+ip+"/student/aadharcheck";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            textToSpeech.speak("aadhar number already exists", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForAadhar();
                                }
                            }, 3000);

                        } else {
                            String url ="http://"+ip+"/admin/aadharcheck";
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject array = new JSONObject(response);
                                        if (array.get("status").equals("success")) {
                                            textToSpeech.speak("aadhar number already exists", TextToSpeech.QUEUE_ADD, null);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listenForAadhar();
                                                }
                                            }, 3000);
                                        } else {
                                            String url = "http://"+ip+"/teacher/aadharcheck";
                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject array = new JSONObject(response);
                                                        if (array.get("status").equals("success")) {
                                                            textToSpeech.speak("aadhar number already exists", TextToSpeech.QUEUE_ADD, null);
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    listenForAadhar();
                                                                }
                                                            }, 5000);
                                                        } else {
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    listenForAddress1();
                                                                }
                                                            }, 8000);
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
                                                    map.put("aadhar", ano1.getText().toString());
                                                    return map;
                                                }
                                            };
                                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                            queue.add(stringRequest);
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
                                    map.put("aadhar", ano1.getText().toString());
                                    return map;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            queue.add(stringRequest);

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
                    map.put("aadhar", ano1.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("teacher")) {
            String url = "http://"+ip+"/teacher/aadharcheck";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            textToSpeech.speak("aadhar number already exists", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForAadhar();
                                }
                            }, 3000);

                        } else {
                            String url = "http://"+ip+"/admin/aadharcheck";
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject array = new JSONObject(response);
                                        if (array.get("status").equals("success")) {
                                            textToSpeech.speak("aadhar number already exists", TextToSpeech.QUEUE_ADD, null);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listenForAadhar();
                                                }
                                            }, 3000);
                                        } else {
                                            String url = "http://"+ip+"/student/aadharcheck";
                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject array = new JSONObject(response);
                                                        if (array.get("status").equals("success")) {
                                                            textToSpeech.speak("aadhar number already exists", TextToSpeech.QUEUE_ADD, null);
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    listenForAadhar();
                                                                }
                                                            }, 5000);
                                                        } else {
                                                            spell(ano);
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    listenForAddress1();
                                                                }
                                                            }, 6000);
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
                                                    map.put("aadhar", ano1.getText().toString());
                                                    return map;
                                                }
                                            };
                                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                            queue.add(stringRequest);
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
                                    map.put("aadhar", ano1.getText().toString());
                                    return map;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            queue.add(stringRequest);

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
                    map.put("aadhar", ano1.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("admin")) {
            String url = "http://"+ip+"/admin/aadharcheck";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            textToSpeech.speak("aadhar number already exists", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForAadhar();
                                }
                            }, 3000);

                        } else {
                            String url = "http://"+ip+"/student/aadharcheck";
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject array = new JSONObject(response);
                                        if (array.get("status").equals("success")) {
                                            textToSpeech.speak("aadhar number already exists", TextToSpeech.QUEUE_ADD, null);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listenForAadhar();
                                                }
                                            }, 3000);
                                        } else {
                                            String url = "http://"+ip+"/teacher/aadharcheck";
                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject array = new JSONObject(response);
                                                        if (array.get("status").equals("success")) {
                                                            textToSpeech.speak("aadhar number already exists", TextToSpeech.QUEUE_ADD, null);
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    listenForAadhar();
                                                                }
                                                            }, 5000);
                                                        } else {

                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    listenForAddress1();
                                                                }
                                                            }, 6000);
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
                                                    map.put("aadhar", ano1.getText().toString());
                                                    return map;
                                                }
                                            };
                                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                            queue.add(stringRequest);
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
                                    map.put("aadhar", ano1.getText().toString());
                                    return map;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            queue.add(stringRequest);

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
                    map.put("aadhar", ano1.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
    }

    public void phnocheck()
    {
        if(status.equals("student")) {
            String url = "http://"+ip+"/student/phnocheck";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            textToSpeech.speak("phone number already exists", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForPhone();
                                }
                            }, 3000);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForAadhar();
                                }
                            }, 5000);
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
                    map.put("mobile", phno.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("teacher")) {
            String url = "http://"+ip+"/teacher/phnocheck";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            textToSpeech.speak("phone number already exists", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForPhone();
                                }
                            }, 5000);
                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForAadhar();
                                }
                            }, 4000);
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
                    map.put("mobile", phno.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("admin")) {
            String url = "http://"+ip+"/admin/phnocheck";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            textToSpeech.speak("phone number already exists", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForPhone();
                                }
                            }, 5000);
                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForAadhar();
                                }
                            }, 2000);
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
                    map.put("mobile",phno.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
    }
    public void register() {
        if (status.equals("student")) {
            String url = "http://"+ip+"/student/registration";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            Log.i("tag", array.toString());
                            textToSpeech.speak("Registered successfully", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(RegisterActivity1.this,MainActivity.class);
                                    i.putExtra("status",status);
                                    i.putExtra("ip",ip);
                                    startActivity(i);
                                }
                            }, 5000);
                        } else {
                            textToSpeech.speak("Registration Failed", TextToSpeech.QUEUE_ADD, null);
                            textToSpeech.speak("Close the app and try again", TextToSpeech.QUEUE_ADD, null);
                            registerPage();
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
                    map.put("name", name);
                    map.put("password", password);
                    map.put("dob", dob);
                    map.put("sex", gender);
                    map.put("class", std);
                    map.put("mobile", phno.getText().toString());
                    map.put("aadhar", ano1.getText().toString());
                    map.put("address", addr.getText().toString());
                    map.put("pincode", pin.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
    else if(status.equals("teacher")) {
            String url = "http://"+ip+"/teacher/registration";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            Log.i("tag", array.toString());
                            textToSpeech.speak("Registered successfully", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(RegisterActivity1.this,MainActivity.class);
                                    i.putExtra("status",status);
                                    i.putExtra("ip",ip);
                                    startActivity(i);
                                }
                            }, 5000);
                        } else {
                            textToSpeech.speak("Registration Failed", TextToSpeech.QUEUE_ADD, null);
                            textToSpeech.speak("Close the app and try again", TextToSpeech.QUEUE_ADD, null);
                            registerPage();
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
                    map.put("name", name);
                    map.put("password", password);
                    map.put("dob", dob);
                    map.put("sex", gender);
                    map.put("mobile", phno.getText().toString());
                    map.put("aadhar", ano1.getText().toString());
                    map.put("address", addr.getText().toString());
                    map.put("pincode", pin.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("admin")) {
            String url = "http://"+ip+"/admin/registration";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            Log.i("tag", array.toString());
                            textToSpeech.speak("Registered successfully", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(RegisterActivity1.this,MainActivity.class);
                                    i.putExtra("status",status);
                                    i.putExtra("ip",ip);
                                    startActivity(i);
                                }
                            }, 5000);
                        } else {
                            textToSpeech.speak("Registration Failed", TextToSpeech.QUEUE_ADD, null);
                            textToSpeech.speak("Close the app and try again", TextToSpeech.QUEUE_ADD, null);
                            registerPage();
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
                    map.put("admin_id", regno );
                    map.put("name", name);
                    map.put("password", password);
                    map.put("dob", dob);
                    map.put("sex", gender);
                    map.put("mobile", phno.getText().toString());
                    map.put("aadhar", ano1.getText().toString());
                    map.put("address", addr.getText().toString());
                    map.put("pincode", pin.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
    }
    public void registerPage(){
        Intent i = new Intent(RegisterActivity1.this,RegisterActivity1.class);
        startActivity(i);
    }

    public void spell(String number){
        for(int i=0;i<number.length();i++){
            textToSpeech.speak(Character.toString(number.charAt(i)),TextToSpeech.QUEUE_ADD,null);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:if(resultCode==RESULT_OK)
            {
                String regex="\\d{10}";
                ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                phone=dat.get(0).toString();
                if(!phone.equals(""))
                {
                    phone = phone.replaceAll("\\s+","");
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(phone);
                    if(matcher.matches()){
                        phno.setText(phone);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                phnocheck();
                            }
                        }, 1000);

                            break;
                        }
                        else {
                            textToSpeech.speak("Enter a valid phone number", TextToSpeech.QUEUE_ADD, null);
                            listenForPhone();
                            break;
                        }
                }
                }
            break;
            case 102:if(resultCode==RESULT_OK)
            {
                String regex="\\d{12}";
                ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                ano=dat.get(0).toString();
                if(!ano.equals("")) {
                    ano = ano.replaceAll("\\s+","");
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(ano);
                    if(matcher.matches()){
                        ano = ano.replaceAll("....","$0 ");
                        ano1.setText(ano);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aadharcheck();
                            }
                        }, 1000);
                        break;
                    }
                    else {
                        textToSpeech.speak("Enter a valid number", TextToSpeech.QUEUE_ADD, null);
                        listenForAadhar();
                        break;
                    }
                }

            }break;
            case 103:if(resultCode==RESULT_OK)
            {
                ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                add1=dat.get(0).toString();
                if(!add1.equals("")) {
                    add1=add1.replaceAll("\\s","");
                    add1=add1.replaceAll("by","/");
                    add1=add1.replaceAll("buy","/");
                    add1=add1.replaceAll("boy","/");
                    listenForAddress2();
                }
            }break;
            case 104:if(resultCode==RESULT_OK)
            {
                ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                add2=dat.get(0).toString();
                if(!add2.equals("")) {
                    char[] charArray = add2.toCharArray();
                    boolean foundSpace = true;

                    for(int i = 0; i < charArray.length; i++) {

                        // if the array element is a letter
                        if(Character.isLetter(charArray[i])) {

                            // check space is present before the letter
                            if(foundSpace) {

                                // change the letter into uppercase
                                charArray[i] = Character.toUpperCase(charArray[i]);
                                foundSpace = false;
                            }
                        }

                        else {
                            // if the new character is not character
                            foundSpace = true;
                        }
                    }

                    // convert the char array to the string
                    add2 = String.valueOf(charArray);
                    addr.setText(add1+", "+add2);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listenForPincode();
                        }
                    }, 2000);
                    break;

                }
            }break;
            case 105:if(resultCode==RESULT_OK)
            {
                String regex="\\d{6}";
                ArrayList dat=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                pin1=dat.get(0).toString();
                if(!pin1.equals("")) {
                    pin1 = pin1.replaceAll("\\s+","");
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(pin1);
                    if(matcher.matches()){
                        pin.setText(pin1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               register();
                            }
                        }, 5000);

                        break;
                    }
                    else {
                        textToSpeech.speak("Enter a valid pincode", TextToSpeech.QUEUE_ADD, null);
                        listenForPincode();
                        break;
                    }
                }
            }break;

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
            listenForPhone();
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
            listenForPhone();
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