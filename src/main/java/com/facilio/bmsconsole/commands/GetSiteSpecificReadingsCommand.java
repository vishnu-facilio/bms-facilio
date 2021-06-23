package com.facilio.bmsconsole.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class GetSiteSpecificReadingsCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GetSiteSpecificReadingsCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
			
		FacilioModule module  = (FacilioModule) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
		FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.READING_FIELD);
		
	//    List<Map<String,Object>> siteSpecificReading = ReadingsAPI.getSiteSpecificReadings(module,field);
	//	context.put(FacilioConstants.ContextNames.WORK_ORDER_STATUS_PERCENTAGE, siteSpecificReading);
		
		
		return false;
	}
	


}
