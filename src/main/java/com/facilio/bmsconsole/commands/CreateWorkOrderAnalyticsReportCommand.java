package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportAxisContext;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportGroupByField;
import com.facilio.report.context.WorkorderAnalysisContext;

public class CreateWorkOrderAnalyticsReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkorderAnalysisContext> metrics = (List<WorkorderAnalysisContext>) context.get(FacilioConstants.ContextNames.REPORT_Y_FIELDS);
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		long xFieldId = (long) context.get(FacilioConstants.ContextNames.REPORT_X_FIELD);
		if (metrics != null && !metrics.isEmpty() && startTime != -1 && endTime != -1 && xFieldId != -1) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField xField = modBean.getField(xFieldId);
			ReportAxisContext xAxis = new ReportAxisContext();
			xAxis.setField(xField);
			List<FacilioField> fields = modBean.getAllFields(xField.getModule().getName());
			Map<Long, FacilioField> xfieldIdMap = FieldFactory.getAsIdMap(fields);
			Map<String, LookupField> xlookupFields = getLookupFields(fields);
			AggregateOperator xAggr = (AggregateOperator) context.get(FacilioConstants.ContextNames.REPORT_X_AGGR);
			xAxis.setAggr(xAggr);
			List<ReportDataPointContext> dataPoints = new ArrayList<>();
			for (WorkorderAnalysisContext metric : metrics) {
				ReportDataPointContext dataPoint = new ReportDataPointContext();
				dataPoint.setxAxis(xAxis);
				
				setYAxis(modBean, metric, dataPoint, xfieldIdMap, xlookupFields);
				dataPoint.setCriteria(metric.getCriteria());
				createReportGroupByFields(modBean, metric, dataPoint, xfieldIdMap, xlookupFields);
				
				dataPoints.add(dataPoint);
			}
			ReportContext report = new ReportContext();
			report.setDataPoints(dataPoints);
			report.setDateOperator(DateOperators.BETWEEN);
			report.setDateValue(startTime+", "+endTime);
			CommonReportUtil.fetchBaseLines(report, (List<ReportBaseLineContext>) context.get(FacilioConstants.ContextNames.BASE_LINE_LIST));
			
			context.put(FacilioConstants.ContextNames.REPORT, report);
		}
		else {
			throw new IllegalArgumentException("In sufficient params for WorkOrder Analysis");
		}
		return false;
	}
	
	private void createReportGroupByFields(ModuleBean modBean, WorkorderAnalysisContext metric, ReportDataPointContext dataPoint, Map<Long, FacilioField> xFieldIdMap, Map<String, LookupField> xLookupFields) throws Exception {
		if (metric.getGroupBy() != null && !metric.getGroupBy().isEmpty()) {
			List<FacilioField> yFields = modBean.getAllFields(dataPoint.getyAxis().getField().getModule().getName());
			Map<Long, FacilioField> yFieldIdMap = FieldFactory.getAsIdMap(yFields);
			Map<String, LookupField> yLookupFields = getLookupFields(yFields);
			List<ReportGroupByField> groupBy = new ArrayList<>();
			for (Long groupById : metric.getGroupBy()) {
				ReportGroupByField reportGroupBy = new ReportGroupByField();
				FacilioField groupByField = xFieldIdMap.get(groupById);
				if (groupByField == null) {
					groupByField = yFieldIdMap.get(groupByField);
					if (groupByField == null) {
						groupByField = modBean.getField(groupById);
						LookupField lookupField = xLookupFields.get(groupByField.getModule().getName());
						if (lookupField == null) {
							lookupField = yLookupFields.get(groupByField.getModule().getName());
						}
						if (lookupField == null) {
							throw new IllegalArgumentException("Invalid Y Field ID : "+metric.getyFieldId()+" for WorkOrder Analysis as there is no relation with X Field : "+dataPoint.getxAxis().getField().getName()+" or with Y Field : "+dataPoint.getyAxis().getField().getName());
						}
						reportGroupBy.setJoinOn(getJoinOn(lookupField));
					}
				}
				reportGroupBy.setField(groupByField);
				groupBy.add(reportGroupBy);
			}
			dataPoint.setGroupByFields(groupBy);
		}
	}
	
	private void setYAxis(ModuleBean modBean, WorkorderAnalysisContext metric, ReportDataPointContext dataPoint, Map<Long, FacilioField> xFieldIdMap, Map<String, LookupField> xLookupFields) throws Exception {
		ReportAxisContext yAxis = new ReportAxisContext();
		FacilioField yField = xFieldIdMap.get(metric.getyFieldId());
		if (yField == null) {
			yField = modBean.getField(metric.getyFieldId());
			LookupField lookupField = xLookupFields.get(yField.getModule().getName());
			if (lookupField == null) {
				throw new IllegalArgumentException("Invalid Y Field ID : "+metric.getyFieldId()+" for WorkOrder Analysis as there is no relation with X Field : "+dataPoint.getxAxis().getField().getName());
			}
			yAxis.setJoinOn(getJoinOn(lookupField));
			
		}
		yAxis.setField(yField);
		yAxis.setAggr(metric.getyAggrEnum());
		dataPoint.setyAxis(yAxis);
	}
	
	private String getJoinOn(LookupField lookupField) {
		FacilioField idField = null;
		if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
			idField = LookupSpecialTypeUtil.getIdField(lookupField.getSpecialType());
		}
		else {
			idField = FieldFactory.getIdField(lookupField.getLookupModule());
		}
		return lookupField.getCompleteColumnName()+" = "+idField.getCompleteColumnName();
	}
	
	private Map<String, LookupField> getLookupFields(List<FacilioField> fields) {
		Map<String, LookupField> lookupFields = new HashMap<>();
		for (FacilioField field : fields) {
			if (field.getDataTypeEnum() == FieldType.LOOKUP) {
				LookupField lookupField = (LookupField) field;
				if (LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
					lookupFields.put(lookupField.getSpecialType(), lookupField);
				}
				else {
					lookupFields.put(lookupField.getLookupModule().getName(), lookupField);
				}
			}
		}
		return lookupFields;
	}
}
