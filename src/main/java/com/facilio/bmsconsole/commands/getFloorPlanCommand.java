package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class getFloorPlanCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		FloorPlanContext floorPlan = (FloorPlanContext) context.get(FacilioConstants.ContextNames.FLOOR_PLAN);

	      GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                  .table(ModuleFactory.getFloorPlanModule().getTableName())
                  .select(FieldFactory.getFloorPlanFields())
  					.andCondition(CriteriaAPI.getCondition("Floor_Plan.ID", "id", String.valueOf(floorPlan.getId()), NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectRecordBuilder.get();

			if (props != null && !props.isEmpty()) {
				FloorPlanContext floorplanContext = FieldUtil.getAsBeanFromMap(props.get(0), FloorPlanContext.class);
				context.put(FacilioConstants.ContextNames.FLOOR_PLAN, props.get(0));
				context.put(FacilioConstants.ContextNames.FLOOR_PLAN_VIEW,  floorplanContext);
			}
	      
		return false;
	}

}
