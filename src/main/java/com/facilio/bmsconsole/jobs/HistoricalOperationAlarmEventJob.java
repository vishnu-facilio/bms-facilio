package com.facilio.bmsconsole.jobs;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.OperationAlarmHistoricalLogsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class HistoricalOperationAlarmEventJob extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(HistoricalOperationAlarmEventJob.class.getName());
    @Override
    public void execute(JobContext jc) throws Exception {
    	try {
				long jobId = jc.getJobId();
				List<Long> resourceIds =new ArrayList<>();
				OperationAlarmHistoricalLogsContext operationAlarmHistoricalLogContext = getOperationAlarmHistoricalLogsContextById(jobId);
				FacilioContext context = new FacilioContext();
				long endTime = operationAlarmHistoricalLogContext.getSplitEndTime();
				long startTime = operationAlarmHistoricalLogContext.getSplitStartTime() ;
				resourceIds.add(operationAlarmHistoricalLogContext.getResourceId());
				context.put(FacilioConstants.ContextNames.START_TIME, startTime);
				context.put(FacilioConstants.ContextNames.IS_HISTORICAL, true);
				context.put(FacilioConstants.ContextNames.END_TIME, endTime);
				context.put(FacilioConstants.ContextNames.RESOURCE_LIST, resourceIds);
				context.put(FacilioConstants.ContextNames.HISTORICAL_OPERATIONAL_EVENT_JOB_ID, jobId);
				FacilioChain chain = TransactionChainFactory.getExecuteOperationAlarm();
				chain.execute(context);

        }
    	catch(Exception operationAlarmJobException) {
    		try {
    			FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
    			LOGGER.error("Error occurred" +operationAlarmJobException.toString());
    			throw operationAlarmJobException;			
    		}
    		catch(Exception transactionException) {
    			LOGGER.error(transactionException.toString());
    		}
    	}
    	
    }
    public static OperationAlarmHistoricalLogsContext getOperationAlarmHistoricalLogsContextById (long loggerId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getOperationAlarmHistoricalLogFields())
				.table(ModuleFactory.getOperationAlarmHistoricalLogsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+loggerId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			OperationAlarmHistoricalLogsContext HistoricalLogsContext = FieldUtil.getAsBeanFromMap(props.get(0), OperationAlarmHistoricalLogsContext.class);
			return HistoricalLogsContext;
		}
		return null;
	}
 
}
