package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.PivotDataColumnContext;
import com.facilio.report.context.PivotFormulaColumnContext;
import com.facilio.report.context.PivotRowColumnContext;
import com.facilio.report.formatter.DecimalFormatter;
import com.facilio.report.formatter.Formatter;
import com.facilio.report.formatter.NumberFormatter;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

public class PivotColumnFormatCommand extends FacilioCommand {
    List<PivotRowColumnContext> rowColumns;
    List<PivotDataColumnContext> dataColumns;
    List<PivotFormulaColumnContext> formulaColumns;
    JSONObject templateJson;
    HashMap<String, Object> JsonTable;
    List<Map<String, Object>> pivotRecords;
    Map<String, Object> table;
    Map<String, FacilioField> aliasVsFieldMap = new HashMap<>();
    Map<String, Formatter> formatterMap = new HashMap<>();
    private ModuleBean modBean;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        rowColumns = (List<PivotRowColumnContext>) context.get(FacilioConstants.Reports.ROWS);
        dataColumns = (List<PivotDataColumnContext>) context.get(FacilioConstants.Reports.DATA);
        formulaColumns = (List<PivotFormulaColumnContext>) context.get(FacilioConstants.ContextNames.FORMULA);
        templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        JsonTable = (HashMap<String, Object>) templateJson.get("columnFormatting");
        pivotRecords = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);
        table = reconstruct();
        context.put(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA, table);
        context.put(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD, aliasVsFieldMap);
        return false;
    }

    private FacilioField getField(String alias) throws Exception {
        FacilioField facilioField = null;

        for (PivotRowColumnContext row : rowColumns) {
            if (row.getAlias().equals(alias)) {
                facilioField = modBean.getField(row.getField().getId());
            }
        }

        for (PivotDataColumnContext data : dataColumns) {
            if (data.getModuleType().equals("1")) {
                if (data.getAlias().equals(alias)) {
                    facilioField = modBean.getField(data.getField().getName(), data.getModuleName());
                }
            } else {
                if (data.getAlias().equals(alias)) {
                    facilioField = modBean.getReadingField(data.getReadingField().getId());
                }
            }
        }
        return facilioField;
    }

    private JSONObject getColumnFormatMap(String alias) {
        JSONObject columnFormat = new JSONObject();
        Map<String, Object> columnConfig = (Map<String, Object>) JsonTable.get(alias);
        if (columnConfig.containsKey("format")) {
            Map<String, Object> tempColumnMap = (Map<String, Object>) columnConfig.get("format");
            columnFormat.putAll(tempColumnMap);
        }

        return columnFormat;
    }

    private void setColumnFormatMap(String alias, JSONObject formatterMap) {
        Map<String, Object> columnConfig = (Map<String, Object>) JsonTable.get(alias);
        if (formatterMap != null) {
            columnConfig.put("format", formatterMap);
        }
    }

    private Map<String, String> getColumnStyle(String alias) throws Exception {
        Map<String, String> styleMap = new HashMap<>();
        FacilioField facilioField = getField(alias);
        Map<String, Object> columnFormatMap = getColumnFormatMap(alias);
        if (facilioField == null) {
            styleMap.put("textAlign", "right");
            return styleMap;
        }
        if (columnFormatMap.containsKey("textAlign")) {
            styleMap.put("textAlign", (String) columnFormatMap.get("textAlign"));
        } else {
            String textAlign;
            switch (facilioField.getDataTypeEnum()) {
                case DECIMAL:
                case NUMBER:
                case ID:
                    textAlign = "right";
                    break;
                case BOOLEAN:
                case ENUM:
                case SYSTEM_ENUM:
                    textAlign = "center";
                    break;
                default:
                    textAlign = "left";
            }
            styleMap.put("textAlign", textAlign);
        }

        return styleMap;
    }

    private Map<String, Object> reconstruct() throws Exception {
        LinkedHashMap<String, String> aliasVsDisplayNameRows = new LinkedHashMap<>();
        LinkedHashMap<String, String> aliasVsDisplayNameData = new LinkedHashMap<>();
        LinkedHashMap<String, String> aliasVsDisplayNameFormula = new LinkedHashMap<>();

        Map<String, Object> headers = new HashMap<>();
        List<Map<String, Object>> records = new ArrayList<>();

        for (PivotRowColumnContext row : rowColumns) {
            String key = row.getAlias();
            Map<String, Object> data = (Map<String, Object>) JsonTable.get(key);
            StringJoiner header = new StringJoiner("");
            header.add(data.get("label").toString());
            aliasVsFieldMap.put(key, getField(key));
            JSONObject columnFormatMap = getColumnFormatMap(key);
            Formatter formatter = Formatter.getInstance(getField(key));

            if (formatter != null && !columnFormatMap.isEmpty()) {
                formatter.deserialize(columnFormatMap);
            } else if (formatter != null) {
                setColumnFormatMap(key, formatter.serialize());
            }

            if (columnFormatMap.containsKey("headerUnit")) {
                boolean headerUnit = Boolean.parseBoolean(columnFormatMap.get("headerUnit").toString());
                if (headerUnit) {
                    if (formatter instanceof DecimalFormatter) {
                        DecimalFormatter decimalFormatter = (DecimalFormatter) formatter;
                        header.add(" ( " + decimalFormatter.getUnit() + " )");
                    } else if (formatter instanceof NumberFormatter) {
                        NumberFormatter numberFormatter = (NumberFormatter) formatter;
                        header.add(" ( " + numberFormatter.getUnit() + " )");
                    }
                }
            }

            formatterMap.put(key, formatter);
            headers.put(key, header.toString());
            aliasVsDisplayNameRows.put(key, header.toString());
        }

        for (PivotDataColumnContext prop : dataColumns) {
            String key = prop.getAlias();
            Map<String, Object> data = (Map<String, Object>) JsonTable.get(key);
            StringJoiner header = new StringJoiner("");
            header.add(data.get("label").toString());
            aliasVsFieldMap.put(key, getField(key));
            JSONObject columnFormatMap = getColumnFormatMap(key);
            Formatter formatter = Formatter.getInstance(getField(key));

            if (formatter != null && !columnFormatMap.isEmpty()) {
                formatter.deserialize(columnFormatMap);
            } else if (formatter != null) {
                setColumnFormatMap(key, formatter.serialize());
            }

            if (columnFormatMap.containsKey("headerUnit")) {
                boolean headerUnit = Boolean.parseBoolean(columnFormatMap.get("headerUnit").toString());
                if (headerUnit) {
                    if (formatter instanceof DecimalFormatter) {
                        DecimalFormatter decimalFormatter = (DecimalFormatter) formatter;
                        header.add(" ( " + decimalFormatter.getUnit() + " )");
                    } else if (formatter instanceof NumberFormatter) {
                        NumberFormatter numberFormatter = (NumberFormatter) formatter;
                        header.add(" ( " + numberFormatter.getUnit() + " )");
                    }
                }
            }
            formatterMap.put(key, formatter);
            headers.put(key, header.toString());
            aliasVsDisplayNameData.put(key, header.toString());
        }

        for (PivotFormulaColumnContext prop : formulaColumns) {
            String key = prop.getAlias();
            Map<String, Object> data = (Map<String, Object>) JsonTable.get(key);
            StringJoiner header = new StringJoiner("");
            header.add(data.get("label").toString());
            JSONObject columnFormatMap = getColumnFormatMap(key);

            Formatter formatter = Formatter.getInstance(prop.toFacilioField());

            if (formatter != null && !columnFormatMap.isEmpty()) {
                formatter.deserialize(columnFormatMap);
            } else if (formatter != null) {
                setColumnFormatMap(key, formatter.serialize());
            }

            if (columnFormatMap.containsKey("headerUnit")) {
                boolean headerUnit = Boolean.parseBoolean(columnFormatMap.get("headerUnit").toString());
                if (headerUnit) {
                    if (formatter instanceof DecimalFormatter) {
                        DecimalFormatter decimalFormatter = (DecimalFormatter) formatter;
                        header.add(" ( " + decimalFormatter.getUnit() + " )");
                    } else if (formatter instanceof NumberFormatter) {
                        NumberFormatter numberFormatter = (NumberFormatter) formatter;
                        header.add(" ( " + numberFormatter.getUnit() + " )");
                    }
                }
            }
            formatterMap.put(key, formatter);
            headers.put(key, header.toString());
            aliasVsDisplayNameFormula.put(key, header.toString());
        }

        for (Map<String, Object> record : pivotRecords) {
            LinkedHashMap<String, Object> tempRecord = new LinkedHashMap<>();
            Map<String, Object> row = (Map<String, Object>) record.get("rows");
            Map<String, Object> data = (Map<String, Object>) record.get("data");
            Map<String, Object> formula = (Map<String, Object>) record.get("formula");

            for (String actualKey : aliasVsDisplayNameRows.keySet()) {
                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put("value", row.get(actualKey));
                Formatter formatter = formatterMap.get(actualKey);
                if (formatter != null && row.get(actualKey) != null && !row.get(actualKey).equals("")) {
                    tempMap.put("formattedValue", formatter.format(row.get(actualKey)));
                } else {
                    tempMap.put("formattedValue", row.get(actualKey));
                }
                tempMap.put("style", getColumnStyle(actualKey));
                tempRecord.put(actualKey, tempMap);
            }
            for (String actualKey : aliasVsDisplayNameData.keySet()) {
                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put("value", data.get(actualKey));
                Formatter formatter = formatterMap.get(actualKey);
                if (formatter != null && data.get(actualKey) != null && !data.get(actualKey).equals("")) {
                    tempMap.put("formattedValue", formatter.format(data.get(actualKey)));
                } else {
                    tempMap.put("formattedValue", data.get(actualKey));
                }
                tempMap.put("style", getColumnStyle(actualKey));
                tempRecord.put(actualKey, tempMap);
            }
            for (String actualKey : aliasVsDisplayNameFormula.keySet()) {
                Map<String, Object> tempMap = new HashMap<>();
                if (formula == null) {
                    continue;
                }
                tempMap.put("value", formula.get(actualKey));
                Formatter formatter = formatterMap.get(actualKey);
                if (formatter != null && formula.get(actualKey) != null && !formula.get(actualKey).equals("")) {
                    tempMap.put("formattedValue", formatter.format(formula.get(actualKey)));
                } else {
                    tempMap.put("formattedValue", formula.get(actualKey));
                }
                tempMap.put("style", getColumnStyle(actualKey));
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
