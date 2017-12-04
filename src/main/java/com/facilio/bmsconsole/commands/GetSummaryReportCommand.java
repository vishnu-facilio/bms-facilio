package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetSummaryReportCommand implements Command {

	@Override
	public boolean execute(Context ctxt) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext context = (FacilioReportContext) ctxt;
		String moduleName = context.getModuleName();
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		Condition orgCondition = (Condition) context.get(FacilioConstants.Reports.ORG_CONDITION);
		String customWhere = (String) context.get(FacilioConstants.Reports.CUSTOM_WHERE_CONDITION);
		JSONArray reportJoins = context.getJoins();
		String groupBy = context.getGroupBy();
		int limit = context.getLimit();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.connection(conn)
				.select(fields)
				.table(dataTableName)
				.andCondition(orgCondition);
		
		if(customWhere !=null && !customWhere.isEmpty()) {
			builder.andCustomWhere(customWhere);
		}
		
		if(reportJoins!=null && !reportJoins.isEmpty()) {
			for(int i=0;i<reportJoins.size();i++) {
				JSONObject thisJoin = (JSONObject) reportJoins.get(i);
				String joinTable = (String) thisJoin.get(FacilioConstants.Reports.JOIN_TABLE);
				String joinOn = (String) thisJoin.get(FacilioConstants.Reports.JOIN_ON);
				String joinType = (String) thisJoin.get(FacilioConstants.Reports.JOIN_TYPE);
				switch(joinType) {
				case FacilioConstants.Reports.INNER_JOIN:
					builder.innerJoin(joinTable)
					.on(joinOn);
					break;
				case FacilioConstants.Reports.LEFT_JOIN:
					builder.leftJoin(joinTable)
					.on(joinOn);
					break;
				case FacilioConstants.Reports.FULL_JOIN:
					builder.fullJoin(joinTable)
					.on(joinOn);
					break;
				case FacilioConstants.Reports.RIGHT_JOIN:
					builder.rightJoin(joinTable)
					.on(joinOn);
					break;
				}
			}
		}
		
		if(groupBy!=null && !groupBy.isEmpty()) {
			builder.groupBy(groupBy);
		}
		
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
		
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		
		if(limit>0) {
			builder.limit(limit);
		}

		List<Map<String, Object>> rs = builder.get();
		
		context.put(FacilioConstants.Reports.RESULT_SET, rs);
		
		return false;
	}
	
}
