package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlScheduleSlot;

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
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_PLANNED_SLOTS, slots);
		return false;
	}

}
