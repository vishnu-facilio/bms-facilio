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
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class EnergyStarFetchDataJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(AssetActionJob.class.getName());
	@Override
	public void execute(JobContext jc) {
		try {
			
			List<Map<String, Object>> propertyProps = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarPropertyModule(), FieldFactory.getEnergyStarPropertyFields(),null,CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getEnergyStarPropertyModule()));
			List<EnergyStarPropertyContext> properties = FieldUtil.getAsBeanListFromMapList(propertyProps, EnergyStarPropertyContext.class);
			
			long fetchTime = DateTimeUtil.addMonths(DateTimeUtil.getMonthStartTime(), -1);
			
			for(EnergyStarPropertyContext property :properties) { 
				
				FacilioChain chain = TransactionChainFactory.getEnergyStarFetchDataChain();
				
				FacilioContext context = chain.getContext();
				
				context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, property);
				
				context.put(EnergyStarUtil.ENERGY_STAR_FETCH_TIME_LIST, Collections.singletonList(fetchTime));
				
				chain.execute();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			LOGGER.error("Energy Star fetch data Failed", e);
			CommonCommandUtil.emailException(AssetActionJob.class.getName(), "Energy Star fetch data Failed -- "+AccountUtil.getCurrentOrg().getId(), e);
		}
	}

}
