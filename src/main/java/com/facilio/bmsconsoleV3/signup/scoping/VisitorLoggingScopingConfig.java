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

public class VisitorLoggingScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule visitorLoggingModule = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);

            //adding vendor scope in vendor portal
            long vendorPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("vendor", "com.facilio.modules.UserValueGenerator", ScopeOperator.SCOPING_IS);
            condition.setModuleName(visitorLoggingModule.getName());
            criteria.addAndCondition(condition);
            scoping.setCriteria(criteria);
            scoping.setScopingId(vendorPortalScopingId);
            scoping.setModuleId(visitorLoggingModule.getModuleId());

            //adding tenant scope in tenant portal
            long tenantPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            Criteria criteria_tenant = new Criteria();
            Condition tenant_condition = CriteriaAPI.getCondition("tenant", "com.facilio.modules.UserValueGenerator", ScopeOperator.SCOPING_IS);
            tenant_condition.setModuleName(visitorLoggingModule.getName());
            criteria_tenant.addAndCondition(tenant_condition);
            scoping.setCriteria(criteria_tenant);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            tenantScoping.setScopingId(tenantPortalScopingId);
            tenantScoping.setModuleId(visitorLoggingModule.getModuleId());

            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
            scopingConfig.add(scoping);
            scopingConfig.add(tenantScoping);

            ApplicationApi.addScopingConfigForApp(scopingConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
