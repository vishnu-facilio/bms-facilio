package com.facilio.bmsconsoleV3.commands.site;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchSiteWithoutScoping extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long siteId = getSiteIdFromRecord(id);
        if (siteId > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
            SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>()
                    .module(module)
                    .beanClass(SiteContext.class)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getIdCondition(siteId, module))
                    .skipScopeCriteria();
            SiteContext siteContext = builder.fetchFirst();
            context.put("site", siteContext);
        }
        return false;
    }

    private static Long getSiteIdFromRecord(Long recordId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceReqModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST);
        List<FacilioField> srFields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_REQUEST);
        SelectRecordsBuilder<V3ServiceRequestContext> srbuilder = new SelectRecordsBuilder<V3ServiceRequestContext>()
                .moduleName(serviceReqModule.getName())
                .select(srFields)
                .beanClass(V3ServiceRequestContext.class)
                .andCondition(CriteriaAPI.getIdCondition(recordId, serviceReqModule));

        V3ServiceRequestContext context = srbuilder.fetchFirst();
        if (context != null && context.getSiteId() > 0) {
            return context.getSiteId();
        } else {
            return null;
        }

    }
}