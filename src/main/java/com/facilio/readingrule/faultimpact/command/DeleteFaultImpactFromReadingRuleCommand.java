package com.facilio.readingrule.faultimpact.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.readingrule.context.NewReadingRuleContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

@Log4j
public class DeleteFaultImpactFromReadingRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean canDelete = (boolean) context.getOrDefault("canDeleteFaultImpact", false);
        if (canDelete) {
            NewReadingRuleContext readingRule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
            NamespaceAPI.deleteNameSpacesFromRuleId(readingRule.getId(), NSType.FAULT_IMPACT_RULE);
            LOGGER.info("Successfully deleted fault impact rules. rule : " + readingRule.getImpactId());
            readingRule.setImpactId(-99L);
            readingRule.setImpact(null);
        }
        return false;
    }
}
