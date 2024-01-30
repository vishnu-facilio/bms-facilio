package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.PublicFileUtil;
import com.facilio.services.pdf.PDFOptions;
import com.facilio.services.pdf.PDFService;
import com.facilio.services.pdf.PDFServiceFactory;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.Map;

public class DashboardExportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String linkName = (String) context.get(FacilioConstants.ContextNames.LINK_NAME);
        String fileName = "dashboardPdf-" + System.currentTimeMillis();
        String appLink = AccountUtil.getCurrentApp().getLinkName();
        Map<String,Object> options = new HashMap<>();
//        options.put("landscape",true);
//        options.put("format","A3");
        PDFOptions pdfOptions = FieldUtil.getAsBeanFromMap(options, PDFOptions.class);
        long fileId = PDFServiceFactory.getPDFService().exportURL(fileName, "/dashboard/"+ appLink +"/dashboard/pdf/"+linkName+"?hideHeader=true&hideSidebar=true", PDFService.ExportType.PDF, pdfOptions);
        if(fileId > 0) {
            context.put("fileUrl", PublicFileUtil.createFileUrlForOrg(-1,-1,fileId,true,false,"dashboard"));
        }
        return false;
    }
}
