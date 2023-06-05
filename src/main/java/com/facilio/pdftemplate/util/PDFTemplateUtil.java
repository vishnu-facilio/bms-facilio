package com.facilio.pdftemplate.util;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.pdftemplate.context.PDFTemplate;

import com.facilio.modules.FieldUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class PDFTemplateUtil {

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
}
