package com.facilio.mailtracking.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class RunHistoricalMailCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long startId = (long) context.get("startId");
        long endId = (long) context.get("endId");
        boolean runAll = (boolean) context.get("runAll");
        if(endId == 0) {
            endId = startId;
            runAll = true;
        }

        JSONObject response = new JSONObject();
        ModuleBean modBean = Constants.getModBean();
        String moduleName = MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER;
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        Condition startIdCond = CriteriaAPI.getCondition(FieldFactory.getIdField(module), startId+"", NumberOperators.GREATER_THAN_EQUAL);
        Condition endIdCond = CriteriaAPI.getCondition(FieldFactory.getIdField(module), endId+"", NumberOperators.LESS_THAN_EQUAL);
        Condition recipientCount = CriteriaAPI.getCondition("RECIPIENT_COUNT", "recipientCount", "0", NumberOperators.EQUALS);

        SelectRecordsBuilder<V3OutgoingMailLogContext> selectBuilder = new SelectRecordsBuilder<V3OutgoingMailLogContext>()
                .beanClass(V3OutgoingMailLogContext.class)
                .table(module.getTableName())
                .module(module)
                .select(fields)
                .fetchSupplement((SupplementRecord) fieldMap.get("textContent"))
                .fetchSupplement((SupplementRecord) fieldMap.get("htmlContent"))
                .andCondition(startIdCond)
                .andCondition(endIdCond);
        if(!runAll) {
            selectBuilder.andCondition(recipientCount);
        }
        List<V3OutgoingMailLogContext> rows = selectBuilder.get();
        response.put("rowCount", rows.size());

        Map<Long, Long> attachmentMapper = new HashMap<>();
//        attachmentMapper.put(16428L, 1195617L);
//        attachmentMapper.put(16462L, 1195768L);
//        attachmentMapper.put(16464L, 1195617L);
//        attachmentMapper.put(16577L, 1196297L);

        FileStore fs = FacilioFactory.getFileStore();
        int successCnt = 0;
        for(V3OutgoingMailLogContext row : rows) {
            Long loggerId = null;
            try {
                FacilioChain chain = MailTransactionChainFactory.pushToMailTemp();
                FacilioContext newContext = chain.getContext();
                JSONObject mailJson = convertToMailJson(row);
                if (mailJson == null) {
                    continue;
                }
                loggerId = row.getId();
                newContext.put(MailConstants.Params.MAIL_JSON, mailJson);
                newContext.put(MailConstants.Params.LOGGER_ID, loggerId);
                if (attachmentMapper.keySet().contains(loggerId)) {
                    Map<String, String> files = new HashMap<>();
                    Long fileId = attachmentMapper.get(row.getId());
                    FileInfo fileInfo = fs.getFileInfo(fileId);
                    String downloadUrl = fs.getOrgiDownloadUrl(fileId);
                    files.put(fileInfo.getFileName(), downloadUrl);
                    newContext.put(MailConstants.Params.FILES, files);
                }
                chain.execute();
                successCnt++;
            } catch (Exception e) {
                LOGGER.error("Failed.. skipping LOGGER_ID :: "+loggerId, e);
            }
        }

        response.put("mailSent", successCnt);
        context.put("response", response);

        return false;
    }

    private static JSONObject convertToMailJson(V3OutgoingMailLogContext record){
        try {
            JSONObject mailJson = FieldUtil.getAsJSON(record);
            if(record.getHtmlContent()!=null) {
                mailJson.put("mailType", "html");
            }
            mailJson.put("sender", mailJson.get("from"));
            mailJson.put("originalTo", mailJson.get("to"));
            mailJson.put("originalBcc", mailJson.get("bcc"));
            mailJson.put("originalCc", mailJson.get("cc"));
            return mailJson;
        } catch (Exception e) {
            LOGGER.error("Failed to parse :: "+record.getId(), e);
            return null;
        }
    }
}
