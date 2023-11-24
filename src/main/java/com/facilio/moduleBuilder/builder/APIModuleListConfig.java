package com.facilio.moduleBuilder.builder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.moduleBuilder.command.AddAutomationModuleBasedOnTabType;
import com.facilio.moduleBuilder.command.GetSubModulesForTransactionRuleCommand;
import com.facilio.v3.annotation.Config;

import java.util.Arrays;
import java.util.function.Supplier;

@Config
public class APIModuleListConfig {
    @Feature("slaModules")
    public static Supplier<ModuleListHandler> getSLAModules(){
        return () -> new ModuleListHandler()
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.SERVICE_REQUEST, Arrays.asList(FacilioConstants.ContextNames.SERVICE_REQUEST), null)
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.INSPECTION, Arrays.asList(FacilioConstants.Inspection.INSPECTION_RESPONSE), null)
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.FSM, Arrays.asList(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,
                        FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT), null)
                .add(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER))
                .fetchCustomModules()
                .done();

    }

    @Feature("transactionRule")
    public static Supplier<ModuleListHandler> getTransactionRuleModules(){
        return () -> new ModuleListHandler()
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.PURCHASE,Arrays.asList(FacilioConstants.ContextNames.PURCHASE_REQUEST,  FacilioConstants.ContextNames.PURCHASE_ORDER),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.QUOTATION,Arrays.asList(FacilioConstants.ContextNames.QUOTE),null )
                .add(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER))
                .fetchCustomModules()
                .afterFetch(new GetSubModulesForTransactionRuleCommand())
                .done();

    }

    @Feature("automation")
    public static Supplier<ModuleListHandler> getAutomationModules(){
        return () -> new ModuleListHandler()
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.VISITOR, Arrays.asList(FacilioConstants.ContextNames.VISITOR_LOG,
                        FacilioConstants.ContextNames.INVITE_VISITOR,FacilioConstants.ContextNames.BASE_VISIT,FacilioConstants.ContextNames.GROUP_VISITOR_INVITE), null)
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.CONTRACT,Arrays.asList(FacilioConstants.ContextNames.PURCHASE_CONTRACTS,
                        FacilioConstants.ContextNames.LABOUR_CONTRACTS,FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,FacilioConstants.ContextNames.WARRANTY_CONTRACTS),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.WORK_PERMIT,Arrays.asList(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.INVENTORY,Arrays.asList(FacilioConstants.ContextNames.INVENTORY_REQUEST),Arrays.asList(FacilioConstants.Induction.INDUCTION_RESPONSE))
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.PURCHASE,Arrays.asList(FacilioConstants.ContextNames.PURCHASE_REQUEST,
                        FacilioConstants.ContextNames.PURCHASE_ORDER),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.VENDOR,Arrays.asList(FacilioConstants.ContextNames.VENDORS,
                        FacilioConstants.ContextNames.INSURANCE,FacilioConstants.ContextNames.VENDOR_CONTACT),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.SERVICE_REQUEST,Arrays.asList(FacilioConstants.ContextNames.SERVICE_REQUEST),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.TENANTS,Arrays.asList(FacilioConstants.ContextNames.TENANT
                        ,FacilioConstants.ContextNames.TENANT_CONTACT, FacilioConstants.ContextNames.TENANT_UNIT_SPACE),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.BUDGET_MONITORING,Arrays.asList(FacilioConstants.ContextNames.BUDGET),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.FACILITY_BOOKING,Arrays.asList(FacilioConstants.ContextNames.FacilityBooking.FACILITY,
                        FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.QUOTATION,Arrays.asList(FacilioConstants.ContextNames.QUOTE),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.COMMUNITY,Arrays.asList(FacilioConstants.ContextNames.ANNOUNCEMENT),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.WORKPLACE_APPS,Arrays.asList(FacilioConstants.ContextNames.Floorplan.DESKS),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.SURVEY,Arrays.asList(FacilioConstants.Survey.SURVEY_RESPONSE),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.NEW_ALARMS,Arrays.asList(FacilioConstants.ContextNames.NEW_READING_ALARM,
                        FacilioConstants.ContextNames.BMS_ALARM),Arrays.asList(FacilioConstants.ContextNames.ALARM) )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.PM_PLANNER,Arrays.asList(FacilioConstants.ContextNames.JOB_PLAN,
                        FacilioConstants.ContextNames.PLANNEDMAINTENANCE),null )
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.FSM,Arrays.asList(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,
                        FacilioConstants.TimeOff.TIME_OFF,FacilioConstants.Territory.TERRITORY,FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, FacilioConstants.TimeSheet.TIME_SHEET, FacilioConstants.Trip.TRIP),null )
                .add(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER,FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.SITE,
                        FacilioConstants.ContextNames.FLOOR, FacilioConstants.ContextNames.BUILDING,
                        FacilioConstants.ContextNames.SPACE, FacilioConstants.PeopleGroup.PEOPLE_GROUP,
                        FacilioConstants.Meter.METER, FacilioConstants.Inspection.INSPECTION_TEMPLATE,
                        FacilioConstants.Inspection.INSPECTION_RESPONSE, FacilioConstants.Induction.INDUCTION_TEMPLATE))
                .fetchCustomModules()
                .afterFetch(new AddAutomationModuleBasedOnTabType())
                .done();
    }



}
