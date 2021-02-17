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
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportGroupByField;
import com.facilio.report.context.ReportPivotFieldContext;
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
		Criteria basecriteria = (Criteria) context.get(FacilioConstants.ContextNames.CRITERIA);
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
		
		if(basecriteria != null) {
			reportContext.setCriteria(basecriteria);
		}
		
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
				FacilioField yField = null;
				if(yData.getReadingField() != null) {
					if(yData.getReadingField().getId()>0) {
					yField =  modBean.getField(yData.getReadingField().getId());
					}
				} else if (yData.getField() != null) {
				if(yData.getField().getId()!=-1) {
					yField = modBean.getField(yData.getField().getId(), yData.getField().getModuleId());
				} else {
					FacilioModule yAxisModule = modBean.getModule(yData.getField().getModuleId());
					yField = modBean.getField(yData.getField().getName(), yAxisModule.getName());
				}
				}
				dataDetails.put("displayName", yField!=null ? yField.getDisplayName():"");
				dataDetails.put(FacilioConstants.ContextNames.FORMATTING, yData.getFormatting());
				dataAlias.put(yData.getAlias(), dataDetails);
			}
		}
		
		if (rows != null && rows.size()>0) {
			for (int i = 0; i < rows.size(); i++) {
				ReportPivotTableRowsContext groupByRow = rows.get(i);
				
				ReportPivotFieldContext groupByRowField = groupByRow.getField();
				FacilioModule groupByModule = null;
				FacilioField gField = null;
				if(groupByRowField != null){
					if (groupByRowField.getModuleId() != -1) {
						groupByModule = modBean.getModule(groupByRowField.getModuleId());
					}
					if(groupByRowField.getId()!=-1) {
						gField = modBean.getField(groupByRowField.getId(), groupByRowField.getModuleId());
					} else {
						gField = modBean.getField(groupByRowField.getName(), groupByModule.getName());
					}
				}
				rowHeaders.add(groupByRow.getAlias());
				Map<String,Object> rowDetails = new HashMap<>();
				rowDetails.put("displayName", gField.getDisplayName());
				rowDetails.put(FacilioConstants.ContextNames.FORMATTING, groupByRow.getFormatting());
				rowAlias.put(groupByRow.getAlias(), rowDetails);
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
		FacilioModule xAxisModule = null;
		
		if (firstRow != null && firstRow.getField() != null) {
			ReportPivotFieldContext rowField = firstRow.getField();
			if (rowField.getModuleId() != -1) {
				xAxisModule = modBean.getModule(rowField.getModuleId());
			}
			if(rowField.getId()!=-1) {
				xField = modBean.getField(rowField.getId(), rowField.getModuleId());
			} else {
				xField = modBean.getField(rowField.getName(), rowField.getName());
			}
		}
		
		if (firstRow.getLookupFieldId() != -1) {
			xAxis.setLookupFieldId(firstRow.getLookupFieldId());
		}
		
		if (xField == null) {
			throw new Exception("atleast one row mandatory");
		}
		xAxis.setField(xAxisModule, xField);
		if(firstRow.getModuleName() != null) {
			xAxis.setModule(modBean.getModule(firstRow.getModuleName()));
		}
		if(firstRow.getAlias() != null) {
			xAxis.setAlias(firstRow.getAlias());;
		}
		dataPointContext.setxAxis(xAxis);
		orderBy.add(xField.getCompleteColumnName());
		dataPointContext.setOrderBy(orderBy);
		reportContext.setxAggr(0);
		reportContext.setxAlias(firstRow.getAlias());
		
		
		ReportYAxisContext yAxis = new ReportYAxisContext();
		FacilioField yField = null;

		AggregateOperator yAggr = CommonAggregateOperator.COUNT;
		FacilioModule yAxisModule = null;
		if (data != null) {
				if(data.getAggrEnum() != null) {
					yAggr = data.getAggrEnum();
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
				if(data.getReadingField() != null) {
					if(data.getReadingField().getId()>0) {
					yField =  modBean.getField(data.getReadingField().getId());
					}
					yAxisModule = modBean.getModule(data.getReadingField().getModuleId());
					//yAxisModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
					yAggr = NumberAggregateOperator.SUM;
				} else if(data.getField() != null){
					if (data.getField().getModuleId() > 0) {
						yAxisModule = modBean.getModule(data.getField().getModuleId());
					}
					if(data.getField().getId() > 0) {
						yField = modBean.getField(data.getField().getId(), data.getField().getModuleId());
					} else {
						yField = modBean.getField(data.getField().getName(), yAxisModule.getName());
					}
				}
				if(data.getAlias() != null) {
					yAxis.setAlias(data.getAlias());
				}
				yAxis.setField(yAxisModule, yField);
				yAxis.setAggr(yAggr);
				dataPointContext.setyAxis(yAxis);
				//dataPointContext.setModuleName(FacilioConstants.ContextNames.RESOURCE);
		}
		
		if (rows != null && rows.size()>1) {
			List<ReportGroupByField> groupByFields = new ArrayList<>();
			for (int i = 1; i < rows.size(); i++) {
				ReportPivotTableRowsContext groupByRow = rows.get(i);
				ReportGroupByField groupByField = new ReportGroupByField();
				
				ReportPivotFieldContext groupByRowField = groupByRow.getField();
				FacilioModule groupByModule = null;
				FacilioField gField = null;
				if(groupByRowField != null){
					if (groupByRowField.getModuleId() > 0) {
						groupByModule = modBean.getModule(groupByRowField.getModuleId());
					}
					if(groupByRowField.getId()> 0) {
						gField = modBean.getField(groupByRowField.getId(), groupByRowField.getModuleId());
					} else {
						gField = modBean.getField(groupByRowField.getName(), groupByModule.getName());
					}
				}
				
				if (groupByRow.getLookupFieldId() > 0) {
					groupByField.setLookupFieldId(groupByRow.getLookupFieldId());
				}
				if(groupByRow.getAlias() != null) {
					groupByField.setAlias(groupByRow.getAlias());
				}
				groupByField.setField(groupByModule, gField);
				groupByField.setAlias(groupByRow.getAlias());	
				if(groupByRow.getModuleName() != null) {
					groupByField.setModule(modBean.getModule(groupByRow.getModuleName()));
				}
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
