package com.facilio.readingrule.faultimpact.command;

import com.facilio.command.FacilioCommand;
import com.facilio.ns.context.NSType;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class DeleteFaultImpactFromReadingRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean canDelete = (boolean) context.getOrDefault("canDeleteFaultImpact", false);
        if (canDelete) {
            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<NewReadingRuleContext> list = recordMap.get(moduleName);
            List<NSType> nsList = new ArrayList<>();
            nsList.add(NSType.FAULT_IMPACT_RULE);
            if (CollectionUtils.isNotEmpty(list)) {
                for (NewReadingRuleContext readingRule : list) {
                    Constants.getNsBean().deleteNameSpacesFromRuleId(readingRule.getId(), nsList);
                    LOGGER.info("Successfully deleted fault impact rules. rule : " + readingRule.getImpactId());
                }
            }
        }
        return false;
    }
}
