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

import java.util.*;

public class PivotFormulaColumnCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PivotFormulaColumnContext> formulaContextList = (List<PivotFormulaColumnContext>) context.get(FacilioConstants.ContextNames.FORMULA);
        List<Map<String, Object>> table = (List<Map<String, Object>>)  context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);

        List<String> rowHeaders= (List<String>) context.get(FacilioConstants.ContextNames.ROW_HEADERS);
        List<String>  columnHeaders = (List<String>) context.get(FacilioConstants.ContextNames.DATA_HEADERS);
        List<String> aliasList = new ArrayList<>();
        aliasList.addAll(rowHeaders);
        aliasList.addAll(columnHeaders);

        List<Map<String,Object>> paramList = new ArrayList<>();
        for (Map<String, Object> record : table) {
            Map<String,Object> params = new HashMap<>();
            Map<String,Object> rows = (Map<String, Object>) record.get("rows");
            Map<String,Object> data = (Map<String, Object>) record.get("data");
            params.putAll(rows);
            params.putAll(data);
            paramList.add(params);
        }

        for(PivotFormulaColumnContext formulaContext : formulaContextList) {
            String script = this.generateScript(aliasList,formulaContext.getScriptExpression());
            if (isRequiredVariablesAvailable(columnHeaders, rowHeaders, formulaContext.getVariablesUsedInExp())) {
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
                if(formulaRecord.containsKey(formulaContext.getAlias()) &&
                        formulaRecord.get(formulaContext.getAlias()) != null &&
                        isRequiredVariablesAvailable(columnHeaders, rowHeaders, formulaContext.getVariablesUsedInExp())){
                    if(formulaContext.getDataTypeEnum() != null && formulaContext.getDataTypeEnum().equals("DECIMAL")){
                        formulaMap.put(formulaContext.getAlias(),formulaRecord.get(formulaContext.getAlias()));
                    } else if(formulaContext.getDataTypeEnum() != null && formulaContext.getDataTypeEnum().equals("NUMBER") || formulaContext.getDataTypeEnum().equals("DATE_TIME")) {
                        Long res = Long.parseLong(formulaRecord.get(formulaContext.getAlias()).toString());
                        formulaMap.put(formulaContext.getAlias(),Math.round(res));
                    } else if(formulaContext.getDataTypeEnum() != null && formulaContext.getDataTypeEnum().equals("BOOLEAN") || formulaContext.getDataTypeEnum().equals("STRING")) {
                        String res = String.valueOf(formulaRecord.get(formulaContext.getAlias()));
                        formulaMap.put(formulaContext.getAlias(),res);
                    }
                } else {
                    formulaMap.put(formulaContext.getAlias(),"#VALUE!");
                }
            }
            record.put("formula",formulaMap);
        }

        context.put(FacilioConstants.ContextNames.FORMULA_HEADERS,formulaHeaders);
        return false;
    }

    private String generateScript(List<String> aliases, String expression){
        StringJoiner scriptString = new StringJoiner("\n");
        scriptString.add("void test(List data, String key)");
        scriptString.add("{");
        scriptString.add("for each index, value in data {");
        String varInitTemplate = "${alias} = new NameSpace(\"map\").get(value, \"${alias}\");";
        String varExpressionTemplate = "result = ${expression};";
        for (String alias: aliases) {
            scriptString.add(varInitTemplate.replace("${alias}", alias));
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
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("data",params);
        paramMap.put("key",key);
        try {
            WorkflowUtil.getWorkflowExpressionResult(script, paramMap, true);
        } catch (Exception e){
            for (Map<String,Object> row : params){
                row.put(key, "#VALUE!");
            }
        }
    }

}
