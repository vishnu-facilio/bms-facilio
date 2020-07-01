package com.facilio.services.email;


import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.MailContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.CustomMailMessageApi;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3MailMessageContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.sun.mail.imap.IMAPFolder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.search.*;
import java.util.*;

public class ImapsClient {
    private Session session;
    private Store store;
    private Folder folder;
    private String folderName = "INBOX";
    private static Logger log = LogManager.getLogger(ImapsClient.class.getName());
    private SupportEmailContext supportEmailContext;
    public ImapsClient(SupportEmailContext mailDetails) {
        if (mailDetails != null ) {
            supportEmailContext = mailDetails;
            Properties properties = System.getProperties();
            properties.setProperty("mail.store.protocol", "imaps");
            properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.imap.socketFactory.fallback", "false");

            Session session = Session.getDefaultInstance(properties, null);

            try {
                store = session.getStore("imaps");
                store.connect(mailDetails.getMailServer(), mailDetails.getPort(), mailDetails.getUserName(), mailDetails.getPassword());
            } catch (MessagingException e) {
            }

        }
    }

    public void logout() throws MessagingException {
        folder.close(false);
        store.close();
        store = null;
        session = null;
    }

    public int getMessageCount() {
        int messageCount = 0;
        try {
            messageCount = folder.getMessageCount();
        } catch (MessagingException me) {
            me.printStackTrace();
        }
        return messageCount;
    }

    public Message[] getMessages() throws MessagingException {
        return folder.getMessages();
    }

    public void getMessageGtUID(long latestMessageUID) throws Exception {
        if (store != null) {
            IMAPFolder inbox = null;
            inbox = (IMAPFolder) store.getFolder(folderName);
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.getMessagesByUID(latestMessageUID + 1, UIDFolder.MAXUID);
            parseMessage(messages, inbox, supportEmailContext);
        }
    }

    private void parseMessage (Message[] messages, IMAPFolder inbox, SupportEmailContext mailDeta) throws MessagingException {
        List<V3MailMessageContext> mailMessages = new ArrayList<>();
        try {
            Map<Long, List<Object>> mailAttachment = new HashMap<>();
            // inbox.get
            long lastUID = 0;
            if (messages.length > 0) {
                for (Message message : messages) {
                    V3MailMessageContext mailMessage = new V3MailMessageContext();
                    mailMessage = V3MailMessageContext.instance(message);
                    mailMessage.setSupportMailId(-1l);
                    lastUID = inbox.getUID(message);
                    mailMessage.setMessageUID(lastUID);
                    mailMessage.setSupportMailId(mailDeta.getId());
                    if (mailMessage.getAttachmentsList().size() > 0) {
                        mailAttachment.put(lastUID, mailMessage.getAttachmentsList());
                    }
                    log.info("getUId" + mailMessage.getMessageUID());
                    mailMessages.add(mailMessage);
                }
                CustomMailMessageApi.insertV3CustomMailMessage(mailMessages);
                SupportEmailContext updateLatestUID = new SupportEmailContext();
                updateLatestUID.setLatestMessageUID(lastUID);
                updateLatestUID.setUidValidaity(inbox.getUIDValidity());
                if (mailAttachment.size() > 0 ) {
                    FacilioContext context = new FacilioContext();
                    context.put(FacilioConstants.ContextNames.ATTACHMENT_MAP_FILE_LIST, mailAttachment);
                    FacilioChain c = TransactionChainFactory.addMultipleAttachment();
                    c.execute(context);
                }
                CustomMailMessageApi.updateLatestMailUID(updateLatestUID, mailDeta.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getNDaysMails(int days) throws  Exception {
        if (store != null) {
            IMAPFolder inbox = null;
            inbox = (IMAPFolder) store.getFolder(folderName);
            inbox.open(Folder.READ_ONLY);
            long hrsToCheckinMillis= days * 10 * 3600000;
            long endDate = System.currentTimeMillis();
            long startDate = System.currentTimeMillis() - hrsToCheckinMillis ;
            SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LE, new Date(endDate));
            SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GE, new Date(startDate));
            SearchTerm andTerm = new AndTerm(olderThan, newerThan);
            Message messages[] = inbox.search(andTerm);
            log.info("Size" + messages.length);
            parseMessage(messages, inbox, supportEmailContext);
        }
    }
//    public List<MailContext> getLastHoursMails(int hours, String inboxName) throws Exception {
//        IMAPFolder inbox = null;
//
//        try {
//            List<MailContext> mailMessages = new ArrayList<>();
//            inbox = (IMAPFolder) store.getFolder(inboxName);
//            inbox.open(Folder.READ_ONLY);
//            Message[] messages = inbox.getMessagesByUID(662310 + 1, UIDFolder.MAXUID);
//
//            log.info("Message Count " + messages.length);
//            for (Message message: messages) {
//                MailContext mailMessage = new MailContext();
//                mailMessage = MailContext.instance(message);
//                mailMessage.setSupportMailId(-1l);
//                mailMessage.setMessageUID(inbox.getUID(message));
//                log.info("getUId" + mailMessage.getMessageUID());
//                mailMessages.add(mailMessage);
//            }
//            if (mailMessages.size() > 0) {
//                CustomMailMessageApi.insertCustomMailMessage(mailMessages);
//            }
//            return  mailMessages;
//        } catch (MessagingException e) {
//            throw new Exception(e);
//        } finally {
//            closeFolder(inbox);
//        }
//    }
    private void closeFolder(Folder inbox) {
        try {
            if (inbox != null)
                inbox.close(false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
