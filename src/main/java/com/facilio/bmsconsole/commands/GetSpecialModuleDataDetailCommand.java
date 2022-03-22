package com.facilio.bmsconsole.commands;


import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetSpecialModuleDataDetailCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        long recordId = (long) context.get(FacilioConstants.ContextNames.ID);

        if (recordId > 0) {
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            switch (moduleName) {
                case FacilioConstants.ContextNames.READING_RULE_MODULE:
                case FacilioConstants.ContextNames.NEW_READING_RULE_MODULE:
                    FacilioChain fetchAlarmChain = ReadOnlyChainFactory.fetchAlarmRuleWithActionsChain();
                    context.put(FacilioConstants.ContextNames.IS_NEW_READING_RULE, moduleName.equals(FacilioConstants.ContextNames.NEW_READING_RULE_MODULE));
                    fetchAlarmChain.execute(context);
                    context.put(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.ALARM_RULE));
                default:
                    break;
            }
        }
        return false;
    }
}
