package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.IndoorFloorPlanContext;
import com.facilio.bmsconsole.context.IndoorFloorPlanObjectContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;



public class AddIndoorFloorPlanObjectsCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		IndoorFloorPlanContext floorPlan = (IndoorFloorPlanContext) context.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);
		
        if (floorPlan.getFloorPlanObjects() != null) {
		long orgId = AccountUtil.getCurrentOrg().getId();
        List<Map<String, Object>> floorplanObjectProps = new ArrayList<>();
		for(IndoorFloorPlanObjectContext floorPlanObject :  floorPlan.getFloorPlanObjects()) {
            long floorPlanId = floorPlan.getId();
			floorPlanObject.setFloorplanId(floorPlanId);
			floorplanObjectProps.add(FieldUtil.getAsProperties(floorPlanObject));
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getIndoorFloorPlanObjectModule().getTableName())
					.fields(FieldFactory.getIndoorFloorPlanObjectFields())
					.addRecords(floorplanObjectProps);
            		insertBuilder.save();

        }
		return false;
	}

}
