package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportPivotTableDataContext;
import com.facilio.report.context.ReportPivotTableRowsContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class ExportPivotReport extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Boolean isS3Url = (Boolean) context.get("isS3Url");
        if (isS3Url == null) {
            isS3Url = false;
        }

        ReportContext report = (com.facilio.report.context.ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        FileInfo.FileFormat fileFormat = (FileInfo.FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
        String fileUrl = null;
        String fileName = "Report Data";
        if (StringUtils.isNotEmpty(report.getName())) {
            fileName = report.getName();
        }
        fileName += " - " + DateTimeUtil.getFormattedTime(System.currentTimeMillis(), "dd-MM-yyyy HH-mm");

        List<String> rowHeaders= (List<String>) context.get(FacilioConstants.ContextNames.ROW_HEADERS);
        List<String>  columnHeaders = (List<String>) context.get(FacilioConstants.ContextNames.DATA_HEADERS);
        Map<String, Object>  rowAlias = (Map<String, Object>) context.get(FacilioConstants.ContextNames.ROW_ALIAS);
        Map<String, Object> columnAlias = (Map<String, Object>) context.get(FacilioConstants.ContextNames.DATA_ALIAS);
        List<ReportPivotTableRowsContext> rowColumns = (List<ReportPivotTableRowsContext>) context.get(FacilioConstants.Reports.ROWS);
        List<ReportPivotTableDataContext> dataColumns = (List<ReportPivotTableDataContext>) context.get(FacilioConstants.Reports.DATA);
        JSONObject templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        LinkedHashMap<String,Object> JsonTable = (LinkedHashMap<String, Object>) templateJson.get("columnFormatting");
        LinkedHashMap<String,String> aliasVsDisplayNameRows = new LinkedHashMap<>();
        LinkedHashMap<String,String> aliasVsDisplayNameData = new LinkedHashMap<>();

        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> pivotRecords = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);
        List<Map<String, Object>> records = new ArrayList<>();

//        for(String key : JsonTable.keySet())
//        {
//            Map<String,Object> data = (Map<String, Object>) JsonTable.get(key);
//            headers.add((String) data.get("label"));
//            aliasVsDisplayName.put(key,(String) data.get("label"));
//        }

        for(ReportPivotTableRowsContext row : rowColumns)
        {
            String key = row.getAlias();
            Map<String,Object> data = (Map<String, Object>) JsonTable.get(key);
            headers.add((String) data.get("label"));
            aliasVsDisplayNameRows.put(key,(String) data.get("label"));
        }

        for(ReportPivotTableDataContext prop : dataColumns)
        {
            String key = prop.getAlias();
            Map<String,Object> data = (Map<String, Object>) JsonTable.get(key);
            headers.add((String) data.get("label"));
            aliasVsDisplayNameData.put(key,(String) data.get("label"));
        }

        for(Map<String, Object> record: pivotRecords){
            LinkedHashMap<String, Object> tempRecord = new LinkedHashMap<>();
            Map<String,Object> row = (Map<String, Object>) record.get("rows");
            Map<String,Object> data = (Map<String, Object>) record.get("data");
            for (String actualKey: aliasVsDisplayNameRows.keySet()) {
                tempRecord.put(aliasVsDisplayNameRows.get(actualKey), row.get(actualKey));
            }
            for (String actualKey: aliasVsDisplayNameData.keySet()) {
                tempRecord.put(aliasVsDisplayNameData.get(actualKey), data.get(actualKey));
            }
            records.add(tempRecord);
        }
        Map<String, Object> table = new HashMap<String, Object>();
        table.put("headers", headers);
        table.put("records", records);
        fileUrl = ExportUtil.exportData(fileFormat, fileName, table, isS3Url);
        context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
        context.put(FacilioConstants.ContextNames.FILE_NAME, fileName);
        return false;
    }
}
