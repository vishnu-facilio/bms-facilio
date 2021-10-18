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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        JSONObject templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        Map<String,Object> JsonTable = (Map<String, Object>) templateJson.get("columnFormatting");
        Map<String,String> aliasVsDisplayName = new HashMap<>();


        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> pivotRecords = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);
        List<Map<String, Object>> records = new ArrayList<>();

        for(String key : JsonTable.keySet())
        {
            Map<String,Object> data = (Map<String, Object>) JsonTable.get(key);
            headers.add((String) data.get("label"));
            aliasVsDisplayName.put(key,(String) data.get("label"));
        }

        for(Map<String, Object> record: pivotRecords){
            Map<String, Object> tempRecord = new HashMap<>();
            for(String key: record.keySet()){
                Map<String,Object> column = (Map<String, Object>) record.get(key);
                for (String actualKey: column.keySet()) {
                    tempRecord.put(aliasVsDisplayName.get(actualKey), column.get(actualKey));
                }
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
