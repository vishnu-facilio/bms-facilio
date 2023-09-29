package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.constants.FacilioConstants.ContextNames.Budget;
import com.facilio.constants.FacilioConstants.ContextNames.FacilityBooking;
import com.facilio.constants.FacilioConstants.ContextNames.WorkPermit;
import com.facilio.constants.FacilioConstants.Induction;
import com.facilio.constants.FacilioConstants.Inspection;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;

//This command is used for showing module list while adding Lookup field type in form builder and custom module list in other places.
public class GetModuleListCommand extends FacilioCommand {
	
	private static final List<String> MODULES = Arrays.asList(new String[] {
			ContextNames.WORK_ORDER,
			ContextNames.USERS,
			ContextNames.ASSET,
			ContextNames.SITE,
			ContextNames.BUILDING,
			ContextNames.FLOOR,
			ContextNames.SPACE,
			ContextNames.VENDORS,
			Inspection.INSPECTION_TEMPLATE,
			Inspection.INSPECTION_RESPONSE,
			Induction.INDUCTION_TEMPLATE,
			Induction.INDUCTION_RESPONSE,
			WorkPermit.WORKPERMIT,
			ContextNames.SERVICE,
			ContextNames.SERVICE_REQUEST,
			ContextNames.TENANT,
			ContextNames.TENANT_UNIT_SPACE,
			ContextNames.PEOPLE,
			ContextNames.CLIENT,
			Budget.BUDGET,
			FacilityBooking.FACILITY,
			FacilityBooking.FACILITY_BOOKING,
			ContextNames.TOOL,
			ContextNames.ITEM,
			ContextNames.ITEM_TYPES,
			ContextNames.TOOL_TYPES,
			ContextNames.SERVICE,
			ContextNames.STORE_ROOM,
			ContextNames.INVENTORY_REQUEST,
			ContextNames.ASSET_CATEGORY,
			ContextNames.ROLE,
			ContextNames.PURCHASE_ORDER,
			ContextNames.RECEIVABLE,
			ContextNames.PURCHASE_REQUEST,
			ContextNames.REQUEST_FOR_QUOTATION,
			ContextNames.VENDOR_QUOTES,
			ContextNames.TRANSFER_REQUEST,
			ContextNames.TRANSFER_REQUEST_SHIPMENT,
			ContextNames.TERMS_AND_CONDITIONS,
			ContextNames.ANNOUNCEMENT,
			ContextNames.Tenant.NEWS_AND_INFORMATION,
			ContextNames.FAILURE_CLASS,
			ContextNames.NEW_READING_ALARM,
			ContextNames.BMS_ALARM,
			ContextNames.DEPARTMENT,
			ContextNames.EMPLOYEE,
			ContextNames.Floorplan.DESKS,
			ContextNames.MOVES,
			ContextNames.DELIVERIES,
			ContextNames.LOCKERS,
			ContextNames.PARKING_STALL,
			ContextNames.SPACE_BOOKING,
			ContextNames.FAILURE_CLASS,
			FacilioConstants.PeopleGroup.PEOPLE_GROUP,
			ContextNames.QUOTE,
			FacilioConstants.TimeOff.TIME_OFF,
			FacilioConstants.Territory.TERRITORY,
			FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,
			FacilioConstants.TimeSheet.TIME_SHEET,
			ContextNames.FieldServiceManagement.SERVICE_ORDER,
			ContextNames.EMPLOYEE,
			ContextNames.VENDOR_CONTACT,
			ContextNames.TENANT_CONTACT,
			ContextNames.CLIENT_CONTACT,
			FacilioConstants.Trip.TRIP
	});

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);

		boolean onlyCustom = false;
		if (moduleType == null || moduleType <= 0) {
			moduleType = ModuleType.BASE_ENTITY.getValue();
			onlyCustom = true;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> moduleList = modBean.getModuleList(ModuleType.valueOf(moduleType), onlyCustom);


		if(CollectionUtils.isNotEmpty(moduleList) && onlyCustom && (AccountUtil.getCurrentOrg().getOrgId() == 321l || AccountUtil.getCurrentOrg().getOrgId() == 173l)){
			List<FacilioModule> splModules = moduleList.stream()
					.filter(mod->mod.getName().equals("custom_tenantcontract") || mod.getName().equals("custom_timesheetmanagement") || mod.getName().equals("custom_servicebilllineitems"))
					.collect(Collectors.toList());

			if(CollectionUtils.isNotEmpty(splModules)){
				splModules.sort(new Comparator<FacilioModule>() {
					@Override
					public int compare(FacilioModule o1, FacilioModule o2) {
						return Long.compare(o2.getModuleId(),o1.getModuleId());
					}
				});
				moduleList.removeAll(splModules);
				moduleList.addAll(0,splModules);
			}
		}

		// This is used in formbuilder
		Boolean fetchDefaultModules = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_DEFAULT_MODULES);
		if (fetchDefaultModules != null && fetchDefaultModules) {
			for(String moduleName: MODULES) {
				if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
					moduleList.add(modBean.getModule(moduleName));
				}
			}
		}
		context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
		return false;
	}

}
