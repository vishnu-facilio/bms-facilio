package com.facilio.readingkpi.commands.delete;

import com.facilio.command.FacilioCommand;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class DeleteNamespaceReadingKpiCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ReadingKPIContext> list = recordMap.get(moduleName);
        ReadingKPIContext readingKPIContext = list.get(0);
        NamespaceAPI.deleteNameSpacesFromRuleId(readingKPIContext.getId(), NSType.KPI_RULE);
        return false;
    }
}
