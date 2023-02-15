package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

import io.jsonwebtoken.lang.Collections;

public class ValidateAndGetAllPMPlannersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.PM_V2.PM_V2_PLANNER);
		
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		Map<String,FacilioField> pmFieldsMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.PM_V2.PM_V2_MODULE_NAME));
		
		SelectRecordsBuilder<PMPlanner> select = new SelectRecordsBuilder<PMPlanner>()
				.moduleName(FacilioConstants.PM_V2.PM_V2_PLANNER)
				.innerJoin(Constants.getModBean().getModule(FacilioConstants.PM_V2.PM_V2_MODULE_NAME).getTableName())
				.on(Constants.getModBean().getModule(FacilioConstants.PM_V2.PM_V2_MODULE_NAME).getTableName()+".ID = "+ Constants.getModBean().getModule(FacilioConstants.PM_V2.PM_V2_PLANNER).getTableName()+".PM_ID")
				.select(fields)
				.beanClass(PMPlanner.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("trigger"), "", CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("pmStatus"), String.valueOf(PlannedMaintenance.PMStatus.ACTIVE.getVal()), BooleanOperators.IS))
				;
		
		List<PMPlanner> planners = select.get();
		   
		context.put(FacilioConstants.PM_V2.PLANNER_LIST, planners);
		
		return false;
	}
}
