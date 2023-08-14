package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.PivotDataColumnContext;
import com.facilio.report.context.PivotFormulaColumnContext;
import com.facilio.report.context.PivotRowColumnContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PivotMetaFieldCommand extends FacilioCommand {

    private ModuleBean modBean;


    List<PivotRowColumnContext> rowColumns;
    List<PivotDataColumnContext> dataColumns;
    List<PivotFormulaColumnContext> formulaColumns;
    private JSONObject columnFormatting;

    public JSONObject getResult() {
        return result;
    }

    public void setResult(String key, Object value) {
        this.result.put(key, value);
    }

    JSONObject result;



    private FacilioField getField(String alias) throws Exception {
        FacilioField facilioField = null;

        for (PivotRowColumnContext row : rowColumns) {
            if (row.getAlias().equals(alias)) {
                if(row.getField().getId() <=0 && row.getField().getName() != null && row.getField().getName().equals("siteId")) {
                    facilioField = FieldFactory.getSiteField(modBean.getModule(row.getField().getModuleId()));
                }else{
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
                if (data.getAlias().equals(alias)) {
                    facilioField = modBean.getReadingField(data.getReadingField().getId()).clone();
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

    @Override
    public boolean executeCommand(Context context) throws Exception {
        modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        rowColumns = (List<PivotRowColumnContext>) context.get(FacilioConstants.Reports.ROWS);
        dataColumns = (List<PivotDataColumnContext>) context.get(FacilioConstants.Reports.DATA);
        formulaColumns = (List<PivotFormulaColumnContext>) context.get(FacilioConstants.ContextNames.FORMULA);
        columnFormatting = (JSONObject) context.get(FacilioConstants.ContextNames.FORMATTING);

        List<FacilioField> facilioFieldList = new ArrayList<>();

        for(PivotRowColumnContext row: rowColumns){
            Map<String, Object> columnFormatMap = (Map<String, Object>) columnFormatting.get(row.getAlias());
            FacilioField facilioField = getField(row.getAlias(), columnFormatMap.get("label").toString());
            facilioFieldList.add(facilioField);
        }

        for(PivotDataColumnContext data: dataColumns){
            Map<String, Object> columnFormatMap = (Map<String, Object>) columnFormatting.get(data.getAlias());
            FacilioField facilioField = getField(data.getAlias(), columnFormatMap.get("label").toString());
            facilioFieldList.add(facilioField);
        }

        for(PivotFormulaColumnContext formula: formulaColumns){
            Map<String, Object> columnFormatMap = (Map<String, Object>) columnFormatting.get(formula.getAlias());
            FacilioField facilioField = getField(formula.getAlias(), columnFormatMap.get("label").toString());
            facilioFieldList.add(facilioField);
        }
        context.put(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD, facilioFieldList);
        return false;
    }
}
