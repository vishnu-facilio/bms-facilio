package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.attendance.AttendanceSettings;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class GetAttendanceSettingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        AttendanceSettings settings = AttendanceAPI.getAttendanceSettings();
        context.put("attendanceSettings", settings);
        return false;
    }
}
