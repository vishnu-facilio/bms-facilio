package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.HazardTemplatePage;
import com.facilio.bmsconsole.TemplatePages.SafetyPlanTemplatePage;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class HazardModule extends BaseModuleConfig{
    public HazardModule(){
        setModuleName(FacilioConstants.ContextNames.HAZARD);
    }
    public void addData() throws Exception {
        addSystemButtons();
    }
    public static void addSystemButtons() throws Exception {
        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit");
        edit.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        edit.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        edit.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.HAZARD,edit);
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> hazard = new ArrayList<FacilioView>();
        hazard.add(getAllHazardModuleView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.HAZARD);
        groupDetails.put("views", hazard);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHazardModuleView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Hazards");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule hazardModule = modBean.getModule(FacilioConstants.ContextNames.HAZARD);

        FacilioForm hazardForm = new FacilioForm();
        hazardForm.setDisplayName("HAZARD");
        hazardForm.setName("default_hazard_web");
        hazardForm.setModule(hazardModule);
        hazardForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        hazardForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> hazardFormFields = new ArrayList<>();
        hazardFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        hazardFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        hazardFormFields.add(new FormField("type", FacilioField.FieldDisplayType.SELECTBOX, "Type", FormField.Required.OPTIONAL, 3, 1));
//        hazardForm.setFields(hazardFormFields);

        FormSection section = new FormSection("Default", 1, hazardFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        hazardForm.setSections(Collections.singletonList(section));
        hazardForm.setIsSystemForm(true);
        hazardForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(hazardForm);
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
            appNameVsPage.put(appName,getHazardViewPage(app, module));
        }
        return appNameVsPage;
    }
    private List<PagesContext> getHazardViewPage(ApplicationContext app, FacilioModule module) throws Exception {

        return new ModulePages()
                .addPage("hazardViewPage", "Default Hazard View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("hazardsummaryfields", null, null)
                .addWidget("hazardFieldsWidget", "Hazard Details",  PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, HazardTemplatePage.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("hazardsprecaution", null, null)
                .addWidget("hazardsprecaution", "Associated Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, HazardTemplatePage.getPrecautionWidgetParams(), null)
                .widgetDone()
                .sectionDone()
                .addSection("hazardswidgetGroup", null,  null)
                .addWidget("hazardscommentandattachmentwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, HazardTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }
}

