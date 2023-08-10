package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkflowRuleActionLogContext;
import com.facilio.bmsconsole.context.WorkflowRuleLogContext;
import com.facilio.bmsconsole.util.WorkflowRuleLogUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class GetWorkflowRuleActionLogs extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<WorkflowRuleLogContext> workflowRuleLogContextList = Constants.getRecordList((FacilioContext) context);
        List<Long> ruleIds=workflowRuleLogContextList.stream().map(i->i.getId()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(ruleIds)) {
            List<WorkflowRuleActionLogContext> actions = WorkflowRuleLogUtil.getActionsForRuleId(ruleIds);
            Map<Long, List<WorkflowRuleActionLogContext>> actionsContextMap=new HashMap<>();
            if (CollectionUtils.isNotEmpty(actions)) {
                actionsContextMap = actions.stream().collect(Collectors.groupingBy(WorkflowRuleActionLogContext::getWorkflowRuleLogId));
            }
            for (WorkflowRuleLogContext rule : workflowRuleLogContextList)
            {
                if(MapUtils.isNotEmpty(actionsContextMap)&&actionsContextMap.containsKey(rule.getId())){
                    rule.setActions(actionsContextMap.get(rule.getId()));
                }
                rule.setRecordModuleName(modBean.getModule(rule.getRecordModuleId()).getDisplayName());
                }
            }

        return false;
    }
}
