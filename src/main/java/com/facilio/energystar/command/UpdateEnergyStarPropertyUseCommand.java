package com.facilio.energystar.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.context.EnergyStarPropertyUseContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateEnergyStarPropertyUseCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarPropertyContext propContext = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		
		if(propContext.getPropertyUseContexts() != null) {
			
			//delete and add use here. update use id in prop
			
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getEnergyStarPropertyUseFields());
			
			Criteria criteria = new Criteria();
			
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("propertyId"), propContext.getId()+"", NumberOperators.EQUALS));
			
			EnergyStarUtil.deleteEnergyStarRelated(ModuleFactory.getEnergyStarPropertyUseModule(), criteria);
			
			for(EnergyStarPropertyUseContext propertyUse : propContext.getPropertyUseContexts()) {
				propertyUse.setId(-1);
				propertyUse.setPropertyId(propContext.getId());
				EnergyStarUtil.addEnergyStarPropertyUse(propertyUse);
			}
		}
		
		return false;
	}

}
