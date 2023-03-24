package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.v3.context.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AttendanceJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(AttendanceJob.class.getName());

    @Override
    public void execute(JobContext jobContext) throws Exception {

        if (attendanceLicenseNotEnabled()) {
            return;
        }
        LOGGER.debug("marking previous day attendance");

        Long previousDay = ShiftAPI.getTodayEpochDate() - ShiftAPI.DAY_IN_MILLIS;
        List<V3PeopleContext> people = AttendanceAPI.getPeopleWithoutAttendance(previousDay);

        List<Attendance> attendanceToBeAdded = new ArrayList<>();

        for (V3PeopleContext p : people) {
            Shift shift = ShiftAPI.getPeopleShiftForDay(p.getId(), previousDay);
            Attendance.Status prevDayStatus = shift.isWeeklyOff(previousDay) ?
                    Attendance.Status.WEEKLY_OFF :
                    Attendance.Status.ABSENT;
            attendanceToBeAdded.add(new Attendance(previousDay, prevDayStatus));
        }

        FacilioModule attendanceMod =
                Constants.getModBean().getModule(FacilioConstants.ContextNames.ATTENDANCE);

        List<FacilioField> attendanceFields =
                Constants.getModBean().getAllFields(FacilioConstants.ContextNames.ATTENDANCE);

        InsertRecordBuilder<Attendance> insert = new InsertRecordBuilder<Attendance>()
                .module(attendanceMod)
                .fields(attendanceFields)
                .addRecords(attendanceToBeAdded);
        insert.save();

        LOGGER.debug("marked " + attendanceToBeAdded.size() + " attendances for " + previousDay);

    }

    private static boolean attendanceLicenseNotEnabled() throws Exception {
        return !AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.ATTENDANCE);
    }
}
