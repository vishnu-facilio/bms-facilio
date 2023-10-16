package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddRCAMappingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext rule = (NewReadingRuleContext) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE);
        ReadingRuleRCAContext rca = rule.getRca();
        if (rca != null) {
            List<Long> rcaRuleIds = rca.getRcaRuleIds();
            if (CollectionUtils.isNotEmpty(rcaRuleIds)) {
                List<Map<String, Object>> props = ReadingRuleRcaAPI.getRcaMappingProps(rca.getId(), rcaRuleIds);
                ReadingRuleRcaAPI.insertRcaMapping(props);
            }
        }
        return false;
    }
}
