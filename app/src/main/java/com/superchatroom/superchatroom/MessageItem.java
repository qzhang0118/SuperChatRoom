package com.superchatroom.superchatroom;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents a message in the recycler view.
 */
public class MessageItem {
    private static DateFormat DATE_FORMAT;

    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DATE_FORMAT.setTimeZone(Calendar.getInstance().getTimeZone());
    }

    // The time this message is received.
    private Date receivedTime;
    private String message;

    public MessageItem(Date time, String message) {
        this.receivedTime = time;
        this.message = message;
    }

    public String getReceivedTime() {
        return DATE_FORMAT.format(receivedTime);
    }

    public String getMessage() {
        return message;
    }
}
