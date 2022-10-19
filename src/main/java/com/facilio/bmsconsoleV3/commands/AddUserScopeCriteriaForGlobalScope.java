package com.facilio.bmsconsoleV3.commands;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AddUserScopeCriteriaForGlobalScope extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long scopeVariableId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        if(scopeVariableId == null) {
            Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
            if(id != null) {
                scopeVariableId = id;
            }
        }
        if(scopeVariableId != null) {
            ScopingUtil.addUserscopeConfigForGlobalScope(scopeVariableId);
        }
        return false;
    }


}
