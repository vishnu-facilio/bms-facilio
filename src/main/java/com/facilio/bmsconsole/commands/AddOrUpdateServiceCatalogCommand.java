package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ServiceCatalogApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateServiceCatalogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ServiceCatalogContext serviceCatalog = (ServiceCatalogContext) context.get(FacilioConstants.ContextNames.SERVICE_CATALOG);
        if (serviceCatalog != null && StringUtils.isNotEmpty(moduleName)) {
            if (serviceCatalog.getId() > 0) {
                updateServiceCatalog(serviceCatalog);
            }
            else {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                if (module == null) {
                    throw new IllegalArgumentException("Invalid module");
                }

                serviceCatalog.setModuleId(module.getModuleId());
                addServiceCatalog(serviceCatalog);
            }
        }
        return false;
    }

    private void updateServiceCatalog(ServiceCatalogContext serviceCatalog) throws Exception {
        checkForDuplicateName(serviceCatalog.getName(), serviceCatalog.getId());

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .fields(FieldFactory.getServiceCatalogFields())
                .andCondition(CriteriaAPI.getIdCondition(serviceCatalog.getId(), ModuleFactory.getServiceCatalogModule()))
                ;
        builder.update(FieldUtil.getAsProperties(serviceCatalog));
    }

    private void checkForDuplicateName(String name, long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .select(FieldFactory.getServiceCatalogFields())
                .andCondition(CriteriaAPI.getCondition("NAME", "name", name, StringOperators.IS));
        if (id > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(id), NumberOperators.NOT_EQUALS));
        }
        Map<String, Object> map = builder.fetchFirst();
        if (map != null) {
            throw new IllegalArgumentException("Name cannot be duplicated");
        }
    }

    private void addServiceCatalog(ServiceCatalogContext serviceCatalog) throws Exception {
        checkForDuplicateName(serviceCatalog.getName(), -1);
        if (serviceCatalog.isComplaintType()) {
        		serviceCatalog.setGroupId(ServiceCatalogApi.getComplaintCategory().getId());
        }

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .fields(FieldFactory.getServiceCatalogFields())
                ;
        Map<String, Object> map = FieldUtil.getAsProperties(serviceCatalog);
        builder.insert(map);
        serviceCatalog.setId((long) map.get("id"));
    }

    private void validateForm(FacilioForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Invalid form");
        }
        form.setFormType(FacilioForm.FormType.SERVICE_CATALOG);
        form.setName("servicecatalog");
        form.setHideInList(true);
        List<FormSection> sections = form.getSections();
        if (sections == null || sections.size() != 2) {
            throw new IllegalArgumentException("Sections should be always 2");
        }
    }
}
