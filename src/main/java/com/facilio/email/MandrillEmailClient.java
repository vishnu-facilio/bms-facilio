package com.facilio.email;

import com.cribbstechnologies.clients.mandrill.exception.RequestFailedException;
import com.cribbstechnologies.clients.mandrill.model.MandrillAttachment;
import com.cribbstechnologies.clients.mandrill.model.MandrillRecipient;
import com.cribbstechnologies.clients.mandrill.model.response.message.MessageResponse;
import com.cribbstechnologies.clients.mandrill.model.response.message.SendMessageResponse;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.email.EmailClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import com.cribbstechnologies.clients.mandrill.model.MandrillHtmlMessage;
import com.cribbstechnologies.clients.mandrill.model.MandrillMessageRequest;
import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.cribbstechnologies.clients.mandrill.request.MandrillRESTRequest;
import com.cribbstechnologies.clients.mandrill.util.MandrillConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.Session;


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
            baseUrl = FacilioProperties.getMandrillUrl();
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
    public void sendEmail(JSONObject mailJson) throws Exception {
        sendEmail(mailJson, new HashMap<>());
    }

    @Override
    public void sendEmail(JSONObject mailJson, Map<String, String> files) throws Exception {
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
                if( ! messageResponse.getStatus().equals("sent") || messageResponse.getRejectReason() != null) {
                    LOGGER.info("Email sending failed : to "+ messageResponse.getEmail() +" reason: " + messageResponse.getRejectReason() + " , status: " + messageResponse.getStatus());
                }
            }
        } catch (RequestFailedException e) {
            LOGGER.info("Exception while sending email");
        }
        AwsUtil.logEmail(mailJson);
    }
}
