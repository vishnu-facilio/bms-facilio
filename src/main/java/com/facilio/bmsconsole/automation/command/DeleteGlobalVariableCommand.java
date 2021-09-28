package com.facilio.bmsconsole.automation.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.automation.util.GlobalVariableUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.cache.LRUCache;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

public class DeleteGlobalVariableCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ID to delete");
        }

        GlobalVariableUtil.deleteVariable(id);
        LRUCache.getGlobalVariableCache().remove(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getOrgId()));
        return false;
    }
}
