package com.facilio.agent.integration.queue;

import com.facilio.timeseries.TimeSeriesAPI;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Class for handling data from Google Pub Sub through Agent Message Integration Preprocessor
 */
public class GooglePubSub extends AgentIntegrationQueue{
    private static final Logger LOGGER = LogManager.getLogger(GooglePubSub.class.getName());


    private String projectId;
    private String subscriptionId;

    GooglePubSub(String projectId, String subscriptionId){
        this.projectId=projectId;
        this.subscriptionId=subscriptionId;
    }
    /**
     * Starts asynchronous thread for handling data from pub sub
     */

    @Override
    public void startProcessor(long orgid) {

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);
        LOGGER.info("Google Pub Sub started...");
        // Instantiate an asynchronous message receiver
        MessageReceiver receiver =
                new MessageReceiver() {
                    @Override
                    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
                        // handle incoming message, then ack/nack the received message

                        LOGGER.info("Id : " + message.getMessageId());
                        LOGGER.info("Time: "+ System.currentTimeMillis());
                        LOGGER.info("Data : " + message.getData().toStringUtf8());
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = (JSONObject)parser.parse(message.getData().toStringUtf8());
                            if (jsonObject.containsKey("events")&&(((JSONArray)jsonObject.get("events")).size()>0)) {
                                List<JSONObject> messages = getPreProcessor().preProcess(jsonObject);
                                for (JSONObject msg :
                                        messages) {
                                    TimeSeriesAPI.processPayLoad(0,msg, null);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("Error while processing data");
                            e.printStackTrace();
                        }
                        consumer.ack();
                    }
                };

        Subscriber subscriber = null;
        try {
            // Create a subscriber for "my-subscription-id" bound to the message receiver
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
            subscriber.startAsync().awaitRunning();
            // Allow the subscriber to run indefinitely unless an unrecoverable error occurs
            subscriber.awaitTerminated();
        }catch (Exception ex){
            LOGGER.info(ex.getMessage());
        }
        finally {
            // Stop receiving messages
            LOGGER.info("Stopping Google Pub Sub : "+projectId);
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }

    }

}
