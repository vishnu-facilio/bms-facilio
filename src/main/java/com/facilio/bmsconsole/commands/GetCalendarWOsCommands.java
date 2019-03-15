package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.view.ViewFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.CommonAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetCalendarWOsCommands implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		FacilioField dateField = (FacilioField) context.get(FacilioConstants.ContextNames.DATE_FIELD);
		SelectRecordsBuilder<WorkOrderContext> woBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																.module(module)
																.andCondition(CriteriaAPI.getCondition(dateField, String.valueOf(startTime+","+endTime), DateOperators.BETWEEN))
																;
																
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if (view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			if (AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_SCHEDULED_WO) && AccountUtil.getCurrentOrg().getOrgId() == 75) { //Temp hack
				Map<String, Condition> conditionMap = view.getCriteria().getConditions();
				Set<Map.Entry<String, Condition>> conditions = conditionMap.entrySet();
				Criteria scheduledWoCriteria = new Criteria();
				Criteria statusCriteria = null;
				for (Map.Entry<String, Condition> conditionEntry: conditions) {
					Condition condition = conditionEntry.getValue();
					if (condition.getColumnName().equals("Tickets.STATUS_ID")) {
						statusCriteria = new Criteria();
						statusCriteria.addAndCondition(condition);
						statusCriteria.addOrCondition(ViewFactory.getPreOpenStatusCondition());
					} else {
						scheduledWoCriteria.addAndCondition(condition);
					}
				}
				woBuilder.andCriteria(scheduledWoCriteria);
				if (statusCriteria != null) {
					woBuilder.andCriteria(statusCriteria);
				}
			} else {
				woBuilder.andCriteria(view.getCriteria());
			}
		}
		
		boolean isCount = (boolean) context.get(FacilioConstants.ContextNames.COUNT);
		if (isCount) {
			List<FacilioField> fields = new ArrayList<>();
			FacilioField countField = CommonAggregateOperator.COUNT.getSelectField(FieldFactory.getIdField(module));
			countField.setName("count");
			fields.add(countField);
			
			DateAggregateOperator aggr = (DateAggregateOperator) context.get(FacilioConstants.ContextNames.AGGR_KEY);
			fields.add(aggr.getTimestampField(dateField));
			woBuilder.groupBy(aggr.getSelectField(dateField).getCompleteColumnName())
						.select(fields);
			
			context.put(FacilioConstants.ContextNames.WORK_ORDER_COUNT, woBuilder.getAsProps());
		}
		else {
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			woBuilder.select(fields)
					.beanClass(WorkOrderContext.class);
			context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, woBuilder.get());
		}
		
		
		return false;
	}

}
