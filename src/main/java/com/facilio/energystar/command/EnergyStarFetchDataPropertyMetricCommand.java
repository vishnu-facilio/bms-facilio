package com.facilio.energystar.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.context.Property_Metrics;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateRange;

public class EnergyStarFetchDataPropertyMetricCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(EnergyStarFetchDataPropertyEnergyCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long propertyId = (Long)context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_ID);
		
		DateRange dateRange = (DateRange)context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		List<Map<String, Object>> props = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarPropertyModule(), FieldFactory.getEnergyStarPropertyFields(), null, CriteriaAPI.getIdCondition(propertyId, ModuleFactory.getEnergyStarPropertyModule()));
		
		if(props != null && !props.isEmpty()) {
			
			EnergyStarPropertyContext property = FieldUtil.getAsBeanFromMap(props.get(0), EnergyStarPropertyContext.class);
			
			property.setDatum("building", SpaceAPI.getBuildingSpace(property.getBuildingId()));
			
			Map<String, Object> values = EnergyStarUtil.fillEnergyStarCardData(property, new ArrayList<>(Property_Metrics.getAllMetrics().values()), dateRange);
			
			property.setDatum("values", values);
			
			context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, property);
			
		}
		
		return false;
		
	}

}
