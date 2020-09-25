package com.usingstudioo.Models;

import org.json.JSONObject;

import java.io.Serializable;

public class SongModel extends BaseModel implements Serializable {

    private int songId;
    private String songType,songTitle,songArtist,songKey,youtubeLink,linkFireLink;
    private String createdOn,updatedOn,songStatus;

    public String getAudioVideo() {
        return audioVideo;
    }

    public void setAudioVideo(String audioVideo) {
        this.audioVideo = audioVideo;
    }

    private String audioVideo;

    public String getEmbeddedLink() {
        return embeddedLink;
    }

    public void setEmbeddedLink(String embeddedLink) {
        this.embeddedLink = embeddedLink;
    }

    private String embeddedLink;

    public SongModel(JSONObject jsonResponse){
        this.songId = Integer.valueOf(getValue(jsonResponse,kSongId,String.class));
        this.songTitle = getValue(jsonResponse,kSongTitle,String.class);
        this.songArtist = getValue(jsonResponse,kSongArtist,String.class);
        this.songKey = getValue(jsonResponse, kSongKey,String.class);
        this.songType = getValue(jsonResponse,kSongType,String.class);
        this.songStatus = getValue(jsonResponse,kSongStatus,String.class);
        this.youtubeLink = getValue(jsonResponse,kYoutubeLink,String.class);
        this.linkFireLink = getValue(jsonResponse,kLinkFireLink,String.class);
        this.createdOn = getValue(jsonResponse,kCreatedOn,String.class);
        this.updatedOn = getValue(jsonResponse,kUpdateOn,String.class);
        this.embeddedLink=getValue(jsonResponse,kEmbeddedLink,String.class);
        this.audioVideo=getValue(jsonResponse,kAudioVideo,String.class);
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongType() {
        return songType;
    }

    public void setSongType(String songType) {
        this.songType = songType;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongKey() {
        return songKey;
    }

    public void setSongKey(String songKey) {
        this.songKey = songKey;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getLinkFireLink() {
        return linkFireLink;
    }

    public void setLinkFireLink(String linkFireLink) {
        this.linkFireLink = linkFireLink;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getSongStatus() {
        return songStatus;
    }

    public void setSongStatus(String songStatus) {
        this.songStatus = songStatus;
    }
}
