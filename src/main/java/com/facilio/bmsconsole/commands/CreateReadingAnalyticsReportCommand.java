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
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.report.util.ReportUtil;

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
			List<ReportDataPointContext> dataPoints = new ArrayList<>();
			for (ReadingAnalysisContext metric : metrics) {
				ReportDataPointContext dataPoint = null;
				switch (metric.getTypeEnum()) {
					case MODULE:
						dataPoint = getModuleDataPoint(metric, mode, modBean);
						break;
					case DERIVATION:
						dataPoint = getDerivedDataPoint(metric);
						break;
				}
				
				setName(dataPoint, dataPoint.getyAxis().getField(), mode, resourceMap, metric);
				dataPoint.setType(metric.getTypeEnum());
				dataPoint.setAliases(metric.getAliases());
				dataPoint.setTransformWorkflow(metric.getTransformWorkflow());
				dataPoint.setTransformWorkflowId(metric.getTransformWorkflowId());
				dataPoints.add(dataPoint);
			}
			ReportContext report = ReportUtil.constructReport((FacilioContext) context, dataPoints, startTime, endTime);
			context.put(FacilioConstants.ContextNames.REPORT, report);
		}
		else {
			throw new IllegalArgumentException("In sufficient params for Reading Analysis");
		}
		
		return false;
	}
	
	private ReportDataPointContext getModuleDataPoint(ReadingAnalysisContext metric, ReportMode mode, ModuleBean modBean) throws Exception {
		ReportDataPointContext dataPoint = new ReportDataPointContext();
		ReportYAxisContext yAxis = metric.getyAxis();
		FacilioField yField = modBean.getField(metric.getyAxis().getFieldId());
		yAxis.setField(yField);
		dataPoint.setyAxis(yAxis);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(yField.getModule().getName()));
		setXAndDateFields(dataPoint, mode, fieldMap);
		setCriteria(dataPoint, fieldMap, metric);
		return dataPoint;
	}
	
	private ReportDataPointContext getDerivedDataPoint(ReadingAnalysisContext metric) {
		ReportDataPointContext dataPoint = new ReportDataPointContext();
		ReportYAxisContext xAxis = new ReportYAxisContext();
		xAxis.setField(FieldFactory.getField("ttime", "Timestamp", "TTIME", null, FieldType.DATE_TIME));
		dataPoint.setxAxis(xAxis);
		dataPoint.setyAxis(metric.getyAxis());
		return dataPoint;
	}
	
	private void setXAndDateFields (ReportDataPointContext dataPoint, ReportMode mode, Map<String, FacilioField> fieldMap) {
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
		ReportFieldContext xAxis = new ReportFieldContext();
		xAxis.setField(xField);
		dataPoint.setxAxis(xAxis);
		dataPoint.setDateField(fieldMap.get("ttime"));
	}
	
	private void setName(ReportDataPointContext dataPoint, FacilioField yField, ReportMode mode, Map<Long, ResourceContext> resourceMap, ReadingAnalysisContext metric) {
		
		if(metric.getName() != null && !metric.getName().isEmpty()) {
			dataPoint.setName(metric.getName());
		}
		else {
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
	}
	
	private void setCriteria(ReportDataPointContext dataPoint, Map<String, FacilioField> fieldMap, ReadingAnalysisContext metric) {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), metric.getParentId(), NumberOperators.EQUALS));
		dataPoint.setCriteria(criteria);
	}
	
	private Map<Long, ResourceContext> getResourceMap(List<ReadingAnalysisContext> metrics) throws Exception {
		Set<Long> resourceIds = new HashSet<>();
		for (ReadingAnalysisContext metric : metrics) {
			if (metric.getyAxis().getFieldId() != -1 && metric.getParentId() != null) {
				for (Long parentId : metric.getParentId()) {
					resourceIds.add(parentId);
				}
			}
			else if(metric.getType() != DataPointType.DERIVATION.getValue()) {
				throw new IllegalArgumentException("In sufficient params for Reading Analysis for one of the metrics");
			}
		}
		return ResourceAPI.getResourceAsMapFromIds(resourceIds);
	}
}
