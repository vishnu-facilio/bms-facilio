package com.facilio.ns.command;

import com.facilio.command.FacilioCommand;
import com.facilio.connected.IConnectedRule;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetParentIdForNamespaceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<Object> list = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(list)) {
            for (Object fddObject : list) {
                if (fddObject instanceof IConnectedRule) {
                    IConnectedRule connectedRule = (IConnectedRule) fddObject;
                    NameSpaceContext ns = connectedRule.getNs();
                    ns.setParentRuleId(connectedRule.getId());
                    if (ns.getWorkflowContext() != null) {
                        ns.getWorkflowContext().setIsV2Script(true);
                    }
                }
            }
        }
        return false;
    }
}