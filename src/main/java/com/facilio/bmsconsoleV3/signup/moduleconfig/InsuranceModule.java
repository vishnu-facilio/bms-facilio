package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class InsuranceModule extends BaseModuleConfig{

    public InsuranceModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.INSURANCE);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> insurance = new ArrayList<FacilioView>();
        insurance.add(getAllInsuranceView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INSURANCE);
        groupDetails.put("views", insurance);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> vendorPortalInsurance = new ArrayList<FacilioView>();
        vendorPortalInsurance.add(getVendorActiveInsuranceView().setOrder(order++));
        vendorPortalInsurance.add(getVendorExpiredInsuranceView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "vendorportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INSURANCE);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
        groupDetails.put("views", vendorPortalInsurance);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInsuranceView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Insurance");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getVendorActiveInsuranceView() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getActiveInsurancesCondition());
        FacilioModule insuranceModule = ModuleFactory.getInsuranceModule();
        FacilioField idField = FieldFactory.getIdField();
        idField.setModule(insuranceModule);
        FacilioView allView = new FacilioView();
        allView.setName("vendorActive");
        allView.setDisplayName("Active");
        allView.setCriteria(criteria);

        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));

        return allView;
    }

    private static FacilioView getVendorExpiredInsuranceView() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getExpiredInsurancesCondition());
        FacilioModule insuranceModule = ModuleFactory.getInsuranceModule();
        FacilioField validTill = FieldFactory.getField("validTill", "VALID_TILL", insuranceModule, FieldType.DATE_TIME);
        FacilioView allView = new FacilioView();
        allView.setName("vendorExpired");
        allView.setDisplayName("Expired");
        allView.setCriteria(criteria);
        allView.setSortFields(Arrays.asList(new SortField(validTill, false)));

        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));

        return allView;
    }

    public static Condition getActiveInsurancesCondition() {
        FacilioModule module = ModuleFactory.getInsuranceModule();
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition open = new Condition();
        open.setField(statusField);
        open.setOperator(LookupOperator.LOOKUP);
        open.setCriteriaValue(getOpenStatusCriteria());

        return open;
    }

    public static Condition getExpiredInsurancesCondition() {
        FacilioModule module = ModuleFactory.getInsuranceModule();
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition open = new Condition();
        open.setField(statusField);
        open.setOperator(LookupOperator.LOOKUP);
        open.setCriteriaValue(getCloseStatusCriteria());

        return open;
    }

    public static Criteria getOpenStatusCriteria() {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("typeCode");
        statusTypeField.setColumnName("STATUS_TYPE");
        statusTypeField.setDataType(FieldType.NUMBER);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusOpen = new Condition();
        statusOpen.setField(statusTypeField);
        statusOpen.setOperator(NumberOperators.EQUALS);
        statusOpen.setValue(String.valueOf(FacilioStatus.StatusType.OPEN.getIntVal()));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusOpen);

        return criteria;
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        String appName=FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INSURANCE);
        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNameVsPage.put(appName, buildInsurancePage(app,module,false,true));
        return appNameVsPage;
    }
    private List<PagesContext> buildInsurancePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName() + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";

        return  new ModulePages()
                .addPage(pageName, pageDisplayName,"", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("insurancedetails", "Insurance Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0,  null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }
    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("comments", "Comments", "")
                .addWidget("commentwidget", "Comments", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField validFromField = moduleBean.getField("validFrom", moduleName);
        FacilioField validTillField = moduleBean.getField("validTill", moduleName);
        FacilioField vendorField = moduleBean.getField("vendor", moduleName);
        FacilioField insuranceField = moduleBean.getField("insurance", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField createdTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField modifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField modifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, vendorField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, validFromField,1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, validTillField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, insuranceField,1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, createdByField,2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, createdTimeField,2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, modifiedByField,2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, modifiedTimeField,2, 4, 1);

        widgetGroup.setName("insuranceDeatils");
        widgetGroup.setDisplayName("");
        widgetGroup.setColumns(4);
        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);
        return FieldUtil.getAsJSON(pageWidget);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);
            if(widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            }
            else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }


    public static Criteria getCloseStatusCriteria() {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("typeCode");
        statusTypeField.setColumnName("STATUS_TYPE");
        statusTypeField.setDataType(FieldType.NUMBER);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusClose = new Condition();
        statusClose.setField(statusTypeField);
        statusClose.setOperator(NumberOperators.EQUALS);
        statusClose.setValue(String.valueOf(FacilioStatus.StatusType.CLOSED.getIntVal()));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusClose);

        return criteria;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule insuranceModule = modBean.getModule(FacilioConstants.ContextNames.INSURANCE);

        FacilioForm insuranceForm = new FacilioForm();
        insuranceForm.setDisplayName("INSURANCE");
        insuranceForm.setName("default_insurance_web");
        insuranceForm.setModule(insuranceModule);
        insuranceForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        insuranceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> insuranceFormFields = new ArrayList<>();
        insuranceFormFields.add(new FormField("companyName", FacilioField.FieldDisplayType.TEXTBOX, "Company Name", FormField.Required.REQUIRED, 1, 1));
        insuranceFormFields.add(new FormField("validFrom", FacilioField.FieldDisplayType.DATE, "Valid From", FormField.Required.OPTIONAL, 2, 1));
        insuranceFormFields.add(new FormField("validTill", FacilioField.FieldDisplayType.DATE, "Valid Till", FormField.Required.OPTIONAL, 3, 1));
        FormField vendorField = new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED,"vendors", 3, 2);
        insuranceFormFields.add(vendorField);
        insuranceFormFields.add(new FormField("insurance", FacilioField.FieldDisplayType.FILE, "Insurance", FormField.Required.OPTIONAL, 1, 1));

        FormSection insuranceFormSection = new FormSection("Default", 1, insuranceFormFields, false);
        insuranceFormSection.setSectionType(FormSection.SectionType.FIELDS);
        insuranceForm.setSections(Collections.singletonList(insuranceFormSection));
        insuranceForm.setIsSystemForm(true);
        insuranceForm.setType(FacilioForm.Type.FORM);

        FacilioForm portalInsuranceForm = new FacilioForm();
        portalInsuranceForm.setDisplayName("INSURANCE");
        portalInsuranceForm.setName("default_insurance_portal");
        portalInsuranceForm.setModule(insuranceModule);
        portalInsuranceForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        portalInsuranceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> portalInsuranceFormFields = new ArrayList<>();
        portalInsuranceFormFields.add(new FormField("companyName", FacilioField.FieldDisplayType.TEXTBOX, "Company Name", FormField.Required.REQUIRED, 1, 1));
        portalInsuranceFormFields.add(new FormField("validFrom", FacilioField.FieldDisplayType.DATE, "Valid From", FormField.Required.OPTIONAL, 2, 1));
        portalInsuranceFormFields.add(new FormField("validTill", FacilioField.FieldDisplayType.DATE, "Valid Till", FormField.Required.OPTIONAL, 3, 1));
        portalInsuranceFormFields.add(new FormField("insurance", FacilioField.FieldDisplayType.FILE, "Insurance", FormField.Required.OPTIONAL, 1, 1));
//        portalInsuranceForm.setFields(portalInsuranceFormFields);

        FormSection portalInsuranceFormSection = new FormSection("Default", 1, portalInsuranceFormFields, false);
        portalInsuranceFormSection.setSectionType(FormSection.SectionType.FIELDS);
        portalInsuranceForm.setSections(Collections.singletonList(portalInsuranceFormSection));
        portalInsuranceForm.setIsSystemForm(true);
        portalInsuranceForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> insuranceModuleForms = new ArrayList<>();
        insuranceModuleForms.add(insuranceForm);
        insuranceModuleForms.add(portalInsuranceForm);

        return insuranceModuleForms;
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("validFrom");
        fieldNames.add("validTill");
        fieldNames.add("vendor");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
