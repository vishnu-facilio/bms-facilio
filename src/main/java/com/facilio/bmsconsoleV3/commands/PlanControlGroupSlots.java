package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleGroupedSlot;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class PlanControlGroupSlots extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ControlGroupContext controlGroup = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		long startTime = (long) context.getOrDefault(FacilioConstants.ContextNames.START_TIME, DateTimeUtil.getCurrenTime());
		
		long endTime = (long) context.getOrDefault(FacilioConstants.ContextNames.END_TIME,DateTimeUtil.addMonths(startTime, 1));
		
		ControlScheduleUtil.deleteSlotsAndGroupedSlots(controlGroup,startTime,endTime);
		
		List<ControlScheduleSlot> slots = ControlScheduleUtil.planScheduleSlots(controlGroup, startTime, endTime);
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_UNPLANNED_SLOTS,slots);
		
		ControlScheduleUtil.addRecord(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME, slots);
		return false;
	}


}
