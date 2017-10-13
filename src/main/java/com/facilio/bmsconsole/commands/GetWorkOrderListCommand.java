package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

public class GetWorkOrderListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
														.connection(conn)
														.table(dataTableName)
														.moduleName(moduleName)
														.beanClass(WorkOrderContext.class)
														.select(fields)
														.orderBy("CREATED_TIME desc")
														.maxLevel(0);

//		if(view != null) {
//			Criteria criteria = view.getCriteria();
//			selectBuilder.andCriteria(criteria);
//		}
		
		List<Condition> conditionList = (List<Condition>) context.get(FacilioConstants.ContextNames.FILTER_CONDITIONS);
		if(conditionList != null && !conditionList.isEmpty()) {
			for(Condition condition : conditionList) {
				selectBuilder.andCondition(condition);
			}
		}
		
		List<WorkOrderContext> workOrders = selectBuilder.get();
		context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		
		return false;
	}
	
}
