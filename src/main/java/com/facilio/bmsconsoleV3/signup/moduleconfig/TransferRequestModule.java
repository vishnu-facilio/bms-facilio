package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.TransferRequestTemplatePage;
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
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class TransferRequestModule extends BaseModuleConfig{
    public TransferRequestModule(){
        setModuleName(FacilioConstants.ContextNames.TRANSFER_REQUEST);
    }

    public void addData() throws Exception {
        addSystemButtons();
    }

    private static void addSystemButtons() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> trFields = modBean.getAllFields(FacilioConstants.ContextNames.TRANSFER_REQUEST);
        Map<String,FacilioField> trFieldMap = FieldFactory.getAsMap(trFields);

        SystemButtonRuleContext stageRequest = new SystemButtonRuleContext();
        stageRequest.setName("Stage Request");
        stageRequest.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        stageRequest.setIdentifier("stageRequest");
        stageRequest.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria stageRequestBtnCriteria = new Criteria();
        stageRequestBtnCriteria.addAndCondition(CriteriaAPI.getCondition(trFieldMap.get("isStaged"),"false", BooleanOperators.IS));
        stageRequest.setCriteria(stageRequestBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TRANSFER_REQUEST,stageRequest);

        SystemButtonRuleContext shipRequest = new SystemButtonRuleContext();
        shipRequest.setName("Ship Request");
        shipRequest.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        shipRequest.setIdentifier("shipRequest");
        shipRequest.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria shipRequestBtnCriteria = new Criteria();
        shipRequestBtnCriteria.addAndCondition(CriteriaAPI.getCondition(trFieldMap.get("isStaged"),"true", BooleanOperators.IS));
        shipRequestBtnCriteria.addAndCondition(CriteriaAPI.getCondition(trFieldMap.get("isShipped"),"false", BooleanOperators.IS));
        shipRequestBtnCriteria.addAndCondition(CriteriaAPI.getCondition(trFieldMap.get("isShipmentTrackingNeeded"),"true", BooleanOperators.IS));
        shipRequest.setCriteria(shipRequestBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TRANSFER_REQUEST,shipRequest);

        SystemButtonRuleContext completeRequest = new SystemButtonRuleContext();
        completeRequest.setName("Complete Request");
        completeRequest.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        completeRequest.setIdentifier("completeRequest");
        completeRequest.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria completeRequestBtnCriteria = new Criteria();
        completeRequestBtnCriteria.addAndCondition(CriteriaAPI.getCondition(trFieldMap.get("isStaged"),"true", BooleanOperators.IS));
        completeRequestBtnCriteria.addAndCondition(CriteriaAPI.getCondition(trFieldMap.get("isShipped"),"true", BooleanOperators.IS));
        completeRequestBtnCriteria.addAndCondition(CriteriaAPI.getCondition(trFieldMap.get("isCompleted"),"false", BooleanOperators.IS));
        completeRequest.setCriteria(completeRequestBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TRANSFER_REQUEST,completeRequest);

        SystemButtonRuleContext goToShipment = new SystemButtonRuleContext();
        goToShipment.setName("Go To Shipment");
        goToShipment.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        goToShipment.setIdentifier("goToShipment");
        goToShipment.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        Criteria goToShipmentBtnCriteria = new Criteria();
        goToShipmentBtnCriteria.addAndCondition(CriteriaAPI.getCondition(trFieldMap.get("isShipped"),"true", BooleanOperators.IS));
        goToShipment.setCriteria(goToShipmentBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TRANSFER_REQUEST,goToShipment);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit");
        edit.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        edit.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        edit.setPermissionRequired(true);
        Criteria editBtnCriteria = new Criteria();
        editBtnCriteria.addAndCondition(CriteriaAPI.getCondition(trFieldMap.get("isStaged"),"false", BooleanOperators.IS));
        edit.setCriteria(editBtnCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TRANSFER_REQUEST,edit);

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> transferRequest = new ArrayList<FacilioView>();
        transferRequest.add(getAllTransferRequestView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TRANSFER_REQUEST);
        groupDetails.put("views", transferRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTransferRequestView() {

        FacilioModule module = ModuleFactory.getTransferRequestModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Transfer Requests");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

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
        FacilioModule transferRequestModule = modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST);

        FacilioForm transferRequestForm = new FacilioForm();
        transferRequestForm.setDisplayName("TRANSFER REQUEST");
        transferRequestForm.setName("default_transferrequest_web");
        transferRequestForm.setModule(transferRequestModule);
        transferRequestForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        transferRequestForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> transferRequestFormDefaultFields = new ArrayList<>();
        transferRequestFormDefaultFields.add(new FormField("requestSubject", FacilioField.FieldDisplayType.TEXTBOX, "Request Subject", FormField.Required.REQUIRED, 1, 1));
        transferRequestFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        transferRequestFormDefaultFields.add(new FormField("transferFromStore", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Transfer from Store", FormField.Required.REQUIRED, "storeRoom", 3, 2));
        transferRequestFormDefaultFields.add(new FormField("transferToStore", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Transfer to Store", FormField.Required.REQUIRED, "storeRoom", 3, 3));
        transferRequestFormDefaultFields.add(new FormField("transferInitiatedOn", FacilioField.FieldDisplayType.DATE, "Transfer Date", FormField.Required.OPTIONAL, 4, 2));
        transferRequestFormDefaultFields.add(new FormField("expectedCompletionDate", FacilioField.FieldDisplayType.DATE, "Expected Arrival Date", FormField.Required.OPTIONAL, 4, 3));
        transferRequestFormDefaultFields.add(new FormField("transferredBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Transferred By", FormField.Required.OPTIONAL, "people", 5, 2));
        transferRequestFormDefaultFields.add(new FormField("isShipmentTrackingNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Is Shipment Tracking Needed", FormField.Required.OPTIONAL, 5, 3));

        List<FormField> lineItemFields = new ArrayList<>();
        lineItemFields.add(new FormField("transferrequestlineitems", FacilioField.FieldDisplayType.INVREQUEST_LINE_ITEMS, "LINE ITEMS", FormField.Required.REQUIRED, 6, 1));

        List<FormField> transferRequestFormFields = new ArrayList<>();
        transferRequestFormFields.addAll(transferRequestFormDefaultFields);
        transferRequestFormFields.addAll(lineItemFields);


        FormSection defaultSection = new FormSection("Inventory Request", 1, transferRequestFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("Line Items", 2, lineItemFields, true);
        lineItemSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(lineItemSection);

        transferRequestForm.setSections(sections);
        transferRequestForm.setIsSystemForm(true);
        transferRequestForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(transferRequestForm);
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
            appNameVsPage.put(appName,getTransferRequestViewPage(app, module));
        }
        return appNameVsPage;
    }
    private List<PagesContext> getTransferRequestViewPage(ApplicationContext app, FacilioModule module) throws Exception {

        return new ModulePages()
                .addPage("transferRequestViewPage", "Default Transfer Request View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("transferrequestsummaryfields", null, null)
                .addWidget("transferrequestsummaryFieldsWidget", "Transfer Requests",  PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, TransferRequestTemplatePage.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("summaryDetailsCard",null,null)
                .addWidget("transferrequestcard1","",PageWidget.WidgetType.TRANSFER_REQUEST_DETAILS_CARD,"webtransferrequestitemlist_2_4",0,0,TransferRequestTemplatePage.getSummaryCardDetails("Transfer from store","transferFromStore.name","Transfer to store","transferToStore.name",false),null)
                .widgetDone()
                .addWidget("transferrequestcard2","",PageWidget.WidgetType.TRANSFER_REQUEST_DETAILS_CARD,"webtransferrequestitemlist_2_4",4,0,TransferRequestTemplatePage.getSummaryCardDetails("Transfer initiated on","transferInitiatedOn","Expected arrival date","expectedCompletionDate",true),null)
                .widgetDone()
                .addWidget("transferrequestcard3","",PageWidget.WidgetType.TRANSFER_REQUEST_DETAILS_CARD,"webtransferrequestitemlist_2_4",8,0,TransferRequestTemplatePage.getSummaryCardDetails("Transferred by","transferredBy.name","Created By","sysCreatedBy.name",false),null)
                .widgetDone()
                .sectionDone()
                .addSection("transferItemListItem", null, null)
                .addWidget("transferItemList", "Line Items", PageWidget.WidgetType.LINE_ITEMS_LIST,"flexiblewebtransferitemlist_6", 0, 0,  TransferRequestTemplatePage.getLineItemListParams(), null)
                .widgetDone()
                .sectionDone()
                .addSection("transferrequestwidgetGroup", null,  null)
                .addWidget("transferrequestcommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, TransferRequestTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }

}
