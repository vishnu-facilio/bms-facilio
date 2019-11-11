package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetServiceCatalogDetailsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        if (id > 0) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getServiceCatalogModule().getTableName())
                    .select(FieldFactory.getServiceCatalogFields())
                    .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getServiceCatalogModule()));

            Map<String, Object> prop = builder.fetchFirst();
            ServiceCatalogContext serviceCatalog = FieldUtil.getAsBeanFromMap(prop, ServiceCatalogContext.class);
            long formId = serviceCatalog.getFormId();
            FacilioForm form = FormsAPI.getFormFromDB(formId);
            serviceCatalog.setForm(form);

            context.put(FacilioConstants.ContextNames.SERVICE_CATALOG, serviceCatalog);
        }
        return false;
    }
}
