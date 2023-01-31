package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetAutomationModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> modules = new ArrayList<>();
        modules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.ASSET));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.SITE));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.FLOOR));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.BUILDING));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.SPACE));
        modules.add(modBean.getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP));

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VISITOR)) {
     //   	modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOG));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.BASE_VISIT));
        }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONTRACT)) {
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS));
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS));
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS));
        }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WORK_PERMIT)) {
    	   modules.add(modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT));
       }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
           modules.add(modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST));
       }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PURCHASE)) {
	    	   modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
	    	   modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
	   }
	   if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VENDOR)) {
		   modules.add(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
		   modules.add(modBean.getModule(FacilioConstants.ContextNames.INSURANCE));
           modules.add(modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT));
	   }

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SERVICE_REQUEST)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
        }

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.QUOTE));
        }

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.TENANT));
            modules.add(modBean.getModule(ContextNames.TENANT_CONTACT));
            modules.add(modBean.getModule(ContextNames.TENANT_UNIT_SPACE));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.BUDGET_MONITORING)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.BUDGET));
        }
//        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INSPECTION)) {
            modules.add(modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE));
            modules.add(modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE));
//        }
        
//        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INDUCTION)) {
            modules.add(modBean.getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE));
            modules.add(modBean.getModule(FacilioConstants.Induction.INDUCTION_RESPONSE));
//        }
            
//            modules.add(modBean.getModule(FacilioConstants.Survey.SURVEY_TEMPLATE));
//            modules.add(modBean.getModule(FacilioConstants.Survey.SURVEY_RESPONSE));
        
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FACILITY_BOOKING)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PURCHASE) && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.REQUEST_FOR_QUOTATION)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY) && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TRANSFER_REQUEST)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST));
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM));
            FacilioModule bmsAlarmModule = modBean.getModule(FacilioConstants.ContextNames.BMS_ALARM);
            if (bmsAlarmModule != null) {
                modules.add(bmsAlarmModule);
            }
        }
        else {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ALARM));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.COMMUNITY)){
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENT));
        }
        
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PM_PLANNER)) {
        	 modules.add(modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN));
        	 modules.add(modBean.getModule(ContextNames.PLANNEDMAINTENANCE));
        }
        
        modules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
}
