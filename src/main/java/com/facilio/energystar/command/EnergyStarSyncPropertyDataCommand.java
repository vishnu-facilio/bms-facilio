package com.facilio.energystar.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class EnergyStarSyncPropertyDataCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(EnergyStarSyncMeterDataCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		List<EnergyStarPropertyContext> propertyContexts = (List<EnergyStarPropertyContext>)context.get(EnergyStarUtil.ENERGY_STAR_PROPERTIES_CONTEXT);
		
		HistoricalLoggerContext logger = (HistoricalLoggerContext) context.get(FacilioConstants.ContextNames.HISTORICAL_RULE_LOGGER);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule dataModule = modBean.getModule(EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_MODULE_NAME));
		
		Long firstDataRecivedDate = (Long)context.get(EnergyStarUtil.FIRST_DATA_RECIEVIED_TIME);
		
		if(firstDataRecivedDate != null) {
			for(EnergyStarPropertyContext propertyContext : propertyContexts) {
				
				deletePropertyData(propertyContext,logger, dataModule, fieldMap);
				
				FacilioChain chain = TransactionChainFactory.getEnergyStarFetchDataChain();
				
				FacilioContext context1 = chain.getContext();
				
				context1.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, propertyContext);
				
				context1.put(FacilioConstants.ContextNames.START_TIME,firstDataRecivedDate);
				context1.put(FacilioConstants.ContextNames.END_TIME,logger.getEndTime());
				
				chain.execute();
			}
		}
		 
		return false;
	}
	
	private void deletePropertyData(EnergyStarPropertyContext property,HistoricalLoggerContext logger,FacilioModule meterDataModule,Map<String, FacilioField> fieldMap) throws Exception {
		
		Criteria deleteCriteria = new Criteria();
		deleteCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), property.getId()+"", NumberOperators.EQUALS));
		deleteCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), logger.getStartTime()+","+logger.getEndTime(), DateOperators.BETWEEN));
		
		EnergyStarUtil.deleteEnergyStarRelated(meterDataModule, deleteCriteria);
	}
}
