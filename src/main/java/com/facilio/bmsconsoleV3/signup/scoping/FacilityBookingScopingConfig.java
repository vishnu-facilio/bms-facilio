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

import java.util.Arrays;
import java.util.Collections;

public class FacilityBookingScopingConfig extends SignUpData {
    @Override
    public void addData() throws Exception {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);

            //adding site scope in Facilio
            long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            condition.setModuleName(resourceModule.getName());
            criteria.addAndCondition(condition);
            scoping.setScopingId(applicationScopingId);
            scoping.setModuleId(resourceModule.getModuleId());
            scoping.setCriteria(criteria);

            //adding site scope in Maintenance
            long maintenanceScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            ScopingConfigContext maintenance_scoping = new ScopingConfigContext();
            Criteria maintenance_criteria = new Criteria();
            Condition maintenance_condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            maintenance_condition.setModuleName(resourceModule.getName());
            maintenance_criteria.addAndCondition(maintenance_condition);
            maintenance_scoping.setScopingId(maintenanceScopingId);
            maintenance_scoping.setModuleId(resourceModule.getModuleId());
            maintenance_scoping.setCriteria(maintenance_criteria);
            ApplicationApi.addScopingConfigForApp(Arrays.asList(scoping,maintenance_scoping));

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
