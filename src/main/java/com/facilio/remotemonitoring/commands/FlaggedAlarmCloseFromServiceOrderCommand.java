package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleClosureConfigContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FlaggedAlarmCloseFromServiceOrderCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.REMOTE_MONITORING)) {
            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<ServiceOrderContext> serviceOrders = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(serviceOrders)) {
                for (ServiceOrderContext serviceOrder : serviceOrders) {
                    FlaggedEventContext flaggedEvent = serviceOrder.getFlaggedEvent();
                    if (flaggedEvent != null) {
                        FlaggedEventContext fetchedFlaggedEvent = FlaggedEventUtil.getFlaggedEvent(flaggedEvent.getId());
                        if (fetchedFlaggedEvent != null && fetchedFlaggedEvent.getFlaggedAlarmProcess() != null) {
                            AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
                            FlaggedEventRuleContext rule = alarmBean.getFlaggedEventRule(fetchedFlaggedEvent.getFlaggedAlarmProcess().getId());
                            if (rule != null && rule.getFlaggedEventRuleClosureConfig() != null) {
                                FlaggedEventRuleClosureConfigContext closure = FlaggedEventUtil.getFlaggedEventClosureConfig(rule.getFlaggedEventRuleClosureConfig().getId());
                                if (closure != null) {
                                    List<ServiceOrderTicketStatusContext> serviceOrderStatuses = closure.getServiceOrderStatuses();
                                    if (matchingStatus(serviceOrderStatuses, serviceOrder.getStatus())) {
                                        FlaggedEventUtil.closeFlaggedEvent(fetchedFlaggedEvent.getId(),true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean matchingStatus(List<ServiceOrderTicketStatusContext> statues, ServiceOrderTicketStatusContext currentServiceOrderStatus) {
        if (CollectionUtils.isNotEmpty(statues)) {
            for (ServiceOrderTicketStatusContext status : statues) {
                if (status.getId() == currentServiceOrderStatus.getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}
