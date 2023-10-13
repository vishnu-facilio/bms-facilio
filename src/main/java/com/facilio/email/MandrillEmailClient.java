package com.facilio.email;

import com.cribbstechnologies.clients.mandrill.exception.RequestFailedException;
import com.cribbstechnologies.clients.mandrill.model.MandrillAttachment;
import com.cribbstechnologies.clients.mandrill.model.MandrillHtmlMessage;
import com.cribbstechnologies.clients.mandrill.model.MandrillMessageRequest;
import com.cribbstechnologies.clients.mandrill.model.MandrillRecipient;
import com.cribbstechnologies.clients.mandrill.model.response.message.MessageResponse;
import com.cribbstechnologies.clients.mandrill.model.response.message.SendMessageResponse;
import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.cribbstechnologies.clients.mandrill.request.MandrillRESTRequest;
import com.cribbstechnologies.clients.mandrill.util.MandrillConfiguration;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.email.EmailClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import javax.mail.Session;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MandrillEmailClient extends EmailClient {

    private static final Logger LOGGER = LogManager.getLogger(MandrillEmailClient.class.getName());
    private static final String API_VERSION = "1.0";
    private static String baseUrl = "https://mandrillapp.com/api";
    private static String mandrillApiKey;

    private static MandrillRESTRequest request = new MandrillRESTRequest();
    private static MandrillConfiguration config = new MandrillConfiguration();
    private static MandrillMessagesRequest messagesRequest = new MandrillMessagesRequest();
    private static HttpClient client = new DefaultHttpClient();
    private static ObjectMapper mapper = new ObjectMapper();
    private static boolean configurationSet = false;

    private MandrillEmailClient() {
        setConfiguration();
    }

    private static final MandrillEmailClient INSTANCE = new MandrillEmailClient();

    private void setConfiguration() {
        try {
            if(FacilioProperties.getMandrillUrl() != null) {
                baseUrl = FacilioProperties.getMandrillUrl();
            }
            if(baseUrl.endsWith("/")) {
                baseUrl =baseUrl+"1.0/messages/send.json";
            } else {
                baseUrl = baseUrl+"/1.0/messages/send.json";
            }
            mandrillApiKey = FacilioProperties.getMandrillApiKey();
            config.setApiKey(mandrillApiKey);
            config.setApiVersion(API_VERSION);
            config.setBaseURL(baseUrl);
            request.setConfig(config);
            request.setObjectMapper(mapper);
            request.setHttpClient(client);
            messagesRequest.setRequest(request);
            configurationSet = true;
        } catch (Exception e) {
            LOGGER.info("Error while setting configuration");
        }
    }

    public static EmailClient getClient(){
        return INSTANCE;
    }

    @Override
    protected Session getSession() {
        return null;
    }

    @Override
    public JSONObject getSuppressionStatus(String email) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "not handled in Mandrill email client");
        return jsonObject;
    }

    @Override
    public String sendEmailImpl(JSONObject mailJson) throws Exception {
        return sendEmailImpl(mailJson, new HashMap<>());
    }


    private void sendEmailViaHttpConnection(JSONObject mailJson, Map<String, String> files) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        BufferedReader inputReader = null;
        try {
            URL url = new URL(baseUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            outputStream = new DataOutputStream(connection.getOutputStream());
            org.json.JSONObject mailContent = new org.json.JSONObject();
            mailContent.put("key", mandrillApiKey);
            org.json.JSONObject message = new org.json.JSONObject();
            message.put("html", mailJson.get(MESSAGE));
            // message.put("text", );
            message.put("subject", mailJson.get(SUBJECT));
            message.put("from_name", FacilioProperties.senderName());
            message.put("from_email", FacilioProperties.senderEmail());

            JSONArray recipientList = new JSONArray();

            String toAddress = (String)mailJson.get(TO);

            if(toAddress == null) {
                return;
            }
            for(String address : toAddress.split(",")) {
                org.json.JSONObject recipient = new org.json.JSONObject();
                recipient.put("email", address);
                recipientList.put(recipient);
            }
            message.put("to", recipientList);
            if (files != null && !files.isEmpty()) {
                JSONArray attachmentList = new JSONArray();
                for (Map.Entry<String, String> fileEntry : files.entrySet()) {
                    String fileUrl = fileEntry.getValue();
                    File file = new File(fileUrl);
                    byte[] encoded = Base64.encodeBase64(org.apache.commons.io.FileUtils.readFileToByteArray(file));
                    String fileContent = new String(encoded, StandardCharsets.US_ASCII);
                    org.json.JSONObject attachment = new org.json.JSONObject();
                    attachment.put("type", Files.probeContentType(file.toPath()));
                    attachment.put("name", fileEntry.getKey());
                    attachment.put("content", fileContent);
                    attachmentList.put(attachment);
                }
                message.put("attachments", attachmentList);
            }
            mailContent.put("message", message);

            outputStream.writeBytes(mailContent.toString());
            outputStream.flush();

            inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = inputReader.readLine()) != null) {
                response.append(inputLine);
            }
            LOGGER.info(response.toString());
        } catch (Exception e) {
            LOGGER.info("Exception while sending email ", e);
        } finally{
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.info("Exception while closing stream: ", e);
                }
            }
            if(inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    LOGGER.info("Exception while closing stream ", e);
                }
            }
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public String sendEmailImpl(JSONObject mailJson, Map<String, String> files) throws Exception {
        sendEmailViaHttpConnection(mailJson, files);
        return null;
    }

    private void sendEmailViaMandrillLibrary(JSONObject mailJson, Map<String, String> files) throws Exception {
        if( ! configurationSet) {
            return;
        }

        MandrillMessageRequest mmr = new MandrillMessageRequest();
        MandrillHtmlMessage message = new MandrillHtmlMessage();
        Map<String, String> headers = new HashMap<>();
        message.setFrom_email(FacilioProperties.senderEmail());
        message.setFrom_name(FacilioProperties.senderName());
        message.setHeaders(headers);
        message.setHtml((String)mailJson.get(MESSAGE));
        message.setSubject((String) mailJson.get(SUBJECT));
        if (files != null && !files.isEmpty()) {
            List<MandrillAttachment> attachments = new ArrayList<>();
            for (Map.Entry<String, String> fileEntry : files.entrySet()) {
                String fileUrl = fileEntry.getValue();
                File file = new File(fileUrl);
                byte[] encoded = Base64.encodeBase64(org.apache.commons.io.FileUtils.readFileToByteArray(file));
                String fileContent = new String(encoded, StandardCharsets.US_ASCII);
                attachments.add(new MandrillAttachment(Files.probeContentType(file.toPath()), fileEntry.getKey(), fileContent));
            }
            message.setAttachments(attachments);
        }
        String toAddress = (String) mailJson.get(TO);
        MandrillRecipient mandrillRecipient = new MandrillRecipient(toAddress, toAddress, "to");
        message.setTo(new MandrillRecipient[]{mandrillRecipient});
        message.setTrack_clicks(true);
        message.setTrack_opens(true);
        mmr.setMessage(message);
        try {
            SendMessageResponse response = messagesRequest.sendMessage(mmr);
            for(MessageResponse messageResponse: response.getList()) {
                if( "sent".equalsIgnoreCase(messageResponse.getStatus())) {
                    //AwsUtil.logEmail(mailJson);
                } else if (messageResponse.getRejectReason() != null) {
                    LOGGER.info("Email sending failed : to "+ messageResponse.getEmail() +" reason: " + messageResponse.getRejectReason() + " , status: " + messageResponse.getStatus());
                }
            }
        } catch (RequestFailedException e) {
            LOGGER.info("Exception while sending email ", e);
        }
    }
}
