package com.facilio.services.email;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.sun.mail.imap.IMAPFolder;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.search.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class ImapsClient implements AutoCloseable {
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
            } catch (MessagingException ex) {
                ex.printStackTrace();
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
            if(store.isConnected()) {
                IMAPFolder inbox = null;
                inbox = (IMAPFolder) store.getFolder(folderName);
                inbox.open(Folder.READ_ONLY);
                Message[] messages = inbox.getMessagesByUID(latestMessageUID + 1, UIDFolder.MAXUID);
                parseMessage(messages, inbox, supportEmailContext);
            } else {
                log.info("Unable to connet IMAP for support Mail " + supportEmailContext.getId());
            }
        }
    }

    public void parseMessage (Message[] messages, IMAPFolder inbox, SupportEmailContext mailDeta) throws MessagingException {
        List<ModuleBaseWithCustomFields> mailMessages = new ArrayList<>();
        try {
            long lastUID = 0;
            log.info("Mail fetched ===> Message Size" + messages.length);
            if (messages.length > 0) {
                for (Message message : messages) {
                    BaseMailMessageContext mailMessage = BaseMailMessageContext.instance(message);
                    lastUID = inbox.getUID(message);
                    mailMessage.setMessageUID(lastUID);
                    mailMessage.setParentId(mailDeta.getId());
                    mailMessage.setSiteId(mailDeta.getSiteId());
                    Map<String, List<Map<String, Object>>> attachments = new HashMap<>();
                    if (mailMessage.getAttachmentsList().size() > 0) {
                      attachments.put("mailAttachments", mailMessage.getAttachmentsList());
                        mailMessage.setSubForm(attachments);
                    }
                    mailMessages.add(mailMessage);
                }
                FacilioChain chain = TransactionChainFactory.getSaveMailMessage();

                FacilioContext context = chain.getContext();


                mailDeta.setLatestMessageUID(lastUID);
                mailDeta.setUidValidaity(inbox.getUIDValidity());

                context.put(FacilioConstants.ContextNames.MESSAGES, mailMessages);
                context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, mailDeta);

                chain.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeFolder(inbox);
        }
    }
    public void getNDaysMails(int days) throws  Exception {
        if (store != null) {
            IMAPFolder inbox = null;
            if(store.isConnected()) {
                inbox = (IMAPFolder) store.getFolder(folderName);
                inbox.open(Folder.READ_ONLY);
                int count = inbox.getMessageCount();					//getting only the last message
                Message lastMessage = inbox.getMessage(count);
                Message[] messages = {lastMessage};
//                long hrsToCheckinMillis= days * 10 * 3600000;
//                long endDate = System.currentTimeMillis();
//                long startDate = System.currentTimeMillis() - hrsToCheckinMillis ;
//                SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LE, new Date(endDate));
//                SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GE, new Date(startDate));
//                SearchTerm andTerm = new AndTerm(olderThan, newerThan);
//                Message messages[] = inbox.search(andTerm);
                log.info("Size" + messages.length);
                parseMessage(messages, inbox, supportEmailContext);
            } else {
                log.info("Unable to connet IMAP for support Mail " + supportEmailContext.getId());
            }
        }
    }
    private void closeFolder(Folder inbox) {
        try {
            if (inbox != null)
                inbox.close(true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws MessagingException {
        store.close();
        log.log(Level.INFO, "Closing ImapClient ");
    }
}
