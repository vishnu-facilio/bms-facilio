package com.facilio.ims.handler;

import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.bean.MailBean;
import com.facilio.mailtracking.context.MailEnums;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class OutgoingMailHandler extends ImsHandler {

    public static String KEY = "__sendmail__/org";

    @Override
    public void processMessage(Message message) {
        Long orgId = message.getOrgId();
        Long mapperId = null;
        try {
            if(orgId != null && orgId > 0) {
                JSONObject mailJson = message.getContent();
                mapperId = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() ->  registerOutgoingMailMapper(orgId));
                LOGGER.info("OG_MAIL_LOG :: MAPPER_ID inserted :: "+mapperId +" for LOGGER_ID :: "+mailJson.get(MailConstants.Params.ID));
                mailJson.put(MailConstants.Params.MAPPER_ID, mapperId);
                mailJson.put(MailConstants.Params.MAIL_STATUS, MailEnums.MailStatus.IN_PROCESS.name());

                MailBean mailBean = MailConstants.getMailBean(orgId);
                mailBean.trackAndSendMail(mailJson);
            }
        } catch (Exception e) {
            LOGGER.error("OG_MAIL_ERROR :: ERROR IN [OutgoingMailHandler] for ORGID "+ orgId + " with MAPPER_ID ::"+mapperId, e);
        }
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
