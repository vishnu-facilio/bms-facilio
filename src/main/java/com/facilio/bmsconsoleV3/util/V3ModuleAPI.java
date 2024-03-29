package com.facilio.bmsconsoleV3.util;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.remotemonitoring.signup.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class V3ModuleAPI {

    private enum MODULES {
        WORKORDER(FacilioConstants.ContextNames.WORK_ORDER,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ASSET(FacilioConstants.ContextNames.ASSET,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        VENDORS(FacilioConstants.ContextNames.VENDORS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        VENDOR_CONTACT(FacilioConstants.ContextNames.VENDOR_CONTACT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        PURCHASE_CONTRACTS(FacilioConstants.ContextNames.PURCHASE_CONTRACTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        LABOUR_CONTRACTS(FacilioConstants.ContextNames.LABOUR_CONTRACTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
//        LABOURS(FacilioConstants.ContextNames.LABOUR,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        RENTAL_LEASE_CONTRACTS(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        WARRANTY_CONTRACTS(FacilioConstants.ContextNames.WARRANTY_CONTRACTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SERVICE(FacilioConstants.ContextNames.SERVICE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        TENANT(FacilioConstants.ContextNames.TENANT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        TENANT_CONTACT(FacilioConstants.ContextNames.TENANT_CONTACT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        TENANT_UNIT_SPACE(FacilioConstants.ContextNames.TENANT_UNIT_SPACE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        PURCHASE_REQUEST(FacilioConstants.ContextNames.PURCHASE_REQUEST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        PURCHASE_ORDER(FacilioConstants.ContextNames.PURCHASE_ORDER,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SERVICE_REQUEST(FacilioConstants.ContextNames.SERVICE_REQUEST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        FACILITY(FacilioConstants.ContextNames.FacilityBooking.FACILITY,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        FACILITY_BOOKING(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        QUOTE(FacilioConstants.ContextNames.QUOTE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        INVOICE(FacilioConstants.ContextNames.INVOICE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        WORKPERMIT(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,Arrays.asList()),
        CONTROL_SCHEDULE_MODULE_NAME(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME,Arrays.asList()),
        CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME,Arrays.asList()),
        CONTROL_GROUP_MODULE_NAME(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME,Arrays.asList()),
        CONTROL_GROUP_TENANT_SHARING_MODULE_NAME(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME,Arrays.asList()),
        CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME,Arrays.asList()),
        VISITOR(FacilioConstants.ContextNames.VISITOR,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        VISITOR_LOG(FacilioConstants.ContextNames.VISITOR_LOG,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        INVITE_VISITOR(FacilioConstants.ContextNames.INVITE_VISITOR,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        GROUP_VISITOR_INVITE(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)),
        WATCHLIST(FacilioConstants.ContextNames.WATCHLIST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        ANNOUNCEMENT(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        PEOPLE_ANNOUNCEMENTS(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS,Arrays.asList()),
        NEIGHBOURHOOD(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        NEWS_AND_INFORMATION(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        DEALS_AND_OFFERS(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ADMIN_DOCUMENTS(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        CONTACT_DIRECTORY(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        BUDGET(FacilioConstants.ContextNames.Budget.BUDGET,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        CHART_OF_ACCOUNT(FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        DEPARTMENT(FacilioConstants.ContextNames.DEPARTMENT,Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        MOVES(FacilioConstants.ContextNames.MOVES,Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        DELIVERIES(FacilioConstants.ContextNames.DELIVERIES,Arrays.asList(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)),
        EMPLOYEE(FacilioConstants.ContextNames.EMPLOYEE,Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        ITEM(FacilioConstants.ContextNames.ITEM,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        ITEM_TYPES(FacilioConstants.ContextNames.ITEM_TYPES,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        TOOL(FacilioConstants.ContextNames.TOOL,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        TOOL_TYPES(FacilioConstants.ContextNames.TOOL_TYPES,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        STORE_ROOM(FacilioConstants.ContextNames.STORE_ROOM,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        GATE_PASS(FacilioConstants.ContextNames.GATE_PASS,Arrays.asList()),
        SHIPMENT(FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INVENTORY_REQUEST(FacilioConstants.ContextNames.INVENTORY_REQUEST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TRANSFER_REQUEST(FacilioConstants.ContextNames.TRANSFER_REQUEST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        PREVENTIVE_MAINTENANCE(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ACCOUNT_TYPE(FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INSURANCE(FacilioConstants.ContextNames.INSURANCE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ITEM_TRANSACTIONS(FacilioConstants.ContextNames.ITEM_TRANSACTIONS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TOOL_TRANSACTIONS(FacilioConstants.ContextNames.TOOL_TRANSACTIONS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        DELIVERY_AREA(FacilioConstants.ContextNames.DELIVERY_AREA,Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        LOCKERS(FacilioConstants.ContextNames.LOCKERS,Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        PARKING_STALL(FacilioConstants.ContextNames.PARKING_STALL,Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        DESKS(FacilioConstants.ContextNames.Floorplan.DESKS,Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        INDOOR_FLOORPLAN(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN,Arrays.asList(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        INSPECTION_TEMPLATE(FacilioConstants.Inspection.INSPECTION_TEMPLATE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INSPECTION_RESPONSE(FacilioConstants.Inspection.INSPECTION_RESPONSE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INDUCTION_TEMPLATE(FacilioConstants.Induction.INDUCTION_TEMPLATE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INDUCTION_RESPONSE(FacilioConstants.Induction.INDUCTION_RESPONSE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SURVEY_TEMPLATE(FacilioConstants.Survey.SURVEY_TEMPLATE,Arrays.asList()),
        SURVEY_RESPONSE(FacilioConstants.Survey.SURVEY_RESPONSE,Arrays.asList()),
        SAFETY_PLAN(FacilioConstants.ContextNames.SAFETY_PLAN,Arrays.asList()),
        HAZARD(FacilioConstants.ContextNames.HAZARD,Arrays.asList()),
        PRECAUTION(FacilioConstants.ContextNames.PRECAUTION,Arrays.asList()),
        SITE(FacilioConstants.ContextNames.SITE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        BUILDING(FacilioConstants.ContextNames.BUILDING,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        FLOOR(FacilioConstants.ContextNames.FLOOR,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        SPACE(FacilioConstants.ContextNames.SPACE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        RECEIVABLE(FacilioConstants.ContextNames.RECEIVABLE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TERMS_AND_CONDITIONS(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        AUDIENCE(FacilioConstants.ContextNames.AUDIENCE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        AMENITY(FacilioConstants.ContextNames.FacilityBooking.AMENITY,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        FAULTS(FacilioConstants.ContextNames.NEW_READING_ALARM,Arrays.asList()),
        BMS_ALARMS(FacilioConstants.ContextNames.BMS_ALARM,Arrays.asList()),
        SPACE_BOOKING(FacilioConstants.ContextNames.SPACE_BOOKING,Arrays.asList(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        ROOMS(FacilioConstants.ContextNames.ROOMS,Arrays.asList(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP)),
        CLIENT(FacilioConstants.ContextNames.CLIENT,Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP)),
        JOB_PLAN(FacilioConstants.ContextNames.JOB_PLAN,Arrays.asList()),
        PLANNEDMAINTENANCE(ContextNames.PLANNEDMAINTENANCE,Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP)),
        RULES(FacilioConstants.ReadingRules.NEW_READING_RULE, Arrays.asList()),
        NEW_KPI(FacilioConstants.ReadingKpi.READING_KPI, Arrays.asList()),
        ROUTES(ContextNames.ROUTES,Arrays.asList()),
        SHIFT(FacilioConstants.Shift.SHIFT, Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        REQUEST_FOR_QUOTATION(ContextNames.REQUEST_FOR_QUOTATION,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        VENDOR_QUOTES(ContextNames.VENDOR_QUOTES,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP )),
        BREAK(FacilioConstants.Break.BREAK, Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        ATTENDANCE(FacilioConstants.Attendance.ATTENDANCE, Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        CLIENT_CONTACT(ContextNames.CLIENT_CONTACT,Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP)),
        VENDOR_DOCUMENTS(ContextNames.VENDOR_DOCUMENTS, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SENSOR_ROLLUP_ALARM(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),

        METER(FacilioConstants.Meter.METER, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        VIRTUAL_METER_TEMPLATE(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),

        CALENDAR(FacilioConstants.Calendar.CALENDAR_MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        EVENT(FacilioConstants.Calendar.EVENT_MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        CONTROL_ACTION(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        CONTROL_ACTION_TEMPLATE(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),

        UTILITY_INTEGRATION_CUSTOMER(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        UTILITY_INTEGRATION_BILLS(FacilioConstants.UTILITY_INTEGRATION_BILLS, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        UTILITY_INTEGRATION_TARIFF(FacilioConstants.UTILITY_INTEGRATION_TARIFF, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        UTILITY_DISPUTE(FacilioConstants.UTILITY_DISPUTE, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SERVICE_ORDER(ContextNames.SERVICE_ORDER,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        TERRITORY(FacilioConstants.Territory.TERRITORY,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        TIME_OFF(FacilioConstants.TimeOff.TIME_OFF,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        SERVICE_APPOINTMENT(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        TIME_SHEET(FacilioConstants.TimeSheet.TIME_SHEET,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        TRIP(FacilioConstants.Trip.TRIP,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        FAILURE_CLASS(ContextNames.FAILURE_CLASS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        FAILURE_CODE(ContextNames.FAILURE_CODE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        FAILURE_CODE_PROBLEMS(ContextNames.FAILURE_CODE_PROBLEMS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        FAILURE_CODE_CAUSES(ContextNames.FAILURE_CODE_CAUSES,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        FAILURE_CODE_REMEDIES(ContextNames.FAILURE_CODE_REMEDIES,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),

        //ELECTRICITY_METER(FacilioConstants.Meter.ELECTRICITY_METER, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        //GAS_METER(FacilioConstants.Meter.GAS_METER, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        //WATER_METER(FacilioConstants.Meter.WATER_METER, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        //HEAT_METER(FacilioConstants.Meter.HEAT_METER, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        //BTU_METER(FacilioConstants.Meter.BTU_METER, Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SERVICE_PLANNED_MAINTENANCE(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        SERVICE_PLAN(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        SERVICE_PM_TEMPLATE(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP)),
        RAW_ALARM(RawAlarmModule.MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)),
        ALARM_REGEX_MATCHING(AlarmDefinitionMappingModule.MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)),
        ALARM_DEFINITION_TAGGIND(AlarmDefinitionTaggingModule.MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)),
        ALARM_ASSET_TAGGING(AlarmAssetTaggingModule.MODULE_NAME, Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)),
        ALARM_TYPE(AlarmTypeModule.MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)),
        ALARM_CATEGORY(AlarmCategoryModule.MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)),
        ALARM_DEFINITION(AlarmDefinitionModule.MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)),
        FILTERED_ALARMS(FilteredAlarmModule.MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)),
        FLAGGED_EVENTS(FlaggedEventModule.MODULE_NAME,Arrays.asList(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)),
        ;


        private String moduleName;
        private List<String> applicationLinkNames;

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public List<String> getApplicationLinkNames() {
            return applicationLinkNames;
        }

        public void setApplicationLinkNames(List<String> applicationLinkNames) {
            this.applicationLinkNames = applicationLinkNames;
        }

        MODULES(String moduleName, List<String> applicationLinkNames) {
            this.moduleName = moduleName;
            this.applicationLinkNames = applicationLinkNames;
        }
    }

    public static List<String> getSystemModuleNames() {
        List<String> sysModuleNames = new ArrayList<>();
        for (MODULES module : MODULES.values()) {
            sysModuleNames.add(module.getModuleName());
        }
        return sysModuleNames;
    }

    public static List<FacilioModule> getSystemModule() throws Exception {
        List<FacilioModule> sysModules = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (MODULES module : MODULES.values()) {
            FacilioModule facilioModule = modBean.getModule(module.getModuleName());
            sysModules.add(facilioModule);
        }
        return sysModules;
    }

    public static List<FacilioModule> getSystemModuleWithFeatureLicenceCheck() throws Exception {
        List<FacilioModule> sysModules = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (MODULES module : MODULES.values()) {
            if (AccountUtil.isModuleLicenseEnabled(module.getModuleName())) {
                FacilioModule facilioModule = modBean.getModule(module.getModuleName());
                sysModules.add(facilioModule);
            }
        }
        return sysModules;
    }

    private static List<String> specialModules = Arrays.asList(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
    public static List<String> getSystemModuleNamesForApp(String linkName){
        return getSystemModuleNamesForApp(linkName,false);
    }
    public static List<String> getSystemModuleNamesForApp(String linkName,boolean excludeSpecialModules){
        List<String> sysModuleNames = new ArrayList<>();
        for (MODULES module : MODULES.values()) {
            if(excludeSpecialModules && specialModules.contains(module.getModuleName())){
                continue;
            }
            if(module.getApplicationLinkNames().contains(linkName)){
                sysModuleNames.add(module.getModuleName());
            }
        }
        return sysModuleNames;
    }

}