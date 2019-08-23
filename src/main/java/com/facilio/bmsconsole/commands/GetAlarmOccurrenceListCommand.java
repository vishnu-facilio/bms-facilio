package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.util.NewAlarmAPI;
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

public class GetAlarmOccurrenceListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Long> recordId = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

		List<AlarmOccurrenceContext> alarmOccurrences = NewAlarmAPI.getAlarmOccurrences(recordId);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, alarmOccurrences);
		return false;
	}

}
