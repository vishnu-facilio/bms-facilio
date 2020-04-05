package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.FloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class getAllFloorPlanCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		FloorPlanContext floorPlan = (FloorPlanContext) context.get(FacilioConstants.ContextNames.FLOOR_PLAN);
	
	      GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                  .table(ModuleFactory.getFloorPlanModule().getTableName())
                  .select(FieldFactory.getFloorPlanFields())
			.andCondition(CriteriaAPI.getCondition("Floor_Plan.ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS));

			List<Map<String, Object>> props = selectRecordBuilder.get();
			if (props != null && !props.isEmpty()) {
				context.put(FacilioConstants.ContextNames.FLOOR_PLANS, props);
			}
	      
		return false;
	}
}
