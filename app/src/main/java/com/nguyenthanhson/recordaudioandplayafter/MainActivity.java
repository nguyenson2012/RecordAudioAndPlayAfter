package com.nguyenthanhson.recordaudioandplayafter;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String APP_TAG = "record";

    private MediaRecorder recorder = new MediaRecorder();
    private MediaPlayer player = new MediaPlayer();

    private Button btRecord;
    private Button btPlay;
    private Button btRecordAndPlay;
    private TextView resultView;

    private boolean recording = false;
    private boolean playing = false;
    private File outfile = null;
    private int countClick=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = (TextView) findViewById(R.id.output);
        prepareBeforeRecord();
        btRecord = (Button) findViewById(R.id.btRecord);
        btRecord.setOnClickListener(handleRecordClick);

        btPlay = (Button) findViewById(R.id.btPlay);
        btPlay.setOnClickListener(handlePlayClick);
        btRecordAndPlay=(Button)findViewById(R.id.btRecordAndPlay);
        btRecordAndPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RecordAndPlayAsyncTask().execute();


            }
        });


    }
    public void prepareBeforeRecord(){
        try {
            // the soundfile
            File direct = new File(Environment.getExternalStorageDirectory() + "/MyFolder/abc");

            if (!direct.exists()) {
                File wallpaperDirectory = new File("/sdcard/MyFolder/abc");
                wallpaperDirectory.mkdirs();
            }
            Random random=new Random();
            int n=random.nextInt(100);

            outfile = new File(new File("/sdcard/MyFolder/abc"), "sound"+n+".3gp");

            // init recorder
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(outfile.getAbsolutePath());

            // init player
            player.setDataSource(outfile.getAbsolutePath());
        } catch (IOException e) {
            Log.w(APP_TAG, "File not accessible ", e);
        } catch (IllegalArgumentException e) {
            Log.w(APP_TAG, "Illegal argument ", e);
        } catch (IllegalStateException e) {
            Log.w(APP_TAG, "Illegal state, call reset/restore", e);
        }
    }

    private final View.OnClickListener handleRecordClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(countClick<2) {
                if (!recording) {
                    startRecord();
                } else {
                    stopRecord();
                }
                countClick++;
            }else {
                if (!recording) {
                    outfile = new File(new File("/sdcard/MyFolder/abc"), "soundabc.3gp");
                    if (outfile.exists()) {
                        outfile.delete();
                    }

                    // init recorder
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(outfile.getAbsolutePath());

                    // init player
                    try {
                        player.setDataSource(outfile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startRecord();
                } else {
                    stopRecord();
                }


            }
        }
    };

    private final View.OnClickListener handlePlayClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!playing) {
                startPlay();
            } else {
                stopPlay();
            }
        }
    };

    private void startRecord() {
        Log.d(APP_TAG,"start recording");
        printResult("start recording..");
        try {
            recorder.prepare();
            recorder.start();
            recording = true;
        } catch (IllegalStateException e) {
            Log
                    .w(APP_TAG,
                            "Invalid recorder state .. reset/release should have been called");
        } catch (IOException e) {
            Log.w(APP_TAG, "Could not write to sd card");
        }
    }

    private void stopRecord() {
        Log.d(APP_TAG, "stop recording..");
        printResult("stop recording..");
        recorder.stop();
        recorder.reset();
//        recorder.release();
        recording = false;
    }

    private void startPlay() {
        Log.d(APP_TAG, "starting playback..");
        printResult("start playing..");
        try {
            playing = true;
            player.prepare();
            player.start();
        } catch (IllegalStateException e) {
            Log.w(APP_TAG, "illegal state .. player should be reset");
        } catch (IOException e) {
            Log.w(APP_TAG, "Could not write to sd card");
        }
    }

    private void stopPlay() {
        Log.d(APP_TAG, "stopping playback..");
        printResult("stop playing..");
        player.stop();
        player.reset();
//        player.release();
        playing = false;
    }

    private void printResult(String result) {
        resultView.setText(result);
    }
    class RecordAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            prepareBeforeRecord();
            try {
                recorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recorder.stop();
            recorder.reset();
        }
    }
    class PlayAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            try {
                player.prepare();
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            player.stop();
            player.reset();
        }
    }
    class RecordAndPlayAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            new RecordAsyncTask().execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new PlayAsyncTask().execute();
        }
    }
}
