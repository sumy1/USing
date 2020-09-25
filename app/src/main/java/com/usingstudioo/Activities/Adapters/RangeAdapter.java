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

import com.usingstudioo.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.usingstudioo.Constants.Constants.kName;
import static com.usingstudioo.Constants.Constants.kType;

public class RangeAdapter extends RecyclerView.Adapter<RangeAdapter.myViewHolder>{
    private Context context;
    private ArrayList<HashMap<String,Object>> mData;
    private OnItemClickListener listener;

    public RangeAdapter(Context context, ArrayList<HashMap<String,Object>> data,OnItemClickListener listener) {
        this.context = context;
        this.mData = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_type_list_view, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int pos) {
        HashMap<String,Object> model = mData.get(pos);
        myViewHolder.eName.setText((String)model.get(kName));
        if((int)model.get(kType)==1)
            myViewHolder.ivCheck.setImageDrawable(context.getDrawable(R.drawable.canvas_tick_check_view));
        else
            myViewHolder.ivCheck.setImageDrawable(context.getDrawable(R.drawable.canvas_empty_check_view));
        myViewHolder.itemView.setOnClickListener(v -> {
            setDataUpdate(pos);
            listener.onRangeClick(pos,(String)model.get(kName));
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void setDataUpdate(int pos){
        for(int i=0;i<getItemCount();i++){
            if(i!=pos)
                mData.get(i).put(kType,0);
            else
                mData.get(i).put(kType,1);
        }
        notifyDataSetChanged();
    }


    class myViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lvRow;
        ImageView ivCheck;
        TextView eName;
        private myViewHolder(View itemView) {
            super(itemView);
            eName=itemView.findViewById(R.id.tv_song);
            lvRow=itemView.findViewById(R.id.row_view);
            ivCheck=itemView.findViewById(R.id.iv_check);
        }
    }

    public interface OnItemClickListener{
        void onRangeClick(int pos, String name);
    }
}
