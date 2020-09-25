package com.usingstudioo.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.usingstudioo.Models.SongModel;
import com.usingstudioo.Utils.Toaster;
import com.usingstudioo.Utils.Utils;
import com.usingstudioo.R;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import jaygoo.widget.wlv.WaveLineView;

import static com.usingstudioo.Constants.Constants.kData;

public class WfiveRecorderActivity extends AppCompatActivity {
    private MediaRecorder mRecorder;
    private static final String TAG = WfiveRecorderActivity.class.getSimpleName();
    private SongModel song;
    private TextView record;
    private TextView tvTimer;
    private CountDownTimer timer;
    private int songLoaded;
    private SeekBar volumeSeekBar, mediaSeekBar;
    private AudioManager audioManager;
    private WaveLineView waveLineView;
    private ImageView ivPlay;
    private TextView tvPlay, timerr;
    private File songFile;
    private MediaPlayer mPlayer;

    private boolean playPause;
    private ProgressDialog progressDialog;
    ProgressBar progressbar;
    private boolean initialStage = true;
    String songurl;
    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_wfive_recorder);
        song = (SongModel) getIntent().getSerializableExtra(kData);
         songurl = song.getLinkFireLink();
        Log.d("FileLink", song.getLinkFireLink());
        progressbar=findViewById(R.id.progressbar);

        browser = (WebView) findViewById(R.id.webview);
        findViewById(R.id.lay_play_stop).setVisibility(View.VISIBLE);
        showInfoDialog();

        TextView tvSongg = findViewById(R.id.tv_song_name_w);
        TextView tvTypee = findViewById(R.id.tv_song_type_w);
        TextView tvKeyy = findViewById(R.id.tv_song_key_w);
        tvSongg.setText(song.getSongTitle());
        tvTypee.setText(song.getSongType());
        tvKeyy.setText(song.getSongKey());

        volumeSeekBar = findViewById(R.id.volume_seek);
        mediaSeekBar = findViewById(R.id.audio_seek);
        timerr = findViewById(R.id.timer_w);

        ivPlay = findViewById(R.id.iv_play);
        tvPlay = findViewById(R.id.tv_play);

        record = findViewById(R.id.btn_record);
        tvTimer = findViewById(R.id.timer);

        initControls();


        findViewById(R.id.card_record).setOnClickListener(v -> {
            recording();

        });
        record.setOnClickListener(v -> {
            recording();
        });

        findViewById(R.id.bt_back).setOnClickListener(v -> {finish();
        }
        );

        findViewById(R.id.bt_recordings).setOnClickListener(v -> RecordListIntent());

        findViewById(R.id.iv_logo).setOnClickListener(v -> {
            Intent in = new Intent(WfiveRecorderActivity.this, WelcomeActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);
            finish();
        });

    }

    private void loadBrowser(String url){
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.clearCache(true);
        browser.clearHistory();// Add this
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        browser.setWebViewClient(new SSLTolerentWebViewClient(progressbar));
        browser.loadUrl(url);
    }


    private class SSLTolerentWebViewClient extends WebViewClient {

        private ProgressBar progressBar;
        public SSLTolerentWebViewClient(ProgressBar progressBar) {
            this.progressBar=progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

            AlertDialog.Builder builder = new AlertDialog.Builder(WfiveRecorderActivity.this);
            AlertDialog alertDialog = builder.create();
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }
            handler.proceed();


            message += " Do you want to continue anyway?";
            alertDialog.setTitle("SSL Certificate Error");
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Ignore SSL certificate errors
                    handler.proceed();
                }
            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    handler.cancel();
                }
            });
            //alertDialog.show();
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                //Do something after 1000ms
                progressBar.setVisibility(View.GONE);
                browser.setVisibility(View.VISIBLE);
            }, 2800);

        }

    }


    private void recording() {
        if (mRecorder == null)
            startRecording();
        else
            endRecording();
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(Utils.createPath(WfiveRecorderActivity.this, song.getSongTitle(), ".mp3"));
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        mRecorder.start();
        //loadBrowser(songurl);
       /* browser.resumeTimers();
        browser.onResume();*/
        timerStart(300000);
        Toaster.toastRangeen("Start Recording...");
        record.setText("Stop");;
    }

    private void endRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        timerStop();
        tvTimer.setText(getString(R.string.clock_hint));
        browser.loadUrl("about:blank");
        browser.onPause();
        browser.clearHistory();
        if (mPlayer != null)
            stopPlaying();
        Toaster.toastRangeen("Stop Recording...");
        record.setText("Start");;
        showEndRecordingInfoDialog();

        /*Utils.showConfirmDialog(WfiveRecorderActivity.this, getString(R.string.recording_done_msg),
                (dialog, which) -> RecordListIntent(), (dialog, which) -> dialog.dismiss(),
                "View Recordings", "Cancel");*/
    }

    private void showEndRecordingInfoDialog(){
        Dialog dialog = new Dialog(WfiveRecorderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_endrecord_information);

        Button btn_close=dialog.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(v -> {
            dialog.dismiss();
            //showInfoDialog();
        });
        Button btn_viewRecording=dialog.findViewById(R.id.btn_viewRecording);
        btn_viewRecording.setOnClickListener(v -> {
            RecordListIntent();
        });


        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showInfoDialog() {
        Dialog dialog = new Dialog(WfiveRecorderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_record_informationn);

        dialog.findViewById(R.id.btn_close).setOnClickListener(v ->{dialog.dismiss();


            findViewById(R.id.lay_play_stop).setOnClickListener(V->{
                findViewById(R.id.lay_play_stop).setVisibility(View.GONE);
                //browser.setVisibility(View.VISIBLE);

                loadBrowser(songurl);
            });
        } );
        dialog.show();
    }

    private void RecordListIntent() {
        Intent intent = new Intent(WfiveRecorderActivity.this, RecorderListActivity.class);
        intent.putExtra("FROM","1");
        intent.putExtra(kData,song);
        startActivity(intent);
        finish();
    }


    public void timerStart(long timeLengthMilli) {
        timer = new CountDownTimer(timeLengthMilli, 100) {

            @Override
            public void onTick(long milliTillFinish) {
                long time;
                time = timeLengthMilli - milliTillFinish;
                long min = (time / (1000 * 60));
                long sec = ((time / 1000) - min * 60);
                tvTimer.setText(Utils.changeTimeFormatNew((min) + ":" + (sec)));
                Log.i("Tick", "Tock:" + time);
            }

            @Override
            public void onFinish() {
            }
        };
        timer.start();
    }

    public void timerStop() {
        if (timer != null)
            timer.cancel();
    }


    private void initControls() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            assert audioManager != null;
            volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stopPlaying() {
     /*   mPlayer.stop();
        mPlayer.release();
        mPlayer = null;*/

        waveLineView.stopAnim();
        waveLineView.clearAnimation();
        waveLineView.setVisibility(View.INVISIBLE);
        ivPlay.setImageDrawable(getDrawable(R.drawable.play));
        tvPlay.setText(getString(R.string.play_recordingg));

        mediaSeekBar.setProgress(0);
        timerr.setText("00:00");
    }


    @Override
    protected void onPause() {
        browser.onPause();
        browser.clearHistory();
        super.onPause();
        if (mRecorder != null)
            endRecording();
       /* if (mPlayer != null)
            stopPlaying();*/



    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (browser != null) {
            browser.destroy();
        }
    }

}
