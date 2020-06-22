package com.facilio.energystar.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarCustomerContext.Sync_Status;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

public class AddEnergyStarSyncJobCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarCustomerContext customer = (EnergyStarCustomerContext)context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		if(customer.getSyncStatus() == Sync_Status.SYNC_IN_PROGRESS.getIntVal()) {
			throw new Exception("Sync In progress");
		}
		else {
			customer.setSyncStatus(Sync_Status.SYNC_IN_PROGRESS.getIntVal());
			customer.setLastSyncedTime(DateTimeUtil.getCurrenTime());
		}
		
		Criteria UpdateCriteria = new Criteria();
		
		UpdateCriteria.addAndCondition(CriteriaAPI.getIdCondition(customer.getId(), ModuleFactory.getEnergyStarCustomerModule()));
		
		EnergyStarUtil.updateEnergyStarRelModule(ModuleFactory.getEnergyStarCustomerModule(), FieldFactory.getEnergyStarCustomerFields(), customer,UpdateCriteria);
		
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
