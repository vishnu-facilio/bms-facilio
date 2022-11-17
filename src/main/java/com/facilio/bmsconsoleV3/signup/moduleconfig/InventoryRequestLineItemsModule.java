package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CustomPageWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;

import java.util.*;

public class InventoryRequestLineItemsModule extends BaseModuleConfig{
    public InventoryRequestLineItemsModule(){
        setModuleName(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
    }

    @Override
    public void addData() throws Exception {
        super.addData();
        ArrayList<String> apps = new ArrayList<>();
        if (!SignupUtil.maintenanceAppSignup()) {
            apps.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        apps.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        for(String appLinkName: apps) {
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule inventoryRequestLineItemModule = moduleBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);

            CustomPageWidget pageWidget1 = new CustomPageWidget();
            SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();

            SummaryWidgetGroupFields groupField11 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField12 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField13 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField14 = new SummaryWidgetGroupFields();

            groupField11.setName(sysCreatedByField.getName());
            groupField11.setDisplayName(sysCreatedByField.getDisplayName());
            groupField11.setFieldId(sysCreatedByField.getId());
            groupField11.setRowIndex(1);
            groupField11.setColIndex(1);
            groupField11.setColSpan(2);

            groupField12.setName(sysCreatedTimeField.getName());
            groupField12.setDisplayName(sysCreatedTimeField.getDisplayName());
            groupField12.setFieldId(sysCreatedTimeField.getId());
            groupField12.setRowIndex(1);
            groupField12.setColIndex(3);
            groupField12.setColSpan(2);

            groupField13.setName(sysModifiedByField.getName());
            groupField13.setDisplayName(sysModifiedByField.getDisplayName());
            groupField13.setFieldId(sysModifiedByField.getId());
            groupField13.setRowIndex(2);
            groupField13.setColIndex(1);
            groupField13.setColSpan(2);

            groupField14.setName(sysModifiedTimeField.getName());
            groupField14.setDisplayName(sysModifiedTimeField.getDisplayName());
            groupField14.setFieldId(sysModifiedTimeField.getId());
            groupField14.setRowIndex(2);
            groupField14.setColIndex(3);
            groupField14.setColSpan(2);

            List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
            groupOneFields.add(groupField11);
            groupOneFields.add(groupField12);
            groupOneFields.add(groupField13);
            groupOneFields.add(groupField14);

            widgetGroup1.setName("moreDetails");
            widgetGroup1.setDisplayName("Additional Details");
            widgetGroup1.setColumns(4);
            widgetGroup1.setFields(groupOneFields);

            List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
            widgetGroupList.add(widgetGroup1);

            pageWidget1.setName("inventoryRequestLineItemsSummaryWidget");
            pageWidget1.setDisplayName("Inventory Request Line Items Summary");
            pageWidget1.setModuleId(inventoryRequestLineItemModule.getModuleId());
            pageWidget1.setAppId(ApplicationApi.getApplicationIdForLinkName(appLinkName));
            pageWidget1.setGroups(widgetGroupList);

            SummaryWidgetUtil.addPageWidget(pageWidget1);
        }
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventoryRequestLineItems = new ArrayList<FacilioView>();
        inventoryRequestLineItems.add(getAllInventoryRequestLineItemsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
        groupDetails.put("views", inventoryRequestLineItems);
        groupVsViews.add(groupDetails);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        groupDetails.put("appLinkNames", appLinkNames);

        return groupVsViews;
    }

    private static FacilioView getAllInventoryRequestLineItemsView() {
        FacilioModule invReqLineItems = ModuleFactory.getInventoryRequestLineItemsModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inventory Request Line Items");
        allView.setModuleName(invReqLineItems.getName());
        allView.setSortFields(sortFields);
        allView.setFields(getAllViewColumns());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("itemType","Item"));
        columns.add(new ViewField("description","Description","itemType"));
        columns.add(new ViewField("storeRoom","Storeroom"));
        columns.add(new ViewField("quantity","Quantity"));
//        columns.add(new ViewField("reservationType","Reservation"));
        return columns;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inventoryRequestLineItemModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);

        FacilioForm inventoryRequestLineItemsForm = new FacilioForm();
        inventoryRequestLineItemsForm.setDisplayName("Inventory Request Line Items");
        inventoryRequestLineItemsForm.setName("default_"+FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS+"_web");
        inventoryRequestLineItemsForm.setModule(inventoryRequestLineItemModule);
        inventoryRequestLineItemsForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        inventoryRequestLineItemsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> inventoryRequestLineItemsFormFields = new ArrayList<>();
        int seqNum = 0;
        inventoryRequestLineItemsFormFields.add(new FormField("itemType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Item", FormField.Required.REQUIRED,"itemTypes", ++seqNum, 1));
//        inventoryRequestLineItemsFormFields.add(new FormField("reservationType", FacilioField.FieldDisplayType.SELECTBOX, "Reservation Type", FormField.Required.REQUIRED, ++seqNum, 1));
        inventoryRequestLineItemsFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.NUMBER, "Quantity", FormField.Required.REQUIRED, ++seqNum, 1));

        inventoryRequestLineItemsForm.setFields(inventoryRequestLineItemsFormFields);
        FormSection section = new FormSection("Default", 1, inventoryRequestLineItemsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        inventoryRequestLineItemsForm.setSections(Collections.singletonList(section));
        inventoryRequestLineItemsForm.setIsSystemForm(true);
        inventoryRequestLineItemsForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(inventoryRequestLineItemsForm);
    }

}
