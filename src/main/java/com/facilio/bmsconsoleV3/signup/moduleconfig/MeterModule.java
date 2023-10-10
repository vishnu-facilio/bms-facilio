package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

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
        addMeterSubModules(meterModule);
        addSystemButtons();

    }

    public FacilioModule addMeterModule() throws Exception{

        ModuleBean moduleBean = Constants.getModBean();

        FacilioModule module = new FacilioModule("meter", "Meters", "Meters", FacilioModule.ModuleType.BASE_ENTITY, true);
        module.setDescription("The meter module offers a digital representation of your physical and virtual meters, enabling advanced monitoring, predictive maintenance, and data-driven insights.");

        List<FacilioField> fields = new ArrayList<>();

        StringField name = new StringField(module, "name", "Name", FacilioField.FieldDisplayType.TEXTBOX, "NAME", FieldType.STRING, true, false, true, true);
        fields.add(name);

        LookupField meterLocation = new LookupField(module, "meterLocation", "Meter Location", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "METER_LOCATION_ID", FieldType.LOOKUP, false, false, true, null, "Meter Location", moduleBean.getModule(FacilioConstants.ContextNames.BASE_SPACE));
        fields.add(meterLocation);

        StringField uniqueId = new StringField(module, "uniqueId", "Meter Identification Number", FacilioField.FieldDisplayType.TEXTBOX, "METER_ID_NUMBER", FieldType.STRING, false, false, true, null);
        fields.add(uniqueId);

        StringField qrVal = new StringField(module, "qrVal", "QR Value", FacilioField.FieldDisplayType.TEXTBOX, "QR_VALUE", FieldType.STRING, false, false, true, null);
        fields.add(qrVal);

        SystemEnumField meterType = (SystemEnumField) FieldFactory.getDefaultField("meterType", "Meter Type", "METER_TYPE", FieldType.SYSTEM_ENUM);
        meterType.setEnumName("MeterType");
        fields.add(meterType);

        LookupField utilityType = new LookupField(module, "utilityType", "Utility Type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "UTILITY_TYPE", FieldType.LOOKUP, false, false, true, null, "Utility Type", moduleBean.getModule(FacilioConstants.Meter.UTILITY_TYPE));
        fields.add(utilityType);

        StringField manufacturer = new StringField(module, "manufacturer", "Manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "MANUFACTURER", FieldType.STRING, false, false, true, null);
        fields.add(manufacturer);

        StringField model = new StringField(module, "model", "Model", FacilioField.FieldDisplayType.TEXTBOX, "MODEL", FieldType.STRING, false, false, true, null);
        fields.add(model);

        StringField serialNumber = new StringField(module, "serialNumber", "Serial Number", FacilioField.FieldDisplayType.TEXTBOX, "SERIAL_NUMBER", FieldType.STRING, false, false, true, null);
        fields.add(serialNumber);

        DateField purchasedDate = new DateField(module, "purchasedDate", "Purchased Date", FacilioField.FieldDisplayType.DATETIME, "PURCHASED_DATE", FieldType.DATE_TIME, false, false, true, null);
        fields.add(purchasedDate);

        DateField retireDate = new DateField(module, "retireDate", "Retire Date", FacilioField.FieldDisplayType.DATETIME, "RETIRE_DATE", FieldType.DATE_TIME, false, false, true, null);
        fields.add(retireDate);

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
        
        LookupField virtualMeterTemplate = new LookupField(module, "virtualMeterTemplate", "Virtual Meter Template", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "VIRTUAL_METER_TEMPLATE_ID", FieldType.LOOKUP, false, false, true, null, "Child Meters", moduleBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE));
        fields.add(virtualMeterTemplate);

        BooleanField isCheckMeter = new BooleanField(module, "isCheckMeter", "Is Check Meter", FacilioField.FieldDisplayType.DECISION_BOX, "IS_CHECK_METER", FieldType.BOOLEAN, false, false, true, null);
        fields.add(isCheckMeter);

        fields.add((FacilioField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME));

        fields.add((FacilioField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME));

        LookupField createdByPeople = FieldFactory.getDefaultField("sysCreatedByPeople","Created By","SYS_CREATED_BY",FieldType.LOOKUP);
        createdByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(createdByPeople);

        LookupField modifiedByPeople = FieldFactory.getDefaultField("sysModifiedByPeople","Modified By","SYS_MODIFIED_BY",FieldType.LOOKUP);
        modifiedByPeople.setLookupModule(Objects.requireNonNull(moduleBean.getModule(FacilioConstants.ContextNames.PEOPLE),"People module doesn't exists."));
        fields.add(modifiedByPeople);

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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
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
        MeterForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<FormField> MeterFormFields = new ArrayList<>();
        MeterFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        MeterFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        FormField utilityTypeField = new FormField("utilityType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Utility Type", FormField.Required.REQUIRED, "utilitytype", 3, 2);
        utilityTypeField.setIsDisabled(true);
        MeterFormFields.add(utilityTypeField);
        MeterFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 4, 2));
        MeterFormFields.add(new FormField("meterLocation", FacilioField.FieldDisplayType.SPACECHOOSER, "Meter Location", FormField.Required.REQUIRED, 5, 2));
        MeterFormFields.add(new FormField("isCheckMeter", FacilioField.FieldDisplayType.DECISION_BOX, "Is Check Meter", FormField.Required.OPTIONAL, 6, 2));

        FormSection section = new FormSection("Meter Details", 1, MeterFormFields, false);
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

    private void addMeterSubModules(FacilioModule meterModule) throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule meterRichText = constructMeterRichTextModule(meterModule);
        modules.add(meterRichText);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, false);
        addModuleChain.execute();

        addRichTextField(meterModule, meterRichText);
    }

    private FacilioModule constructMeterRichTextModule(FacilioModule meterModule) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Meter.METER_RICH_TEXT,
                "Meter Rich Text",
                "Large_Text_Values",
                FacilioModule.ModuleType.SUB_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parentId", "Parent ID", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(meterModule);
        fields.add(parentField);
        fields.add(FieldFactory.getDefaultField("fileId", "File ID", "FILE_ID", FieldType.NUMBER));

        module.setFields(fields);

        return module;
    }
    /**
     * creates description field for Meter
     **/
    private void addRichTextField(FacilioModule meterModule, FacilioModule richText) throws Exception {
        LargeTextField field = (LargeTextField) FieldFactory.getDefaultField("description", "Description", null, FieldType.LARGE_TEXT);
        field.setModule(meterModule);
        field.setRelModuleId(richText.getModuleId());

        Constants.getModBean().addField(field);
    }

    private static void addSystemButtons() throws Exception {
        SystemButtonRuleContext editMeter = new SystemButtonRuleContext();
        editMeter.setName("Edit");
        editMeter.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editMeter.setIdentifier("edit");
        editMeter.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.Meter.METER,editMeter);
    }

    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("name", "Name"));
        columns.add(new ViewField("utilityType", "Utility Type"));
        columns.add(new ViewField("meterType", "Meter Type"));
        columns.add(new ViewField("meterLocation", "Meter Location"));
        columns.add(new ViewField("siteId", "Site"));
        columns.add(new ViewField("isCheckMeter", "Is Check Meter"));
        columns.add(new ViewField("virtualMeterTemplate", "Virtual Meter Template"));

        return columns;

    }

    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String, List<PagesContext>> pageTemp = new HashMap<>();
        pageTemp.put(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, getSystemPage());
        pageTemp.put(FacilioConstants.ApplicationLinkNames.ENERGY_APP,getSystemPage());
        return  pageTemp;
    }
    private static List<PagesContext> getSystemPage() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        FacilioModule meterModule = modBean.getModule(FacilioConstants.Meter.METER);
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Meter.METER_ACTIVITY);
        List<PagesContext> MeterPages = new ArrayList<>();
        PagesContext physicalMeterSystemTypePage = new PagesContext("physicalmetersystemtypepage", "Physical Meter System Type Page", "Physical Meter page for System UtilityTypes", getPhysicalMeterSystemTypeCriteria(), false, false, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("metersummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("meterDetailsSection", null, null)
                .addWidget("meterDetailsWidget", "Meter Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getPhysicalMeterSummaryWidgetDetails(meterModule.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("commentsAndAttachmentsSection", null, null)
                .addWidget("commentsAndAttachmentsWidget", "Comments And Attachments", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getCommentsAttachmentsWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("monthlyConsumptionSection", null, null)
                .addWidget("monthlyConsumptionWidget", "Monthly Consumption", PageWidget.WidgetType.MONTHLY_CONSUMPTION, "webmonthlyconsumption_3_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("totalConsumptionSection", null, null)
                .addWidget("totalConsumptionWidget", "Yearly Consumption", PageWidget.WidgetType.TOTAL_CONSUMPTION, "webtotalconsumption_3_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("peakDemandSection", null, null)
                .addWidget("peakDemandWidget", "Peak Demand", PageWidget.WidgetType.PEAK_DEMAND, "webpeakDemamd_3_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterreadings", "Readings", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterReadingsSection", null, null)
                .addWidget("meterReadingsWidget", "Meter Readings", PageWidget.WidgetType.METER_READINGS, "flexiblewebmeterreadings_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterrelated", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(meterModule))
                .widgetDone()
                .sectionDone()
                .addSection("meterrelatedlist", "Related List", "List of related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 4, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(meterModule))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterhistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("historySection", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
        PagesContext physicalMeterCustomTypePage = new PagesContext("physicalmetercustomtypepage", "Physical Meter Custom Type Page", "Physical Meter page for Custom UtilityTypes", getPhysicalMeterCustomTypeCriteria(), false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("metersummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterDetailsSection", null, null)
                .addWidget("meterDetailsWidget", "Meter Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getPhysicalMeterSummaryWidgetDetails(meterModule.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("commentsAndAttachmentsSection", null, null)
                .addWidget("commentsAndAttachmentsWidget", "Comments And Attachments", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getCommentsAttachmentsWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterreadings", "Readings", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterReadingsSection", null, null)
                .addWidget("meterReadingsWidget", "Meter Readings", PageWidget.WidgetType.METER_READINGS, "flexiblewebmeterreadings_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterrelated", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(meterModule))
                .widgetDone()
                .sectionDone()
                .addSection("meterrelatedlist", "Related List", "List of related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 4, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(meterModule))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterhistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("historySection", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
        PagesContext virtualMeterSystemTypePage = new PagesContext("virtualmetersystemtypepage", "Virtual Meter System Type Page", "Virtual Meter page for System UtilityTypes", getVirtualMeterSystemTypeCriteria(), false, false, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("metersummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("meterDetailsSection", null, null)
                .addWidget("meterDetailsWidget", "Meter Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getVirtualMeterSummaryWidgetDetails(meterModule.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("commentsAndAttachmentsSection", null, null)
                .addWidget("commentsAndAttachmentsWidget", "Comments And Attachments", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getCommentsAttachmentsWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("monthlyConsumptionSection", null, null)
                .addWidget("monthlyConsumptionWidget", "Monthly Consumption", PageWidget.WidgetType.MONTHLY_CONSUMPTION, "webmonthlyconsumption_3_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("totalConsumptionSection", null, null)
                .addWidget("totalConsumptionWidget", "Yearly Consumption", PageWidget.WidgetType.TOTAL_CONSUMPTION, "webtotalconsumption_3_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("peakDemandSection", null, null)
                .addWidget("peakDemandWidget", "Peak Demand", PageWidget.WidgetType.PEAK_DEMAND, "webpeakDemamd_3_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterreadings", "Readings", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterReadingsSection", null, null)
                .addWidget("meterReadingsWidget", "Meter Readings", PageWidget.WidgetType.METER_READINGS, "flexiblewebmeterreadings_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterrelated", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(meterModule))
                .widgetDone()
                .sectionDone()
                .addSection("meterrelatedlist", "Related List", "List of related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 4, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(meterModule))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterhistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("historySection", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
        PagesContext virtualMeterCustomTypePage = new PagesContext("virtualmetercustomtypepage", "Virtual Meter Custom Type Page", "Virtual Meter page for Custom UtilityTypes", getVirtualMeterCustomTypeCriteria(), false, false, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("metersummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterDetailsSection", null, null)
                .addWidget("meterDetailsWidget", "Meter Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getVirtualMeterSummaryWidgetDetails(meterModule.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("commentsAndAttachmentsSection", null, null)
                .addWidget("commentsAndAttachmentsWidget", "Comments And Attachments", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getCommentsAttachmentsWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterreadings", "Readings", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterReadingsSection", null, null)
                .addWidget("meterReadingsWidget", "Meter Readings", PageWidget.WidgetType.METER_READINGS, "flexiblewebmeterreadings_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterrelated", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(meterModule))
                .widgetDone()
                .sectionDone()
                .addSection("meterrelatedlist", "Related List", "List of related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 4, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(meterModule))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("meterhistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("historySection", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
        MeterPages.add(physicalMeterSystemTypePage);
        MeterPages.add(physicalMeterCustomTypePage);
        MeterPages.add(virtualMeterSystemTypePage);
        MeterPages.add(virtualMeterCustomTypePage);
        return MeterPages;
    }
    private static Criteria getPhysicalMeterSystemTypeCriteria() {
        Criteria criteria = new Criteria();
        Condition meterTypeCondition = new Condition();
        meterTypeCondition.setFieldName("meterType");
        meterTypeCondition.setColumnName("Meters.METER_TYPE");
        meterTypeCondition.setOperator(EnumOperators.IS);
        meterTypeCondition.setValue(String.valueOf(V3MeterContext.MeterType.PHYSICAL.getVal()));
        meterTypeCondition.setModuleName(FacilioConstants.Meter.METER);
        criteria.addAndCondition(meterTypeCondition);
        Condition utilityTypeCon = new Condition();
        utilityTypeCon.setModuleName("meter");
        utilityTypeCon.setFieldName("utilityType");
        utilityTypeCon.setOperator(LookupOperator.LOOKUP);
        Condition condition = CriteriaAPI.getCondition("Utility_Type.IS_DEFAULT","isDefault","true" , BooleanOperators.IS);
        Criteria oneLevel = new Criteria();
        oneLevel.addAndCondition(condition);
        utilityTypeCon.setCriteriaValue(oneLevel);
        criteria.addAndCondition(utilityTypeCon);
        return criteria;
    }
    private static Criteria getPhysicalMeterCustomTypeCriteria() {
        Criteria criteria = new Criteria();
        Condition meterTypeCondition = new Condition();
        meterTypeCondition.setFieldName("meterType");
        meterTypeCondition.setColumnName("Meters.METER_TYPE");
        meterTypeCondition.setOperator(EnumOperators.IS);
        meterTypeCondition.setValue(String.valueOf(V3MeterContext.MeterType.PHYSICAL.getVal()));
        meterTypeCondition.setModuleName(FacilioConstants.Meter.METER);
        criteria.addAndCondition(meterTypeCondition);
        Condition utilityTypeCon = new Condition();
        utilityTypeCon.setModuleName("meter");
        utilityTypeCon.setFieldName("utilityType");
        utilityTypeCon.setOperator(LookupOperator.LOOKUP);
        Condition condition = CriteriaAPI.getCondition("Utility_Type.IS_DEFAULT","isDefault","false" , BooleanOperators.IS);
        Criteria oneLevel = new Criteria();
        oneLevel.addAndCondition(condition);
        utilityTypeCon.setCriteriaValue(oneLevel);
        criteria.addAndCondition(utilityTypeCon);
        return criteria;
    }
    private static Criteria getVirtualMeterSystemTypeCriteria() {
        Criteria criteria = new Criteria();
        Condition meterTypeCondition = new Condition();
        meterTypeCondition.setFieldName("meterType");
        meterTypeCondition.setColumnName("Meters.METER_TYPE");
        meterTypeCondition.setOperator(EnumOperators.IS);
        meterTypeCondition.setValue(String.valueOf(V3MeterContext.MeterType.VIRTUAL.getVal()));
        meterTypeCondition.setModuleName(FacilioConstants.Meter.METER);
        criteria.addAndCondition(meterTypeCondition);
        Condition utilityTypeCon = new Condition();
        utilityTypeCon.setModuleName("meter");
        utilityTypeCon.setFieldName("utilityType");
        utilityTypeCon.setOperator(LookupOperator.LOOKUP);
        Condition condition = CriteriaAPI.getCondition("Utility_Type.IS_DEFAULT","isDefault","true" , BooleanOperators.IS);
        Criteria oneLevel = new Criteria();
        oneLevel.addAndCondition(condition);
        utilityTypeCon.setCriteriaValue(oneLevel);
        criteria.addAndCondition(utilityTypeCon);
        return criteria;
    }
    private static Criteria getVirtualMeterCustomTypeCriteria() {
        Criteria criteria = new Criteria();
        Condition meterTypeCondition = new Condition();
        meterTypeCondition.setFieldName("meterType");
        meterTypeCondition.setColumnName("Meters.METER_TYPE");
        meterTypeCondition.setOperator(EnumOperators.IS);
        meterTypeCondition.setValue(String.valueOf(V3MeterContext.MeterType.VIRTUAL.getVal()));
        meterTypeCondition.setModuleName(FacilioConstants.Meter.METER);
        criteria.addAndCondition(meterTypeCondition);
        Condition utilityTypeCon = new Condition();
        utilityTypeCon.setModuleName("meter");
        utilityTypeCon.setFieldName("utilityType");
        utilityTypeCon.setOperator(LookupOperator.LOOKUP);
        Condition condition = CriteriaAPI.getCondition("Utility_Type.IS_DEFAULT","isDefault","false" , BooleanOperators.IS);
        Criteria oneLevel = new Criteria();
        oneLevel.addAndCondition(condition);
        utilityTypeCon.setCriteriaValue(oneLevel);
        criteria.addAndCondition(utilityTypeCon);
        return criteria;
    }
    private static JSONObject getPhysicalMeterSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField utilityType = moduleBean.getField("utilityType", moduleName);
        FacilioField meterType = moduleBean.getField("meterType", moduleName);
        FacilioField isCheckMeter = moduleBean.getField("isCheckMeter", moduleName);
        FacilioField description = moduleBean.getField("description", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, utilityType, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, meterType, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, isCheckMeter, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, description, 2, 1, 4);

        widgetGroup.setName("meterModuleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);


        FacilioField meterLocation = moduleBean.getField("meterLocation",moduleName);
        FacilioField site = moduleBean.getField("siteId",moduleName);

        SummaryWidgetGroup locationWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(locationWidgetGroup, meterLocation, 1, 1, 1);
        addSummaryFieldInWidgetGroup(locationWidgetGroup, site, 1, 2, 1);

        locationWidgetGroup.setName("locationDetails");
        locationWidgetGroup.setDisplayName("Location");
        locationWidgetGroup.setColumns(4);


        FacilioField manufacturer = moduleBean.getField("manufacturer", moduleName);
        FacilioField model = moduleBean.getField("model", moduleName);
        FacilioField serialNumber = moduleBean.getField("serialNumber", moduleName);
        FacilioField purchasedDate = moduleBean.getField("purchasedDate", moduleName);
        FacilioField retireDate = moduleBean.getField("retireDate", moduleName);

        SummaryWidgetGroup manufactureWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, manufacturer, 1, 1, 1);
        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, model, 1, 2, 1);
        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, serialNumber, 1, 3, 1);
        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, purchasedDate, 1, 4, 1);
        addSummaryFieldInWidgetGroup(manufactureWidgetGroup, retireDate, 2, 1, 1);

        manufactureWidgetGroup.setName("manufactureDetails");
        manufactureWidgetGroup.setDisplayName("Manufacture");
        manufactureWidgetGroup.setColumns(4);


        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedByPeople", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedBy = moduleBean.getField("sysModifiedByPeople", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetGroup systemInformationGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(systemInformationGroup, sysCreatedBy, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysCreatedTime, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysModifiedBy, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysModifiedTime, 1, 4, 1);

        systemInformationGroup.setName("systemDetails");
        systemInformationGroup.setDisplayName("System Information");
        systemInformationGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();

        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(locationWidgetGroup);
        widgetGroupList.add(manufactureWidgetGroup);
        widgetGroupList.add(systemInformationGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }
    private static JSONObject getVirtualMeterSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        FacilioField utilityType = moduleBean.getField("utilityType", moduleName);
        FacilioField site = moduleBean.getField("siteId",moduleName);
        FacilioField vmTemplate = moduleBean.getField("virtualMeterTemplate", moduleName);
        FacilioField meterType = moduleBean.getField("meterType", moduleName);
        FacilioField description = moduleBean.getField("description", moduleName);
        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, utilityType, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, site, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, vmTemplate, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, meterType, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, description, 2, 1, 4);
        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
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
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            if (field.getName().equals("meterLocation")) {
                summaryField.setDisplayName("Meter Location");
            }
            else {
                summaryField.setDisplayName(field.getDisplayName());
            }
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);
            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
    private static JSONObject getCommentsAttachmentsWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put(FacilioConstants.ContextNames.NOTES_MODULE_NAME, FacilioConstants.Meter.METER_NOTES);

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put(FacilioConstants.ContextNames.ATTACHMENTS_MODULE_NAME, FacilioConstants.Meter.METER_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comments", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}
