package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowEventContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ResetPMReadingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Boolean reset = (Boolean) context.get(FacilioConstants.ContextNames.PM_RESET_READING);
		if(reset != null && reset) {
			List<PreventiveMaintenance> pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
			if(pms == null) {
				PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
				pms = Collections.singletonList(pm);
			}
			
			if(pms != null && !pms.isEmpty()) {
				for(PreventiveMaintenance pm : pms) {
					if(pm.getReadingRuleId() != -1) {
						long latestValue = getLatestReading(pm.getReadingRuleId());
						if(latestValue != -1) {
							WorkflowAPI.updateLastValueInReadingRule(pm.getReadingRuleId(), latestValue);
						}
					}
				}
			}
		}
		return false;
	}
	
	private long getLatestReading(long readingRuleId) throws Exception {
		ReadingRuleContext rule = (ReadingRuleContext) WorkflowAPI.getWorkflowRule(readingRuleId);
		WorkflowEventContext event = rule.getEvent();
		
		Criteria criteria = rule.getCriteria();
		Condition condition = criteria.getConditions().get(1);
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = bean.getModule(event.getModuleId());
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																			.module(module)
																			.select(bean.getAllFields(module.getName()))
																			.orderBy("TTIME DESC")
																			.limit(1);
		
		List<ReadingContext> readings = selectBuilder.get();
		if(readings != null && readings.isEmpty()) {
			long lastValue = new Double(readings.get(0).getReading(condition.getFieldName()).toString()).longValue();
			return lastValue;
		}
		return -1;
	}
}
