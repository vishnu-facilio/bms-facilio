package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.pdf.PdfUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class AddNdaForVisitorLogCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorLoggingContext> visitorLoggings = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(visitorLoggings)) {
            for(V3VisitorLoggingContext vL : visitorLoggings) {
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
