package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.IndoorFloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class DeleteIndoorFloorPlanObjectsCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		IndoorFloorPlanContext floorPlan = (IndoorFloorPlanContext) context.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);

		Map<String, Object> props = FieldUtil.getAsProperties(floorPlan);

				GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
						.table(ModuleFactory.getIndoorFloorPlanObjectModule().getTableName())
						.andCustomWhere("FLOORPLAN_ID = ?", floorPlan.getId());
				deleteBuilder.delete();
			
		return false;
	}
}