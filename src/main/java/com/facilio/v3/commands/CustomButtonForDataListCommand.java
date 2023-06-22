package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.CustomButtonAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CustomButtonForDataListCommand extends FacilioCommand {

    private static final Logger LOGGER =
            LogManager.getLogger(CustomButtonForDataListCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(skipCustomButtonForDataListCommand()){
            return false;
        }

        Object withoutCustomButton = context.get(Constants.WITHOUT_CUSTOMBUTTONS); ;
        if (withoutCustomButton != null && (Boolean)withoutCustomButton){
            LOGGER.info("skipping command for export");
            return false;
        }

        String moduleName = Constants.getModuleName(context);

        LOGGER.info("CustomButtonForDataListCommand called for moduleName:"+moduleName);

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<? extends ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        List<Integer> positionTypes = (List<Integer>) context.get(FacilioConstants.ContextNames.POSITION_TYPE);
        if (CollectionUtils.isNotEmpty(records)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            List<WorkflowRuleContext> customButtons;
            positionTypes = CollectionUtils.isEmpty(positionTypes) ? new ArrayList<Integer>(){{
                add(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
                add(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
            }} : positionTypes;

            final List<Integer> finalPositionTypes = positionTypes;
            List<CustomButtonRuleContext.PositionType> positionTypeList = EnumSet.allOf(CustomButtonRuleContext.PositionType.class).stream().filter(positionType -> finalPositionTypes.contains(positionType.getIndex())).collect(Collectors.toList());
            customButtons = CustomButtonAPI.getCustomButtons(module,positionTypeList.toArray(new CustomButtonRuleContext.PositionType[positionTypeList.size()]));

            Set<WorkflowRuleContext> evaluatedCustomButtons = new HashSet<>();
            if (CollectionUtils.isNotEmpty(customButtons)) {
                for (ModuleBaseWithCustomFields record : records) {
                    List<WorkflowRuleContext> executableCustomButtons = CustomButtonAPI.getExecutableCustomButtons(customButtons, moduleName, record, context);
                    if (CollectionUtils.isNotEmpty(executableCustomButtons)) {
                        evaluatedCustomButtons.addAll(executableCustomButtons);
                        record.addEvaluatedButtonIds(executableCustomButtons.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList()));
                    }
                }
            }
            context.put(Constants.CUSTOM_BUTTONS, evaluatedCustomButtons);
        }
        return false;
    }
    private static boolean skipCustomButtonForDataListCommand()  {
        try{
            long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId();
            Map<String,Object> map = CommonCommandUtil.getOrgInfo(orgId,"skipCustomButtonForDataListCommand");
            if(map!=null){
                Object value = map.getOrDefault("value",false);
                return FacilioUtil.parseBoolean(value);
            }
        }catch (Exception e){
            LOGGER.info("CustomButtonForDataListCommand error:"+e.getMessage());
        }
        return false;

    }
}
