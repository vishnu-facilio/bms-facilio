package com.facilio.energystar.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.context.Property_Metrics;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class EnergyStarFetchDataMainSummaryCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(EnergyStarFetchDataMainSummaryCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		String fieldName = (String) context.get(FacilioConstants.ContextNames.MODULE_FIELD_NAME);
		
		Property_Metrics propertMetric = null;
		if(fieldName != null) {
			propertMetric = Property_Metrics.getNameMap().get(fieldName);
		}
		
		List<Map<String, Object>> props = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarPropertyModule(), FieldFactory.getEnergyStarPropertyFields(), null, CriteriaAPI.getCondition("BUILDING_ID", "buildingId", null, CommonOperators.IS_NOT_EMPTY));
		
		List<EnergyStarPropertyContext> properties = FieldUtil.getAsBeanListFromMapList(props, EnergyStarPropertyContext.class);
		
		double max = 0;
		for(EnergyStarPropertyContext prop :properties) {
			
			prop.setDatum("building", SpaceAPI.getBuildingSpace(prop.getBuildingId()));
			
			if(propertMetric != null) {
				Map<String, Object> values = EnergyStarUtil.fillEnergyStarCardData(prop, Collections.singletonList(propertMetric), null);
				
				if(propertMetric != Property_Metrics.SCORE) {
					Map<String, Object> dataValue = (Map<String, Object>) values.get(propertMetric.getName());
					
					double val = Double.parseDouble(dataValue.get("max").toString());
					
					if(val > max) {
						max = val;
					}
				}
				
				prop.setDatum("values", values);
			}
		}
		
		if(propertMetric != null) {
			
			if(propertMetric != Property_Metrics.SCORE) {
				double maxValue = max + (max * 20 /100);
				
				for(EnergyStarPropertyContext prop :properties) {
					
					Map<String, Object> values = (Map<String, Object>) prop.getDatum("values");
					
					Map<String, Object> dataValue = (Map<String, Object>)values.get(propertMetric.getName());
					
					dataValue.put("maxValue", maxValue);
				}
			}
		}
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTIES_CONTEXT, properties);
		
		return false;
	}

	private double getMaxValue(Map<String, Object> values,double max) {
		for(String key : values.keySet()) {
			try {
				double val = Double.parseDouble(values.get(key).toString());
				if(val > max) {
					max = val;
				}
			}
			catch(Exception e) {
				
			}
		}
		return max;
	}

}