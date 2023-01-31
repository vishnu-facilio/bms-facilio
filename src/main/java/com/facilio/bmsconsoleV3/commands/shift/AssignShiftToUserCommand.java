package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AssignShiftToUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        User user = (User) context.get(FacilioConstants.ContextNames.USER);
        if (user == null){
            return false;
        }
        ShiftAPI.assignDefaultShiftToEmployee(user.getPeopleId());
        return false;
    }
}
