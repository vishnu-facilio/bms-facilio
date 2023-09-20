package com.facilio.fsm.signup;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fsm.context.ServicePMTemplateContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.bmsconsole.util.SystemButtonApi.addSystemButton;
import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

public class ServicePMTemplateModule extends BaseModuleConfig {
    public static List<String> servicePMTemplateSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);
    public ServicePMTemplateModule(){
        setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
    }
    @Override
    public void addData() throws Exception {
        FacilioModule servicePMTemplateModule = constructServicePMTemplateModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(servicePMTemplateModule));
        addModuleChain.execute();

        addPMTemplateLookupField();
        addActivityModuleForServicePMTemplate();
        addTemplateFieldInTrigger();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE));
        addSystemButtons();
    }
    private FacilioModule constructServicePMTemplateModule() throws Exception{
        ModuleBean bean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE, "Master PM", "Service_PM_Template", FacilioModule.ModuleType.BASE_ENTITY,bean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE),true);
        module.setHideFromParents(true);

        List<FacilioField> fields = new ArrayList<>();
        FacilioField name = FieldFactory.getDefaultField("masterPMName","Name","NAME", FieldType.STRING,true);
        name.setRequired(true);
        fields.add(name);
        LookupField assetCategory = FieldFactory.getDefaultField("assetCategory","Asset Category","ASSET_CATEGORY", FieldType.LOOKUP);
        assetCategory.setLookupModule(bean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);

        LookupField spaceCategory = FieldFactory.getDefaultField("spaceCategory","Space Category","SPACE_CATEGORY", FieldType.LOOKUP);
        spaceCategory.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);

        SystemEnumField masterPMType = FieldFactory.getDefaultField("masterPMType","Master PM Type","MASTER_PM_TYPE",FieldType.SYSTEM_ENUM);
        masterPMType.setEnumName("MasterPMType");
        fields.add(masterPMType);
        module.setFields(fields);
        return module;
    }
    private void addPMTemplateLookupField()throws Exception{
        ModuleBean bean = Constants.getModBean();
        FacilioModule servicePM  = bean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        FacilioModule servicePMTemplate = bean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
        bean.addSubModule(servicePM.getModuleId(), servicePMTemplate.getModuleId(),0);

        LookupField servicePMTemplateField = FieldFactory.getDefaultField("servicePMTemplate","Master PM","SERVICE_PM_TEMPLATE", FieldType.LOOKUP);
        servicePMTemplateField.setLookupModule(servicePMTemplate);
        servicePMTemplateField.setModule(servicePM);
        bean.addField(servicePMTemplateField);
    }
    public void addActivityModuleForServicePMTemplate() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePMTemplate = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE_ACTIVITY, "Master PM Activity", "Service_PM_Template_Activity", FacilioModule.ModuleType.ACTIVITY);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING));
        LookupField doneBy = FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);
        module.setFields(fields);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
        modBean.addSubModule(servicePMTemplate.getModuleId(), module.getModuleId());
    }
    private static void addSystemButtons() throws Exception {
        SystemButtonRuleContext createBtn = new SystemButtonRuleContext();
        createBtn.setName("Create PM");
        createBtn.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        createBtn.setIdentifier("createServicePM");
        addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE, createBtn);

        SystemButtonRuleContext create = new SystemButtonRuleContext();
        create.setName("Create Master PM");
        create.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        create.setIdentifier(FacilioConstants.ContextNames.CREATE);
        create.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        create.setPermission("CREATE");
        create.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE,create);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit_list");
        edit.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        edit.setPermission("UPDATE");
        edit.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE,edit);

        SystemButtonRuleContext summaryEditButton = new SystemButtonRuleContext();
        summaryEditButton.setName("Edit");
        summaryEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        summaryEditButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        summaryEditButton.setIdentifier("edit_summary");
        summaryEditButton.setPermissionRequired(true);
        summaryEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE,summaryEditButton);

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE,listDeleteButton);


        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE,bulkDeleteButton);

        SystemButtonApi.addExportAsCSV(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> servicePMTemplateViews = new ArrayList<FacilioView>();
        servicePMTemplateViews.add(getAllServicePMTemplateViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "default");
        groupDetails.put("displayName", "Default");
        groupDetails.put("moduleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
        groupDetails.put("views", servicePMTemplateViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private FacilioView getAllServicePMTemplateViews()throws Exception{
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Master PMs");
        allView.setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(servicePMTemplateSupportedApps);


        List<ViewField> servicePMTemplateViewFields = new ArrayList<>();

        servicePMTemplateViewFields.add(new ViewField("masterPMName","Name"));
        servicePMTemplateViewFields.add(new ViewField("pmDescription","Description"));
        servicePMTemplateViewFields.add(new ViewField("masterPMType","Master PM Type"));
        servicePMTemplateViewFields.add(new ViewField("spaceCategory","Space Category"));
        servicePMTemplateViewFields.add(new ViewField("assetCategory","Asset Category"));
        servicePMTemplateViewFields.add(new ViewField("servicePMTrigger","Recurring Schedule"));
        servicePMTemplateViewFields.add(new ViewField("servicePlan","Job Plan"));
        servicePMTemplateViewFields.add(new ViewField("client","Client"));
        servicePMTemplateViewFields.add(new ViewField("resolutionDueDuration","Resolution Due Duration"));
        servicePMTemplateViewFields.add(new ViewField("estimatedDuration","Estimated Duration"));
        servicePMTemplateViewFields.add(new ViewField("leadTime","Lead Time"));
        servicePMTemplateViewFields.add(new ViewField("previewPeriod","Preview Period"));

        allView.setFields(servicePMTemplateViewFields);

        List<String> roles = new ArrayList<>();
        roles.add(FacilioConstants.DefaultRoleNames.FSM_SUPER_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_ADMIN);
        roles.add(FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);

        allView.setViewSharing(getSharingContext(roles));

        return allView;
    }
    private static SharingContext<SingleSharingContext> getSharingContext(List<String> roles)throws Exception{
        SharingContext<SingleSharingContext> sharingContext = new SharingContext<>();
        for(String role: roles){
            Role fsmRole = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), role);
            SingleSharingContext context = new SingleSharingContext();
            context.setType(SingleSharingContext.SharingType.ROLE.ordinal()+1);
            context.setRoleId(fsmRole.getRoleId());
            sharingContext.add(context);
        }
        return sharingContext;
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePMTemplateModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);

        FacilioForm servicePMTemplateForm = new FacilioForm();
        servicePMTemplateForm.setDisplayName("Standard");
        servicePMTemplateForm.setName("default_servicePMTemplate_web");
        servicePMTemplateForm.setModule(servicePMTemplateModule);
        servicePMTemplateForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        servicePMTemplateForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> servicePMTemplateSectionFields = new ArrayList<>();
        servicePMTemplateSectionFields.add(new FormField("masterPMName", FacilioField.FieldDisplayType.TEXTBOX, "Master PM Name", FormField.Required.REQUIRED, 1, 1));
        servicePMTemplateSectionFields.add(new FormField("pmName", FacilioField.FieldDisplayType.TEXTBOX, "PM Name", FormField.Required.REQUIRED, 2, 1));
        servicePMTemplateSectionFields.add(new FormField("pmDescription", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        servicePMTemplateSectionFields.add(new FormField("masterPMType", FacilioField.FieldDisplayType.SELECTBOX, "Master PM Type", FormField.Required.REQUIRED, 4, 2));
        servicePMTemplateSectionFields.add(new FormField("servicePlan", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Job Plan", FormField.Required.OPTIONAL, FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN, 5, 2));
        FormField spaceCategory= new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space Category", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.SPACE_CATEGORY, 6, 1);
        spaceCategory.setHideField(true);
        servicePMTemplateSectionFields.add(spaceCategory);
        FormField assetCategory = new FormField("assetCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Asset Category", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.ASSET_CATEGORY, 7, 1);
        assetCategory.setHideField(true);
        servicePMTemplateSectionFields.add(assetCategory);
        servicePMTemplateSectionFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.CLIENT, 8, 2));
        servicePMTemplateSectionFields.add(new FormField("resolutionDueDuration", FacilioField.FieldDisplayType.DURATION, "Resolution Due Duration", FormField.Required.OPTIONAL, 9, 2));
        servicePMTemplateSectionFields.add(new FormField("estimatedDuration", FacilioField.FieldDisplayType.DURATION, "Estimated Duration", FormField.Required.REQUIRED, 10, 2));
        servicePMTemplateSectionFields.add(new FormField("leadTime", FacilioField.FieldDisplayType.NUMBER, "Lead Time (Days)", FormField.Required.OPTIONAL, 11, 2));
        servicePMTemplateSectionFields.add(new FormField("previewPeriod", FacilioField.FieldDisplayType.NUMBER, "Preview Period (Days)", FormField.Required.OPTIONAL, 12, 2));

        FormSection templateSection = new FormSection("Master PM Information", 1, servicePMTemplateSectionFields, true);
        templateSection.setSectionType(FormSection.SectionType.FIELDS);

        FormField servicePMTriggerField= new FormField("servicePMTemplateTrigger", FacilioField.FieldDisplayType.SERVICE_PM_TEMPLATE_TRIGGER, "PM Trigger", FormField.Required.OPTIONAL, 2, 1);

        FormSection triggerSection = new FormSection("Recurring Schedule Information", 2, Collections.singletonList(servicePMTriggerField), true);
        triggerSection.setSectionType(FormSection.SectionType.FIELDS);


        List<FormField> serviceOrderSectionFields = new ArrayList<>();
        serviceOrderSectionFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        serviceOrderSectionFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Work Order Description", FormField.Required.OPTIONAL, 2, 1));
        serviceOrderSectionFields.add(new FormField("category", FacilioField.FieldDisplayType.SELECTBOX, "Category", FormField.Required.REQUIRED, 3, 2));
        serviceOrderSectionFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.REQUIRED, 4, 2));
        serviceOrderSectionFields.add(new FormField("fieldAgent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.PEOPLE, 5, 2));
        serviceOrderSectionFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.VENDORS, 6, 2));
        serviceOrderSectionFields.add(new FormField("autoCreateSa", FacilioField.FieldDisplayType.DECISION_BOX, "Auto Create Appointment", FormField.Required.OPTIONAL, 7, 2));

        FormSection serviceOrderSection = new FormSection("Work Order Information", 3, serviceOrderSectionFields, true);
        serviceOrderSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> servicePMTemplateSections = new ArrayList<>();
        servicePMTemplateSections.add(templateSection);
        servicePMTemplateSections.add(triggerSection);
        servicePMTemplateSections.add(serviceOrderSection);

        servicePMTemplateForm.setSections(servicePMTemplateSections);
        servicePMTemplateForm.setIsSystemForm(true);
        servicePMTemplateForm.setType(FacilioForm.Type.FORM);

        FormRuleContext assetCategoryFieldRule = addShowAssetCategoryFieldRule();
        FormRuleContext spaceCategoryFieldRule = addShowSpaceCategoryFieldRule();
        FormRuleContext addHideAssetCategoryFieldRule = addHideAssetCategoryFieldRule();
        FormRuleContext addHideSpaceCategoryFieldRule = addHideSpaceCategoryFieldRule();
        List<FormRuleContext> rules = new ArrayList<>();
        rules.add(assetCategoryFieldRule);
        rules.add(spaceCategoryFieldRule);
        rules.add(addHideAssetCategoryFieldRule);
        rules.add(addHideSpaceCategoryFieldRule);
        servicePMTemplateForm.setDefaultFormRules(rules);

        return Collections.singletonList(servicePMTemplateForm);
    }
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, getSystemPage(app, module, false, true));
        }
        return appNameVsPage;
    }
    public static List<PagesContext> getSystemPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE_ACTIVITY);
        return new ModulePages()
                .addPage("servicePMTemplate", "Default Master PM Page", "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryFields", null, null)
                .addWidget("summaryFieldsWidget", "PM Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null,  getSummaryWidgetDetails(app,FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("servicePlan", "Job Plan", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicePlanTasks", null, null)
                .addWidget("servicePlanTasks", "Tasks", PageWidget.WidgetType.SERVICE_PLAN_TASKS, "webServicePlanTasks_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("servicePlanInventory", null, null)
                .addWidget("servicePlanInventory", "Inventory", PageWidget.WidgetType.SERVICE_PLAN_INVENTORY, "webServicePlanInventory_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("servicePMs", "Planned Maintenance", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicePMs", null, null)
                .addWidget("servicePMs", "Planned Maintenance", PageWidget.WidgetType.SERVICE_PM, "webPM_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
    private static JSONObject getSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> templateFields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
        Map<String, FacilioField> templateFieldsMap = FieldFactory.getAsMap(templateFields);
        List<FacilioField> servicePMTriggerFields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
        Map<String, FacilioField> servicePMTriggerFieldsMap = FieldFactory.getAsMap(servicePMTriggerFields);
        FacilioField pmNameField = templateFieldsMap.get("pmName");
        FacilioField descriptionField = templateFieldsMap.get("pmDescription");
        FacilioField masterPMTypeField = templateFieldsMap.get("masterPMType");
        FacilioField assetCategoryField = templateFieldsMap.get("assetCategory");
        FacilioField spaceCategoryField = templateFieldsMap.get("spaceCategory");
        FacilioField clientField = templateFieldsMap.get("client");
        FacilioField resolutionDueDurationField = templateFieldsMap.get("resolutionDueDuration");
        FacilioField servicePlanField = templateFieldsMap.get("servicePlan");
        FacilioField estimatedDurationField = templateFieldsMap.get("estimatedDuration");
        FacilioField leadTimeField = templateFieldsMap.get("leadTime");
        FacilioField previewPeriodField = templateFieldsMap.get("previewPeriod");
        FacilioField soName = templateFieldsMap.get("name");
        FacilioField soDescription = templateFieldsMap.get("description");
        FacilioField soCategory = templateFieldsMap.get("category");
        FacilioField soPriority = templateFieldsMap.get("priority");
        FacilioField soFieldAgent = templateFieldsMap.get("fieldAgent");
        FacilioField soVendor = templateFieldsMap.get("vendor");
        FacilioField soAutoCreateSa = templateFieldsMap.get("autoCreateSa");
        FacilioField servicePMTriggerField = templateFieldsMap.get("servicePMTrigger");
        FacilioField triggerName = servicePMTriggerFieldsMap.get("name");
        FacilioField triggerFrequency = servicePMTriggerFieldsMap.get("frequency");
        FacilioField triggerStartTime = servicePMTriggerFieldsMap.get("startTime");
        FacilioField triggerEndTime = servicePMTriggerFieldsMap.get("endTime");


        SummaryWidget pageWidget = new SummaryWidget();

        // Group 1
        SummaryWidgetGroup templateInfoWidgetGroup = new SummaryWidgetGroup();
        templateInfoWidgetGroup.setName("templateInformation");
        templateInfoWidgetGroup.setDisplayName("Master PM Information");
        templateInfoWidgetGroup.setColumns(4);
        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup,"PM Name", pmNameField, 1, 1, 4);
        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, descriptionField, 2, 1, 4);

        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, masterPMTypeField, 3, 1, 1);
        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, assetCategoryField, 3, 2, 1);
        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, spaceCategoryField, 3, 3, 1);
        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, servicePlanField, 3, 4, 1);


        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, clientField, 4, 1, 1);
        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, estimatedDurationField, 4, 2, 1);
        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, resolutionDueDurationField, 4, 3, 1);
        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, previewPeriodField, 4, 4, 1);

        addSummaryFieldInWidgetGroup(templateInfoWidgetGroup, leadTimeField, 5, 1, 1);
        // Group 2
        SummaryWidgetGroup scheduleInfoWidgetGroup = new SummaryWidgetGroup();
        scheduleInfoWidgetGroup.setName("recurringScheduleInformation");
        scheduleInfoWidgetGroup.setDisplayName("Recurring Schedule Information");
        scheduleInfoWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,"Schedule Info", triggerName, 1, 1, 1,servicePMTriggerField);
        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,triggerFrequency.getDisplayName(), triggerFrequency, 1, 2, 1,servicePMTriggerField);

        //GROUP 3
        SummaryWidgetGroup serviceOrderWidgetGroup = new SummaryWidgetGroup();
        serviceOrderWidgetGroup.setName("serviceOrderInformation");
        serviceOrderWidgetGroup.setDisplayName("Work Order Information");
        serviceOrderWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,"Subject", soName, 1, 1, 4);

        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soDescription.getDisplayName(), soDescription, 2, 1, 4);

        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soCategory.getDisplayName(), soCategory, 3, 1, 1);
        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soPriority.getDisplayName(), soPriority, 3, 2, 1);
        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soFieldAgent.getDisplayName(), soFieldAgent, 3, 3, 1);
        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soVendor.getDisplayName(), soVendor, 3, 4, 1);

        addSummaryFieldInWidgetGroup(serviceOrderWidgetGroup,soAutoCreateSa.getDisplayName(), soAutoCreateSa, 4, 1, 1);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(templateInfoWidgetGroup);
        widgetGroupList.add(scheduleInfoWidgetGroup);
        widgetGroupList.add(serviceOrderWidgetGroup);

        pageWidget.setName("templateDetails");
        pageWidget.setDisplayName("Master PM Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 4, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 4, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    private FormRuleContext addShowSpaceCategoryFieldRule() throws Exception {

        FormRuleContext showRule = new FormRuleContext();
        showRule.setName("Space Category Show Rule");
        showRule.setDescription("Rule To Show Space Category field.");
        showRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        showRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        showRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("MASTER_PM_TYPE","masterPMType",String.valueOf(ServicePMTemplateContext.MasterPMType.SPACE.getIndex()), EnumOperators.IS));
        showRule.setCriteria(criteria);

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Master PM Type");
        showRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.SHOW_FIELD.getVal());

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
        actionField.setFormFieldName("Space Category");

        showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(showAction);

        showRule.setActions(actions);
        showRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return showRule;
    }
    private FormRuleContext addShowAssetCategoryFieldRule() throws Exception {

        FormRuleContext showRule = new FormRuleContext();
        showRule.setName("Asset Category Show Rule");
        showRule.setDescription("Rule To Show Asset Category field.");
        showRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        showRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        showRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("MASTER_PM_TYPE","masterPMType",String.valueOf(ServicePMTemplateContext.MasterPMType.ASSET.getIndex()), EnumOperators.IS));
        showRule.setCriteria(criteria);

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Master PM Type");
        showRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.SHOW_FIELD.getVal());

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        actionField.setFormFieldName("Asset Category");

        showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(showAction);

        showRule.setActions(actions);
        showRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return showRule;
    }
    private FormRuleContext addHideAssetCategoryFieldRule() throws Exception {

        FormRuleContext showRule = new FormRuleContext();
        showRule.setName("Asset Category Hide Rule");
        showRule.setDescription("Rule To Hide Asset Category field.");
        showRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        showRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        showRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("MASTER_PM_TYPE","masterPMType",String.valueOf(ServicePMTemplateContext.MasterPMType.ASSET.getIndex()), EnumOperators.ISN_T));
        showRule.setCriteria(criteria);

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Master PM Type");
        showRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.HIDE_FIELD.getVal());

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        actionField.setFormFieldName("Asset Category");

        showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(showAction);

        showRule.setActions(actions);
        showRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return showRule;
    }
    private FormRuleContext addHideSpaceCategoryFieldRule() throws Exception {

        FormRuleContext showRule = new FormRuleContext();
        showRule.setName("Space Category Hide Rule");
        showRule.setDescription("Rule To Hide Space Category field.");
        showRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        showRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        showRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("MASTER_PM_TYPE","masterPMType",String.valueOf(ServicePMTemplateContext.MasterPMType.SPACE.getIndex()), EnumOperators.ISN_T));
        showRule.setCriteria(criteria);

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Master PM Type");
        showRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext showAction = new FormRuleActionContext();
        showAction.setActionType(FormActionType.HIDE_FIELD.getVal());

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();
        actionField.setFormFieldName("Space Category");

        showAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(showAction);

        showRule.setActions(actions);
        showRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return showRule;
    }
    private void addTemplateFieldInTrigger()throws Exception{
        ModuleBean bean = Constants.getModBean();
        LookupField servicePMTemplate = FieldFactory.getDefaultField("servicePMTemplate","Master PM","SERVICE_PM_TEMPLATE", FieldType.LOOKUP);
        servicePMTemplate.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE),"Service PM Template module doesn't exist."));
        servicePMTemplate.setModule(bean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER));
        bean.addField(servicePMTemplate);
    }

}
