package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class PlanControlScheduleExceptionSlotCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> exceptionIds = (List<Long>) context.get("recordIds");
		
		ControlScheduleExceptionContext exception = null;
		
		String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
		
		if(exceptionIds != null && !exceptionIds.isEmpty()) {
			exception = new ControlScheduleExceptionContext();
			exception.setId(exceptionIds.get(0));
		}
		else {
			exception = (ControlScheduleExceptionContext) ControlScheduleUtil.getObjectFromRecordMap(context, moduleName);
		}
		
		deleteCurrentExceptionSlots(exception);
		
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
			
			String groupModuleName = null;
			
			if(moduleName.equals(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME)) {
				
				groupModuleName = ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME;
			}
			else if (moduleName.equals(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME)) {
				
				groupModuleName = ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME;
			}
			
			List<ControlGroupContext> groups = ControlScheduleUtil.fetchRecord(ControlGroupContext.class, groupModuleName, null, CriteriaAPI.getCondition("CONTROL_SCHEDULE", "controlSchedule", relatedScheduleId+"", NumberOperators.EQUALS));
			
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
						
						controlGroupContext = ControlScheduleUtil.getControlGroup(controlGroupContext.getId(),groupModuleName);
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

	private void deleteCurrentExceptionSlots(ControlScheduleExceptionContext exception) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> slotFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME);
		
		Map<String, FacilioField> slotFieldMap = FieldFactory.getAsMap(slotFields);
		
		DeleteRecordBuilder<ControlScheduleSlot> delete = new  DeleteRecordBuilder<ControlScheduleSlot>()
				.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME)
				.andCondition(CriteriaAPI.getCondition(slotFieldMap.get("exception"), exception.getId()+"", NumberOperators.EQUALS))
				;
		
		delete.delete();
	}


}
