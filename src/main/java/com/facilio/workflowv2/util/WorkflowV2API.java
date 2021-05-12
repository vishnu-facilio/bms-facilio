package com.facilio.workflowv2.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.command.SchedulerAPI;
import com.facilio.workflows.context.ScheduledWorkflowContext;

public class WorkflowV2API {
	
	public static ScheduledWorkflowContext getScheduledWorkflowContext(long scheduledWorkflowId,Boolean isActive) throws Exception {
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(FieldFactory.getScheduledWorkflowFields())
				.table(ModuleFactory.getScheduledWorkflowModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowId, ModuleFactory.getScheduledWorkflowModule()));
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getScheduledWorkflowFields());
		if(isActive != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("isActive"), isActive.toString(), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = select.get();
		
		ScheduledWorkflowContext scheduledWorkflowContext = null;
		if(props != null && !props.isEmpty()) {
			scheduledWorkflowContext = FieldUtil.getAsBeanFromMap(props.get(0), ScheduledWorkflowContext.class);
			
			SchedulerAPI.getSchedulerActions(Collections.singletonList(scheduledWorkflowContext));
		}
		return scheduledWorkflowContext;
	}

}
