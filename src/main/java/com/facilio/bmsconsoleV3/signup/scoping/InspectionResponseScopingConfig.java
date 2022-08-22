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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InspectionResponseScopingConfig extends SignUpData {
    @Override
    public void addData() throws Exception {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule inspectionTemplateModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);
            //adding site scope in Facilio
            long applicationScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            Criteria criteria = new Criteria();
            Condition condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            condition.setModuleName(inspectionTemplateModule.getName());
            criteria.addAndCondition(condition);
            scoping.setScopingId(applicationScopingId);
            scoping.setModuleId(inspectionTemplateModule.getModuleId());
            scoping.setCriteria(criteria);

            //adding site scope in Maintenance
            long maintenanceScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            ScopingConfigContext maintenanceScoping = new ScopingConfigContext();
            Criteria maintenanceCriteria = new Criteria();
            Condition maintenanceCondition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
            maintenanceCondition.setModuleName(inspectionTemplateModule.getName());
            maintenanceCriteria.addAndCondition(maintenanceCondition);
            maintenanceScoping.setScopingId(maintenanceScopingId);
            maintenanceScoping.setModuleId(inspectionTemplateModule.getModuleId());
            maintenanceScoping.setCriteria(maintenanceCriteria);
            ApplicationApi.addScopingConfigForApp(Arrays.asList(scoping,maintenanceScoping));

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
