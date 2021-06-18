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
import android.os.Parcelable;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextView regno;
    private TextView name;
    private TextView password;
    private TextView day1;
    String user2;
    private String day,month,year,dob,gender1,regno1,regno2,pass,cpass,user,std1,status,pwd,pwd1;
    int count=0,co=0,st,cnt=0,cnt1=0;
    private TextView month1;
    private TextView year1;
    TextView std;
    private TextView gender;
    private RadioButton female,male;
    private RadioGroup mOption;
    private CheckBox tamil,english;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    public String ip,user1;
    private int d,m;
    int i=1;
    private String [] y = {"1990","1991","1992","1993","1994","1995","1996","1997","1998","1999","2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015"};
    private String [] da = {"six","1","2","3","4","5","6","7","8","9","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","i","ii","iii","iv","v","vi","vii","viii","ix","x","xi","xii",
            "xiii","xiv","xv","xvi","xvii","xviii","xix","xx","xxi","xxii","xxiii","xxiv","xxv",
            "xxvi","xxvii","xxviii","xxix","xxx","xxxi"};
    private String [] mo = {"six","1","2","3","4","5","6","7","8","9","01","02","03","04","05","06","07","08","09","10","11","12","i","ii","iii","iv","v","vi","vii","viii","ix","x","xi","xii"};
    private String [] s = {"1","2","3","4","5","6","7","8","9","10","11","12"};
    private FrameLayout name_frame,aadhar_frame,password_frame,regno_frame,address_frame,class_room_frame,pincode_frame,mobile_frame,date_frame,month_frame,year_frame,sex_frame;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},200);
        textToSpeech=new TextToSpeech(this,this);
        Intent Extra = getIntent();
        status = Extra.getStringExtra("status");
        ip = Extra.getStringExtra("ip");
        regno = (TextView)findViewById(R.id.regno);
        name = (TextView)findViewById(R.id.name);
        password = (TextView)findViewById(R.id.password);
        day1 = (TextView)findViewById(R.id.date);
        month1 = (TextView)findViewById(R.id.month);
        year1 = (TextView)findViewById(R.id.year);
        std = (TextView)findViewById(R.id.std);
        gender = (TextView)findViewById(R.id.gender);
        female = (RadioButton) findViewById(R.id.female);
        male = (RadioButton) findViewById(R.id.male);
        mOption = (RadioGroup) findViewById(R.id.checked);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    public static void exitApplication(Context context)
    {
        Intent intent = new Intent(context, RegisterActivity.class);

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
                RegisterActivity.exitApplication(context);
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
        if(i== TextToSpeech.SUCCESS)
        {
            textToSpeech.setLanguage(Locale.ENGLISH);
            textToSpeech.setPitch(1);
            textToSpeech.setSpeechRate(1);
        }
    }

    public void listenForRegno()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your register number",TextToSpeech.QUEUE_FLUSH,null);
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
                        listenForRegno();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                String regex="\\d{7}";
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                regno1 = data.get(0).toString();
                if(!regno1.equals(""))
                {
                    regno1 = regno1.replaceAll("\\s+","");
                    regno1 =regno1.toUpperCase();
                    if(regno1.length()==7)
                    {
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(regno1);
                        if(matcher.matches()) {
                            regno.setText(regno1);
                            spell(regno1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    confirmRegno();
                                }
                            },4000);
                        }
                        else{
                            textToSpeech.speak("Please enter your register number properly",TextToSpeech.QUEUE_ADD,null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForRegno();
                                }
                            },1500);
                        }

                    }
                    else{
                        textToSpeech.speak("Please enter your register number properly",TextToSpeech.QUEUE_ADD,null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listenForRegno();
                            }
                        },1500);
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

    public void confirmRegno()
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
                textToSpeech.speak("say register to reenter your register number",TextToSpeech.QUEUE_ADD,null);
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
                        confirmRegno();
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

                    if (a.equals("register")) {
                        while (cnt1 <= 2) {
                            if (cnt1 < 1) {
                                regno.setText("");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        listenForRegno();
                                    }
                                },2000);

                                cnt1++;
                                break;
                            }

                            if (cnt1 == 1) {
                                textToSpeech.speak("Spell your register number digit by digit", TextToSpeech.QUEUE_ADD, null);
                                regno.setText("");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        promptRegno();
                                    }
                                },2000);
                                break;
                            }
                            if (cnt1 == 2) {
                                textToSpeech.speak("Close the app and try again shake the device to close app", TextToSpeech.QUEUE_ADD, null);
                                break;
                            }
                        }
                    }

                    else if(a.equals("continue")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                regno.setText(regno.getText().toString());
                                regnoCheck();
                            }
                        },1000);

                    }
                    else {
                        textToSpeech.speak("Invalid command",TextToSpeech.QUEUE_ADD,null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmRegno();
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

    public void promptRegno()
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
                        promptRegno();
                    }
                },4000);


            }

            @Override
            public void onResults(Bundle results) {
                String regex = "^[0-9]{1}$";
                Pattern pattern;
                Matcher matcher;
                ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                regno2 = data.get(0).toString();
                if(!regno2.equals("")) {
                    regno2 = regno2.toLowerCase();
                    if (regno2.equals("tu")) {
                        regno2 = "2";
                    }
                    if (regno2.equals("six") || regno2.equals("sex")) {
                        regno2 = "6";
                    }
                    if (regno2.equals("tree") || regno2.equals("free") || regno2.equals("three")) {
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
                    }
                    pattern = Pattern.compile(regex);
                    //Creating a Matcher object
                    matcher = pattern.matcher(regno2);


                    while (i <= 7) {
                        if (matcher.matches()) {
                            regno.setText(regno.getText().toString() + regno2);
                            i++;
                            if (i <= 7) {
                                //Toast.makeText(RegisterActivity.this,i,Toast.LENGTH_LONG).show();
                                promptRegno();
                                break;
                            }
                        }
                        else {
                            textToSpeech.speak("Tell digit" + i + "properly", TextToSpeech.QUEUE_ADD, null);
                            i--;
                            promptRegno();
                            i++;
                            //Toast.makeText(RegisterActivity.this, regno2, Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    if (i == 8) {
                        spell(regno.getText().toString());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmRegno();
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


    public void listenForName()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your name",TextToSpeech.QUEUE_ADD,null);
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
                        listenForName();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                user = data.get(0).toString();
                if(!user.equals(""))
                {
                    char[] charArray = user.toCharArray();
                    boolean foundSpace = true;

                    for(int i = 0; i < charArray.length; i++) {

                        // if the array element is a letter
                        if(Character.isLetter(charArray[i])) {

                            // check space is present before the letter
                            if(foundSpace) {

                                // change the letter into uppercase
                                charArray[i] = Character.toLowerCase(charArray[i]);
                                foundSpace = false;
                            }
                        }

                        else {
                            // if the new character is not character
                            foundSpace = true;
                        }
                    }
                    user = String.valueOf(charArray);
                    user = user.substring(0, 1).toUpperCase() + user.substring(1);
                    user = user.replaceAll("\\s+","");
                    textToSpeech.speak("The name you entered is "+user,TextToSpeech.QUEUE_ADD,null);
                    name.setText(user);
                    spell(user);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            confirmusername();
                        }
                    },8000);

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
                    if (pwd.equals("yeah") || pwd.equals("yea") ||pwd.equals("yeh")||pwd.equals("yah")||pwd.equals("ye")||pwd.equals("ae")) {
                        pwd = "a";
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
                    if (pwd.equals("en") || pwd.equals("yen") ||pwd.equals("yan")) {
                        pwd = "n";
                    }
                    if (pwd.equals("ov") || pwd.equals("woh") || pwd.equals("oh") || pwd.equals("ohh") || pwd.equals("oo")) {
                        pwd = "o";
                    }
                    if (pwd.equals("queue") || pwd.equals("queen") || pwd.equals("kyon") || pwd.equals("kyu") || pwd.equals("qu")||pwd.equals("qew")||pwd.equals("que")) {
                        pwd = "q";
                    }
                    if (pwd.equals("our") || pwd.equals("aur") || pwd.equals("are")) {
                        pwd = "r";
                    }
                    if (pwd.equals("yes") || pwd.equals("yas") || pwd.equals("yaas")||pwd.equals("yash")||pwd.equals("yush")||pwd.equals("sh")) {
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
                    if (pwd.equals("set") || pwd.equals("jet") || pwd.equals("izzat")|| pwd.equals("izzet")||pwd.equals("zet")||pwd.equals("zee")) {
                        pwd = "z";
                    }

                    pattern = Pattern.compile(regex);
                    //Creating a Matcher object
                    matcher = pattern.matcher(pwd);

                    while (!pwd.equals("finish")) {

                        if (matcher.matches()) {
                            if (!pwd.equals("finish")) {
                                output.append(pwd);
                                password.setText(password.getText().toString() + output.toString());
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

                        spell(password.getText().toString());
                            pwd1 = password.getText().toString();
                            pwd1 = pwd1.toLowerCase();
                            password.setText("");
                            password.setText(pwd1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    confirmpwd();
                                }
                            },4000);

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


    public void listenForPassword()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your password",TextToSpeech.QUEUE_FLUSH,null);
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
                        password.setText(pass);
                        textToSpeech.speak("Your password is",TextToSpeech.QUEUE_ADD,null);
                        spell(pass);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                confirmpwd();
                            }
                        },5000);
                    }
                    else{
                        textToSpeech.speak("password length should be between 4 to 8 characters",TextToSpeech.QUEUE_ADD,null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listenForPassword();
                            }
                        },5000);
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
        },7000);
    }
    public void promptName()
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
                        promptName();
                    }
                },4000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                user1 = data.get(0).toString();
                String regex = "^[a-zA-Z]*$";
                Pattern pattern;
                Matcher matcher;
                StringBuilder output = new StringBuilder(100);
                //String regex = "^[0-9]{1}$";
                //Pattern pattern;
                //Matcher matcher;
                //ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //regno2 = data.get(0).toString();
                if (!user1.equals("")) {
                    // regno2 = regno2.substring(0, 1).toUpperCase() + regno2.substring(1);
                    user1 = user1.toLowerCase();
                    if (user1.equals("yeah") || user1.equals("yea") || user1.equals("yeh")||user1.equals("yah")||user1.equals("ye")||user1.equals("ae")) {
                        user1 = "a";
                    }
                    if (user1.equals("see") || user1.equals("sea")) {
                        user1 = "c";
                    }
                    if (user1.equals("ee")) {
                        user1 = "e";
                    }
                    if (user1.equals("ji")) {
                        user1 = "g";
                    }
                    if (user1.equals("haj") || user1.equals("sach")) {
                        user1 = "h";
                    }
                    if (user1.equals("jay") || user1.equals("jey")) {
                        user1 = "j";
                    }
                    if (user1.equals("k") || user1.equals("ke") || user1.equals("kay")) {
                        user1 = "k";
                    }
                    if (user1.equals("yell") || user1.equals("el") || user1.equals("yel")) {
                        user1 = "l";
                    }
                    if (user1.equals("yam") || user1.equals("yum") || user1.equals("em")) {
                        user1 = "m";
                    }
                    if (user1.equals("en") || user1.equals("yen") || user1.equals("yan")) {
                        user1 = "n";
                    }
                    if (user1.equals("ov") || user1.equals("woh") || user1.equals("oh") || user1.equals("ohh") || user1.equals("oo")) {
                        user1 = "o";
                    }
                    if (user1.equals("queue") || user1.equals("queen") || user1.equals("kyon") || user1.equals("kyu") || user1.equals("qu")||user1.equals("qew")||user1.equals("que")) {
                        user1 = "q";
                    }
                    if (user1.equals("our") || user1.equals("aur") || user1.equals("are")) {
                        user1 = "r";
                    }
                    if (user1.equals("yes") || user1.equals("yas") || user1.equals("yaas")||user1.equals("yash")||user1.equals("yush")||user1.equals("sh")) {
                        user1 = "s";
                    }
                    if (user1.equals("tea") || user1.equals("tee")) {
                        user1 = "t";
                    }
                    if (user1.equals("you") || user1.equals("yu")) {
                        user1 = "u";
                    }
                    if (user1.equals("we")) {
                        user1 = "v";
                    }
                    if (user1.equals("double")) {
                        user1 = "w";
                    }
                    if (user1.equals("ex") || user1.equals("yex") || user1.equals("ishq")) {
                        user1 = "x";
                    }
                    if (user1.equals("why") || user1.equals("oye") || user1.equals("oii")) {
                        user1 = "y";
                    }
                    if (user1.equals("set") || user1.equals("jet") || user1.equals("izzat")|| user1.equals("izzet")||user1.equals("zet")||user1.equals("zee")) {
                        user1 = "z";
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
                    matcher = pattern.matcher(user1);

                    while (!user1.equals("finish")) {

                        if (matcher.matches()) {
                            if (!user1.equals("finish")) {
                                output.append(user1);
                                name.setText(name.getText().toString() + output.toString());
                                promptName();
                                break;
                            }
                        } else {
                            textToSpeech.speak("Tell a character", TextToSpeech.QUEUE_ADD, null);
                            promptName();
                            break;
                        }

                    }
                    if (user1.equals("finish")) {

                        spell(name.getText().toString());
                        user2 = name.getText().toString();
                        user2 = user2.toLowerCase();
                        user2 = user2.substring(0, 1).toUpperCase() + user2.substring(1);
                        name.setText("");
                        name.setText(user2);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listenForPassword();
                            }
                        },8000);

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

    public void listenForDay()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell day of birth",TextToSpeech.QUEUE_FLUSH,null);
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
                                day1.setText(day);
                                listenForMonth();
                            } else {
                                day1.setText(day);
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
                textToSpeech.speak("Tell month of birth",TextToSpeech.QUEUE_FLUSH,null);
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
                                month1.setText(month);
                                listenForYear();
                            } else {
                                month1.setText(month);
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
                textToSpeech.speak("Tell year of birth",TextToSpeech.QUEUE_FLUSH,null);
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
                year=data.get(0).toString();
                if(!year.equals(""))
                {
                    year = year.replaceAll("\\s", "");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (year.length() == 4) {
                                if (Arrays.asList(y).contains(year)) {
                                    year1.setText(year);
                                    dob = day + "-" + month + "-" + year;
                                    listenForGender();
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

    public void listenForGender()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your gender",TextToSpeech.QUEUE_FLUSH,null);
            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Please say male or female",TextToSpeech.QUEUE_FLUSH,null);
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
                        listenForGender();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String a=data.get(0).toString();
                if(!a.equals(""))
                {
                    a=a.toLowerCase();
                    if(a.equals("female")) {
                        female.setChecked(true);
                        gender1="female";
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(status.equals("student")) {
                                    listenForStd();
                                }
                                else if(status.equals("teacher")){
                                    registerPage1();
                                }
                                else if(status.equals("admin")){
                                    registerPage1();
                                }
                            }
                        },3000);

                    }
                    else if(a.equals("male")||a.equals("mail")||a.equals("maile")){
                        gender1="male";
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(status.equals("student")) {
                                    listenForStd();
                                }
                                else if(status.equals("teacher")){
                                    registerPage1();
                                }
                            }
                        },3000);

                    }
                    else {
                        textToSpeech.speak("Please enter Female or Male", TextToSpeech.QUEUE_ADD, null);
                        listenForGender();

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
        },9000);
    }

    public void listenForStd()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Tell your class",TextToSpeech.QUEUE_FLUSH,null);
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
                        listenForStd();
                    }
                },3000);


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList data=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                std1=data.get(0).toString();
                if(!std1.equals(""))
                {
                    if(Arrays.asList(s).contains(std1)){
                        st = Integer.parseInt(std1);
                        if(st>0 && st<13){
                            std.setText(std1);
                            registerPage1();
                        }
                        else{
                            textToSpeech.speak("Say valid standard", TextToSpeech.QUEUE_ADD, null);
                            listenForStd();
                        }
                    }
                    else{
                        textToSpeech.speak("Say valid standard", TextToSpeech.QUEUE_ADD, null);
                        listenForStd();
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


    public void exist()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Do you want to reset passsword or login",TextToSpeech.QUEUE_ADD,null);
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
                textToSpeech.speak("If you want to login say login",TextToSpeech.QUEUE_ADD,null);
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
                        exist();
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
                                    Intent i = new Intent(RegisterActivity.this,ResetActivity.class);
                                    i.putExtra("regno", regno.getText().toString());
                                    i.putExtra("status", status);
                                    i.putExtra("ip",ip);
                                    startActivity(i);
                                }
                            },3000);
                            break;
                        }
                        else if(a.equals("login")){
                            textToSpeech.speak("Login started,Wait for a minute", TextToSpeech.QUEUE_ADD, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(RegisterActivity.this,MainActivity.class);
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
                                textToSpeech.speak("Please enter reset or login", TextToSpeech.QUEUE_ADD, null);
                                exist();
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
        },11000);

    }

    public void spell(String number){
        for(int i=0;i<number.length();i++){
            textToSpeech.speak(Character.toString(number.charAt(i)),TextToSpeech.QUEUE_ADD,null);
        }
    }
    public void confirmusername()
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
                textToSpeech.speak("If you want to reenter your name say name",TextToSpeech.QUEUE_ADD,null);
            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("otherwise  say continue",TextToSpeech.QUEUE_ADD,null);
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
                        confirmusername();
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

                        if (a.equals("name")) {
                            while (cnt <= 1) {
                                if (cnt < 1) {
                                    name.setText("");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            listenForName();
                                        }
                                    },2000);

                                    cnt++;
                                    break;
                                }
                                if (cnt == 1) {
                                    textToSpeech.speak("Spell your name", TextToSpeech.QUEUE_ADD, null);
                                    name.setText("");
                                    promptName();
                                    //spellYourName();
                                    break;
                                }
                            }
                        }

                        else if(a.equals("continue")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    name.setText(name.getText().toString());
                                    listenForPassword();
                                }
                            },1000);

                        }
                        else {
                            textToSpeech.speak("Invalid command",TextToSpeech.QUEUE_ADD,null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    confirmusername();
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
                                password.setText("");
                                promptPass();
                            }
                        }, 2000);
                    } else if (a.equals("continue")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                listenForDay();
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

    public void registerPage1(){
        Intent i = new Intent(RegisterActivity.this,RegisterActivity1.class);
        i.putExtra("regno",regno.getText().toString());
        i.putExtra("name",name.getText().toString());
        i.putExtra("password",password.getText().toString());
        i.putExtra("dob",dob);
        i.putExtra("gender",gender1);
        i.putExtra("std",std1);
        i.putExtra("status",status);
        i.putExtra("ip",ip);
        startActivity(i);
    }

    public void regnoCheck() {
        if (status.equals("student")) {
            String url = "http://"+ip+"/student/check";
            //Toast.makeText(RegisterActivity.this,url,Toast.LENGTH_LONG).show();
            //Toast.makeText(RegisterActivity.this,regno.getText().toString(),Toast.LENGTH_LONG).show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);
                        if (array.get("status").equals("success")) {
                            textToSpeech.speak("User already exists", TextToSpeech.QUEUE_ADD, null);
                            textToSpeech.speak("User not allowed to register with same register number", TextToSpeech.QUEUE_ADD, null);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    exist();
                                }
                            }, 6000);
                        }
                        else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listenForName();
                                }
                            }, 3000);

                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("regno", regno.getText().toString());
                    //map.put("name", name.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if (status.equals("teacher")) {
            String url = "http://"+ip+"/teacher/check";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);

                            if (array.get("status").equals("failed")) {
                                listenForName();

                            } else {
                                    textToSpeech.speak("User already exists", TextToSpeech.QUEUE_ADD, null);
                                    textToSpeech.speak("User not allowed to register with same register number", TextToSpeech.QUEUE_ADD, null);
                                    exist();
                                }

                            }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("regId", regno.getText().toString());
                    //map.put("name", name.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        else if(status.equals("admin")) {
            String url ="http://"+ip+"/admin/check";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject array = new JSONObject(response);

                            if (array.get("status").equals("failed")) {
                                listenForName();
                            } else {
                                    textToSpeech.speak("User already exists", TextToSpeech.QUEUE_ADD, null);
                                    textToSpeech.speak("User not allowed to register with same register number", TextToSpeech.QUEUE_ADD, null);
                                    exist();
                                }
                        }
                     catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("admin_id", regno.getText().toString());
                    //map.put("name", name.getText().toString());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
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
    public void pos(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Now we are in registration page",TextToSpeech.QUEUE_ADD,null);
                if(status.equals("student"))
                {
                    status="student";

                    regno.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    day1.setVisibility(View.VISIBLE);
                    month1.setVisibility(View.VISIBLE);
                    year1.setVisibility(View.VISIBLE);
                    gender.setVisibility(View.VISIBLE);
                    std.setVisibility(View.VISIBLE);
                    listenForRegno();
                }
                else if(status.equals("teacher"))
                {
                    status="teacher";

                    regno.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    day1.setVisibility(View.VISIBLE);
                    month1.setVisibility(View.VISIBLE);
                    year1.setVisibility(View.VISIBLE);
                    gender.setVisibility(View.VISIBLE);
                    listenForRegno();
                }
                else if(status.equals("admin"))
                {
                    status="admin";
                    regno.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    day1.setVisibility(View.VISIBLE);
                    month1.setVisibility(View.VISIBLE);
                    year1.setVisibility(View.VISIBLE);
                    gender.setVisibility(View.VISIBLE);
                    listenForRegno();
                }
                else if(status.equals("super admin"))
                {
                    status="super admin";
                }
                else
                {
                    textToSpeech.speak("No option found",TextToSpeech.QUEUE_FLUSH,null);
                    initialPlaylist();
                }
            }
        },3000);
    }

    private void initialPlaylist() {
        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE != orientation) {
            textToSpeech.speak("Please Change phone from portrait mode to Landscape mode for better experience",TextToSpeech.QUEUE_FLUSH,null);
        }
        else{
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            pos();
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
            pos();
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


