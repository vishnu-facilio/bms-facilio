package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsoleV3.util.V3AttachmentAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteAndAddRuleAttachmentsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(FlaggedEventRuleModule.MODULE_NAME);
        if(CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for(FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                deleteRuleAttachments(flaggedEventRule.getId());
                List<Long> fileIds = flaggedEventRule.getFileIds();
                List<AttachmentContext> attachmentContextList = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(fileIds)) {
                    for(Long fileId : fileIds) {
                        AttachmentContext attachmentContext = new AttachmentContext();
                        ModuleBaseWithCustomFields parentRec = new ModuleBaseWithCustomFields();
                        parentRec.setId(flaggedEventRule.getId());
                        attachmentContext.setParent(parentRec);
                        attachmentContext.setCreatedTime(System.currentTimeMillis());
                        attachmentContext.setFileId(fileId);
                        attachmentContextList.add(attachmentContext);
                    }
                    AttachmentsAPI.addAttachments(attachmentContextList,RemoteMonitorConstants.FLAGGED_EVENT_RULE_ATTACHMENT_MOD_NAME);
                }
            }
        }
        return false;
    }

    private static void deleteRuleAttachments(Long ruleId) throws Exception {
        if(ruleId != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ID","parentId",String.valueOf(ruleId), NumberOperators.EQUALS));
            DeleteRecordBuilder<AttachmentV3Context> deleteRecordBuilder = new DeleteRecordBuilder<AttachmentV3Context>()
                    .module(modBean.getModule(RemoteMonitorConstants.FLAGGED_EVENT_RULE_ATTACHMENT_MOD_NAME))
                    .andCriteria(criteria);
            deleteRecordBuilder.delete();
        }
    }
}