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
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReadingAnalysisContext.XCriteriaMode;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportXCriteriaContext;
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
		XCriteriaMode xCriteriaMode = (XCriteriaMode) context.get(FacilioConstants.ContextNames.REPORT_X_CRITERIA_MODE);
		if (metrics != null && !metrics.isEmpty() && startTime != -1 && endTime != -1) {
			Map<Long, ResourceContext> resourceMap = null;
			if (xCriteriaMode == null || xCriteriaMode == XCriteriaMode.NONE) { //Resource map is needed only when there is not x criteria
				resourceMap = getResourceMap(metrics);
			}
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
				
				String name = getName(dataPoint.getyAxis().getField(), mode, xCriteriaMode, resourceMap, metric, (FacilioContext) context);
				dataPoint.setName(name);
				dataPoint.setType(metric.getTypeEnum());
				dataPoint.setAliases(metric.getAliases());
				dataPoint.setTransformWorkflow(metric.getTransformWorkflow());
				dataPoint.setTransformWorkflowId(metric.getTransformWorkflowId());
				
				if (metric.getOrderByFuncEnum() != null && metric.getOrderByFuncEnum() != OrderByFunction.NONE) {
					dataPoint.setOrderByFunc(metric.getOrderByFuncEnum());
					dataPoint.setOrderBy(dataPoint.getyAxis().getField().getName()+", "+dataPoint.getxAxis().getField().getName());
				}
				if (metric.getLimit() != -1) {
					dataPoint.setLimit(metric.getLimit());
				}
				
				dataPoints.add(dataPoint);
			}
			ReportContext report = ReportUtil.constructReport((FacilioContext) context, dataPoints, startTime, endTime);
			ReportXCriteriaContext xCriteria = constructXCriteria(xCriteriaMode, (FacilioContext) context);
			if (xCriteria != null) {
				report.setxCriteria(xCriteria);
			}
			
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
	
	private String getName(FacilioField yField, ReportMode mode, XCriteriaMode xCriteriaMode, Map<Long, ResourceContext> resourceMap, ReadingAnalysisContext metric, FacilioContext context) throws Exception {
		
		if (xCriteriaMode == null || xCriteriaMode == XCriteriaMode.NONE) {
			if(metric.getName() != null && !metric.getName().isEmpty()) {
				return metric.getName();
			}
			else {
				if (mode == ReportMode.CONSOLIDATED) {
					return yField.getDisplayName();
				}
				else {
					StringJoiner joiner = new StringJoiner(", ");
					for (Long parentId : metric.getParentId()) {
						ResourceContext resource = resourceMap.get(parentId);
						joiner.add(resource.getName());
					}
					return joiner.toString()+" ("+yField.getDisplayName()+")";
				}
			}
		}
		
		switch (xCriteriaMode) {
			case NONE:
				throw new RuntimeException("This is not supposed to happen!!");
			case ALL_ASSET_CATEGORY: //Assuming there'll be only one datapoint
				long categoryId = (long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
				AssetCategoryContext category = AssetsAPI.getCategoryForAsset(categoryId);
				if (category == null) {
					throw new IllegalArgumentException("Invalid asset category");
				}
				return category.getName();
		}
		
		return null;
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
	
	private ReportXCriteriaContext constructXCriteria (XCriteriaMode mode, FacilioContext context) throws Exception {
		
		if (mode != null) {
			switch (mode) {
				case NONE:
					return null;
				case ALL_ASSET_CATEGORY:
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					long categoryId = (long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
					ReportXCriteriaContext xCriteria = new ReportXCriteriaContext();
					xCriteria.setxField(modBean.getField("id", FacilioConstants.ContextNames.ASSET));
					FacilioField categoryField = modBean.getField("category", FacilioConstants.ContextNames.ASSET);
					
					Criteria criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition(categoryField, String.valueOf(categoryId), PickListOperators.IS));
					
					xCriteria.setCriteria(criteria);
					return xCriteria;
			}
		}
		return null;
	}
}
