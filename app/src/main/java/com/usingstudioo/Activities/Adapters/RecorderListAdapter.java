package com.usingstudioo.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usingstudioo.Utils.Utils;
import com.usingstudioo.R;

import java.io.File;
import java.util.ArrayList;

public class RecorderListAdapter extends RecyclerView.Adapter<RecorderListAdapter.myViewHolder>{
    private Context context;
    private ArrayList<File> mData;
    private OnSongClickListener listener;

    public RecorderListAdapter(Context context, ArrayList<File> data,OnSongClickListener listener) {
        this.context = context;
        this.mData = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recording_list_view, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
        File model = mData.get(i);
        myViewHolder.eName.setText(model.getName());
        myViewHolder.eTime.setText(Utils.getFileTimeStamp(model.lastModified()));
        myViewHolder.itemView.setOnClickListener(v -> listener.onSongClick(model));
        myViewHolder.ivShare.setOnClickListener(v -> listener.onShareClick(model));
        myViewHolder.ivDelete.setOnClickListener(v -> listener.onDeleteClick(model));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateList(ArrayList<File> list){
        mData = list;
        notifyDataSetChanged();
    }


    class myViewHolder extends RecyclerView.ViewHolder {
        ImageView ivType,ivShare,ivDelete;
        LinearLayout lvRow;
        TextView eName,eTime;
        private myViewHolder(View itemView) {
            super(itemView);
            ivType = itemView.findViewById(R.id.iv_image_type);
            ivShare = itemView.findViewById(R.id.iv_share);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            eName=itemView.findViewById(R.id.tv_song);
            eTime=itemView.findViewById(R.id.tv_time);
            lvRow=itemView.findViewById(R.id.row_view);
        }
    }

    public interface OnSongClickListener{
        void onSongClick(File model);
        void onShareClick(File model);
        void onDeleteClick(File model);
    }
}
