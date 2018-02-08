package com.superchatroom.superchatroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.superchatroom.superchatroom.util.ApplicationConstants;
import com.superchatroom.superchatroom.util.MqttClient;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private MqttClient mqttClient;
    private Button sendButton;
    private EditText inputEditText;
    private InputMethodManager inputMethodManager;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(new ArrayList<MessageItem>());
        recyclerView.setAdapter(adapter);

        sendButton = findViewById(R.id.submit_button);
        inputEditText = findViewById(R.id.message_input);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputEditText.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    new SendMessageOperation()
                            .execute(ApplicationConstants.TOPIC, message,
                                    firebaseAuth.getCurrentUser().getDisplayName());
                }
                inputEditText.setText("");
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        mqttClient = new MqttClient(getApplicationContext());
        mqttClient.setCallback(new MqttClient.MqttClientCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                adapter.appendMessageItem(new MessageItem(Calendar.getInstance().getTime(),
                        message.toString()));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mqttClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mqttClient.disconnect();
    }
}