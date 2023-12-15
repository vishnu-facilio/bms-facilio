package com.facilio.pdftemplate.command;

import com.facilio.command.FacilioCommand;
import com.facilio.pdftemplate.util.PDFTemplateUtil;
import org.apache.commons.chain.Context;

public class AddDefaultPDFTemplatesCommand extends FacilioCommand  {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PDFTemplateUtil.addDefaultPDFTemplates();
        return false;
    }
}
