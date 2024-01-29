package com.facilio.moduleBuilder.builder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.moduleBuilder.command.GetModuleListForRelationshipCommand;
import com.facilio.moduleBuilder.command.GetSubModulesForTransactionRuleCommand;
import com.facilio.moduleBuilder.command.RemoveStateFlowDisabledModules;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.v3.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

@Config
public class APIModuleListConfig {
    @Feature("slaModules")
    public static Supplier<ModuleListHandler> getSLAModules(){
        return () -> new ModuleListHandler()
                .add(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER, FacilioConstants.ContextNames.SERVICE_REQUEST,
                        FacilioConstants.Inspection.INSPECTION_RESPONSE))
                .addModulesForApp(FacilioConstants.ApplicationLinkNames.FSM_APP, Arrays.asList(
                        FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT))
                .fetchCustomModules()
                .done();

    }

    @Feature("transactionRule")
    public static Supplier<ModuleListHandler> getTransactionRuleModules(){
        return () -> new ModuleListHandler()
                .add(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER, FacilioConstants.ContextNames.PURCHASE_REQUEST,
                        FacilioConstants.ContextNames.PURCHASE_ORDER, FacilioConstants.ContextNames.QUOTE,FacilioConstants.ContextNames.INVOICE))
                .fetchCustomModules()
                .afterFetch(new GetSubModulesForTransactionRuleCommand())
                .done();

    }

    @Feature("relationshipModules")
    public static Supplier<ModuleListHandler> getRelationshipModules(){
        return () -> new ModuleListHandler()
                .add(new ArrayList<String>() {{
                    add(FacilioConstants.ContextNames.ASSET);
                    add(FacilioConstants.ContextNames.METER_MOD_NAME);
                    add(FacilioConstants.ContextNames.SITE);
                    add(FacilioConstants.ContextNames.BUILDING);
                    add(FacilioConstants.ContextNames.FLOOR);
                    add(FacilioConstants.ContextNames.SPACE);
                    add(FacilioConstants.ContextNames.ITEM);
                    add(FacilioConstants.ContextNames.WEATHER_READING);
                    add(FacilioConstants.ModuleNames.WEATHER_STATION);
                    add(FacilioConstants.ContextNames.CLIENT);
                    add(FacilioConstants.ContextNames.TENANT);
                }})
                .fetchCustomModules()
                .responseFields(Arrays.asList("displayName","name", "moduleId"))
                .afterFetch(new GetModuleListForRelationshipCommand())
                .done();

    }

    @Feature("automation")
    public static Supplier<ModuleListHandler> getAutomationModules(){
        return () -> new ModuleListHandler()
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.NEW_ALARMS,Arrays.asList(FacilioConstants.ContextNames.NEW_READING_ALARM,
                        FacilioConstants.ContextNames.BMS_ALARM),Arrays.asList(FacilioConstants.ContextNames.ALARM) )
                .additionalLicenseToCheckForModules(AccountUtil.FeatureLicense.VISITOR, Arrays.asList(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE)) //TODO check MODULELIST the module license is actually GROUP_INVITES but here VISITOR is used
                .additionalLicenseToCheckForModules(AccountUtil.FeatureLicense.PURCHASE, Arrays.asList(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, FacilioConstants.ContextNames.VENDOR_QUOTES))
                .additionalLicenseToCheckForModules(AccountUtil.FeatureLicense.INVENTORY, Arrays.asList(FacilioConstants.ContextNames.TRANSFER_REQUEST))

                .add(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER,FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.SITE,
                        FacilioConstants.ContextNames.FLOOR, FacilioConstants.ContextNames.BUILDING,
                        FacilioConstants.ContextNames.SPACE, FacilioConstants.PeopleGroup.PEOPLE_GROUP,
                        FacilioConstants.Meter.METER, FacilioConstants.Inspection.INSPECTION_TEMPLATE,
                        FacilioConstants.Inspection.INSPECTION_RESPONSE, FacilioConstants.Induction.INDUCTION_TEMPLATE,
                        FacilioConstants.Induction.INDUCTION_RESPONSE, FacilioConstants.ContextNames.VISITOR_LOG,
                        FacilioConstants.ContextNames.INVITE_VISITOR,FacilioConstants.ContextNames.BASE_VISIT,
                        FacilioConstants.ContextNames.PURCHASE_CONTRACTS, FacilioConstants.ContextNames.LABOUR_CONTRACTS,
                        FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,FacilioConstants.ContextNames.WARRANTY_CONTRACTS,
                        FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, FacilioConstants.ContextNames.INVENTORY_REQUEST,
                        FacilioConstants.ContextNames.PURCHASE_REQUEST, FacilioConstants.ContextNames.PURCHASE_ORDER,
                        FacilioConstants.ContextNames.VENDORS, FacilioConstants.ContextNames.INSURANCE,
                        FacilioConstants.ContextNames.VENDOR_CONTACT, FacilioConstants.ContextNames.SERVICE_REQUEST,
                        FacilioConstants.ContextNames.TENANT, FacilioConstants.ContextNames.TENANT_CONTACT,
                        FacilioConstants.ContextNames.TENANT_UNIT_SPACE, FacilioConstants.ContextNames.BUDGET,
                        FacilioConstants.ContextNames.FacilityBooking.FACILITY, FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,
                        FacilioConstants.ContextNames.QUOTE, FacilioConstants.ContextNames.ANNOUNCEMENT,
                        FacilioConstants.ContextNames.Floorplan.DESKS, FacilioConstants.Survey.SURVEY_RESPONSE,
                        FacilioConstants.ContextNames.JOB_PLAN, FacilioConstants.ContextNames.PLANNEDMAINTENANCE,
                        FacilioConstants.UTILITY_INTEGRATION_CUSTOMER, FacilioConstants.UTILITY_INTEGRATION_BILLS,FacilioConstants.ContextNames.INVOICE))

                .addModulesForApp(FacilioConstants.ApplicationLinkNames.FSM_APP, Arrays.asList(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,
                        FacilioConstants.TimeOff.TIME_OFF,FacilioConstants.Territory.TERRITORY,
                        FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, FacilioConstants.TimeSheet.TIME_SHEET,
                        FacilioConstants.Trip.TRIP))

                .addModulesForApp(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING, Arrays.asList(AlarmTypeModule.MODULE_NAME, AlarmCategoryModule.MODULE_NAME,
                        AlarmDefinitionModule.MODULE_NAME, AlarmDefinitionMappingModule.MODULE_NAME,
                        AlarmAssetTaggingModule.MODULE_NAME, FilteredAlarmModule.MODULE_NAME))
                .fetchCustomModules()
                .done();
    }

    @Feature("emailTemplate")
    public static Supplier<ModuleListHandler> getEmailTemplateModules(){
        return () -> new ModuleListHandler()
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.NEW_ALARMS,Arrays.asList(FacilioConstants.ContextNames.NEW_READING_ALARM,
                        FacilioConstants.ContextNames.BMS_ALARM),Arrays.asList(FacilioConstants.ContextNames.ALARM) )
                .additionalLicenseToCheckForModules(AccountUtil.FeatureLicense.VISITOR, Arrays.asList(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE)) //TODO check MODULELIST the module license is actually GROUP_INVITES but here VISITOR is used
                .additionalLicenseToCheckForModules(AccountUtil.FeatureLicense.PURCHASE, Arrays.asList(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, FacilioConstants.ContextNames.VENDOR_QUOTES))
                .additionalLicenseToCheckForModules(AccountUtil.FeatureLicense.INVENTORY, Arrays.asList(FacilioConstants.ContextNames.TRANSFER_REQUEST))

                .add(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER,FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.SITE,
                        FacilioConstants.ContextNames.FLOOR, FacilioConstants.ContextNames.BUILDING,
                        FacilioConstants.ContextNames.SPACE, FacilioConstants.PeopleGroup.PEOPLE_GROUP,
                        FacilioConstants.Meter.METER, FacilioConstants.Inspection.INSPECTION_TEMPLATE,
                        FacilioConstants.Inspection.INSPECTION_RESPONSE, FacilioConstants.Induction.INDUCTION_TEMPLATE,
                        FacilioConstants.Induction.INDUCTION_RESPONSE, FacilioConstants.ContextNames.VISITOR_LOG,
                        FacilioConstants.ContextNames.INVITE_VISITOR,FacilioConstants.ContextNames.BASE_VISIT,
                        FacilioConstants.ContextNames.PURCHASE_CONTRACTS, FacilioConstants.ContextNames.LABOUR_CONTRACTS,
                        FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,FacilioConstants.ContextNames.WARRANTY_CONTRACTS,
                        FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, FacilioConstants.ContextNames.INVENTORY_REQUEST,
                        FacilioConstants.ContextNames.PURCHASE_REQUEST, FacilioConstants.ContextNames.PURCHASE_ORDER,
                        FacilioConstants.ContextNames.VENDORS, FacilioConstants.ContextNames.INSURANCE,
                        FacilioConstants.ContextNames.VENDOR_CONTACT, FacilioConstants.ContextNames.SERVICE_REQUEST,
                        FacilioConstants.ContextNames.TENANT, FacilioConstants.ContextNames.TENANT_CONTACT,
                        FacilioConstants.ContextNames.TENANT_UNIT_SPACE, FacilioConstants.ContextNames.BUDGET,
                        FacilioConstants.ContextNames.FacilityBooking.FACILITY, FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,
                        FacilioConstants.ContextNames.QUOTE, FacilioConstants.ContextNames.ANNOUNCEMENT,
                        FacilioConstants.ContextNames.Floorplan.DESKS, FacilioConstants.Survey.SURVEY_RESPONSE,
                        FacilioConstants.ContextNames.JOB_PLAN, FacilioConstants.ContextNames.PLANNEDMAINTENANCE,
                        FlaggedEventModule.MODULE_NAME,FacilioConstants.ContextNames.INVOICE))

                .addModulesForApp(FacilioConstants.ApplicationLinkNames.FSM_APP, Arrays.asList(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,
                        FacilioConstants.TimeOff.TIME_OFF,FacilioConstants.Territory.TERRITORY,
                        FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, FacilioConstants.TimeSheet.TIME_SHEET,
                        FacilioConstants.Trip.TRIP))

                .addModulesForApp(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING, Arrays.asList(AlarmTypeModule.MODULE_NAME, AlarmCategoryModule.MODULE_NAME,
                        AlarmDefinitionModule.MODULE_NAME, AlarmDefinitionMappingModule.MODULE_NAME,
                        AlarmAssetTaggingModule.MODULE_NAME, FilteredAlarmModule.MODULE_NAME))
                .fetchCustomModules()
                .done();
    }


    @Feature("stateflow")
    public static Supplier<ModuleListHandler> getStateFlowModules(){
        return () -> new ModuleListHandler()
                .addLicenseEnabledAndDisabledModulesToFetch(AccountUtil.FeatureLicense.NEW_ALARMS,Arrays.asList(FacilioConstants.ContextNames.NEW_READING_ALARM,
                        FacilioConstants.ContextNames.BMS_ALARM),Arrays.asList(FacilioConstants.ContextNames.ALARM) )
                .additionalLicenseToCheckForModules(AccountUtil.FeatureLicense.VISITOR, Arrays.asList(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE)) //TODO check MODULELIST the module license is actually GROUP_INVITES but here VISITOR is used
                .additionalLicenseToCheckForModules(AccountUtil.FeatureLicense.PURCHASE, Arrays.asList(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, FacilioConstants.ContextNames.VENDOR_QUOTES))
                .additionalLicenseToCheckForModules(AccountUtil.FeatureLicense.INVENTORY, Arrays.asList(FacilioConstants.ContextNames.TRANSFER_REQUEST))

                .add(Arrays.asList(FacilioConstants.ContextNames.WORK_ORDER,FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.SITE,
                        FacilioConstants.ContextNames.FLOOR, FacilioConstants.ContextNames.BUILDING,
                        FacilioConstants.ContextNames.SPACE, FacilioConstants.PeopleGroup.PEOPLE_GROUP,
                        FacilioConstants.Meter.METER, FacilioConstants.Inspection.INSPECTION_TEMPLATE,
                        FacilioConstants.Inspection.INSPECTION_RESPONSE, FacilioConstants.Induction.INDUCTION_TEMPLATE,
                        FacilioConstants.Induction.INDUCTION_RESPONSE, FacilioConstants.ContextNames.VISITOR_LOG,
                        FacilioConstants.ContextNames.INVITE_VISITOR,FacilioConstants.ContextNames.BASE_VISIT,
                        FacilioConstants.ContextNames.PURCHASE_CONTRACTS, FacilioConstants.ContextNames.LABOUR_CONTRACTS,
                        FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,FacilioConstants.ContextNames.WARRANTY_CONTRACTS,
                        FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, FacilioConstants.ContextNames.INVENTORY_REQUEST,
                        FacilioConstants.ContextNames.PURCHASE_REQUEST, FacilioConstants.ContextNames.PURCHASE_ORDER,
                        FacilioConstants.ContextNames.VENDORS, FacilioConstants.ContextNames.INSURANCE,
                        FacilioConstants.ContextNames.VENDOR_CONTACT, FacilioConstants.ContextNames.SERVICE_REQUEST,
                        FacilioConstants.ContextNames.TENANT, FacilioConstants.ContextNames.TENANT_CONTACT,
                        FacilioConstants.ContextNames.TENANT_UNIT_SPACE, FacilioConstants.ContextNames.BUDGET,
                        FacilioConstants.ContextNames.FacilityBooking.FACILITY, FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,
                        FacilioConstants.ContextNames.QUOTE, FacilioConstants.ContextNames.ANNOUNCEMENT,
                        FacilioConstants.ContextNames.Floorplan.DESKS, FacilioConstants.Survey.SURVEY_RESPONSE,
                        FacilioConstants.ContextNames.JOB_PLAN, FacilioConstants.ContextNames.PLANNEDMAINTENANCE,FacilioConstants.ContextNames.INVOICE))

                .addModulesForApp(FacilioConstants.ApplicationLinkNames.FSM_APP, Arrays.asList(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,
                        FacilioConstants.TimeOff.TIME_OFF,FacilioConstants.Territory.TERRITORY,
                        FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, FacilioConstants.TimeSheet.TIME_SHEET,
                        FacilioConstants.Trip.TRIP))

                .addModulesForApp(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING, Arrays.asList(AlarmTypeModule.MODULE_NAME, AlarmCategoryModule.MODULE_NAME,
                        AlarmDefinitionModule.MODULE_NAME, AlarmDefinitionMappingModule.MODULE_NAME,
                        AlarmAssetTaggingModule.MODULE_NAME, FilteredAlarmModule.MODULE_NAME))
                .fetchCustomModules()
                .afterFetch(new RemoveStateFlowDisabledModules())
                .done();
    }

}
