package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetCalendarWOsCommands extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GetCalendarWOsCommands.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		FacilioField dateField = (FacilioField) context.get(FacilioConstants.ContextNames.DATE_FIELD);
		SelectRecordsBuilder<WorkOrderContext> woBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																.module(module)
																.andCondition(CriteriaAPI.getCondition(dateField, String.valueOf(startTime+","+endTime), DateOperators.BETWEEN))
																.skipModuleCriteria();
																;
																
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if (view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			/* if (AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_SCHEDULED_WO)) { //Temp hack
				Map<String, Condition> conditionMap = view.getCriteria().getConditions();
				Set<Map.Entry<String, Condition>> conditions = conditionMap.entrySet();
				for (Map.Entry<String, Condition> conditionEntry: conditions) {
					Condition condition = conditionEntry.getValue();
					if (condition.getColumnName().equals("Tickets.STATUS_ID")) {
						Criteria statusCriteria = new Criteria();
						statusCriteria.addAndCondition(condition);
						statusCriteria.addOrCondition(ViewFactory.getPreOpenStatusCondition());
						woBuilder.andCriteria(statusCriteria);
					} else {
						woBuilder.andCondition(condition);
					}
				}
			} else { */
				woBuilder.andCriteria(view.getCriteria());
			//}
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

		LOGGER.log(Level.SEVERE, "gen sql : " + woBuilder.toString());
		
		return false;
	}

}
