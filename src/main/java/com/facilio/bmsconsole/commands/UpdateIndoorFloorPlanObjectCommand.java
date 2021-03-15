package com.facilio.bmsconsole.commands;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.IndoorFloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateIndoorFloorPlanObjectCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
        List<IndoorFloorPlanContext> floorPlanObjects  = (List<IndoorFloorPlanContext>) context.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS);

        if (floorPlanObjects != null) {
	    long orgId = AccountUtil.getCurrentOrg().getId();
        List<Map<String, Object>> floorplanObjectProps = new ArrayList<>();
		for(IndoorFloorPlanContext floorPlanObject : floorPlanObjects) {
			floorPlanObject.setOrgId(orgId);
			floorplanObjectProps.add(FieldUtil.getAsProperties(floorPlanObject));
		}

        if (floorplanObjectProps != null && !floorplanObjectProps.isEmpty()) {
			for(Map<String, Object> floorplanObjectProp:floorplanObjectProps) {
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getIndoorFloorPlanObjectModule().getTableName())
                .fields(FieldFactory.getIndoorFloorPlanObjectFields())
             .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(floorPlan.getId()), NumberOperators.EQUALS));
           updateBuilder.update(floorplanObjectProp);
			}
		}
        
        }
		return false;
	}

}
