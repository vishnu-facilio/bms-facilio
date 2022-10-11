package com.facilio.mailtracking.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.MailEnums;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OutgoingMailStatsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject data = new JSONObject();

        long startTime = (long) context.get("startTime");
        long endTime = (long) context.get("endTime");
        if(startTime == 0) {
            startTime = DateTimeUtil.getDayStartTime();
            endTime = DateTimeUtil.getDayEndTimeOf(startTime);
        }

        List<V3OutgoingMailLogContext> records = getRecords(startTime, endTime);
        data.put("totalCount", records.size());
        data.put("startTime", convertToDateTime(startTime));

        if(endTime!=-1) {
            data.put("endTime", convertToDateTime(endTime));
        }


        Map<String, Object> lastRecordMap = new HashMap<>();
        if(records.size() != 0) {
            V3OutgoingMailLogContext lastRow = records.get(records.size()-1);
            lastRecordMap.put("id", lastRow.getId());
            lastRecordMap.put("ttime", lastRow.getSysCreatedTime());
            lastRecordMap.put("datetime", convertToDateTime(lastRow.getSysCreatedTime()));
            lastRecordMap.put("mailstatus", lastRow.getMailStatus().toString());
            lastRecordMap.put("timezone", AccountUtil.getCurrentOrg().getTimezone());
        }
        data.put("lastRecord", lastRecordMap);
        Map<Long, V3OutgoingMailLogContext> recordsMap = records.stream().collect(
                Collectors.toMap(r->r.getId(), r->r));

        Map<MailEnums.MailStatus, List<Long>> typeMap = records.stream().collect(
                Collectors.groupingBy(r -> r.getMailStatus(),
                        Collectors.mapping(r -> r.getId(), Collectors.toList())));

        Map<String, Object> mailStatusMeta = new HashMap<>();
        typeMap.entrySet().forEach(en -> {
            String mailStatus = en.getKey().toString();

            Map<String, Object> mailTypeMeta = new HashMap<>();
            int msCount = en.getValue().size();
            mailTypeMeta.put("count", en.getValue().size());
            if(msCount != 0) {
                V3OutgoingMailLogContext lastRecord = recordsMap.get(en.getValue().get(msCount-1));
                mailTypeMeta.put("id", lastRecord.getId());
                mailTypeMeta.put("ttime", lastRecord.getSysCreatedTime());
                mailTypeMeta.put("datetime", convertToDateTime(lastRecord.getSysCreatedTime()));
            }
            mailStatusMeta.put(mailStatus, mailTypeMeta);
        });

        data.put("mailstatusMeta", mailStatusMeta);
        context.put("data", data);
        return false;
    }

    private Object convertToDateTime(long ttime) {
        ZonedDateTime zdt = DateTimeUtil.getDateTime(ttime, false);
        return zdt.toString().replace("T", " ").split("\\.")[0];
    }

    private List<V3OutgoingMailLogContext> getRecords(long startTime, long endTime) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        String moduleName = MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER;
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        FacilioField createdTime = fieldMap.get("sysCreatedTime");

        Condition startTimeCond = CriteriaAPI.getCondition(createdTime, startTime+"", NumberOperators.GREATER_THAN_EQUAL);
        Condition endTimeCond = CriteriaAPI.getCondition(createdTime, endTime+"", NumberOperators.LESS_THAN);


        SelectRecordsBuilder<V3OutgoingMailLogContext> selectBuilder = new SelectRecordsBuilder<V3OutgoingMailLogContext>()
                .beanClass(V3OutgoingMailLogContext.class)
                .table(module.getTableName())
                .module(module)
                .select(fields)
                .andCondition(startTimeCond);
        if(endTime!=-1) {
            selectBuilder.andCondition(endTimeCond);
        }
        return selectBuilder.get();

    }


}
