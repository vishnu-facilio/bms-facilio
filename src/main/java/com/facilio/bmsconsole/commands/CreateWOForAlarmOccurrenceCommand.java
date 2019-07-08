package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
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
					.select(allFields)
					.fetchLookup((LookupField) fieldMap.get("alarm"))
					.andCondition(CriteriaAPI.getIdCondition(recordId, module));
			AlarmOccurrenceContext alarmOccurrenceContext = builder.fetchFirst();
			if (alarmOccurrenceContext != null) {
				WorkOrderContext workorder = new WorkOrderContext();
				workorder.setSubject(alarmOccurrenceContext.getAlarm().getSubject());
			}
		}
		return false;
	}

}
