package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.SilentPushNotificationContext;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.util.SilentNotificationUtilForFsm;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SilentNotificationForAttendanceTransactionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String ModName = FacilioConstants.Attendance.ATTENDANCE_TRANSACTION;
        Map<String, List> recordMap = (Map<String, List>) context.get("recordMap");
        List<AttendanceTransaction> transactionsCreated = recordMap.get(ModName);
        AttendanceTransaction transaction = transactionsCreated.get(0);
        if(transaction.getPeople() != null) {
            Long peopleID = transaction.getPeople().getId();


            if (transaction.getTransactionType().equals(AttendanceTransaction.Type.CHECK_IN)) {
                SilentNotificationUtilForFsm.sendNotificationForFsm(transaction.getId(),Collections.singletonList(peopleID), SilentPushNotificationContext.ActionType.CHECK_IN, 300000L, 120000L);

            } else if (transaction.getTransactionType().equals(AttendanceTransaction.Type.CHECK_OUT)) {
                SilentNotificationUtilForFsm.sendNotificationForFsm(transaction.getId(),Collections.singletonList(peopleID), SilentPushNotificationContext.ActionType.CHECK_OUT, 300000L, 120000L);
            }
        }
       return false;
    }
}
