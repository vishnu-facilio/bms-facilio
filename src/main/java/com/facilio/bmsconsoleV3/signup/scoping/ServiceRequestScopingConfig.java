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

import java.util.Collections;

public class ServiceRequestScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST);

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
            ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
            
            
            long tenantAppScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenant_scoping = new ScopingConfigContext();
            Criteria criteria_tenant = new Criteria();
            Condition tenantCondition = CriteriaAPI.getCondition("requester", "com.facilio.modules.PeopleValueGenerator", ScopeOperator.SCOPING_IS);
            tenantCondition.setModuleName(module.getName());
            criteria_tenant.addAndCondition(tenantCondition);
            tenant_scoping.setScopingId(tenantAppScopingId);
            tenant_scoping.setModuleId(module.getModuleId());
            tenant_scoping.setCriteria(criteria_tenant);
            ApplicationApi.addScopingConfigForApp(Collections.singletonList(tenant_scoping));

            //adding site scope in Maintenance
            long maintenanceScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            ScopingConfigContext maintenance_scoping = new ScopingConfigContext();
            Criteria maintenance_criteria = new Criteria();
            Condition maintenance_condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            maintenance_condition.setModuleName(module.getName());
            maintenance_criteria.addAndCondition(maintenance_condition);
            maintenance_scoping.setScopingId(maintenanceScopingId);
            maintenance_scoping.setModuleId(module.getModuleId());
            maintenance_scoping.setCriteria(maintenance_criteria);
            ApplicationApi.addScopingConfigForApp(Collections.singletonList(maintenance_scoping));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
