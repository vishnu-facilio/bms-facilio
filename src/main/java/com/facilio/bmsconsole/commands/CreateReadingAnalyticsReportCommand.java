package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.CommonAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.SpaceAggregateOperator;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReadingAnalysisContext.ReportFilterMode;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportFilterContext;
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
		ReportFilterMode filterMode = (ReportFilterMode) context.get(FacilioConstants.ContextNames.REPORT_FILTER_MODE);
		if (metrics != null && !metrics.isEmpty() && startTime != -1 && endTime != -1) {
			Map<Long, ResourceContext> resourceMap = null;
			if (filterMode == null || filterMode == ReportFilterMode.NONE) { //Resource map is needed only when there is no filters. For now
				resourceMap = getResourceMap(metrics);
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			ReportContext report = ReportUtil.constructReport((FacilioContext) context, startTime, endTime);
			report.setType(ReportType.READING_REPORT);
			setModeWiseXAggr(report, mode);
			List<ReportFilterContext> filters = constructFilters(filterMode, (FacilioContext) context);
			if (filters != null) {
				report.setFilters(filters);
			}
			
			List<ReportDataPointContext> dataPoints = new ArrayList<>();
			Set<String> resourceAlias = new HashSet<>();
			for (ReadingAnalysisContext metric : metrics) {
				ReportDataPointContext dataPoint = null;
				switch (metric.getTypeEnum()) {
					case MODULE:
						dataPoint = getModuleDataPoint(report, metric, mode, modBean, resourceAlias);
						break;
					case DERIVATION:
						dataPoint = getDerivedDataPoint(report, metric, mode, resourceAlias);
						break;
				}
				
				String name = getName(dataPoint.getyAxis(), mode, filterMode, resourceMap, metric, (FacilioContext) context);
				dataPoint.setName(name);
				dataPoint.setType(metric.getTypeEnum());
				dataPoint.setAliases(metric.getAliases());
				dataPoint.setTransformWorkflow(metric.getTransformWorkflow());
				dataPoint.setTransformWorkflowId(metric.getTransformWorkflowId());
//				dataPoint.setFetchResource(report.getxAggrEnum() instanceof SpaceAggregateOperator);
				
				if (metric.getOrderByFuncEnum() != null && metric.getOrderByFuncEnum() != OrderByFunction.NONE) {
					dataPoint.setOrderByFunc(metric.getOrderByFuncEnum());
					
					List<String> orderBy = new ArrayList<>();
					orderBy.add(dataPoint.getyAxis().getField().getName());
					orderBy.add(dataPoint.getxAxis().getField().getName());
					dataPoint.setOrderBy(orderBy);
				}
				if (metric.getLimit() != -1) {
					dataPoint.setLimit(metric.getLimit());
				}
				
				dataPoints.add(dataPoint);
			}
			report.setDataPoints(dataPoints);
			
			if (!resourceAlias.isEmpty()) {
				JSONArray resourceAliases = new JSONArray();
				resourceAliases.addAll(resourceAlias);
				report.addToReportState(FacilioConstants.ContextNames.REPORT_RESOURCE_ALIASES, resourceAliases);
			}

			context.put(FacilioConstants.ContextNames.REPORT, report);
		}
		else {
			throw new IllegalArgumentException("In sufficient params for Reading Analysis");
		}
		
		return false;
	}
	
	private void setModeWiseXAggr (ReportContext report, ReportMode mode) {
		AggregateOperator aggr = null;
		switch (mode) {
			case SITE:
				aggr = SpaceAggregateOperator.SITE;
				break;
			case BUILDING:
				aggr = SpaceAggregateOperator.BUILDING;
				break;
			default: 
				break;
		}
		if (aggr != null) {
			if (report.getxAggrEnum() != null && report.getxAggrEnum() != CommonAggregateOperator.ACTUAL) {
				throw new IllegalArgumentException("Report X Aggr cannot be specified explicitly for these modes");
			}
			report.setxAggr(aggr);
		}
	}
	
	private ReportDataPointContext getModuleDataPoint(ReportContext report, ReadingAnalysisContext metric, ReportMode mode, ModuleBean modBean, Set<String> resourceAlias) throws Exception {
		ReportDataPointContext dataPoint = new ReportDataPointContext();
		dataPoint.setMetaData(metric.getMetaData());
		dataPoint.setDefaultSortPoint(metric.isDefaultSortPoint());
		ReportYAxisContext yAxis = metric.getyAxis();
		FacilioField yField = modBean.getField(metric.getyAxis().getFieldId());
		yAxis.setField(yField);
		dataPoint.setyAxis(yAxis);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(yField.getModule().getName()));
		setXAndDateFields(dataPoint, mode, fieldMap);
		setCriteria(dataPoint, fieldMap, metric);
		if (dataPoint.isFetchResource()) {
			resourceAlias.add(report.getxAlias());
		}
		
		return dataPoint;
	}
	
	private ReportDataPointContext getDerivedDataPoint(ReportContext report, ReadingAnalysisContext metric, ReportMode mode, Set<String> resourceAlias) {
		ReportDataPointContext dataPoint = new ReportDataPointContext();
		dataPoint.setMetaData(metric.getMetaData());
		ReportFieldContext xAxis = new ReportFieldContext();
		xAxis.setField(getDerivedDPXField(dataPoint, mode));
		dataPoint.setxAxis(xAxis);
		dataPoint.setyAxis(metric.getyAxis());
		if (dataPoint.isFetchResource()) {
			resourceAlias.add(report.getxAlias());
		}
		return dataPoint;
	}
	
	private FacilioField getDerivedDPXField (ReportDataPointContext dp, ReportMode mode) {
		switch (mode) {
			case SERIES:
				return FieldFactory.getField("parentId", "Parent ID", "PARENT_ID", null, FieldType.NUMBER);
			case SITE:
			case BUILDING:
			case RESOURCE:
				dp.setFetchResource(true);
				return FieldFactory.getField("parentId", "Parent ID", "PARENT_ID", null, FieldType.NUMBER);
			case TIMESERIES:
			case CONSOLIDATED:
			case TIME_CONSOLIDATED:
			case TIME_SPLIT:
				return FieldFactory.getField("ttime", "Timestamp", "TTIME", null, FieldType.DATE_TIME);
		}
		return null;
	}
	
	private void setXAndDateFields (ReportDataPointContext dataPoint, ReportMode mode, Map<String, FacilioField> fieldMap) {
		FacilioField xField = null;
		switch (mode) {
			case SERIES:
				xField = fieldMap.get("parentId");
				break;
			case SITE:
			case BUILDING:
			case RESOURCE:
				xField = fieldMap.get("parentId");
				dataPoint.setFetchResource(true);
				break;
			case TIMESERIES:
			case CONSOLIDATED:
			case TIME_CONSOLIDATED:
			case TIME_SPLIT:
				xField = fieldMap.get("ttime");
				break;
		}
		ReportFieldContext xAxis = new ReportFieldContext();
		xAxis.setField(xField);
		dataPoint.setxAxis(xAxis);
		dataPoint.setDateField(fieldMap.get("ttime"));
	}
	
	private String getName(ReportYAxisContext yField, ReportMode mode, ReportFilterMode filterMode, Map<Long, ResourceContext> resourceMap, ReadingAnalysisContext metric, FacilioContext context) throws Exception {
		
		if (filterMode == null || filterMode == ReportFilterMode.NONE) {
			if(metric.getName() != null && !metric.getName().isEmpty()) {
				return metric.getName();
			}
			else {
				if (mode == ReportMode.CONSOLIDATED) {
					return yField.getLabel();
				}
				else {
					StringJoiner joiner = new StringJoiner(", ");
					for (Long parentId : metric.getParentId()) {
						ResourceContext resource = resourceMap.get(parentId);
						joiner.add(resource.getName());
					}
					return joiner.toString()+" ("+ yField.getLabel()+(metric.getPredictedTime() == -1 ? "" : (" @ "+DateTimeUtil.getDateTimeFormat("yyyy-MM-dd HH:mm").format(DateTimeUtil.getDateTime(metric.getPredictedTime()))))+")";
				}
			}
		}
		
		switch (filterMode) {
			case NONE:
				throw new RuntimeException("This is not supposed to happen!!");
			case ALL_ASSET_CATEGORY: //Assuming there'll be only one datapoint
			case SPECIFIC_ASSETS_OF_CATEGORY:
			case SPACE:
				return  yField.getLabel();
		}
		return null;
	}
	
	private void setCriteria(ReportDataPointContext dataPoint, Map<String, FacilioField> fieldMap, ReadingAnalysisContext metric) {
		if (metric.getParentId() != null && !metric.getParentId().isEmpty()) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), metric.getParentId(), NumberOperators.EQUALS));
			dataPoint.setCriteria(criteria);
			
			if (metric.getyAxis().getField().getModule().getTypeEnum() == ModuleType.PREDICTED_READING) {
				if (metric.getPredictedTime() == -1) {
					criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("predictedTime"), DateOperators.CURRENT_HOUR_START_TIME));
				}
				else {
					criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("predictedTime"), String.valueOf(metric.getPredictedTime()), DateOperators.IS));
				}
			}
			
			dataPoint.addMeta(FacilioConstants.ContextNames.PARENT_ID_LIST, metric.getParentId());
		}
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
	
	private ReportFilterContext getBasicReadingReportFilter(ModuleBean modBean) throws Exception {
		ReportFilterContext filter = new ReportFilterContext();
		filter.setFilterFieldName("parentId");
		filter.setField(modBean.getField("id", FacilioConstants.ContextNames.ASSET));
		return filter;
	}
	
	private List<ReportFilterContext> constructFilters (ReportFilterMode mode, FacilioContext context) throws Exception {
		
		if (mode != null) {
			Criteria criteria = null;
			ReportFilterContext filter = null;
			switch (mode) {
				case NONE:
					return null;
				case ALL_ASSET_CATEGORY:
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					List<Long> categoryId = (List<Long>) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
					filter = getBasicReadingReportFilter(modBean);
					FacilioField categoryField = modBean.getField("category", FacilioConstants.ContextNames.ASSET);
					
					criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition(categoryField, categoryId, PickListOperators.IS));
					
					filter.setCriteria(criteria);
					return Collections.singletonList(filter);
				case SPECIFIC_ASSETS_OF_CATEGORY:
					List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
					if (parentIds == null || parentIds.isEmpty()) {
						throw new IllegalArgumentException("Atleast one asset has to be mentioned for this xCriteri mode");
					}
					
					modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					filter = getBasicReadingReportFilter(modBean);
					filter.setDataFilter(true);
					filter.setFilterOperator(PickListOperators.IS);
					filter.setFilterValue(StringUtils.join(parentIds, ","));
					return Collections.singletonList(filter);
				case SPACE:
					modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					categoryId = (List<Long>) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
					List<Long> spaceId = (List<Long>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST);
					filter = getBasicReadingReportFilter(modBean);
					categoryField = modBean.getField("category", FacilioConstants.ContextNames.ASSET);
					FacilioField spaceField = modBean.getField("space", FacilioConstants.ContextNames.ASSET);
					
					criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition(categoryField, categoryId, PickListOperators.IS));
					criteria.addAndCondition(CriteriaAPI.getCondition(spaceField, spaceId, BuildingOperator.BUILDING_IS));
					
					filter.setCriteria(criteria);
					return Collections.singletonList(filter);
			}
		}
		return null;
	}
}
