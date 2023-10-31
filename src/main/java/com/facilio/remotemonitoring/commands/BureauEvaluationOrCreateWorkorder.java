package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.io.InputStream;
import java.util.*;

public class BureauEvaluationOrCreateWorkorder extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventContext> flaggedEvents = (List<FlaggedEventContext>) recordMap.get(FlaggedEventModule.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        if (CollectionUtils.isNotEmpty(flaggedEvents)) {
            for(FlaggedEventContext flaggedEvent : flaggedEvents) {
                boolean createWorkorder = false;
                if(flaggedEvent.getFlaggedAlarmProcess() != null) {
                    FlaggedEventRuleContext flaggedEventRule = alarmBean.getFlaggedEventRule(flaggedEvent.getFlaggedAlarmProcess().getId());
                    autoCloseFlaggedEventJob(flaggedEventRule,flaggedEvent);
                    cloneRuleAttachments(flaggedEventRule,flaggedEvent);
                    if(flaggedEventRule.shouldCreateWorkorder()) {
                        createWorkorder = true;
                    }
                    if (flaggedEventRule != null) {
                        List<FlaggedEventBureauActionsContext> bureauActions = (List<FlaggedEventBureauActionsContext>) context.get(RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_ACTIONS);
                        if (CollectionUtils.isNotEmpty(bureauActions)) {
                            FlaggedEventBureauActionsContext action = bureauActions.get(0);
                            if(action != null) {
                                Map<String,Object> prop = new HashMap<>();
                                prop.put("status",FlaggedEventContext.FlaggedEventStatus.OPEN.getIndex());
                                prop.put("currentBureauActionDetail",ImmutableMap.of("id",action.getId()));
                                prop.put("team",ImmutableMap.of("id",action.getTeam().getId()));
                                V3Util.updateBulkRecords(FlaggedEventModule.MODULE_NAME, prop,Collections.singletonList(flaggedEvent.getId()),false);

                                FlaggedEventUtil.updateBureauActionStatus(action.getId(), FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.OPEN);
                                if(createWorkorder) {
                                    Long nextExecutionTime = (System.currentTimeMillis() + action.getTakeCustodyPeriod()) / 1000;
                                    FacilioTimer.scheduleOneTimeJobWithTimestampInSec(action.getId(), RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_TAKE_CUSTODY_JOB, nextExecutionTime, "priority");
                                }
                                createWorkorder = false;
                            }
                        }
                    }
                }
                if(createWorkorder) {
                    FlaggedEventUtil.checkAndCreateWorkorderForFlaggedEvent(flaggedEvent.getId());
                }
            }
        }
        return false;
    }
    private static void autoCloseFlaggedEventJob(FlaggedEventRuleContext flaggedEventRule,FlaggedEventContext flaggedEvent) throws Exception {
        if(flaggedEvent != null && flaggedEventRule != null && flaggedEventRule.getFlaggedEventRuleClosureConfig() != null && flaggedEventRule.getFlaggedEventRuleClosureConfig().getAutoClosureDuration() != null && flaggedEventRule.getFlaggedEventRuleClosureConfig().getAutoClosureDuration() > 0) {
            Long nextExecutionTime = (System.currentTimeMillis() + flaggedEventRule.getFlaggedEventRuleClosureConfig().getAutoClosureDuration()) / 1000;
            FacilioTimer.scheduleOneTimeJobWithTimestampInSec(flaggedEvent.getId(), RemoteMonitorConstants.FLAGGED_EVENT_AUTO_CLOSURE_SCHEDULED_JOB, nextExecutionTime, "priority");
        }
    }
    private static void cloneRuleAttachments(FlaggedEventRuleContext rule,FlaggedEventContext event) throws Exception {
        if(rule != null) {
            List<AttachmentContext> ruleAttachments = AttachmentsAPI.getAttachments(RemoteMonitorConstants.FLAGGED_EVENT_RULE_ATTACHMENT_MOD_NAME, rule.getId());
            if(CollectionUtils.isNotEmpty(ruleAttachments)) {
                List<AttachmentContext> eventAttachmentList = new ArrayList<>();
                for(AttachmentContext attachment : ruleAttachments) {
                    Long fileId = cloneFile(attachment.getFileId());
                    AttachmentContext eventAttachment = new AttachmentContext();
                    ModuleBaseWithCustomFields parent = new ModuleBaseWithCustomFields();
                    parent.setId(event.getId());
                    eventAttachment.setParent(parent);
                    eventAttachment.setFileId(fileId);
                    eventAttachment.setCreatedTime(System.currentTimeMillis());
                    eventAttachmentList.add(eventAttachment);
                }
                AttachmentsAPI.addAttachments(eventAttachmentList,RemoteMonitorConstants.FLAGGED_EVENT_ATTACHMENT_MOD_NAME);
            }
        }
    }

    private static Long cloneFile(Long fileId) throws Exception {
        if(fileId != null) {
            FileStore fileStore = FacilioFactory.getFileStore();
            if (fileStore != null) {
                InputStream fileStream = fileStore.readFile(fileId);
                FileInfo info = fileStore.getFileInfo(fileId);
                if (info != null && fileStream != null) {
                    return FacilioFactory.getFileStore().addFileFromStream(info.getFileName(), info.getContentType(), fileStream);
                }
            }
        }
        return null;
    }
}