package com.facilio.bmsconsoleV3.signup.employeePortalApp;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
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
import java.util.List;
import java.util.Set;

public class AddEmployeePortalScoping extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long scopingId = (Long) context.get(FacilioConstants.ContextNames.EmployeePortal.EMPLOYEE_ADMIN_SCOPING_ID);
        Set<String> siteIdAllowedModules = FieldUtil.getSiteIdAllowedModules();
        for (String moduleName : siteIdAllowedModules) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                if (module != null) {
                        //adding site scope in application
                        ScopingConfigContext employeePortalScoping = new ScopingConfigContext();
                        Criteria criteria_employee = new Criteria();
                        Condition employee_condition = CriteriaAPI.getCondition("siteId", "com.facilio.modules.SiteValueGenerator", ScopeOperator.SCOPING_IS);
                        employee_condition.setModuleName(module.getName());
                        criteria_employee.addAndCondition(employee_condition);
                        employeePortalScoping.setScopingId(scopingId);
                        employeePortalScoping.setModuleId(module.getModuleId());
                        employeePortalScoping.setCriteria(criteria_employee);
                        employeePortalScoping.setModuleId(module.getModuleId());
                        List<ScopingConfigContext> scopingConfig = new ArrayList<>();
                        scopingConfig.add(employeePortalScoping);
                        ApplicationApi.addScopingConfigForApp(scopingConfig);
                    }
                }

        return false;
    }
}
