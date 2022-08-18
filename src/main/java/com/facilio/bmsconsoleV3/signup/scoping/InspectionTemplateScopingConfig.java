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


public class InspectionTemplateScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
            //adding scope in Facilio
            long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("SITE_ID","siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            condition.setModuleName(module.getName());
            criteria.addAndCondition(condition);
            Condition condition1 = CriteriaAPI.getCondition("dummy","sites", "com.facilio.modules.ContainsSiteValueGenerator", ScopeOperator.SCOPING_IS);
            condition1.setModuleName(module.getName());
            criteria.addOrCondition(condition1);
            scoping.setScopingId(applicationScopingId);
            scoping.setModuleId(module.getModuleId());
            scoping.setCriteria(criteria);
            //adding scope in Maintenance
            long maintenanceScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            ScopingConfigContext maintenance_scoping = new ScopingConfigContext();
            Criteria maintenance_criteria = new Criteria();
            Condition maintenance_condition = CriteriaAPI.getCondition("SITE_ID","siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            maintenance_condition.setModuleName(module.getName());
            maintenance_criteria.addAndCondition(maintenance_condition);
            Condition maintenance_condition1 = CriteriaAPI.getCondition("dummy","sites", "com.facilio.modules.ContainsSiteValueGenerator", ScopeOperator.SCOPING_IS);
            maintenance_condition1.setModuleName(module.getName());
            maintenance_criteria.addOrCondition(maintenance_condition1);
            maintenance_scoping.setScopingId(maintenanceScopingId);
            maintenance_scoping.setModuleId(module.getModuleId());
            maintenance_scoping.setCriteria(maintenance_criteria);
            List<ScopingConfigContext> scopingList = new ArrayList<>();
            scopingList.add(scoping);
            scopingList.add(maintenance_scoping);
            ApplicationApi.addScopingConfigForApp(scopingList);

        }
        catch(Exception e){
            e.printStackTrace();

        }
    }
}
