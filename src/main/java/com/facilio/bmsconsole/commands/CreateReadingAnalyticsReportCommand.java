package com.facilio.bmsconsole.commands;

import java.util.Collections;
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
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class CreateReadingAnalyticsReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long fieldId = (long) context.get(FacilioConstants.ContextNames.READING_FIELD);
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		if (fieldId != -1 && parentId != -1 && range != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			ReportDataPointContext dataPoint = new ReportDataPointContext();
			FacilioField yField = modBean.getField(fieldId);
			dataPoint.setyAxisField(yField);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(yField.getModule().getName()));
			FacilioField xField = fieldMap.get("ttime");
			dataPoint.setxAxisField(xField);
			
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS));
			dataPoint.setCriteria(criteria);
			
			AggregateOperator xAggr = (AggregateOperator) context.get(FacilioConstants.ContextNames.REPORT_X_AGGR);
			if (xAggr != null) {
				dataPoint.setxAxisAggr(xAggr);
			}
			AggregateOperator yAggr = (AggregateOperator) context.get(FacilioConstants.ContextNames.REPORT_Y_AGGR);
			if (yAggr != null) {
				dataPoint.setyAxisAggr(yAggr);
			}
			
			ResourceContext resource = ResourceAPI.getResource(parentId);
			dataPoint.setName(resource.getName()+" ("+yField.getDisplayName()+")");
					
			ReportContext report = new ReportContext();
			report.setDataPoints(Collections.singletonList(dataPoint));		
			
			context.put(FacilioConstants.ContextNames.REPORT, report);
			context.put(FacilioConstants.ContextNames.REPORT_DATE_FILTER, CriteriaAPI.getCondition(xField, range.toString(), DateOperators.BETWEEN));
		}
		else {
			throw new IllegalArgumentException("In sufficient params for Reaging Analysis");
		}
		
		return false;
	}

}
