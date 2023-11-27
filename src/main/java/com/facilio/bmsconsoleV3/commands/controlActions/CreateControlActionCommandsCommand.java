package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CalendarActivityType;
import com.facilio.bmsconsole.activity.ControlActionActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsoleV3.actions.V3ControlActionAction;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Log4j
public class CreateControlActionCommandsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("CreateControlActionCommandsCommand Class Entry ---->");
        Long controlActionId = (Long) context.get(FacilioConstants.Control_Action.CONTROL_ACTION_ID);
        if(controlActionId == null || controlActionId < 0){
            return false;
        }
        V3ControlActionContext v3ControlActionContext = ControlActionAPI.getControlAction(controlActionId);
        if(CollectionUtils.isEmpty(v3ControlActionContext.getActionContextList())){
            return false;
        }
        List<V3AssetContext> assetContextList = ControlActionAPI.getFilteredAssets(v3ControlActionContext.getSiteCriteria(),v3ControlActionContext.getAssetCriteria(),v3ControlActionContext.getControllerCriteria(),v3ControlActionContext.getAssetCategory());
        if(CollectionUtils.isEmpty(assetContextList)){
            return false;
        }
        boolean check = true;
        List<PeopleContext> firstLevelApproverList = ControlActionAPI.getApprovalList(v3ControlActionContext.getId(),FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME);
        V3CommandsContext.ControlActionCommandStatus commandStatus = V3CommandsContext.ControlActionCommandStatus.SCHEDULED;
        if(CollectionUtils.isNotEmpty(firstLevelApproverList)) {
            LOGGER.info("First Level Approver List is not Empty --->");
            if (v3ControlActionContext.getControlActionStatus() == V3ControlActionContext.ControlActionStatus.PUBLISHED.getVal()){
                commandStatus = V3CommandsContext.ControlActionCommandStatus.NOT_SCHEDULED;
                LOGGER.info("Command Status will be Not Scheduled");
                check = false;
            }
        }

        List<V3CommandsContext> commandsContextList = new ArrayList<>();
        for(V3AssetContext assetContext : assetContextList) {
            List<V3ActionContext> actionContextList = v3ControlActionContext.getActionContextList();
            for (V3ActionContext v3ActionContext : actionContextList) {
                commandsContextList.add(createCommand(v3ControlActionContext, v3ActionContext, v3ControlActionContext.getScheduledActionDateTime(), assetContext, V3CommandsContext.CommandActionType.SCHEDULED_ACTION,commandStatus));
                if(v3ActionContext.getRevertActionValue() != null && v3ControlActionContext.getRevertActionDateTime() != null){
                    commandsContextList.add(createCommand(v3ControlActionContext, v3ActionContext, v3ControlActionContext.getRevertActionDateTime(), assetContext, V3CommandsContext.CommandActionType.REVERT_ACTION,commandStatus));
                }
            }
        }
        if(CollectionUtils.isNotEmpty(commandsContextList)){
            ControlActionAPI.createCommand(commandsContextList);
            if(check) {
                ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.COMMAND_GENERATED.getValue(), v3ControlActionContext.getId());
                v3ControlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SCHEDULED.getVal());
                v3ControlActionContext.setScheduleActionStatus(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SCHEDULED.getVal());
                ControlActionAPI.updateControlAction(v3ControlActionContext);
                ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.SCHEDULE_ACTION_SCHEDULED.getValue(), v3ControlActionContext.getId());
            }
            else{
                LOGGER.info("Approval Pending - Control Action status will be Published And Command Status Will be Not Scheduled");
            }
        }
        context.put(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,v3ControlActionContext);
        return false;
    }
    public static V3CommandsContext createCommand(V3ControlActionContext controlActionContext, V3ActionContext actionContext, Long actionTime, V3AssetContext assetContext, V3CommandsContext.CommandActionType commandActionType,V3CommandsContext.ControlActionCommandStatus commandStatus) throws Exception{
        V3CommandsContext commandsContext = new V3CommandsContext();
        commandsContext.setAction(actionContext);
        commandsContext.setName(constructCommandSubject(actionContext,commandActionType));
        commandsContext.setControlAction(controlActionContext);
        commandsContext.setSite(V3SpaceAPI.getSiteSpace(assetContext.getSiteId()));
        commandsContext.setAsset(assetContext);
        commandsContext.setActionTime(actionTime);
        commandsContext.setFieldId(actionContext.getReadingFieldId());
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField field = moduleBean.getField(actionContext.getReadingFieldId());
        ReadingDataMeta readingDataMeta =  ReadingsAPI.getReadingDataMeta(assetContext.getId(), field);
        if(readingDataMeta.getInputType() != ReadingDataMeta.ReadingInputType.CONTROLLER_MAPPED.getValue() ){
            commandsContext.setControlActionCommandStatus(V3CommandsContext.ControlActionCommandStatus.POINT_NOT_COMMISSIONED.getVal());
        }
        else{
            commandsContext.setControlActionCommandStatus(commandStatus.getVal());
        }
        commandsContext.setCommandActionType(commandActionType.getVal());
        return commandsContext;
    }
    public static String constructCommandSubject(V3ActionContext actionContext,V3CommandsContext.CommandActionType commandActionType){
        String subject = "";
        if(commandActionType == V3CommandsContext.CommandActionType.SCHEDULED_ACTION){
            if(actionContext.getScheduledActionOperatorTypeEnum() == V3ActionContext.ActionOperatorTypeEnum.FIXED){
                subject += "Set Value To ";
            }
            else if(actionContext.getScheduledActionOperatorTypeEnum() == V3ActionContext.ActionOperatorTypeEnum.INCREASED_BY){
                subject += "Increase By ";
            }
            else if(actionContext.getScheduledActionOperatorTypeEnum() == V3ActionContext.ActionOperatorTypeEnum.DECREASED_BY){
                subject += "Decrease By ";
            }
            subject += String.valueOf(actionContext.getScheduleActionValue());
        }
        else if(commandActionType == V3CommandsContext.CommandActionType.REVERT_ACTION){
            if(actionContext.getRevertActionOperatorTypeEnum() == V3ActionContext.ActionOperatorTypeEnum.FIXED){
                subject += "Set Value To ";
            }
            else if(actionContext.getRevertActionOperatorTypeEnum() == V3ActionContext.ActionOperatorTypeEnum.INCREASED_BY){
                subject += "Increase By ";
            }
            else if(actionContext.getRevertActionOperatorTypeEnum() == V3ActionContext.ActionOperatorTypeEnum.DECREASED_BY){
                subject += "Decrease By ";
            }
            subject += String.valueOf(actionContext.getRevertActionValue());
        }
        return subject;
    }
}
