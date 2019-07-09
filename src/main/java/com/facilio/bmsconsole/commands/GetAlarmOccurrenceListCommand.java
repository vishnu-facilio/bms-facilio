package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetAlarmOccurrenceListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (recordId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			List<FacilioField> allFields = modBean.getAllFields(module.getName());
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
			SelectRecordsBuilder<AlarmOccurrenceContext> builder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
					.module(module)
					.beanClass(AlarmOccurrenceContext.class)
					.select(allFields)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarm"), String.valueOf(recordId), NumberOperators.EQUALS));
			List<AlarmOccurrenceContext> list = builder.get();
			
			context.put(FacilioConstants.ContextNames.RECORD_LIST, list);
		}
		return false;
	}

}
