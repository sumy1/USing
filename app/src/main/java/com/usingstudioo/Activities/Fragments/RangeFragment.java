package com.usingstudioo.Activities.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usingstudioo.Activities.Adapters.RangeAdapter;
import com.usingstudioo.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.usingstudioo.Constants.Constants.kName;
import static com.usingstudioo.Constants.Constants.kType;

public class RangeFragment extends Fragment {

    private static final String TAG = RangeFragment.class.getSimpleName();
    private Context context;
    private RangeSelectInterface listener;
    private static int selectId;

    public static RangeFragment newInstance(int id) {
        selectId = id;
        return new RangeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        context = getActivity();

        String[] list = getResources().getStringArray(R.array.range_list);
        ArrayList<HashMap<String,Object>> data = new ArrayList<>();
        for(int i=0;i<list.length;i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put(kName,list[i]);
            if(i==selectId)
                map.put(kType,1);
            else
                map.put(kType,0);
            data.add(map);
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RangeAdapter mAdapter = new RangeAdapter(context,data,(pos, name) -> listener.rangeSelect(pos,name));
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public void setRangeSelectListener(RangeSelectInterface callback) {
        this.listener = callback;
    }

    public interface RangeSelectInterface{
        void rangeSelect(int id, String name);
    }
}
