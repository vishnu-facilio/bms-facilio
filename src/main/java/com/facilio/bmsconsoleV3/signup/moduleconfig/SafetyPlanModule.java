package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.SafetyPlanTemplatePage;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;

import java.util.*;

public class SafetyPlanModule extends BaseModuleConfig{
    public SafetyPlanModule(){
        setModuleName(FacilioConstants.ContextNames.SAFETY_PLAN);
    }
    public void addData() throws Exception {
        addSystemButtons();
    }

    private static void addSystemButtons() throws Exception {
        SystemButtonRuleContext addHazard = new SystemButtonRuleContext();
        addHazard.setName("Add Hazard");
        addHazard.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        addHazard.setIdentifier("addHazard");
        addHazard.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SAFETY_PLAN,addHazard);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit");
        edit.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        edit.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        edit.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SAFETY_PLAN,edit);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> safetyPlan = new ArrayList<FacilioView>();
        safetyPlan.add(getAllSafetyPlansView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SAFETY_PLAN);
        groupDetails.put("views", safetyPlan);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllSafetyPlansView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Safety Plans");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule safetyPlanModule = modBean.getModule(FacilioConstants.ContextNames.SAFETY_PLAN);

        FacilioForm safetyPlanForm = new FacilioForm();
        safetyPlanForm.setDisplayName("SAFETY PLAN");
        safetyPlanForm.setName("default_safetyPlan_web");
        safetyPlanForm.setModule(safetyPlanModule);
        safetyPlanForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        safetyPlanForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> safetyPlanFormFields = new ArrayList<>();
        safetyPlanFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        safetyPlanFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        safetyPlanFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 3, 1));
//        safetyPlanForm.setFields(safetyPlanFormFields);

        FormSection section = new FormSection("Default", 1, safetyPlanFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        safetyPlanForm.setSections(Collections.singletonList(section));
        safetyPlanForm.setIsSystemForm(true);
        safetyPlanForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(safetyPlanForm);
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields maintenanceApp = new ScopeVariableModulesFields();
        maintenanceApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_maintenance_site"));
        maintenanceApp.setModuleId(module.getModuleId());
        maintenanceApp.setFieldName("siteId");

        scopeConfigList = Arrays.asList(maintenanceApp);
        return scopeConfigList;
    }
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getSafetyPlanViewPage(app, module));
        }
        return appNameVsPage;
    }
    private List<PagesContext> getSafetyPlanViewPage(ApplicationContext app, FacilioModule module) throws Exception {

        return new ModulePages()
                .addPage("safetyPlanViewPage", "Default Safety Plan View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("safetyplansummaryfields", null, null)
                .addWidget("safetyplansummaryFieldsWidget", "Safety Plan Details",  PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, SafetyPlanTemplatePage.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("safetyplanhazard", null, null)
                .addWidget("safetyplanhazard", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("safetyplanprecaution", null, null)
                .addWidget("safetyplanprecaution", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("safetyplanwidgetGroup", null,  null)
                .addWidget("safetyplancommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, SafetyPlanTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("workassettab", "Work Asset", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("workassetsection", null, null)
                .addWidget("workAssetList", "Work Asset", PageWidget.WidgetType.WORK_ASSET_LIST,"flexiblewebsafetyplanworkassetwidget_6", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }
}

