package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.report.context.PivotDataColumnContext;
import com.facilio.report.context.PivotFormulaColumnContext;
import com.facilio.report.context.PivotRowColumnContext;
import com.facilio.report.formatter.*;
import com.facilio.report.formatter.Formatter;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.util.*;

public class PivotColumnFormatCommand extends FacilioCommand {
    List<PivotRowColumnContext> rowColumns;
    List<PivotDataColumnContext> dataColumns;
    List<PivotFormulaColumnContext> formulaColumns;
    JSONObject templateJson;
    HashMap<String, Object> JsonTable;
    List<Map<String, Object>> pivotRecords;
    Map<String, FacilioField> aliasVsFieldMap = new HashMap<>();
    Map<String, Formatter> formatterMap = new HashMap<>();
    Map<String, Object> lookupMap;
    private ModuleBean modBean;
    Map<String, Integer> alias_vs_aggr= new HashMap<>();

    @Override
    public boolean executeCommand(Context context) throws Exception {
        modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        rowColumns = (List<PivotRowColumnContext>) context.get(FacilioConstants.Reports.ROWS);
        dataColumns = (List<PivotDataColumnContext>) context.get(FacilioConstants.Reports.DATA);
        formulaColumns = (List<PivotFormulaColumnContext>) context.get(FacilioConstants.ContextNames.FORMULA);
        templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        JsonTable = (HashMap<String, Object>) templateJson.get("columnFormatting");
        pivotRecords = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);
        lookupMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.PIVOT_LOOKUP_MAP);
        if(dataColumns != null && dataColumns.size() > 0) {
            for(PivotDataColumnContext dataColumn : dataColumns){
                alias_vs_aggr.put(dataColumn.getAlias(), dataColumn.getAggr());
            }
        }
        Map<String,Object> params = formatHeaders();
        Map<String, Object> table = formatPivotRecords(params);

        context.put(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA, table);
        context.put(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD, aliasVsFieldMap);

        return false;
    }

    private FacilioField getField(String alias) throws Exception {
        FacilioField facilioField = null;

        for (PivotRowColumnContext row : rowColumns) {
            if (row.getAlias().equals(alias)) {
                if(row.getField().getId() <=0 && row.getField().getName() != null && row.getField().getName().equals("siteId")){
                    facilioField = FieldFactory.getSiteField(modBean.getModule(row.getField().getModuleId()));
                }
                else
                {
                    facilioField = modBean.getField(row.getField().getId()).clone();
                }
            }
        }

        for (PivotDataColumnContext data : dataColumns) {
            if (data.getModuleType().equals("1")) {
                if (data.getAlias().equals(alias)) {
                    facilioField = modBean.getField(data.getField().getName(), data.getModuleName()).clone();
                }
            } else {
                if (data.getAlias().equals(alias) && data.getReadingField().getId() > 0) {
                    if(data.getReadingField().getModuleId() > 0)
                    {
                        FacilioModule module = modBean.getModule(data.getReadingField().getModuleId());
                        if(module != null && (module.getTypeEnum() == FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA || module.getTypeEnum() == FacilioModule.ModuleType.READING_RULE || module.getTypeEnum() == FacilioModule.ModuleType.LIVE_FORMULA)){
                            facilioField =  modBean.getField(data.getReadingField().getId()).clone();
                        }else{
                            facilioField = modBean.getReadingField(data.getReadingField().getId()).clone();
                        }
                    }
                } else if(data.getAlias().equals(alias) && data.getReadingField().getModuleId() > 0 &&  data.getReadingField().getName() != null){
                    FacilioModule module = modBean.getModule(data.getReadingField().getModuleId());
                    facilioField = modBean.getField(data.getReadingField().getName(), module.getName());
                }
            }
        }

        for (PivotFormulaColumnContext formula : formulaColumns) {
            if (formula.getAlias().equals(alias)) {
                facilioField = formula.toFacilioField().clone();
            }
        }

        return facilioField;
    }


    private FacilioField getField(String alias, String header) throws Exception {
        FacilioField facilioField = getField(alias);
        if(facilioField != null){
            facilioField.setName(alias);
            facilioField.setDisplayName(header);
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
    private String applyHeaderUnit(String header, boolean headerUnit, Formatter formatter){
        StringJoiner headerString = new StringJoiner("");
        headerString.add(header);
        if (headerUnit) {
            if (formatter instanceof DecimalFormatter) {
                DecimalFormatter decimalFormatter = (DecimalFormatter) formatter;
                headerString.add(" ( " + decimalFormatter.getUnit() + " )");
            } else if (formatter instanceof NumberFormatter) {
                NumberFormatter numberFormatter = (NumberFormatter) formatter;
                headerString.add(" ( " + numberFormatter.getUnit() + " )");
            }
        }
        return headerString.toString();
    }
    private Map<String, Object> formatHeaders() throws Exception {

        Map<String, Object> params = new HashMap<>();

        LinkedHashMap<String, String> aliasVsDisplayNameRows = new LinkedHashMap<>();
        LinkedHashMap<String, String> aliasVsDisplayNameData = new LinkedHashMap<>();
        LinkedHashMap<String, String> aliasVsDisplayNameFormula = new LinkedHashMap<>();
        Map<String, Object> headers = new HashMap<>();

        for (PivotRowColumnContext row : rowColumns) {
            String key = row.getAlias();
            Map<String, Object> data = (Map<String, Object>) JsonTable.get(key);

            String header = "";

            JSONObject columnFormatMap = getColumnFormatMap(key);
            FacilioField field = getField(key);

            if(StringUtils.isNotEmpty(field.getName()) && field.getName().equals("siteId")){
                field = new FacilioField();
                field.setDataType(FieldType.LOOKUP);
            }

            Formatter formatter = Formatter.getInstance(field);

            if (formatter != null && !columnFormatMap.isEmpty()) {
                formatter.deserialize(columnFormatMap);
            } else if (formatter != null) {
                setColumnFormatMap(key, formatter.serialize());
            }
            boolean headerUnit = false;
            if (columnFormatMap.containsKey("headerUnit")) {
                headerUnit = Boolean.parseBoolean(columnFormatMap.get("headerUnit").toString());
            }

            header = applyHeaderUnit(data.get("label").toString(), headerUnit, formatter);

            if(formatter instanceof LookupFormatter){
                Map<String,Object> rowLookupMap = (Map<String, Object>) lookupMap.get(row.getAlias());
                ((LookupFormatter) formatter).setLookupMap(rowLookupMap);
                ((LookupFormatter) formatter).setAlias(row.getAlias());
            }

            aliasVsFieldMap.put(key, getField(key, header));

            formatterMap.put(key, formatter);
            headers.put(key, header);
            aliasVsDisplayNameRows.put(key, header);
        }

        for (PivotDataColumnContext prop : dataColumns) {
            String key = prop.getAlias();
            Map<String, Object> data = (Map<String, Object>) JsonTable.get(key);

            String header = "";

            JSONObject columnFormatMap = getColumnFormatMap(key);
            Formatter formatter = Formatter.getInstance(getField(key));

            if (formatter != null && !columnFormatMap.isEmpty()) {
                formatter.deserialize(columnFormatMap);
            } else if (formatter != null) {
                setColumnFormatMap(key, formatter.serialize());
            }

            boolean headerUnit = false;
            if (columnFormatMap.containsKey("headerUnit")) {
                headerUnit = Boolean.parseBoolean(columnFormatMap.get("headerUnit").toString());
            }
            header = applyHeaderUnit(data.get("label").toString(), headerUnit, formatter);


            aliasVsFieldMap.put(key, getField(key, header));

            formatterMap.put(key, formatter);
            headers.put(key, header);
            aliasVsDisplayNameData.put(key, header);
        }

        for (PivotFormulaColumnContext prop : formulaColumns) {
            String key = prop.getAlias();
            Map<String, Object> formula = (Map<String, Object>) JsonTable.get(key);

            String header = "";

            JSONObject columnFormatMap = getColumnFormatMap(key);

            Formatter formatter = Formatter.getInstance(prop.toFacilioField());

            if (formatter != null && !columnFormatMap.isEmpty()) {
                formatter.deserialize(columnFormatMap);
            } else if (formatter != null) {
                setColumnFormatMap(key, formatter.serialize());
            }


            boolean headerUnit = false;
            if (columnFormatMap.containsKey("headerUnit")) {
                headerUnit = Boolean.parseBoolean(columnFormatMap.get("headerUnit").toString());
            }
            header = applyHeaderUnit(formula.get("label").toString(), headerUnit, formatter);

            aliasVsFieldMap.put(key, getField(key, header));

            formatterMap.put(key, formatter);
            headers.put(key, header);
            aliasVsDisplayNameFormula.put(key, header);
        }


        params.put("aliasVsDisplayNameRows", aliasVsDisplayNameRows);
        params.put("aliasVsDisplayNameData", aliasVsDisplayNameData);
        params.put("aliasVsDisplayNameFormula", aliasVsDisplayNameFormula);
        params.put("headers", headers);

        return params;
    }

    private Map<String, Object> formatPivotRecords(Map<String, Object> params) throws Exception {
        LinkedHashMap<String, String> aliasVsDisplayNameRows = (LinkedHashMap<String, String>) params.get("aliasVsDisplayNameRows");
        LinkedHashMap<String, String> aliasVsDisplayNameData = (LinkedHashMap<String, String>) params.get("aliasVsDisplayNameData");
        LinkedHashMap<String, String> aliasVsDisplayNameFormula = (LinkedHashMap<String, String>) params.get("aliasVsDisplayNameFormula");
        Map<String, Object> headers = (Map<String, Object>) params.get("headers");

        List<Map<String, Object>> records = new ArrayList<>();


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
                JSONObject columnFormatMap = getColumnFormatMap(actualKey);

                tempMap.put("value", data.get(actualKey));
                Formatter formatter = formatterMap.get(actualKey);
                if(formatter instanceof BooleanFormatter)
                {
                    if(data.get(actualKey) instanceof Integer && alias_vs_aggr.containsKey(actualKey) && alias_vs_aggr.get(actualKey) == BmsAggregateOperators.SpecialAggregateOperator.LAST_VALUE.getValue()){
                        Integer value = (Integer) data.get(actualKey);
                        tempMap.put("formattedValue", formatter.format(value == 0 ? Boolean.FALSE : Boolean.TRUE));
                    }
                    else if(data.get(actualKey) instanceof Integer){
                        tempMap.put("formattedValue", (Integer) data.get(actualKey));
                    }else if(data.get(actualKey) instanceof BigDecimal){
                        Double value = Double.valueOf(data.get(actualKey).toString());
                        tempMap.put("formattedValue", value);
                    }else if(data.get(actualKey) instanceof Boolean){
                        tempMap.put("formattedValue", formatter.format(data.get(actualKey)));
                    }
                }
                else if (formatter != null && data.get(actualKey) != null && !data.get(actualKey).equals("")) {
                    tempMap.put("formattedValue", formatter.format(data.get(actualKey)));
                } else if(columnFormatMap != null &&
                        columnFormatMap.containsKey("customNullValue") &&
                        columnFormatMap.get("customNullValue") != null &&
                        data.get(actualKey) == null) {
                    tempMap.put("formattedValue", columnFormatMap.get("customNullValue"));
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
                if (formatter != null && formula.get(actualKey) != null && !formula.get(actualKey).equals("") && !formula.get(actualKey).equals("#VALUE!")) {
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
