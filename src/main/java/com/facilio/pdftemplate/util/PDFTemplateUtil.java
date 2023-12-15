package com.facilio.pdftemplate.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.pdftemplate.context.PDFTemplate;

import com.facilio.modules.FieldUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Log4j
public class PDFTemplateUtil {
    private static final String DEFAULT_PDF_TEMPLATES_PATH = FacilioUtil.normalizePath("conf/pdftemplates/defaultPDFTemplates.yml");

    public static PDFTemplate getPDFTemplate(long id) throws Exception {

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPDFTemplatesModule().getTableName())
                .select(FieldFactory.getPDFTemplatesFields())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getPDFTemplatesModule()));

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
            return pdfTemplate;
        }
        return null;
    }

    /*
    Add default PDF templates for all modules configured
     */
    public static void addDefaultPDFTemplates() throws Exception {
        populateDefaultPDFTemplates(null);
    }

    /*
    Add default PDF templates for given module
     */
    public static void addDefaultPDFTemplates(String moduleName) throws Exception {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "moduleName is empty");

        populateDefaultPDFTemplates(moduleName);
    }

    private static void populateDefaultPDFTemplates(String defaultModuleName) throws Exception {
        Map<String, Object> json = null;
        try {
            json = FacilioUtil.loadYaml(DEFAULT_PDF_TEMPLATES_PATH);
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while reading default pdf templates conf file. "+e.getMessage(), e);
            throw e;
        }
        try {
            FileStore fs = FacilioFactory.getFileStore();
            if (json != null) {
                List<Map<String, Object>> defaultPDFTemplates = (List<Map<String, Object>>) json.get("templates");
                if (CollectionUtils.isNotEmpty(defaultPDFTemplates)) {
                    for (Map<String, Object> defaultPDFTemplate : defaultPDFTemplates) {
                        String moduleName = (String) defaultPDFTemplate.get("moduleName");
                        if (StringUtils.isEmpty(defaultModuleName) || defaultModuleName.equalsIgnoreCase(moduleName)) {
                            String name = (String) defaultPDFTemplate.get("name");
                            String description = (String) defaultPDFTemplate.get("description");
                            String htmlPath = (String) defaultPDFTemplate.get("html_path");
                            String cssPath = (String) defaultPDFTemplate.get("css_path");
                            String settingsPath = (String) defaultPDFTemplate.get("settings_path");

                            File htmlFile = FacilioUtil.getConfFilePath("conf/pdftemplates/" + htmlPath);
                            File cssFile = FacilioUtil.getConfFilePath("conf/pdftemplates/" + cssPath);
                            File settingsFile = FacilioUtil.getConfFilePath("conf/pdftemplates/" + settingsPath);

                            ModuleBean modBean = Constants.getModBean();
                            FacilioModule module = modBean.getModule(moduleName);

                            FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module configured in pdf template. module: "+moduleName);
                            FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(name), "PDF Template Name cannot be empty");

                            long htmlContentId = fs.addFile(moduleName+"-pdftemplate-"+System.currentTimeMillis()+".html", htmlFile, "text/html");
                            long htmlContentCssId = fs.addFile(moduleName+"-pdftemplate-"+System.currentTimeMillis()+".css", cssFile, "text/css");

                            PDFTemplate pdfTemplate = new PDFTemplate();
                            pdfTemplate.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                            pdfTemplate.setName(name);
                            if (StringUtils.isNotEmpty(description)) {
                                pdfTemplate.setDescription(description);
                            }
                            pdfTemplate.setModuleId(module.getModuleId());
                            pdfTemplate.setIsDefault(true);
                            pdfTemplate.setHtmlContentId(htmlContentId);
                            pdfTemplate.setHtmlContentCssId(htmlContentCssId);

                            if (settingsFile != null && settingsFile.exists()) {
                                String templateSettings = FileUtils.readFileToString(settingsFile, StandardCharsets.UTF_8);
                                pdfTemplate.setTemplateSettings(templateSettings);
                            }

                            if (AccountUtil.getCurrentUser() != null) {
                                pdfTemplate.setSysCreatedBy(AccountUtil.getCurrentUser().getOuid());
                                pdfTemplate.setSysModifiedBy(AccountUtil.getCurrentUser().getOuid());
                            }
                            pdfTemplate.setSysCreatedTime(DateTimeUtil.getCurrenTime());
                            pdfTemplate.setSysModifiedTime(DateTimeUtil.getCurrenTime());

                            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                                    .table(ModuleFactory.getPDFTemplatesModule().getTableName())
                                    .fields(FieldFactory.getPDFTemplatesFields());

                            long pdfTemplateId = insertBuilder.insert(FieldUtil.getAsProperties(pdfTemplate));
                            LOGGER.info("Default PDF template created. orgId: "+AccountUtil.getCurrentOrg().getOrgId()+" moduleName: "+moduleName+" templateName: "+name+" templateId: "+pdfTemplateId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while parsing default pdf templates conf file. "+e.getMessage(), e);
            throw e;
        }
    }
}
