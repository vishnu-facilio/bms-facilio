package com.facilio.elasticsearch.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.elasticsearch.context.SyncContext;
import com.facilio.elasticsearch.util.SyncUtil;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ListSyncModulesChain extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SyncContext> allSync = SyncUtil.getAllSync();
        if (CollectionUtils.isNotEmpty(allSync)) {
            List<FacilioModule> syncModules = allSync.stream().map(SyncContext::getSyncModule).collect(Collectors.toList());
            context.put(FacilioConstants.ContextNames.MODULE_LIST, syncModules);
        }
        return false;
    }
}
