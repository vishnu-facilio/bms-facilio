package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;

import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorFaceAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;

public class SearchVisitorByPhotoCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(SearchVisitorByPhotoCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		File searchPhoto = (File) context.get(FacilioConstants.ContextNames.VISITOR_PHOTO);
			
		if (searchPhoto != null) {
			Long visitorId = VisitorFaceAPI.searchVisitorByPhoto(ModuleFactory.getVisitorFacesModule(), VisitorFaceAPI.AWS_VISITOR_COLLECTION_ID(), FileUtils.readFileToByteArray(searchPhoto));
			
			if (visitorId != null) {
				VisitorContext visitorContext = VisitorManagementAPI.getVisitor(visitorId, null);
				context.put(FacilioConstants.ContextNames.VISITOR, visitorContext);
				
				VisitorLoggingContext visitorLogContext = VisitorManagementAPI.getValidChildLogForToday(0, System.currentTimeMillis(), true, visitorId);
				if (visitorLogContext != null) {
					context.put(FacilioConstants.ContextNames.VISITOR_LOGGING, visitorLogContext);
				}
				LOGGER.log(Level.INFO, "Visitor found for the given photo. visitorId: "+visitorId);
			}
			else {
				LOGGER.log(Level.WARNING, "The given photo not matched with any visitor.");
			}
		}		
		return false;
	}
}
