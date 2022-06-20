package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.PivotDataColumnContext;
import com.facilio.report.context.PivotFormulaColumnContext;
import com.facilio.report.context.PivotValueColumnContext;
import com.facilio.report.context.PivotValueColumnType;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class PivotValueColumnToDataAndFormulaColumn extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PivotDataColumnContext> data = (List<PivotDataColumnContext>) context.get(FacilioConstants.Reports.DATA);
        List<PivotFormulaColumnContext> formula = (List<PivotFormulaColumnContext>) context.get(FacilioConstants.ContextNames.FORMULA);
        List<PivotValueColumnContext> values = (List<PivotValueColumnContext>) context.get(FacilioConstants.ContextNames.VALUES);
        boolean isBuilderV2 = (boolean) context.get(FacilioConstants.ContextNames.IS_BUILDER_V2);
        if(isBuilderV2) valueColumnToDataFormulaColumn(data, formula, values);
        return false;
    }

    private void valueColumnToDataFormulaColumn(List<PivotDataColumnContext> data , List<PivotFormulaColumnContext> formula, List<PivotValueColumnContext> values){
        data.clear();
        formula.clear();

        for(PivotValueColumnContext value : values){
            if(value.getValueType().equals(PivotValueColumnType.DATA.name())){
                PivotDataColumnContext newData = value.getModuleMeasure();
                data.add(newData);
            } else {
                PivotFormulaColumnContext newFormula = value.getCustomMeasure();
                formula.add(newFormula);
            }
        }
    }
}
