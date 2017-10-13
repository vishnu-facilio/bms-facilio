package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.RequesterContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

public class GetWorkOrderRequestListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		SelectRecordsBuilder<WorkOrderRequestContext> builder = new SelectRecordsBuilder<WorkOrderRequestContext>()
														.connection(conn)
														.table(dataTableName)
														.moduleName(moduleName)
														.beanClass(WorkOrderRequestContext.class)
														.select(fields)
														.orderBy("CREATED_TIME desc")
														.maxLevel(0);

		if(view != null) {
			Criteria criteria = view.getCriteria();
			builder.andCriteria(criteria);
		}
		
		List<Condition> conditionList = (List<Condition>) context.get(FacilioConstants.ContextNames.FILTER_CONDITIONS);
		if(conditionList != null && !conditionList.isEmpty()) {
			for(Condition condition : conditionList) {
				builder.andCondition(condition);
			}
		}
		
		List<WorkOrderRequestContext> workOrderRequests = builder.get();
		loadRequesters(workOrderRequests, conn);
		TicketAPI.loadTicketLookups(workOrderRequests);
		context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST_LIST, workOrderRequests);
		
		return false;
	}
	
	private void loadRequesters(List<WorkOrderRequestContext> workOrderRequests, Connection conn) throws Exception {
		if(workOrderRequests != null && !workOrderRequests.isEmpty()) {
			StringBuilder ids = new StringBuilder();
			boolean isFirst = true;
			for(WorkOrderRequestContext workOrderRequest : workOrderRequests) {
				if(workOrderRequest.getRequester() != null)
				{
					if(isFirst) {
						isFirst = false;
					}
					else {
						ids.append(",");
					}
					ids.append(workOrderRequest.getRequester().getRequesterId());
				}
			}
			
			if(ids.length() > 0)
			{
				Map<Long, RequesterContext> requesters = CommonCommandUtil.getRequesters(ids.toString(), conn);
				for(WorkOrderRequestContext workOrderRequest : workOrderRequests) {
					if(workOrderRequest.getRequester() != null)
					{
						workOrderRequest.setRequester(requesters.get(workOrderRequest.getRequester().getRequesterId()));
					}
				}
			}
		}
	}
}
