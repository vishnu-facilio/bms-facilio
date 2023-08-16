package com.facilio.bmsconsole.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.report.context.PivotFormulaColumnContext;
import com.facilio.report.context.ReportContext;
import com.facilio.time.DateRange;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.text.NumberFormat;
import java.util.*;


public class PivotFormulaColumnCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(PivotFormulaColumnCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PivotFormulaColumnContext> formulaContextList = (List<PivotFormulaColumnContext>) context.get(FacilioConstants.ContextNames.FORMULA);
        List<Map<String, Object>> table = (List<Map<String, Object>>)  context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);

        List<String> rowHeaders= (List<String>) context.get(FacilioConstants.ContextNames.ROW_HEADERS);
        List<String>  columnHeaders = (List<String>) context.get(FacilioConstants.ContextNames.DATA_HEADERS);

        List<Map<String,Object>> paramList = new ArrayList<>();
        for (Map<String, Object> record : table) {
            Map<String,Object> params = new HashMap<>();
            Map<String,Object> rows = (Map<String, Object>) record.get("rows");
            Map<String,Object> data = (Map<String, Object>) record.get("data");

            if(columnHeaders != null && columnHeaders.size() > 0)
            {
                for(String data_column_alias : columnHeaders)
                {
                    if(data != null && !data.containsKey(data_column_alias))
                    {
                        data.put(data_column_alias, 0);
                    }
                }
            }
            params.putAll(rows);
            params.putAll(data);
            paramList.add(params);
        }

        long dateFieldId = (long) context.get(FacilioConstants.ContextNames.DATE_FIELD);
        long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
        long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
        Boolean timelinefilterApplied  = (Boolean) context.get(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED);
        Boolean showTimelineFilter = (Boolean) context.get(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER);
        DateRange range = null;
        if(dateFieldId > 0 && startTime > 0 && endTime > 0 && timelinefilterApplied != null && timelinefilterApplied == Boolean.TRUE){
            range = new DateRange(startTime, endTime);
        }else if(showTimelineFilter && context.containsKey(FacilioConstants.ContextNames.DATE_OPERATOR) && context.get(FacilioConstants.ContextNames.DATE_OPERATOR) != null){
            int date_operator = (int) context.get(FacilioConstants.ContextNames.DATE_OPERATOR);
            DateOperators dateOperator = (DateOperators) Operator.getOperator(date_operator);
            try {
                range = dateOperator.getRange(null);
            }catch (Exception e){
                LOGGER.log(Level.INFO, "error while getting daterange for pivot script");
                range = null;
            }
        }
        for(PivotFormulaColumnContext formulaContext : formulaContextList) {
            String script = this.generateScript(formulaContext.getVariableMap(),formulaContext.getExpression(), range);
            ArrayList<String> variablesUsedInExp = getVariablesUsedInExp(formulaContext.getVariableMap());
            if (isRequiredVariablesAvailable(columnHeaders, rowHeaders, variablesUsedInExp)) {
                this.executeFormulaColumn(script,paramList,formulaContext.getAlias());
            }
        }

        List<String> formulaHeaders = new ArrayList<>();
        for(PivotFormulaColumnContext formulaContext : formulaContextList) {
            formulaHeaders.add(formulaContext.getAlias());
        }
        for (int index=0;index<table.size();index++) {
            Map<String,Object> record = table.get(index);
            Map<String,Object> formulaMap = new HashMap<>();
            for(PivotFormulaColumnContext formulaContext : formulaContextList) {
                Map<String,Object> formulaRecord = paramList.get(index);
                if(formulaRecord.containsKey(formulaContext.getAlias()) && formulaRecord.get(formulaContext.getAlias()) != null){
                    if(formulaRecord.get(formulaContext.getAlias()).equals("#VALUE!")) {
                        String res = String.valueOf(formulaRecord.get(formulaContext.getAlias()));
                        formulaMap.put(formulaContext.getAlias(), res);
                    } else if(formulaContext.getDataTypeEnum() != null && formulaContext.getDataTypeEnum().equals("DECIMAL")){
                        formulaMap.put(formulaContext.getAlias(),formulaRecord.get(formulaContext.getAlias()));
                    } else if(formulaContext.getDataTypeEnum() != null && formulaContext.getDataTypeEnum().equals("NUMBER") || formulaContext.getDataTypeEnum().equals("DATE_TIME")) {
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        long res = numberFormat.parse(formulaRecord.get(formulaContext.getAlias()).toString()).longValue();
                        formulaMap.put(formulaContext.getAlias(),Math.round(res));
                    } else if(formulaContext.getDataTypeEnum() != null && formulaContext.getDataTypeEnum().equals("BOOLEAN")) {
                        Boolean res = Boolean.parseBoolean(formulaRecord.get(formulaContext.getAlias()).toString());
                        formulaMap.put(formulaContext.getAlias(),res);
                    } else if(formulaContext.getDataTypeEnum().equals("STRING")) {
                        String res = String.valueOf(formulaRecord.get(formulaContext.getAlias()));
                        formulaMap.put(formulaContext.getAlias(),res);
                    }
                }
            }
            record.put("formula",formulaMap);
        }

        context.put(FacilioConstants.ContextNames.FORMULA_HEADERS,formulaHeaders);
        return false;
    }

    private ArrayList<String> getVariablesUsedInExp(Map<String,String> variableMap){
        ArrayList<String> result = new ArrayList<>();
        for(String key: variableMap.keySet()){
            result.add(key);
        }
        return result;
    }


    private String generateScript(Map<String, String> aliases, String expression, DateRange range){
        StringJoiner scriptString = new StringJoiner("\n");
        scriptString.add("void test(List data, String key)");
        scriptString.add("{");
        if(range != null && range.getStartTime() > 0 && range.getEndTime() > 0) {
            scriptString.add("startTime=" +range.getStartTime()+";");
            scriptString.add("endTime=" +range.getStartTime()+";");
        }
        scriptString.add("for each index, value in data {");
        String varInitTemplate = "${variable} = new NameSpace(\"map\").get(value, \"${alias}\");";
        String varExpressionTemplate = "result = ${expression};";
        for (String alias: aliases.keySet()) {
            String expressionString = varInitTemplate
                    .replace("${alias}", alias)
                    .replace("${variable}", aliases.get(alias));
            scriptString.add(expressionString);
        }
        if(expression.contains("result")){
            scriptString.add(expression);
        }else {
            scriptString.add(varExpressionTemplate.replace("${expression}", expression));
        }
        scriptString.add("put = new NameSpace(\"map\").put(value, key, result);");
        scriptString.add("}");
        scriptString.add("}");
        return scriptString.toString();
    }

    private boolean isRequiredVariablesAvailable(List<String> data, List<String> rows, List<String> variableAliasArray){
        boolean status = true;
        for(String alias: variableAliasArray){
            if(!data.contains(alias) && !rows.contains(alias)){
                status = false;
                break;
            }
        }
        return status;
    }

    private void executeFormulaColumn(String script, List<Map<String,Object>> params, String key) {
        List<Object> paramList = new ArrayList<>();
        paramList.add(params);
        paramList.add(key);
        try {
            WorkflowContext workflowContext = new WorkflowContext();
            workflowContext.setIsV2Script(true);
            workflowContext.setThrowExceptionForSyntaxError(true);
            workflowContext.setWorkflowV2String(script);
            workflowContext.setParams(paramList);
            workflowContext.executeWorkflow();
        } catch (Exception e){
            for (Map<String,Object> row : params){
                row.put(key, "#VALUE!");
            }
        }
    }

}
