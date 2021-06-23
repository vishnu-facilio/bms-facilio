package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.activity.AlarmActivityType;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

public class UpdateAlarmOccurrenceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		AlarmOccurrenceContext alarmOccurrence = (AlarmOccurrenceContext) context.get(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
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
			if (alarmOccurrence.getSeverity() != null && alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
				alarmOccurrence.setClearedTime(currentTimeMillis);
				info.put("field", "Severity");
				info.put("newValue", AlarmAPI.getAlarmSeverity("Clear").getDisplayName());
				info.put("clearedBy", AccountUtil.getCurrentUser().getId());
				CommonCommandUtil.addEventType(EventType.ALARM_CLEARED, (FacilioContext) context);
			}

			Condition idCondition = CriteriaAPI.getIdCondition(recordIds, occurrenceModule);
			UpdateRecordBuilder<AlarmOccurrenceContext> occurrenceUpdateBuilder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
					.module(occurrenceModule)
					.fields(updateOnlyOccurrenceFields)
					.andCondition(idCondition);
			occurrenceUpdateBuilder.update(alarmOccurrence);
			
			BaseAlarmContext baseAlarm = new BaseAlarmContext();
			alarmOccurrence.updateAlarm(baseAlarm);
			baseAlarm.setLastOccurrence(null); // should not update occurrence id when updating in bulk
			if (baseAlarm.getSeverity() != null && baseAlarm.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
				baseAlarm.setLastClearedTime(currentTimeMillis);
			}

			FacilioModule alarmModule = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
			List<FacilioField> alarmFields = modBean.getAllFields(alarmModule.getName());
			Map<String, FacilioField> alarmFieldMap = FieldFactory.getAsMap(alarmFields);
			List<FacilioField> updateOnlyAlarmFields = new ArrayList<>();
			updateOnlyAlarmFields.add(alarmFieldMap.get("severity"));
			updateOnlyAlarmFields.add(alarmFieldMap.get("acknowledged"));
			updateOnlyAlarmFields.add(alarmFieldMap.get("acknowledgedBy"));
			updateOnlyAlarmFields.add(alarmFieldMap.get("acknowledgedTime"));
			updateOnlyAlarmFields.add(alarmFieldMap.get("lastClearedTime"));
			
			UpdateRecordBuilder<BaseAlarmContext> alarmUpdateBuilder = new UpdateRecordBuilder<BaseAlarmContext>()
					.module(alarmModule)
					.fields(updateOnlyAlarmFields)
					.andCondition(CriteriaAPI.getCondition("LAST_OCCURRENCE_ID", "lastOccurrenceId", StringUtils.join(recordIds, ','), NumberOperators.EQUALS));
			alarmUpdateBuilder.withChangeSet(BaseAlarmContext.class);
			alarmUpdateBuilder.update(baseAlarm);
			Map<Long, List<UpdateChangeSet>> changeSet = alarmUpdateBuilder.getChangeSet();

			List<AlarmOccurrenceContext> alarmOccurrences = NewAlarmAPI.getAlarmOccurrences(recordIds);
			if (CollectionUtils.isNotEmpty(alarmOccurrences)) {

				Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = new HashMap<>();
				Map<String, List> recordMap = new HashMap<>();
				for (AlarmOccurrenceContext occurrence: alarmOccurrences) {
					String moduleName = NewAlarmAPI.getAlarmModuleName(occurrence.getAlarm().getTypeEnum());
					List list = recordMap.get(moduleName);
					if (list == null) {
						list = new ArrayList();
						recordMap.put(moduleName, list);
					}
					list.add(occurrence.getAlarm());

					Map<Long, List<UpdateChangeSet>> longListMap = changeSetMap.get(moduleName);
					if (longListMap == null) {
						longListMap = new HashMap<>();
						changeSetMap.put(moduleName, longListMap);
					}
					longListMap.put(occurrence.getAlarm().getId(), changeSet.get(occurrence.getAlarm().getId()));
					CommonCommandUtil.addAlarmActivityToContext(occurrence.getAlarm().getId(),-1, AlarmActivityType.CLEAR_ALARM, info, (FacilioContext) context, occurrence.getId());

				}
				context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
				context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, changeSetMap);
			}
		}
		return false;
	}

}
