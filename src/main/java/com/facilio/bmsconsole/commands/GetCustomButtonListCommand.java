package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CustomButtonAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.List;

public class GetCustomButtonListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        if (StringUtils.isEmpty(moduleName)) {
            throw new Exception("Module cannot be empty");
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new Exception("Module cannot be empty");
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        List<WorkflowRuleContext> allCustomButtons = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getCustomButtonRuleModule(),
                        FieldFactory.getCustomButtonRuleFields(), criteria, searchString, pagination, CustomButtonRuleContext.class);

        allCustomButtons = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(allCustomButtons, CustomButtonRuleContext.class), true, true);
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, allCustomButtons);
        return false;
    }
}
