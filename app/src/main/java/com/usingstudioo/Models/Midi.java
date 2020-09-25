package com.usingstudioo.Models;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Midi extends BaseModel implements Serializable {

    private Integer tempo;
    private Integer ppqn;
    private ArrayList<Notes> notes;
    private HashMap<Integer,Object> note;
    private int accessNotes[];
    private Integer status;

    public Midi(JSONObject jsonObject){
        this.tempo = Integer.valueOf(getValue(jsonObject,kTempo,String.class));
        this.ppqn = Integer.valueOf(getValue(jsonObject,kPPQN,String.class));
        this.status = Integer.valueOf(getValue(jsonObject,kStatus,String.class));
        try {
            this.notes = handleMIDINotes(getValue(jsonObject,kNotes,JSONArray.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.note = handleMIDINoteMap(getValue(jsonObject,kNotes,JSONArray.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Integer getTempo() {
        return tempo;
    }

    public void setTempo(Integer tempo) {
        this.tempo = tempo;
    }

    public Integer getPpqn() {
        return ppqn;
    }

    public void setPpqn(Integer ppqn) {
        this.ppqn = ppqn;
    }

    public ArrayList<Notes> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Notes> notes) {
        this.notes = notes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public HashMap<Integer, Object> getNote() {
        return note;
    }

    public void setNote(HashMap<Integer, Object> note) {
        this.note = note;
    }

    public int[] getAccessNotes() {
        return accessNotes;
    }

    public void setAccessNotes(int[] accessNotes) {
        this.accessNotes = accessNotes;
    }

    private ArrayList<Notes> handleMIDINotes(JSONArray array) throws JSONException {
        ArrayList<Notes> list = new ArrayList<>();
        if(array.length()!=0){
            for(int i=0;i<array.length();i++){
                Notes note =new Notes(array.getJSONObject(i));
                list.add(note);
            }
        }
        return list;
    }

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer,Object> handleMIDINoteMap(JSONArray array) throws  JSONException {
        HashMap<Integer,Object> map = new HashMap<>();
        accessNotes = new int[array.length()];
        int count=0;
        if(array.length()!=0){
            for(int i=0;i<array.length();i++){
                if(array.getJSONObject(i).getString(kNoteStatus).equals("y")){
                    map.put(array.getJSONObject(i).getInt(kStartTime),array.getJSONObject(i).get(kNoteName)) ;
                    accessNotes[count] = array.getJSONObject(i).getInt(kStartTime);
                    count++;
                }
            }
        }
        return map;
    }

}
