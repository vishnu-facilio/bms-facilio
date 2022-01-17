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

public class PeopleAnnouncementScopingConfig extends SignUpData {

    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);

            //adding user scope in Tenant Portal
            long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("people", "com.facilio.modules.PeopleValueGenerator", ScopeOperator.SCOPING_IS);
            condition.setModuleName(module.getName());
            criteria.addAndCondition(condition);
            scoping.setScopingId(applicationScopingId);
            scoping.setModuleId(module.getModuleId());
            scoping.setCriteria(criteria);
            ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
