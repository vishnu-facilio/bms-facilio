package com.facilio.wmsv2.handler;

import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.bean.MailBean;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TopicHandler(
        topic = Topics.Mail.outgoingMail + "/#",
        sendToAllWorkers = false,
        priority = -5
)
@Log4j
public class OutgoingMailHandler extends BaseHandler {

    @Override
    public Message processOutgoingMessage(Message message) {
        Long orgId = message.getOrgId();
        try {
            if(orgId != null && orgId > 0) {
                JSONObject mailJson = message.getContent();

                Long mapperId = registerOutgoingMailMapper(orgId);
                mailJson.put(MailConstants.Params.MAPPER_ID, mapperId);

                MailBean mailBean = MailConstants.getMailBean(orgId);
                mailBean.trackAndSendMail(mailJson);
                LOGGER.info("Processing mail from queue");
            }
        } catch (Exception e) {
            LOGGER.info("ERROR IN OutgoingMailHandler for orgId "+ orgId, e);
        }
        return null;
    }

    private Long registerOutgoingMailMapper(long orgId) throws Exception {
        Map<String, Object> row = new HashMap<>();
        row.put("orgId", orgId);
        row.put("sysCreatedTime", System.currentTimeMillis());
        FacilioModule module = ModuleFactory.getMailMapperModule();
        List<FacilioField> fields = FieldFactory.getMailMapperFields();
        Long mapperId = OutgoingMailAPI.insert(module, fields, row);
        return mapperId;
    }
}
