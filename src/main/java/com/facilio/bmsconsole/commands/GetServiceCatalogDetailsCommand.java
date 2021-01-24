package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

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
            if (prop == null) {
                throw new IllegalArgumentException("Service Catalog not found");
            }

            ServiceCatalogContext serviceCatalog = FieldUtil.getAsBeanFromMap(prop, ServiceCatalogContext.class);
            long formId = serviceCatalog.getFormId();
            
            
            Context formMetaContext = new FacilioContext();
    		
            formMetaContext.put(FacilioConstants.ContextNames.FORM_ID, formId);
            formMetaContext.put(FacilioConstants.ContextNames.FETCH_FORM_RULE_FIELDS, true);
    		
    		FacilioChain c = FacilioChainFactory.getFormMetaChain();
    		c.execute(formMetaContext);
    		
    		FacilioForm form = (FacilioForm) formMetaContext.get(FacilioConstants.ContextNames.FORM);
            serviceCatalog.setForm(form);

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(serviceCatalog.getModuleId());
            serviceCatalog.setModule(module);

            context.put(FacilioConstants.ContextNames.SERVICE_CATALOG, serviceCatalog);
        }
        return false;
    }
}
