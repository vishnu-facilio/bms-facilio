package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class InventoryRequestModule extends BaseModuleConfig{
    public InventoryRequestModule(){
        setModuleName(FacilioConstants.ContextNames.INVENTORY_REQUEST);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventoryRequest = new ArrayList<FacilioView>();
        inventoryRequest.add(getAllInventoryRequestView().setOrder(order++));
        inventoryRequest.add(getPendingInventoryRequestView().setOrder(order++));
        inventoryRequest.add(getPartiallyReservedInventoryRequestView().setOrder(order++));
        inventoryRequest.add(getFullyReservedInventoryRequestView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        groupDetails.put("views", inventoryRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInventoryRequestView() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryRequestModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        allView.setFields(getAllViewColumns());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getPendingInventoryRequestView() throws Exception {
        FacilioView pendingInvReqView = new FacilioView();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryRequestModule());

        pendingInvReqView.setName("pending");
        pendingInvReqView.setDisplayName("Pending");
        pendingInvReqView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        pendingInvReqView.setFields(getAllViewColumns());
        pendingInvReqView.setCriteria(getPendingInventoryRequestViewCriteria());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        pendingInvReqView.setAppLinkNames(appLinkNames);

        return pendingInvReqView;
    }

    private static Criteria getPendingInventoryRequestViewCriteria() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria pendingInventoryRequestViewCriteria = new Criteria();
        FacilioField inventoryRequestReservationStatusField = modBean.getField("inventoryRequestReservationStatus", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        if(inventoryRequestReservationStatusField != null) {
            Condition pendingInventoryRequestViewCondition = new Condition();
            pendingInventoryRequestViewCondition.setField(inventoryRequestReservationStatusField);
            pendingInventoryRequestViewCondition.setOperator(NumberOperators.EQUALS);
            pendingInventoryRequestViewCondition.setValue(String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.PENDING.getIndex()));
            pendingInventoryRequestViewCriteria.addAndCondition(pendingInventoryRequestViewCondition);
        }
        return pendingInventoryRequestViewCriteria;
    }

    private static FacilioView getPartiallyReservedInventoryRequestView() throws Exception {
        FacilioView partiallyReservedInvReqView = new FacilioView();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryRequestModule());

        partiallyReservedInvReqView.setName("partiallyReserved");
        partiallyReservedInvReqView.setDisplayName("Partially Reserved");
        partiallyReservedInvReqView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        partiallyReservedInvReqView.setFields(getAllViewColumns());
        partiallyReservedInvReqView.setCriteria(getPartiallyReservedInventoryRequestViewCriteria());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        partiallyReservedInvReqView.setAppLinkNames(appLinkNames);

        return partiallyReservedInvReqView;
    }

    private static Criteria getPartiallyReservedInventoryRequestViewCriteria() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria partiallyReservedInventoryRequestViewCriteria = new Criteria();
        FacilioField inventoryRequestReservationStatusField = modBean.getField("inventoryRequestReservationStatus", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        if(inventoryRequestReservationStatusField != null) {
            Condition partiallyReservedInventoryRequestViewCondition = new Condition();
            partiallyReservedInventoryRequestViewCondition.setField(inventoryRequestReservationStatusField);
            partiallyReservedInventoryRequestViewCondition.setOperator(NumberOperators.EQUALS);
            partiallyReservedInventoryRequestViewCondition.setValue(String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.PARTIALLY_RESERVED.getIndex()));
            partiallyReservedInventoryRequestViewCriteria.addAndCondition(partiallyReservedInventoryRequestViewCondition);
        }
        return partiallyReservedInventoryRequestViewCriteria;
    }

    private static FacilioView getFullyReservedInventoryRequestView() throws Exception {
        FacilioView fullyReservedInvReqView = new FacilioView();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryRequestModule());

        fullyReservedInvReqView.setName("fullyReserved");
        fullyReservedInvReqView.setDisplayName("Fully Reserved");
        fullyReservedInvReqView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        fullyReservedInvReqView.setFields(getAllViewColumns());
        fullyReservedInvReqView.setCriteria(getFullyReservedInventoryRequestViewCriteria());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        fullyReservedInvReqView.setAppLinkNames(appLinkNames);

        return fullyReservedInvReqView;
    }

    private static Criteria getFullyReservedInventoryRequestViewCriteria() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria fullyReservedInventoryRequestViewCriteria = new Criteria();
        FacilioField inventoryRequestReservationStatusField = modBean.getField("inventoryRequestReservationStatus", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        if(inventoryRequestReservationStatusField != null) {
            Condition fullyReservedInventoryRequestViewCondition = new Condition();
            fullyReservedInventoryRequestViewCondition.setField(inventoryRequestReservationStatusField);
            fullyReservedInventoryRequestViewCondition.setOperator(NumberOperators.EQUALS);
            fullyReservedInventoryRequestViewCondition.setValue(String.valueOf(V3InventoryRequestContext.InventoryRequestReservationStatus.FULLY_RESERVED.getIndex()));
            fullyReservedInventoryRequestViewCriteria.addAndCondition(fullyReservedInventoryRequestViewCondition);
        }
        return fullyReservedInventoryRequestViewCriteria;
    }

    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("name","Name"));
        columns.add(new ViewField("inventoryRequestReservationStatus","Reservation Status"));
        columns.add(new ViewField("storeRoom","Store Room"));
        columns.add(new ViewField("workorder","Work Order"));
        columns.add(new ViewField("sysCreatedBy","Created By"));
        columns.add(new ViewField("sysCreatedTime","Created Time"));
        columns.add(new ViewField("sysModifiedBy","Modified By"));
        columns.add(new ViewField("sysModifiedTime","Modified Time"));
        return columns;
    }


    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inventoryRequestModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST);

        FacilioForm inventoryRequestModuleForm = new FacilioForm();
        inventoryRequestModuleForm.setDisplayName("INVENTORY REQUEST");
        inventoryRequestModuleForm.setName("default_inventoryrequest_web");
        inventoryRequestModuleForm.setModule(inventoryRequestModule);
        inventoryRequestModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        inventoryRequestModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> inventoryRequestModuleFormDefaultFields = new ArrayList<>();
        int seqNum = 0;
        inventoryRequestModuleFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, ++seqNum, 1));
        inventoryRequestModuleFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, ++seqNum, 1));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requestedTime", FacilioField.FieldDisplayType.DATE, "Requested Date", FormField.Required.OPTIONAL, ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requiredTime", FacilioField.FieldDisplayType.DATE, "Required Date", FormField.Required.OPTIONAL, ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requestedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested By", FormField.Required.OPTIONAL, "user", ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requestedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested For", FormField.Required.OPTIONAL, "user", ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.REQUIRED, "storeRoom", ++seqNum, 2));
//        inventoryRequestModuleFormDefaultFields.add(new FormField("transactionType", FacilioField.FieldDisplayType.SELECTBOX, "Transaction Type", FormField.Required.REQUIRED, ++seqNum, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Work Order", FormField.Required.REQUIRED, ++seqNum, 2));

        List<FormField> lineItemFields = new ArrayList<>();
        lineItemFields.add(new FormField("inventoryrequestlineitems", FacilioField.FieldDisplayType.INVREQUEST_LINE_ITEMS, "LINE ITEMS", FormField.Required.REQUIRED, ++seqNum, 1));

        List<FormField> inventoryRequestModuleFormFields = new ArrayList<>();
        inventoryRequestModuleFormFields.addAll(inventoryRequestModuleFormDefaultFields);
        inventoryRequestModuleFormFields.addAll(lineItemFields);

        FormSection defaultSection = new FormSection("Inventory Request", 1, inventoryRequestModuleFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("Line Items", 2, lineItemFields, true);
        lineItemSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(lineItemSection);

        inventoryRequestModuleForm.setSections(sections);
        inventoryRequestModuleForm.setIsSystemForm(true);
        inventoryRequestModuleForm.setType(FacilioForm.Type.FORM);
        return Collections.singletonList(inventoryRequestModuleForm);
    }
}
