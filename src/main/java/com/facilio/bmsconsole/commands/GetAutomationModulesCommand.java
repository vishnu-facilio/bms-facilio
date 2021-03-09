package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
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
        modules.add(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.SITE));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.FLOOR));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.BUILDING));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.SPACE));

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VISITOR)) {
     //   	modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOG));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR));
        }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONTRACT)) {
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACTS));
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.LABOUR_CONTRACTS));
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS));
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.WARRANTY_CONTRACTS));
        }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.HUDSON_YARDS)) {
    	   modules.add(modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT));
       }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
    	   modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
    	   modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
           modules.add(modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST));
       }

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SERVICE_REQUEST)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
        }

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.QUOTE));
        }

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FACILITY_BOOKING)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING));
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM));
            FacilioModule bmsAlarmModule = modBean.getModule(FacilioConstants.ContextNames.BMS_ALARM);
            if (bmsAlarmModule != null) {
                modules.add(bmsAlarmModule);
            }
            FacilioModule agentAlarm = modBean.getModule(FacilioConstants.ContextNames.AGENT_ALARM);

            if (agentAlarm != null) {
                modules.add(agentAlarm);
                FacilioModule controllerAlarm = modBean.getModule("controllerAlarm");
                if (controllerAlarm!=null){
                    modules.add(controllerAlarm);
                }
            }

        }
        else {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ALARM));
        }

        modules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
}
