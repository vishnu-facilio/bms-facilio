package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.PivotColumnContext;
import com.facilio.report.context.PivotDataColumnContext;
import com.facilio.report.context.PivotFormulaColumnContext;
import com.facilio.report.context.PivotRowColumnContext;
import com.facilio.report.formatter.*;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PivotDrillDownCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PivotRowColumnContext> rowColumns= (List<PivotRowColumnContext>) context.get(FacilioConstants.Reports.ROWS);
        boolean showTimelineFilter = false;
        if(context.containsKey(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER) && context.get(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER) != null)
            showTimelineFilter = (boolean) context.get(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER);
        Long dateFieldId = (Long) context.get(FacilioConstants.ContextNames.DATE_FIELD);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Map<String, FacilioField> pivotDrillDownFields = new HashMap<>();
        Map<String, Object> pivotDrillDownOperators = new HashMap<>();

        for(PivotRowColumnContext row: rowColumns){
            FacilioField facilioField = getField(row, modBean);
            pivotDrillDownFields.put(row.getAlias(), facilioField);
            pivotDrillDownOperators.put(row.getAlias(), getOperatorId(facilioField));
        }

        if(showTimelineFilter && dateFieldId > 0){
            FacilioField timeFilterField = modBean.getField(dateFieldId);
            pivotDrillDownFields.put("timeFilter", timeFilterField);
            pivotDrillDownOperators.put("timeFilter", getOperatorId(timeFilterField));
        }

        context.put(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_FIELDS, pivotDrillDownFields);
        context.put(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_OPERATORS, pivotDrillDownOperators);
        return false;
    }
    private FacilioField getField(PivotRowColumnContext rowColumn, ModuleBean modBean) throws Exception {
        if(rowColumn.getLookupFieldId() > 0){
            return modBean.getField(rowColumn.getLookupFieldId()).clone();
        }
        else if(rowColumn.getField().getId() <=0 && rowColumn.getField().getName() != null && rowColumn.getField().getName().equals("siteId")){
            return  FieldFactory.getSiteField(modBean.getModule(rowColumn.getField().getModuleId()));
        }
        return modBean.getField(rowColumn.getField().getId()).clone();
    }

    private int getOperatorId(FacilioField field){
        switch (field.getDataTypeEnum()){
            case DECIMAL:
            case NUMBER:
                return NumberOperators.EQUALS.getOperatorId();
            case STRING:
                return StringOperators.IS.getOperatorId();
            case BOOLEAN:
                return BooleanOperators.IS.getOperatorId();
            case ENUM:
            case SYSTEM_ENUM:
                return EnumOperators.IS.getOperatorId();
            case MULTI_ENUM:
                return MultiFieldOperators.CONTAINS.getOperatorId();
            case DATE:
            case DATE_TIME:
                return DateOperators.BETWEEN.getOperatorId();
            case LOOKUP:
                return PickListOperators.IS.getOperatorId();
        }
        return -1;
    }
}
