package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ScheduledRuleJobsMetaContext;
import com.facilio.bmsconsole.util.ScheduledRuleJobsMetaUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateScheduleRuleJobMetaCommand extends FacilioCommand implements PostTransactionCommand{

	private static final Logger LOGGER = LogManager.getLogger(UpdateScheduleRuleJobMetaCommand.class.getName());

	ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext = new ScheduledRuleJobsMetaContext();
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		scheduledRuleJobsMetaContext = (ScheduledRuleJobsMetaContext) context.get(FacilioConstants.ContextNames.SCHEDULE_RULE_META);	
		return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		
		if(scheduledRuleJobsMetaContext != null) {
			scheduledRuleJobsMetaContext.setIsActive(false);
			ScheduledRuleJobsMetaUtil.updateScheduledRuleJobsMeta(scheduledRuleJobsMetaContext);	
		}
		
		return false;
	}
	
}
