package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetRequesterWorkorderCountCommand extends FacilioCommand {

	FacilioModule module;
	FacilioField idField;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		module = modBean.getModule(ContextNames.WORK_ORDER);
		idField = FieldFactory.getIdField(module);
		
		long allCount = getCount(null);
		
		Condition open = ViewFactory.getOpenStatusCondition();
		long openCount = getCount(open);
		
		Criteria closed = ViewFactory.getClosedTicketsCriteria();
		long closedCount = getCount(null, closed);
		
		context.put("all", allCount);
		context.put("open", openCount);
		context.put("closed", closedCount);
		
		return false;
	}
	
	private long getCount(Condition condition, Criteria...criteria) throws Exception {
		
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
															.module(module)
															.beanClass(WorkOrderContext.class)
															.aggregate(CommonAggregateOperator.COUNT, idField)
															;
		
		if (condition != null) {
			builder.andCondition(condition);
		}
		if (criteria.length > 0) {
			builder.andCriteria(criteria[0]);
		}
		
		return builder.fetchFirst().getId();
	}

}