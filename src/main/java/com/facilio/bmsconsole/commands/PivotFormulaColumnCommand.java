package com.facilio.bmsconsole.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.PivotFormulaColumnContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

import java.text.NumberFormat;
import java.util.*;


public class PivotFormulaColumnCommand extends FacilioCommand {
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

        for(PivotFormulaColumnContext formulaContext : formulaContextList) {
            String script = this.generateScript(formulaContext.getVariableMap(),formulaContext.getExpression());
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

    private String generateScript(Map<String, String> aliases, String expression){
        StringJoiner scriptString = new StringJoiner("\n");
        scriptString.add("void test(List data, String key)");
        scriptString.add("{");
        scriptString.add("for each index, value in data {");
        String varInitTemplate = "${variable} = new NameSpace(\"map\").get(value, \"${alias}\");";
        String varExpressionTemplate = "result = ${expression};";
        for (String alias: aliases.keySet()) {
            String expressionString = varInitTemplate
                    .replace("${alias}", alias)
                    .replace("${variable}", aliases.get(alias));
            scriptString.add(expressionString);
        }
        scriptString.add(varExpressionTemplate.replace("${expression}", expression));
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
