package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.OperationAlarmHistoricalLogsContext;
import com.facilio.bmsconsole.util.OperationAlarmApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class ExecuteOperationalEventCommand extends FacilioCommand implements PostTransactionCommand {
	private static final Logger LOGGER = LogManager.getLogger(ExecuteOperationalEventCommand.class.getName());
	private OperationAlarmHistoricalLogsContext operationHistoricalLoggerContext = null;
	private Long jobId;
	Boolean isHistorical;
	public boolean executeCommand(Context context) throws Exception {
		isHistorical = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_HISTORICAL, false);
		if (isHistorical) {
			jobId = (Long) context.get(FacilioConstants.ContextNames.HISTORICAL_OPERATIONAL_EVENT_JOB_ID);
			operationHistoricalLoggerContext = OperationAlarmApi.getOperationAlarmHistoricalLoggerById(jobId);
		}
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
    	long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		List<Long> resourceList = (List<Long>) context.getOrDefault(FacilioConstants.ContextNames.RESOURCE_LIST, null);

		OperationAlarmApi.processOutOfCoverage(startTime, endTime, resourceList, context);
	    return false;
	}

	@Override
	public boolean postExecute() throws Exception {
		if (isHistorical) {
			operationHistoricalLoggerContext.setStatus(OperationAlarmHistoricalLogsContext.Status.RESOLVED.getIntVal());
			operationHistoricalLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
			OperationAlarmApi.updateOperationAlarmHistoricalLogger(operationHistoricalLoggerContext);
			if (operationHistoricalLoggerContext.getLogStateAsEnum() == OperationAlarmHistoricalLogsContext.LogState.IS_LAST_JOB) {
				FacilioTimer.scheduleOneTimeJobWithDelay(operationHistoricalLoggerContext.getParentId(), "HistoricalOperationalAlarmProcessingJob", 30, "history");

			}
		}
		return false;
	}

	public void onError() throws Exception {
		if (isHistorical) {
			operationHistoricalLoggerContext.setStatus(OperationAlarmHistoricalLogsContext.Status.FAILED.getIntVal());
			operationHistoricalLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
			OperationAlarmApi.updateOperationAlarmHistoricalLogger(operationHistoricalLoggerContext);
			if (operationHistoricalLoggerContext.getLogStateAsEnum() == OperationAlarmHistoricalLogsContext.LogState.IS_LAST_JOB) {

			}
		}
	}
}