package com.facilio.bmsconsole.jobs;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class EnergyStarPushData extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(EnergyStarPushData.class.getName());
	@Override
	public void execute(JobContext jc) {
		try {
			
			List<Map<String, Object>> meterProps = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarMeterModule(), FieldFactory.getEnergyStarMeterFields(),null,CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getEnergyStarMeterModule()));
			List<EnergyStarMeterContext> meters = FieldUtil.getAsBeanListFromMapList(meterProps, EnergyStarMeterContext.class);
			
			for(EnergyStarMeterContext meter :meters) {
				
				if(meter.getMeterId() > 0) {
					
					FacilioChain chain = TransactionChainFactory.getESPushMeterDataChain();
					
					FacilioContext context = chain.getContext();
					
					context.put(EnergyStarUtil.ENERGY_STAR_METER_CONTEXT, meter);
					
					List<DateRange> intervals = Collections.singletonList(new DateRange(DateTimeUtil.getMonthStartTime(-1), DateTimeUtil.getMonthEndTimeOf(DateTimeUtil.getMonthStartTime(-1))));
					
					context.put(FacilioConstants.ContextNames.INTERVAL, intervals);
					
					chain.execute();
				}
			}
			
			
		}
		catch(Exception e) {
			LOGGER.error("Energy Star Push data Failed", e);
			CommonCommandUtil.emailException(AssetActionJob.class.getName(), "Energy Star Push data Failed -- "+AccountUtil.getCurrentOrg().getId(), e);
		}
	}

}
