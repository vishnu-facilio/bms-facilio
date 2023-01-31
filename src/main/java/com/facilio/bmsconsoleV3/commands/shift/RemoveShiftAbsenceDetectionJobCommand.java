package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.command.FacilioCommand;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class RemoveShiftAbsenceDetectionJobCommand extends FacilioCommand {

    private void deleteAbsenceDetectionJob(Shift s) throws Exception {
        FacilioTimer.deleteJob(s.getId(), "AttendanceAbsentSchedulerJob");
    }
    
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<Shift> shifts = recordMap.get(moduleName);
        for (Shift s : shifts) {
            deleteAbsenceDetectionJob(s);
        }
        return false;
    }

}
