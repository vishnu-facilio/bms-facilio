package com.facilio.telemetry.command;

import com.facilio.command.FacilioCommand;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.telemetry.context.TelemetryCriteriaContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class UpdateNameSpaceParentIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
        List<TelemetryCriteriaContext> telemetryCriteriaList = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(telemetryCriteriaList)) {
            for (TelemetryCriteriaContext telemetryCriteria : telemetryCriteriaList) {
                NameSpaceContext nameSpaceContext = telemetryCriteria.getNamespace();
                nameSpaceContext.setParentRuleId(telemetryCriteria.getId());
                Constants.getNsBean().updateNamespace(nameSpaceContext);
            }
        }
        return false;
    }
}
