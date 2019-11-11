package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrUpdateServiceCatalogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ServiceCatalogContext serviceCatalog = (ServiceCatalogContext) context.get(FacilioConstants.ContextNames.SERVICE_CATALOG);
        if (serviceCatalog != null && StringUtils.isNotEmpty(moduleName)) {
            if (serviceCatalog.getId() > 0) {
                serviceCatalog.setFormId(-1);
                updateServiceCatalog(serviceCatalog);
            }
            else {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                if (module == null) {
                    throw new IllegalArgumentException("Invalid module");
                }

                serviceCatalog.setModuleId(module.getModuleId());
                validateForm(serviceCatalog.getForm());
                addServiceCatalog(serviceCatalog);
            }
        }
        return false;
    }

    private void updateServiceCatalog(ServiceCatalogContext serviceCatalog) throws Exception {
        StateFlowRulesAPI.addOrUpdateFormDetails(serviceCatalog);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .fields(FieldFactory.getServiceCatalogFields())
                .andCondition(CriteriaAPI.getIdCondition(serviceCatalog.getId(), ModuleFactory.getServiceCatalogModule()))
                ;
        builder.update(FieldUtil.getAsProperties(serviceCatalog));
    }

    private void addServiceCatalog(ServiceCatalogContext serviceCatalog) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .fields(FieldFactory.getServiceCatalogFields())
                ;
        Map<String, Object> map = FieldUtil.getAsProperties(serviceCatalog);
        builder.insert(map);
        serviceCatalog.setId((long) map.get("id"));

        StateFlowRulesAPI.addOrUpdateFormDetails(serviceCatalog);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .fields(Collections.singletonList(FieldFactory.getField("formId", "FORM_ID", ModuleFactory.getServiceCatalogModule(), FieldType.NUMBER)))
                .andCondition(CriteriaAPI.getIdCondition(serviceCatalog.getId(), ModuleFactory.getServiceCatalogModule()));
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("formId", serviceCatalog.getFormId());
        updateBuilder.update(updateMap);
    }

    private void validateForm(FacilioForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Invalid form");
        }
        form.setFormType(FacilioForm.FormType.SERVICE_CATALOG);
        form.setName("servicecatalog");
        List<FormSection> sections = form.getSections();
        if (sections == null || sections.size() != 2) {
            throw new IllegalArgumentException("Sections should be always 2");
        }
    }
}
