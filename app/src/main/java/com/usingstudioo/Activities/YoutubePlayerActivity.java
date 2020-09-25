package com.usingstudioo.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.usingstudioo.Models.SongModel;
import com.usingstudioo.R;

import org.jetbrains.annotations.NotNull;

import static com.usingstudioo.Constants.Constants.kData;

public class YoutubePlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player_view);
        SongModel song = (SongModel)getIntent().getSerializableExtra(kData);
        assert song != null;
        String videoId = song.getYoutubeLink().substring(song.getYoutubeLink().lastIndexOf("/")+1);
        Log.e("song name",videoId);

        TextView tvSong = findViewById(R.id.tv_song_name);
        TextView tvType = findViewById(R.id.tv_song_type);
        TextView tvKey = findViewById(R.id.tv_song_key);

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0);
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {

            }

            @Override
            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {

            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(@NotNull YouTubePlayer youTubePlayer, @NotNull String s) {

            }

            @Override
            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {

            }
        });

        tvSong.setText(song.getSongTitle());
        tvType.setText(song.getSongType());
        tvKey.setText(song.getSongKey());

        findViewById(R.id.bt_back).setOnClickListener(v -> finish());

    }
}
