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

public class WorkorderScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

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

            //adding wo scope in Occupant Portal
            long occupantScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
            ScopingConfigContext occupantScoping = new ScopingConfigContext();
            Criteria criteria_occupant = new Criteria();
            Condition occCondition = CriteriaAPI.getCondition("requester", "com.facilio.modules.UserValueGenerator", ScopeOperator.SCOPING_IS);
            occCondition.setModuleName(module.getName());
            criteria_occupant.addAndCondition(occCondition);
            occupantScoping.setScopingId(occupantScopingId);
            occupantScoping.setModuleId(module.getModuleId());
            occupantScoping.setCriteria(criteria_occupant);

            //adding site scope in Maintenance App
            long maintenanceScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            ScopingConfigContext maintenanceScoping = new ScopingConfigContext();
            Criteria criteria_maintenance = new Criteria();
            Condition maintenance_condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            maintenance_condition.setModuleName(module.getName());
            criteria_maintenance.addAndCondition(maintenance_condition);
            maintenanceScoping.setScopingId(maintenanceScopingId);
            maintenanceScoping.setModuleId(module.getModuleId());
            maintenanceScoping.setCriteria(criteria_maintenance);


            List<ScopingConfigContext> scopingList = new ArrayList<>();
            scopingList.add(scoping);
            scopingList.add(occupantScoping);
            scopingList.add(maintenanceScoping);

            ApplicationApi.addScopingConfigForApp(scopingList);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
