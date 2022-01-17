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

public class InsuranceScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule insuranceModule = modBean.getModule(FacilioConstants.ContextNames.INSURANCE);

            //adding vendor scope in vendor portal
            long vendorPortalScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            condition.setModuleName(insuranceModule.getName());
            criteria.addAndCondition(condition);
            scoping.setScopingId(vendorPortalScopingId);
            scoping.setModuleId(insuranceModule.getModuleId());
            scoping.setCriteria(criteria);
            ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
