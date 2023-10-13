package com.facilio.services.email;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.util.DBConf;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Session;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SGEmailClient extends EmailClient{
    private static final Logger LOGGER = LogManager.getLogger(SGEmailClient.class.getName());
    private static final SGEmailClient INSTANCE =new SGEmailClient();


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
        jsonObject.put("message", "not handled in SG email client");
        return jsonObject;
    }

    @Override
    protected String sendEmailImpl(JSONObject mailJson) throws Exception {
        return sendEmailImpl(mailJson, null);
    }

    @Override
    protected String sendEmailImpl(JSONObject mailJson, Map<String, String> files) throws Exception {
        return sendEmailViaSG(mailJson, files);
    }
    private String sendEmailViaSG(JSONObject mailJson, Map<String,String> files) throws Exception  {
//        logEmail(mailJson);
        if(canSendEmail(mailJson)) {
            try {
                boolean isTrackingConfNotFound = DBConf.getInstance().getMailTrackingConfName()==null;
                if(isTrackingConfNotFound) { // normal behaviour for production env
                    if(files==null || files.isEmpty()) {
                        return sendMail(mailJson,null);
                    }
                }
                return sendMail(mailJson, files);
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
        Personalization personalization0 = new Personalization();
        Arrays.stream(((String) mailJson.get("to")).split(",")).forEach(emailString->personalization0.addTo(new Email(emailString)));
        if (mailJson.containsKey("cc") && ((String) mailJson.get("cc")) != null) {
            Arrays.stream(((String) mailJson.get("cc")).split(",")).forEach(emailString -> personalization0.addCc(new Email(emailString)));
        }
        if (mailJson.containsKey("bcc") && ((String) mailJson.get("bcc")) != null) {
            Arrays.stream(((String) mailJson.get("bcc")).split(",")).forEach(emailString -> personalization0.addBcc(new Email(emailString)));
        }
        Mail mail = new Mail();
        //Email from = new Email((String) mailJson.get("from"));
        Email from = new Email(FacilioProperties.getConfig("sg.fromAddress"));
        mail.setFrom(from);
        mail.setSubject((String) mailJson.get("subject"));
        Content content = new Content();

        if (mailJson.get("mailType") == "plain/text") {
            content.setType("text/plain");
        } else {
            content.setType("text/html");
        }
        content.setValue((String) mailJson.get("message"));
        mail.addContent(content);
        if(files!=null){
            getAttachments(files).stream().forEach(attachments -> {mail.addAttachments(attachments);});
        }
        mail.addPersonalization(personalization0);

        SendGrid sg = new SendGrid(FacilioProperties.getConfig("sg.connectionStr"));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            request.setBody(mail.build());
            Response response = sg.api(request);
            return response.getHeaders().get("X-Message-Id");
        } catch (IOException ex) {
            throw ex;
        }
    }
    private static List<Attachments> getAttachments(Map<String, String> files) throws IOException {
        List<Attachments> attachmentsList = new ArrayList<>();
        Iterator fileItr = files.entrySet().iterator();

        while(fileItr.hasNext()) {
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
                Attachments attachment0 = new Attachments();
                attachment0.setContent(encodedString);
                attachment0.setFilename(file.getKey());
                attachment0.setType(((String) fileUrl).substring(fileUrl.lastIndexOf(".")+1));
                attachment0.setDisposition("attachment");
                attachmentsList.add(attachment0);
            }
        }
        return attachmentsList;
    }
}

