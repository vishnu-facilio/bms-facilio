package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.remotemonitoring.signup.*;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            modules.add(modBean.getModule(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE));

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

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVOICE))
        {
            modules.add(modBean.getModule(ContextNames.INVOICE));
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
            modules.add(modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES));
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
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WORKPLACE_APPS)) {
            modules.add(modBean.getModule(ContextNames.Floorplan.DESKS));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SURVEY)) {
            modules.add(modBean.getModule(FacilioConstants.Survey.SURVEY_RESPONSE));
        }

        if(AccountUtil.getCurrentTab() != null && AccountUtil.getCurrentTab().getTypeEnum() == WebTabContext.Type.EMAIL_TEMPLATES) {
            FacilioModule flaggedEventModule = modBean.getModule(FlaggedEventModule.MODULE_NAME);
            if(flaggedEventModule != null) {
                modules.add(flaggedEventModule);
            }
        }

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FSM)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER));
            modules.add(modBean.getModule(FacilioConstants.Territory.TERRITORY));
            modules.add(modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
            modules.add(modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET));
            modules.add(modBean.getModule(FacilioConstants.Trip.TRIP));
        }
        modules.add(modBean.getModule(FacilioConstants.TimeOff.TIME_OFF));
        modules.add(modBean.getModule(FacilioConstants.Meter.METER));
        modules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));

        if(AccountUtil.getCurrentApp() != null && Objects.equals(AccountUtil.getCurrentApp().getLinkName(), FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)){
            modules.add(modBean.getModule(RawAlarmModule.MODULE_NAME));
            modules.add(modBean.getModule(AlarmTypeModule.MODULE_NAME));
            modules.add(modBean.getModule(AlarmCategoryModule.MODULE_NAME));
            modules.add(modBean.getModule(AlarmDefinitionModule.MODULE_NAME));
            modules.add(modBean.getModule(AlarmDefinitionMappingModule.MODULE_NAME));
            modules.add(modBean.getModule(AlarmDefinitionTaggingModule.MODULE_NAME));
            modules.add(modBean.getModule(AlarmAssetTaggingModule.MODULE_NAME));
            modules.add(modBean.getModule(FilteredAlarmModule.MODULE_NAME));
            modules.add(modBean.getModule(FlaggedEventModule.MODULE_NAME));
        }
        modules.add(modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER));
        modules.add(modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS));

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
}
