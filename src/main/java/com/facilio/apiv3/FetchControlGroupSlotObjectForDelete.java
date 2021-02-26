package com.facilio.apiv3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;

public class FetchControlGroupSlotObjectForDelete extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> slotIds = (List<Long>) context.get("recordIds");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<ControlScheduleSlot> records = ControlScheduleUtil.fetchRecord(ControlScheduleSlot.class, ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME, null, CriteriaAPI.getIdCondition(slotIds, modBean.getModule(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME)));
		
		Map<String,Object> recordMap = new HashMap<String, Object>();
		
		recordMap.put((String)context.get(FacilioConstants.ContextNames.MODULE_NAME), records);
		
		context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
		
		context.put(FacilioConstants.ContextNames.ACTION, "delete");
		return false;
	}

}
