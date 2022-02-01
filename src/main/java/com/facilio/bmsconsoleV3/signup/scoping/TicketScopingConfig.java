package com.facilio.bmsconsoleV3.signup.scoping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.ArrayList;
import java.util.List;

public class TicketScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TICKET);

            //adding scope in Facilio
            long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            condition.setModuleName(module.getName());
            criteria.addAndCondition(condition);
            scoping.setScopingId(applicationScopingId);
            scoping.setModuleId(module.getModuleId());
            scoping.setCriteria(criteria);
            scoping.setModuleId(module.getModuleId());

            //adding scope in Tenant Portal
            long tenantPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            Criteria criteria_tenant = new Criteria();
            Condition tenantCondition = CriteriaAPI.getCondition("tenant", "com.facilio.modules.UserValueGenerator", ScopeOperator.SCOPING_IS);
            tenantCondition.setModuleName(module.getName());
            criteria_tenant.addAndCondition(tenantCondition);
            tenantScoping.setScopingId(tenantPortalScopingId);
            tenantScoping.setModuleId(module.getModuleId());
            tenantScoping.setCriteria(criteria_tenant);

            //adding scope in Vendor Portal
            long vendorPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
            ScopingConfigContext vendorScoping = new ScopingConfigContext();
            Criteria criteria_vendor = new Criteria();
            Condition vendorCondition = CriteriaAPI.getCondition("vendor", "com.facilio.modules.UserValueGenerator", ScopeOperator.SCOPING_IS);
            vendorCondition.setModuleName(module.getName());
            criteria_vendor.addAndCondition(vendorCondition);
            vendorScoping.setScopingId(vendorPortalScopingId);
            vendorScoping.setModuleId(module.getModuleId());
            vendorScoping.setCriteria(criteria_vendor);

            List<ScopingConfigContext> scopingList = new ArrayList<>();
            scopingList.add(scoping);
            scopingList.add(tenantScoping);
            scopingList.add(vendorScoping);

            ApplicationApi.addScopingConfigForApp(scopingList);


        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
