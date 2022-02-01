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

public class SiteScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);

            //adding site scope in Facilio
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

            //adding site scope in tenant portal
            long tenantPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            Criteria criteria_tenant = new Criteria();
            Condition tenant_condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            tenant_condition.setModuleName(module.getName());
            criteria_tenant.addAndCondition(tenant_condition);
            scoping.setScopingId(tenantPortalScopingId);
            scoping.setModuleId(module.getModuleId());
            scoping.setCriteria(criteria_tenant);
            tenantScoping.setModuleId(module.getModuleId());

            //adding site scope in occupant portal
            long occupantPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
            ScopingConfigContext occupantScoping = new ScopingConfigContext();
            Criteria criteria_occupant = new Criteria();
            Condition condition_occupant = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            condition_occupant.setModuleName(module.getName());
            criteria_occupant.addAndCondition(condition_occupant);
            occupantScoping.setScopingId(occupantPortalScopingId);
            occupantScoping.setModuleId(module.getModuleId());
            occupantScoping.setCriteria(criteria_occupant);
            occupantScoping.setModuleId(module.getModuleId());

            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
            scopingConfig.add(scoping);
            scopingConfig.add(tenantScoping);
            scopingConfig.add(occupantScoping);

            ApplicationApi.addScopingConfigForApp(scopingConfig);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
