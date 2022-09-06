package com.facilio.mailtracking.commands;

import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.bean.MailBean;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Log4j
public class ParseMailResponseCommand {

    public static String executeCommand(AwsMailResponseContext awsResponses) throws Exception {
        JSONObject response = awsResponses.getResponse();
        String eventType = awsResponses.getEventType();

        JSONObject mail = (JSONObject) response.get("mail");
        V3Util.throwRestException(mail.isEmpty(), ErrorCode.VALIDATION_ERROR, "mail can't be null");

        Map<String, Object> tags = (Map<String, Object>) mail.get("tags");
        V3Util.throwRestException(tags.isEmpty(), ErrorCode.VALIDATION_ERROR, "tags can't be null");

        List<String> mapperIdList = (List<String>) tags.get(MailConstants.Params.MAPPER_ID);
        V3Util.throwRestException(CollectionUtils.isEmpty(mapperIdList), ErrorCode.VALIDATION_ERROR, "mapperId can't be null");

        String mapperId = mapperIdList.get(0);
        OutgoingMailAPI.logResponses(mapperId, awsResponses);
        MailBean mailBean = getMailBeanWithCurrentOrg(mapperId);

        switch(eventType) {
            case "Delivery" :
                mailBean.updateDeliveryStatus(mapperId, (JSONObject) response.get("delivery"), false);
                break;
            case "DeliveryDelay" :
                mailBean.updateDeliveryStatus(mapperId, (JSONObject) response.get("deliveryDelay"), true);
                break;
            case "Bounce" :
                mailBean.updateBounceStatus(mapperId, (JSONObject) response.get("bounce"));
                break;
            default:
                LOGGER.info("OG_MAIL_NOTIFY :: Unknown eventType detected :: "+eventType);
        }
        return mapperId;
    }

    private static MailBean getMailBeanWithCurrentOrg(String mapperId) throws Exception {
        Map<String, Object> mapperRecord = OutgoingMailAPI.getMapperRecord(mapperId);
        V3Util.throwRestException(mapperRecord.isEmpty(), ErrorCode.VALIDATION_ERROR, "Given mapperId not found :: "+mapperId);
        long orgId = FacilioUtil.parseLong(mapperRecord.get("orgId"));
        return MailConstants.getMailBean(orgId);
    }

}
