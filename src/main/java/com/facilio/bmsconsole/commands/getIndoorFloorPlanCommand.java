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

public class getIndoorFloorPlanCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		IndoorFloorPlanContext floorPlan = (IndoorFloorPlanContext) context.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);

	      GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                  .table(ModuleFactory.getIndoorFloorPlanModule().getTableName())
                  .select(FieldFactory.getIndoorFloorPlanFields())
  				.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(floorPlan.getId()), NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectRecordBuilder.get();

			if (props != null && !props.isEmpty()) {
				IndoorFloorPlanContext floorplanContext = FieldUtil.getAsBeanFromMap(props.get(0), IndoorFloorPlanContext.class);
				context.put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN, floorplanContext);
			}
	      
		return false;
	}
}
