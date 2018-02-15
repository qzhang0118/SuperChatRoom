package com.superchatroom.superchatroom;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.superchatroom.superchatroom.adapter.MessageAdapter;
import com.superchatroom.superchatroom.database.ChatEntryDbHelper;
import com.superchatroom.superchatroom.item.MessageItem;
import com.superchatroom.superchatroom.item.Topic;
import com.superchatroom.superchatroom.operation.SendMessageOperation;
import com.superchatroom.superchatroom.util.ApplicationConstants;
import com.superchatroom.superchatroom.util.MqttClient;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Calendar;

import static com.superchatroom.superchatroom.R.id.topic;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private MqttClient mqttClient;
    private Button sendButton;
    private EditText inputEditText;
    private InputMethodManager inputMethodManager;
    private FirebaseAuth firebaseAuth;
    public String currentTopic;
    private String user;
    private Topic parcelableTopic;
    private ChatEntryDbHelper chatEntryDbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatEntryDbHelper = new ChatEntryDbHelper(this);

        Intent intent = getIntent();
        if (intent.hasExtra(ApplicationConstants.TOPIC_KEY)) {
            parcelableTopic = intent.getParcelableExtra(ApplicationConstants.TOPIC_KEY);
            currentTopic = parcelableTopic.getTopic();
            ((TextView) findViewById(topic)).setText(parcelableTopic.getDisplayName());
            adapter = new MessageAdapter(chatEntryDbHelper.getChatEntry(currentTopic));
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());

        sendButton = findViewById(R.id.submit_button);
        inputEditText = findViewById(R.id.message_input);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputEditText.getText().toString();
                message = TextUtils.join("\\\\n",message.split("\\n"));
                user = firebaseAuth.getCurrentUser().getDisplayName();
                if (!TextUtils.isEmpty(message)) {
                    new SendMessageOperation().execute(currentTopic, message, user);
                    inputEditText.setText("");
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        inputEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    sendButton.callOnClick();
                    return true;
                }
                return false;
            }
        });

        mqttClient = new MqttClient(getApplicationContext());
        mqttClient.setCallback(new MqttClient.MqttClientCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String arrivedMessage = message.toString().replace("\\n", "\n");
                MessageItem messageItem = new MessageItem(Calendar.getInstance().getTime(),
                        arrivedMessage);
                new SaveMessageOperation().execute(messageItem.getReceivedTime(),
                        arrivedMessage, currentTopic, user);
                adapter.appendMessageItem(messageItem);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    }
                });
            }
        });
    }

    private class SaveMessageOperation extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            chatEntryDbHelper.addChatEntry(strings[0], strings[1], strings[2], strings[3]);
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mqttClient.connect(new MqttClient.Callback() {
            @Override
            public void onConnected() {
                mqttClient.subscribe(currentTopic);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mqttClient.disconnect();
    }
}