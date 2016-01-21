package com.example.ct.audiocapture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
//import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class MainActivity extends Activity {
    //Button stop;
    //FloatingActionButton myFab;
    ImageButton stop, play, record, myFab;
    private MediaRecorder myAudioRecorder;
    private MediaPlayer m = new MediaPlayer();
    private String outputFile = null;
    private String outputTemp = null;
    private boolean currentlyRecording = false;
    private boolean hasRecordedOnce = false;

    private Switch myLoopSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loop switch stuff. Leaving things that don't hurt. Commenting those that do.
        //It'd be nice to eventually get this working.
        //UPDATE: Got the loop switch working. Just ignore this stuff for now, don't delete though
        myLoopSwitch = (Switch) findViewById(R.id.myLoopSwitch);
        myLoopSwitch.setChecked(false);
        myLoopSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                /*
                if (isChecked) {
                    m.setLooping(true);
                }
                else {
                    m.setLooping(false);
                }
                */
            }
        });

        //Creates the two directories where audio files will be saved
        File vDir = new File(Environment.getExternalStorageDirectory()+
                File.separator+"Rithum_Memo's");
        vDir.mkdirs();
        File aDir = new File(Environment.getExternalStorageDirectory()+
                File.separator+"Rithum_Audio");
        aDir.mkdirs();


        //Necessary to get an image as button
        //myFab=(FloatingActionButton)findViewById(R.id.myFab);
        myFab=(ImageButton)findViewById(R.id.myFab);
        record=(ImageButton)findViewById(R.id.myButton);
        play=(ImageButton)findViewById(R.id.myPlayButton);
        stop=(ImageButton)findViewById(R.id.myStopButton);
        //newRecEn=(ImageButton)findViewById(R.id.myButton1);

        stop.setEnabled(false);
        play.setEnabled(false);

        //getRecordingType();

        getFileName();

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //myAudioRecorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());


        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //What if we haven't set the file name?
                if (outputFile == null) {
                    recreate();
                } else {

                    //Show overwrite menu if track already recorded
                    if (hasRecordedOnce) {
                        overwriteDialog();
                    } else {
                        hasRecordedOnce = true;

                        currentlyRecording = true;

                        //ImageButton Stuff
                        record.setImageResource(R.drawable.recbuttenabled);
                        play.setImageResource(R.drawable.playbuttenabled);

                        try {
                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        record.setEnabled(false);
                        stop.setEnabled(true);
                        play.setEnabled(false);
                        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Stop looping if we are stopping track playback. Has no effect on recording
                if (m.isLooping() == true) {
                    myLoopSwitch.setChecked(false);
                    m.setLooping(false);
                }

                else if (currentlyRecording) {
                    myAudioRecorder.stop();
                    myAudioRecorder.reset();

                    //Boiler plate to allow us to record over existing track OR play track upon stop
                    myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    //myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    //myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    //myAudioRecorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());

                    currentlyRecording = false;
                }

                //ImageButton Stuff
                record.setImageResource(R.drawable.recbuttdisabled);
                play.setImageResource(R.drawable.playbuttdisabled);

                stop.setEnabled(false);
                play.setEnabled(true);
                record.setEnabled(true); //originally set to false

                //Trying to send "audio recorded successfully" message
                if (currentlyRecording) {
                    Toast.makeText(getApplicationContext(), "Track Successfully Recorded",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
                }
                currentlyRecording = false;
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {


                //Just leave this here for now John
                //******************
               // myLoopSwitch.setChecked(true);
                //******************

                currentlyRecording = false;

                //ImageButton Stuff
                play.setImageResource(R.drawable.playbuttenabled);

                //Don't want to record and play at the same time here
                record.setEnabled(false);

                m = new MediaPlayer();

                play.setEnabled(false);
                stop.setEnabled(true);

                try {
                    m.setDataSource(outputFile);
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                if (myLoopSwitch.isChecked()){
                    m.setLooping(true);
                }

                try {
                    m.prepare();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_SHORT).show();
            }
        });

        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
    //            Toast.makeText(getBaseContext(), "FAB Clicked", Toast.LENGTH_SHORT).show();
            }
        });



    }


    public double getAmplitude(){
        return myAudioRecorder.getMaxAmplitude() /100;
    }


    public void getRecordingType(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Type Of Recording");

        alert.setPositiveButton("Voice Memo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //outputFile = vPath;
            }
        });
    }

    public void overwriteDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Are you sure you wish to overwrite?");

        alert.setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Toast.makeText(getApplicationContext(), "Track will be overwritten",
                        Toast.LENGTH_SHORT).show();
                hasRecordedOnce = false;
                record.performClick();
            }
        });

        alert.setNegativeButton("Save recording and exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                System.exit(0);
                Log.w("audiocapture", "user declined to overwrite and exited app");
            }
        });
        alert.show();
    }

    public void getFileName(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter your session name and type");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Memo (low quality)", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                outputTemp = input.getText().toString();

                //Ok so outputTemp is definitely being stored properly, this log proves it
                Log.w("audiocapture", outputTemp);

                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Rithum_Memo's/" + outputTemp + ".mp4";
                myAudioRecorder.setOutputFile(outputFile);
            }
        });

        alert.setNeutralButton("Music (high quality)", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                outputTemp = input.getText().toString();

                //Change Audio Quality (for the better)
                myAudioRecorder.setAudioEncodingBitRate(128000);
                myAudioRecorder.setAudioSamplingRate(44100);

                //Ok so outputTemp is definitely being stored properly, this log proves it
                Log.w("audiocapture", outputTemp);

                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Rithum_Audio/" + outputTemp + ".mp4";
                myAudioRecorder.setOutputFile(outputFile);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //
                System.exit(0);
                Log.w("audiocapture", "user hit cancel");
            }
        });

        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}