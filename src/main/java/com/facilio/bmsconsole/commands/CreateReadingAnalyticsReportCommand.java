package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportAxisContext;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class CreateReadingAnalyticsReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReadingAnalysisContext> metrics = (List<ReadingAnalysisContext>) context.get(FacilioConstants.ContextNames.REPORT_Y_FIELDS);
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		ReportMode mode = (ReportMode) context.get(FacilioConstants.ContextNames.REPORT_MODE);
		if (metrics != null && !metrics.isEmpty() && startTime != -1 && endTime != -1) {
			Map<Long, ResourceContext> resourceMap = getResourceMap(metrics);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			AggregateOperator xAggr = (AggregateOperator) context.get(FacilioConstants.ContextNames.REPORT_X_AGGR);
			List<ReportDataPointContext> dataPoints = new ArrayList<>();
			for (ReadingAnalysisContext metric : metrics) {
				ReportDataPointContext dataPoint = new ReportDataPointContext();
				
				ReportAxisContext yAxis = new ReportAxisContext();
				FacilioField yField = modBean.getField(metric.getFieldId());
				yAxis.setField(yField);
				dataPoint.setyAxis(yAxis);
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(yField.getModule().getName()));
				setFields(dataPoint, mode, fieldMap);
				setCriteriaAndAggr(dataPoint, xAggr, fieldMap, metric);
				setName(dataPoint, yField, mode, resourceMap, metric);
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
			throw new IllegalArgumentException("In sufficient params for Reading Analysis");
		}
		
		return false;
	}
	
	private void setFields (ReportDataPointContext dataPoint, ReportMode mode, Map<String, FacilioField> fieldMap) {
		FacilioField xField = null;
		switch (mode) {
			case SERIES:
				xField = fieldMap.get("parentId");
				break;
			case TIMESERIES:
			case CONSOLIDATED:
				xField = fieldMap.get("ttime");
				break;
		}
		ReportAxisContext xAxis = new ReportAxisContext();
		xAxis.setField(xField);
		dataPoint.setxAxis(xAxis);
		dataPoint.setDateField(fieldMap.get("ttime"));
	}
	
	private void setName(ReportDataPointContext dataPoint, FacilioField yField, ReportMode mode, Map<Long, ResourceContext> resourceMap, ReadingAnalysisContext metric) {
		if (mode == ReportMode.CONSOLIDATED) {
			dataPoint.setName(yField.getDisplayName());
		}
		else {
			StringJoiner joiner = new StringJoiner(", ");
			for (Long parentId : metric.getParentId()) {
				ResourceContext resource = resourceMap.get(parentId);
				joiner.add(resource.getName());
			}
			dataPoint.setName(joiner.toString()+" ("+yField.getDisplayName()+")");
		}
	}
	
	private void setCriteriaAndAggr(ReportDataPointContext dataPoint, AggregateOperator xAggr, Map<String, FacilioField> fieldMap, ReadingAnalysisContext metric) {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), metric.getParentId(), NumberOperators.EQUALS));
		dataPoint.setCriteria(criteria);
		
		dataPoint.getxAxis().setAggr(xAggr);
		dataPoint.getyAxis().setAggr(metric.getyAggrEnum());
	}
	
	private Map<Long, ResourceContext> getResourceMap(List<ReadingAnalysisContext> metrics) throws Exception {
		Set<Long> resourceIds = new HashSet<>();
		for (ReadingAnalysisContext metric : metrics) {
			if (metric.getFieldId() != -1 && metric.getParentId() != null) {
				for (Long parentId : metric.getParentId()) {
					resourceIds.add(parentId);
				}
			}
			else {
				throw new IllegalArgumentException("In sufficient params for Reading Analysis for one of the metrics");
			}
		}
		return ResourceAPI.getResourceAsMapFromIds(resourceIds);
	}
}
