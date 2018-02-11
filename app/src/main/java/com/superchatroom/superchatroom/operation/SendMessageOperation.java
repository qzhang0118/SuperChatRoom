package com.superchatroom.superchatroom.operation;

import android.os.AsyncTask;
import android.util.Log;

import com.superchatroom.superchatroom.util.ApplicationConstants;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Send a message to server via Http request. Inherits from {@link AsyncTask} to avoid running on UI
 * thread.
 */
public class SendMessageOperation extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = SendMessageOperation.class.getSimpleName();

    private static final MediaType MEDIATYPE_JSON =
            MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    private String postMessage(String json) throws IOException {
        RequestBody body = RequestBody.create(MEDIATYPE_JSON, json);
        Request request = new Request.Builder()
                .url(ApplicationConstants.SEND_MSG_ENDPOINT)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String assembleMessageJson(String topic, String message, String username) {
        return "{\"topic\":\"" + topic + "\",\"message\":\"" + message
                + "\",\"username\":\"" + username + "\"}";
    }

    /**
     * Posts a message in background. Expects three parameters, the first is topic, the second is
     * message, the third is username.
     *
     * @return true if message is sent successfully, false otherwise.
     */
    @Override
    protected Boolean doInBackground(String... params) {
        try {
            postMessage(assembleMessageJson(params[0], params[1], params[2]));
        } catch (IOException e) {
            Log.e(TAG, "Unable to send message: " + params[1] + " to topic: " + params[0]
                    + " from username " + params[2], e);
            return false;
        }
        return true;
    }
}
