package com.facilio.readingkpi.commands.create;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetFieldAndModuleIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ReadingKPIContext> list = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(list)) {
            for(ReadingKPIContext kpi : list) {
                kpi.setOrgId(AccountUtil.getCurrentOrg().getId());
                List<FacilioModule> modules = (List) context.get("modules");
                kpi.setReadingModuleId(modules.get(0).getModuleId());
                List moduleFieldIds = (List) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);
                kpi.setReadingFieldId((Long) moduleFieldIds.get(0));
                kpi.setReadingField(null);
                kpi.setStatus(true);
            }
            }
        return false;
    }

}
