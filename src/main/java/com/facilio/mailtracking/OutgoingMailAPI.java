package com.facilio.mailtracking;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.mailtracking.commands.ParseMailResponseCommand;
import com.facilio.mailtracking.context.AwsMailResponseContext;
import com.facilio.mailtracking.context.V3OutgoingMailAttachmentContext;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.mailtracking.context.V3OutgoingRecipientContext;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.email.EmailClient;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;
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
        V3Util.processAndUpdateSingleRecord(moduleName, record.getId(), row, null, null, null, null, null,null);
    }

    public static V3OutgoingMailLogContext convertToMailLogContext(JSONObject mailJson) throws IOException {
        V3OutgoingMailLogContext mailLogContext = FieldUtil.getAsBeanFromJson(mailJson, V3OutgoingMailLogContext.class);
        Object recModuleVal = mailJson.get("moduleId");
        if(mailJson.get("moduleId")!=null) {
            mailLogContext.setRecordsModuleId(FacilioUtil.parseLong(recModuleVal));
        }
        Object loggerId = mailJson.get(MailConstants.Params.LOGGER_ID);
        if(loggerId!=null) {
            mailLogContext.setId(FacilioUtil.parseLong(loggerId));
        }
        return mailLogContext;
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

    public static String parseMailResponse(AwsMailResponseContext awsMailResponseContext) throws Exception {
        return ParseMailResponseCommand.executeCommand(awsMailResponseContext);
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
        if(mailJson.get("htmlContent") != null) {
            mailJson.put(EmailClient.MESSAGE, mailJson.remove("htmlContent"));
        } else if(mailJson.get("textContent") != null) {
            mailJson.put(EmailClient.MESSAGE, mailJson.remove("textContent"));
        }
    }

    public static void restoreMailJson(JSONObject mailJson) {
        JSONObject header = (JSONObject) mailJson.getOrDefault(MailConstants.Params.HEADER, new JSONObject());
        if(!header.isEmpty()) {
            header.remove(MailConstants.Params.MAPPER_ID);
            header.remove(MailConstants.Params.REGION);
            mailJson.put(MailConstants.Params.HEADER, header);
        }
        restoreMailMessage(mailJson);
    }

    public static void restoreEmailAddress(JSONObject mailJson, String key) {
        String originalKey = "original"+ StringUtils.capitalize(key);
        if(mailJson.containsKey(originalKey)) {
            JSONObject emailMetaJson = (JSONObject) mailJson.get(originalKey);
            Set<Map.Entry> keys = emailMetaJson.entrySet();
            List<String> emailList = new ArrayList<>();
            for (Map.Entry en : keys) {
                emailList.add(MailMessageUtil.getOriginalEmailAddress.apply(en));
            }
            mailJson.put(key, emailList.stream().collect(Collectors.joining(",")));
        }
    }
}
