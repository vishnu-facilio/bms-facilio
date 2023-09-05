package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateControlActionCommandsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
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
        List<V3CommandsContext> commandsContextList = new ArrayList<>();
        for(V3AssetContext assetContext : assetContextList) {
            List<V3ActionContext> actionContextList = v3ControlActionContext.getActionContextList();
            for (V3ActionContext v3ActionContext : actionContextList) {
                commandsContextList.add(createCommand(v3ControlActionContext, v3ActionContext, v3ControlActionContext.getScheduledActionDateTime(), assetContext, V3CommandsContext.CommandActionType.SCHEDULED_ACTION));
                if(v3ActionContext.getRevertActionValue() != null && v3ControlActionContext.getRevertActionDateTime() != null){
                    commandsContextList.add(createCommand(v3ControlActionContext, v3ActionContext, v3ControlActionContext.getRevertActionDateTime(), assetContext, V3CommandsContext.CommandActionType.REVERT_ACTION));
                }
            }
        }
        if(CollectionUtils.isNotEmpty(commandsContextList)){
            ControlActionAPI.createCommand(commandsContextList);
        }
        context.put(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,v3ControlActionContext);
        return false;
    }
    public static V3CommandsContext createCommand(V3ControlActionContext controlActionContext, V3ActionContext actionContext, Long actionTime, V3AssetContext assetContext, V3CommandsContext.CommandActionType commandActionType) throws Exception{
        V3CommandsContext commandsContext = new V3CommandsContext();
        commandsContext.setAction(actionContext);
        commandsContext.setControlAction(controlActionContext);
        commandsContext.setSite(V3SpaceAPI.getSiteSpace(assetContext.getSiteId()));
        commandsContext.setAsset(assetContext);
        commandsContext.setActionTime(actionTime);
        commandsContext.setFieldId(actionContext.getReadingFieldId());
        commandsContext.setControlActionCommandStatus(V3CommandsContext.ControlActionCommandStatus.NOT_SCHEDULED.getVal());
        commandsContext.setCommandActionType(commandActionType.getVal());
        return commandsContext;
    }
}
