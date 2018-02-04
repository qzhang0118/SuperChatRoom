package com.superchatroom.superchatroom.util;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

/**
 * A wrapper that handles MQTT connection and topic subscriptions.
 */
public class MqttClient {
    private static final String TAG = MqttClient.class.getSimpleName();

    public abstract static class MqttClientCallback implements MqttCallbackExtended {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {}

        @Override
        public void connectionLost(Throwable cause) {}

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {}

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {}
    }

    private MqttAndroidClient mqttAndroidClient;

    public MqttClient(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context,
                ApplicationConstants.MQTT_SERVER_URI, UUID.randomUUID().toString());
        connect();
    }

    public void setCallback(MqttClientCallback callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Failed to connect to: " +
                            ApplicationConstants.MQTT_SERVER_URI, exception);
                }
            });
        } catch (MqttException ex) {
            Log.e(TAG, "Failed to connect to: " +
                    ApplicationConstants.MQTT_SERVER_URI, ex);
        }
    }

    public void disconnect() {
        try {
            mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            Log.e(TAG, "Failed to disconnect to: " +
                    ApplicationConstants.MQTT_SERVER_URI, e);
        }
    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(ApplicationConstants.TOPIC, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Subscribed to " + ApplicationConstants.TOPIC + ".");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Subscription to topic " +
                            ApplicationConstants.TOPIC + " failed.", exception);
                }
            });

        } catch (MqttException ex) {
            Log.e(TAG, "Subscription to topic " +
                    ApplicationConstants.TOPIC + " failed.", ex);
        }
    }
}
