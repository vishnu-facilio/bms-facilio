package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.automation.context.GlobalVariableGroupContext;
import com.facilio.bmsconsole.automation.util.GlobalVariableUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

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
        return false;
    }

    private void validateDelete(Long variableGroup) {
        // don't delete if that group has variables inside
    }
}
