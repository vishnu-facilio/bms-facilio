package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorFaceAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;

public class VisitorFaceRecognitionCommand extends FacilioCommand implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(VisitorFaceRecognitionCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		LOGGER.log(Level.INFO, "Face Recognition command called....");
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(AccountUtil.getCurrentOrg().getId(), "visitor_face_recognition");
        if (orgInfo != null && orgInfo.get("value").toString().equalsIgnoreCase("true")) {
		
			List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			
			if (records != null && !records.isEmpty()) {
				for (ModuleBaseWithCustomFields record : records) {
					Long photoId = null;
					Long visitorId = null;
					FacilioModule faceModule = null;
					if (FacilioConstants.ContextNames.VISITOR.equals(moduleName)) {
						faceModule = ModuleFactory.getVisitorFacesModule();
						VisitorContext visitor = VisitorManagementAPI.getVisitor(record.getId(), null);
						visitorId = visitor.getId();
						photoId = visitor.getAvatarId();
					}
					else if (FacilioConstants.ContextNames.VISITOR_LOGGING.equals(moduleName)) {
						faceModule = ModuleFactory.getVisitorFacesModule();
						VisitorLoggingContext visitorLog = (VisitorLoggingContext) record;
//						visitorLog = VisitorManagementAPI.getVisitorLogging(visitorLog.getVisitor().getId(), false, visitorLog.getId());
						visitorId = visitorLog.getVisitor().getId();
						photoId = visitorLog.getAvatarId();
					}
					
					if (visitorId != null && photoId != null && photoId > 0) {
						try {
							VisitorFaceAPI.indexVisitorFace(faceModule, VisitorFaceAPI.AWS_VISITOR_COLLECTION_ID(), visitorId, photoId);
						}
						catch (Exception e) {
							LOGGER.log(Level.WARNING, "Exception when index visitor face. visitorId: "+visitorId+" photoId: "+photoId, e);
						}
					}
				}
			}
        }
		return false;
	}
}
