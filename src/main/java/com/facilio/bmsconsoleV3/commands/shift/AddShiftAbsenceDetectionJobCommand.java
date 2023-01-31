package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.command.FacilioCommand;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;


public class AddShiftAbsenceDetectionJobCommand extends FacilioCommand {

    private void addDetectionJob(Shift shift) throws Exception {
        ScheduleInfo schedule = new ScheduleInfo();
        schedule.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        schedule.addTime(LocalTime.ofSecondOfDay(shift.getEndTime()));

        final String jobName = "AttendanceAbsentSchedulerJob";
        final long jobID = shift.getId();
        final long startTime = System.currentTimeMillis();
        final String executor = "facilio";
        FacilioTimer.scheduleCalendarJob(jobID, jobName, startTime, schedule, executor);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<Shift> shifts = recordMap.get(moduleName);
        for (Shift s : shifts) {
            addDetectionJob(s);
        }
        return false;
    }
}
