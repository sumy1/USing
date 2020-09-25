package com.usingstudioo.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usingstudioo.Activities.Adapters.RecorderListAdapter;
import com.usingstudioo.BuildConfig;
import com.usingstudioo.Models.SongModel;
import com.usingstudioo.R;
import com.usingstudioo.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.usingstudioo.Constants.Constants.kData;
import static com.usingstudioo.Constants.Constants.kType;


public class RecorderListActivity extends AppCompatActivity {

    private LinearLayout emptyView;
    private RecorderListAdapter listAdapter;
    private SongModel song;
    private String fromValue;
    int activityType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder_list);
        emptyView = findViewById(R.id.empty_view);
        song = (SongModel) getIntent().getSerializableExtra(kData);
        fromValue=getIntent().getStringExtra("FROM");

        if(fromValue.equalsIgnoreCase("3")){
            activityType=getIntent().getIntExtra(kType,0);
        }

        Log.d("From",fromValue+"/"+activityType);
        RecyclerView rvList = findViewById(R.id.rv_list);
        LinearLayoutManager mLayoutManager=new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        rvList.setLayoutManager(mLayoutManager);
        listAdapter = new RecorderListAdapter(this, new ArrayList<>(), new RecorderListAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(File model) {
                Intent intent = new Intent(RecorderListActivity.this, RecorderPlayerActivity.class);
                intent.putExtra(kData,model);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                startActivity(intent);
                //finish();
            }

            @Override
            public void onShareClick(File model) {
                shareContent(model);
            }

            @Override
            public void onDeleteClick(File model) {
                Utils.showConfirmDialog(RecorderListActivity.this,getString(R.string.delete_check_msg),
                        (dialog, which) -> deleteFile(model),(dialog, which) -> dialog.dismiss(),
                        "Ok","Cancel");
            }
        });
        rvList.setAdapter(listAdapter);
        checkEmptyView();

        findViewById(R.id.bt_back).setOnClickListener(v ->
                {

                    if(fromValue.equalsIgnoreCase("1")){
                        //song = (SongModel) getIntent().getSerializableExtra(kData);
                        Intent intent = new Intent(RecorderListActivity.this, WfiveRecorderActivity.class );
                        intent.putExtra(kData,song);
                        startActivity(intent);
                        finish();
                    }else if(fromValue.equalsIgnoreCase("2")){
                        Intent intent = new Intent(RecorderListActivity.this, YoutubeRecorderActivity.class );
                        intent.putExtra(kData,song);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(RecorderListActivity.this, SongListActivity.class );
                        intent.putExtra(kType,activityType);
                        startActivity(intent);
                        finish();
                    }


                }

                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRecorderList();
    }

    private void getRecorderList(){
        ArrayList<File> list = Utils.getFiles(RecorderListActivity.this);
        listAdapter.updateList(list);
        checkEmptyView();
    }

    private void checkEmptyView(){
        if(listAdapter.getItemCount()==0)
            emptyView.setVisibility(View.VISIBLE);
        else
            emptyView.setVisibility(View.GONE);
    }

    private void shareContent(File file){
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider",file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("audio/mp3");
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    private void deleteFile(File file){
        try {
            boolean action = file.getCanonicalFile().delete();
            if(action)
                getRecorderList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
