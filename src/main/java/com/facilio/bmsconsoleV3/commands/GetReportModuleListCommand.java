package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetReportModuleListCommand extends FacilioCommand {


    private static final List<String> MODULES = Arrays.asList(new String[] {
            FacilioConstants.ContextNames.WORK_ORDER,
            FacilioConstants.ContextNames.USERS,
            FacilioConstants.ContextNames.ASSET,
            FacilioConstants.ContextNames.SITE,
            FacilioConstants.ContextNames.BUILDING,
            FacilioConstants.ContextNames.FLOOR,
            FacilioConstants.ContextNames.SPACE,
            FacilioConstants.ContextNames.VENDORS,
            FacilioConstants.Inspection.INSPECTION_TEMPLATE,
            FacilioConstants.Inspection.INSPECTION_RESPONSE,
            FacilioConstants.Induction.INDUCTION_TEMPLATE,
            FacilioConstants.Induction.INDUCTION_RESPONSE,
            FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,
            FacilioConstants.ContextNames.SERVICE,
            FacilioConstants.ContextNames.SERVICE_REQUEST,
            FacilioConstants.ContextNames.TENANT,
            FacilioConstants.ContextNames.TENANT_UNIT_SPACE,
            FacilioConstants.ContextNames.PEOPLE,
            FacilioConstants.ContextNames.CLIENT,
            FacilioConstants.ContextNames.Budget.BUDGET,
            FacilioConstants.ContextNames.FacilityBooking.FACILITY,
            FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,
            FacilioConstants.ContextNames.TOOL,
            FacilioConstants.ContextNames.ITEM,
            FacilioConstants.ContextNames.ASSET_CATEGORY,
            FacilioConstants.ContextNames.ROLE,
            FacilioConstants.ContextNames.PURCHASE_ORDER,
            FacilioConstants.ContextNames.PURCHASE_REQUEST,
    });

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);

        boolean onlyCustom = false;
        if (moduleType == null || moduleType <= 0) {
            moduleType = FacilioModule.ModuleType.BASE_ENTITY.getValue();
            onlyCustom = true;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> moduleList = modBean.getModuleList(FacilioModule.ModuleType.valueOf(moduleType), onlyCustom);

        User user = AccountUtil.getCurrentUser();
        Role role = user.getRole();
        List<FacilioModule> moduleList1 = new ArrayList<>();
        HashMap<String, String> moduleMap = new HashMap<>();
        if((role != null && role.getName().equals("Super Administrator") || role.getName().equals("Administrator"))){
            moduleList1.addAll(moduleList);
        } else {
            List<Permissions> permissions = role.getPermissions();
            for(Permissions permission : permissions)
            {
                moduleMap.put(permission.getModuleName(), permission.getModuleName());
            }
            for(FacilioModule module : moduleList){
                if(!moduleMap.isEmpty() && moduleMap.containsKey(module.getName()))
                {
                    moduleList1.add(module);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(moduleList1) && onlyCustom && (AccountUtil.getCurrentOrg().getOrgId() == 321l || AccountUtil.getCurrentOrg().getOrgId() == 173l)){
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
                    moduleList1.add(modBean.getModule(moduleName));
                }
            }

            if(AccountUtil.getCurrentOrg().getOrgId() == 429l) { //temp
                moduleList.add(modBean.getModule(FacilioConstants.ContextNames.DEPARTMENT));
                moduleList.add(modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE));
                moduleList.add(modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS));
                moduleList.add(modBean.getModule(FacilioConstants.ContextNames.MOVES));
                moduleList.add(modBean.getModule(FacilioConstants.ContextNames.DELIVERIES));
                moduleList.add(modBean.getModule(FacilioConstants.ContextNames.LOCKERS));
                moduleList.add(modBean.getModule(FacilioConstants.ContextNames.PARKING_STALL));
            }
        }
        context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList1);
        return false;
    }

}
