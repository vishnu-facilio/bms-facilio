package com.facilio.bmsconsoleV3.commands.attendance;

import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import com.jcraft.jsch.HASH;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListAttendanceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        validateProps(context);

        Long from = (Long) context.get(FacilioConstants.Attendance.RANGE_FROM);
        Long to = (Long) context.get(FacilioConstants.Attendance.RANGE_TO);
        Long peopleID = (Long) context.get(FacilioConstants.Attendance.PEOPLE_ID);

        List<Attendance> attendance = AttendanceAPI.fetchAttendanceInRange(peopleID, from, to);

        context.put(FacilioConstants.Attendance.ATTENDANCE, attendance);

        List<Long> shifts = attendance
                .stream()
                .filter(a -> a.getShift() != null)
                .map(a -> a.getShift().getId())
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(shifts)){
            return false;
        }

        List<Shift> associatedShifts = ShiftAPI.getShifts(shifts);
        Map<Long, Shift> shiftMapping= new HashMap<>();
        for (Shift s : associatedShifts) {
            shiftMapping.put(s.getId(), s);
        }
        context.put(FacilioConstants.Shift.SHIFTS, shiftMapping);
        return false;
    }

    private void validateProps(Context context) {
        Long from = (Long) context.get(FacilioConstants.Attendance.RANGE_FROM);
        if (from == null || from <= 0) {
            throw new IllegalArgumentException("rangeFrom is a mandatory prop");
        }
        Long to = (Long) context.get(FacilioConstants.Attendance.RANGE_TO);
        if (to == null || to <= 0) {
            throw new IllegalArgumentException("rangeTo is a mandatory prop");
        }
        Long peopleID = (Long) context.get(FacilioConstants.Attendance.PEOPLE_ID);
        if (peopleID == null || peopleID <= 0) {
            throw new IllegalArgumentException("peopleID is a mandatory prop");
        }
    }
}
