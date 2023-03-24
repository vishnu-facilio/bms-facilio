package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
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
