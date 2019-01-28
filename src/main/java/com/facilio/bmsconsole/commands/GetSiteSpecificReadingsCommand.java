package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class GetSiteSpecificReadingsCommand implements Command{

	private static final Logger LOGGER = Logger.getLogger(GetSiteSpecificReadingsCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
			
		FacilioModule module  = (FacilioModule) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
		FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.READING_FIELD);
		
	//    List<Map<String,Object>> siteSpecificReading = ReadingsAPI.getSiteSpecificReadings(module,field);
	//	context.put(FacilioConstants.ContextNames.WORK_ORDER_STATUS_PERCENTAGE, siteSpecificReading);
		
		
		return false;
	}
	


}
