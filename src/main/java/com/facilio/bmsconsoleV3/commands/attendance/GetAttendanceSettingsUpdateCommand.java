package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.Attendance;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceSettings;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetAttendanceSettingsUpdateCommand extends FacilioCommand {

    final Long HALF_HOUR_IN_MILLIS = 30 * 60 * 1000L;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long duration = (Long) context.get(FacilioConstants.Attendance.DURATION);
        AttendanceSettings.WorkingHoursMode mode =
                (AttendanceSettings.WorkingHoursMode) context.get(FacilioConstants.Attendance.MODE);

        if (mode.equals(AttendanceSettings.WorkingHoursMode.CUSTOM) && duration == null) {
            throw new IllegalArgumentException("Duration is mandatory for non Shift working mode");
        }

        if (duration < HALF_HOUR_IN_MILLIS ) {
            throw new IllegalArgumentException("Minimum working hours cannot be less than 30 min");
        }

        AttendanceSettings settings = new AttendanceSettings(mode, duration);

        AttendanceAPI.setAttendanceSettings(settings);

        return false;
    }
}
