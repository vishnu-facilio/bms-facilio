package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.CommonAggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportYAxisContext;

public class ConstructReportData implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ReportContext reportContext = new ReportContext();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		reportContext.setType(ReportType.READING_REPORT);
		reportContext.setxAggr(0);
		reportContext.setxAlias("X");
		
		JSONObject xAxisJSON = (JSONObject) context.get("x-axis");
		
		ReportDataPointContext dataPointContext = new ReportDataPointContext();
		ReportFieldContext xAxis = new ReportFieldContext();
		FacilioField xField = modBean.getField((Long) xAxisJSON.get("field_id"));
		xAxis.setField(xField);
		dataPointContext.setxAxis(xAxis);
		
		ReportYAxisContext yAxis = new ReportYAxisContext();
		yAxis.setAggr(CommonAggregateOperator.COUNT);
		FacilioField yField = FieldFactory.getIdField(xField.getModule());
		yField.setColumnName(yField.getModule().getTableName() + ".ID");
		yAxis.setField(yField);
		dataPointContext.setyAxis(yAxis);
		
		Map<String, String> aliases = new HashMap<>();
		aliases.put("actual", "Y");
		dataPointContext.setAliases(aliases);
		dataPointContext.setName(xField.getName());
		
		reportContext.addDataPoint(dataPointContext);
		
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		
		return false;
	}

}
