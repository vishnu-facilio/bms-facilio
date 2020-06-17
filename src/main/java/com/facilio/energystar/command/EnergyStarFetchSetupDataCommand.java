package com.facilio.energystar.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class EnergyStarFetchSetupDataCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(EnergyStarFetchSetupDataCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Map<String, Object>> props = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarPropertyModule(), FieldFactory.getEnergyStarPropertyFields(), null, null);
		
		List<EnergyStarPropertyContext> propertyContexts = new ArrayList<EnergyStarPropertyContext>();
		
		boolean isConfigured = true;
		
		if(props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop : props) {
				
				EnergyStarPropertyContext property = FieldUtil.getAsBeanFromMap(prop, EnergyStarPropertyContext.class);
				
				List<Map<String, Object>> meterProps = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarMeterModule(), FieldFactory.getEnergyStarMeterFields(), null, CriteriaAPI.getCondition("PROPERTY_ID", "propertyId", property.getId()+"", NumberOperators.EQUALS));
				
				if(property.getBuildingId() > 0) {
					property.setDatum("building", SpaceAPI.getBuildingSpace(property.getBuildingId()));
				}
				else {
					isConfigured = false;
				}
				
				List<EnergyStarMeterContext> meters = FieldUtil.getAsBeanListFromMapList(meterProps, EnergyStarMeterContext.class);
				
				for(EnergyStarMeterContext meter :meters) {
					if(meter.getMeterId() > 0) {
						meter.setMeterContext(AssetsAPI.getAssetInfo(meter.getMeterId()));
					}
					else {
						isConfigured = false;
					}
				}
				
				property.setMeterContexts(meters);
				
				property.setConfigured(isConfigured);
				
				propertyContexts.add(property);
				
			}
			
			context.put(EnergyStarUtil.ENERGY_STAR_PROPERTIES_CONTEXT, propertyContexts);
			
		}
		
		return false;
		
	}

}