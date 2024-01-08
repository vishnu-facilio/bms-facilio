package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.constants.FacilioConstants.Induction;
import com.facilio.constants.FacilioConstants.Inspection;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

// This command is used for showing module list under Customization tab
public class GetModulesListCommand extends FacilioCommand {

	public static final List<String> MODULES = Arrays.asList(
			ContextNames.WORK_ORDER,
			ContextNames.ASSET,
			ContextNames.VENDORS,
			ContextNames.BIN,
			ContextNames.INSURANCE,
			ContextNames.BASE_VISIT,
			ContextNames.VISITOR_LOG,
			ContextNames.INVITE_VISITOR,
			ContextNames.PURCHASE_CONTRACTS,
			ContextNames.LABOUR_CONTRACTS,
			ContextNames.RENTAL_LEASE_CONTRACTS,
			ContextNames.WARRANTY_CONTRACTS,
			ContextNames.SERVICE,
			ContextNames.TENANT,
			ContextNames.TENANT_UNIT_SPACE,
			ContextNames.PURCHASE_REQUEST,
			ContextNames.PURCHASE_ORDER,
			ContextNames.SERVICE_REQUEST,
			ContextNames.QUOTE,
			ContextNames.WorkPermit.WORKPERMIT,
			ContextNames.FacilityBooking.FACILITY,
			ContextNames.FacilityBooking.FACILITY_BOOKING,
			ContextNames.SITE,
			ContextNames.BUILDING,
			ContextNames.FLOOR,
			ContextNames.SPACE,
			Inspection.INSPECTION_TEMPLATE,
			Inspection.INSPECTION_RESPONSE,
			Induction.INDUCTION_TEMPLATE,
			Induction.INDUCTION_RESPONSE,
			ContextNames.TRANSFER_REQUEST,
			ContextNames.TRANSFER_REQUEST_SHIPMENT,
			ContextNames.REQUEST_FOR_QUOTATION,
			ContextNames.PLANNEDMAINTENANCE,
			ContextNames.ANNOUNCEMENT,
			ContextNames.JOB_PLAN,
			ContextNames.ATTENDANCE,
			FacilioConstants.PeopleGroup.PEOPLE_GROUP,
	        FacilioConstants.Meter.METER,
			FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE,
			FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,
			FacilioConstants.UTILITY_INTEGRATION_METER,
			FacilioConstants.UTILITY_INTEGRATION_BILLS,
			FacilioConstants.UTILITY_DISPUTE,
			FacilioConstants.Calendar.CALENDAR_MODULE_NAME,
			FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME,
			FacilioConstants.TimeOff.TIME_OFF,
			FacilioConstants.Territory.TERRITORY,
			FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,
			FacilioConstants.TimeSheet.TIME_SHEET,
			ContextNames.FieldServiceManagement.SERVICE_ORDER,
			FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,
			FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN,
			FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE,
			ContextNames.EMPLOYEE,
			ContextNames.VENDOR_CONTACT,
			ContextNames.TENANT_CONTACT,
			ContextNames.CLIENT_CONTACT,
			FacilioConstants.Trip.TRIP,
			ContextNames.INVOICE,
			ContextNames.CLIENT,
			FlaggedEventModule.MODULE_NAME
	);

	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> sytemModules = new ArrayList<>();
		for(String moduleName: MODULES) {
			if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
				sytemModules.add(modBean.getModule(moduleName));
			}
		}
		List<FacilioModule> customModules = new ArrayList<>();

		customModules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));
		
		
		if(CollectionUtils.isNotEmpty(customModules) && (AccountUtil.getCurrentOrg().getOrgId() == 321l || AccountUtil.getCurrentOrg().getOrgId() == 173l)){
			List<FacilioModule> splModules = customModules.stream()
					.filter(mod->mod.getName().equals("custom_tenantcontract") || mod.getName().equals("custom_timesheetmanagement") || mod.getName().equals("custom_servicebilllineitems"))
					.collect(Collectors.toList());

			if(CollectionUtils.isNotEmpty(splModules)){
				splModules.sort(new Comparator<FacilioModule>() {
					@Override
					public int compare(FacilioModule o1, FacilioModule o2) {
						return Long.compare(o2.getModuleId(),o1.getModuleId());
					}
				});
				customModules.removeAll(splModules);
				customModules.addAll(0,splModules);
			}
		}
		
		

		Map<String, List<FacilioModule>> modules = new HashMap<String, List<FacilioModule>>();
		modules.put("systemModules", sytemModules);
		modules.put("customModules", customModules);

		context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        
		return false;
	}

	

}
