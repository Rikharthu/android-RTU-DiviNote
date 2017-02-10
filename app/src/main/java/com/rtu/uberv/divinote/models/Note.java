package com.rtu.uberv.divinote.models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by UberV on 2/9/2017.
 */

public class Note {

    // TODO add user identification
    // unix millis
    private long createdAt;
    private long updatedAt;
    private long remindAt;
    // default - false
    private boolean completed = false;
    private String title;
    private String content;
    private String id;


    public Note() {
    }

    // TODO move somewhere else
    public static String generateId() {
        // generate timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "note_" + timestamp + "_v1";
    }

    public String getId() {
        return id;
    }

    public Note setId(String id) {
        this.id = id;
        return this;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Note setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public Note setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Note setCompleted(boolean completed) {
        this.completed = completed;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Note setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Note setContent(String content) {
        this.content = content;
        return this;
    }

    public long getRemindAt() {
        return remindAt;
    }

    public Note setRemindAt(long remindAt) {
        this.remindAt = remindAt;
        return this;
    }
}
