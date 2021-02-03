package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;

public class PlanControlScheduleExceptionSlotCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> exceptionIds = (List<Long>) context.get("recordIds");
		
		ControlScheduleExceptionContext exception = null;
		
		if(exceptionIds != null && !exceptionIds.isEmpty()) {
			exception = new ControlScheduleExceptionContext();
			exception.setId(exceptionIds.get(0));
		}
		else {
			String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
			exception = (ControlScheduleExceptionContext) ControlScheduleUtil.getObjectFromRecordMap(context, moduleName);
		}
		
		List<Long> relatedScheduleIds = new ArrayList<Long>();
		if(exception.getSchedule() != null) {
			relatedScheduleIds.add(exception.getSchedule().getId());
		}
		else {
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getControlScheduleVsExceptionModule().getTableName())
					.select(FieldFactory.getControlScheduleVsExceptionFields())
					.andCustomWhere("EXCEPTION_ID = ?", exception.getId());
			
			List<Map<String, Object>> props = select.get();
			
			if(props != null) {
				for(Map<String, Object> prop: props) {
					relatedScheduleIds.add((long)prop.get("scheduleId"));
				}
			}
		}
		
		for(Long relatedScheduleId : relatedScheduleIds) {
			
			List<ControlGroupContext> groups = ControlScheduleUtil.fetchRecord(ControlGroupContext.class, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, null, CriteriaAPI.getCondition("CONTROL_SCHEDULE", "controlSchedule", relatedScheduleId+"", NumberOperators.EQUALS));
			
			boolean isPlanByJob = false;
			
			if(groups != null) {
				for(ControlGroupContext controlGroupContext :groups) {
					if(isPlanByJob) {
						JSONObject obj = new JSONObject();
						
						obj.put(ControlScheduleUtil.CONTROL_GROUP_ID, controlGroupContext.getId());
						
						BmsJobUtil.deleteJobWithProps(controlGroupContext.getId(), "ControlScheduleSlotCreationJob");
						
						BmsJobUtil.scheduleOneTimeJobWithProps(controlGroupContext.getId(), "ControlScheduleSlotCreationJob", 5, "facilio", obj);
					}

					else {
						
						controlGroupContext = ControlScheduleUtil.getControlGroup(controlGroupContext.getId());
						long startTime = DateTimeUtil.getDayStartTime();
						
						long endTime = DateTimeUtil.addMonths(startTime, 1);
						
//						long endTime = DateTimeUtil.addDays(startTime, 7);
						
						FacilioChain chain = TransactionChainFactoryV3.planControlGroupSlotsAndRoutines();
						
						FacilioContext newContext = chain.getContext();
						
						newContext.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, controlGroupContext);
						newContext.put(FacilioConstants.ContextNames.START_TIME, startTime);
						newContext.put(FacilioConstants.ContextNames.END_TIME, endTime);
						
						chain.execute();
					}
				}
			}
		}
		
		return false;
	}

	private Object prop(List<Map<String, Object>> props) {
		// TODO Auto-generated method stub
		return null;
	}

}
