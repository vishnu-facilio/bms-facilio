package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class FetchCurrentDaySlotsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<ControlScheduleSlot> slots = (List<ControlScheduleSlot>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(context.get(FacilioConstants.ContextNames.MODULE_NAME)));
		
		String controlGroupModuleName = (String) ((Map<String,Object>)context.get("rawInput")).get("controlGroupModuleName");
		
		if(controlGroupModuleName == null) {
			controlGroupModuleName = ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		ControlScheduleSlot currentSlot = slots.get(0);
		
		Long curentDayTime = currentSlot.getStartTime();
		
		List<FacilioField> fields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		Criteria criteria = new Criteria();
		
		long startTime = DateTimeUtil.getDayStartTimeOf(curentDayTime);
		
		long endTime = DateTimeUtil.getDayEndTimeOf(curentDayTime);
		
		ControlGroupContext controlGroup = ControlScheduleUtil.getControlGroup(currentSlot.getGroup().getId(),controlGroupModuleName);
		
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("startTime"), startTime+"", DateOperators.IS_AFTER));
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("endTime"), endTime+"", DateOperators.IS_BEFORE));
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("group"), currentSlot.getGroup().getId()+"", NumberOperators.EQUALS));
		
		List<Object> slotsToCompute = ControlScheduleUtil.fetchRecord(ControlScheduleSlot.class, ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME, criteria, null);
		
		ControlScheduleUtil.deleteDeleteGroupedSlotAndCommands(controlGroup, startTime, endTime);
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME,controlGroup);
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_UNPLANNED_SLOTS,slotsToCompute);
		
		return false;
	}

}
