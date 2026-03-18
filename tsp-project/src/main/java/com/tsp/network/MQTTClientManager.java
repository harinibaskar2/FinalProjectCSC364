package com.tsp.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.tsp.model.Result;
import com.tsp.model.Task;

/**
 * MQTT manager for publishing tasks and receiving results.
 * Works for both Coordinator (publishes tasks, receives results)
 * and Workers (subscribe tasks, publish results).
 */
public class MQTTClientManager {

    private final String brokerUrl;
    private final String clientId;
    private MqttClient client;

    private final String taskTopic = "tsp/tasks";
    private final String resultTopic = "tsp/results";

    private ResultListener resultListener;

    /** Callback interface for Coordinator to receive results */
    public interface ResultListener {
        void onResultReceived(Result result);
    }

    /** Callback interface for Worker to receive tasks */
    public interface TaskListener {
        void onTaskReceived(Task task);
    }

    /** Constructor for Coordinator */
    public MQTTClientManager(String brokerUrl, String clientId, ResultListener listener) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.resultListener = listener;
        connect();
    }

    /** Connect to MQTT broker and subscribe to results if listener is set */
    private void connect() {
        try {
            client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);

            // If Coordinator: subscribe to results topic
            if (resultListener != null) {
                client.subscribe(resultTopic, (topic, message) -> {
                    try {
                        Result result = deserializeResult(message.getPayload());
                        resultListener.onResultReceived(result);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }

            System.out.println("MQTT connected: " + clientId);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /** Publish a Task to workers */
    public void publishTask(Task task) {
        try {
            byte[] payload = serialize(task);
            MqttMessage message = new MqttMessage(payload);
            message.setQos(1); // at least once delivery
            client.publish(taskTopic, message);
            System.out.println("Task published: " + task.getStartIndex() + " -> " + task.getEndIndex());
        } catch (MqttException | IOException e) {
            e.printStackTrace();
        }
    }

    /** Subscribe to tasks (Worker only) */
    public void subscribeTasks(final TaskListener listener) {
        try {
            client.subscribe(taskTopic, (topic, message) -> {
                try {
                    Task task = deserializeTask(message.getPayload());
                    listener.onTaskReceived(task);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /** Publish a Result (Worker only) */
    public void publishResult(Result result) {
        try {
            byte[] payload = serialize(result);
            MqttMessage message = new MqttMessage(payload);
            message.setQos(1);
            client.publish(resultTopic, message);
        } catch (MqttException | IOException e) {
            e.printStackTrace();
        }
    }

    /** Serialize an object to byte array */
    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    /** Deserialize Task from byte array */
    private Task deserializeTask(byte[] payload) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(payload);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Task) ois.readObject();
        }
    }

    /** Deserialize Result from byte array */
    private Result deserializeResult(byte[] payload) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(payload);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Result) ois.readObject();
        }
    }

    /** Disconnect safely */
    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}