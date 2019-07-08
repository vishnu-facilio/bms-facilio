package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateAlarmOccurrenceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
			
			long currentTimeMillis = System.currentTimeMillis();
			if (alarmOccurrence.isAcknowledged()) {
				alarmOccurrence.setAcknowledgedTime(currentTimeMillis);
				alarmOccurrence.setAcknowledgedBy(AccountUtil.getCurrentUser());
			}
			if (alarmOccurrence.getSeverity() != null && alarmOccurrence.getSeverity().equals(AlarmAPI.getAlarmSeverity("Clear"))) {
				alarmOccurrence.setClearedTime(currentTimeMillis);
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
			alarmUpdateBuilder.update(baseAlarm);
		}
		return false;
	}

}
