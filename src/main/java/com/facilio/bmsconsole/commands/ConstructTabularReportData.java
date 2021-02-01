package com.facilio.bmsconsole.commands;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportGroupByField;
import com.facilio.report.context.ReportPivotTableRowsContext;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportPivotTableDataContext;


;

public class ConstructTabularReportData extends FacilioCommand {
	private FacilioModule module;
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		List<ReportPivotTableRowsContext> rows = (List<ReportPivotTableRowsContext>) context.get(FacilioConstants.Reports.ROWS);
		List<ReportPivotTableDataContext> data = (List<ReportPivotTableDataContext>) context.get(FacilioConstants.Reports.DATA);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if (reportContext == null) {
			reportContext = new ReportContext();
			reportContext.setType(ReportType.PIVOT_REPORT);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		module = null;
		if (reportContext.getModuleId() > 0) {
			module = modBean.getModule(reportContext.getModuleId());
		} else if (StringUtils.isNotEmpty(moduleName)) {
			module = modBean.getModule(moduleName);
		}
		
		if (module == null) {
			throw new Exception("Module name should not be empty");
		}
		
		context.put(FacilioConstants.ContextNames.MODULE, module);
		
		reportContext.setModuleId(module.getModuleId());
		
		List<String> rowHeaders = new ArrayList<>();
		List<String> dataHeaders = new ArrayList<>();
		Map<String,Object> rowAlias = new HashMap<>();
		Map<String,Object> dataAlias = new HashMap<>();
		
		for (int i = 0; i < data.size(); i++) {
			ReportPivotTableDataContext yData = data.get(i);
			addDataPointContext(modBean, reportContext, rows, yData, module);
			dataHeaders.add(yData.getAlias());
			if(yData.getField() != null) {
				Map<String,Object> dataDetails = new HashMap<>();
				dataDetails.put("displayName", yData.getField().getDisplayName());
				dataDetails.put(FacilioConstants.ContextNames.FORMATTING, yData.getFormatting());
				dataAlias.put(yData.getAlias(), dataDetails);
			}
		}
		
		if (rows != null && rows.size()>0) {
			for (int i = 0; i < rows.size(); i++) {
				ReportPivotTableRowsContext groupByRow = rows.get(i);
				
				FacilioField groupByRowField = groupByRow.getField();
				FacilioField field = modBean.getField(groupByRowField.getFieldId(), groupByRowField.getModule().getName());
				FacilioModule groupByModule = null;
				if (field.getModule() != null) {
					groupByModule = field.getModule();
				} else if(field.getModuleId() != -1) {
					groupByModule = modBean.getModule(field.getModuleId());
				}
				rowHeaders.add(field.getName()+'_'+groupByModule.getName());
				Map<String,Object> rowDetails = new HashMap<>();
				rowDetails.put("displayName", field.getDisplayName());
				rowDetails.put(FacilioConstants.ContextNames.FORMATTING, groupByRow.getFormatting());
				rowAlias.put(field.getName()+'_'+groupByModule.getName(), rowDetails);
			}
		}
		context.put(FacilioConstants.ContextNames.GET_MODULE_FROM_DP, true);
		context.put(FacilioConstants.ContextNames.ROW_HEADERS, rowHeaders);
		context.put(FacilioConstants.ContextNames.DATA_HEADERS, dataHeaders);
		context.put(FacilioConstants.ContextNames.ROW_ALIAS, rowAlias);
		context.put(FacilioConstants.ContextNames.DATA_ALIAS, dataAlias);
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		return false;
	}
	
	private void addDataPointContext(ModuleBean modBean, ReportContext reportContext, List<ReportPivotTableRowsContext> rows, ReportPivotTableDataContext data, FacilioModule module) throws Exception {
		ReportDataPointContext dataPointContext = new ReportDataPointContext();
		
		ReportFieldContext xAxis = new ReportFieldContext();
		ReportPivotTableRowsContext firstRow = rows.get(0);
		List<String> orderBy = new ArrayList<>();
		
		FacilioField xField = null;
		if (firstRow != null && firstRow.getField() != null) {
			FacilioField rowField = firstRow.getField();
			xField = modBean.getField(rowField.getFieldId(), rowField.getModule().getName());
		}
		
		if (firstRow.getLookupFieldId() != -1) {
			xAxis.setLookupFieldId(firstRow.getLookupFieldId());
		}
		if (xField == null) {
			throw new Exception("atleast one row mandatory");
		}
		FacilioModule xAxisModule = null;
		if (xField.getModule() != null) {
			xAxisModule = xField.getModule();
		} else if(xField.getModuleId() != -1) {
			xAxisModule = modBean.getModule(xField.getModuleId());
		}
		xAxis.setField(xAxisModule, xField);
		dataPointContext.setxAxis(xAxis);
		orderBy.add(xField.getCompleteColumnName());
		dataPointContext.setOrderBy(orderBy);
		reportContext.setxAggr(0);
		reportContext.setxAlias(xField.getName()+"_"+xAxisModule.getName());
		
		
		ReportYAxisContext yAxis = new ReportYAxisContext();
		FacilioField yField = null;

		AggregateOperator yAggr = CommonAggregateOperator.COUNT;
		FacilioModule yAxisModule = null;
		if (data != null) {
				yAggr = data.getAggrEnum();
				yField = data.getField();
				if (yField.getModuleId() != -1) {
					yAxisModule = modBean.getModule(yField.getModuleId());
				}
				Criteria criteria = data.getCriteria();
				if (criteria != null) {
					dataPointContext.setCriteria(criteria);
				}
				if(data.getDateFieldId() != -1 && data.getDateFieldId() != -99 && data.getDatePeriod() != -1) {
					FacilioField dateField = modBean.getField(data.getDateFieldId(), yAxisModule.getName());
					Operator dateOperator = Operator.getOperator(data.getDatePeriod());
					Criteria otherCrit = new Criteria();
					Condition newCond = CriteriaAPI.getCondition(dateField, dateOperator);
					otherCrit.addAndCondition(newCond);
					dataPointContext.setOtherCriteria(otherCrit);
				}
		}

		yAxis.setAggr(yAggr);
		yAxis.setField(yAxisModule, yField);
		dataPointContext.setyAxis(yAxis);
		
		if (rows != null && rows.size()>1) {
			List<ReportGroupByField> groupByFields = new ArrayList<>();
			for (int i = 1; i < rows.size(); i++) {
				ReportPivotTableRowsContext groupByRow = rows.get(i);
				ReportGroupByField groupByField = new ReportGroupByField();
				
				FacilioField groupByRowField = groupByRow.getField();
				FacilioField field = modBean.getField(groupByRowField.getFieldId(), groupByRowField.getModule().getName());
				
				
				FacilioModule groupByModule = null;
				if (field.getModule() != null) {
					groupByModule = field.getModule();
				} else if(field.getModuleId() != -1) {
					groupByModule = modBean.getModule(field.getModuleId());
				}
				
				if (groupByRow.getLookupFieldId() != -1) {
					groupByField.setLookupFieldId(groupByRow.getLookupFieldId());
				}
				groupByField.setField(groupByModule, field);
				groupByField.setAlias(field.getName()+'_'+groupByModule.getName());				
				groupByFields.add(groupByField);
			}
			dataPointContext.setGroupByFields(groupByFields);
		}
		
		dataPointContext.setOrderByFunc(3);
		Map<String, String> aliases = new HashMap<>();
		aliases.put("actual", data.getAlias());
		dataPointContext.setAliases(aliases);
		dataPointContext.setName(xField.getName());
		reportContext.addDataPoint(dataPointContext);
	}
}
