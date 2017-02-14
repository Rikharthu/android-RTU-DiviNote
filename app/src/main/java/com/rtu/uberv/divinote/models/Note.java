package com.rtu.uberv.divinote.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Note implements Parcelable {

    // TODO add user identification
    // unix millis
    private long createdAt;
    private long updatedAt;
    private long remindAt;
    // default - false
    private boolean completed = false;
    private String title;
    private String content;
    private long id;


    public Note() {
    }

    // TODO move somewhere else
    public static String generateId() {
        // generate timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "note_" + timestamp + "_v1";
    }

    public long getId() {
        return id;
    }

    public Note setId(long id) {
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

    // PARCELABLE
    protected Note(Parcel in) {
        createdAt = in.readLong();
        updatedAt = in.readLong();
        remindAt = in.readLong();
        completed = in.readByte() != 0x00;
        title = in.readString();
        content = in.readString();
        id = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeLong(remindAt);
        dest.writeByte((byte) (completed ? 0x01 : 0x00));
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

}
