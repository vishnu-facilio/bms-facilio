package com.facilio.mailtracking.commands;

import com.facilio.db.transaction.NewTransactionService;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.bean.MailBean;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.Map;

@Log4j
public class ParseMailResponseCommand {

    public static void executeCommand(AwsMailResponseContext awsMailResponse) throws Exception {
        String mapperId = String.valueOf(awsMailResponse.getMapperId());
        Map<String, Object> mapperRecord = OutgoingMailAPI.getMapperRecord(mapperId);
        V3Util.throwRestException(mapperRecord.isEmpty(), ErrorCode.VALIDATION_ERROR,
                "OG_MAIL_ERROR :: Given MAPPER_ID not found :: "+mapperId);
        long orgId = FacilioUtil.parseLong(mapperRecord.get("orgId"));

        NewTransactionService.newTransaction(() -> updateMailResponseStatus(orgId, awsMailResponse));
    }

    private static void updateMailResponseStatus(long orgId,AwsMailResponseContext awsMailResponse) throws Exception {
        JSONObject response = awsMailResponse.getResponse();
        String eventType = awsMailResponse.getEventType();
        String mapperId = String.valueOf(awsMailResponse.getMapperId());
        MailBean mailBean = MailConstants.getMailBean(orgId);
        switch(eventType) { // handled event types
            case "Delivery" :
                mailBean.updateDeliveryStatus(mapperId, (Map<String, Object>) response.get("delivery"));
                break;
            case "Bounce" :
                mailBean.updateBounceStatus(mapperId, (Map<String, Object>) response.get("bounce"));
                break;
            default:
                LOGGER.info("OG_MAIL_NOTIFY :: Unhandled eventType detected :: "+eventType);
        }
    }

}
