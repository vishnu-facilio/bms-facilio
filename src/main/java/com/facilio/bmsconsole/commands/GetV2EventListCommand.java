package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetV2EventListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (recordId > 0) {
			AlarmOccurrenceContext alarmOccurrence = NewAlarmAPI.getAlarmOccurrence(recordId);
			if (alarmOccurrence != null) {
				Type alarmType = alarmOccurrence.getAlarm().getTypeEnum();
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(NewEventAPI.getEventModuleName(alarmType));
				List<FacilioField> allFields = modBean.getAllFields(module.getName());
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
				SelectRecordsBuilder<BaseEventContext> builder = new SelectRecordsBuilder<BaseEventContext>()
						.module(module)
						.select(allFields)
						.beanClass(NewEventAPI.getEventClass(alarmType))
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarmOccurrence"), String.valueOf(recordId), NumberOperators.EQUALS));
				List<BaseEventContext> list = builder.get();
				
				context.put(FacilioConstants.ContextNames.RECORD_LIST, list);
			}
		}
		return false;
	}

}
