package com.facilio.pdftemplate.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.pdftemplate.context.PDFTemplate;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class AddOrUpdatePDFTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        PDFTemplate pdfTemplate = (PDFTemplate) context.get(FacilioConstants.ContextNames.PDF_TEMPLATE);

        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);

        FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(pdfTemplate.getName()), "Name cannot be empty");

        pdfTemplate.setModuleId(module.getModuleId());
        pdfTemplate.setSysModifiedBy(AccountUtil.getCurrentUser().getOuid());
        pdfTemplate.setSysModifiedTime(DateTimeUtil.getCurrenTime());

        if (StringUtils.isNotEmpty(pdfTemplate.getHtmlContent())) {
            FileStore fs = FacilioFactory.getFileStore();
            if (pdfTemplate.getHtmlContentId() > 0) {
                fs.deleteFile(pdfTemplate.getHtmlContentId());
            }

            long htmlContentId = fs.addFile("pdftemplate-"+System.currentTimeMillis()+".html", pdfTemplate.getHtmlContent(), "text/html");
            pdfTemplate.setHtmlContentId(htmlContentId);
        }

        if(StringUtils.isNotEmpty(pdfTemplate.getHtmlContentCss())) {
            FileStore fs = FacilioFactory.getFileStore();
            if(pdfTemplate.getHtmlContentCssId() > 0) {
                fs.deleteFile(pdfTemplate.getHtmlContentCssId());
            }
            long htmlContentCssId = fs.addFile("pdftemplate-"+System.currentTimeMillis()+".css", pdfTemplate.getHtmlContentCss(), "text/css");
            pdfTemplate.setHtmlContentCssId(htmlContentCssId);
        }

        if (pdfTemplate.getId() > 0) {
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getPDFTemplatesModule().getTableName())
                    .fields(FieldFactory.getPDFTemplatesFields())
                    .andCondition(CriteriaAPI.getIdCondition(pdfTemplate.getId(), ModuleFactory.getPDFTemplatesModule()));

            updateRecordBuilder.update(FieldUtil.getAsProperties(pdfTemplate));
        } else {
            pdfTemplate.setSysCreatedBy(AccountUtil.getCurrentUser().getOuid());
            pdfTemplate.setSysCreatedTime(DateTimeUtil.getCurrenTime());

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPDFTemplatesModule().getTableName())
                    .fields(FieldFactory.getPDFTemplatesFields());

            long pdfTemplateId = insertBuilder.insert(FieldUtil.getAsProperties(pdfTemplate));
            pdfTemplate.setId(pdfTemplateId);
        }
        context.put(FacilioConstants.ContextNames.PDF_TEMPLATE, pdfTemplate);
        return false;
    }
}
