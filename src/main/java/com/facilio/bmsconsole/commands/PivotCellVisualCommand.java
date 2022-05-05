package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.PivotColumnContext;
import com.facilio.report.context.PivotDataColumnContext;
import com.facilio.report.context.PivotFormulaColumnContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

public class PivotCellVisualCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PivotDataColumnContext> pivotDataColumnContextList = (List<PivotDataColumnContext>) context.get(FacilioConstants.ContextNames.DATA);
        List<PivotFormulaColumnContext> pivotFormulaColumnContextList = (List<PivotFormulaColumnContext>) context.get(FacilioConstants.ContextNames.FORMULA);


        List<HashMap<String,Object>> pivotTableData = (List<HashMap<String, Object>>) context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA);

        for(PivotColumnContext columnContext: pivotDataColumnContextList) {
            for (HashMap<String, Object> params : pivotTableData) {
                Map<String, Object> dataMap = (Map<String, Object>) params.get("data");
                applyVisualSettings(dataMap, columnContext);
            }
        }

        for(PivotColumnContext columnContext: pivotFormulaColumnContextList) {
            for (HashMap<String, Object> params : pivotTableData) {
                Map<String, Object> formulaMap = (Map<String, Object>) params.get("formula");
                applyVisualSettings(formulaMap, columnContext);
            }
        }
        return false;
    }

    private void applyVisualSettings(Map<String, Object> dataMap, PivotColumnContext columnContext) {
            JSONObject visualConfig;

            if (columnContext instanceof PivotDataColumnContext) {
                PivotDataColumnContext dataColumnContext = (PivotDataColumnContext) columnContext;
                visualConfig = dataColumnContext.getVisualConfig();
            } else {
                PivotFormulaColumnContext formulaColumnContext = (PivotFormulaColumnContext) columnContext;
                visualConfig = formulaColumnContext.getVisualConfig();
            }

            if(visualConfig == null) return;

            double currentValue = 0;

            if(dataMap.containsKey(columnContext.getAlias()) && dataMap.get(columnContext.getAlias()) != null)
                currentValue =  Double.parseDouble(dataMap.get(columnContext.getAlias()).toString());

            double value = currentValue;

            if(visualConfig.containsKey("columnMaxValue") && visualConfig.get("columnMaxValue") != null){
                double max = Double.parseDouble(visualConfig.get("columnMaxValue").toString());
                if(max < value){
                    visualConfig.put("columnMaxValue", value);
                }
            } else {
                visualConfig.put("columnMaxValue", value);
            }
    }



}
