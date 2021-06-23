package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class updateFloorPlanCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FloorPlanContext floorPlan = (FloorPlanContext) context.get(FacilioConstants.ContextNames.FLOOR_PLAN);
		FacilioModule floorplan = ModuleFactory.getFloorPlanModule();

		Map<String, Object> props = FieldUtil.getAsProperties(floorPlan);

				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                 .table(ModuleFactory.getFloorPlanModule().getTableName())
 				.fields(FieldFactory.getFloorPlanFields())
  				.andCondition(CriteriaAPI.getCondition("Floor_Plan.ID", "id", String.valueOf(floorPlan.getFloorPlanId()), NumberOperators.EQUALS));
				updateBuilder.update(props);
			
		return false;
	}
}
