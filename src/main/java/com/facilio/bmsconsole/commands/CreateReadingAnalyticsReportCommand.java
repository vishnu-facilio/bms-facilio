package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			AggregateOperator xAggr = (AggregateOperator) context.get(FacilioConstants.ContextNames.REPORT_X_AGGR);
			List<ReportDataPointContext> dataPoints = new ArrayList<>();
			for (ReportAnalysisContext metric : metrics) {
				if (metric.getFieldId() != -1 && metric.getParentId() != -1) {
					ReportDataPointContext dataPoint = new ReportDataPointContext();
					FacilioField yField = modBean.getField(metric.getFieldId());
					dataPoint.setyAxisField(yField);
					Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(yField.getModule().getName()));
					FacilioField xField = null;
					switch (mode) {
						case SERIES:
							xField = fieldMap.get("parentId");
							break;
						case TIMESERIES:
							xField = fieldMap.get("ttime");
							break;
					}
					dataPoint.setxAxisField(xField);
					dataPoint.setDateField(fieldMap.get("ttime"));
					
					Criteria criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(metric.getParentId()), NumberOperators.EQUALS));
					dataPoint.setCriteria(criteria);
					
					if (xAggr != null) {
						dataPoint.setxAxisAggr(xAggr);
					}
					AggregateOperator yAggr = metric.getyAggrEnum();
					if (yAggr != null) {
						dataPoint.setyAxisAggr(yAggr);
					}
					
					ResourceContext resource = ResourceAPI.getResource(metric.getParentId());
					dataPoint.setName(resource.getName()+" ("+yField.getDisplayName()+")");
					dataPoints.add(dataPoint);
				}
				else {
					throw new IllegalArgumentException("In sufficient params for Reaging Analysis for one of the metrics");
				}
			}
			ReportContext report = new ReportContext();
			report.setDataPoints(dataPoints);
			report.setDateOperator(DateOperators.BETWEEN);
			report.setDateValue(range.toString());
			
			context.put(FacilioConstants.ContextNames.REPORT, report);
		}
		else {
			throw new IllegalArgumentException("In sufficient params for Reaging Analysis");
		}
		
		return false;
	}

}
