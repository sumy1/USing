package com.usingstudioo.Models;

import org.json.JSONObject;

import java.io.Serializable;

public class Notes extends BaseModel implements Serializable {

    private int time;
    private String NoteName;
    private String NoteStatus;

    public Notes(JSONObject jsonObject){
        this.time = getValue(jsonObject,kStartTime,Integer.class);
        this.NoteName = getValue(jsonObject,kNoteName,String.class);
        this.NoteStatus = getValue(jsonObject,kNoteStatus,String.class);
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getNoteName() {
        return NoteName;
    }

    public void setNoteName(String noteName) {
        NoteName = noteName;
    }

    public String getNoteStatus() {
        return NoteStatus;
    }

    public void setNoteStatus(String noteStatus) {
        NoteStatus = noteStatus;
    }
}
