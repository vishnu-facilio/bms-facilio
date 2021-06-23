package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class UpdateWoIdInNewAlarmCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		AlarmOccurrenceContext alarmOccurrence = (AlarmOccurrenceContext) context.get(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		if (alarmOccurrence != null) {
			alarmOccurrence.setWoId(wo.getId());
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			
			FacilioField woField = FieldFactory.getAsMap(modBean.getAllFields(module.getName())).get("woId");
			UpdateRecordBuilder<AlarmOccurrenceContext> builder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
					.module(module)
					.fields(Collections.singletonList(woField))
					.andCondition(CriteriaAPI.getIdCondition(alarmOccurrence.getId(), module));
			builder.update(alarmOccurrence);

			BaseAlarmContext baseAlarm = alarmOccurrence.getAlarm();
			if (baseAlarm.getLastOccurrenceId() == alarmOccurrence.getId()) {
				UpdateRecordBuilder<BaseAlarmContext> alarmUpdateBuilder = new UpdateRecordBuilder<BaseAlarmContext>()
						.module(modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM))
						.fields(Collections.singletonList(modBean.getField("lastWoId", FacilioConstants.ContextNames.BASE_ALARM)))
						.andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(), modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM)));
				baseAlarm.setLastWoId(alarmOccurrence.getWoId());
				alarmUpdateBuilder.update(baseAlarm);
			}
		}
		return false;
	}

}
