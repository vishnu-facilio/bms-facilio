package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiResourceModule extends BaseModuleConfig{
    public MultiResourceModule(){
        setModuleName(FacilioConstants.MultiResource.NAME);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule multiResourceModule = constructMultiResourceModule();
        modules.add(multiResourceModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        addForm();
        addSummaryWidget(multiResourceModule);


    }

    public FacilioModule constructMultiResourceModule() throws Exception{

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule(FacilioConstants.MultiResource.NAME,
                FacilioConstants.MultiResource.DISPLAY_NAME,
                FacilioConstants.MultiResource.TABLE_NAME,
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER, FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        fields.add(siteField);

        LookupField asset = FieldFactory.getDefaultField("asset","Asset","ASSET_ID",FieldType.LOOKUP);
        asset.setLookupModule(bean.getModule(FacilioConstants.ContextNames.ASSET));

        fields.add(asset);

        LookupField space = FieldFactory.getDefaultField("space","Space","SPACE_ID",FieldType.LOOKUP);
        space.setLookupModule(bean.getModule(FacilioConstants.ContextNames.BASE_SPACE));

        fields.add(space);

        NumberField parentModuleIdField = (NumberField) FieldFactory.getDefaultField("parentModuleId", "Parent_Module_Id", "PARENT_MODULE_ID", FieldType.NUMBER,true);
        fields.add(parentModuleIdField);

        NumberField parentRecordIdField = (NumberField) FieldFactory.getDefaultField("parentRecordId", "Parent_Record_Id", "PARENT_RECORD_ID", FieldType.NUMBER);
        fields.add(parentRecordIdField);

        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.BIG_STRING, FacilioField.FieldDisplayType.TEXTAREA));

        fields.add(FieldFactory.getDefaultField("sequence", "Sequence", "SEQUENCE", FieldType.NUMBER));

        module.setFields(fields);
        return module;
    }

    public void addSummaryWidget(FacilioModule multiResourceModule) throws Exception {
        ArrayList<String> apps = new ArrayList<>();
        if (!SignupUtil.maintenanceAppSignup()) {
            apps.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        apps.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        apps.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);

        for (String app : apps) {
            ModuleBean moduleBean = Constants.getModBean();

            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.MultiResource.NAME);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.MultiResource.NAME);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.MultiResource.NAME);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.MultiResource.NAME);


            SummaryWidget widget = new SummaryWidget();

            SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
            SummaryWidgetGroupFields groupField1 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField2 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField3 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField4 = new SummaryWidgetGroupFields();


            groupField1.setName(sysCreatedByField.getName());
            groupField1.setDisplayName(sysCreatedByField.getDisplayName());
            groupField1.setFieldId(sysCreatedByField.getId());
            groupField1.setRowIndex(1);
            groupField1.setColIndex(1);
            groupField1.setColSpan(2);

            groupField2.setName(sysCreatedTimeField.getName());
            groupField2.setDisplayName(sysCreatedTimeField.getDisplayName());
            groupField2.setFieldId(sysCreatedTimeField.getId());
            groupField2.setRowIndex(1);
            groupField2.setColIndex(3);
            groupField2.setColSpan(2);

            groupField3.setName(sysModifiedByField.getName());
            groupField3.setDisplayName(sysModifiedByField.getDisplayName());
            groupField3.setFieldId(sysModifiedByField.getId());
            groupField3.setRowIndex(2);
            groupField3.setColIndex(1);
            groupField3.setColSpan(2);

            groupField4.setName(sysModifiedTimeField.getName());
            groupField4.setDisplayName(sysModifiedTimeField.getDisplayName());
            groupField4.setFieldId(sysModifiedTimeField.getId());
            groupField4.setRowIndex(2);
            groupField4.setColIndex(3);
            groupField4.setColSpan(2);


            List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
            groupOneFields.add(groupField1);
            groupOneFields.add(groupField2);
            groupOneFields.add(groupField3);
            groupOneFields.add(groupField4);


            widgetGroup.setName("moreDetails");
            widgetGroup.setDisplayName("More Details");
            widgetGroup.setColumns(4);
            widgetGroup.setFields(groupOneFields);

            List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
            widgetGroupList.add(widgetGroup);


            widget.setName("multiResourceWidget");
            widget.setDisplayName("MultiResource Widget");
            widget.setModuleId(multiResourceModule.getModuleId());
            widget.setAppId(ApplicationApi.getApplicationIdForLinkName(app));
            widget.setGroups(widgetGroupList);


            SummaryWidgetUtil.addPageWidget(widget);
        }
    }


    public void addForm() throws Exception {
        ArrayList<String> apps = new ArrayList<>();
        if (!SignupUtil.maintenanceAppSignup()) {
            apps.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        apps.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        apps.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);

        for (String app : apps) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule multiResourceModule = modBean.getModule(FacilioConstants.MultiResource.NAME);

            FacilioForm defaultForm = new FacilioForm();
            defaultForm.setName("standard_" + app);
            defaultForm.setModule(multiResourceModule);
            defaultForm.setDisplayName("Standard_" + app);
            defaultForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
            defaultForm.setShowInWeb(true);
            defaultForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.IWMS_APP));
            defaultForm.setIsSystemForm(true);
            defaultForm.setType(FacilioForm.Type.FORM);
            defaultForm.setAppLinkName(app);

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(multiResourceModule.getName()));
            List<FormSection> sections = new ArrayList<FormSection>();
            FormSection section = new FormSection();
            section.setName("Add Associated Asset / Space ");
            section.setSectionType(FormSection.SectionType.FIELDS);
            section.setShowLabel(false);

            List<FormField> fields = new ArrayList<>();
            int seq = 0;
            fields.add(new FormField(fieldMap.get("sequence").getFieldId(), "sequence", FacilioField.FieldDisplayType.NUMBER, "Sequence", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("asset").getFieldId(), "asset", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("space").getFieldId(), "space", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space", FormField.Required.OPTIONAL, ++seq, 1));
            fields.add(new FormField(fieldMap.get("description").getFieldId(), "description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seq, 1));

            section.setFields(fields);
            section.setSequenceNumber(1);
            sections.add(section);

            defaultForm.setSections(sections);

            FormsAPI.createForm(defaultForm, multiResourceModule);
            Map<Long, FormField> formFieldMap = defaultForm.getSections().stream().map(FormSection::getFields).flatMap(List::stream).collect(Collectors.toMap(FormField::getFieldId, Function.identity()));
            addRuleForSpace(defaultForm, fieldMap, formFieldMap);
            addRuleForAsset(defaultForm, fieldMap, formFieldMap);
        }
        
    }

    public void addRuleForSpace(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Reset space value when asset exists");
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        singleRule.setFormId(defaultForm.getId());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("asset"), CommonOperators.IS_NOT_EMPTY));
        singleRule.setCriteria(criteria);
        List<FormRuleTriggerFieldContext> triggerFieldList = new ArrayList<>();
        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldId(formFieldMap.get(fieldMap.get("asset").getId()).getId());
        triggerFieldList.add(triggerField);
        singleRule.setTriggerFields(triggerFieldList);
        long spaceFormFieldId = formFieldMap.get(fieldMap.get("space").getId()).getId();
        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.EXECUTE_SCRIPT.getVal());
        String workflowString = "List getActions(Map formData) {\n" +
                "result = [];\n" +
                "temp = {};\n" +
                "action = {};\n" +
                "asset_exists = \"false\";\n"+
                "action.actionName = \"set\";\n" +
                "action.value = null;\n" +
                "temp.action = action;  \n" +
                "temp.fieldId =" + spaceFormFieldId + ";\n" +
                "result.push(temp);\n" +
                "return result;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "}";
        WorkflowContext workflowScript = new WorkflowContext();
        workflowScript.setWorkflowV2String(workflowString);
        workflowScript.setIsV2Script(true);
        showAction.setWorkflow(workflowScript);
        actions.add(showAction);
        singleRule.setActions(actions);

        FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
        Context context = chain.getContext();
        context.put(FormRuleAPI.FORM_RULE_CONTEXT, singleRule);
        chain.execute();
    }

    public void addRuleForAsset(FacilioForm defaultForm, Map<String, FacilioField> fieldMap,Map<Long, FormField> formFieldMap) throws Exception {
        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Reset asset value when space value exists");
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        singleRule.setFormId(defaultForm.getId());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("space"), CommonOperators.IS_NOT_EMPTY));
        singleRule.setCriteria(criteria);
        List<FormRuleTriggerFieldContext> triggerFieldList = new ArrayList<>();
        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldId(formFieldMap.get(fieldMap.get("space").getId()).getId());
        triggerFieldList.add(triggerField);
        singleRule.setTriggerFields(triggerFieldList);
        long assetFormFieldId = formFieldMap.get(fieldMap.get("asset").getId()).getId();
        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.EXECUTE_SCRIPT.getVal());
        String workflowString = "List getActions(Map formData) {\n" +
                "result = [];\n" +
                "temp = {};\n" +
                "action = {};\n" +
                "asset_exists = \"false\";\n"+
                "action.actionName = \"set\";\n" +
                "action.value = null;\n" +
                "temp.action = action;  \n" +
                "temp.fieldId =" + assetFormFieldId + ";\n" +
                "result.push(temp);\n" +
                "return result;\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "}";
        WorkflowContext workflowScript = new WorkflowContext();
        workflowScript.setWorkflowV2String(workflowString);
        workflowScript.setIsV2Script(true);
        showAction.setWorkflow(workflowScript);
        actions.add(showAction);
        singleRule.setActions(actions);

        FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
        Context context = chain.getContext();
        context.put(FormRuleAPI.FORM_RULE_CONTEXT, singleRule);
        chain.execute();
    }


    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> multiResourceModule = new ArrayList<FacilioView>();
        multiResourceModule.add(getMultiResource().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.MultiResource.NAME);
        groupDetails.put("views", multiResourceModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getMultiResource() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Multi_Resource.ID", FieldType.NUMBER), true));

        FacilioView multiResourceView = new FacilioView();
        multiResourceView.setName("all");
        multiResourceView.setDisplayName(FacilioConstants.MultiResource.DISPLAY_NAME);

        multiResourceView.setModuleName(FacilioConstants.MultiResource.NAME);
        multiResourceView.setSortFields(sortFields);

        List<ViewField> multiResourceViewFields = new ArrayList<>();

        multiResourceViewFields.add(new ViewField("sequence","Sequence"));
        multiResourceViewFields.add(new ViewField("asset","Asset"));
        multiResourceViewFields.add(new ViewField("space","Space"));
        multiResourceViewFields.add(new ViewField("description","Description"));

        multiResourceView.setFields(multiResourceViewFields);

        return multiResourceView;
    }
}
