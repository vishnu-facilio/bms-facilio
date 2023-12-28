package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.TermsAndConditionTemplatePageFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

public class TermsAndConditionModule extends BaseModuleConfig{
    public TermsAndConditionModule(){
        setModuleName(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> termsAndCondition = new ArrayList<FacilioView>();
        termsAndCondition.add(getAllTermsAndConditionView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        groupDetails.put("views", termsAndCondition);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    public void addData() throws Exception {
        addSystemButtons();
    }
    public static void addSystemButtons() throws Exception{
        // summary buttons
        addDeleteButton("delete_summary",CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        addEditButton("edit_summary",CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        addPublishButton();
        addReviseButton();

        //list buttons
        addListButtons();
    }

    private static void addListButtons() throws Exception{
        addDeleteButton("delete_list",CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        addEditButton("edit_list",CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        SystemButtonApi.addCreateButtonWithModuleDisplayName(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        SystemButtonApi.addExportAsExcel(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
    }
    private static void addDeleteButton(String identifier,int position) throws Exception{
        SystemButtonRuleContext listDeleteBtn = new SystemButtonRuleContext();
        listDeleteBtn.setName("delete");
        listDeleteBtn.setModuleName(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        listDeleteBtn.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteBtn.setPositionType(position);
        listDeleteBtn.setIdentifier(identifier);
        listDeleteBtn.setCriteria(getNotPublishedCriteria());
        listDeleteBtn.setPermissionRequired(true);
        listDeleteBtn.setPermission("DELETE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, listDeleteBtn);
    }
    private static void addEditButton(String identifier,int position) throws Exception{
        SystemButtonRuleContext listDeleteBtn = new SystemButtonRuleContext();
        listDeleteBtn.setName("edit");
        listDeleteBtn.setModuleName(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        listDeleteBtn.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listDeleteBtn.setPositionType(position);
        listDeleteBtn.setIdentifier(identifier);
        listDeleteBtn.setCriteria(getNotPublishedCriteria());
        listDeleteBtn.setPermissionRequired(true);
        listDeleteBtn.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, listDeleteBtn);
    }


    public static void addReviseButton() throws Exception {
        SystemButtonRuleContext reviseBtn = new SystemButtonRuleContext();
        reviseBtn.setName("revise");
        reviseBtn.setModuleName(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        reviseBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        reviseBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        reviseBtn.setIdentifier("revise_summary");
        reviseBtn.setCriteria(getRevisionCriteria());
        reviseBtn.setPermissionRequired(true);
        reviseBtn.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, reviseBtn);
    }

    private static void addPublishButton() throws Exception{
        SystemButtonRuleContext publishBtn = new SystemButtonRuleContext();
        publishBtn.setName("publish");
        publishBtn.setModuleName(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        publishBtn.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        publishBtn.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        publishBtn.setIdentifier("publish_summary");
        publishBtn.setCriteria(getNotPublishedCriteria());
        publishBtn.setPermissionRequired(true);
        publishBtn.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, publishBtn);
    }

    private static Criteria getRevisionCriteria() throws Exception{
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("isRevised"),String.valueOf(false),BooleanOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("isPublished"),String.valueOf(true),BooleanOperators.IS));
        criteria.setPattern("(1 AND 2)");
        return criteria;
    }
    private static Criteria getNotPublishedCriteria() throws Exception{
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("IS_PUBLISHED","isPublished",String.valueOf(false),BooleanOperators.IS));
        criteria.setPattern("(1)");
        return criteria;
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception{
        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNameList = new ArrayList<>();
        appNameList.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNameList.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        for (String appName : appNameList) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createTermsAndConditionDefaultPage(app, module, true, false));
        }
        return appNameVsPage;
    }

    private List<PagesContext> createTermsAndConditionDefaultPage(ApplicationContext app, FacilioModule module, boolean isDefault, boolean isTemplate) throws Exception {
        return new ModulePages()
                .addPage("termsAndConditionDefaultPage","Default Terms and condition default page","",null,isTemplate,isDefault,true)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                        .addTab("termsAndCondition","Terms & Conditions", PageTabContext.TabType.SIMPLE,true,null)
                            .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                              .addSection("termsAndConditions","",null)
                                  .addWidget("termsAndConditions","Terms And Conditions", PageWidget.WidgetType.TERMS_CONDITIONS,"webTermsAndConditions_6_9",0,0,null,null)
                                  .widgetDone()
                             .sectionDone()
                           .columnDone()
                       .tabDone()
                       .addTab("notesAndInformation","Notes & Information", PageTabContext.TabType.SIMPLE,true,null)
                            .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                                .addSection("summaryfields", "", null)
                                    .addWidget("summaryFieldsWidget", "Term details",PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_6", 0, 0, null, TermsAndConditionTemplatePageFactory.getSummaryWidgetDetails(module.getName(),app))
                                    .widgetDone()
                                .sectionDone()
                                .addSection("widgetGroup", null, null)
                                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                                    .widgetDone()
                                .sectionDone()
                            .columnDone()
                       .tabDone()
                    .layoutDone()
                .pageDone().getCustomPages();
    }
    private JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static FacilioView getAllTermsAndConditionView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("name");
        name.setModule(ModuleFactory.getTermsAndConditionModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All T&C(s)");
        allView.setSortFields(Arrays.asList(new SortField(name, false)));

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
        FacilioModule termsAndConditionsModule = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);

        FacilioForm termsAndConditionForm = new FacilioForm();
        termsAndConditionForm.setDisplayName("TERMS AND CONDITION");
        termsAndConditionForm.setName("web_default");
        termsAndConditionForm.setModule(termsAndConditionsModule);
        termsAndConditionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        termsAndConditionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> termsAndConditionFormFields = new ArrayList<>();
        termsAndConditionFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        termsAndConditionFormFields.add(new FormField("termType", FacilioField.FieldDisplayType.TEXTBOX, "Term Type", FormField.Required.OPTIONAL, 2, 1));
        termsAndConditionFormFields.add(new FormField("shortDesc", FacilioField.FieldDisplayType.TEXTAREA, "Short Description", FormField.Required.OPTIONAL, 3, 1));
        FormField descField = new FormField("longDesc", FacilioField.FieldDisplayType.TEXTAREA, "Long Description", FormField.Required.OPTIONAL, 4, 1);
        descField.addToConfig("richText", true);
        termsAndConditionFormFields.add(descField);
        termsAndConditionFormFields.add(new FormField("defaultOnPo", FacilioField.FieldDisplayType.DECISION_BOX, "Default On PO", FormField.Required.OPTIONAL, 5, 2));
        termsAndConditionFormFields.add(new FormField("defaultOnQuotation", FacilioField.FieldDisplayType.DECISION_BOX, "Default On Quotation", FormField.Required.OPTIONAL, 6, 2));
        termsAndConditionFormFields.add(new FormField("defaultOnInvoice", FacilioField.FieldDisplayType.DECISION_BOX, "Default On Invoice", FormField.Required.OPTIONAL, 7, 2));

        FormSection section = new FormSection("Default", 1, termsAndConditionFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        termsAndConditionForm.setSections(Collections.singletonList(section));
        termsAndConditionForm.setIsSystemForm(true);
        termsAndConditionForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(termsAndConditionForm);
    }
}
