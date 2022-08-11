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
        JSONObject mail = awsResponses.getMail();
        JSONObject delivery = awsResponses.getDelivery();
        JSONObject bounce = awsResponses.getBounce();

        Map<String, Object> tags = (Map<String, Object>) mail.get("tags");
        V3Util.throwRestException(tags.isEmpty(), ErrorCode.VALIDATION_ERROR, "Tags can't be null");

        List<String> mapperIdList = (List<String>) tags.get(MailConstants.Params.MAPPER_ID);
        V3Util.throwRestException(CollectionUtils.isEmpty(mapperIdList), ErrorCode.VALIDATION_ERROR, "mapperId can't be null");

        String mapperId = mapperIdList.get(0);
        MailBean mailBean = getMailBeanWithCurrentOrg(mapperId);

        if(!(delivery == null || delivery.isEmpty())) {
            mailBean.updateDeliveryStatus(mapperId, delivery);
            LOGGER.info("Delivery status updated successfully for mapperId  : "+mapperIdList);
        }

        if(!(bounce == null || bounce.isEmpty())) {
            mailBean.updateBounceStatus(mapperId, bounce);
            LOGGER.info("Bounce status updated successfully for mapperId  : "+mapperIdList);
        }
        LOGGER.info("Mail response status has been updated for SendMail with id : "+ mapperId);
        return mapperId;
    }

    private static MailBean getMailBeanWithCurrentOrg(String mapperId) throws Exception {
        Map<String, Object> mapperRecord = OutgoingMailAPI.getMapperRecord(mapperId);
        V3Util.throwRestException(mapperRecord.isEmpty(), ErrorCode.VALIDATION_ERROR, "Given mapperId not found :: "+mapperId);
        long orgId = FacilioUtil.parseLong(mapperRecord.get("orgId"));
        return MailConstants.getMailBean(orgId);
    }

}
