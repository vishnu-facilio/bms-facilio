package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.VisitorFaceAPI;
import com.facilio.bmsconsoleV3.commands.visitorlogging.VisitorFaceRecognitionCommandV3;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;

public class VisitorFaceRecognitionForVisitorLogCommandV3 extends FacilioCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(VisitorFaceRecognitionCommandV3.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.log(Level.INFO, "Face Recognition command called....");

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(AccountUtil.getCurrentOrg().getId(), "visitor_face_recognition");
        if (orgInfo != null && orgInfo.get("value").toString().equalsIgnoreCase("true")) {

            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<VisitorLogContextV3> records = recordMap.get(moduleName);

            if (records != null && !records.isEmpty()) {
                for (ModuleBaseWithCustomFields record : records) {
                    Long photoId = null;
                    Long visitorId = null;
                    FacilioModule faceModule = null;
                    if (FacilioConstants.ContextNames.VISITOR.equals(moduleName)) {
                        faceModule = ModuleFactory.getVisitorFacesModule();
                        V3VisitorContext visitor = V3VisitorManagementAPI.getVisitor(record.getId(), null);
                        visitorId = visitor.getId();
                        photoId = visitor.getAvatarId();
                    }
                    else if (FacilioConstants.ContextNames.VISITOR_LOG.equals(moduleName)) {
                        faceModule = ModuleFactory.getVisitorFacesModule();
                        VisitorLogContextV3 visitorLog = (VisitorLogContextV3) record;
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
