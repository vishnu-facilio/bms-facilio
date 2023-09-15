package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;

import java.util.*;

public class UtilityIntegrationMeterModule extends BaseModuleConfig {

    public UtilityIntegrationMeterModule(){
        setModuleName(FacilioConstants.UTILITY_INTEGRATION_METER);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule utilityIntegrationMeterModule = addUtilityIntegrationMeterModule();
        modules.add(utilityIntegrationMeterModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        addSummaryWidget(utilityIntegrationMeterModule);

    }
    public FacilioModule addUtilityIntegrationMeterModule() throws Exception{

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule("utilityIntegrationMeter", "Utility Integration Meter", "Utility_Integration_Meter",FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();


        LookupField utilityIntegrationCustomer = FieldFactory.getDefaultField("utilityIntegrationCustomer", "Utility Integration Customer ", "UTILITY_INTEGRATION_CUSTOMER_ID", FieldType.LOOKUP);
        utilityIntegrationCustomer.setLookupModule(moduleBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER));
        fields.add(utilityIntegrationCustomer);

//        NumberField utilityIntegrationCustomer = new NumberField(module, "utilityIntegrationCustomerId", "Utility Integration Customer Id", FacilioField.FieldDisplayType.NUMBER, "UTILITY_INTEGRATION_CUSTOMER_ID", FieldType.NUMBER, false, false, true, null);
//        fields.add(utilityIntegrationCustomer);

        DateField createdTime =  FieldFactory.getDefaultField("createdTime", "Authorization Submitted Time", "CREATED_TIME", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(createdTime);

        StringField meterUid = (StringField) FieldFactory.getDefaultField("meterUid", "Meter UID", "METER_UID", FieldType.STRING,true);
        fields.add(meterUid);

        StringField customerId = (StringField) FieldFactory.getDefaultField("customerUid", "Customer Id", "CUSTOMER_UID", FieldType.STRING);
        fields.add(customerId);


        StringField status = (StringField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.STRING);
        fields.add(status);

        StringField userEmail = (StringField) FieldFactory.getDefaultField("userEmail", "User Email", "USER_EMAIL", FieldType.STRING);
        fields.add(userEmail);

        NumberField userUid = (NumberField) FieldFactory.getDefaultField("userUid", "User UID", "USER_UID", FieldType.NUMBER);
        fields.add(userUid);

        BooleanField isArchieved = (BooleanField)FieldFactory.getDefaultField("isArchived", "Is Archived", "IS_ARCHIVED", FieldType.BOOLEAN);
        fields.add(isArchieved);

        BooleanField isActivated = (BooleanField)FieldFactory.getDefaultField("isActivated", "Is Activated", "IS_ACTIVATED", FieldType.BOOLEAN);
        fields.add(isActivated);

        StringField utilityID = (StringField) FieldFactory.getDefaultField("utilityID", "Utility ID", "UTILITY_ID", FieldType.STRING);
        fields.add(utilityID);

        NumberField billCount = (NumberField) FieldFactory.getDefaultField("billCount", "Bill Count", "BILL_COUNT", FieldType.NUMBER);
        fields.add(billCount);

        NumberField intervalCount = (NumberField) FieldFactory.getDefaultField("intervalCount", "Interval Count", "INTERVAL_COUNT", FieldType.NUMBER);
        fields.add(intervalCount);

        SystemEnumField meterState = (SystemEnumField) FieldFactory.getDefaultField("meterState", "Meter State", "METER_STATE", FieldType.SYSTEM_ENUM);
        meterState.setEnumName("MeterState");
        fields.add(meterState);

//        NumberField meterState = (NumberField) FieldFactory.getDefaultField("meterState", "Meter State", "METER_STATE", FieldType.NUMBER);
//        fields.add(meterState);



        StringField meta = (StringField) FieldFactory.getDefaultField("meta", "Meta", "META_JSON", FieldType.STRING);
        fields.add(meta);

        StringField frequency = (StringField) FieldFactory.getDefaultField("frequency", "Frequency", "FREQUENCY", FieldType.STRING);
        fields.add(frequency);


        DateField nextPrepay =  FieldFactory.getDefaultField("nextPrepay", "Next Prepay", "NEXT_PREPAY", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(nextPrepay);

        DateField nextRefresh =  FieldFactory.getDefaultField("nextRefresh", "Next Refresh", "NEXT_REFRESH", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(nextRefresh);


        StringField prepay = (StringField) FieldFactory.getDefaultField("prepay", "Prepay", "PREPAY", FieldType.STRING);
        fields.add(prepay);

        StringField serviceTariff = (StringField) FieldFactory.getDefaultField("serviceTariff", "Service Tariff", "SERVICE_TARIFF", FieldType.STRING);
        fields.add(serviceTariff);

        StringField serviceClass = (StringField) FieldFactory.getDefaultField("serviceClass", "Service Class Type", "SERVICE_CLASS", FieldType.STRING);
        fields.add(serviceClass);

        LookupField utilityIntegrationTariff = FieldFactory.getDefaultField("utilityIntegrationTariff", "Utility Integration Tariff ", "UTILITY_INTEGRATION_TARIFF", FieldType.LOOKUP);
        utilityIntegrationTariff.setLookupModule(moduleBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TARIFF));
        fields.add(utilityIntegrationTariff);

        LookupField meter = FieldFactory.getDefaultField("meter", "Meter", "METER", FieldType.LOOKUP);
        meter.setLookupModule(moduleBean.getModule(FacilioConstants.Meter.METER));
        fields.add(meter);

        module.setFields(fields);
        return module;
    }
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> utilityMeterModule = new ArrayList<FacilioView>();
        utilityMeterModule.add(getUtilityMeterViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.UTILITY_INTEGRATION_METER);
        groupDetails.put("views", utilityMeterModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getUtilityMeterViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "utilityIntegrationMeter.ID", FieldType.NUMBER), true));

        FacilioView utilityMeterView = new FacilioView();
        utilityMeterView.setName("all");
        utilityMeterView.setDisplayName("Utility Integration Meters");

        utilityMeterView.setModuleName(FacilioConstants.UTILITY_INTEGRATION_METER);
        utilityMeterView.setSortFields(sortFields);

        List<ViewField> utilityMeterViewFields = new ArrayList<>();

        utilityMeterViewFields.add(new ViewField("meterUid","Meter UID"));
        utilityMeterViewFields.add(new ViewField("customerUid","Customer Id"));
        utilityMeterViewFields.add(new ViewField("utilityID","Utility ID"));
        utilityMeterViewFields.add(new ViewField("isActivated","Is Activated"));
        utilityMeterViewFields.add(new ViewField("meterState","Meter State"));
            utilityMeterViewFields.add(new ViewField("serviceTariff","Service Tariff"));


        utilityMeterView.setFields(utilityMeterViewFields);

        return utilityMeterView;
    }

    public void addSummaryWidget(FacilioModule utilityIntegrationMeterModule) throws Exception {
        ArrayList<String> apps = new ArrayList<>();
        apps.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        apps.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);


        for (String app : apps) {
            ModuleBean moduleBean = Constants.getModBean();

            FacilioField createdTimeField = moduleBean.getField("createdTime", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField userEmailField = moduleBean.getField("userEmail", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField userUidField = moduleBean.getField("userUid", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField isArchivedField = moduleBean.getField("isArchived", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField intervalCountField = moduleBean.getField("intervalCount", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField frequencyField = moduleBean.getField("frequency", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField nextPrepayField = moduleBean.getField("nextPrepay", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField nextRefreshField = moduleBean.getField("nextRefresh", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField prepayField = moduleBean.getField("prepay", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.UTILITY_INTEGRATION_METER);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.UTILITY_INTEGRATION_METER);


            SummaryWidget widget = new SummaryWidget();

            SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
            SummaryWidgetGroupFields groupField1 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField2 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField3 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField5 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField6 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField7 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField8 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField9 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField10 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField11 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField12 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField13 = new SummaryWidgetGroupFields();


            groupField1.setName(createdTimeField.getName());
            groupField1.setDisplayName(createdTimeField.getDisplayName());
            groupField1.setFieldId(createdTimeField.getId());
            groupField1.setRowIndex(1);
            groupField1.setColIndex(1);
            groupField1.setColSpan(1);

            groupField2.setName(userEmailField.getName());
            groupField2.setDisplayName(userEmailField.getDisplayName());
            groupField2.setFieldId(userEmailField.getId());
            groupField2.setRowIndex(1);
            groupField2.setColIndex(2);
            groupField2.setColSpan(1);

            groupField3.setName(userUidField.getName());
            groupField3.setDisplayName(userUidField.getDisplayName());
            groupField3.setFieldId(userUidField.getId());
            groupField3.setRowIndex(1);
            groupField3.setColIndex(3);
            groupField3.setColSpan(1);

//            groupField4.setName(isArchivedField.getName());
//            groupField4.setDisplayName(isArchivedField.getDisplayName());
//            groupField4.setFieldId(isArchivedField.getId());
//            groupField4.setRowIndex(1);
//            groupField4.setColIndex(4);
//            groupField4.setColSpan(2);

            groupField5.setName(intervalCountField.getName());
            groupField5.setDisplayName(intervalCountField.getDisplayName());
            groupField5.setFieldId(intervalCountField.getId());
            groupField5.setRowIndex(1);
            groupField5.setColIndex(4);
            groupField5.setColSpan(1);

            groupField6.setName(frequencyField.getName());
            groupField6.setDisplayName(frequencyField.getDisplayName());
            groupField6.setFieldId(frequencyField.getId());
            groupField6.setRowIndex(2);
            groupField6.setColIndex(1);
            groupField6.setColSpan(1);

            groupField7.setName(nextPrepayField.getName());
            groupField7.setDisplayName(nextPrepayField.getDisplayName());
            groupField7.setFieldId(nextPrepayField.getId());
            groupField7.setRowIndex(2);
            groupField7.setColIndex(2);
            groupField7.setColSpan(1);

            groupField8.setName(nextRefreshField.getName());
            groupField8.setDisplayName(nextRefreshField.getDisplayName());
            groupField8.setFieldId(nextRefreshField.getId());
            groupField8.setRowIndex(2);
            groupField8.setColIndex(3);
            groupField8.setColSpan(1);

            groupField9.setName(prepayField.getName());
            groupField9.setDisplayName(prepayField.getDisplayName());
            groupField9.setFieldId(prepayField.getId());
            groupField9.setRowIndex(2);
            groupField9.setColIndex(4);
            groupField9.setColSpan(1);

            groupField10.setName(sysCreatedByField.getName());
            groupField10.setDisplayName(sysCreatedByField.getDisplayName());
            groupField10.setFieldId(sysCreatedByField.getId());
            groupField10.setRowIndex(3);
            groupField10.setColIndex(1);
            groupField10.setColSpan(1);

            groupField11.setName(sysCreatedTimeField.getName());
            groupField11.setDisplayName(sysCreatedTimeField.getDisplayName());
            groupField11.setFieldId(sysCreatedTimeField.getId());
            groupField11.setRowIndex(3);
            groupField11.setColIndex(2);
            groupField11.setColSpan(1);

            groupField12.setName(sysModifiedByField.getName());
            groupField12.setDisplayName(sysModifiedByField.getDisplayName());
            groupField12.setFieldId(sysModifiedByField.getId());
            groupField12.setRowIndex(3);
            groupField12.setColIndex(3);
            groupField12.setColSpan(1);

            groupField13.setName(sysModifiedTimeField.getName());
            groupField13.setDisplayName(sysModifiedTimeField.getDisplayName());
            groupField13.setFieldId(sysModifiedTimeField.getId());
            groupField13.setRowIndex(3);
            groupField13.setColIndex(4);
            groupField13.setColSpan(1);


            List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
            groupOneFields.add(groupField1);
            groupOneFields.add(groupField2);
            groupOneFields.add(groupField3);
            groupOneFields.add(groupField5);
            groupOneFields.add(groupField6);
            groupOneFields.add(groupField7);
            groupOneFields.add(groupField8);
            groupOneFields.add(groupField9);
            groupOneFields.add(groupField10);
            groupOneFields.add(groupField11);
            groupOneFields.add(groupField12);
            groupOneFields.add(groupField13);



            widgetGroup.setName("moreDetails");
            //widgetGroup.setDisplayName("More Details");
            widgetGroup.setColumns(4);
            widgetGroup.setFields(groupOneFields);

            List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
            widgetGroupList.add(widgetGroup);


            widget.setName("meterWidget");
            widget.setDisplayName("Meter Widget");
            widget.setModuleId(utilityIntegrationMeterModule.getModuleId());
            widget.setAppId(ApplicationApi.getApplicationIdForLinkName(app));
            widget.setGroups(widgetGroupList);


            SummaryWidgetUtil.addPageWidget(widget);
        }
    }

}

