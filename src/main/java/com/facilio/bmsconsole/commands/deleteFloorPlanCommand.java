package com.facilio.bmsconsole.commands;


import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;

public class deleteFloorPlanCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		FloorPlanContext floorPlan = (FloorPlanContext) context.get(FacilioConstants.ContextNames.FLOOR_PLAN);
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
		         .table(ModuleFactory.getFloorPlanModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("Floor_Plan.ID", "id", String.valueOf(floorPlan.getFloorPlanId()), NumberOperators.EQUALS));
		int deletedRows = builder.delete();
		
				if (deletedRows == 1) {
					return true;
				}
				return false;
	}
}
