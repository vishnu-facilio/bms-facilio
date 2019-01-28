package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ConsumptionAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class GetTotalConsumptionByBuidlingCommand implements Command{
	
	private static final Logger LOGGER = Logger.getLogger(GetTotalConsumptionByBuidlingCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
			
		long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String fieldName = (String) context.get(FacilioConstants.ContextNames.MODULE_FIELD_NAME);
	    
		Map<String,Object> totalConsumptionByBuilding = ConsumptionAPI.getTotalConsumptionByBuildings(startTime, endTime, moduleName, fieldName);
		context.put(FacilioConstants.ContextNames.TOTAL_CONSUMPTION, totalConsumptionByBuilding);
		
		
		return false;
	}
	

}
