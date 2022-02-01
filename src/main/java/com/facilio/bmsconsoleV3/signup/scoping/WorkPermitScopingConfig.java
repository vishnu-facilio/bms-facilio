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

public class WorkPermitScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workPermitModule = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);

            //adding vendor scope in vendor portal
            long vendorPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("vendor", "com.facilio.modules.UserValueGenerator", ScopeOperator.SCOPING_IS);
            condition.setModuleName(workPermitModule.getName());
            criteria.addAndCondition(condition);
            scoping.setScopingId(vendorPortalScopingId);
            scoping.setModuleId(workPermitModule.getModuleId());
            scoping.setCriteria(criteria);

            //adding tenant scope in tenant portal
            long tenantPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            Criteria criteria_tenant = new Criteria();
            Condition tenantCondition = CriteriaAPI.getCondition("tenant", "com.facilio.modules.UserValueGenerator", ScopeOperator.SCOPING_IS);
            tenantCondition.setModuleName(workPermitModule.getName());
            criteria_tenant.addAndCondition(tenantCondition);
            tenantScoping.setScopingId(tenantPortalScopingId);
            tenantScoping.setModuleId(workPermitModule.getModuleId());
            tenantScoping.setCriteria(criteria_tenant);

            //adding site scope in Facilio
            long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ScopingConfigContext appScoping = new ScopingConfigContext();
            Criteria criteria_main = new Criteria();
            Condition mainCondition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            mainCondition.setModuleName(workPermitModule.getName());
            criteria_main.addAndCondition(mainCondition);
            appScoping.setScopingId(applicationScopingId);
            appScoping.setModuleId(workPermitModule.getModuleId());
            appScoping.setCriteria(criteria_main);

            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
            scopingConfig.add(scoping);
            scopingConfig.add(tenantScoping);
            scopingConfig.add(appScoping);

            ApplicationApi.addScopingConfigForApp(scopingConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
