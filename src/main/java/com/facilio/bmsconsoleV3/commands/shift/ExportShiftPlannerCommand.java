package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
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
            Long peopleId = (Long) context.get(FacilioConstants.ContextNames.PEOPLE_ID);
            url = exportListView(fileType, rangeFrom, rangeTo, peopleId);
        } else {
            List<V3PeopleContext> peopleList =
                    (List<V3PeopleContext>) context.get(FacilioConstants.ContextNames.PEOPLE);
            url = exportCalendarView(fileType, rangeFrom, rangeTo, peopleList);
        }
        context.put(FacilioConstants.Shift.URL, url);
        return false;
    }

    private String exportCalendarView(FileInfo.FileFormat fileType, Long rangeFrom, Long rangeTo, List<V3PeopleContext> peopleList) throws Exception {
        Map<String, Object> table = new HashMap<>();
        table.put("headers", getCalendarExportHeaders(rangeFrom, rangeTo));

        ArrayList peopleShifts = new ArrayList();
        for (V3PeopleContext people: peopleList) {
            List<Map<String, Object>> pShifts = ShiftAPI.getShiftListDecoratedWithWeeklyOff(people.getId(), rangeFrom, rangeTo);
            List<String> row = new ArrayList<>();
            row.add(people.getName());
            row.addAll(pShifts.stream().map(s -> (String) s.get("name")).collect(Collectors.toList()));
            peopleShifts.add(row);
        }
        table.put("records", peopleShifts);
        String fileName = "shift-planner-calendar-export";
        return ExportUtil.exportData(fileType, fileName, table, false);
    }

    private ArrayList<String> getCalendarExportHeaders(Long rangeFrom, Long rangeTo) {
        ArrayList<String> list = new ArrayList<>();
        list.add("people");
        list.addAll(ShiftAPI.computeRange(rangeFrom, rangeTo)
                .stream()
                .map(d ->   ShiftAPI.epochToReadableDate(d))
                .collect(Collectors.toList()));
        return list;
    }

    private String exportListView(FileInfo.FileFormat fileType, Long rangeFrom, Long rangeTo, Long peopleId) throws Exception {
        List<Map<String, Object>> shifts = ShiftAPI.getShiftListDecoratedWithWeeklyOff(peopleId, rangeFrom, rangeTo);
        Map<String, Object> table = new HashMap<>();
        table.put("headers", getListExportHeaders());
        table.put("records", shifts);

        PeopleContext people = PeopleAPI.getPeopleForId(peopleId);
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
