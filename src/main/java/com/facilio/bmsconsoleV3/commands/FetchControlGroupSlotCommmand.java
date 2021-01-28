package com.facilio.bmsconsoleV3.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.map.HashedMap;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class FetchControlGroupSlotCommmand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		ControlGroupContext group = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> slotFieldMap = FieldFactory.getAsMap(modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME));
		
		Criteria criteria = new Criteria();
		
		criteria.addAndCondition(CriteriaAPI.getCondition(slotFieldMap.get("group"), group.getId()+"", NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition(slotFieldMap.get("startTime"), startTime+"", DateOperators.IS_AFTER));
		criteria.addAndCondition(CriteriaAPI.getCondition(slotFieldMap.get("endTime"), endTime+"", DateOperators.IS_BEFORE));
		
		List<ControlScheduleSlot> slots = ControlScheduleUtil.fetchRecord(ControlScheduleSlot.class, ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME, criteria, null);
		
		Map<Long, ControlScheduleExceptionContext> exceptionMap = getAsExceptionMap(group.getControlSchedule().getExceptions());
		
		for(ControlScheduleSlot slot :slots) {
			if(slot.getException() != null) {
				slot.setException(exceptionMap.get(slot.getException().getId()));
			}
		}
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_PLANNED_SLOTS, slots);
		return false;
	}

	
	private Map<Long,ControlScheduleExceptionContext> getAsExceptionMap(List<ControlScheduleExceptionContext> exceptions) {
		
		if(exceptions != null) {
			Map<Long,ControlScheduleExceptionContext> exceptionMap = new HashMap<Long, ControlScheduleExceptionContext>();
			
			for(ControlScheduleExceptionContext exception : exceptions) {
				exceptionMap.put(exception.getId(), exception);
			}
			return exceptionMap;
		}
		return null;
		
		
	}
}
