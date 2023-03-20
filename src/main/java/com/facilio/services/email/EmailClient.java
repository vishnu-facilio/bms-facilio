package com.facilio.services.email;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
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
import com.facilio.mailtracking.context.MailEnums;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
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
            LOGGER.info("OG_MAIL_LOG :: Sending email to user who is not in the org  - " + email);
        	return true;
        }
        return user.getUserStatus();
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
        return pushToEmailPreprocessQueue(mailJson, files, handleUserDelegation, true);
    }

    private long sendEmail(JSONObject mailJson, Map<String, String> files, boolean isActive) throws Exception {
        return pushToEmailPreprocessQueue(mailJson, files, false, isActive);
    }

    private long pushToEmailPreprocessQueue(JSONObject mailJson, Map<String, String> files, boolean handleUserDelegation, boolean isActive) throws Exception {
        try {
            if(canTrack(mailJson)) {
                mailJson.put(MailConstants.Params.FILES, files);
                mailJson.put(MailConstants.Params.HANDLE_DELEGATION, handleUserDelegation);
                mailJson.put(MailConstants.Params.IS_ACTIVE, isActive);

                long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
                String topicIdentifier = OutgoingMailAPI.getTopicIdentifier(mailJson, orgId);
                SessionManager.getInstance().sendMessage(new Message()
                        .setTopic(Topics.Mail.prepareOutgoingMail + "/" + topicIdentifier)
                        .setOrgId(orgId)
                        .setContent(mailJson));
                LOGGER.info("OG_MAIL_LOG :: Pushing outgoing mail content preprocess queue/wms for topic ::" + topicIdentifier);
            } else {
                prepareAndPushOutgoingMail(mailJson, files, handleUserDelegation, isActive);
            }
        } catch (Exception e) {
            LOGGER.error("OG_MAIL_ERROR :: outgoing mail preprocessing push failed [BEFORE-1st-QUEUE]. " + mailJson, e);
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION);
        }
        return -1;
    }

    /**
     * Used to log outgoing mail tracking info. Don't use this method directly.
     * Use {@link #sendEmailWithActiveUserCheck(JSONObject, Map)} instead.
     */
    @Deprecated
    public long prepareAndPushOutgoingMail(JSONObject mailJson, Map<String, String> files, boolean handleUserDelegation, boolean isActive) throws Exception {
        if(isActive) {
            if(!removeInActiveUsers(mailJson)) {
                return -1;
            }
            if(handleUserDelegation) {
                checkUserDelegation(mailJson);
            }
        }
        if(canTrack(mailJson)) {
            if(!isActive) {
                preserveOriginalEmailAddress(mailJson);
            }
            return pushEmailToQueue(mailJson, files);
        }
        return sendMailWithoutTracking(mailJson, files);
    }

    private boolean canTrack(JSONObject mailJson) throws Exception {
        boolean isSignUp = Constants.getModBean().getModule(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER) == null;
        if(isSignUp) {
            return false;
        }
        boolean isLicenseEnabled = AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.EMAIL_TRACKING);
        boolean trackingConfFound = StringUtils.isNotEmpty(DBConf.getInstance().getMailTrackingConfName());
        boolean doTracking = (boolean) mailJson.getOrDefault("_tracking", true);
        return isLicenseEnabled && trackingConfFound && doTracking;
    }

    private long pushEmailToQueue(JSONObject mailJson, Map<String, String> files) throws Exception {
        FacilioChain chain = MailTransactionChainFactory.pushOutgoingMailToQueue();
        FacilioContext context = chain.getContext();
        try {
            //new behaviour for non-prod env
            context.put(MailConstants.Params.MAIL_JSON, mailJson);
            context.put(MailConstants.Params.FILES, files);
            chain.execute();
            return (long) context.getOrDefault(MailConstants.Params.LOGGER_ID, -1);
        } catch (Exception e) {
            MailEnums.MailStatus mailStatus = MailEnums.MailStatus.valueOf(
                    (String) mailJson.getOrDefault(MailConstants.Params.MAIL_STATUS, MailEnums.MailStatus.TRIGGERED.name()));
            if(mailStatus == MailEnums.MailStatus.INVALID) {
                return -1;
            }
            LOGGER.error("OG_MAIL_ERROR :: outgoing mail tracking failed [BEFORE-QUEUE]. So sending in normal flow :: "+mailJson, e);
            OutgoingMailAPI.triggerFallbackMailSendChain(context);
            return -1;
        }
    }

    public long sendMailWithoutTracking(JSONObject mailJson, Map<String, String> files) throws Exception {
        LOGGER.info("OG_MAIL_LOG :: Sending mail without tracking");
        FacilioChain chain = MailTransactionChainFactory.sendNormalMailChain();
        FacilioContext context = chain.getContext();
        context.put(MailConstants.Params.MAIL_JSON, mailJson);
        context.put(MailConstants.Params.FILES, files);
        chain.execute();

        OutgoingMailAPI.resetMailJson(mailJson);
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

        OutgoingMailAPI.resetMailJson(mailJson);
        preserveOriginalEmailAddress(mailJson);
    }

    private Set<String> changeEmailToDelegatedUserEmail(Set<String> toEmails) throws Exception {

        if(!CollectionUtils.isEmpty(toEmails)) {

            Set<String> toEmailsList =new HashSet<>();
            UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");

            for(String email : toEmails){
                User user = userBean.getUserFromEmail(email, null, AccountUtil.getCurrentOrg().getOrgId(), true);
                if(user != null) {
                    List<User> delegatedUsers = DelegationUtil.getDelegatedUsers(user, DateTimeUtil.getCurrenTime(), DelegationType.EMAIL_NOTIFICATION);
                    if(CollectionUtils.isNotEmpty(delegatedUsers)){
                        Set<String> delegatedUsersEmails=delegatedUsers.stream().map(User::getEmail).collect(Collectors.toSet());
                        toEmailsList.addAll(delegatedUsersEmails);
                    }
                    else{
                        toEmailsList.add(email);
                    }
                }else{
                    toEmailsList.add(email);
                }
            }

            return toEmailsList;
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
                LOGGER.info(MessageFormat.format("OG_MAIL_LOG :: Not sending email since ''to address'' ({0}) is empty after removing inactive users", mailJson.get(TO)));
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
            Set<String> originalEmailAddresses = new HashSet<>();
            for(String origAddr : FacilioUtil.splitByComma(emailAddressString)) {
                Pair<String, String> emailMeta = MailMessageUtil.getUserNameAndEmailAddress.apply(origAddr);
                String address = emailMeta.getKey();
                if(!FacilioProperties.isProduction()) {
                    if (address != null && address.contains("@facilio.com") && (!checkActive || checkIfActiveUserFromEmail(address))) {
                        emailAddress.add(address);
                        originalEmailAddresses.add(origAddr);
                    }
                } else {
                    if (address != null && address.contains("@") && (!checkActive || checkIfActiveUserFromEmail(address))) {
                        emailAddress.add(address);
                        originalEmailAddresses.add(origAddr);
                    }
                }
            }
            preserveOriginalEmailAddress(mailJson, key, originalEmailAddresses);
        }
        return emailAddress;
    }

    private void preserveOriginalEmailAddress(JSONObject mailJson, String key, Set<String> emailAddresses) {
        if(emailAddresses.isEmpty()) {
            return;
        }
        String originalKey = "original"+StringUtils.capitalize(key);
        String originalAddress = (String) mailJson.get(originalKey);
        if(StringUtils.isNotEmpty(originalAddress)) {
            return;
        }
        mailJson.put(originalKey, combineEmailsAgain(emailAddresses));
    }

    private void preserveOriginalEmailAddress(JSONObject mailJson) throws Exception {
        getEmailAddresses(mailJson, TO, false);
        getEmailAddresses(mailJson, CC, false);
        getEmailAddresses(mailJson, BCC, false);
    }

}
