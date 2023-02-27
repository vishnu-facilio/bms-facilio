package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.AddSubModulesSystemFieldsCommad;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.SummaryWidgetUtil;

import java.util.*;

public class VendorQuotesModule extends BaseModuleConfig{
    public VendorQuotesModule() {
        setModuleName(FacilioConstants.ContextNames.VENDOR_QUOTES);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vendorQuotesModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES);
        addVendorQuotesActivityModuleChain(vendorQuotesModule);
        addRfqDetailsSummaryWidget();
    }

    private void addRfqDetailsSummaryWidget() throws Exception {
        List<String> appLinkNamesForSummaryWidget = new ArrayList<>();
        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vendorQuotesModule = moduleBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES);
        for(String appLinkName : appLinkNamesForSummaryWidget) {
            if (vendorQuotesModule != null && vendorQuotesModule.getModuleId() > 0) {
                boolean skipCheck = appLinkName.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                ApplicationContext app = ApplicationApi.getApplicationForLinkName(appLinkName, skipCheck);
                if(app != null) {
                    CustomPageWidget widget = SummaryWidgetUtil.getAllWidgets(app.getId(), vendorQuotesModule.getModuleId());
                    if(widget == null) {
                        FacilioField rfqField = moduleBean.getField("requestForQuotation", FacilioConstants.ContextNames.VENDOR_QUOTES);
                        FacilioField nameField = moduleBean.getField("name", FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
                        FacilioField descriptionField = moduleBean.getField("description", FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
                        FacilioField storeRoomField = moduleBean.getField("storeRoom", FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
                        FacilioField expectedReplyDateField = moduleBean.getField("expectedReplyDate", FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
                        FacilioField requiredDateField = moduleBean.getField("requiredDate", FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
                        FacilioField vendorField = moduleBean.getField("vendor", FacilioConstants.ContextNames.VENDOR_QUOTES);

                        CustomPageWidget pageWidget = new CustomPageWidget();
                        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
                        SummaryWidgetGroupFields groupField11 = new SummaryWidgetGroupFields();
                        SummaryWidgetGroupFields groupField21 = new SummaryWidgetGroupFields();
                        SummaryWidgetGroupFields groupField31 = new SummaryWidgetGroupFields();
                        SummaryWidgetGroupFields groupField32 = new SummaryWidgetGroupFields();
                        SummaryWidgetGroupFields groupField33 = new SummaryWidgetGroupFields();
                        SummaryWidgetGroupFields groupField34 = new SummaryWidgetGroupFields();

                        groupField11.setName(nameField.getName());
                        groupField11.setDisplayName(nameField.getDisplayName());
                        groupField11.setFieldId(nameField.getId());
                        groupField11.setParentLookupFieldId(rfqField.getId());
                        groupField11.setRowIndex(1);
                        groupField11.setColIndex(1);
                        groupField11.setColSpan(4);

                        groupField21.setName(descriptionField.getName());
                        groupField21.setDisplayName(descriptionField.getDisplayName());
                        groupField21.setFieldId(descriptionField.getId());
                        groupField21.setParentLookupFieldId(rfqField.getId());
                        groupField21.setRowIndex(2);
                        groupField21.setColIndex(1);
                        groupField21.setColSpan(4);

                        groupField31.setName(storeRoomField.getName());
                        groupField31.setDisplayName(storeRoomField.getDisplayName());
                        groupField31.setFieldId(storeRoomField.getId());
                        groupField31.setParentLookupFieldId(rfqField.getId());
                        groupField31.setRowIndex(3);
                        groupField31.setColIndex(1);
                        groupField31.setColSpan(1);

                        groupField32.setName(expectedReplyDateField.getName());
                        groupField32.setDisplayName(expectedReplyDateField.getDisplayName());
                        groupField32.setFieldId(expectedReplyDateField.getId());
                        groupField32.setParentLookupFieldId(rfqField.getId());
                        groupField32.setRowIndex(3);
                        groupField32.setColIndex(2);
                        groupField32.setColSpan(1);

                        groupField33.setName(requiredDateField.getName());
                        groupField33.setDisplayName("Need By Date");
                        groupField33.setFieldId(requiredDateField.getId());
                        groupField33.setParentLookupFieldId(rfqField.getId());
                        groupField33.setRowIndex(3);
                        groupField33.setColIndex(3);
                        groupField33.setColSpan(1);

                        groupField34.setName(vendorField.getName());
                        groupField34.setDisplayName(vendorField.getDisplayName());
                        groupField34.setFieldId(vendorField.getId());
                        groupField34.setRowIndex(3);
                        groupField34.setColIndex(4);
                        groupField34.setColSpan(1);

                        List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
                        groupOneFields.add(groupField11);
                        groupOneFields.add(groupField21);
                        groupOneFields.add(groupField31);
                        groupOneFields.add(groupField32);
                        groupOneFields.add(groupField33);
                        groupOneFields.add(groupField34);

                        widgetGroup.setName("rfqDetails");
                        widgetGroup.setDisplayName("Request For Quotation Details");
                        widgetGroup.setColumns(4);
                        widgetGroup.setFields(groupOneFields);

                        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
                        widgetGroupList.add(widgetGroup);

                        pageWidget.setName("vendorQuotesRfqDetailsWidget");
                        pageWidget.setDisplayName("Request For Quotation Details Widget");
                        pageWidget.setModuleId(vendorQuotesModule.getModuleId());
                        pageWidget.setAppId(app.getId());
                        pageWidget.setGroups(widgetGroupList);

                        SummaryWidgetUtil.addPageWidget(pageWidget);
                    }
                }
            }
        }
    }
    private void addVendorQuotesActivityModuleChain(FacilioModule vendorQuotesModule) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vendorQuotesActivityModule = new FacilioModule(
                FacilioConstants.ContextNames.VENDOR_QUOTES_ACTIVITY,
                "Vendor Quote Activity",
                "Vendor_Quotes_Activity",
                FacilioModule.ModuleType.ACTIVITY);
        List<FacilioField> fields = new ArrayList<>();
        NumberField parentId = (NumberField) FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);
        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);
        fields.add(timefield);
        NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);
        LookupField doneBy = (LookupField) FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP, FacilioField.FieldDisplayType.LOOKUP_POPUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);
        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA);
        fields.add(info);
        vendorQuotesActivityModule.setFields(fields);
        try {
            FacilioChain addVendorQuotesActivityModuleChain = TransactionChainFactory.addSystemModuleChain();
            addVendorQuotesActivityModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(vendorQuotesActivityModule));
            addVendorQuotesActivityModuleChain.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        modBean.addSubModule(vendorQuotesModule.getModuleId(), vendorQuotesActivityModule.getModuleId());
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> vendorQuote = new ArrayList<FacilioView>();
        vendorQuote.add(getAllVendorQuotesView().setOrder(order++));
        vendorQuote.add(getAllVendorQuotesViewForVendorPortal().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VENDOR_QUOTES);
        groupDetails.put("views", vendorQuote);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        groupDetails.put("appLinkNames", appLinkNames);

        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllVendorQuotesView() {

        FacilioModule module = ModuleFactory.getVendorQuotesModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Vendor Quotes");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);
        allView.setFields(getAllVqViewFieldsForMaintenance());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAllVendorQuotesViewForVendorPortal() {
        FacilioModule module = ModuleFactory.getVendorQuotesModule();
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Vendor Quotes");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);
        allView.setFields(getAllVqViewFieldsForVendorPortal());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static List<ViewField> getAllVqViewFieldsForMaintenance() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("requestForQuotation", "Request For Quotation"));
        columns.add(new ViewField("vendor", "Vendor"));
        columns.add(new ViewField("expectedReplyDate", "Expected Reply Date"));
        columns.add(new ViewField("moduleState", "Module State"));
        columns.add(new ViewField("purchaseOrder", "Purchase Order"));
        columns.add(new ViewField("replyDate", "Reply Date"));
        columns.add(new ViewField("sysCreatedBy", "Created By"));
        columns.add(new ViewField("sysCreatedTime", "Created Time"));
        columns.add(new ViewField("sysModifiedBy", "Modified By"));
        columns.add(new ViewField("sysModifiedTime", "Modified Time"));
        return columns;
    }

    private static List<ViewField> getAllVqViewFieldsForVendorPortal() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("requestForQuotation", "Request For Quotation"));
        columns.add(new ViewField("expectedReplyDate", "Expected Reply Date"));
        columns.add(new ViewField("moduleState", "Module State"));
        columns.add(new ViewField("purchaseOrder", "Purchase Order"));
        columns.add(new ViewField("replyDate", "Reply Date"));
        columns.add(new ViewField("sysCreatedBy", "Created By"));
        columns.add(new ViewField("sysCreatedTime", "Created Time"));
        columns.add(new ViewField("sysModifiedBy", "Modified By"));
        columns.add(new ViewField("sysModifiedTime", "Modified Time"));
        return columns;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vendorQuotes = modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES);

        FacilioForm vendorQuotesForm = new FacilioForm();
        vendorQuotesForm.setDisplayName("VENDOR QUOTES");
        vendorQuotesForm.setName("default_vendorQuotes_web");
        vendorQuotesForm.setModule(vendorQuotes);
        vendorQuotesForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        vendorQuotesForm.setAppLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        vendorQuotesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> vendorQuotesFormFields = new ArrayList<>();

        FacilioField expRepDateField = null;
        try {
            expRepDateField = modBean.getField("expectedReplyDate", FacilioConstants.ContextNames.VENDOR_QUOTES);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("expectedReplyDate field not found");
        }
        FormField vendorField = new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED, "vendors", 1, 1).setAllowCreateOptions(true).setCreateFormName("vendors_form");
        vendorField.setIsDisabled(true);
        vendorQuotesFormFields.add(vendorField);
        vendorQuotesFormFields.add(new FormField("replyDate", FacilioField.FieldDisplayType.DATE, "Reply Date", FormField.Required.OPTIONAL, 2, 1));
        vendorQuotesFormFields.add(new FormField(expRepDateField.getId(),"expectedReplyDate", FacilioField.FieldDisplayType.DATE,"Expected Reply Date", FormField.Required.OPTIONAL,2,2,true));
//        vendorQuotesForm.setFields(vendorQuotesFormFields);

        FormSection vendorQuotesFormSection = new FormSection("Default", 1, vendorQuotesFormFields, false);
        vendorQuotesFormSection.setSectionType(FormSection.SectionType.FIELDS);
        vendorQuotesForm.setSections(Collections.singletonList(vendorQuotesFormSection));
        vendorQuotesForm.setIsSystemForm(true);
        vendorQuotesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(vendorQuotesForm);
    }
    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES);
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields vendorPortalApp = new ScopeVariableModulesFields();
        vendorPortalApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_vendor_user"));
        vendorPortalApp.setModuleId(module.getModuleId());
        vendorPortalApp.setFieldName("vendor");

        scopeConfigList = Arrays.asList(vendorPortalApp);
        return scopeConfigList;
    }
}
