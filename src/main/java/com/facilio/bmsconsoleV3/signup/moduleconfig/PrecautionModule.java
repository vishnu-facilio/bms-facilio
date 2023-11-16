package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.HazardTemplatePage;
import com.facilio.bmsconsole.TemplatePages.PrecautionsTemplatePage;
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

public class PrecautionModule extends BaseModuleConfig{
    public PrecautionModule(){
        setModuleName(FacilioConstants.ContextNames.PRECAUTION);
    }
    public void addData() throws Exception {
        addSystemButtons();
    }
    public static void addSystemButtons() throws Exception {
        SystemButtonApi.addSummaryEditButton(FacilioConstants.ContextNames.PRECAUTION);
        SystemButtonApi.addCreateButtonWithModuleDisplayName(FacilioConstants.ContextNames.PRECAUTION);
        SystemButtonApi.addExportAsCSV(FacilioConstants.ContextNames.PRECAUTION);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.PRECAUTION);
        SystemButtonApi.addListEditButton(FacilioConstants.ContextNames.PRECAUTION);
        SystemButtonApi.addListDeleteButton(FacilioConstants.ContextNames.PRECAUTION);
        SystemButtonApi.addBulkDeleteButton(FacilioConstants.ContextNames.PRECAUTION);
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> precaution = new ArrayList<FacilioView>();
        precaution.add(getAllPrecautionView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PRECAUTION);
        groupDetails.put("views", precaution);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPrecautionView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("Precautions");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule precautionModule = modBean.getModule(FacilioConstants.ContextNames.PRECAUTION);

        FacilioForm precautionForm = new FacilioForm();
        precautionForm.setDisplayName("PREACUTION");
        precautionForm.setName("default_precaution_web");
        precautionForm.setModule(precautionModule);
        precautionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        precautionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> precautionFormFields = new ArrayList<>();
        precautionFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        precautionFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        precautionForm.setFields(precautionFormFields);

        FormSection section = new FormSection("Default", 1, precautionFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        precautionForm.setSections(Collections.singletonList(section));
        precautionForm.setIsSystemForm(true);
        precautionForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(precautionForm);
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
            appNameVsPage.put(appName,getPrecautionViewPage(app, module));
        }
        return appNameVsPage;
    }
    private List<PagesContext> getPrecautionViewPage(ApplicationContext app, FacilioModule module) throws Exception {

        return new ModulePages()
                .addPage("hazardViewPage", "Default Hazard View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("precautionsummaryfields", null, null)
                .addWidget("precautionFieldsWidget", "Precaution Details",  PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, PrecautionsTemplatePage.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("precautionhazard", null, null)
                .addWidget("precautionhazard", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, PrecautionsTemplatePage.getHazardWidgetParams(), null)
                .widgetDone()
                .sectionDone()
                .addSection("precautionwidgetGroup", null,  null)
                .addWidget("precautioncommentandattachmentwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, PrecautionsTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }
}

