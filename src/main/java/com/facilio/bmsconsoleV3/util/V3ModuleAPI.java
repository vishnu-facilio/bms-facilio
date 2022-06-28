package com.facilio.bmsconsoleV3.util;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class V3ModuleAPI {

    private enum MODULES{
        WORKORDER(FacilioConstants.ContextNames.WORK_ORDER,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ASSET(FacilioConstants.ContextNames.ASSET,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        VENDORS(FacilioConstants.ContextNames.VENDORS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        VENDOR_CONTACT(FacilioConstants.ContextNames.VENDOR_CONTACT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        PURCHASE_CONTRACTS(FacilioConstants.ContextNames.PURCHASE_CONTRACTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        LABOUR_CONTRACTS(FacilioConstants.ContextNames.LABOUR_CONTRACTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        LABOURS(FacilioConstants.ContextNames.LABOUR,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        RENTAL_LEASE_CONTRACTS(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        WARRANTY_CONTRACTS(FacilioConstants.ContextNames.WARRANTY_CONTRACTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SERVICE(FacilioConstants.ContextNames.SERVICE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TENANT(FacilioConstants.ContextNames.TENANT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TENANT_CONTACT(FacilioConstants.ContextNames.TENANT_CONTACT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TENANT_UNIT_SPACE(FacilioConstants.ContextNames.TENANT_UNIT_SPACE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        PURCHASE_REQUEST(FacilioConstants.ContextNames.PURCHASE_REQUEST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        PURCHASE_ORDER(FacilioConstants.ContextNames.PURCHASE_ORDER,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SERVICE_REQUEST(FacilioConstants.ContextNames.SERVICE_REQUEST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        FACILITY(FacilioConstants.ContextNames.FacilityBooking.FACILITY,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        FACILITY_BOOKING(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        QUOTE(FacilioConstants.ContextNames.QUOTE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        WORKPERMIT(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,Arrays.asList()),
        CONTROL_SCHEDULE_MODULE_NAME(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME,Arrays.asList()),
        CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME,Arrays.asList()),
        CONTROL_GROUP_MODULE_NAME(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME,Arrays.asList()),
        CONTROL_GROUP_TENANT_SHARING_MODULE_NAME(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME,Arrays.asList()),
        CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME,Arrays.asList()),
        VISITOR(FacilioConstants.ContextNames.VISITOR,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        VISITOR_LOG(FacilioConstants.ContextNames.VISITOR_LOG,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INVITE_VISITOR(FacilioConstants.ContextNames.INVITE_VISITOR,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        WATCHLIST(FacilioConstants.ContextNames.WATCHLIST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ANNOUNCEMENT(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        PEOPLE_ANNOUNCEMENTS(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS,Arrays.asList()),
        NEIGHBOURHOOD(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        NEWS_AND_INFORMATION(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        DEALS_AND_OFFERS(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ADMIN_DOCUMENTS(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        CONTACT_DIRECTORY(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        BUDGET(FacilioConstants.ContextNames.Budget.BUDGET,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        CHART_OF_ACCOUNT(FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        DEPARTMENT(FacilioConstants.ContextNames.DEPARTMENT,Arrays.asList()),
        MOVES(FacilioConstants.ContextNames.MOVES,Arrays.asList()),
        DELIVERIES(FacilioConstants.ContextNames.DELIVERIES,Arrays.asList()),
        EMPLOYEE(FacilioConstants.ContextNames.EMPLOYEE,Arrays.asList()),
        ITEM(FacilioConstants.ContextNames.ITEM,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ITEM_TYPES(FacilioConstants.ContextNames.ITEM_TYPES,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TOOL(FacilioConstants.ContextNames.TOOL,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TOOL_TYPES(FacilioConstants.ContextNames.TOOL_TYPES,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        STORE_ROOM(FacilioConstants.ContextNames.STORE_ROOM,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        GATE_PASS(FacilioConstants.ContextNames.GATE_PASS,Arrays.asList()),
        SHIPMENT(FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INVENTORY_REQUEST(FacilioConstants.ContextNames.INVENTORY_REQUEST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TRANSFER_REQUEST(FacilioConstants.ContextNames.TRANSFER_REQUEST,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        PREVENTIVE_MAINTENANCE(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ACCOUNT_TYPE(FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INSURANCE(FacilioConstants.ContextNames.INSURANCE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        ITEM_TRANSACTIONS(FacilioConstants.ContextNames.ITEM_TRANSACTIONS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TOOL_TRANSACTIONS(FacilioConstants.ContextNames.TOOL_TRANSACTIONS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        DELIVERY_AREA(FacilioConstants.ContextNames.DELIVERY_AREA,Arrays.asList()),
        LOCKERS(FacilioConstants.ContextNames.LOCKERS,Arrays.asList()),
        PARKING_STALL(FacilioConstants.ContextNames.PARKING_STALL,Arrays.asList()),
        DESKS(FacilioConstants.ContextNames.Floorplan.DESKS,Arrays.asList()),
        INDOOR_FLOORPLAN(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN,Arrays.asList()),
        INSPECTION_TEMPLATE(FacilioConstants.Inspection.INSPECTION_TEMPLATE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INSPECTION_RESPONSE(FacilioConstants.Inspection.INSPECTION_RESPONSE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INDUCTION_TEMPLATE(FacilioConstants.Induction.INDUCTION_TEMPLATE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        INDUCTION_RESPONSE(FacilioConstants.Induction.INDUCTION_RESPONSE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SURVEY_TEMPLATE(FacilioConstants.Survey.SURVEY_TEMPLATE,Arrays.asList()),
        SURVEY_RESPONSE(FacilioConstants.Survey.SURVEY_RESPONSE,Arrays.asList()),
        SAFETY_PLAN(FacilioConstants.ContextNames.SAFETY_PLAN,Arrays.asList()),
        HAZARD(FacilioConstants.ContextNames.HAZARD,Arrays.asList()),
        PRECAUTION(FacilioConstants.ContextNames.PRECAUTION,Arrays.asList()),
        SITE(FacilioConstants.ContextNames.SITE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        BUILDING(FacilioConstants.ContextNames.BUILDING,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        FLOOR(FacilioConstants.ContextNames.FLOOR,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        SPACE(FacilioConstants.ContextNames.SPACE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        RECEIVABLE(FacilioConstants.ContextNames.RECEIVABLE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        TERMS_AND_CONDITIONS(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        AUDIENCE(FacilioConstants.ContextNames.AUDIENCE,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)),
        AMENITY(FacilioConstants.ContextNames.FacilityBooking.AMENITY,Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

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
        MODULES(String moduleName,List<String> applicationLinkNames){
            this.moduleName = moduleName;
            this.applicationLinkNames = applicationLinkNames;
        }
    }
    public static List<String> getSystemModuleNames(){
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
    public static List<String> getSystemModuleNamesForApp(String linkName){
        List<String> sysModuleNames = new ArrayList<>();
        for (MODULES module : MODULES.values()) {
            if(module.getApplicationLinkNames().contains(linkName)){
                sysModuleNames.add(module.getModuleName());
            }
        }
        return sysModuleNames;
    }
}