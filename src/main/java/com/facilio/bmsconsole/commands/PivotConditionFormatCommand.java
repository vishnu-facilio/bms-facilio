package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.fields.*;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportPivotTableDataContext;
import com.facilio.report.context.ReportPivotTableRowsContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
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
    HashMap<String,Object> JsonTable;
    List<Map<String, Object>> pivotRecords;
    Map<String, Object> table;
    private ModuleBean modBean;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        report = (com.facilio.report.context.ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        rowHeaders= (List<String>) context.get(FacilioConstants.ContextNames.ROW_HEADERS);
        columnHeaders = (List<String>) context.get(FacilioConstants.ContextNames.DATA_HEADERS);
        rowAlias = (Map<String, Object>) context.get(FacilioConstants.ContextNames.ROW_ALIAS);
        columnAlias = (Map<String, Object>) context.get(FacilioConstants.ContextNames.DATA_ALIAS);
        rowColumns = (List<ReportPivotTableRowsContext>) context.get(FacilioConstants.Reports.ROWS);
        dataColumns = (List<ReportPivotTableDataContext>) context.get(FacilioConstants.Reports.DATA);
        templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        JsonTable = (HashMap<String, Object>) templateJson.get("columnFormatting");
        pivotRecords = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);
        globalContext = context;
        table = reconstruct();
        context.put("restructuredPivotTable", table);
        return false;
    }

    private void processTextAlign(String alias){

    }

    private String valueFormatter(Object value, String alias) throws Exception {
        FacilioField facilioField = null;
        String result = null;
        Map<String, Object> columnFormat = new HashMap<>();

        for(ReportPivotTableRowsContext row : rowColumns){
            if(row.getAlias().equals(alias)){
                facilioField = modBean.getField(row.getField().getId());
            }
        }

        for(ReportPivotTableDataContext data : dataColumns){
            if(data.getAlias().equals(alias)){
                facilioField = modBean.getField(data.getField().getName(),data.getModuleName());
            }
        }
        if(facilioField == null || value == null){
            return null;
        }
        Map<String, Object> columnConfig = (Map<String, Object>) JsonTable.get(alias);
        if(columnConfig.containsValue("format")){
            columnFormat = (Map<String, Object>) columnConfig.get("format");
        }
        switch (facilioField.getDataTypeEnum()){
            case DECIMAL:
                result = String.valueOf(value);
                break;
            case BOOLEAN:
                result = value.toString();
                break;
            case ENUM:
                result = value.toString();
                break;
            case SYSTEM_ENUM:
                result = value.toString();
                break;
            case NUMBER:
                result = value.toString();
                break;
            default:
                result = value.toString();
        }
        if(facilioField instanceof NumberField){
            NumberField numberField = (NumberField) facilioField;
            if(numberField.getUnit() != null) {
                result = unitFormatter(result, numberField);
            }

        }
        return result;
    }

    private String unitConverter(String value, int from, int to){
        return null;
    }
    private String unitFormatter(String value, NumberField field) throws Exception {
        return null;
    }

    private Map<String, Object> reconstruct() throws Exception {
        LinkedHashMap<String,String> aliasVsDisplayNameRows = new LinkedHashMap<>();
        LinkedHashMap<String,String> aliasVsDisplayNameData = new LinkedHashMap<>();

        Map<String, Object> headers = new HashMap<>();
        List<Map<String, Object>> records = new ArrayList<>();

        for(ReportPivotTableRowsContext row : rowColumns)
        {
            String key = row.getAlias();
            Map<String,Object> data = (Map<String, Object>) JsonTable.get(key);
            headers.put(key, data.get("label"));
            aliasVsDisplayNameRows.put(key,(String) data.get("label"));
        }

        for(ReportPivotTableDataContext prop : dataColumns)
        {
            String key = prop.getAlias();
            Map<String,Object> data = (Map<String, Object>) JsonTable.get(key);
            headers.put(key, data.get("label"));
            aliasVsDisplayNameData.put(key,(String) data.get("label"));
        }

        for(Map<String, Object> record: pivotRecords){
            LinkedHashMap<String, Object> tempRecord = new LinkedHashMap<>();
            Map<String,Object> row = (Map<String, Object>) record.get("rows");
            Map<String,Object> data = (Map<String, Object>) record.get("data");
            for (String actualKey: aliasVsDisplayNameRows.keySet()) {
                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put("value", row.get(actualKey));
                tempMap.put("formattedValue", valueFormatter(row.get(actualKey), actualKey));
                tempRecord.put(actualKey, tempMap);
            }
            for (String actualKey: aliasVsDisplayNameData.keySet()) {
                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put("value", data.get(actualKey));
                tempMap.put("formattedValue", valueFormatter(data.get(actualKey), actualKey));
                tempRecord.put(actualKey, tempMap);
            }
            records.add(tempRecord);
        }
        Map<String, Object> table = new HashMap<String, Object>();
        table.put("headers", headers);
        table.put("records", records);
        return table;
    }

}
