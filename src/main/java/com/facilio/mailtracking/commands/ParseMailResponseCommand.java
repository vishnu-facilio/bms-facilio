package com.facilio.mailtracking.commands;

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
        JSONObject response = awsMailResponse.getResponse();
        String eventType = awsMailResponse.getEventType();
        String mapperId = String.valueOf(awsMailResponse.getMapperId());
        MailBean mailBean = getMailBeanWithCurrentOrg(mapperId);

        switch(eventType) { // handled event types
            case "Delivery" :
                mailBean.updateDeliveryStatus(mapperId, (JSONObject) response.get("delivery"));
                break;
            case "Bounce" :
                mailBean.updateBounceStatus(mapperId, (JSONObject) response.get("bounce"));
                break;
            default:
                LOGGER.info("OG_MAIL_NOTIFY :: Unhandled eventType detected :: "+eventType);
        }
    }

    private static MailBean getMailBeanWithCurrentOrg(String mapperId) throws Exception {
        Map<String, Object> mapperRecord = OutgoingMailAPI.getMapperRecord(mapperId);
        V3Util.throwRestException(mapperRecord.isEmpty(), ErrorCode.VALIDATION_ERROR, "Given mapperId not found :: "+mapperId);
        long orgId = FacilioUtil.parseLong(mapperRecord.get("orgId"));
        return MailConstants.getMailBean(orgId);
    }

}
