package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.IndoorFloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class getAllIndoorFloorPlan extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		

	      GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                  .table(ModuleFactory.getIndoorFloorPlanModule().getTableName())
                  .select(FieldFactory.getIndoorFloorPlanFields());
			List<Map<String, Object>> props = selectRecordBuilder.get();

			if (props != null && !props.isEmpty()) {
				List<IndoorFloorPlanContext> floorplanList = new ArrayList<>();
				for(Map<String, Object> prop : props) {
					floorplanList.add(FieldUtil.getAsBeanFromMap(prop, IndoorFloorPlanContext.class));
				}
				context.put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLANS, floorplanList);
			}
	      
		return false;
	}
}