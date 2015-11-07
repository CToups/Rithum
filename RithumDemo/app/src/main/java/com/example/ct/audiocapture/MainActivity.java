package com.example.ct.audiocapture;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.widget.ImageButton;


public class MainActivity extends Activity {
    Button play,stop;
    ImageButton record, newRecEn;
    private MediaRecorder myAudioRecorder;
    private MediaPlayer m = new MediaPlayer();
    private String outputFile = null;
    private String outputTemp = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play=(Button)findViewById(R.id.button3);
        stop=(Button)findViewById(R.id.button2);
        //record=(Button)findViewById(R.id.button);

        //Necessary to get an image as button
        record=(ImageButton)findViewById(R.id.myButton);
        //newRecEn=(ImageButton)findViewById(R.id.myButton1);

        stop.setEnabled(false);
        play.setEnabled(false);

        getFileName();

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ImageButton Stuff
                record.setImageResource(R.drawable.recbuttenabled);




                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                }

                catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                record.setEnabled(false);
                stop.setEnabled(true);
                play.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ImageButton Stuff
                record.setImageResource(R.drawable.recbuttdisabled);

                //Stop looping if we are stopping track playback. Has no effect on recording
                if(m.isLooping() == true) {
                    m.setLooping(false);
                }
                else {
                    myAudioRecorder.stop();
                    myAudioRecorder.reset();
                    //myAudioRecorder.release();
                    //myAudioRecorder  = null;

                    //Boiler plate to allow us to record over existing track OR play track upon stop
                    myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

                    Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
                }
                stop.setEnabled(false);
                play.setEnabled(true);
                record.setEnabled(true); //originally set to false

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {
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

                try {
                    m.setLooping(true);
                    m.prepare();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });
    }

    public double getAmplitude(){
        return myAudioRecorder.getMaxAmplitude() /100;
    }

    public void getFileName(){
        //Helper methods to get input for output filename

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter your session name");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                outputTemp = input.getText().toString();

                //Ok so outputTemp is definitely being stored properly, this log proves it
                Log.w("audiocapture", outputTemp);

                //Crazy Test
                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + outputTemp + ".3gp";
                myAudioRecorder.setOutputFile(outputFile);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
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