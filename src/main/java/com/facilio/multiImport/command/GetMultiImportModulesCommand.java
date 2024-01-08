package com.facilio.multiImport.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

public class
GetMultiImportModulesCommand extends FacilioCommand {

    public static final List<String> MODULES = Arrays.asList(
            FacilioConstants.ContextNames.WORK_ORDER,
            FacilioConstants.ContextNames.ASSET,
            FacilioConstants.ContextNames.VENDORS,
            FacilioConstants.ContextNames.VENDOR_CONTACT,
            FacilioConstants.ContextNames.SITE,
            FacilioConstants.ContextNames.BUILDING,
            FacilioConstants.ContextNames.FLOOR,
            FacilioConstants.ContextNames.SPACE,
            FacilioConstants.ContextNames.LOCATION,
            FacilioConstants.ContextNames.TENANT,
            FacilioConstants.ContextNames.TENANT_CONTACT,
            FacilioConstants.ContextNames.TENANT_UNIT_SPACE,
            FacilioConstants.Meter.METER,
            FacilioConstants.ContextNames.CLIENT_CONTACT,
            FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,
            FacilioConstants.UTILITY_INTEGRATION_METER,
            FacilioConstants.UTILITY_INTEGRATION_BILLS,
            FacilioConstants.UTILITY_INTEGRATION_LINE_ITEMS
            /*FacilioConstants.ContextNames.INSURANCE,
            FacilioConstants.ContextNames.BASE_VISIT,
            FacilioConstants.ContextNames.VISITOR_LOG,
            FacilioConstants.ContextNames.INVITE_VISITOR,
            FacilioConstants.ContextNames.PURCHASE_CONTRACTS,
            FacilioConstants.ContextNames.LABOUR_CONTRACTS,
            FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,
            FacilioConstants.ContextNames.WARRANTY_CONTRACTS,
            FacilioConstants.ContextNames.SERVICE,

            FacilioConstants.ContextNames.PURCHASE_REQUEST,
            FacilioConstants.ContextNames.PURCHASE_ORDER,
            FacilioConstants.ContextNames.SERVICE_REQUEST,
            FacilioConstants.ContextNames.QUOTE,
            FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,
            FacilioConstants.ContextNames.FacilityBooking.FACILITY,
            FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,
            FacilioConstants.Inspection.INSPECTION_TEMPLATE,
            FacilioConstants.Inspection.INSPECTION_RESPONSE,
            FacilioConstants.Induction.INDUCTION_TEMPLATE,
            FacilioConstants.Induction.INDUCTION_RESPONSE,
            FacilioConstants.ContextNames.TRANSFER_REQUEST,
            FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT,
            FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,
            FacilioConstants.ContextNames.ANNOUNCEMENT,
            FacilioConstants.ContextNames.ATTENDANCE,
            FacilioConstants.PeopleGroup.PEOPLE_GROUP*/
           );


    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = Constants.getModBean();
        List<FacilioModule> systemModules = new ArrayList<>();

        for(String moduleName: MODULES) {
            if (AccountUtil.isModuleLicenseEnabled(moduleName)) {
                systemModules.add(modBean.getModule(moduleName));
            }
        }

        List<FacilioModule> customModules = new ArrayList<>();
        customModules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));

        Map<String, List<FacilioModule>> modules = new HashMap<String, List<FacilioModule>>();
        modules.put("systemModules", systemModules);
        modules.put("customModules", customModules);

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);

        return false;
    }

}
