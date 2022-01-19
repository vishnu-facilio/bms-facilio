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

public class VendorScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule vendorsModule = modBean.getModule(FacilioConstants.ContextNames.VENDORS);

            //adding tenant scope in tenant portal
            long tenantPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("tenant", "com.facilio.modules.UserValueGenerator", ScopeOperator.SCOPING_IS);
            condition.setModuleName(vendorsModule.getName());
            criteria.addAndCondition(condition);
            tenantScoping.setScopingId(tenantPortalScopingId);
            tenantScoping.setModuleId(vendorsModule.getModuleId());
            tenantScoping.setCriteria(criteria);

            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
            scopingConfig.add(tenantScoping);

            ApplicationApi.addScopingConfigForApp(scopingConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
