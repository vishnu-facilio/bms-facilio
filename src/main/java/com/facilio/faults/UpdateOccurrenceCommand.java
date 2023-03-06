package com.facilio.faults;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j2
public class UpdateOccurrenceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        List<Map<String, Object>> bulkRawInput = (List<Map<String, Object>>) context.get("bulkRawInput");
        for (Map<String, Object> rawInput : bulkRawInput) {
            AlarmOccurrenceContext alarmOccurrence = new AlarmOccurrenceContext();
            alarmOccurrence.setAcknowledged((Boolean) rawInput.get("acknowledged"));
            Long severityId = (Long) ((HashMap) rawInput.get("severity")).get("id");
            String severityName = AlarmAPI.getAlarmSeverity(severityId).getSeverity();
            ObjectMapper mapper = new ObjectMapper();
            AlarmSeverityContext alarmSeverityContext = mapper.convertValue(rawInput.get("severity"), AlarmSeverityContext.class);
            alarmOccurrence.setSeverity(alarmSeverityContext);

            if (CollectionUtils.isNotEmpty(recordIds) && alarmOccurrence != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

                FacilioModule occurrenceModule = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
                List<FacilioField> occurrenceFields = modBean.getAllFields(occurrenceModule.getName());
                Map<String, FacilioField> occurrenceFieldMap = FieldFactory.getAsMap(occurrenceFields);
                List<FacilioField> updateOnlyOccurrenceFields = new ArrayList<>();
                updateOnlyOccurrenceFields.add(occurrenceFieldMap.get("severity"));
                updateOnlyOccurrenceFields.add(occurrenceFieldMap.get("acknowledged"));
                updateOnlyOccurrenceFields.add(occurrenceFieldMap.get("acknowledgedBy"));
                updateOnlyOccurrenceFields.add(occurrenceFieldMap.get("acknowledgedTime"));
                updateOnlyOccurrenceFields.add(occurrenceFieldMap.get("clearedTime"));
                updateOnlyOccurrenceFields.add(occurrenceFieldMap.get("duration"));

                long currentTimeMillis = System.currentTimeMillis();
                JSONObject info = new JSONObject();

                if (alarmOccurrence.isAcknowledged()) {
                    alarmOccurrence.setAcknowledgedTime(currentTimeMillis);
                    alarmOccurrence.setAcknowledgedBy(AccountUtil.getCurrentUser());
                    info.put("field", "Acknowledge");
                    info.put("newValue", "true");
                    info.put("acknowledgedBy", alarmOccurrence.getAcknowledgedBy().getId());
                    info.put("acknowledgedTime", alarmOccurrence.getAcknowledgedTime());
                }
                if (severityName.equals("Clear")) {
                    alarmOccurrence.setClearedTime(currentTimeMillis);
                    info.put("field", "Severity");
                    info.put("newValue", AlarmAPI.getAlarmSeverity("Clear").getDisplayName());
                    info.put("clearedBy", AccountUtil.getCurrentUser().getId());
                    CommonCommandUtil.addEventType(EventType.ALARM_CLEARED, (FacilioContext) context);
                }

                Condition alarmCondition = CriteriaAPI.getCondition("ALARM_ID", "alarm", StringUtils.join(recordIds, ","), NumberOperators.EQUALS);
                UpdateRecordBuilder<AlarmOccurrenceContext> occurrenceUpdateBuilder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
                        .module(occurrenceModule)
                        .fields(updateOnlyOccurrenceFields)
                        .andCondition(alarmCondition);
                occurrenceUpdateBuilder.update(alarmOccurrence);
            }
            FacilioChain c = TransactionChainFactory.getV2UpdateAlarmChain();
            c.execute(context);
        }
        return false;
    }

}

