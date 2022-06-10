package com.facilio.bmsconsoleV3.signup.scoping;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.MultiFieldOperators;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseVisitsScopingConfig  extends SignUpData {
    @Override
    public void addData() {
        try {
            List<Long> values = new ArrayList<Long>();
            Long currentSiteId = (Long) AccountUtil.getSwitchScopingFieldValue("siteId");

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_VISIT);

            //adding user scope in maintenance application
            long maintenanceScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            ScopingConfigContext maintenanceScoping = new ScopingConfigContext();
            Criteria criteria_maintenance = new Criteria();
            //Condition maintenance_condition = CriteriaAPI.getCondition("host", "com.facilio.modules.UserValueGenerator", ScopeOperator.SCOPING_IS);
            Condition maintenance_condition_1 = CriteriaAPI.getCondition("visitedSpace", "com.facilio.modules.BasespaceHasValueGenerator", ScopeOperator.SCOPING_IS);
            maintenance_condition_1.setModuleName(module.getName());
            criteria_maintenance.addAndCondition(maintenance_condition_1);
            //criteria_maintenance.addOrCondition(maintenance_condition_1);
            maintenanceScoping.setScopingId(maintenanceScopingId);
            maintenanceScoping.setModuleId(module.getModuleId());
            maintenanceScoping.setCriteria(criteria_maintenance);
            maintenanceScoping.setModuleId(module.getModuleId());

            ApplicationApi.addScopingConfigForApp(Collections.singletonList(maintenanceScoping));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
