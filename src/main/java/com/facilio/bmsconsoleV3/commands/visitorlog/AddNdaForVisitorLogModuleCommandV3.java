package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.pdf.PdfUtil;
import com.facilio.v3.context.Constants;

public class AddNdaForVisitorLogModuleCommandV3 extends FacilioCommand implements Serializable {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<VisitorLogContextV3> visitorLoggings = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(visitorLoggings)) {
            for(VisitorLogContextV3 vL : visitorLoggings) {
                if(vL.isNdaSigned()) {
                    File file = PdfUtil.exportUrlAsFile(FacilioProperties.getClientAppUrl() + "/app/pdf/visitornda/" + vL.getId(), vL.getId()+" - NDA");
                    //VisitorManagementAPI.updateVisitorLogNDA(vL.getId(), fileId);
                    V3VisitorManagementAPI.updateVisitorLogNDA(vL.getId(), file);
                }
            }
        }
        return false;
    }
}

