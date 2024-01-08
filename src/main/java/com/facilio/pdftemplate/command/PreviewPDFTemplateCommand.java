package com.facilio.pdftemplate.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.pdftemplate.context.PDFTemplate;
import com.facilio.pdftemplate.util.HandleBarsHelper;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviewPDFTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        PDFTemplate pdfTemplate = (PDFTemplate) context.get(FacilioConstants.ContextNames.PDF_TEMPLATE);
        Map<String, Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.FORMATTED_RECORD_MAP);

        String moduleName = Constants.getModBean().getModule(pdfTemplate.getModuleId()).getName();
        Map<String, Map<String, Object>> placeholders = new HashMap<>();
        placeholders.put(moduleName, recordMap);
        placeholders.put("org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()));
        placeholders.put("user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()));

        String renderedContent = HandleBarsHelper.getInstance().compile(pdfTemplate.getHtmlContent(),placeholders);

        String pdfTemplateWithCSS = renderedContent + "<style>" + pdfTemplate.getHtmlContentCss() + "</style>";
        context.put(FacilioConstants.ContextNames.PDF_TEMPLATE_HTML, pdfTemplateWithCSS);
        context.put(FacilioConstants.ContextNames.PDF_TEMPLATE_RAW_HTML, renderedContent);
        return false;
    }
}

