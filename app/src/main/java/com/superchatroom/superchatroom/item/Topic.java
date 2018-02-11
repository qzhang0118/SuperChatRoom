package com.superchatroom.superchatroom.item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a topic in the recycler view.
 */
public class Topic implements Parcelable {
    private String topic;
    private String displayName;

    public Topic(String topic, String displayName) {
        this.topic = topic;
        this.displayName = displayName;
    }

    private Topic(Parcel in) {
        topic = in.readString();
        displayName = in.readString();
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    public String getTopic() {
        return topic;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(topic);
        parcel.writeString(displayName);
    }
}
