package com.facilio.bmsconsole.automation.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.automation.context.GlobalVariableContext;
import com.facilio.bmsconsole.automation.util.GlobalVariableUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.cache.LRUCache;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Delete Global variable group command. It will throw error, if any variables is assigned with the variable group.
 * It is needed that we should delete all the variables if we are using this command.
 *
 * If it is needed to cascade all the variables along with Variable Group,
 * use this method instead, {@link GlobalVariableUtil#deleteVariableGroup}.
 */
public class DeleteGlobalVariableGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ID to delete");
        }

        validateDelete(id);

        GlobalVariableUtil.deleteVariableGroup(id);
        LRUCache.getGlobalVariableCache().remove(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getOrgId()));
        return false;
    }

    private void validateDelete(Long variableGroup) throws Exception {
        // don't delete if that group has variables inside
        List<GlobalVariableContext> allGlobalVariables = GlobalVariableUtil.getAllGlobalVariables(variableGroup);
        if (CollectionUtils.isNotEmpty(allGlobalVariables)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Remove variables, before removing group");
        }
    }
}
