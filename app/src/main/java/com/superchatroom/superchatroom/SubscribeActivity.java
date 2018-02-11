package com.superchatroom.superchatroom;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.superchatroom.superchatroom.adapter.TopicAdapter;
import com.superchatroom.superchatroom.item.Topic;
import com.superchatroom.superchatroom.util.ApplicationConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * User subscribes to topics.
 */
public class SubscribeActivity extends AppCompatActivity {
    private interface Callback {
        void done(List<Topic> topics);
    }

    private RecyclerView recyclerView;
    private TopicAdapter adapter;
    private GetTopicOperation getTopicOperation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        recyclerView = findViewById(R.id.topic_subscription_recycler_view);
        adapter = new TopicAdapter(new ArrayList<Topic>());
        adapter.setOnItemClickListener(new TopicAdapter.ClickListener() {
            @Override
            public void onItemClick(Topic topic) {
                Intent intent = new Intent(SubscribeActivity.this, MainActivity.class);
                intent.putExtra(ApplicationConstants.TOPIC_KEY, topic);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        getTopicOperation = new GetTopicOperation(new Callback() {
            @Override
            public void done(List<Topic> topics) {
                updateUi(topics);
            }
        });
        getTopicOperation.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        getTopicOperation.cancel(true);
    }

    private void updateUi(List<Topic> topics) {
        adapter.refreshTopics(topics);
    }

    private static class GetTopicOperation extends AsyncTask<Void, Void, List<Topic>> {
        private static final String TAG = GetTopicOperation.class.getSimpleName();
        List<Topic> listOfTopics = new ArrayList<>();
        private Callback callback;

        public GetTopicOperation(Callback callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected List<Topic> doInBackground(Void... voids) {
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(ApplicationConstants.GET_TOPIC_ENDPOINT)
                    .get()
                    .build();

            JSONArray jsonArray = new JSONArray();
            try {
                jsonArray = new JSONArray(client.newCall(request).execute().body().string());
            } catch (JSONException|IOException e) {
                Log.e(TAG, "Failed to get topics.", e);
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    listOfTopics.add(new Topic(jsonObject.getString(ApplicationConstants.TOPIC_KEY),
                            jsonObject.getString(ApplicationConstants.TOPIC_NAME_KEY)));
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to get topics.", e);
                }
            }
            return listOfTopics;
        }

        protected void onPostExecute(List<Topic> result) {
            callback.done(result);
        }
    }
}