package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportPivotTableDataContext;
import com.facilio.report.context.ReportPivotTableRowsContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

public class PivotConditionFormatCommand extends FacilioCommand {
    ReportContext report;
    Context globalContext;
    List<String> rowHeaders;
    List<String>  columnHeaders;
    Map<String, Object> rowAlias;
    Map<String, Object> columnAlias;
    List<ReportPivotTableRowsContext> rowColumns;
    List<ReportPivotTableDataContext> dataColumns;
    JSONObject templateJson;
    LinkedHashMap<String,Object> JsonTable;
    List<Map<String, Object>> pivotRecords;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        report = (com.facilio.report.context.ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        rowHeaders= (List<String>) context.get(FacilioConstants.ContextNames.ROW_HEADERS);
        columnHeaders = (List<String>) context.get(FacilioConstants.ContextNames.DATA_HEADERS);
        rowAlias = (Map<String, Object>) context.get(FacilioConstants.ContextNames.ROW_ALIAS);
        columnAlias = (Map<String, Object>) context.get(FacilioConstants.ContextNames.DATA_ALIAS);
        rowColumns = (List<ReportPivotTableRowsContext>) context.get(FacilioConstants.Reports.ROWS);
        dataColumns = (List<ReportPivotTableDataContext>) context.get(FacilioConstants.Reports.DATA);
        templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        JsonTable = (LinkedHashMap<String, Object>) templateJson.get("columnFormatting");
        pivotRecords = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);
        globalContext = context;
        Map<String, Object> table = reconstruct();
        context.put("restructuredPivotTable", table);
        return false;
    }

    private void processTextAlign(String alias){

    }

    private void valueFormatter(String alias){

    }

    private Map<String, Object> reconstruct(){
        LinkedHashMap<String,String> aliasVsDisplayNameRows = new LinkedHashMap<>();
        LinkedHashMap<String,String> aliasVsDisplayNameData = new LinkedHashMap<>();

        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> records = new ArrayList<>();

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
                tempRecord.put(actualKey, row.get(actualKey));
            }
            for (String actualKey: aliasVsDisplayNameData.keySet()) {
                tempRecord.put(actualKey, data.get(actualKey));
            }
            records.add(tempRecord);
        }
        Map<String, Object> table = new HashMap<String, Object>();
        table.put("headers", headers);
        table.put("records", records);
        return table;
    }

}
