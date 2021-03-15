package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.IndoorFloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetIndoorFloorPlanObjectsCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		IndoorFloorPlanContext floorPlan = (IndoorFloorPlanContext) context.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);

	      GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getIndoorFloorPlanObjectModule().getTableName())
                .fields(FieldFactory.getIndoorFloorPlanObjectFields())
  				.andCondition(CriteriaAPI.getCondition("INDOOR_FLOOR_PLAN_ID", "id", String.valueOf(floorPlan.getId()), NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectRecordBuilder.get();

			if (props != null && !props.isEmpty()) {
				List<IndoorFloorPlanObjectContext> floorplanObjects = new ArrayList<>();
				for(Map<String, Object> prop : props) {
					floorplanList.add(FieldUtil.getAsBeanFromMap(prop, IndoorFloorPlanObjectContext.class));
				}
                floorPlan.setFloorPlanObjects(floorplanObjects);
                context.put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN, floorPlan);
				context.put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS, floorplanObjects);
			}
	      
		return false;
	}
}
