package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.IndoorFloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class updateIndoorFloorPlanCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		IndoorFloorPlanContext floorPlan = (IndoorFloorPlanContext) context.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);

		Map<String, Object> props = FieldUtil.getAsProperties(floorPlan);

				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                 .table(ModuleFactory.getIndoorFloorPlanModule().getTableName())
 				.fields(FieldFactory.getIndoorFloorPlanFields())
  				.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(floorPlan.getId()), NumberOperators.EQUALS));
				updateBuilder.update(props);
			
		return false;
	}
}