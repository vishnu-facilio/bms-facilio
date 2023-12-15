package com.facilio.pdftemplate.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.pdftemplate.context.PDFTemplate;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.io.IOUtils;

import java.util.List;
import java.util.Map;


public class GetPDFTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPDFTemplatesModule().getTableName())
                .select(FieldFactory.getPDFTemplatesFields());

        if (id > 0) {
            builder.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getPDFTemplatesModule()));
        }
        else {
            // if templateId not passed, pick default template
            FacilioModule module = Constants.getModBean().getModule(moduleName);
            builder.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", module.getModuleId()+"", NumberOperators.EQUALS));
            builder.andCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", "true", BooleanOperators.IS));
        }

        List<Map<String,Object>> rows = builder.get();
        if (rows != null && rows.size() > 0) {
            PDFTemplate pdfTemplate = FieldUtil.getAsBeanFromMap(rows.get(0), PDFTemplate.class);
            if (pdfTemplate.getHtmlContentId() > 0) {
                FileStore fs = FacilioFactory.getFileStore();
                pdfTemplate.setHtmlContent(IOUtils.toString(fs.readFile(pdfTemplate.getHtmlContentId())));
            }
            if(pdfTemplate.getHtmlContentCssId() > 0) {
                FileStore fs = FacilioFactory.getFileStore();
                pdfTemplate.setHtmlContentCss(IOUtils.toString(fs.readFile(pdfTemplate.getHtmlContentCssId())));
            }
            context.put(FacilioConstants.ContextNames.PDF_TEMPLATE, pdfTemplate);
        }

        return false;
    }
}
