package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetWorkflowRuleResourceLoggersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long parentRuleLoggerId = (long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_PARENT_LOGGER_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleResourceLoggerFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleResourceLoggerFields())
				.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRuleLoggerId"), "" +parentRuleLoggerId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<WorkflowRuleResourceLoggerContext> workflowRuleResourceLoggerContextList = new ArrayList<WorkflowRuleResourceLoggerContext>();	
		List<Long> resourceIds = new ArrayList<Long>();	
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext = FieldUtil.getAsBeanFromMap(prop, WorkflowRuleResourceLoggerContext.class);
				workflowRuleResourceLoggerContextList.add(workflowRuleResourceLoggerContext);
				resourceIds.add(workflowRuleResourceLoggerContext.getResourceId());
			}
			
			List<ResourceContext> resources = ResourceAPI.getResources(resourceIds,true);
			if(resources != null && !resources.isEmpty()) {
				Map<Long, ResourceContext> resourcesMap = new LinkedHashMap<Long, ResourceContext>();
				for(ResourceContext resource:resources) {
					resourcesMap.put(resource.getId(), resource);
				}
				
				for(WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext :workflowRuleResourceLoggerContextList) {
					workflowRuleResourceLoggerContext.setResourceContext(resourcesMap.get(workflowRuleResourceLoggerContext.getResourceId()));
				}
			}
		}
		
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_RESOURCE_LOGGERS, workflowRuleResourceLoggerContextList);
		return false;
	}

}
