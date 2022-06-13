package com.facilio.bmsconsoleV3.signup.scoping;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class ToolScopingConfig  extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
            //adding user scope in maintenance application
            long maintenanceScopingId = ApplicationApi.addDefaultScoping(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            ScopingConfigContext maintenanceScoping = new ScopingConfigContext();
            Criteria criteria_maintenance = new Criteria();
            Condition maintenance_condition = CriteriaAPI.getCondition("storeRoom", "com.facilio.modules.StoreRoomValueGenerator", ScopeOperator.SCOPING_IS);
            maintenance_condition.setModuleName(module.getName());
            criteria_maintenance.addAndCondition(maintenance_condition);
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