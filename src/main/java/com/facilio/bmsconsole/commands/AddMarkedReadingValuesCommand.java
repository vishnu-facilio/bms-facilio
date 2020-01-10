package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.util.MarkingUtil;
import com.facilio.constants.FacilioConstants;

public class AddMarkedReadingValuesCommand extends FacilioCommand {

	
	private static final Logger LOGGER = LogManager.getLogger(AddMarkedReadingValuesCommand.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<MarkedReadingContext> markedList= (List<MarkedReadingContext>)context.get(FacilioConstants.ContextNames.MARKED_READINGS);
		Boolean isCopyReading = (Boolean) context.get("IS_COPY_READING");
		
		if(isCopyReading == true) {
			LOGGER.info("####Asset Copy Reading execution in AddMarkedReadingValuesCommand start time : "+ System.currentTimeMillis());
		}
		if(markedList==null || markedList.isEmpty()) {
			return false;
		}
		MarkingUtil.addMarkedreadings(markedList);
		return false;
	}
	
	

}
