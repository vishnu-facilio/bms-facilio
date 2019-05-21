package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GetSummaryReportCommand implements Command {

	@Override
	public boolean execute(Context ctxt) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext context = (FacilioReportContext) ctxt;
		String moduleName = context.getModuleName();
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		Condition orgCondition = (Condition) context.get(FacilioConstants.Reports.ORG_CONDITION);
		String customWhere = (String) context.get(FacilioConstants.Reports.CUSTOM_WHERE_CONDITION);
		JSONArray reportJoins = context.getJoins();
		String groupBy = context.getGroupBy();
		int limit = context.getLimit();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(dataTableName)
				.andCondition(orgCondition);
		
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
		if (scopeCriteria != null) {
			builder.andCriteria(scopeCriteria);
		}
		
		Criteria permissionCriteria = AccountUtil.getCurrentUser().getRole().permissionCriteria(moduleName,"read");
		if (permissionCriteria != null) {
			builder.andCriteria(permissionCriteria);
		}
		
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
		
		Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		if (criteria != null) {
			builder.andCriteria(criteria);
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
