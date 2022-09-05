package com.facilio.services.email;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.util.DBConf;
import com.facilio.delegate.context.DelegationType;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.email.BaseEmailClient;
import com.facilio.fw.BeanFactory;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.commands.MailTransactionChainFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EmailClient extends BaseEmailClient {

    private static final Logger LOGGER = LogManager.getLogger(EmailClient.class.getName());
    public static final String mailDomain = FacilioProperties.getMailDomain();
    public static final String ERROR_MAIL_FROM="mlerror@" + mailDomain;
    public static final String ERROR_MAIL_TO="ai@" + mailDomain;
    public static final String ERROR_AT_FACILIO="error@" + mailDomain;
    public static final String ERROR_AND_ALERT_AT_FACILIO="error+alert@" + mailDomain;
    protected abstract Session getSession();

    private static boolean checkIfActiveUserFromEmail(String email) throws Exception { //TODO Have to handle this in bulk. For now all emails are not sent in user thread and so okay I guess
        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        User user = userBean.getUserFromEmail(email, null, AccountUtil.getCurrentOrg().getOrgId(), true);
        if (user == null) {
        	LOGGER.info("Sending email to user who is not in the org  - " + email);
//        	return false;
        }
        return (user == null || user.getUserStatus());
    }

    public long sendEmailWithActiveUserCheck (JSONObject mailJson) throws Exception {
        return sendEmailWithActiveUserCheck(mailJson, true);
    }

    public long sendEmailWithActiveUserCheck(JSONObject mailJson, boolean handleUserDelegation) throws Exception {
        return sendEmailWithActiveUserCheck(mailJson, null, handleUserDelegation);
    }

    public long sendEmailWithActiveUserCheck (JSONObject mailJson, Map<String, String> files) throws Exception {
        return sendEmailWithActiveUserCheck(mailJson, files, true);
    }

    private long sendEmailWithActiveUserCheck (JSONObject mailJson, Map<String, String> files, boolean handleUserDelegation) throws Exception {
        if (removeInActiveUsers(mailJson)) {
            if (handleUserDelegation) {
                checkUserDelegation(mailJson);
            }
            return sendEmail(mailJson, files, true);
        }
        return -1;
    }

    private long sendEmail(JSONObject mailJson, Map<String, String> files, boolean isActive) throws Exception {
        if(canTrack(mailJson)) {
            if(!isActive) {
                preserveOriginalEmailAddress(mailJson);
            }
            return pushEmailToQueue(mailJson, files);
        }
        return sendMailWithoutTracking(mailJson, files);
    }

    private boolean canTrack(JSONObject mailJson) throws Exception {
        boolean trackingConfFound = StringUtils.isNotEmpty(DBConf.getInstance().getMailTrackingConfName());
        boolean isSignUp = Constants.getModBean().getModule(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER) == null;
        boolean doTracking = (boolean) mailJson.getOrDefault("_tracking", true);
        return trackingConfFound && !isSignUp && doTracking;
    }

    private long pushEmailToQueue(JSONObject mailJson, Map<String, String> files) throws Exception {
        try {
            //new behaviour for non-prod env
            FacilioChain chain = MailTransactionChainFactory.pushOutgoingMailToQueue();
            FacilioContext context = chain.getContext();
            context.put(MailConstants.Params.MAIL_JSON, mailJson);
            context.put(MailConstants.Params.FILES, files);
            chain.execute();
            return (long) context.getOrDefault(MailConstants.Params.LOGGER_ID, -1);
        } catch (Exception e) {
            LOGGER.error("OG_MAIL_ERROR :: outgoing mail tracking failed before pushing to queue. So sending in normal flow", e);
            OutgoingMailAPI.restoreMailMessage(mailJson);
            return sendMailWithoutTracking(mailJson, files);
        }
    }

    public long sendMailWithoutTracking(JSONObject mailJson, Map<String, String> files) throws Exception {
        if(files == null) {
            sendEmailImpl(mailJson);
        } else {
            sendEmailImpl(mailJson, files);
        }
        return -1;
    }


    private void checkUserDelegation(JSONObject mailJson) throws Exception {

    	Set<String> toEmails = getEmailAddresses(mailJson, TO);
    	
    	Set<String> modifiedToEmailList = changeEmailToDelegatedUserEmail(toEmails);
    	
    	if(!CollectionUtils.isEmpty(modifiedToEmailList)) {
    		mailJson.put(TO, combineEmailsAgain(modifiedToEmailList));
    	}
    	
    	Set<String> toCCEmails = getEmailAddresses(mailJson, CC);
    	
    	Set<String> modifiedToCCEmailList = changeEmailToDelegatedUserEmail(toCCEmails);
    	
    	if(!CollectionUtils.isEmpty(modifiedToCCEmailList)) {
    		mailJson.put(CC, combineEmailsAgain(modifiedToCCEmailList));
    	}
    	
    	Set<String> toBCCEmails = getEmailAddresses(mailJson, BCC);
    	
    	Set<String> modifiedToBCCEmailList = changeEmailToDelegatedUserEmail(toBCCEmails);
    	
    	if(!CollectionUtils.isEmpty(modifiedToBCCEmailList)) {
    		mailJson.put(BCC, combineEmailsAgain(modifiedToBCCEmailList));
    	}
    }
    
    private Set<String> changeEmailToDelegatedUserEmail(Set<String> toEmails) throws Exception {
    	
    	if(!CollectionUtils.isEmpty(toEmails)) {
    		
    		List<String> toEmailsList = toEmails.stream().collect(Collectors.toList());
    		
    		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
    		
    		for(int i=0;i<toEmailsList.size();i++) {
        		
    			String email = toEmailsList.get(i);
    			
    			User user = userBean.getUserFromEmail(email, null, AccountUtil.getCurrentOrg().getOrgId(), true);
    			if(user != null) {
    				User delegatedUser = DelegationUtil.getDelegatedUser(user, DateTimeUtil.getCurrenTime(), DelegationType.EMAIL_NOTIFICATION);
        			
        			toEmailsList.set(i, delegatedUser.getEmail());
    			}
        	}
    		
    		return toEmailsList.stream().collect(Collectors.toSet());
    	}
        return null;
    }

    private String combineEmailsAgain (Set<String> emailAddresses) { //TODO Have to handle this better
        return emailAddresses.stream().collect(Collectors.joining(","));
    }

    private void handleOtherEmailAddress (JSONObject mailJson, String key) throws Exception {
        Set<String> emailAddress = getEmailAddresses(mailJson, key, true);
        if (CollectionUtils.isNotEmpty(emailAddress)) {
            mailJson.put(key, combineEmailsAgain(emailAddress));
        }
        else {
            mailJson.remove(key);
        }
    }

    private boolean removeInActiveUsers (JSONObject mailJson) throws Exception {
        if (AccountUtil.getCurrentOrg() != null) {
            Set<String> emailAddress = getEmailAddresses(mailJson, TO, true);
            if (CollectionUtils.isEmpty(emailAddress)) { //Not sending email if to is empty. Not even checking cc or Bcc in this case
                LOGGER.info(MessageFormat.format("Not sending email since ''to address'' ({0}) is empty after removing inactive users", mailJson.get(TO)));
                return false;
            }
            mailJson.put(TO, combineEmailsAgain(emailAddress));
            handleOtherEmailAddress(mailJson, CC);
            handleOtherEmailAddress(mailJson, BCC);
        }
        return true;
    }


    /**
     * Used to log outgoing mail tracking info. Don't use this method directly.
     * Use {@link #sendEmailWithActiveUserCheck(JSONObject, Map)} instead.
     */
    public String sendEmailFromWMS(JSONObject mailJson, Map<String, String> files) throws Exception {
        if(files == null || files.isEmpty()) {
            return sendEmailImpl(mailJson);
        }
        return sendEmailImpl(mailJson, files);
    }

    /**
     * @deprecated
     * Use {@link #sendEmailWithActiveUserCheck(JSONObject)} instead.
     */
    @Deprecated
    public long sendEmail(JSONObject mailJson) throws Exception {
        return sendEmail(mailJson, null);
    }

    /**
     * @deprecated
     * Use {@link #sendEmailWithActiveUserCheck(JSONObject, Map)} instead.
     */
    @Deprecated
    public long sendEmail(JSONObject mailJson, Map<String, String> files) throws Exception {
        return sendEmail(mailJson, files, false);
    }

    protected abstract String sendEmailImpl(JSONObject mailJson) throws Exception;

    protected abstract String sendEmailImpl(JSONObject mailJson, Map<String, String> files) throws Exception;

    MimeMessage getEmailMessage(JSONObject mailJson, Map<String, String> files) throws Exception {
        Session session = getSession();
        MimeMessage message = constructMimeMessageContent(mailJson,session,files);
        message.addHeader(HOST, FacilioProperties.getAppDomain());
        return message;
    }

    public static String getNoReplyFromEmail() {
        return getFromEmail("noreply");
    }
    
    public static String getSystemFromAddress(EmailFromAddress.SourceType sourceType) {
        
    	String emailAddress = getNoReplyFromEmail();
		try {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME));
			
			SelectRecordsBuilder<EmailFromAddress> select = new SelectRecordsBuilder<EmailFromAddress>()
					.moduleName(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
					.beanClass(EmailFromAddress.class)
					.select(modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("creationType"), EmailFromAddress.CreationType.DEFAULT.getIntValue()+"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), sourceType.getIntValue()+"", NumberOperators.EQUALS));
			
			EmailFromAddress fromAddress = select.fetchFirst();
			if(fromAddress != null) {
				emailAddress = MailMessageUtil.getWholeEmailFromNameAndEmail.apply(fromAddress.getDisplayName(), fromAddress.getEmail());
			}
		} catch (Exception e) {
			LOGGER.error("unable to fetch email address", e);
		}
		return emailAddress;
    	
    }
    
    public static String getNotificationFromAddressEmailFromName(String name) throws Exception {
        
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME));
		
		SelectRecordsBuilder<EmailFromAddress> select = new SelectRecordsBuilder<EmailFromAddress>()
				.moduleName(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
				.beanClass(EmailFromAddress.class)
				.select(modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), name, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("verificationStatus"), Boolean.TRUE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), EmailFromAddress.SourceType.NOTIFICATION.getIndex()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("activeStatus"), Boolean.TRUE.toString(), BooleanOperators.IS));
		
		EmailFromAddress fromAddress = select.fetchFirst();
		if(fromAddress != null) {
			String emailAddress = MailMessageUtil.getWholeEmailFromNameAndEmail.apply(fromAddress.getDisplayName(), fromAddress.getEmail());
			return emailAddress;
		}
		
		return null;
    }

    public static String getFromEmail(String localPart) {
        StringBuilder builder = new StringBuilder(localPart)
                                        .append("@");
        if (AccountUtil.getCurrentOrg() != null) {
            builder.append(AccountUtil.getCurrentOrg().getDomain()).append(".");
        }
        builder.append(mailDomain);

        return builder.toString();
    }

    public void sendErrorMail(long orgid,long ml_id,String error)
    {
        try
        {
            JSONObject json = new JSONObject();
            json.put(SENDER, ERROR_MAIL_FROM);
            json.put(TO, ERROR_MAIL_TO);
            json.put(SUBJECT, orgid+" - "+ml_id);

            StringBuilder body = new StringBuilder()
                    .append(error)
                    .append("\n\nInfo : \n--------\n")
                    .append("\n Org Time : ").append(DateTimeUtil.getDateTime())
                    .append("\n Indian Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
                    .append("\n\nMsg : ")
                    .append(error)
                    .append("\n\nOrg Info : \n--------\n")
                    .append(orgid)
                    ;
            json.put(MESSAGE, body.toString());

            sendEmailImpl(json);
        }
        catch(Exception e)
        {
            LOGGER.error("Error while sending mail ",e);
        }
    }
    void logEmail(JSONObject mailJson) {
        try {
            String toAddress = (String) mailJson.get("to");
            String ccAddress = null, bccAddress = null;
            if (mailJson.get("cc") != null) {
                ccAddress = (String) mailJson.get("cc");
            }
            if (mailJson.get("bcc") != null) {
                bccAddress = (String) mailJson.get("bcc");
            }
            if (!ERROR_AND_ALERT_AT_FACILIO.equals(toAddress) && !ERROR_AT_FACILIO.equals(toAddress)) {
                toAddress = toAddress == null ? "" : toAddress;
                JSONObject info = new JSONObject();
                info.put(SENDER, mailJson.get(SENDER));
                info.put(SUBJECT, mailJson.get(SUBJECT));
                if (mailJson.get(CC) != null) {
                    info.put("cc", mailJson.get(CC));
                }
                if (mailJson.get(BCC) != null) {
                    info.put("bcc", mailJson.get(BCC));
                }
                CommonAPI.addNotificationLogger(CommonAPI.NotificationType.EMAIL, toAddress, info);
            }
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while logging email", e);
        }
    }
    boolean canSendEmail(JSONObject mailJson, Map<String, String> files) throws Exception {
        if(files == null || files.isEmpty() || FacilioProperties.isDevelopment()) {
            sendEmailImpl(mailJson);
            return false;
        }
        return canSendEmail(mailJson);
    }

    boolean canSendEmail(JSONObject mailJson) throws Exception {
        if (FacilioProperties.isDevelopment()) {
            return false;
        }
        return (getEmailAddresses(mailJson, TO).size() >0 );
    }

    Set<String> getEmailAddresses(JSONObject mailJson, String key) throws Exception {
        return getEmailAddresses(mailJson, key, false);
    }

    private Set<String> getEmailAddresses(JSONObject mailJson, String key, boolean checkActive) throws Exception {
        String emailAddressString = (String)mailJson.get(key);
        Set<String> emailAddress = new HashSet<>();
        if(StringUtils.isNotEmpty(emailAddressString)) {
            JSONObject emailMetaJson = new JSONObject();
            for(String address : FacilioUtil.splitByComma(emailAddressString)) {
                Pair<String, String> emailMeta = MailMessageUtil.getUserNameAndEmailAddress.apply(address);
                address = emailMeta.getKey();
                if(!FacilioProperties.isProduction()) {
                    if (address != null && address.contains("@facilio.com") && (!checkActive || checkIfActiveUserFromEmail(address))) {
                        emailAddress.add(address);
                        emailMetaJson.put(address, emailMeta.getValue());
                    }
                } else {
                    if (address != null && address.contains("@") && (!checkActive || checkIfActiveUserFromEmail(address))) {
                        emailAddress.add(address);
                        emailMetaJson.put(address, emailMeta.getValue());
                    }
                }
            }
            preserveOriginalEmailAddress(mailJson, key, emailMetaJson);
        }

        return emailAddress;
    }

    private void preserveOriginalEmailAddress(JSONObject mailJson, String key, JSONObject emailMetaJson) {
        String originalKey = "original"+StringUtils.capitalize(key);
        if(mailJson.containsKey(originalKey)) {
            return;
        }
        mailJson.put(originalKey, emailMetaJson);
    }

    private void preserveOriginalEmailAddress(JSONObject mailJson) throws Exception {
        getEmailAddresses(mailJson, TO, false);
        getEmailAddresses(mailJson, CC, false);
        getEmailAddresses(mailJson, BCC, false);
    }

}
