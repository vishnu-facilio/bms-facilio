package com.facilio.pdftemplate.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.pdftemplate.context.PDFTemplate;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class GetPDFTemplatesListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPDFTemplatesModule().getTableName())
                .select(FieldFactory.getPDFTemplatesFields())
                .andCondition(CriteriaAPI.getModuleIdIdCondition(module.getModuleId(), ModuleFactory.getPDFTemplatesModule()));


        List<PDFTemplate> pdfTemplates = new ArrayList<>();
        List<Map<String,Object>> rows = builder.get();
        if (rows != null && rows.size() > 0) {
            for (Map<String,Object> row : rows) {
                pdfTemplates.add(FieldUtil.getAsBeanFromMap(row, PDFTemplate.class));
            }
        }

        context.put(FacilioConstants.ContextNames.PDF_TEMPLATES, pdfTemplates);
        return false;
    }
}
