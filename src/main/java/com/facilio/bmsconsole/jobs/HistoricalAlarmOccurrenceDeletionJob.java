package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.HistoricalRunForReadingRuleCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLogsContext;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLoggerUtil;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLogsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleLoggerAPI;
import com.facilio.bmsconsole.util.WorkflowRuleResourceLoggerAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class HistoricalAlarmOccurrenceDeletionJob extends FacilioJob {
	
	private static final Logger LOGGER = LogManager.getLogger(HistoricalAlarmOccurrenceDeletionJob.class.getName());
	
	long jobId = -1;
	public void execute(JobContext jc) throws Exception {
		jobId = jc.getJobId();
		FacilioChain chain = TransactionChainFactory.getExecuteHistoricalAlarmOccurrenceDeletion();
		chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_ALARM_OCCURRENCE_DELETION_JOB_ID, jc.getJobId());
		chain.getContext().put(FacilioConstants.ContextNames.HISTORICAL_ALARM_OCCURRENCE_DELETION_JOB_RETRY_COUNT, jc.getJobExecutionCount());
		chain.execute();	
	}

	@Override
	public void handleTimeOut() {
		// TODO Auto-generated method stub
		LOGGER.info("Time out called during HistoricalRuleAlarmOccurrenceDeletion JobId --"+jobId);
	 	super.handleTimeOut();
	}		
}


