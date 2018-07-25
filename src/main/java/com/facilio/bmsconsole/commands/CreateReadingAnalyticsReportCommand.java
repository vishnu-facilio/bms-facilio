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
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportAnalysisContext;
import com.facilio.report.context.ReportAnalysisContext.ReportMode;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class CreateReadingAnalyticsReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportAnalysisContext> metrics = (List<ReportAnalysisContext>) context.get(FacilioConstants.ContextNames.REPORT_Y_FIELDS);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		ReportMode mode = (ReportMode) context.get(FacilioConstants.ContextNames.REPORT_MODE);
		if (metrics != null && !metrics.isEmpty() && range != null) {
			Map<Long, ResourceContext> resourceMap = getResourceMap(metrics);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			AggregateOperator xAggr = (AggregateOperator) context.get(FacilioConstants.ContextNames.REPORT_X_AGGR);
			List<ReportDataPointContext> dataPoints = new ArrayList<>();
			for (ReportAnalysisContext metric : metrics) {
				ReportDataPointContext dataPoint = new ReportDataPointContext();
				FacilioField yField = modBean.getField(metric.getFieldId());
				dataPoint.setyAxisField(yField);
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(yField.getModule().getName()));
				setFields(dataPoint, mode, fieldMap);
				setCriteriaAndAggr(dataPoint, xAggr, fieldMap, metric);
				setName(dataPoint, yField, mode, resourceMap, metric);
				dataPoints.add(dataPoint);
			}
			ReportContext report = new ReportContext();
			report.setDataPoints(dataPoints);
			report.setDateOperator(DateOperators.BETWEEN);
			report.setDateValue(range.toString());
			
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
		dataPoint.setxAxisField(xField);
		dataPoint.setDateField(fieldMap.get("ttime"));
	}
	
	private void setName(ReportDataPointContext dataPoint, FacilioField yField, ReportMode mode, Map<Long, ResourceContext> resourceMap, ReportAnalysisContext metric) {
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
	
	private void setCriteriaAndAggr(ReportDataPointContext dataPoint, AggregateOperator xAggr, Map<String, FacilioField> fieldMap, ReportAnalysisContext metric) {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), metric.getParentId(), NumberOperators.EQUALS));
		dataPoint.setCriteria(criteria);
		
		if (xAggr != null) {
			dataPoint.setxAxisAggr(xAggr);
		}
		AggregateOperator yAggr = metric.getyAggrEnum();
		if (yAggr != null) {
			dataPoint.setyAxisAggr(yAggr);
		}
	}
	
	private Map<Long, ResourceContext> getResourceMap(List<ReportAnalysisContext> metrics) throws Exception {
		Set<Long> resourceIds = new HashSet<>();
		for (ReportAnalysisContext metric : metrics) {
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
