package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class AddEnergyStarSyncJobCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		if(dateRange == null) {
			dateRange = new DateRange();
			
			dateRange.setEndTime(DateTimeUtil.getMonthStartTime());
			dateRange.setStartTime(DateTimeUtil.addYears(DateTimeUtil.getMonthStartTime(), -10));
		}
		
		HistoricalLoggerContext syncLogger = new HistoricalLoggerContext();
		
		syncLogger.setOrgId(AccountUtil.getCurrentOrg().getId());
		syncLogger.setStartTime(dateRange.getStartTime());
		syncLogger.setEndTime(dateRange.getEndTime());
		syncLogger.setType(HistoricalLoggerContext.Type.ENERGY_STAR_SYNC.getIntVal());
		syncLogger.setStatus(HistoricalLoggerContext.Status.IN_PROGRESS.getIntVal());
		
		HistoricalLoggerUtil.addHistoricalLogger(syncLogger);
		
		BmsJobUtil.scheduleOneTimeJobWithProps(syncLogger.getId(), "EnergyStarSyncData", 5, "history", null);
		return false;
	}

}
