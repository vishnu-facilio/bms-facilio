package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;

public class PMEditBlockRemovalCommand extends FacilioCommand implements PostTransactionCommand {

    private static final Logger LOGGER = Logger.getLogger(PMEditBlockRemovalCommand.class.getName());
    private long pmId;

    public PMEditBlockRemovalCommand(long pmId) {
        this.pmId = pmId;
    }

    @Override
    public boolean postExecute() throws Exception {
        if (AccountUtil.getCurrentOrg().getOrgId() == 176L) {
            LOGGER.log(Level.SEVERE, "Executing postExecute removal");
        }
        removeBlock();
        return false;
    }

    @Override
    public void onError() throws Exception {
        removeBlock();
    }

    private void removeBlock() throws Exception {
        List<Long> recordIds = Collections.singletonList(pmId);
        if (AccountUtil.getCurrentOrg().getOrgId() == 176L) {
            LOGGER.log(Level.SEVERE, "Executing removeBlock removal");
        }
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
