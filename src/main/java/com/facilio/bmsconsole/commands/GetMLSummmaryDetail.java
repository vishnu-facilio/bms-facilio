package com.facilio.bmsconsole.commands;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MLAlarmOccurenceContext;
import com.facilio.bmsconsole.context.RCAAlarm;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;


public class GetMLSummmaryDetail extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long mlAnomalyId = (long) context.get(ContextNames.ALARM_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ANOMALY_ALARM_OCCURRENCE);
		SelectRecordsBuilder<MLAlarmOccurenceContext> builder = new SelectRecordsBuilder<MLAlarmOccurenceContext>().module(module)
				.beanClass(MLAlarmOccurenceContext.class).select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getCondition("PARENTID", "parentID",String.valueOf(mlAnomalyId),  NumberOperators.EQUALS));
		List<MLAlarmOccurenceContext> list = builder.get();
		context.put(ContextNames.ML_RCA_ALARMS, list);
		return false;
	}

}
