package com.facilio.componentpackage.implementation;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomButtonPackageBeanImpl implements PackageBean<WorkflowRuleContext> {

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return WorkflowRuleAPI.getAllRuleIdVsModuleId(WorkflowRuleContext.RuleType.CUSTOM_BUTTON);
    }

    @Override
    public Map<Long, WorkflowRuleContext> fetchComponents(List<Long> ids) throws Exception {
        return WorkflowRuleAPI.getWorkFlowRules(WorkflowRuleContext.RuleType.CUSTOM_BUTTON, ids);
    }

    @Override
    public void convertToXMLComponent(WorkflowRuleContext customButtonRule, XMLBuilder element) throws Exception {
        ModuleBean modBean = Constants.getModBean();

        if (customButtonRule == null) {
            return;
        }

            if (((CustomButtonRuleContext) customButtonRule).getConfig() != null) {
                JSONObject config=((CustomButtonRuleContext) customButtonRule).getConfig();
                if(config.containsKey(PackageConstants.CustomButtonConstants.NAVIGATE_TO) && !config.containsKey(PackageConstants.CustomButtonConstants.FORM_DATA_JSON)) {
                    return;
                }
                XMLBuilder configXml = element.element(PackageConstants.CustomButtonConstants.CONFIG);

                if(config.containsKey(PackageConstants.CustomButtonConstants.ACTION_TYPE)) {
                    configXml.element(PackageConstants.CustomButtonConstants.ACTION_TYPE).text((String) config.get(PackageConstants.CustomButtonConstants.ACTION_TYPE));
                }
                if(config.containsKey(PackageConstants.CustomButtonConstants.URL)) {
                    configXml.element(PackageConstants.CustomButtonConstants.URL).cData((String) config.get(PackageConstants.CustomButtonConstants.URL));
                }
                if(config.containsKey(PackageConstants.CustomButtonConstants.CONNECTED_APP_ID)) {
                    long connectedAppId= (long) config.get(PackageConstants.CustomButtonConstants.CONNECTED_APP_ID);
                    ConnectedAppContext connectedApp=ConnectedAppAPI.getConnectedApp(connectedAppId);
                    configXml.element(PackageConstants.CustomButtonConstants.CONNECTED_APP_LINK_NAME).text(connectedApp.getLinkName());
                }
                if(config.containsKey(PackageConstants.CustomButtonConstants.WIDGET_ID)) {
                    long widgetId= (long) config.get(PackageConstants.CustomButtonConstants.WIDGET_ID);
                    ConnectedAppWidgetContext widget= ConnectedAppAPI.getConnectedAppWidget(widgetId);
                    configXml.element(PackageConstants.CustomButtonConstants.WIDGET_LINK_NAME).text(widget.getLinkName());
                }
                if(config.containsKey(PackageConstants.CustomButtonConstants.NAVIGATE_TO)) {
                    configXml.element(PackageConstants.CustomButtonConstants.NAVIGATE_TO).text((String) config.get(PackageConstants.CustomButtonConstants.NAVIGATE_TO));
                }
                if(config.containsKey(PackageConstants.MODULENAME)) {
                    configXml.element(PackageConstants.MODULENAME).text((String) config.get(PackageConstants.MODULENAME));
                }
                if(config.containsKey(PackageConstants.CustomButtonConstants.FORM_DATA_JSON)) {
                    configXml.element(PackageConstants.CustomButtonConstants.FORM_DATA_JSON).text((String) config.get(PackageConstants.CustomButtonConstants.FORM_DATA_JSON));
                }

            }

            String moduleName = customButtonRule.getModuleName();
            element.element(PackageConstants.MODULENAME).text(customButtonRule.getModuleName());
            element.element(PackageConstants.NAME).text(customButtonRule.getName());
            element.element(PackageConstants.CustomButtonConstants.BUTTON_TYPE).text(((CustomButtonRuleContext)customButtonRule).getButtonType()>0? String.valueOf(((CustomButtonRuleContext) customButtonRule).getButtonTypeEnum()) :null);
            element.element(PackageConstants.CustomButtonConstants.POSITION_TYPE).text(((CustomButtonRuleContext)customButtonRule).getPositionType()>0?String.valueOf(((CustomButtonRuleContext) customButtonRule).getPositionTypeEnum()):null);
            if(customButtonRule.getDescription() != null) {
                element.element(PackageConstants.DESCRIPTION).text(customButtonRule.getDescription());
            }


            //criteria
            if (customButtonRule.getCriteria() != null) {
                element.addElement(PackageBeanUtil.constructBuilderFromCriteria(customButtonRule.getCriteria(), element.element(PackageConstants.CriteriaConstants.CRITERIA), moduleName));
            }


            if (((CustomButtonRuleContext) customButtonRule).getLookupFieldId() > 0) {
                FacilioField lookupField=Constants.getModBean().getField(((CustomButtonRuleContext) customButtonRule).getLookupFieldId());
                element.element(PackageConstants.LOOKUP_FIELD_NAME).text(lookupField.getName());
               element.element(PackageConstants.LOOKUP_MODULE_NAME).text(lookupField.getModule().getName());
            }
            if (((CustomButtonRuleContext) customButtonRule).getFormId() > 0) {
                FacilioForm form = FormsAPI.getFormFromDB(((CustomButtonRuleContext) customButtonRule).getFormId());
                if (form != null) {
                    element.element(PackageConstants.FormRuleConstants.FORM_NAME).text(form.getName());
                }
            }
            if(((CustomButtonRuleContext) customButtonRule).getFormModuleName()!=null){
                element.element(PackageConstants.CustomButtonConstants.FORM_MODULE_NAME).text(((CustomButtonRuleContext) customButtonRule).getFormModuleName());
            }


            if (((CustomButtonRuleContext) customButtonRule).getApprovers() != null) {
                XMLBuilder approverList = element.element(PackageConstants.APPROVER_LIST);
                XMLBuilder approverElement = approverList.element(PackageConstants.APPROVERS);
                SharingContext<ApproverContext> approvers = ((CustomButtonRuleContext) customButtonRule).getApprovers();
                for (ApproverContext approverContext : approvers) {
                    approverElement.element(PackageConstants.TYPE).text(approverContext.getType()>0? String.valueOf(approverContext.getTypeEnum()) :null);
                    switch (approverContext.getTypeEnum()) {
                        case USER:
                            long userId = approverContext.getUserId();
                            User user = AccountUtil.getUserBean().getUser(userId, true);
                            if (user != null) {
                                approverElement.element(PackageConstants.USER_NAME).text(user.getName());
                            }
                            break;
                        case ROLE:
                            long roleId = approverContext.getRoleId();
                            Role role = AccountUtil.getRoleBean().getRole(roleId);
                            if (role != null) {
                                approverElement.element(PackageConstants.RoleConstants.ROLE_NAME).text(role.getName());
                            }
                            break;
                        case GROUP:
                            long groupId = approverContext.getGroupId();
                            Group group = AccountUtil.getGroupBean().getGroup(groupId);
                            if (group != null) {
                                approverElement.element(PackageConstants.GroupConstants.GROUP_NAME).text(group.getName());
                            }
                            break;
                        case APP:
                            break;
                        default:
                            long fieldId = approverContext.getFieldId();
                            FacilioField field = modBean.getField(fieldId);
                            if (field != null) {
                                approverElement.element(PackageConstants.FIELDNAME).text(field.getName());
                                approverElement.element(PackageConstants.FIELD_MODULE_NAME).text(field.getModule().getName());
                            }
                            break;
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(customButtonRule.getActions())) {
                PackageBeanUtil.constructBuilderFromActionsList(customButtonRule.getActions(), element.element(PackageConstants.WorkFlowRuleConstants.ACTIONS_LIST));
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
        CustomButtonRuleContext customButtonRule = new CustomButtonRuleContext();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder builder = idVsData.getValue();
            customButtonRule = constructCustomButtonRuleFromBuilder(builder, modBean,true);
            if(customButtonRule==null){
                continue;
            }
            Long ruleId = addOrUpdateCustomButtonRule(customButtonRule);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), ruleId);
        }

        return uniqueIdentifierVsComponentId;


    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        CustomButtonRuleContext customButtonRule;

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();
            if (ruleId == null || ruleId < 0) {
                continue;
            }

            XMLBuilder builder = idVsData.getValue();

            customButtonRule = constructCustomButtonRuleFromBuilder(builder, moduleBean,false);
            if(customButtonRule==null)  continue;
            customButtonRule.setId(ruleId);

            addOrUpdateCustomButtonRule(customButtonRule);
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

    private CustomButtonRuleContext constructCustomButtonRuleFromBuilder(XMLBuilder builder, ModuleBean modBean,boolean isCreate) throws Exception
    {
        if(builder.getChildElement(PackageConstants.MODULENAME)==null) return null;
        CustomButtonRuleContext customButtonRule=new CustomButtonRuleContext();

        String moduleName = builder.getChildElement(PackageConstants.MODULENAME).getText();
        customButtonRule.setModuleName(moduleName);
        customButtonRule.setModule(modBean.getModule(moduleName));

        if(builder.getElement(PackageConstants.NAME)!=null) {
            String name = builder.getElement(PackageConstants.NAME).getText();
            customButtonRule.setName(name);
        }

        if(builder.getElement(PackageConstants.CustomButtonConstants.BUTTON_TYPE)!=null) {
            String buttonTypeStr = builder.getElement(PackageConstants.CustomButtonConstants.BUTTON_TYPE).getText();
            CustomButtonRuleContext.ButtonType buttonType= StringUtils.isNotEmpty(buttonTypeStr)? CustomButtonRuleContext.ButtonType.valueOf(buttonTypeStr):null;
            if(buttonType!=null) customButtonRule.setButtonType(buttonType.getIndex());
        }
        if(builder.getElement(PackageConstants.CustomButtonConstants.POSITION_TYPE)!=null) {
            String positionTypeStr = builder.getElement(PackageConstants.CustomButtonConstants.POSITION_TYPE).getText();
            CustomButtonRuleContext.PositionType positionType = StringUtils.isNotEmpty(positionTypeStr) ? CustomButtonRuleContext.PositionType.valueOf(positionTypeStr) : null;
            if (positionType !=null) customButtonRule.setPositionType(positionType.getIndex());
        }

        if(builder.getElement(PackageConstants.DESCRIPTION)!=null) {
            String description = builder.getElement(PackageConstants.DESCRIPTION).text();
            customButtonRule.setDescription(description);
        }

        XMLBuilder criteriaElement = builder.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            customButtonRule.setCriteria(criteria);
        }
        if(builder.getElement(PackageConstants.LOOKUP_FIELD_NAME)!=null) {
            String lookupFieldName = builder.getElement(PackageConstants.LOOKUP_FIELD_NAME).getText();
            String lookupModuleName=builder.getElement(PackageConstants.LOOKUP_MODULE_NAME).getText();
            if (lookupModuleName !=null && lookupFieldName!=null) {
                customButtonRule.setLookupFieldId(modBean.getField(lookupFieldName,lookupModuleName).getFieldId());
            }
        }

        if(builder.getElement(PackageConstants.FORM_NAME)!=null) {
            String formName = builder.getElement(PackageConstants.FORM_NAME).getText();
            String formModuleName;
            if(builder.getElement(PackageConstants.CustomButtonConstants.FORM_MODULE_NAME)!=null){
                formModuleName=builder.getElement(PackageConstants.CustomButtonConstants.FORM_MODULE_NAME).getText();
                customButtonRule.setFormModuleName(formModuleName);
            }
            else{
                formModuleName=moduleName;
            }
            FacilioForm form=FormsAPI.getFormFromDB(formName,modBean.getModule(formModuleName));
            if(form!=null) customButtonRule.setFormId(form.getId());

        }
        if(isCreate) customButtonRule.setShouldFormInterfaceApply(false);
        else customButtonRule.setShouldFormInterfaceApply(true);


        XMLBuilder configXml= builder.getElement(PackageConstants.CustomButtonConstants.CONFIG);
        if(configXml!=null){
            JSONObject json=new JSONObject();

               if(configXml.getElement(PackageConstants.CustomButtonConstants.ACTION_TYPE)!=null)
               {
                   String actionType= configXml.getElement(PackageConstants.CustomButtonConstants.ACTION_TYPE).getText();
                   if(actionType!=null ) json.put(PackageConstants.CustomButtonConstants.ACTION_TYPE,actionType);
               }
                if(configXml.getElement(PackageConstants.CustomButtonConstants.URL)!=null)
                {
                    String url= configXml.getElement(PackageConstants.CustomButtonConstants.URL).getCData();
                    if(url!=null ) json.put(PackageConstants.CustomButtonConstants.URL,url);
                }
                if(configXml.getElement(PackageConstants.CustomButtonConstants.CONNECTED_APP_LINK_NAME)!=null) {
                    String linkName = configXml.getElement(PackageConstants.CustomButtonConstants.CONNECTED_APP_LINK_NAME).getText();
                    if (linkName != null) {
                        // TODO - Remove comments once ConnectedApp is done
//                        ConnectedAppContext connectedApp = ConnectedAppAPI.getConnectedApp(linkName);
//                        json.put(PackageConstants.CustomButtonConstants.CONNECTED_APP_ID, connectedApp.getId());
                    }

                    if (configXml.getElement(PackageConstants.CustomButtonConstants.WIDGET_LINK_NAME) != null) {
                        String widgetLinkName = configXml.getElement(PackageConstants.CustomButtonConstants.WIDGET_LINK_NAME).getText();
                        if (linkName != null) {
                            // TODO - Remove comments once ConnectedApp is done
//                            ConnectedAppWidgetContext widget = ConnectedAppAPI.getConnectedAppWidget(linkName, widgetLinkName);
//                            json.put(PackageConstants.CustomButtonConstants.WIDGET_ID, widget.getId());
                        }
                    }
                }
                    if(configXml.getElement(PackageConstants.CustomButtonConstants.NAVIGATE_TO) != null) {
                        json.put(PackageConstants.CustomButtonConstants.NAVIGATE_TO,configXml.getElement(PackageConstants.CustomButtonConstants.NAVIGATE_TO).getText());
                    }
                    if(configXml.getElement(PackageConstants.MODULENAME) != null) {
                        json.put(PackageConstants.MODULENAME,configXml.getElement(PackageConstants.MODULENAME).getText());
                    }
                    if(configXml.getElement(PackageConstants.CustomButtonConstants.FORM_DATA_JSON) != null) {
                        json.put(PackageConstants.CustomButtonConstants.FORM_DATA_JSON,configXml.getElement(PackageConstants.CustomButtonConstants.FORM_DATA_JSON).getText());
                    }


            customButtonRule.setConfig(json);
        }


        XMLBuilder approverList = builder.getElement(PackageConstants.APPROVER_LIST);
        if (approverList != null){
            List<XMLBuilder> approverElement = approverList.getElementList(PackageConstants.APPROVERS);
            SharingContext<ApproverContext> approverContextList = new SharingContext<>();
            for (XMLBuilder approver : approverElement){
                ApproverContext approverContext = new ApproverContext();

                String approverType = approver.getElement(PackageConstants.TYPE).getText();
                SingleSharingContext.SharingType type=StringUtils.isNotEmpty(approverType) ? SingleSharingContext.SharingType.valueOf(approverType) : null;
                if(type!=null) approverContext.setType(type.getValue());

                switch (approverContext.getTypeEnum()){
                    case USER:
                        if(approver.getElement(PackageConstants.USER_NAME)!=null)
                        {
                            String userName = approver.getElement(PackageConstants.USER_NAME).getText();
                            long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
                            User user = AccountUtil.getUserBean().getAppUserForUserName(userName, appId, AccountUtil.getCurrentOrg().getOrgId());
                            if (user != null) approverContext.setUserId(user.getOuid());
                        }
                        break;
                    case GROUP:
                        if(approver.getElement(PackageConstants.GroupConstants.GROUP_NAME)!=null){
                            String groupName=approver.getElement(PackageConstants.GroupConstants.GROUP_NAME).getText();
                            Group group=AccountUtil.getGroupBean().getGroup(groupName);
                            if(group!=null) approverContext.setGroupId(group.getGroupId());
                        }
                        break;
                    case ROLE:
                        if(approver.getElement(PackageConstants.RoleConstants.ROLE_NAME)!=null){
                            String roleName=approver.getElement(PackageConstants.RoleConstants.ROLE_NAME).getText();
                            Role role=AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),roleName);
                            if(role!=null) approverContext.setRoleId(role.getRoleId());
                        }
                        break;
                    case APP:
                        break;
                    default:
                        if(approver.getElement(PackageConstants.FIELDNAME)!=null){
                            String fieldName=approver.getElement(PackageConstants.FIELDNAME).getText();
                            String fieldModuleName=approver.getElement(PackageConstants.FIELD_MODULE_NAME).getText();
                            FacilioField field=modBean.getField(fieldName,fieldModuleName);
                            if(field!=null){
                                approverContext.setFieldId(field.getFieldId());
                            }
                        }
                        break;

                }
                approverContextList.add(approverContext);
            }
            customButtonRule.setApprovers(approverContextList);
        }


       return customButtonRule;
    }
    public static Long addOrUpdateCustomButtonRule(CustomButtonRuleContext customButtonRule) throws Exception {

        FacilioChain chain = TransactionChainFactory.getAddOrUpdateCustomButtonChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, customButtonRule.getModuleName());
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, customButtonRule);
        chain.execute();

        customButtonRule = (CustomButtonRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        return customButtonRule.getId();
    }

}
