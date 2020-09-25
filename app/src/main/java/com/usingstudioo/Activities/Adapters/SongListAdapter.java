package com.usingstudioo.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.usingstudioo.Models.SongModel;
import com.usingstudioo.Utils.PicassoCircleTransformation;
import com.usingstudioo.R;

import java.util.concurrent.CopyOnWriteArrayList;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.myViewHolder>{
    private Context context;
    private CopyOnWriteArrayList<SongModel> mData;
    private OnSongClickListener listener;
    String embeddedlink;

    public SongListAdapter(Context context, CopyOnWriteArrayList<SongModel> data, OnSongClickListener listener) {
        this.context = context;
        this.mData = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_song_list_view, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
        SongModel model = mData.get(i);
        myViewHolder.eName.setText(model.getSongTitle());
        if(model.getSongType().equals("Piano"))
            Picasso.with(context).load(R.drawable.piano).into(myViewHolder.ivType);
        else if(model.getSongType().equals("Guitar")){
            Picasso.with(context).load(R.drawable.guitar).into(myViewHolder.ivType);
        }else {
            Picasso.with(context).load(R.drawable.wfive4).transform(new PicassoCircleTransformation()).into(myViewHolder.ivType);
        }
        myViewHolder.itemView.setOnClickListener(v -> {
            listener.onSongClick(model,model.getEmbeddedLink());
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateList(CopyOnWriteArrayList<SongModel> list){
        mData = list;
        notifyDataSetChanged();
    }


    class myViewHolder extends RecyclerView.ViewHolder {
        ImageView ivType,ivPause;
        TextView eName;
        private myViewHolder(View itemView) {
            super(itemView);
            ivType = itemView.findViewById(R.id.iv_image_type);
            ivPause = itemView.findViewById(R.id.iv_pause);
            eName=itemView.findViewById(R.id.tv_song);
        }
    }

    public interface OnSongClickListener{
        void onSongClick(SongModel model, String embeddedlink);
    }
}
