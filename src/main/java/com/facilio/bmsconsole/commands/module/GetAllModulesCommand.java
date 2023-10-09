package com.facilio.bmsconsole.commands.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facilio.bmsconsoleV3.util.V3ModuleAPI;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;

public class GetAllModulesCommand extends FacilioCommand {
	
	private static final List<String> MODULES = Arrays.asList(new String[] {
			ContextNames.WORK_ORDER,
			ContextNames.ASSET,
			ContextNames.VENDORS,
			ContextNames.VENDOR_CONTACT,
			ContextNames.PURCHASE_CONTRACTS,
			ContextNames.LABOUR_CONTRACTS,
			ContextNames.RENTAL_LEASE_CONTRACTS,
			ContextNames.WARRANTY_CONTRACTS,
			ContextNames.SERVICE,
			ContextNames.TENANT,
			ContextNames.TENANT_CONTACT,
			ContextNames.TENANT_UNIT_SPACE,
			ContextNames.PURCHASE_REQUEST,
			ContextNames.PURCHASE_ORDER,
			ContextNames.SERVICE_REQUEST,
			ContextNames.FacilityBooking.FACILITY,
			ContextNames.FacilityBooking.FACILITY_BOOKING,
			ContextNames.QUOTE,
			ContextNames.WorkPermit.WORKPERMIT,
			ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME,
			ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME,
			ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME,
			ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME,
			ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME,
			ContextNames.VISITOR,
            ContextNames.VISITOR_LOG,
            ContextNames.INVITE_VISITOR,
			ContextNames.GROUP_VISITOR_INVITE,
			ContextNames.WATCHLIST,
            ContextNames.Tenant.ANNOUNCEMENT,
			ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS,
			ContextNames.Tenant.NEIGHBOURHOOD,
            ContextNames.Tenant.NEWS_AND_INFORMATION,
            ContextNames.Tenant.DEALS_AND_OFFERS,
            ContextNames.Tenant.ADMIN_DOCUMENTS,
            ContextNames.Tenant.CONTACT_DIRECTORY,
			ContextNames.Budget.BUDGET,
			ContextNames.Budget.CHART_OF_ACCOUNT,
			ContextNames.DEPARTMENT,
			ContextNames.MOVES,
			ContextNames.DELIVERIES,
			ContextNames.EMPLOYEE,
			ContextNames.ITEM,
			ContextNames.ITEM_TYPES,
			ContextNames.TOOL,
			ContextNames.TOOL_TYPES,
			ContextNames.STORE_ROOM,
			ContextNames.GATE_PASS,
			ContextNames.SHIPMENT,
			ContextNames.INVENTORY_REQUEST,
			ContextNames.PREVENTIVE_MAINTENANCE,
			ContextNames.Budget.ACCOUNT_TYPE,
			ContextNames.INSURANCE,
			ContextNames.ITEM_TRANSACTIONS,
			ContextNames.TOOL_TRANSACTIONS,
			ContextNames.DELIVERY_AREA,
			ContextNames.LOCKERS,
			ContextNames.PARKING_STALL,
			ContextNames.Floorplan.DESKS,
			ContextNames.Floorplan.INDOOR_FLOORPLAN,
			FacilioConstants.Inspection.INSPECTION_TEMPLATE,
			FacilioConstants.Inspection.INSPECTION_RESPONSE,
			FacilioConstants.Induction.INDUCTION_TEMPLATE,
			FacilioConstants.Induction.INDUCTION_RESPONSE,
			FacilioConstants.Survey.SURVEY_TEMPLATE,
			FacilioConstants.Survey.SURVEY_RESPONSE,
			ContextNames.SAFETY_PLAN,
			ContextNames.HAZARD,
			ContextNames.PRECAUTION,
			ContextNames.SITE,
			ContextNames.NEW_READING_ALARM,
			ContextNames.BMS_ALARM,
			ContextNames.SPACE_BOOKING,
			ContextNames.ROOMS,

			FacilioConstants.Calendar.CALENDAR_MODULE_NAME,
			FacilioConstants.Calendar.EVENT_MODULE_NAME,
			FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,
			FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME,


			FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,
			FacilioConstants.UTILITY_INTEGRATION_BILLS,
			FacilioConstants.UTILITY_INTEGRATION_TARIFF,
			FacilioConstants.UTILITY_DISPUTE,
			FacilioConstants.LocationHistory.LOCATION_HISTORY,
			FacilioConstants.TimeOff.TIME_OFF,
			FacilioConstants.Territory.TERRITORY,
			FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,
			FacilioConstants.TimeSheet.TIME_SHEET,
			ContextNames.FieldServiceManagement.SERVICE_ORDER,
			ContextNames.CLIENT_CONTACT,
			FacilioConstants.Trip.TRIP

	});

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		//List<FacilioModule> modules = modBean.getModuleList(ModuleType.BASE_ENTITY);
		
		List<JSONObject> systemModules = new ArrayList<JSONObject>();
		List<JSONObject> customModules = new ArrayList<JSONObject>();
		
		for(String moduleName: V3ModuleAPI.getSystemModuleNames()) {
			if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
				FacilioModule module = modBean.getModule(moduleName);
				if(module != null) {
					systemModules.add(getModuleJson(module));
				}
			}
		}
		
		List<FacilioModule> modules = modBean.getModuleList(ModuleType.BASE_ENTITY, true);
		for(FacilioModule module: modules) {
			if(module != null) {
				customModules.add(getModuleJson(module));
			}
		}
		
		context.put("systemModules", systemModules);
		context.put("customModules", customModules);
		
		return false;
	}
	
	private JSONObject getModuleJson(FacilioModule module) {
		JSONObject obj = new JSONObject();
		obj.put("name" , module.getName());
		obj.put("displayName" , module.getDisplayName());
		obj.put("moduleId" , module.getModuleId());
		return obj;
	}
	

}
