package com.facilio.energystar.command;

import java.util.Collections;
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
import com.facilio.energystar.context.EnergyStarPropertyUseContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class EnergyStarFetchDataPropertyEnergyCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(EnergyStarFetchDataPropertyEnergyCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long propertyId = (Long)context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule meterData = modBean.getModule(EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME);
		
		List<FacilioField> fields = modBean.getAllFields(EnergyStarUtil.ENERGY_STAR_METER_DATA_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		List<Map<String, Object>> props = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarPropertyModule(), FieldFactory.getEnergyStarPropertyFields(), null, CriteriaAPI.getIdCondition(propertyId, ModuleFactory.getEnergyStarPropertyModule()));
		
		if(props != null && !props.isEmpty()) {
			
			EnergyStarPropertyContext property = FieldUtil.getAsBeanFromMap(props.get(0), EnergyStarPropertyContext.class);
			
			props = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarMeterModule(), FieldFactory.getEnergyStarMeterFields(), null, CriteriaAPI.getCondition("PROPERTY_ID", "propertyId", property.getId()+"", NumberOperators.EQUALS));
			
			property.setDatum("building", SpaceAPI.getBuildingSpace(property.getBuildingId()));
			
			
			fillPropertyUse(property);
			
			List<EnergyStarMeterContext> meters = FieldUtil.getAsBeanListFromMapList(props, EnergyStarMeterContext.class);
			
			for(EnergyStarMeterContext meter :meters) {
				meter.setMeterContext(AssetsAPI.getAssetInfo(meter.getMeterId()));
				
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
						.select(fields)
						.table(meterData.getTableName())
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), meter.getId()+"",NumberOperators.EQUALS))
						.orderBy("TTIME Desc")
						.limit(1);
						;
				List<Map<String, Object>> lastProps = selectBuilder.get();
				
				if(lastProps != null && !lastProps.isEmpty()) {
					meter.setDatum("moseRecentBillData", lastProps.get(0).get("ttime"));
				}
			}
			
			property.setMeterContexts(meters);
			
			context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, property);
			
		}
		
		return false;
		
	}

	private void fillPropertyUse(EnergyStarPropertyContext property) throws Exception {
		
		List<Map<String, Object>> props = EnergyStarUtil.fetchEnergyStarRelated(ModuleFactory.getEnergyStarPropertyUseModule(), FieldFactory.getEnergyStarPropertyUseFields(), null, CriteriaAPI.getCondition("PROPERTY_ID", "propertyId", property.getId()+"", NumberOperators.EQUALS));
	
		if(props != null && !props.isEmpty()) {
			List<EnergyStarPropertyUseContext> propertyUse = FieldUtil.getAsBeanListFromMapList(props, EnergyStarPropertyUseContext.class);
			
			property.setPropertyUseContexts(propertyUse);
		}
	}

}
