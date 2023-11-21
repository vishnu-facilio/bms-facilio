package com.facilio.componentpackage.implementation;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApprovalRuleMetaContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApprovalStateFlowPackageBeanImpl implements PackageBean<WorkflowRuleContext> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }



    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return WorkflowRuleAPI.getAllRuleIdVsModuleId(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW);
    }

    @Override
    public Map<Long, WorkflowRuleContext> fetchComponents(List<Long> ids) throws Exception {
        return WorkflowRuleAPI.getWorkFlowRules(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW,ids);
    }

    @Override
    public void convertToXMLComponent(WorkflowRuleContext approvals, XMLBuilder element) throws Exception {
        ModuleBean modBean= Constants.getModBean();
        WorkflowRuleContext approveRule=null;WorkflowRuleContext rejectRule=null;WorkflowRuleContext reSendRule=null;
        List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(approvals.getId());
        if(CollectionUtils.isNotEmpty(allStateTransitionList)){
            for(WorkflowRuleContext rule:allStateTransitionList){
                if(rule.getName().equals("Approve")){
                    approveRule=rule;
                }
                else if(rule.getName().equals("Reject")){
                    rejectRule=rule;
                }
                else if(rule.getName().equals("Re-Send")){
                    reSendRule=rule;
                }
            }
        }
        if(approvals.getFields()!=null){
            XMLBuilder fieldList = element.element(PackageConstants.FIELDS_LIST);
            for(FieldChangeFieldContext field:approvals.getFields()){
                FacilioField f=modBean.getField(field.getFieldId());
                fieldList.element(PackageConstants.FIELDNAME).text(f.getName());
            }
        }
        element.element(PackageConstants.NAME).text(approvals.getName());
        element.element(PackageConstants.WorkFlowRuleConstants.STATUS).text(String.valueOf(approvals.isActive()));
        element.element(PackageConstants.DESCRIPTION).text(approvals.getDescription());
        if(approvals.getModuleName()!=null) element.element(PackageConstants.MODULENAME).text(approvals.getModuleName());
        element.element(PackageConstants.Approvals.IS_ALL_APPROVAL_NEEDED).text(String.valueOf(((ApprovalStateTransitionRuleContext) approveRule).isAllApprovalRequired()));
        if (approvals.getCriteria() != null) {
            element.addElement(PackageBeanUtil.constructBuilderFromCriteria(approvals.getCriteria(), element.element(PackageConstants.CriteriaConstants.CRITERIA), approvals.getModuleName()));
        }
        if((approvals).getActivityTypeEnum()!=null) element.element(PackageConstants.Approvals.EVENT_TYPE).text(String.valueOf(approvals.getActivityTypeEnum()));

        if (((ApprovalStateTransitionRuleContext)approveRule).getApprovers() != null) {
            PackageBeanUtil.constructBuilderFromSharingContext(((ApprovalStateTransitionRuleContext)approveRule).getApprovers(),element.element(PackageConstants.WorkFlowRuleConstants.APPROVER_LIST));
        }

        if (CollectionUtils.isNotEmpty(approvals.getActions())) {
            PackageBeanUtil.constructBuilderFromActionsList(approvals.getActions(), element.element(PackageConstants.Approvals.APPROVAL_ENTRY_ACTIONS_LIST));
        }
        if (rejectRule!=null) {
           List<ActionContext> actions=ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentUser().getOrgId(),rejectRule.getId());
           if(CollectionUtils.isNotEmpty(actions)) {
               PackageBeanUtil.constructBuilderFromActionsList(actions, element.element(PackageConstants.Approvals.REJECT_ACTIONS_LIST));
           }
            if(((ApprovalStateTransitionRuleContext)rejectRule).getDialogTypeEnum()!=null) element.element(PackageConstants.Approvals.REJECT_DIALOG_TYPE).text(String.valueOf(((ApprovalStateTransitionRuleContext)rejectRule).getDialogTypeEnum()));
            if(((ApprovalStateTransitionRuleContext)rejectRule).getForm()!=null) {
                FacilioForm form = ((ApprovalStateTransitionRuleContext)rejectRule).getForm();
                element.element(PackageConstants.Approvals.REJECT_FORM).text(form.getName());
            }
        }
        if (reSendRule!=null) {
            List<ActionContext> actions=ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentUser().getOrgId(),rejectRule.getId());
            if(CollectionUtils.isNotEmpty(actions)) {
                PackageBeanUtil.constructBuilderFromActionsList(actions, element.element(PackageConstants.Approvals.RESEND_ACTIONS_LIST));
            }

            if (((ApprovalStateTransitionRuleContext)reSendRule).getApprovers() != null) {
                PackageBeanUtil.constructBuilderFromSharingContext(((ApprovalStateTransitionRuleContext)reSendRule).getApprovers(),element.element((PackageConstants.RESEND_APPROVER_LIST)));
            }

            if(((ApprovalStateTransitionRuleContext)reSendRule).getForm()!=null) {
                FacilioForm form = ((ApprovalStateTransitionRuleContext)reSendRule).getForm();
                element.element(PackageConstants.Approvals.RESEND_FORM).text(form.getName());
            }
        }
        if (approveRule!=null) {
            List<ActionContext> actions=ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentUser().getOrgId(),rejectRule.getId());
            if(CollectionUtils.isNotEmpty(actions)) {
                PackageBeanUtil.constructBuilderFromActionsList(actions, element.element(PackageConstants.Approvals.APPROVAL_ACTIONS));
            }
            if(((ApprovalStateTransitionRuleContext)approveRule).getDialogTypeEnum()!=null) element.element(PackageConstants.Approvals.APPROVAL_DIALOG_TYPE).text(String.valueOf(((ApprovalStateTransitionRuleContext)approveRule).getDialogTypeEnum()));
            if(((ApprovalStateTransitionRuleContext)approveRule).getForm()!=null) {
                FacilioForm form = ((ApprovalStateTransitionRuleContext)approveRule).getForm();
                element.element(PackageConstants.Approvals.APPROVAL_FORM).text(form.getName());
            }
        }

        if(((ApprovalStateFlowRuleContext)approvals).getConfigJson()!=null){
            String jsonStr=((ApprovalStateFlowRuleContext)approvals).getConfigJson();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject)parser.parse(jsonStr);
            XMLBuilder configXml = element.element(PackageConstants.Approvals.CONFIG);
            if(jsonObject!=null ){
                if(jsonObject.containsKey("fields")){
                    JSONArray array= (JSONArray) jsonObject.get("fields");
                    if(array.size()>0) {
                        for(int i=0;i< array.size();i++) {
                            configXml.element(PackageConstants.FIELDNAME).text(modBean.getField((Long) array.get(i)).getName());
                        }
                    }
                }
                if(jsonObject.containsKey("relatedModules") ){
                    JSONArray array= (JSONArray) jsonObject.get("relatedModules");
                    if(array.size()>0) {
                        List<Long> ids = (List<Long>) jsonObject.get("relatedModules");
                        for(int i=0;i< array.size();i++) {
                            configXml.element(PackageConstants.MODULENAME).text(modBean.getModule((Long) array.get(i)).getName());
                        }
                    }
                }
            }
        }
    }


    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        ModuleBean modBean = Constants.getModBean();
        ApprovalRuleMetaContext approvalMeta=new ApprovalRuleMetaContext();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder builder = idVsData.getValue();
            String moduleName= builder.getElement(PackageConstants.MODULENAME).getText();
            approvalMeta = constructApprovalRuleFromBuilder(builder, modBean,true);
            Long ruleId = addOrUpdateApprovalRule(approvalMeta,moduleName);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), ruleId);
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        ApprovalRuleMetaContext rule;
        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();
            XMLBuilder builder = idVsData.getValue();
            String moduleName= builder.getElement(PackageConstants.MODULENAME).getText();
            rule = constructApprovalRuleFromBuilder(builder, moduleBean,false);
            rule.setId(ruleId);
            addOrUpdateApprovalRule(rule,moduleName);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            WorkflowRuleAPI.deleteWorkFlowRules(ids);
        }
    }

    private ApprovalRuleMetaContext constructApprovalRuleFromBuilder(XMLBuilder builder, ModuleBean modBean,boolean isCreate) throws Exception {

    ApprovalRuleMetaContext approvalMeta=new ApprovalRuleMetaContext();

    String name=builder.getElement(PackageConstants.NAME).getText();
    String descprition=builder.getElement(PackageConstants.DESCRIPTION).getText();
    String moduleName= builder.getElement(PackageConstants.MODULENAME).getText();
    approvalMeta.setName(name);
    approvalMeta.setDescription(descprition);
    boolean status = Boolean.parseBoolean(builder.getElement(PackageConstants.WorkFlowRuleConstants.STATUS).getText());
    approvalMeta.setStatus(status);

    if(builder.getElement(PackageConstants.Approvals.IS_ALL_APPROVAL_NEEDED)!=null) approvalMeta.setAllApprovalRequired(Boolean.valueOf(builder.getElement(PackageConstants.Approvals.IS_ALL_APPROVAL_NEEDED).getText()));

        XMLBuilder criteriaElement = builder.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            approvalMeta.setCriteria(criteria);
        }
    if(builder.getElement(PackageConstants.Approvals.EVENT_TYPE)!=null) {
        String type= builder.getElement(PackageConstants.Approvals.EVENT_TYPE).getText();
        EventType eventType = StringUtils.isNotEmpty(type) ? EventType.valueOf(type) : null;
        if (eventType !=null) approvalMeta.setEventType(eventType);
    }
        XMLBuilder approverList = builder.getElement(PackageConstants.WorkFlowRuleConstants.APPROVER_LIST);
        if (approverList != null){
            SharingContext<ApproverContext> approverContexts = PackageBeanUtil.constructSharingContextFromBuilder(approverList, ApproverContext.class);
            approvalMeta.setApprovers(approverContexts);
        }

        XMLBuilder entryActionsList = builder.getElement(PackageConstants.Approvals.APPROVAL_ENTRY_ACTIONS_LIST);
        if (entryActionsList != null) {
            List<ActionContext> actionContextList = PackageBeanUtil.constructActionContextsFromBuilder(entryActionsList);
            approvalMeta.setApprovalEntryActions(actionContextList);
        }
        XMLBuilder rejectActionsList = builder.getElement(PackageConstants.Approvals.REJECT_ACTIONS_LIST);
        if (rejectActionsList != null) {
            List<ActionContext> actionContextList = PackageBeanUtil.constructActionContextsFromBuilder(rejectActionsList);
            approvalMeta.setRejectActions(actionContextList);
        }
        if(builder.element(PackageConstants.Approvals.REJECT_DIALOG_TYPE)!=null) {
            String type= builder.getElement(PackageConstants.Approvals.REJECT_DIALOG_TYPE).getText();
            AbstractStateTransitionRuleContext.DialogType dialogType = StringUtils.isNotEmpty(type) ? AbstractStateTransitionRuleContext.DialogType.valueOf(type) : null;
            if (dialogType !=null) approvalMeta.setRejectDialogType(dialogType);
        }
        XMLBuilder resendActionsList = builder.getElement(PackageConstants.Approvals.RESEND_ACTIONS_LIST);
        if (resendActionsList != null) {
            List<ActionContext> actionContextList = PackageBeanUtil.constructActionContextsFromBuilder(resendActionsList);
            approvalMeta.setResendActions(actionContextList);
        }

        XMLBuilder resendApproverList = builder.getElement(PackageConstants.RESEND_APPROVER_LIST);
        if (resendApproverList != null){
            SharingContext<ApproverContext> approverContexts = PackageBeanUtil.constructSharingContextFromBuilder(resendApproverList, ApproverContext.class);
            approvalMeta.setResendApprovers(approverContexts);
        }

        if(builder.getElement(PackageConstants.Approvals.APPROVAL_FORM)!=null){
            String formName=builder.getElement(PackageConstants.Approvals.APPROVAL_FORM).getText();
            FacilioForm form =FormsAPI.getFormFromDB(formName,modBean.getModule(moduleName));
           if(form!=null) approvalMeta.setApprovalFormId(form.getId());
        }
        if(builder.getElement(PackageConstants.Approvals.REJECT_FORM)!=null){
            String formName=builder.getElement(PackageConstants.Approvals.REJECT_FORM).getText();
            FacilioForm form =FormsAPI.getFormFromDB(formName,modBean.getModule(moduleName));
            if(form!=null) approvalMeta.setRejectFormId(form.getId());
        }
        if(builder.getElement(PackageConstants.Approvals.RESEND_FORM)!=null){
            String formName=builder.getElement(PackageConstants.Approvals.RESEND_FORM).getText();
            FacilioForm form =FormsAPI.getFormFromDB(formName,modBean.getModule(moduleName));
            if(form!=null) approvalMeta.setResendFormId(form.getId());
        }
        XMLBuilder approverActionsList = builder.getElement(PackageConstants.Approvals.APPROVAL_ACTIONS);
        if (approverActionsList != null) {
            List<ActionContext> actionContextList = PackageBeanUtil.constructActionContextsFromBuilder(approverActionsList);
            approvalMeta.setApproveActions(actionContextList);
        }
        if(builder.element(PackageConstants.Approvals.APPROVAL_DIALOG_TYPE)!=null) {
            String type= builder.getElement(PackageConstants.Approvals.APPROVAL_DIALOG_TYPE).getText();
            AbstractStateTransitionRuleContext.DialogType dialogType = StringUtils.isNotEmpty(type) ? AbstractStateTransitionRuleContext.DialogType.valueOf(type) : null;
            if (dialogType !=null) approvalMeta.setApprovalDialogType(dialogType);
        }

        XMLBuilder fieldsList = builder.getElement(PackageConstants.FIELDS_LIST);
        if (fieldsList != null) {
            List<XMLBuilder> fieldElement = fieldsList.getElementList(PackageConstants.FIELDNAME);
            List<Long> fieldIds = new ArrayList<>();
            for (XMLBuilder field : fieldElement) {
                String fieldName=field.getText();
                fieldIds.add(modBean.getField(fieldName,moduleName).getFieldId());
            }
            approvalMeta.setFieldIds(fieldIds);
        }

        if(isCreate)approvalMeta.setShouldFormInterfaceApply(false);
        else approvalMeta.setShouldFormInterfaceApply(true);

        XMLBuilder configXml = builder.getElement(PackageConstants.Approvals.CONFIG);
        if(configXml!=null) {
            JSONObject json =new JSONObject();
            List<XMLBuilder> fieldElement = configXml.getElementList(PackageConstants.FIELDNAME);
            List<XMLBuilder> relatedModuleElement = configXml.getElementList(PackageConstants.MODULENAME);
            JSONArray fieldIds = new JSONArray();
            JSONArray moduleIds = new JSONArray();
                for (XMLBuilder field : fieldElement) {
                    String fieldName = field.getText();
                    fieldIds.add(modBean.getField(fieldName, moduleName).getFieldId());
                }
            for (XMLBuilder module : relatedModuleElement) {
                String relatedModuleName=module.getText();
                moduleIds.add(modBean.getModule(relatedModuleName).getModuleId());
            }
            json.put("fields",fieldIds);
            json.put("relatedModules",moduleIds);
            approvalMeta.setConfigJson(json.toJSONString());
        }
        return approvalMeta;
    }
    public static Long addOrUpdateApprovalRule(ApprovalRuleMetaContext rule,String moduleName) throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateApprovalRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.APPROVAL_RULE, rule);
        chain.execute();
        rule = (ApprovalRuleMetaContext) context.get(FacilioConstants.ContextNames.APPROVAL_RULE);
        return rule.getId();
    }

}
