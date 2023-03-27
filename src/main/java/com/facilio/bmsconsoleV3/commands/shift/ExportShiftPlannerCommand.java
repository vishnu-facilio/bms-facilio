package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import java.util.*;
import java.util.stream.Collectors;

public class ExportShiftPlannerCommand extends FacilioCommand {


    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long rangeFrom = (Long) context.get(FacilioConstants.Shift.RANGE_FROM);
        Long rangeTo = (Long) context.get(FacilioConstants.Shift.RANGE_TO);

        String orgTz = AccountUtil.getCurrentOrg().getTimezone();
        if (!FacilioUtil.isEmptyOrNull(orgTz)){
            rangeFrom += TimeZone.getTimeZone(orgTz).getRawOffset();
            rangeTo += TimeZone.getTimeZone(orgTz).getRawOffset();
        }

        FileInfo.FileFormat fileType = context.get(FacilioConstants.Shift.FORMAT).equals("csv") ?
                FileInfo.FileFormat.CSV : FileInfo.FileFormat.XLS;

        Boolean exportList = (Boolean) context.get(FacilioConstants.Shift.EXPORT_LIST);

        String url = "";
        if (exportList != null && exportList) {
            Long employeeID = (Long) context.get(FacilioConstants.Shift.EMPLOYEE_ID);
            url = exportListView(fileType, rangeFrom, rangeTo, employeeID);
        } else {
            List<V3EmployeeContext> employees =
                    (List<V3EmployeeContext>) context.get(FacilioConstants.Shift.EMPLOYEES);
            url = exportCalendarView(fileType, rangeFrom, rangeTo, employees);
        }
        context.put(FacilioConstants.Shift.URL, url);
        return false;
    }

    private String exportCalendarView(FileInfo.FileFormat fileType, Long rangeFrom, Long rangeTo, List<V3EmployeeContext> employees) throws Exception {
        Map<String, Object> table = new HashMap<>();
        table.put("headers", getCalendarExportHeaders(rangeFrom, rangeTo));

        ArrayList employeeShifts = new ArrayList();
        for (V3EmployeeContext emp: employees) {
            List<Map<String, Object>> pShifts = ShiftAPI.getShiftListDecoratedWithWeeklyOff(emp.getId(), rangeFrom, rangeTo);
            List<String> row = new ArrayList<>();
            row.add(emp.getName());
            row.addAll(pShifts.stream().map(s -> (String) s.get("name")).collect(Collectors.toList()));
            employeeShifts.add(row);
        }
        table.put("records", employeeShifts);
        String fileName = "shift-planner-calendar-export";
        return ExportUtil.exportData(fileType, fileName, table, false);
    }

    private ArrayList<String> getCalendarExportHeaders(Long rangeFrom, Long rangeTo) {
        ArrayList<String> list = new ArrayList<>();
        list.add("employees");
        list.addAll(ShiftAPI.computeRange(rangeFrom, rangeTo)
                .stream()
                .map(d ->   ShiftAPI.epochToReadableDate(d))
                .collect(Collectors.toList()));
        return list;
    }

    private String exportListView(FileInfo.FileFormat fileType, Long rangeFrom, Long rangeTo, Long employeeID) throws Exception {
        List<Map<String, Object>> shifts = ShiftAPI.getShiftListDecoratedWithWeeklyOff(employeeID, rangeFrom, rangeTo);
        Map<String, Object> table = new HashMap<>();
        table.put("headers", getListExportHeaders());
        table.put("records", shifts);

        PeopleContext people = PeopleAPI.getPeopleForId(employeeID);
        String fileName = people.getName() +"-shift-planner-list-export";
        return ExportUtil.exportData(fileType, fileName, table, false);
    }

    private ArrayList<String> getListExportHeaders() {
        ArrayList<String> list = new ArrayList<>();
        list.add("date");
        list.add("name");
        list.add("startTime");
        list.add("endTime");
        return list;
    }
}
