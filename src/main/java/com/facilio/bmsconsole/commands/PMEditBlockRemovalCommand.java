package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class PMEditBlockRemovalCommand extends FacilioCommand implements PostTransactionCommand {

    private long pmId;

    public PMEditBlockRemovalCommand(long pmId) {
        this.pmId = pmId;
    }

    @Override
    public boolean postExecute() throws Exception {
        removeBlock();
        return false;
    }

    @Override
    public void onError() throws Exception {
        removeBlock();
    }

    private void removeBlock() throws Exception {
        List<Long> recordIds = Collections.singletonList(pmId);
        try {
            PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(recordIds, 0);
        } catch (Exception e) {
            CommonCommandUtil.emailException("PMEditBlockRemovalCommand", "Exception occurred in updating status for the PM(s) - "+ StringUtils.join(pmId, ','), e);
            throw e;
        }
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        return false;
    }
}
