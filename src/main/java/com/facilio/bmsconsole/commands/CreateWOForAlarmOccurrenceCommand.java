package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class CreateWOForAlarmOccurrenceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (recordId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
			SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
					.module(module)
					.beanClass(AlarmOccurrenceContext.class)
					.select(allFields)
					.fetchLookup((LookupField) fieldMap.get("alarm"))
					.andCondition(CriteriaAPI.getIdCondition(recordId, module));
			AlarmOccurrenceContext alarmOccurrenceContext = builder.fetchFirst();
			if (alarmOccurrenceContext != null) {
				if (alarmOccurrenceContext.getWoId() != -1) {
					throw new IllegalArgumentException("Workorder is already created for the alarm");
				}
				
				BaseAlarmContext baseAlarm = alarmOccurrenceContext.getAlarm();
				context.put(FacilioConstants.ContextNames.ALARM_OCCURRENCE, alarmOccurrenceContext);

				WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
				if (workorder == null) {
					workorder = new WorkOrderContext();
				}
				workorder.setSubject(baseAlarm.getSubject());
				workorder.setDescription(baseAlarm.getDescription());
				workorder.setSourceType(SourceType.ALARM);
				workorder.setResource(baseAlarm.getResource());
				workorder.setScheduledStart(baseAlarm.getLastOccurredTime());
				workorder.setSiteId(baseAlarm.getSiteId());
				
				context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
			}
		}
		return false;
	}

}
