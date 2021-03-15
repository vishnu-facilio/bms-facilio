package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.IndoorFloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddIndoorFloorPlanCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		IndoorFloorPlanContext floorPlan = (IndoorFloorPlanContext) context.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getIndoorFloorPlanModule().getTableName())
				.fields(FieldFactory.getIndoorFloorPlanFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(floorPlan);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		floorPlan.setId((Long) props.get("id"));
		context.put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN,floorPlan);
		return false;
	}

}
