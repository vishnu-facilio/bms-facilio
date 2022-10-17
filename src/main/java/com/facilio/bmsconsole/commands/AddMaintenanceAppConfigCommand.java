package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.induction.AddInductionModules;
import com.facilio.bmsconsoleV3.signup.inspection.AddInspectionModules;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AddMaintenanceAppConfigCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<String> excludeModules = Arrays.asList(FacilioConstants.ContextNames.RESOURCE);
        Long scopingId = (Long) context.get(FacilioConstants.ContextNames.Maintenance.MAINTENANCE_ADMIN_SCOPING_ID);
        Set<String> siteIdAllowedModules = FieldUtil.getSiteIdAllowedModules();
        for (String moduleName : siteIdAllowedModules) {
            if(!excludeModules.contains(moduleName)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                if (module != null) {
                    if (moduleName.equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)) {
                        AddInspectionModules inspectionModules = new AddInspectionModules();
                        inspectionModules.addScopingMaintenanceApp(module, scopingId);
                    } else if (moduleName.equals(FacilioConstants.Induction.INDUCTION_TEMPLATE)) {
                        AddInductionModules inductionModules = new AddInductionModules();
                        inductionModules.addScopingForMaintenanceApp(module, scopingId);
                    } else {
                        if (!moduleName.equals(FacilioConstants.Induction.INDUCTION_TEMPLATE) && !moduleName.equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)) {
                            //adding site scope in application
                            ScopingConfigContext maintenanceScoping = new ScopingConfigContext();
                            Criteria criteria_maintenance = new Criteria();
                            Condition maintenance_condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
                            maintenance_condition.setModuleName(module.getName());
                            criteria_maintenance.addAndCondition(maintenance_condition);
                            maintenanceScoping.setScopingId(scopingId);
                            maintenanceScoping.setModuleId(module.getModuleId());
                            maintenanceScoping.setCriteria(criteria_maintenance);
                            maintenanceScoping.setModuleId(module.getModuleId());
                            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
                            scopingConfig.add(maintenanceScoping);
                            ApplicationApi.addScopingConfigForApp(scopingConfig);
                        }
                    }
                }
            }
        }
        return false;
    }
}
