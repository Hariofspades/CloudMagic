package com.few.cloudmagic.pojo;

import org.json.JSONArray;

/**
 * Created by Hari on 19/08/16.
 */
public class SingletonClass {

    String subject,preview;
    boolean isRead,isStarred;
    int id,ts;
    JSONArray participants;

    public String getPreview() {
        return preview;
    }

    public String getSubject() {
        return subject;
    }

    public JSONArray getParticipants() {
        return participants;
    }

    public void setParticipants(JSONArray participants) {
        this.participants = participants;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public int getId() {
        return id;
    }

    public int getTs() {
        return ts;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }


}
