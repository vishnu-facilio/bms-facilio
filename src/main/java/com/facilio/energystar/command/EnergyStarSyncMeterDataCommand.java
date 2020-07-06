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

public class EnergyStarSyncMeterDataCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(EnergyStarSyncMeterDataCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<EnergyStarPropertyContext> propertyContexts = (List<EnergyStarPropertyContext>)context.get(EnergyStarUtil.ENERGY_STAR_PROPERTIES_CONTEXT);
		
		if(propertyContexts == null) {
			return false;
		}
		
		HistoricalLoggerContext logger = (HistoricalLoggerContext) context.get(FacilioConstants.ContextNames.HISTORICAL_RULE_LOGGER);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule meterDataModule = modBean.getModule(EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME));
		 
		List<EnergyStarMeterDataContext> meterDatas = new ArrayList<EnergyStarMeterDataContext>();
		
		Long firstDataRecivedDate = Long.MAX_VALUE;
		
		for(EnergyStarPropertyContext propertyContext : propertyContexts) {
			
			for(EnergyStarMeterContext meter : propertyContext.getMeterContexts()) {
				
				deleteMeterData(meter,logger, meterDataModule, fieldMap);
				
				if(meter.getEnergyStarMeterId() != null) {
					List<EnergyStarMeterDataContext> meterData = EnergyStarSDK.fetchMeterConsumptionData(meter.getEnergyStarMeterId());
					
					for(EnergyStarMeterDataContext data : meterData) {
						
						data.setParentId(meter.getId());
						data.setOrgId(AccountUtil.getCurrentOrg().getId());
						data.setModuleId(meterDataModule.getModuleId());
						
						if(data.getTtime() < firstDataRecivedDate) {
							firstDataRecivedDate = data.getTtime();
						}
					}
					
					meterDatas.addAll(meterData);
				}
			}
		}
		
		if(!meterDatas.isEmpty()) {
			FacilioChain addCurrentOccupancy = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			
			FacilioContext newContext = addCurrentOccupancy.getContext();
			newContext.put(FacilioConstants.ContextNames.MODULE_NAME, EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME);
			newContext.put(FacilioConstants.ContextNames.READINGS, meterDatas);
			newContext.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.INTEGRATION);
			newContext.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
			addCurrentOccupancy.execute();
		}
		if(firstDataRecivedDate == Long.MAX_VALUE) {
			firstDataRecivedDate = null;
		}
		context.put(EnergyStarUtil.FIRST_DATA_RECIEVIED_TIME, firstDataRecivedDate);
		
		return false;
	}

	private void deleteMeterData(EnergyStarMeterContext meter,HistoricalLoggerContext logger,FacilioModule meterDataModule,Map<String, FacilioField> fieldMap) throws Exception {
		
		Criteria deleteCriteria = new Criteria();
		deleteCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), meter.getId()+"", NumberOperators.EQUALS));
		deleteCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("fromDate"), logger.getStartTime()+","+logger.getEndTime(), DateOperators.BETWEEN));
		
		EnergyStarUtil.deleteEnergyStarRelated(meterDataModule, deleteCriteria);
	}

}
