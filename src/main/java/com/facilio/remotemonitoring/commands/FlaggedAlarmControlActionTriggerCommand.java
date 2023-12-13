package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleClosureConfigContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlaggedAlarmControlActionTriggerCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventContext> flaggedEvents = (List<FlaggedEventContext>) recordMap.get(FlaggedEventModule.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        if (CollectionUtils.isNotEmpty(flaggedEvents)) {
            for (FlaggedEventContext flaggedEvent : flaggedEvents) {
                if(flaggedEvent.getAsset() != null && flaggedEvent.getAsset().getId() > 0) {
                    V3AssetContext asset = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET,flaggedEvent.getAsset().getId(), V3AssetContext.class);
                    FlaggedEventRuleContext flaggedEventRule = alarmBean.getFlaggedEventRule(flaggedEvent.getFlaggedAlarmProcess().getId());
                    if (asset != null && flaggedEventRule != null) {
                        List<V3ControlActionTemplateContext> controlActionsTemplates = flaggedEventRule.getControlActionTemplate();
                        if (CollectionUtils.isNotEmpty(controlActionsTemplates)) {
                            List<Long> controlActionTemplateIds = controlActionsTemplates.stream().map(V3ControlActionTemplateContext::getId).collect(Collectors.toList());
                            controlActionsTemplates = V3RecordAPI.getRecordsList(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME,controlActionTemplateIds,V3ControlActionTemplateContext.class);
                            for (V3ControlActionTemplateContext template : controlActionsTemplates) {
                                if(
                                        template.getAssetCategory() != null
                                        && template.getAssetCategory().getId() > 0
                                        && asset.getCategory() != null
                                        && asset.getCategory().getId() > 0
                                        && asset.getCategory().getId() == template.getAssetCategory().getId()
                                ) {
                                    long orgId = AccountUtil.getCurrentOrg().getOrgId();
                                    JSONObject message = new JSONObject();
                                    message.put("orgId", orgId);
                                    message.put(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_ID, template.getId());
                                    message.put("startTime", 0l);
                                    message.put("endTime", 0l);
                                    message.put("assetId", flaggedEvent.getAsset().getId());
                                    message.put("flaggedEventId", flaggedEvent.getId());
                                    Messenger.getMessenger().sendMessage(new Message()
                                            .setKey("controlActionTemplate/" + template.getId())
                                            .setOrgId(orgId)
                                            .setContent(message)
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
