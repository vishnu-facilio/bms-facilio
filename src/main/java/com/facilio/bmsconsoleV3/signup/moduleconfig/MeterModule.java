package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;

import java.util.*;

public class MeterModule extends BaseModuleConfig{

    public MeterModule() throws Exception {
        setModuleName(FacilioConstants.Meter.METER);
    }

    @Override
    public void addData() throws Exception {

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule meterModule = addMeterModule();
        modules.add(meterModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

        addModuleLocalId();


    }

    public FacilioModule addMeterModule() throws Exception{

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule resourceModule = moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE);

        FacilioModule module = new FacilioModule("meter", "Meters", "Meters", FacilioModule.ModuleType.BASE_ENTITY, resourceModule, true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField utilityType = new LookupField(module, "utilityType", "Utility Type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "UTILITY_TYPE", FieldType.LOOKUP, false, false, true, null, "Utility Type", moduleBean.getModule(FacilioConstants.Meter.UTILITY_TYPE));
        fields.add(utilityType);

        NumberField parentMeterId = new NumberField(module, "parentMeterId", "Parent Meter ID", FacilioField.FieldDisplayType.NUMBER, "PARENT_METER_ID", FieldType.NUMBER, false, false, true, null);
        fields.add(parentMeterId);

        LookupField servingTo = new LookupField(module, "servingTo", "Serving To", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "SERVING_LOCATION_ID", FieldType.LOOKUP, false, false, true, null, "Serving Location", moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        fields.add(servingTo);

        StringField manufacturer = new StringField(module, "manufacturer", "Manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "MANUFACTURER", FieldType.STRING, false, false, true, null);
        fields.add(manufacturer);

        StringField model = new StringField(module, "model", "Model", FacilioField.FieldDisplayType.TEXTBOX, "MODEL", FieldType.STRING, false, false, true, null);
        fields.add(model);

        StringField serialNumber = new StringField(module, "serialNumber", "Serial Number", FacilioField.FieldDisplayType.TEXTBOX, "SERIAL_NUMBER", FieldType.STRING, false, false, true, null);
        fields.add(serialNumber);

        StringField tagNumber = new StringField(module, "tagNumber", "Tag", FacilioField.FieldDisplayType.TEXTBOX, "TAG_NUMBER", FieldType.STRING, false, false, true, null);
        fields.add(tagNumber);

        StringField partNumber = new StringField(module, "partNumber", "Part No.", FacilioField.FieldDisplayType.TEXTBOX, "PART_NUMBER", FieldType.STRING, false, false, true, null);
        fields.add(partNumber);

        NumberField unitPrice = new NumberField(module, "unitPrice", "Unit Price", FacilioField.FieldDisplayType.NUMBER, "UNIT_PRICE", FieldType.NUMBER, false, false, true, null);
        fields.add(unitPrice);

        StringField supplier = new StringField(module, "supplier", "Supplier", FacilioField.FieldDisplayType.TEXTBOX, "SUPPLIER", FieldType.STRING, false, false, true, null);
        fields.add(supplier);

        DateField purchasedDate = new DateField(module, "purchasedDate", "Purchased Date", FacilioField.FieldDisplayType.DATETIME, "PURCHASED_DATE", FieldType.DATE_TIME, false, false, true, null);
        fields.add(purchasedDate);

        DateField retireDate = new DateField(module, "retireDate", "Retire Date", FacilioField.FieldDisplayType.DATETIME, "RETIRE_DATE", FieldType.DATE_TIME, false, false, true, null);
        fields.add(retireDate);

        DateField warrantyExpiryDate = new DateField(module, "warrantyExpiryDate", "Warranty Expiry Date", FacilioField.FieldDisplayType.DATETIME, "WARRANTY_EXPIRY_DATE", FieldType.DATE_TIME, false, false, true, null);
        fields.add(warrantyExpiryDate);

        NumberField localId = new NumberField(module, "localId", "ID", FacilioField.FieldDisplayType.NUMBER, "LOCAL_ID", FieldType.NUMBER, false, false, true, null);
        fields.add(localId);

        BooleanField isCommissioned = new BooleanField(module, "isCommissioned", "Is Commissioned", FacilioField.FieldDisplayType.DECISION_BOX, "IS_COMMISSIONED", FieldType.BOOLEAN, false, false, true, null);
        fields.add(isCommissioned);

        LookupField moduleState = new LookupField(module, "moduleState", "Module State", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "MODULE_STATE", FieldType.LOOKUP, false, false, true, null, "Module State", moduleBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        fields.add(moduleState);

        LookupField approvalStatus = new LookupField(module, "approvalStatus", "Approval State", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "APPROVAL_STATE", FieldType.LOOKUP, false, false, true, null, "Approval State", moduleBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS));
        fields.add(approvalStatus);

        NumberField approvalFlowId = new NumberField(module, "approvalFlowId", "Approval Flow Id", FacilioField.FieldDisplayType.NUMBER, "APPROVAL_FLOW_ID", FieldType.NUMBER, false, false, true, null);
        fields.add(approvalFlowId);

        BooleanField isVirtual = new BooleanField(module, "isVirtual", "Is Virtual", FacilioField.FieldDisplayType.DECISION_BOX, "IS_VIRTUAL", FieldType.BOOLEAN, false, false, true, null);
        fields.add(isVirtual);
        
        LookupField virtualMeterTemplate = new LookupField(module, "virtualMeterTemplate", "Virtual Meter Template", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "VIRTUAL_METER_TEMPLATE_ID", FieldType.LOOKUP, false, false, true, null, "Child Meters", moduleBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE));
        fields.add(virtualMeterTemplate);

        BooleanField isCheckMeter = new BooleanField(module, "isCheckMeter", "Is Check Meter", FacilioField.FieldDisplayType.DECISION_BOX, "IS_CHECK_METER", FieldType.BOOLEAN, false, false, true, null);
        fields.add(isCheckMeter);

        BooleanField isBillable = new BooleanField(module, "isBillable", "Is Billable", FacilioField.FieldDisplayType.DECISION_BOX, "IS_BILLABLE", FieldType.BOOLEAN, false, false, true, null);
        fields.add(isBillable);

        module.setFields(fields);
        return module;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails = new HashMap<>();

        int order = 1;
        ArrayList<FacilioView> meter = new ArrayList<FacilioView>();
        meter.add(getAllMetersView().setOrder(order++));
        meter.add(getMeters("Gas Meter").setOrder(order++));
        meter.add(getMeters("Water Meter").setOrder(order++));
        meter.add(getMeters("Electricity Meter").setOrder(order++));
        meter.add(getMeters("Heat Meter").setOrder(order++));
        meter.add(getMeters("BTU Meter").setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "meterviews");
        groupDetails.put("displayName", "Meter");
        groupDetails.put("views", meter);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllMetersView() throws Exception {
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Meters");
        allView.setSortFields(getSortFields(FacilioConstants.Meter.METER));
        allView.setFields(getAllViewColumns());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    public static List<SortField> getSortFields(String moduleName, FacilioModule...module) throws Exception {
        FacilioModule meterModule = Constants.getModBean().getModule(FacilioConstants.Meter.METER);

        List<SortField> fields = new ArrayList<>();
        switch (moduleName) {
            case FacilioConstants.Meter.METER:
                FacilioField localId = new FacilioField();
                localId.setName("localId");
                localId.setColumnName("LOCAL_ID");
                localId.setDataType(FieldType.NUMBER);
                localId.setModule(meterModule);

                fields = Arrays.asList(new SortField(localId, false));
                break;
            default:
                if (module.length > 0) {
                    FacilioField createdTime = new FacilioField();
                    createdTime.setName("sysCreatedTime");
                    createdTime.setDataType(FieldType.NUMBER);
                    createdTime.setColumnName("CREATED_TIME");
                    createdTime.setModule(module[0]);

                    fields = Arrays.asList(new SortField(createdTime, false));
                }
                break;
        }
        return fields;
    }

    private static FacilioView getMeters(String utilityType) throws Exception {
        FacilioModule meterModule = Constants.getModBean().getModule(FacilioConstants.Meter.METER);

        FacilioView meterView = new FacilioView();
        if (utilityType.equals("Gas Meter")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getUtilityTypeCondition(utilityType));
            meterView.setName("gas");
            meterView.setDisplayName("Gas Meters");
            meterView.setCriteria(criteria);
        } else if (utilityType.equals("Water Meter")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getUtilityTypeCondition(utilityType));
            meterView.setName("water");
            meterView.setDisplayName("Water Meters");
            meterView.setCriteria(criteria);
        } else if (utilityType.equals("Electricity Meter")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getUtilityTypeCondition(utilityType));
            meterView.setName("electricity");
            meterView.setDisplayName("Electricity Meters");
            meterView.setCriteria(criteria);
        } else if (utilityType.equals("Heat Meter")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getUtilityTypeCondition(utilityType));
            meterView.setName("heat");
            meterView.setDisplayName("Heat Meters");
            meterView.setCriteria(criteria);
        } else if (utilityType.equals("BTU Meter")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getUtilityTypeCondition(utilityType));
            meterView.setName("btu");
            meterView.setDisplayName("BTU Meters");
            meterView.setCriteria(criteria);
        }

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(meterModule);

        meterView.setSortFields(Arrays.asList(new SortField(localId, false)));
        meterView.setFields(getAllViewColumns());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        meterView.setAppLinkNames(appLinkNames);

        return meterView;
    }

    private static Condition getUtilityTypeCondition(String utilityType) throws Exception {
        FacilioModule meterModule = Constants.getModBean().getModule(FacilioConstants.Meter.METER);
        FacilioModule utilityTypeModule = Constants.getModBean().getModule(FacilioConstants.Meter.UTILITY_TYPE);

        LookupField statusField = new LookupField();
        statusField.setName("utilityType");
        statusField.setColumnName("UTILITY_TYPE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(meterModule);
        statusField.setLookupModule(utilityTypeModule);

        Condition open = new Condition();
        open.setField(statusField);
        open.setOperator(LookupOperator.LOOKUP);
        open.setCriteriaValue(getUtilityTypeCriteria(utilityType));

        return open;
    }

    private static Criteria getUtilityTypeCriteria(String utilityType) throws Exception {
        FacilioModule utilityTypeModule = Constants.getModBean().getModule(FacilioConstants.Meter.UTILITY_TYPE);

        FacilioField utilitytype = new FacilioField();
        utilitytype.setName("name");
        utilitytype.setColumnName("NAME");
        utilitytype.setDataType(FieldType.STRING);
        utilitytype.setModule(utilityTypeModule);

        Condition statusOpen = new Condition();
        statusOpen.setField(utilitytype);
        statusOpen.setOperator(StringOperators.IS);
        if (utilityType.equals("Gas Meter")) {
            statusOpen.setValue("Gas Meter");
        } else if (utilityType.equals("Water Meter")) {
            statusOpen.setValue("Water Meter");
        } else if (utilityType.equals("Electricity Meter")) {
            statusOpen.setValue("Electricity Meter");
        } else if (utilityType.equals("Heat Meter")) {
            statusOpen.setValue("Heat Meter");
        } else if (utilityType.equals("BTU Meter")) {
            statusOpen.setValue("BTU Meter");
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusOpen);

        return criteria;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule MeterModule = modBean.getModule(FacilioConstants.Meter.METER);

        FacilioForm MeterForm = new FacilioForm();
        MeterForm.setDisplayName("Standard");
        MeterForm.setName("default_meter_web");
        MeterForm.setModule(MeterModule);
        MeterForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        MeterForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> MeterFormFields = new ArrayList<>();
        MeterFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        MeterFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        MeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        FormField utilityTypeField = new FormField("utilityType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Type", FormField.Required.REQUIRED, "utilitytype", 4, 2);
        utilityTypeField.setIsDisabled(true);
        MeterFormFields.add(utilityTypeField);
        MeterFormFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Meter Location", FormField.Required.REQUIRED, 5, 2));
        MeterFormFields.add(new FormField("servingTo", FacilioField.FieldDisplayType.SPACECHOOSER, "Serving To", FormField.Required.REQUIRED, 5, 2));
        MeterFormFields.add(new FormField("manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "Manufacturer", FormField.Required.OPTIONAL, 6, 2));
        MeterFormFields.add(new FormField("supplier", FacilioField.FieldDisplayType.TEXTBOX, "Supplier", FormField.Required.OPTIONAL, 6, 3));
        MeterFormFields.add(new FormField("model", FacilioField.FieldDisplayType.TEXTBOX, "Model", FormField.Required.OPTIONAL, 7, 2));
        MeterFormFields.add(new FormField("serialNumber", FacilioField.FieldDisplayType.TEXTBOX, "Serial Number", FormField.Required.OPTIONAL, 7, 3));
        MeterFormFields.add(new FormField("tagNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tag", FormField.Required.OPTIONAL, 8, 2));
        MeterFormFields.add(new FormField("partNumber", FacilioField.FieldDisplayType.TEXTBOX, "Part No.", FormField.Required.OPTIONAL, 8, 3));
        MeterFormFields.add(new FormField("purchasedDate", FacilioField.FieldDisplayType.DATETIME, "Purchased Date", FormField.Required.OPTIONAL, 9, 2));
        MeterFormFields.add(new FormField("retireDate", FacilioField.FieldDisplayType.DATETIME, "Retire Date", FormField.Required.OPTIONAL, 9, 3));
        MeterFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.OPTIONAL, 10, 2));
        MeterFormFields.add(new FormField("warrantyExpiryDate", FacilioField.FieldDisplayType.DATETIME, "Warranty Expiry Date", FormField.Required.OPTIONAL, 10, 3));
        MeterFormFields.add(new FormField("qrVal", FacilioField.FieldDisplayType.TEXTBOX, "QR Value", FormField.Required.OPTIONAL, 11, 2));
        MeterFormFields.add(new FormField("isCheckMeter", FacilioField.FieldDisplayType.DECISION_BOX, "Is Check Meter", FormField.Required.OPTIONAL, 12, 2));
        MeterFormFields.add(new FormField("isBillable", FacilioField.FieldDisplayType.DECISION_BOX, "Is Billable", FormField.Required.OPTIONAL, 13, 2));


        FormSection section = new FormSection("Default", 1, MeterFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        MeterForm.setSections(Collections.singletonList(section));
        MeterForm.setIsSystemForm(true);
        MeterForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(MeterForm);
    }

    public void addModuleLocalId() throws Exception {

        FacilioModule moduleLocalIdModule = ModuleFactory.getModuleLocalIdModule();
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(moduleLocalIdModule.getTableName())
                .fields(FieldFactory.getModuleLocalIdFields());
        Map<String, Object> map = new HashMap<>();
        map.put("localId", 0);
        map.put("moduleName", "meter");
        insertBuilder.insert(map);

    }

    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("name", "Name"));
        columns.add(new ViewField("description", "Description"));
        columns.add(new ViewField("utilityType", "Utility Type"));
        columns.add(new ViewField("isVirtual", "Is Virtual"));
        columns.add(new ViewField("virtualMeterTemplate", "Virtual Meter Template"));
        columns.add(new ViewField("siteId", "Site"));
        columns.add(new ViewField("space", "Meter Location"));
        columns.add(new ViewField("isCheckMeter", "Is Check Meter"));
        columns.add(new ViewField("isBillable", "Is Billable"));
        columns.add(new ViewField("sysCreatedBy", "Created By"));
        columns.add(new ViewField("sysCreatedTime", "Created Time"));
        columns.add(new ViewField("sysModifiedBy", "Modified By"));
        columns.add(new ViewField("sysModifiedTime", "Modified Time"));

        return columns;

    }
}
