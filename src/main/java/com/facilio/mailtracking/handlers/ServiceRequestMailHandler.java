package com.facilio.mailtracking.handlers;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.commands.SendEmailForEmailConversationThreadingCommand;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.mailtracking.OutgoingMailData;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class ServiceRequestMailHandler implements OutgoingMailData {
    private static final Logger LOGGER = LogManager.getLogger(SendEmailForEmailConversationThreadingCommand.class.getName());
    @Override
    public void loadMailData(V3OutgoingMailLogContext record) throws Exception {
        LOGGER.error("V3OutgoingMailLogContext record :: ------------ "+record);
        long id=record.getRecordId();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module= modBean.getModule(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
        List<FacilioField> fields=new ArrayList<>();
        Map<String,FacilioField> fieldMap=FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        fields.add(fieldMap.get("messageId"));
        UpdateRecordBuilder<EmailConversationThreadingContext> updateRecordBuilder = new UpdateRecordBuilder<EmailConversationThreadingContext>()
                .moduleName(module.getName())
                .fields(fields)
                .andCustomWhere("BaseMailMessage.ID=?",id);
        Map<String,Object> prop= new HashMap<>();
        prop.put("messageId",record.getMessageId());
        updateRecordBuilder.updateViaMap(prop);
        LOGGER.error("V3OutgoingMailLogContext update :: ------------ "+updateRecordBuilder);
    }

}
