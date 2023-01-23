package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
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
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventoryRequest = new ArrayList<FacilioView>();
        inventoryRequest.add(getAllInventoryRequestView().setOrder(order++));
        inventoryRequest.add(getInventoryRequestIssued("issued", "Issued" ,true).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        groupDetails.put("views", inventoryRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInventoryRequestView() {
        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getInventoryRequestModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getInventoryRequestIssued(String viewName, String viewDisplayName, boolean isIssued) {
        FacilioModule irModule = ModuleFactory.getInventoryRequestModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(irModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getIRIssuedCondition(true);

        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        statusView.setAppLinkNames(appLinkNames);
        return statusView;
    }

    private static Criteria getIRIssuedCondition(boolean isIssued) {

        FacilioField irStatusField = new FacilioField();
        irStatusField.setName("isIssued");
        irStatusField.setColumnName("IS_ISSUED");
        irStatusField.setDataType(FieldType.BOOLEAN);
        irStatusField.setModule(ModuleFactory.getInventoryRequestModule());

        Condition statusCond = new Condition();
        statusCond.setField(irStatusField);
        statusCond.setOperator(BooleanOperators.IS);
        statusCond.setValue(String.valueOf(isIssued));

        Criteria inventoryRequestStatusCriteria = new Criteria();
        inventoryRequestStatusCriteria.addAndCondition(statusCond);
        return inventoryRequestStatusCriteria;

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
        inventoryRequestModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> inventoryRequestModuleFormDefaultFields = new ArrayList<>();
        inventoryRequestModuleFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        inventoryRequestModuleFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requestedTime", FacilioField.FieldDisplayType.DATE, "Requested Date", FormField.Required.OPTIONAL, 3, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requiredTime", FacilioField.FieldDisplayType.DATE, "Required Date", FormField.Required.OPTIONAL, 3, 3));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requestedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested By", FormField.Required.OPTIONAL, "user", 4, 2));
        inventoryRequestModuleFormDefaultFields.add(new FormField("requestedFor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested For", FormField.Required.OPTIONAL, "user", 4, 3));
        inventoryRequestModuleFormDefaultFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", 5, 1));

        List<FormField> lineItemFields = new ArrayList<>();
        lineItemFields.add(new FormField("inventoryrequestlineitems", FacilioField.FieldDisplayType.INVREQUEST_LINE_ITEMS, "LINE ITEMS", FormField.Required.REQUIRED, 6, 1));

        List<FormField> inventoryRequestModuleFormFields = new ArrayList<>();
        inventoryRequestModuleFormFields.addAll(inventoryRequestModuleFormDefaultFields);
        inventoryRequestModuleFormFields.addAll(lineItemFields);

//        inventoryRequestModuleForm.setFields(inventoryRequestModuleFormFields);

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

        FacilioForm inventoryRequestWorkOrderForm = new FacilioForm();
        inventoryRequestWorkOrderForm.setDisplayName("INVENTORY REQUEST");
        inventoryRequestWorkOrderForm.setName("web_default");
        inventoryRequestWorkOrderForm.setModule(inventoryRequestModule);
        inventoryRequestWorkOrderForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        inventoryRequestWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> inventoryRequestWorkOrderFormDefaultFields = new ArrayList<>();
        inventoryRequestWorkOrderFormDefaultFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        inventoryRequestWorkOrderFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        inventoryRequestWorkOrderFormDefaultFields.add(new FormField("workOrder", FacilioField.FieldDisplayType.TEXTBOX, "Work Order", FormField.Required.REQUIRED, 3, 2));
        inventoryRequestWorkOrderFormDefaultFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", 3, 3));
        inventoryRequestWorkOrderFormDefaultFields.add(new FormField("requestedBy", FacilioField.FieldDisplayType.USER, "Requested By", FormField.Required.OPTIONAL, "requester", 4, 2));
        inventoryRequestWorkOrderFormDefaultFields.add(new FormField("requestedFor", FacilioField.FieldDisplayType.USER, "Requested For", FormField.Required.OPTIONAL, "requester", 4, 3));
        inventoryRequestWorkOrderFormDefaultFields.add(new FormField("requestedTime", FacilioField.FieldDisplayType.DATE, "Requested Date", FormField.Required.OPTIONAL, 5, 2));
        inventoryRequestWorkOrderFormDefaultFields.add(new FormField("requiredTime", FacilioField.FieldDisplayType.DATE, "Required Date", FormField.Required.OPTIONAL, 5, 3));

        List<FormField> lineItemFields1 = new ArrayList<>();
        lineItemFields1.add(new FormField("lineItems", FacilioField.FieldDisplayType.INVREQUEST_LINE_ITEMS, "LINE ITEMS", FormField.Required.REQUIRED, 6, 1));

        List<FormField> inventoryRequestModuleFormFields1 = new ArrayList<>();
        inventoryRequestModuleFormFields1.addAll(inventoryRequestWorkOrderFormDefaultFields);
        inventoryRequestModuleFormFields1.addAll(lineItemFields1);

//        inventoryRequestWorkOrderForm.setFields(inventoryRequestModuleFormFields1);

        FormSection defaultSection1 = new FormSection("Inventory Request", 1, inventoryRequestWorkOrderFormDefaultFields, true);
        defaultSection1.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection1 = new FormSection("Line Items", 2, lineItemFields1, true);
        lineItemSection1.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections1 = new ArrayList<>();
        sections1.add(defaultSection1);
        sections1.add(lineItemSection1);

        inventoryRequestWorkOrderForm.setSections(sections1);
        inventoryRequestWorkOrderForm.setIsSystemForm(true);
        inventoryRequestWorkOrderForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> inventoryRequestModuleForms = new ArrayList<>();
        inventoryRequestModuleForms.add(inventoryRequestModuleForm);
        inventoryRequestModuleForms.add(inventoryRequestWorkOrderForm);

        return inventoryRequestModuleForms;
    }
}
