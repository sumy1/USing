package com.usingstudioo.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.usingstudioo.Models.SongModel;
import com.usingstudioo.Utils.Toaster;
import com.usingstudioo.Utils.Utils;
import com.usingstudioo.R;

import java.io.IOException;
import java.util.Objects;

import static com.usingstudioo.Constants.Constants.kData;

public class YoutubeRecorderActivity extends AppCompatActivity {

    private static final String TAG = YoutubeRecorderActivity.class.getSimpleName();
    private MediaRecorder mRecorder;
    private SongModel song;
    private TextView record;
    private TextView tvTimer;
    private CountDownTimer timer;
    private YouTubePlayer player;
    private int songLoaded;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_recorder_view);
        showInfoDialog();
        songLoaded=0;
        song = (SongModel)getIntent().getSerializableExtra(kData);

        TextView tvSong = findViewById(R.id.tv_song_name);
        TextView tvType = findViewById(R.id.tv_song_type);
        TextView tvKey = findViewById(R.id.tv_song_key);
        record = findViewById(R.id.btn_record);
        tvTimer = findViewById(R.id.timer);

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        assert song != null;
        String videoId = song.getYoutubeLink().substring(song.getYoutubeLink().lastIndexOf("/")+1);
        Log.e("song name",videoId);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                songLoaded=1;
                player = youTubePlayer;
                youTubePlayer.cueVideo(videoId, 0);
            }
        });

        tvSong.setText(song.getSongTitle());
        tvType.setText(song.getSongType());
        tvKey.setText(song.getSongKey());

        findViewById(R.id.card_record).setOnClickListener(v -> {
            if(songLoaded==1)
                recording();
            else
                showRecordError();
        });
        record.setOnClickListener(v -> {
            if(songLoaded==1)
                recording();
            else
                showRecordError();
        });

        findViewById(R.id.bt_back).setOnClickListener(v -> finish());

        findViewById(R.id.bt_recordings).setOnClickListener(v -> RecordListIntent());

        findViewById(R.id.iv_logo).setOnClickListener(v -> {
            Intent in = new Intent(YoutubeRecorderActivity.this, WelcomeActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);
            finish();
        });
    }

    private void recording(){
        if(mRecorder==null)
            startRecording();
        else
            endRecording();
    }
    private void startRecording(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(Utils.createPath(YoutubeRecorderActivity.this,song.getSongTitle(),".mp3"));
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        mRecorder.start();
        timerStart(300000);
        Toaster.toastRangeen("Start Recording...");
        record.setText("Stop");;
    }

    private void endRecording(){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        timerStop();
        tvTimer.setText(getString(R.string.clock_hint));
        player.pause();
        Toaster.toastRangeen("Stop Recording...");
        record.setText("Start");;


        showEndRecordingInfoDialog();
        /*Utils.showConfirmDialog(YoutubeRecorderActivity.this,getString(R.string.recording_done_msg),
                (dialog, which) -> RecordListIntent(),(dialog, which) -> dialog.dismiss(),
                "View Recordings","Cancel");*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mRecorder!=null)
            endRecording();
    }

    private void showRecordError(){
        Utils.showAlertDialog(this,getString(R.string.playback_error),getString(R.string.playback_msg));
    }

    private void showInfoDialog(){
         dialog = new Dialog(YoutubeRecorderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_record_information);

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showInfoDialogg(){
        Dialog dialog = new Dialog(YoutubeRecorderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_record_information);

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showEndRecordingInfoDialog(){
         dialog = new Dialog(YoutubeRecorderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_endrecord_information);

        Button btn_close=dialog.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(v -> {
            dialog.dismiss();
           //Toaster.customToast("Hi");
            //showInfoDialogg();
        });
        Button btn_viewRecording=dialog.findViewById(R.id.btn_viewRecording);
        btn_viewRecording.setOnClickListener(v -> {
            RecordListIntent();
        });


        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void RecordListIntent(){
        Intent intent = new Intent(YoutubeRecorderActivity.this, RecorderListActivity.class);
        intent.putExtra("FROM","2");
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
                long min = (time/(1000*60));
                long sec = ((time/1000)-min*60);
                tvTimer.setText(Utils.changeTimeFormatNew((min)+":"+(sec)));
                Log.i("Tick", "Tock:"+time);
            }

            @Override
            public void onFinish() { }
        };
        timer.start();
    }

    public void timerStop() {
        if(timer!=null)
            timer.cancel();
    }
}
