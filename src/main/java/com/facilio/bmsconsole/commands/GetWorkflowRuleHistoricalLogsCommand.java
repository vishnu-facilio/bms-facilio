package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLogsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetWorkflowRuleHistoricalLogsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long parentRuleResourceId = (long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_RESOURCE_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleHistoricalLogsFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLogsFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLogsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRuleResourceId"), "" +parentRuleResourceId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<WorkflowRuleHistoricalLogsContext> workflowRuleHistoricalLogsContextList = new ArrayList<WorkflowRuleHistoricalLogsContext>();
		
		if (props != null && !props.isEmpty()) 
		{		
			workflowRuleHistoricalLogsContextList  = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleHistoricalLogsContext.class);		
		}	
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_HISTORICAL_LOGS, workflowRuleHistoricalLogsContextList);
		
		return false;
	}

}
