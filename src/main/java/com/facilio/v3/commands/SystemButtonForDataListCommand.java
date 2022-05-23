package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SystemButtonForDataListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<? extends ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(records)){
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = moduleBean.getModule(moduleName);

            List<WorkflowRuleContext> systemButtons = SystemButtonApi.getSystemButtons(module, CustomButtonRuleContext.PositionType.LIST_BAR, CustomButtonRuleContext.PositionType.LIST_ITEM);
            Set<WorkflowRuleContext> evaluateSystemButtons = new HashSet<>();
            if (CollectionUtils.isNotEmpty(systemButtons)){
                for (ModuleBaseWithCustomFields record : records) {
                    List<WorkflowRuleContext> executableSystemButtons = SystemButtonApi.getExecutableSystemButtons(systemButtons,moduleName,record,context);
                    if (CollectionUtils.isNotEmpty(executableSystemButtons)){
                        evaluateSystemButtons.addAll(executableSystemButtons);
                        record.addEvaluatedButtonIds(executableSystemButtons.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList()));
                    }
                }
            }
            context.put(Constants.SYSTEM_BUTTONS,evaluateSystemButtons);
        }
        return false;
    }
}
