package com.facilio.mailtracking;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.mailtracking.commands.MailTransactionChainFactory;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.facilio.mailtracking.context.V3OutgoingMailAttachmentContext;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.mailtracking.context.V3OutgoingRecipientContext;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class OutgoingMailAPI {

    public static long insert(FacilioModule module, List<FacilioField> fields, Map<String, Object> row) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(fields);
        return builder.insert(row);
    }

    public static long insertV3(String moduleName, V3Context bean) throws Exception {
        insertV3(moduleName, Collections.singletonList(bean));
        return bean.getId();
    }

    public static <T extends ModuleBaseWithCustomFields> void insertV3(String moduleName, List<T> records) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        V3Util.createRecord(module, (List<ModuleBaseWithCustomFields>) records);
    }

    public static void updateV3(String moduleName, V3Context record) throws Exception {
        Map<String, Object> row = FieldUtil.getAsJSON(record);
        V3Util.processAndUpdateSingleRecord(moduleName, record.getId(), row, null, null, null, null, null,null, null, null,null);
    }

    public static long updateRecord(long id, String moduleName, Map<String, Object> row) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Condition condition = CriteriaAPI.getIdCondition(id, module);
        UpdateRecordBuilder builder = new UpdateRecordBuilder()
                .module(module)
                .fields(fields)
                .andCondition(condition);
        return builder.updateViaMap(row);
    }

    public static List<V3OutgoingMailAttachmentContext> getMailAttachments(long mailId) throws Exception {
        String moduleName = MailConstants.ModuleNames.OUTGOING_MAIL_ATTACHMENTS;
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        Condition mailIdCondn = CriteriaAPI.getCondition("MAIL_ID", "mailId", mailId+"", NumberOperators.EQUALS);
        SelectRecordsBuilder<V3OutgoingMailAttachmentContext> selectBuilder = new SelectRecordsBuilder<V3OutgoingMailAttachmentContext>()
                .beanClass(V3OutgoingMailAttachmentContext.class)
                .table(module.getTableName())
                .module(module)
                .select(fields)
                .andCondition(mailIdCondn);
        List<V3OutgoingMailAttachmentContext> rows = selectBuilder.get();
        return rows;
    }

    public static Map<String, Object> getMapperRecord(String mapperId) throws Exception {
        FacilioModule mapperModule = ModuleFactory.getMailMapperModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(mapperModule.getTableName())
                .select(FieldFactory.getMailMapperFields())
                .andCondition(CriteriaAPI.getIdCondition(mapperId, mapperModule));
        List<Map<String, Object>> mapperProps = selectBuilder.get();
        if(mapperProps.isEmpty()) {
            return Collections.emptyMap();
        }
        return mapperProps.get(0);
    }

    public static long getLoggerId(String mapperId) throws Exception {
        String moduleName = MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER;
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        Condition mapperCondn = CriteriaAPI.getCondition("MAPPER_ID", "mapperId", mapperId, NumberOperators.EQUALS);
        SelectRecordsBuilder<V3OutgoingMailLogContext> selectBuilder = new SelectRecordsBuilder<V3OutgoingMailLogContext>()
                .beanClass(V3OutgoingMailLogContext.class)
                .table(module.getTableName())
                .module(module)
                .select(fields)
                .andCondition(mapperCondn);
        List<V3OutgoingMailLogContext> rows = selectBuilder.get();
        if(!rows.isEmpty()) {
            return rows.get(0).getId();
        }
        return -1;
    }

    public static V3OutgoingMailLogContext getLoggerRecord(Long loggerId) throws Exception {
        String moduleName = MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER;;
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Condition idCond = CriteriaAPI.getCondition(FieldFactory.getIdField(module), loggerId+"", NumberOperators.EQUALS);
        SelectRecordsBuilder<V3OutgoingMailLogContext> selectBuilder = new SelectRecordsBuilder<V3OutgoingMailLogContext>()
                .beanClass(V3OutgoingMailLogContext.class)
                .table(moduleName)
                .module(module)
                .select(fields)
                .fetchSupplement((SupplementRecord) fieldMap.get(MailConstants.Email.HTML_CONTENT))
                .fetchSupplement((SupplementRecord) fieldMap.get(MailConstants.Email.TEXT_CONTENT))
                .andCondition(idCond);
        List<V3OutgoingMailLogContext> rows = selectBuilder.get();
        if(!CollectionUtils.isEmpty(rows)) {
            return rows.get(0);
        }
        return null;
    }
    public static List<V3OutgoingRecipientContext> getRecipients(long loggerId) throws Exception {
        return getRecipients(loggerId, null);
    }

    public static List<V3OutgoingRecipientContext> getRecipients(long loggerId, String recipients) throws Exception {
        String moduleName = MailConstants.ModuleNames.OUTGOING_RECIPIENT_LOGGER;
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        Condition loggerCondn = CriteriaAPI.getCondition("LOGGER", "logger", loggerId+"", NumberOperators.EQUALS);

        SelectRecordsBuilder<V3OutgoingRecipientContext> selectBuilder = new SelectRecordsBuilder<V3OutgoingRecipientContext>()
                .beanClass(V3OutgoingRecipientContext.class)
                .table(module.getTableName())
                .module(module)
                .select(fields)
                .andCondition(loggerCondn);
        if(StringUtils.isNotEmpty(recipients)) {
            Condition recipientCond = CriteriaAPI.getCondition("RECIPIENT", "recipient", recipients, StringOperators.IS);
            selectBuilder.andCondition(recipientCond);
        }
        return selectBuilder.get();
    }

    public static void updateRecipientStatus(List<V3OutgoingRecipientContext> records, Integer status) throws Exception {
        String moduleName = MailConstants.ModuleNames.OUTGOING_RECIPIENT_LOGGER;
        List<Long> ids = records.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());
        Map<String, Object> record = new HashMap<>();
        record.put("status", status);
        V3Util.updateBulkRecords(moduleName, record, ids, false);
    }

    public static void restoreMailMessage(JSONObject mailJson) {
        if(mailJson.get(MailConstants.Email.MESSAGE) != null){
            return;
        }
        mailJson.put(MailConstants.Email.MESSAGE, getMailMessage(mailJson));
    }

    private static Object getMailMessage(JSONObject mailJson) {
        if(mailJson.get(MailConstants.Email.HTML_CONTENT) != null) {
            return mailJson.remove(MailConstants.Email.HTML_CONTENT);
        } else if(mailJson.get(MailConstants.Email.TEXT_CONTENT) != null) {
            return mailJson.remove(MailConstants.Email.TEXT_CONTENT);
        } else { // if there is no content has been set. setting dummy content
            return "";
        }
    }

    public static void restoreEmailAddress(JSONObject mailJson, String key) {
        String originalKey = "original"+ StringUtils.capitalize(key);
        if(mailJson.containsKey(originalKey)) {
            mailJson.put(key, mailJson.get(originalKey));
        }
    }

    public static void logResponses(AwsMailResponseContext awsMailResponse) {
        try {
            Map<String, Object> row = FieldUtil.getAsJSON(awsMailResponse);
            row.put("sysCreatedTime", System.currentTimeMillis());
            row.put("response", awsMailResponse.getResponse());
            FacilioModule module = ModuleFactory.getMailResponseModule();
            List<FacilioField> fields = FieldFactory.getMailResponsesFields();
            OutgoingMailAPI.insert(module, fields, row);
        } catch (Exception e) {
            LOGGER.error("OG_MAIL_WARN :: Not able log the mail responses. Exception ::", e);
        }
    }

    public static void triggerFallbackMailSendChain(FacilioContext context) throws Exception {
        FacilioChain chain = MailTransactionChainFactory.getNoTrackingChain();
        chain.setContext(context);
        chain.execute();

        chain = MailTransactionChainFactory.triggerMailHandlerChain();
        chain.setContext(context);
        chain.execute();
    }

    public static void resetMailJson(JSONObject mailJson) {
        mailJson.remove(MailConstants.Email.ORIGINAL_TO);
        mailJson.remove(MailConstants.Email.ORIGINAL_CC);
        mailJson.remove(MailConstants.Email.ORIGINAL_BCC);
        mailJson.remove(MailConstants.Params.MASK_URL);
        mailJson.remove(MailConstants.Params.MAIL_STATUS);
        mailJson.remove(MailConstants.Email.CONTENT_TYPE);
        mailJson.remove(MailConstants.Email.TEXT_CONTENT);
        mailJson.remove(MailConstants.Email.HTML_CONTENT);
    }

    public static String getLogMeta(JSONObject mailJson) {
        Object mapperId = mailJson.get(MailConstants.Params.MAPPER_ID);
        Object loggerId = mailJson.get(MailConstants.Params.LOGGER_ID);
        String logMeta = " for [ MAPPER_ID="+mapperId+", LOGGER_ID="+loggerId+" ]";
        return logMeta;
    }

    public static String getTopicIdentifier(JSONObject mailJson, long orgId) {
        StringBuffer sb = new StringBuffer();
        sb.append(orgId);
        if(mailJson.containsKey(MailConstants.Params.SOURCE_TYPE)) {
            sb.append("/");
            sb.append(mailJson.get(MailConstants.Params.SOURCE_TYPE));
        }
        if(mailJson.containsKey(MailConstants.Params.RECORDS_MODULE_ID)) {
            sb.append("/");
            sb.append(mailJson.get(MailConstants.Params.RECORDS_MODULE_ID));
        }
        if(mailJson.containsKey(MailConstants.Params.RECORD_ID)) {
            sb.append("/");
            sb.append(mailJson.get(MailConstants.Params.RECORD_ID));
        }
        return sb.toString();
    }

}
