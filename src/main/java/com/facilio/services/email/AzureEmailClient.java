package com.facilio.services.email;

import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.*;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.util.DBConf;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Session;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class AzureEmailClient extends EmailClient{

    private static final Logger LOGGER = LogManager.getLogger(AzureEmailClient.class.getName());

    private static com.azure.communication.email.EmailClient emailClient = null;

    private static final AzureEmailClient INSTANCE =new AzureEmailClient();

    @Override
    protected Session getSession() {
        return null;
    }

    @Override
    public JSONObject getSuppressionStatus(String email) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "not handled in Azure email client");
        return jsonObject;
    }

    @Override
    protected String sendEmailImpl(JSONObject mailJson) throws Exception {
        return sendEmailViaAzure(mailJson,null);
    }

    @Override
    protected String sendEmailImpl(JSONObject mailJson, Map<String, String> files) throws Exception {
        return sendEmailViaAzure(mailJson,files);
    }
    private String sendEmailViaAzure(JSONObject mailJson, Map<String,String> files) throws Exception  {
//        logEmail(mailJson);
        if(canSendEmail(mailJson)) {
            try {
                boolean isTrackingConfNotFound = DBConf.getInstance().getMailTrackingConfName()==null;
                if(isTrackingConfNotFound) { // normal behaviour for production env
                    if(files==null || files.isEmpty()) {
                        return sendMail(mailJson,null); //without file
                    }
                }
                return sendMail(mailJson,files);
            } catch (Exception ex) {
                LOGGER.info("The email was not sent.");
                LOGGER.info("Error message: "+ex.getMessage());
                throw ex;
            }
        } else {
            LOGGER.info("Can't send email. Because the TO address is empty");
        }
        return null;
    }
    private static String sendMail(JSONObject mailJson,Map<String,String> files) throws Exception {
        List<EmailAddress> toAddressList = Arrays.stream(((String) mailJson.get("to")).split(",")).map(emailString -> new EmailAddress(emailString)).collect(Collectors.toList());
        try {
            EmailRecipients emailRecipients = new EmailRecipients(toAddressList);
            if (mailJson.containsKey("cc") && ((String) mailJson.get("cc")) != null){
                List<EmailAddress> ccAddressList = Arrays.stream(((String) mailJson.get("cc")).split(",")).map(emailString -> new EmailAddress(emailString)).collect(Collectors.toList());
                emailRecipients.setCc(ccAddressList);
            }
            if (mailJson.containsKey("bcc") && ((String) mailJson.get("bcc")) != null){
                List<EmailAddress> bccAddressList = Arrays.stream(((String) mailJson.get("bcc")).split(",")).map(emailString -> new EmailAddress(emailString)).collect(Collectors.toList());
                emailRecipients.setBcc(bccAddressList);
            }
            EmailContent content = new EmailContent((String) mailJson.get("subject"));
            if (mailJson.get("mailType") == "plain/text") {
                content.setPlainText((String) mailJson.get("message"));
            } else {
                content.setHtml((String) mailJson.get("message"));
            }
            EmailMessage emailMessage = new EmailMessage((String) mailJson.get("sender"), content)
                    .setRecipients(emailRecipients);
            if (files != null && !files.isEmpty()) {
                emailMessage.setAttachments(getFileAttachments(files));
            }
            SendEmailResult response = getAzureEmailClient().send(emailMessage);
            return response.getMessageId();
        }catch (Exception e){
            LOGGER.error("Error occurred while sending mail", e);
            throw e;
        }
    }
    private static List<EmailAttachment> getFileAttachments(Map<String,String> files) throws Exception {
        List<EmailAttachment> emailAttachmentList = new ArrayList<>();
        Iterator fileItr = files.entrySet().iterator();
        while (fileItr.hasNext()) {
            Map.Entry<String, String> file = (Map.Entry) fileItr.next();
            String fileUrl = file.getValue();

            if (fileUrl != null) {
                DataSource fileDataSource = null;
                if (DBConf.getInstance().isDevelopment()) {
                    fileDataSource = new FileDataSource(fileUrl);
                } else {
                    URL url = new URL(fileUrl);
                    fileDataSource = new URLDataSource(url);
                }
                byte[] bytes = IOUtils.toByteArray(fileDataSource.getInputStream());
                String encodedString = Base64.getEncoder().encodeToString(bytes);
                EmailAttachmentType attachmentType = EmailAttachmentType.fromString(((String) fileUrl).substring(fileUrl.lastIndexOf(".")+1));
                if(attachmentType == null) {
                    attachmentType = EmailAttachmentType.ONE;
                }
                emailAttachmentList.add(new EmailAttachment(file.getKey(), attachmentType, encodedString));
            }
        }
            return emailAttachmentList;
    }
    public static EmailClient getClient(){
        return INSTANCE;
    }

    public static com.azure.communication.email.EmailClient  getAzureEmailClient(){
        if(emailClient == null) {
            emailClient = new EmailClientBuilder()
                    .connectionString(FacilioProperties.getConfig("comm.connectionStr"))
                    .buildClient();
        }
        return emailClient;
    }
}


