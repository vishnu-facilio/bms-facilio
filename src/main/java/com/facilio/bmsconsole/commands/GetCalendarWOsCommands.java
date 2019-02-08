package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

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
			woBuilder.andCriteria(view.getCriteria());
		}
		
		boolean isCount = (boolean) context.get(FacilioConstants.ContextNames.COUNT);
		if (isCount) {
			List<FacilioField> fields = new ArrayList<>();
			FacilioField countField = CommonAggregateOperator.COUNT.getSelectField(FieldFactory.getIdField(module));
			countField.setName("count");
			fields.add(countField);
			
			FacilioField createdTimeField = modBean.getField("createdTime", FacilioConstants.ContextNames.WORK_ORDER);
			DateAggregateOperator aggr = (DateAggregateOperator) context.get(FacilioConstants.ContextNames.AGGR_KEY);
			fields.add(aggr.getTimestampField(createdTimeField));
			woBuilder.groupBy(aggr.getSelectField(createdTimeField).getCompleteColumnName())
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
