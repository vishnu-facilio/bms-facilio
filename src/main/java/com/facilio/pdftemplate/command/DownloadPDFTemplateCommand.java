package com.facilio.pdftemplate.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.pdftemplate.context.PDFTemplate;
import com.facilio.services.pdf.PDFOptions;
import com.facilio.services.pdf.PDFService;
import com.facilio.services.pdf.PDFServiceFactory;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadPDFTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        PDFTemplate pdfTemplate = (PDFTemplate) context.get(FacilioConstants.ContextNames.PDF_TEMPLATE);
        String pdfTemplateHtml = (String) context.get(FacilioConstants.ContextNames.PDF_TEMPLATE_HTML);

        PDFOptions pdfOptions = new PDFOptions();
        pdfOptions.setHeaderTemplate(pdfTemplate.getHeaderTemplate());
        pdfOptions.setFooterTemplate(pdfTemplate.getFooterTemplate());

        long fileId = PDFServiceFactory.getPDFService().exportHTML(pdfTemplate.getName() + ".pdf", pdfTemplateHtml , PDFService.ExportType.PDF, pdfOptions);
        context.put(FacilioConstants.ContextNames.FILE_ID, fileId);

        return false;
    }
}
