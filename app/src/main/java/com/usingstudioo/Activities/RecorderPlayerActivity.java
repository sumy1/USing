package com.usingstudioo.Activities;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.usingstudioo.R;

import java.io.File;
import java.io.IOException;

import jaygoo.widget.wlv.WaveLineView;

import static com.usingstudioo.Constants.Constants.kData;

public class RecorderPlayerActivity extends AppCompatActivity implements View.OnClickListener,Runnable {

    private static final String TAG = RecorderPlayerActivity.class.getSimpleName();
    private SeekBar volumeSeekBar,mediaSeekBar;
    private AudioManager audioManager;
    private WaveLineView waveLineView;
    private ImageView ivPlay;
    private TextView tvPlay,timer;
    private File songFile;
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activtiy_record_player);
        volumeSeekBar = findViewById(R.id.volume_seek);
        mediaSeekBar = findViewById(R.id.audio_seek);
        timer = findViewById(R.id.timer);
        waveLineView = findViewById(R.id.waveLineView);

        songFile = (File)getIntent().getSerializableExtra(kData);
        TextView songName = findViewById(R.id.tv_song_name);
        songName.setText(songFile.getName());

        ivPlay = findViewById(R.id.iv_play);
        tvPlay = findViewById(R.id.tv_play);
        findViewById(R.id.lay_play_stop).setOnClickListener(this);

        initControls();

        findViewById(R.id.bt_back).setOnClickListener(v -> finish());
    }

    private void initControls() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            assert audioManager != null;
            volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) { }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) { }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.lay_play_stop){
            if(mPlayer==null){
                startPlaying();
            }else{
                stopPlaying();
            }
        }
    }

    private void startPlaying(){
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(songFile.getAbsolutePath());
            mPlayer.prepare();
            mPlayer.setVolume(5f, 5f);
            mPlayer.setLooping(false);
            mediaSeekBar.setMax(mPlayer.getDuration());
            mPlayer.start();
            new Thread(this).start();
            mPlayer.setOnCompletionListener(mp -> stopPlaying());

            waveLineView.setVisibility(View.VISIBLE);
            waveLineView.startAnim();
            ivPlay.setImageDrawable(getDrawable(R.drawable.stop));
            tvPlay.setText(getString(R.string.stop_recording));
            mediaPlayerSeekBar();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying(){
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;

        waveLineView.stopAnim();
        waveLineView.clearAnimation();
        waveLineView.setVisibility(View.INVISIBLE);
        ivPlay.setImageDrawable(getDrawable(R.drawable.play));
        tvPlay.setText(getString(R.string.play_recording));

        mediaSeekBar.setProgress(0);
        timer.setText("00:00");
    }

    private void mediaPlayerSeekBar(){
        mediaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                int x = (int) Math.ceil(progress / 1000f);

                if (x < 10)
                    timer.setText("00:0" + x);
                else
                    timer.setText("00:" + x);

                /*double percent = progress / (double) seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                int seekWidth = seekBar.getWidth();
                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                int labelWidth = seekBarHint.getWidth();
                seekBarHint.setX(offset + seekBar.getX() + val
                        - Math.round(percent * offset)
                        - Math.round(percent * labelWidth / 2));*/

                if (progress > 0 && mPlayer != null && !mPlayer.isPlaying()) {
                    stopPlaying();
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    public void run() {
        int currentPosition = mPlayer.getCurrentPosition();
        int total = mPlayer.getDuration();
        while (mPlayer != null && mPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mPlayer.getCurrentPosition();
            } catch (Exception e) {
                return;
            }
            mediaSeekBar.setProgress(currentPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPlayer!=null)
            stopPlaying();
    }

}
