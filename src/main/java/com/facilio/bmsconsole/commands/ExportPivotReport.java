package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
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

        ReportContext report = (com.facilio.report.context.ReportContext) context
                .get(FacilioConstants.ContextNames.REPORT);
        FileInfo.FileFormat fileFormat = (FileInfo.FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
        String fileUrl = null;
        String fileName = "Report Data";
        if (StringUtils.isNotEmpty(report.getName())) {
            fileName = report.getName();
        }
        fileName += " - " + DateTimeUtil.getFormattedTime(System.currentTimeMillis(), "dd-MM-yyyy HH-mm");

        List<String> rowHeaders = (List<String>) context.get(FacilioConstants.ContextNames.ROW_HEADERS);
        List<String> columnHeaders = (List<String>) context.get(FacilioConstants.ContextNames.DATA_HEADERS);
        List<String> formulaHeaders = (List<String>) context.get(FacilioConstants.ContextNames.FORMULA_HEADERS);
        JSONObject templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        LinkedHashMap<String, Object> JsonTable = (LinkedHashMap<String, Object>) templateJson.get("columnFormatting");
        LinkedHashMap<String, String> aliasVsDisplayName = new LinkedHashMap<>();

        List<String> headers = new ArrayList<>();
        Map<String, Object> pivotTable = null;
        try {
            pivotTable = ReportUtil.sortPivotTableData(context);
        }catch (Exception e){
            pivotTable = (Map<String, Object>) context.get(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA);
        }
        List<Map<String, Object>> pivotRecords = (List<Map<String, Object>>) pivotTable.get("records");
        List<Map<String, Object>> records = new ArrayList<>();
        if(!templateJson.containsKey("theme") || templateJson.get("theme") == null)
        {
            Map<String,Object> theme = (Map<String,Object>) templateJson.get("theme");
            if(!theme.containsKey("number") || theme.get("number") == null)
            {
                String rowNumberKey = "number";
                String rowDisplayName = "#";
                boolean rowNumber = (boolean) theme.get("number");

                if(rowNumber){
                    headers.add(rowDisplayName);
                    aliasVsDisplayName.put(rowNumberKey, rowDisplayName);
                }
            }
        }


        for (String key : rowHeaders) {
            Map<String, Object> data = (Map<String, Object>) JsonTable.get(key);
            String label = new StringBuilder((String)data.get("label")).append("___").append(key).toString();
            headers.add(label);
            aliasVsDisplayName.put(key, label);
        }
        for (String key : columnHeaders) {
            Map<String, Object> data = (Map<String, Object>) JsonTable.get(key);
            String label = new StringBuilder((String)data.get("label")).append("___").append(key).toString();
            headers.add(label);
            aliasVsDisplayName.put(key, label);
        }

        for (String key : formulaHeaders) {
            Map<String, Object> data = (Map<String, Object>) JsonTable.get(key);
            String label = new StringBuilder((String)data.get("label")).append("___").append(key).toString();
            headers.add(label);
            aliasVsDisplayName.put(key, label);
        }

        for (Map<String, Object> record : pivotRecords) {
            LinkedHashMap<String, Object> tempRecord = new LinkedHashMap<>();
            for (String alias : aliasVsDisplayName.keySet()) {
                Map<String, Object> data = (Map<String, Object>) record.get(alias);
                tempRecord.put(aliasVsDisplayName.get(alias), data.get("formattedValue"));
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
