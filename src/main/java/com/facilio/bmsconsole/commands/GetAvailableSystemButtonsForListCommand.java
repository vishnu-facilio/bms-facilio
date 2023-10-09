package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CustomButtonAPI;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetAvailableSystemButtonsForListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(Constants.RECORD_MAP);

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        Integer positionType = (Integer) context.get(FacilioConstants.ContextNames.POSITION_TYPE);

        if (CollectionUtils.isNotEmpty(records)){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            List<WorkflowRuleContext> systemButtons = SystemButtonApi.getSystemButtons(module, CustomButtonRuleContext.PositionType.valueOf(positionType));
            List<Map<String,Object>> systemButtonsForRecords = new ArrayList<>();

            Set<WorkflowRuleContext> evaluatedSystemButtons = new HashSet<>();
            if (CollectionUtils.isNotEmpty(systemButtons)) {
                for (ModuleBaseWithCustomFields record : records) {
                    List<WorkflowRuleContext> executableSystemButtons = SystemButtonApi.getExecutableSystemButtons(systemButtons,moduleName,record,context);
                    if (CollectionUtils.isNotEmpty(executableSystemButtons)) {
                        evaluatedSystemButtons.addAll(executableSystemButtons);
                        record.addEvaluatedButtonIds(executableSystemButtons.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList()));
                        Map<String,Object> evaluatedButtonIdsWithRecordIds = new HashMap<>();
                        evaluatedButtonIdsWithRecordIds.put("id",record.getId());
                        evaluatedButtonIdsWithRecordIds.put("evaluatedButtonIds",executableSystemButtons.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList()));
                        systemButtonsForRecords.add(evaluatedButtonIdsWithRecordIds);
                    }
                }
            }

            Map<Long,WorkflowRuleContext> systemButtonProps = new HashMap<>();

            for(WorkflowRuleContext systemButton : evaluatedSystemButtons){
                systemButtonProps.put(systemButton.getId(),systemButton);
            }

            context.put(Constants.SYSTEM_BUTTONS, systemButtonProps);
            context.put(Constants.SYSTEM_BUTTONS_RECORDS,systemButtonsForRecords);
        }


        return false;
    }
}
