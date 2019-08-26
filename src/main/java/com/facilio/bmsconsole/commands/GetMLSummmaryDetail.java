package com.facilio.bmsconsole.commands;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RCAAlarm;
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
		// BaseAlarmContext alarm = NewAlarmAPI.getAlarm(14);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("RcaAlarm");
		SelectRecordsBuilder<RCAAlarm> builder = new SelectRecordsBuilder<RCAAlarm>().module(module)
				.beanClass(RCAAlarm.class).select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId",String.valueOf(14),  NumberOperators.EQUALS));
		List<RCAAlarm> list = builder.get();
		context.put(ContextNames.ML_RCA_ALARMS, list);
		return false;
	}

}
