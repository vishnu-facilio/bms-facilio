package com.facilio.mailtracking.bean;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.commands.MailTransactionChainFactory;
import com.facilio.mailtracking.context.Bounce;
import com.facilio.mailtracking.context.MailStatus;
import com.facilio.mailtracking.context.V3OutgoingRecipientContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class MailTrackBeanImpl implements MailBean {

    @Override
    public void updateDeliveryStatus(String mapperId, Map<String, Object> delivery) throws Exception {
        long loggerId = OutgoingMailAPI.getLoggerId(mapperId);
        V3Util.throwRestException(loggerId == -1, ErrorCode.VALIDATION_ERROR,
                "LOGGER_ID is not found for given MAPPER_ID :: "+mapperId);

        List<String> recipientsArr = (List<String>) delivery.get("recipients");
        V3Util.throwRestException(CollectionUtils.isEmpty(recipientsArr), ErrorCode.VALIDATION_ERROR,
                "recipients cant be empty");

        recipientsArr = recipientsArr.stream().map(email -> extractEmailAddress(email)).collect(Collectors.toList());

        Integer deliveryStatus = MailStatus.DELIVERED.getValue();
        List<V3OutgoingRecipientContext> oldRecords = OutgoingMailAPI.getRecipients(loggerId, StringUtils.join(recipientsArr, ", "));
        if(oldRecords.isEmpty()) {
            LOGGER.info("OG_MAIL_LOG :: [delivery status] Given mail addresses not found :: " +recipientsArr + " for MAPPER_ID :: "+mapperId);
            return;
        }
        oldRecords = oldRecords.stream().filter(oldRecord -> !oldRecord.getStatus().equals(deliveryStatus)).collect(Collectors.toList());

        if(oldRecords.isEmpty()) {
            LOGGER.info("OG_MAIL_LOG :: Already updated [delivery status] all the given email addresses");
            return;
        }
        OutgoingMailAPI.updateRecipientStatus(oldRecords, deliveryStatus);
        LOGGER.info("OG_MAIL_LOG :: Delivery status updated successfully for MAPPER_ID  : "+mapperId);
    }

    private String extractEmailAddress(String address) {
        return MailMessageUtil.getUserNameAndEmailAddress.apply(address).getKey();
    }

    @Override
    public void updateBounceStatus(String mapperId, Map<String, Object> bounce) throws Exception {
        long loggerId = OutgoingMailAPI.getLoggerId(mapperId);
        V3Util.throwRestException(loggerId == -1, ErrorCode.VALIDATION_ERROR, "LOGGER_ID is not found for given MAPPER_ID :: "+mapperId);

        String bounceType = (String) bounce.get("bounceType");
        List<Map<String, String>> recipientsArr = (List<Map<String, String>>) bounce.get("bouncedRecipients");

        String moduleName = MailConstants.ModuleNames.OUTGOING_RECIPIENT_LOGGER;
        Integer bounceStatus = MailStatus.BOUNCED.getValue();
        for(Map<String, String> recipient : recipientsArr) {
            String bouncedEmail = recipient.get("emailAddress");
            bouncedEmail = this.extractEmailAddress(bouncedEmail);

            List<V3OutgoingRecipientContext> bounceRecords = OutgoingMailAPI.getRecipients(loggerId, bouncedEmail);
            if(CollectionUtils.isEmpty(bounceRecords)) {
                LOGGER.info("OG_MAIL_LOG :: [bounce status] Given mail addresses not found :: " +bouncedEmail + " for MAPPER_ID :: "+mapperId);
                continue;
            }
            V3OutgoingRecipientContext oldRecord = bounceRecords.get(0);
            if(oldRecord.getStatus().equals(bounceStatus)) {
                LOGGER.info("OG_MAIL_LOG :: Skipping bounce status update for "+bouncedEmail+", since its already updated");
                continue; //already updated
            }
            String statusCode = recipient.get("status");
            Bounce.Reason bounceReason = Bounce.Reason.get(bounceType, statusCode);
            oldRecord.setStatus(bounceStatus);
            oldRecord.setBounceType(bounceReason.getBounce().getValue());
            oldRecord.setBounceReason(bounceReason.getReason());
            oldRecord.setDiagnosticCode(recipient.get("diagnosticCode"));

            OutgoingMailAPI.updateV3(moduleName, oldRecord);
        }
        LOGGER.info("OG_MAIL_LOG :: Bounce status updated successfully for MAPPER_ID  : "+mapperId);
    }

    @Override
    public void trackAndSendMail(JSONObject mailJson) throws Exception {
        FacilioContext context = NewTransactionService.newTransactionWithReturn(() -> prepareOutgoingMailContext(mailJson));
        if(FacilioProperties.isDevelopment()) {
            NewTransactionService.newTransaction(() -> sentOutgoingMail(context, mailJson));
        } else {
            sentOutgoingMail(context, mailJson);
        }
    }

    private FacilioContext prepareOutgoingMailContext(JSONObject mailJson) throws Exception {
        FacilioChain chain = MailTransactionChainFactory.outgoingMailPreChain();
        FacilioContext context = chain.getContext();
        try {
            context.put(MailConstants.Params.MAIL_JSON, mailJson);
            context.put(MailConstants.Params.FILES, mailJson.get(MailConstants.Params.FILES));
            context.put(MailConstants.Params.LOGGER_ID, mailJson.get(MailConstants.Params.LOGGER_ID));
            context.put(MailConstants.Params.MAPPER_ID, mailJson.get(MailConstants.Params.MAPPER_ID));
            chain.execute();
            return context;
        } catch (Exception e) {
            LOGGER.error("OG_MAIL_ERROR :: outgoing mail tracking failed in queue [PRE-SENDMAIL COMMAND]. So sending in normal flow :: "+mailJson, e);
            OutgoingMailAPI.triggerFallbackMailSendChain(context);
            return null;
        }
    }

    private void sentOutgoingMail(FacilioContext context, JSONObject mailJson) throws Exception {
        FacilioChain chain = MailTransactionChainFactory.sendOutgoingMailChain();
        if(context!=null) {
            try {
                chain.setContext(context);
                chain.execute();
            } catch (Exception e) {
                LOGGER.error("OG_MAIL_ERROR :: outgoing mail tracking failed in queue [SENDMAIL COMMAND]. So sending in normal flow :: "+mailJson, e);
                OutgoingMailAPI.triggerFallbackMailSendChain(context);
                return ;
            }

            chain = MailTransactionChainFactory.triggerMailHandlerChain();
            chain.setContext(context);
            chain.execute();
        }
    }

}
