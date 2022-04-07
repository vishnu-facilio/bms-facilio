package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.PivotDataColumnContext;
import com.facilio.report.context.PivotFormulaColumnContext;
import com.facilio.report.context.PivotRowColumnContext;
import org.json.simple.JSONObject;

import java.util.List;

public class PivotMetaField extends FacilioAction {
    private List<FacilioField> meta;
    private List<PivotRowColumnContext> rows;
    private List<PivotDataColumnContext> data;
    private List<PivotFormulaColumnContext> formula;
    private JSONObject columnFormatting;

    public JSONObject getColumnFormatting() {
        return columnFormatting;
    }

    public void setColumnFormatting(JSONObject columnFormatting) {
        this.columnFormatting = columnFormatting;
    }

    public List<PivotRowColumnContext> getRows() {
        return rows;
    }

    public void setRows(List<PivotRowColumnContext> rows) {
        this.rows = rows;
    }

    public List<PivotDataColumnContext> getData() {
        return data;
    }

    public void setData(List<PivotDataColumnContext> data) {
        this.data = data;
    }

    public List<PivotFormulaColumnContext> getFormula() {
        return formula;
    }

    public void setFormula(List<PivotFormulaColumnContext> formula) {
        this.formula = formula;
    }

    public List<FacilioField> getMeta() {
        return meta;
    }

    public void setMeta(List<FacilioField> meta) {
        this.meta = meta;
    }

    public String getPivotMetaFields() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getPivotMetaFieldsChain();
        FacilioContext context = new FacilioContext();

        context.put(FacilioConstants.Reports.ROWS, rows);
        context.put(FacilioConstants.Reports.DATA, data);
        context.put(FacilioConstants.ContextNames.FORMULA, formula);
        context.put(FacilioConstants.ContextNames.FORMATTING, columnFormatting);

        chain.execute(context);
        setMeta((List<FacilioField>) context.get(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD));

        return SUCCESS;
    }
}
