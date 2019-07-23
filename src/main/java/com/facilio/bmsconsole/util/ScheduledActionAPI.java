package com.facilio.bmsconsole.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.job.JobStore;

public class ScheduledActionAPI {
	
	public static void executeScheduledAction(long id, String... toAddr) throws Exception {
		FacilioModule module = ModuleFactory.getScheduledActionModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(FieldFactory.getScheduledActionFields())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		List<Map<String, Object>> props = builder.get();
		if (props != null && !props.isEmpty()) {
			ScheduledActionContext scheduledAction = FieldUtil.getAsBeanFromMap(props.get(0), ScheduledActionContext.class);
			ActionContext action = ActionAPI.getAction(scheduledAction.getActionId());
			if (toAddr != null && toAddr.length > 0 && action.getTemplate() instanceof EMailTemplate) {
				((EMailTemplate)action.getTemplate()).setTo(toAddr[0]);
			}
			Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
			action.executeAction(placeHolders, null, null, null);
		}
	}
	
	public static ScheduledActionContext getScheduledAction (long id) throws Exception {
		FacilioModule module = ModuleFactory.getScheduledActionModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(FieldFactory.getScheduledActionFields())
													.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		List<Map<String, Object>> props = builder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			ScheduledActionContext scheduledAction = FieldUtil.getAsBeanFromMap(props.get(0), ScheduledActionContext.class);
			scheduledAction.setAction(ActionAPI.getAction(scheduledAction.getActionId()));
			return scheduledAction;
		}
		return null;
	}
	
	public static void deleteScheduledAction (long id, String jobName) throws Exception {
		ScheduledActionContext schAction = getScheduledAction(id);
		FacilioModule module = ModuleFactory.getScheduledActionModule();
		JobStore.deleteJob(id,jobName);
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getIdCondition(id, module));
		builder.delete();
		ActionAPI.deleteActions(Collections.singletonList(schAction.getActionId()));
	}
}
