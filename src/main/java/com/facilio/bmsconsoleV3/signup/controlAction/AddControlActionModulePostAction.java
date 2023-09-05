package com.facilio.bmsconsoleV3.signup.controlAction;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ApprovalRuleMetaContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApproverContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddControlActionModulePostAction extends BaseModuleConfig {
    public AddControlActionModulePostAction(){
        setModuleName(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
    }
    @Override
    public void addForms(List<ApplicationContext> allApplications) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule controlActionModule = moduleBean.getModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        addApprovalForControlActionModule(controlActionModule, moduleBean);
    }
    public static void addApprovalForControlActionModule(FacilioModule controlActionModule, ModuleBean moduleBean) throws Exception{
        ApprovalRuleMetaContext approvalRuleMetaContext = new ApprovalRuleMetaContext();
        approvalRuleMetaContext.setAllApprovalRequired(true);
        approvalRuleMetaContext.setApprovalDialogType(AbstractStateTransitionRuleContext.DialogType.MODULE);
        approvalRuleMetaContext.setApprovalOrder(ApprovalRuleContext.ApprovalOrder.SEQUENTIAL);

        SharingContext<ApproverContext> approverContextList = new SharingContext<>();
        ApproverContext firstLevelApprover = new ApproverContext();
        firstLevelApprover.setFieldId(moduleBean.getField("firstLevelApproval",FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME).getFieldId());
        firstLevelApprover.setType(SingleSharingContext.SharingType.FIELD);
        approverContextList.add(firstLevelApprover);
        ApproverContext secondLevelApprover = new ApproverContext();
        secondLevelApprover.setFieldId(moduleBean.getField("secondLevelApproval",FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME).getFieldId());
        secondLevelApprover.setType(SingleSharingContext.SharingType.FIELD);
        approverContextList.add(secondLevelApprover);
        approvalRuleMetaContext.setApprovers(approverContextList);

        approvalRuleMetaContext.setEventType(EventType.CREATE_OR_EDIT);
        approvalRuleMetaContext.setName("Control Action Default Approval");
        approvalRuleMetaContext.setRejectDialogType(AbstractStateTransitionRuleContext.DialogType.MODULE);
        approvalRuleMetaContext.setShouldFormInterfaceApply(false);

        FacilioChain chain = TransactionChainFactory.getAddOrUpdateApprovalRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APPROVAL_RULE,approvalRuleMetaContext);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        chain.setContext(context);
        chain.execute();

    }
}
