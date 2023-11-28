package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;
import org.json.simple.JSONObject;

import java.util.*;

public class BudgetModule extends BaseModuleConfig{
    public BudgetModule(){
        setModuleName(FacilioConstants.ContextNames.BUDGET);
    }

    @Override
    public void addData() throws Exception {
        addSystemButton();
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> budget = new ArrayList<FacilioView>();
        budget.add(getAllBudgetView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BUDGET);
        groupDetails.put("views", budget);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllBudgetView() {

        FacilioModule module = ModuleFactory.getBudgetModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Budgets");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule budgetModule = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET);

        FacilioForm budgetForm = new FacilioForm();
        budgetForm.setDisplayName("Budget");
        budgetForm.setName("default_"+ FacilioConstants.ContextNames.Budget.BUDGET +"_web");
        budgetForm.setModule(budgetModule);
        budgetForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        budgetForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> budgetDetailFields = new ArrayList<>();
        budgetDetailFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        budgetDetailFields.add(new FormField("fiscalYearStart", FacilioField.FieldDisplayType.SELECTBOX, "Fiscal Year Start", FormField.Required.REQUIRED, 2, 2));
        budgetDetailFields.add(new FormField("fiscalYear", FacilioField.FieldDisplayType.SELECTBOX, "Fiscal Year", FormField.Required.REQUIRED, 2, 3));
        budgetDetailFields.add(new FormField("focalPointType", FacilioField.FieldDisplayType.SELECTBOX, "Scope", FormField.Required.OPTIONAL, 3, 1));
        budgetDetailFields.add(new FormField("focalPointResource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.OPTIONAL, 4, 1));

        List<FormField> budgetAmountFields = new ArrayList<>();
        budgetAmountFields.add(new FormField("budgetamount", FacilioField.FieldDisplayType.BUDGET_AMOUNT, "Budget Amounts", FormField.Required.OPTIONAL, 5, 1));

        List<FormField> defaultFormFields = new ArrayList<FormField>();
        defaultFormFields.addAll(budgetDetailFields);
        defaultFormFields.addAll(budgetAmountFields);

//        budgetForm.setFields(defaultFormFields);

        FormSection defaultSection = new FormSection("BUDGET DETAILS", 1, budgetDetailFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection budgetAmountSection = new FormSection("BUDGET AMOUNTS", 2, budgetAmountFields, true);
        budgetAmountSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> budgetFormSections = new ArrayList<>();
        budgetFormSections.add(defaultSection);
        budgetFormSections.add(budgetAmountSection);

        budgetForm.setSections(budgetFormSections);
        budgetForm.setIsSystemForm(true);
        budgetForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(budgetForm);

    }

    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUDGET);
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, getSystemPage(app, module, false, true));
        }
        return appNameVsPage;
    }

    private static List<PagesContext> getSystemPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        return new ModulePages()
                .addPage("budget", "Budget", "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("totalbudget", "Total Budget", PageWidget.WidgetType.TOTAL_BUDGET, "totalbudget_3_4", 0, 0, null, null)
                .widgetDone()
                .addWidget("actualAmount","Actual Amount", PageWidget.WidgetType.ACTUAL_BUDGET_AMOUNT,"actualbudgetamount_3_4",4,0,null,null)
                .widgetDone()
                .addWidget("remainingAmount","Remaining Amount", PageWidget.WidgetType.REMAINING_BUDGET,"remainingbudget_3_4",8,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("budget", null, null)
                .addWidget("budget","Budget", PageWidget.WidgetType.BUDGET_INCOME_EXPENSE,"flexiblebudgetincomeexpense_21",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("notesAndInformation", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Budget Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("transaction", "Transactions", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("transaction", null, null)
                .addWidget("transaction", "All Transactions", PageWidget.WidgetType.BUDGET_TRANSACTIONS, "flexiblewebbudgettransactions_9", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField fiscalYearStartField = moduleBean.getField("fiscalYearStart", moduleName);
        FacilioField fiscalYearField = moduleBean.getField("fiscalYear", moduleName);
        FacilioField focalPointTypeField = moduleBean.getField("focalPointType", moduleName);
        FacilioField focalPointResourceField = moduleBean.getField("focalPointResource", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, fiscalYearStartField,1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, fiscalYearField, 1 , 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, focalPointTypeField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, focalPointResourceField, 1, 4, 1);


        widgetGroup.setName("generalinformation");
        widgetGroup.setDisplayName(null);
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "cmdnotes");

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", "cmdattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("noteswidget", "Notes", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("attachment", "Attachments", "")
                .addWidget("attachmentwidget", "Attachments", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static void addSystemButton() throws Exception{
        String moduleName = FacilioConstants.ContextNames.BUDGET;

        SystemButtonApi.addListEditButton(moduleName);
        SystemButtonApi.addCreateButtonWithModuleDisplayName(moduleName);
        SystemButtonApi.addSummaryEditButton(moduleName);
        SystemButtonApi.addListDeleteButton(moduleName);

    }
}
