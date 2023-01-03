package com.facilio.bmsconsoleV3.commands.visitorlog;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateInviteCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<InviteVisitorContextV3> inviteVisitorLogs = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(inviteVisitorLogs)) {
            for (InviteVisitorContextV3 vL : inviteVisitorLogs) {
                if (vL.getExpectedCheckInTime() != null && vL.getExpectedCheckOutTime() != null && vL.getExpectedCheckInTime()>vL.getExpectedCheckOutTime()) {
                    throw new IllegalArgumentException("Invaild Check-in And Check-out Time");
                }
            }
        }
            return false;
        }

}
