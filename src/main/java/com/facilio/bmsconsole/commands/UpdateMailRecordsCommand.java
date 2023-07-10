package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UpdateMailRecordsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<BaseMailMessageContext> customMailContext = Constants.getRecordList((FacilioContext) context);
        if(!CollectionUtils.isEmpty(customMailContext)) {
            customMailContext =  getInboundMailModuleData(customMailContext);
        }
        return false;
    }
    public List<BaseMailMessageContext> getInboundMailModuleData(List<BaseMailMessageContext>  recordList ) throws Exception {

        ModuleBean modBean = Constants.getModBean();

        List<FacilioField> emailtoModuleDataFields = modBean.getAllFields(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME);
        Map<String, FacilioField> emailtoModuleDataFieldsMap = FieldFactory.getAsMap(emailtoModuleDataFields);

        List<FacilioField> emailConversionThreadFields = modBean.getAllFields(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
        Map<String, FacilioField> emailConversionThreadFieldMap = FieldFactory.getAsMap(emailConversionThreadFields);

        List<Long> recordIds = recordList.stream().map(BaseMailMessageContext::getId).collect(Collectors.toList());

        SelectRecordsBuilder<EmailToModuleDataContext> emailtoModuleDataBuilder = new SelectRecordsBuilder<EmailToModuleDataContext>()
                .select(emailtoModuleDataFields)
                .beanClass(EmailToModuleDataContext.class)
                .moduleName(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME)
                .andCondition(CriteriaAPI.getCondition(emailtoModuleDataFieldsMap.get("parentBaseMail"), StringUtils.join(recordIds, ','), NumberOperators.EQUALS));

        List<EmailToModuleDataContext> emailToModuleDatas = emailtoModuleDataBuilder.get();

        SelectRecordsBuilder<EmailConversationThreadingContext> emailConversionThreadBuilder = new SelectRecordsBuilder<EmailConversationThreadingContext>()
                .select(emailConversionThreadFields)
                .beanClass(EmailConversationThreadingContext.class)
                .moduleName(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME)
                .andCondition(CriteriaAPI.getCondition(emailConversionThreadFieldMap.get("parentBaseMail"), StringUtils.join(recordIds, ','), NumberOperators.EQUALS));

        List<EmailConversationThreadingContext> emailConversionThreadDatas = emailConversionThreadBuilder.get();
        Map<Long,EmailToModuleDataContext> emailToModuleBaseMailMap = emailToModuleDatas.stream().collect(Collectors.toMap(EmailToModuleDataContext::getParentBaseMailId, Function.identity()));
        Map<Long,EmailConversationThreadingContext> emailConversionThreadBaseMailMap = emailConversionThreadDatas.stream().collect(Collectors.toMap(EmailConversationThreadingContext::getParentBaseMailId, Function.identity()));
        for(BaseMailMessageContext record : recordList) {
            Long recordId = record.getId();

            if (emailToModuleBaseMailMap.get(recordId) != null) {
                record.setDatum(FacilioConstants.ContextNames.STATUS, true);
                record.setDatum(FacilioConstants.ContextNames.RECORD_ID, emailToModuleBaseMailMap.get(recordId).getRecordId());
                record.setDatum(FacilioConstants.ContextNames.RELATION_MODULE_NAME, modBean.getModule(emailToModuleBaseMailMap.get(recordId).getDataModuleId()).getDisplayName());
            } else if (emailConversionThreadBaseMailMap.get(recordId) != null) {
                record.setDatum(FacilioConstants.ContextNames.STATUS, true);
                record.setDatum(FacilioConstants.ContextNames.RECORD_ID, emailConversionThreadBaseMailMap.get(recordId).getRecordId());
                record.setDatum(FacilioConstants.ContextNames.RELATION_MODULE_NAME, modBean.getModule(emailConversionThreadBaseMailMap.get(recordId).getDataModuleId()).getDisplayName());
            } else {
                record.setDatum(FacilioConstants.ContextNames.STATUS, false);
            }

        }
        return recordList;
    }
}
