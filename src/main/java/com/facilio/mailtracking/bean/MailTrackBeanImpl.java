package com.facilio.mailtracking.bean;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
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

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class MailTrackBeanImpl implements MailBean {

    @Override
    public void updateDeliveryStatus(String mapperId, JSONObject delivery) throws Exception {
        long loggerId = OutgoingMailAPI.getLoggerId(mapperId);
        V3Util.throwRestException(loggerId == -1, ErrorCode.VALIDATION_ERROR,
                "LoggerId is not found for given mapperId :: "+mapperId);

        List<String> recipientsArr = (List<String>) delivery.get("recipients");
        V3Util.throwRestException(CollectionUtils.isEmpty(recipientsArr), ErrorCode.VALIDATION_ERROR,
                "recipients cant be empty");

        Integer deliveryStatus = MailStatus.DELIVERED.getValue();
        List<V3OutgoingRecipientContext> oldRecords = OutgoingMailAPI.getRecipients(loggerId, StringUtils.join(recipientsArr, ", "));
        oldRecords = oldRecords.stream().filter(oldRecord -> !oldRecord.getStatus().equals(deliveryStatus)).collect(Collectors.toList());

        if(oldRecords.isEmpty()) {
            LOGGER.info("Already updated all the given email addresses");
            return;
        }

        OutgoingMailAPI.updateRecipientStatus(oldRecords, deliveryStatus);

    }

    @Override
    public void updateBounceStatus(String mapperId, JSONObject bounce) throws Exception {
        long loggerId = OutgoingMailAPI.getLoggerId(mapperId);
        V3Util.throwRestException(loggerId == -1, ErrorCode.VALIDATION_ERROR, "LoggerId is not found for given mapperId :: "+mapperId);

        String bounceType = (String) bounce.get("bounceType");
        List<Map<String, String>> recipientsArr = (List<Map<String, String>>) bounce.get("bouncedRecipients");

        String moduleName = MailConstants.ModuleNames.OUTGOING_RECIPIENT_LOGGER;
        Integer bounceStatus = MailStatus.BOUNCED.getValue();
        for(Map<String, String> recipient : recipientsArr) {
            String bouncedEmail = recipient.get("emailAddress");

            List<V3OutgoingRecipientContext> bounceRecords = OutgoingMailAPI.getRecipients(loggerId, bouncedEmail);
            V3OutgoingRecipientContext oldRecord = bounceRecords.get(0);
            if(oldRecord.getStatus().equals(bounceStatus)) {
                LOGGER.info("Skipping bounce status update for "+bouncedEmail+", since its already updated");
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
    }

    @Override
    public void trackAndSendMail(JSONObject mailJson) throws Exception {
        FacilioChain chain = MailTransactionChainFactory.sendOutgoingMailChain();
        FacilioContext context = chain.getContext();
        context.put(MailConstants.Params.MAIL_JSON, mailJson);
        context.put(MailConstants.Params.LOGGER_ID, mailJson.get(MailConstants.Params.LOGGER_ID));
        context.put(MailConstants.Params.MAPPER_ID, mailJson.get(MailConstants.Params.MAPPER_ID));
        chain.execute();

        chain = MailTransactionChainFactory.updateOutgoingMailChain();
        chain.setContext(context);
        chain.execute();
    }


}
