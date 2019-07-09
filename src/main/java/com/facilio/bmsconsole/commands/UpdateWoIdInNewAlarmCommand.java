package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateWoIdInNewAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
		}
		return false;
	}

}
