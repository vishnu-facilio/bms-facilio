package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class GetSystemButtonCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        if(StringUtils.isEmpty(moduleName)){
            throw new Exception("Module Name cannot be null");
        }
        if(id<=0){
            throw new Exception("Id is invalid");
        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        if (module == null){
            throw new IllegalArgumentException("Module cannot be null");
        }
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getSystemButtonRuleModule()));
        criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID","moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        List<WorkflowRuleContext> systemButtonList =  WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getSystemButtonRuleModule(), FieldFactory.getSystemButtonRuleFields(),
                criteria,null,null, SystemButtonRuleContext.class);
        systemButtonList = WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(systemButtonList, SystemButtonRuleContext.class),true,true);
        context.put(FacilioConstants.ContextNames.SYSTEM_BUTTON, CollectionUtils.isNotEmpty(systemButtonList) ? systemButtonList.get(0) : null);
        return false;
    }
}
