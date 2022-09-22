package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.PivotDataColumnContext;
import com.facilio.report.context.PivotFormulaColumnContext;
import com.facilio.report.context.PivotValueColumnContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PivotDataAndFormulaColumnToValueColumn extends FacilioCommand {
    JSONObject templateJson;
    HashMap<String, Object> JsonTable;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PivotDataColumnContext> data = (List<PivotDataColumnContext>) context.get(FacilioConstants.Reports.DATA);
        List<PivotFormulaColumnContext> formula = (List<PivotFormulaColumnContext>) context.get(FacilioConstants.ContextNames.FORMULA);
        List<PivotValueColumnContext> values = (List<PivotValueColumnContext>) context.get(FacilioConstants.ContextNames.VALUES);
        templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        JsonTable = (HashMap<String, Object>) templateJson.get("columnFormatting");
        boolean isBuilderV2 = (boolean) context.get(FacilioConstants.ContextNames.IS_BUILDER_V2);
        if(!isBuilderV2) dataAndFormulaToValueColumn(data, formula, values);
        return false;
    }

    private String getLabel(String alias) {
        Map<String, Object> columnConfig = (Map<String, Object>) JsonTable.get(alias);
        if (columnConfig.containsKey("label")) {
            return columnConfig.get("label").toString();
        }

        return "";
    }


    private void dataAndFormulaToValueColumn(List<PivotDataColumnContext> data , List<PivotFormulaColumnContext> formula, List<PivotValueColumnContext> values){
        values.clear();

        for(PivotDataColumnContext dataColumnContext : data){
           PivotValueColumnContext newValue = new PivotValueColumnContext();
           newValue.setValueType("DATA");
           newValue.setFieldDisplayName(getLabel(dataColumnContext.getAlias()));
           newValue.setAlias(dataColumnContext.getAlias());
           if(dataColumnContext != null && dataColumnContext.getModuleName() != null && !"".equals(dataColumnContext.getModuleName())){
               try {
                   ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                   FacilioModule module = modBean.getModule(dataColumnContext.getModuleName());
                   List<FacilioField> allFields = modBean.getAllFields(dataColumnContext.getModuleName());
                   module.setFields(allFields);
                   if(dataColumnContext.getField() != null){
                       dataColumnContext.getField().setModule(module);
                   }
               }catch (Exception e){
                   System.out.print("error");
               }
           }
           newValue.setModuleMeasure(dataColumnContext);

           values.add(newValue);
        }

        for(PivotFormulaColumnContext formulaColumnContext: formula){
            PivotValueColumnContext newValue = new PivotValueColumnContext();
            newValue.setValueType("FORMULA");
            newValue.setFieldDisplayName(getLabel(formulaColumnContext.getAlias()));
            newValue.setCustomMeasure(formulaColumnContext);
            newValue.setAlias(formulaColumnContext.getAlias());
            values.add(newValue);
        }

    }
}
