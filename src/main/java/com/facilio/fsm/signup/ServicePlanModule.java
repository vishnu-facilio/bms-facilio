package com.facilio.fsm.signup;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.bmsconsole.util.SystemButtonApi.addSystemButton;

public class ServicePlanModule  extends BaseModuleConfig{
    public ServicePlanModule(){
        setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule servicePlanModule = constructServicePlanModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(servicePlanModule));
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        addServicePlanFieldInServicePM(servicePlanModule);
        addActivityModuleForServicePlan();
        addSystemButtons();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN));
    }
    private FacilioModule constructServicePlanModule() throws Exception {
        ModuleBean bean = Constants.getModBean();
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN, "Job Plan", "Service_Plan", FacilioModule.ModuleType.BASE_ENTITY,true);
        module.setDescription("Create flexible task templates suitable for both planned and reactive maintenance.");
        List<FacilioField> fields = new ArrayList<>();
        FacilioField name = FieldFactory.getDefaultField("name","Name","NAME", FieldType.STRING,true);
        name.setRequired(true);
        fields.add(name);
        fields.add(FieldFactory.getDefaultField("description","Description","DESCRIPTION",FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
        LookupField client = FieldFactory.getDefaultField("client","Client","CLIENT", FieldType.LOOKUP);
        client.setLookupModule(bean.getModule(FacilioConstants.ContextNames.CLIENT));
        fields.add(client);
        module.setFields(fields);
        return module;
    }
    private void addServicePlanFieldInServicePM(FacilioModule servicePlanModule)throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePMModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        LookupField servicePlan = FieldFactory.getDefaultField("servicePlan","Job Plan","SERVICE_PLAN", FieldType.LOOKUP);
        servicePlan.setRequired(true);
        servicePlan.setLookupModule(servicePlanModule);
        servicePlan.setModule(servicePMModule);
        modBean.addField(servicePlan);
    }
    public void addActivityModuleForServicePlan() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule servicePlan = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
        FacilioModule module = new FacilioModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ACTIVITY, "Job Plan Activity", "Service_Plan_Activity", FacilioModule.ModuleType.ACTIVITY);

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
        modBean.addSubModule(servicePlan.getModuleId(), module.getModuleId());
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule servicePlanModule = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
        FacilioForm servicePlanForm = new FacilioForm();
        servicePlanForm.setDisplayName("Standard");
        servicePlanForm.setName("default_servicePlan_web");
        servicePlanForm.setModule(servicePlanModule);
        servicePlanForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        servicePlanForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP));
        List<FormField> generalInformationFields = new ArrayList<>();
        generalInformationFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        generalInformationFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        generalInformationFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.OPTIONAL, FacilioConstants.ContextNames.CLIENT, 3, 1));

        FormSection generalInfoSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalInfoSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> serviceTaskFields = new ArrayList<>();
        serviceTaskFields.add(new FormField("serviceTaskTemplate", FacilioField.FieldDisplayType.SERVICE_TASK_TEMPLATE, "Tasks", FormField.Required.OPTIONAL, 1, 1));

        FormSection serviceTaskSection = new FormSection("", 2, serviceTaskFields, false);
        serviceTaskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> servicePlanFormSections = new ArrayList<>();
        servicePlanFormSections.add(generalInfoSection);
        servicePlanFormSections.add(serviceTaskSection);

        servicePlanForm.setSections(servicePlanFormSections);
        servicePlanForm.setIsSystemForm(true);
        servicePlanForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(servicePlanForm);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> servicePlanViews = new ArrayList<FacilioView>();
        servicePlanViews.add(getAllServicePlanViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "default");
        groupDetails.put("displayName", "Default");
        groupDetails.put("moduleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
        groupDetails.put("views", servicePlanViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private FacilioView getAllServicePlanViews() throws Exception{
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Job Plans");
        allView.setModuleName(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP));


        List<ViewField> servicePlanViewFields = new ArrayList<>();
        servicePlanViewFields.add(new ViewField("name","Name"));
        servicePlanViewFields.add(new ViewField("description","Description"));
        servicePlanViewFields.add(new ViewField("client","Client"));
        allView.setFields(servicePlanViewFields);

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
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
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
    private static List<PagesContext> getSystemPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ACTIVITY);
        return new ModulePages()
                .addPage("servicePlan", "Default Job Plan Page", "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicePlanTasks", null, null)
                .addWidget("servicePlanTasks", "Tasks", PageWidget.WidgetType.SERVICE_PLAN_TASKS, "webServicePlanTasks_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("servicePlanInventory", null, null)
                .addWidget("servicePlanInventory", "Inventory", PageWidget.WidgetType.SERVICE_PLAN_INVENTORY, "webServicePlanInventory_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicePlanRelatedList", "Related List", "List of all related records across modules")
                .addWidget("servicePlanRelatedList", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
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
    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ATTACHMENTS);

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
    private static void addSystemButtons() throws Exception {
        SystemButtonRuleContext create = new SystemButtonRuleContext();
        create.setName("Create Job Plan");
        create.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        create.setIdentifier(FacilioConstants.ContextNames.CREATE);
        create.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        create.setPermission("CREATE");
        create.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN,create);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit_list");
        edit.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        edit.setPermission("UPDATE");
        edit.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN,edit);

        SystemButtonRuleContext summaryEditButton = new SystemButtonRuleContext();
        summaryEditButton.setName("Edit");
        summaryEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        summaryEditButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        summaryEditButton.setIdentifier("edit_summary");
        summaryEditButton.setPermissionRequired(true);
        summaryEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN,summaryEditButton);

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN,listDeleteButton);


        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        SystemButtonApi.addSystemButton(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN,bulkDeleteButton);

        SystemButtonApi.addExportAsCSV(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
    }
}
