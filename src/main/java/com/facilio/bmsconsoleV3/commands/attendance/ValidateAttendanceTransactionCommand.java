package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateAttendanceTransactionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AttendanceTransaction> txns = recordMap.get(moduleName);

        if (CollectionUtils.isEmpty(txns)) {
            return false;
        }

        for (AttendanceTransaction tx: txns) {
            if (tx.getTransactionTime() == null ){
                throw new IllegalArgumentException("transaction date is mandatory");
            }

            if (AttendanceAPI.transactionIsNotCausallyValid(tx)){
                switch (tx.getTransactionType()){
                    case CHECK_IN:
                        throw new IllegalArgumentException("Check-In can be done only after Check-Out");
                    case CHECK_OUT:
                        throw new IllegalArgumentException("Check-Out can be done only after Check-In & Resume Work");
                    case BREAK:
                        throw new IllegalArgumentException("Break can be done only after Check-In & Resume Work");
                    case RESUME_WORK:
                        throw new IllegalArgumentException("Resume Work can be done only after Break");
                }
            }

            if (malformedBreakTransaction(tx)){
                throw new IllegalArgumentException("malformed break transactions");
            }

        }
        return false;
    }





    private boolean malformedBreakTransaction(AttendanceTransaction tx) {
        if (!tx.getTransactionType().equals(AttendanceTransaction.Type.BREAK)){
            return false;
        }
        return tx.getShiftBreak() == null;
    }
}
