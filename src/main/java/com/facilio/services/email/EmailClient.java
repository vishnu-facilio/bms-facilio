package com.facilio.services.email;

import java.net.URL;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.delegate.context.DelegationType;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.email.BaseEmailClient;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;

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

    public void sendEmailWithActiveUserCheck (JSONObject mailJson) throws Exception {
        sendEmailWithActiveUserCheck(mailJson, true);
    }

    public void sendEmailWithActiveUserCheck (JSONObject mailJson, boolean handleUserDelegation) throws Exception {
        if (removeInActiveUsers(mailJson)) {
            if (handleUserDelegation) {
                checkUserDelegation(mailJson);
            }
            sendEmail(mailJson);
        }
    }

    public void checkUserDelegation(JSONObject mailJson) throws Exception {
    	
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

	public void sendEmailWithActiveUserCheck (JSONObject mailJson, Map<String, String> files) throws Exception {
        if (removeInActiveUsers(mailJson)) {
        	checkUserDelegation(mailJson);
            sendEmail(mailJson, files);
        }
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
     * @deprecated
     * Use {@link #sendEmailWithActiveUserCheck(JSONObject)} instead.
     */
    @Deprecated
    public abstract void sendEmail(JSONObject mailJson) throws Exception;
    
    /**
     * @deprecated
     * Use {@link #sendEmailWithActiveUserCheck(JSONObject, Map)} instead.
     */
    @Deprecated
    public abstract void sendEmail(JSONObject mailJson, Map<String, String> files) throws Exception;

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

            sendEmail(json);
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
            sendEmail(mailJson);
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
        HashSet<String> emailAddress = new HashSet<>();
        if(StringUtils.isNotEmpty(emailAddressString)) {
            if (!FacilioProperties.isProduction()) {
                for (String address : FacilioUtil.splitByComma(emailAddressString)) {
                	address = MailMessageUtil.getEmailFromPrettifiedFromAddress.apply(address);
                    if (address.contains("@facilio.com") && (!checkActive || checkIfActiveUserFromEmail(address))) {
                        emailAddress.add(address);
                    }
                }
            } else {
                for (String address : FacilioUtil.splitByComma(emailAddressString)) {
                	address = MailMessageUtil.getEmailFromPrettifiedFromAddress.apply(address);
                    if (address != null && address.contains("@") && (!checkActive || checkIfActiveUserFromEmail(address))) {
                        emailAddress.add(address);
                    }
                }
            }
        }
        return emailAddress;
    }

}
