package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.RegressionContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.SpaceAggregateOperator;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.*;
import com.facilio.report.context.ReadingAnalysisContext.ReportFilterMode;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class CreateReadingAnalyticsReportCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReadingAnalysisContext> metrics = (List<ReadingAnalysisContext>) context.get(FacilioConstants.ContextNames.REPORT_Y_FIELDS);
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		ReportMode mode = (ReportMode) context.get(FacilioConstants.ContextNames.REPORT_MODE);
		ReportFilterMode filterMode = (ReportFilterMode) context.get(FacilioConstants.ContextNames.REPORT_FILTER_MODE);
		List<RegressionContext> regressionConfig = (ArrayList<RegressionContext>) context.get(FacilioConstants.ContextNames.REGRESSION_CONFIG);
		
		if (metrics != null && !metrics.isEmpty() && startTime != -1 && endTime != -1) {
			Map<Long, ResourceContext> resourceMap = null;
			if (filterMode == null || filterMode == ReportFilterMode.NONE) { //Resource map is needed only when there is no filters. For now
				resourceMap = getResourceMap(metrics);
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			ReportContext report = ReportUtil.constructReport((FacilioContext) context, startTime, endTime);
			
			if(regressionConfig != null) {
				report.setType(ReportContext.ReportType.REGRESSION_REPORT);
			}
			else if(context.get(FacilioConstants.ContextNames.REPORT_TEMPLATE) != null) {
				report.setType(ReportType.TEMPLATE_REPORT);
			}
			else {
				report.setType(ReportType.READING_REPORT);
			}
			
			
			setModeWiseXAggr(report, mode);
			List<ReportFilterContext> filters = constructFilters(filterMode, (FacilioContext) context);
			if (filters != null) {
				report.setFilters(filters);
			}
			JSONObject ttimeFilter = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_TTIME_FILTER);
			if(ttimeFilter != null) {
				report.setReportTTimeFilter(ttimeFilter);
			}
			List<ReportDataPointContext> dataPoints = new ArrayList<>();
			JSONObject resourceAlias = new JSONObject();
			for (ReadingAnalysisContext metric : metrics) {
				ReportDataPointContext dataPoint = null;
				switch (metric.getTypeEnum()) {
					case MODULE:
					case FIELD:
					case ADDITIONAL:
						dataPoint = getModuleDataPoint(report, metric, mode, modBean, resourceAlias);
						break;
					case DERIVATION:
						dataPoint = getDerivedDataPoint(report, metric, mode, resourceAlias);
						break;
				}
				
				String name = getName(dataPoint.getyAxis(), mode, filterMode, resourceMap, metric, (FacilioContext) context);
				dataPoint.setName(name);
				dataPoint.setResourceName(getResourceName(dataPoint.getyAxis(), mode, filterMode, resourceMap, metric, (FacilioContext) context));
				dataPoint.setAssetCategoryId(getAssetCategoryId(dataPoint.getyAxis(), mode, filterMode, resourceMap, metric, (FacilioContext) context));
				dataPoint.setBuildingId(metric.getBuildingId());
				dataPoint.setType(metric.getTypeEnum());
				dataPoint.setAliases(metric.getAliases());
				dataPoint.setTransformWorkflow(metric.getTransformWorkflow());
				dataPoint.setTransformWorkflowId(metric.getTransformWorkflowId());
//				dataPoint.setFetchResource(report.getxAggrEnum() instanceof SpaceAggregateOperator);
				
				if (metric.getOrderByFuncEnum() != null && metric.getOrderByFuncEnum() != OrderByFunction.NONE) {
					dataPoint.setOrderByFunc(metric.getOrderByFuncEnum());
					
					List<String> orderBy = new ArrayList<>();
					orderBy.add(ReportUtil.getAggrFieldName(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum()));
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
				report.addToReportState(FacilioConstants.ContextNames.REPORT_RESOURCE_ALIASES, (JSONObject) resourceAlias);
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
			case FLOOR:
				aggr = SpaceAggregateOperator.FLOOR;
				break;
			case SPACE:
				aggr = SpaceAggregateOperator.SPACE;
				break;
			default: 
				break;
		}
		if (aggr != null) {
			if (report.getxAggrEnum() != null) {
				if (report.getxAggrEnum() != CommonAggregateOperator.ACTUAL || report.getxAggrEnum() != BmsAggregateOperators.DateAggregateOperator.HOURSOFDAYONLY) {
					throw new IllegalArgumentException("Report X Aggr cannot be specified explicitly for these modes");
				}
			}
			report.setxAggr(aggr);
		}
	}
	
	private ReportDataPointContext getModuleDataPoint(ReportContext report, ReadingAnalysisContext metric, ReportMode mode, ModuleBean modBean, JSONObject resourceAlias) throws Exception {
		ReportDataPointContext dataPoint = new ReportDataPointContext();
		dataPoint.setMetaData(metric.getMetaData());
		dataPoint.setDefaultSortPoint(metric.isDefaultSortPoint());
		ReportYAxisContext yAxis = metric.getyAxis();
		FacilioField yField = modBean.getField(metric.getyAxis().getFieldId());
		yAxis.setField(yField.getModule(), yField);
		dataPoint.setyAxis(yAxis);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(yField.getModule().getName()));
		setXAndDateFields(dataPoint, mode, fieldMap);
		setCriteria(dataPoint, fieldMap, metric);
		if(metric.isxDataPoint()) {
			dataPoint.setxDataPoint(metric.isxDataPoint());
		}
		if(metric.isduplicateDataPoint()) {
			dataPoint.setduplicateDataPoint(metric.isduplicateDataPoint());
		}
		String moduleName = metric.getModuleName();
		if (moduleName == null) {
			moduleName = FacilioConstants.ContextNames.RESOURCE;
			metric.setModuleName(moduleName);
		}
		if (dataPoint.isFetchResource()) {
			resourceAlias.put(report.getxAlias(), moduleName);
		}
		dataPoint.setModuleName(moduleName);
		if(metric.getRule_aggr_type() != null) {
			dataPoint.setRule_aggr_type(metric.getRule_aggr_type());
		}
		dataPoint.setRightInclusive(metric.isRightInclusive());
		return dataPoint;
	}
	
	private ReportDataPointContext getDerivedDataPoint(ReportContext report, ReadingAnalysisContext metric, ReportMode mode, JSONObject resourceAlias) {
		ReportDataPointContext dataPoint = new ReportDataPointContext();
		dataPoint.setMetaData(metric.getMetaData());
		ReportFieldContext xAxis = new ReportFieldContext();
		
		FacilioField field = getDerivedDPXField(dataPoint, mode);
		xAxis.setField(field.getModule(), field);
		dataPoint.setxAxis(xAxis);
		dataPoint.setyAxis(metric.getyAxis());
		if(metric.isxDataPoint()) {
			dataPoint.setxDataPoint(metric.isxDataPoint());
		}
		if(metric.isduplicateDataPoint()) {
			dataPoint.setduplicateDataPoint(metric.isduplicateDataPoint());
		}
		String moduleName = metric.getModuleName();
		if (moduleName == null) {
			moduleName = FacilioConstants.ContextNames.RESOURCE;
			metric.setModuleName(moduleName);
		}
		if (dataPoint.isFetchResource()) {
			resourceAlias.put(report.getxAlias(), moduleName);
		}
		dataPoint.setModuleName(moduleName);
		return dataPoint;
	}
	
	private FacilioField getDerivedDPXField (ReportDataPointContext dp, ReportMode mode) {
		switch (mode) {
			case SERIES:
				return FieldFactory.getField("parentId", "Parent ID", "PARENT_ID", null, FieldType.NUMBER);
			case SITE:
			case BUILDING:
			case FLOOR:
			case SPACE:
			case RESOURCE:
				dp.setFetchResource(true);
				return FieldFactory.getField("parentId", "Parent ID", "PARENT_ID", null, FieldType.NUMBER);
			case TIMESERIES:
			case CONSOLIDATED:
			case TIME_CONSOLIDATED:
			case TIME_SPLIT:
			case TIME_DURATION:
				return FieldFactory.getField("ttime", "Timestamp", "TTIME", null, FieldType.DATE_TIME);
		}
		return null;
	}
	
	private void setXAndDateFields (ReportDataPointContext dataPoint, ReportMode mode, Map<String, FacilioField> fieldMap) throws Exception {
		FacilioField xField = null;
		ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		switch (mode) {
			case SERIES:
				xField = fieldMap.get("parentId");
				break;
			case SITE:
			case BUILDING:
			case FLOOR:
			case SPACE:
			case RESOURCE:
				if(fieldMap.get("parentId")!= null) {
					xField = fieldMap.get("parentId");
				}else {
					FacilioModule module = bean.getModule(mode.getStringVal());
					xField = FieldFactory.getIdField(module);
				}
				dataPoint.setFetchResource(true);
				break;
			case TIMESERIES:
			case CONSOLIDATED:
			case TIME_CONSOLIDATED:
			case TIME_SPLIT:
			case TIME_DURATION:
				xField = fieldMap.get("ttime");
				break;
		}
		ReportFieldContext xAxis = new ReportFieldContext();
		xAxis.setField(xField.getModule(), xField);
		dataPoint.setxAxis(xAxis);
		if(fieldMap.get("ttime")!= null) {
		ReportFieldContext dateField = new ReportFieldContext();
		dateField.setField(fieldMap.get("ttime").getModule(), fieldMap.get("ttime"));
		dataPoint.setDateField(dateField);
		}
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
					Boolean isGetReportFromAlarm =  (Boolean) context.get(FacilioConstants.ContextNames.REPORT_FROM_ALARM);
					ResourceContext alarmResource =  (ResourceContext) context.get(FacilioConstants.ContextNames.ALARM_RESOURCE);
					if(metric.getParentId() != null) {
						for (Long parentId : metric.getParentId()) {
						if(isGetReportFromAlarm != null && isGetReportFromAlarm && alarmResource != null && alarmResource.getId() == parentId) {
							continue;
						}
						ResourceContext resource = resourceMap.get(parentId);
						joiner.add(resource.getName());
						}
					}
					if(joiner.length() > 0) {
						return joiner.toString()+" ("+ yField.getLabel()+(metric.getPredictedTime() == -1 ? "" : (" @ "+DateTimeUtil.getDateTimeFormat("yyyy-MM-dd HH:mm").format(DateTimeUtil.getDateTime(metric.getPredictedTime()))))+")";
					}
					else {
						return yField.getLabel()+(metric.getPredictedTime() == -1 ? "" : (" @ "+DateTimeUtil.getDateTimeFormat("yyyy-MM-dd HH:mm").format(DateTimeUtil.getDateTime(metric.getPredictedTime()))));
					}
				}
			}
		}
		
		switch (filterMode) {
			case NONE:
				throw new RuntimeException("This is not supposed to happen!!");
			case ALL_ASSET_CATEGORY: //Assuming there'll be only one datapoint
			case SPECIFIC_ASSETS_OF_CATEGORY:
			case SPACE:
			case SITE:
				return  yField.getLabel();
		}
		return null;
	}
	
	private String getResourceName(ReportYAxisContext yField, ReportMode mode, ReportFilterMode filterMode, Map<Long, ResourceContext> resourceMap, ReadingAnalysisContext metric, FacilioContext context) throws Exception {
		
		if (filterMode == null || filterMode == ReportFilterMode.NONE) {
				if (mode == ReportMode.CONSOLIDATED) {
					return " ";
				}
				else {
					StringJoiner joiner = new StringJoiner(", ");
					Boolean isGetReportFromAlarm =  (Boolean) context.get(FacilioConstants.ContextNames.REPORT_FROM_ALARM);
					ResourceContext alarmResource =  (ResourceContext) context.get(FacilioConstants.ContextNames.ALARM_RESOURCE);
					if(metric.getParentId() != null) {
						for (Long parentId : metric.getParentId()) {
							if(isGetReportFromAlarm != null && isGetReportFromAlarm && alarmResource != null && alarmResource.getId() == parentId) {
								continue;
							}
							ResourceContext resource = resourceMap.get(parentId);
							if(resource != null) {
								joiner.add(resource.getName());
							}
						}
					}
					if(joiner.length() > 0) {
						return joiner.toString();
					}
					else {
						return " ";
					}
				}
			}
		
		switch (filterMode) {
			case NONE:
				throw new RuntimeException("This is not supposed to happen!!");
			case ALL_ASSET_CATEGORY: //Assuming there'll be only one datapoint
			case SPECIFIC_ASSETS_OF_CATEGORY:
			case SPACE:
			case SITE:
				return  " ";
		}
		return null;
	}
	
private long getAssetCategoryId(ReportYAxisContext yField, ReportMode mode, ReportFilterMode filterMode, Map<Long, ResourceContext> resourceMap, ReadingAnalysisContext metric, FacilioContext context) throws Exception {
		
		if (filterMode == null || filterMode == ReportFilterMode.NONE) {
				if (mode == ReportMode.CONSOLIDATED) {
					return -1;
				}
				else {
					Boolean isGetReportFromAlarm =  (Boolean) context.get(FacilioConstants.ContextNames.REPORT_FROM_ALARM);
					ResourceContext alarmResource =  (ResourceContext) context.get(FacilioConstants.ContextNames.ALARM_RESOURCE);
					if(metric.getParentId() != null) {
						for (Long parentId : metric.getParentId()) {
							if(isGetReportFromAlarm != null && isGetReportFromAlarm && alarmResource != null && alarmResource.getId() == parentId) {
								continue;
							}
							ResourceContext resource = resourceMap.get(parentId);
							if(resource != null && resource.getResourceTypeEnum() == ResourceType.ASSET) {
								AssetContext asset = AssetsAPI.getAssetInfo(parentId);
								//Assuming only one parentId -- temp
								return asset.getCategory().getId();
							}
							else {
								return -1;
							}
						}
					}
					return -1;
				}
			}
		
		switch (filterMode) {
			case NONE:
				throw new RuntimeException("This is not supposed to happen!!");
			case ALL_ASSET_CATEGORY: //Assuming there'll be only one datapoint
			case SPECIFIC_ASSETS_OF_CATEGORY:
			case SPACE:
			case SITE:
				return  -1;
		}
		return -1;
	}
	
	private void setCriteria(ReportDataPointContext dataPoint, Map<String, FacilioField> fieldMap, ReadingAnalysisContext metric) {
		if (metric.getParentId() != null && !metric.getParentId().isEmpty()) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), metric.getParentId(), NumberOperators.EQUALS));
			dataPoint.setCriteria(criteria);
			
			if (metric.getyAxis().getModule().getTypeEnum() == ModuleType.PREDICTED_READING) {
				if (metric.getPredictedDuration() != -1) {
					criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("predictedTime"), String.valueOf(metric.getPredictedDuration()), DateOperators.HOUR_START_TIME));
				}
				else if (metric.getPredictedTime() != -1) {
					criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("predictedTime"), String.valueOf(metric.getPredictedTime()), DateOperators.IS));
				}
				else {
					criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("predictedTime"), "0", DateOperators.HOUR_START_TIME));
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
	
	private ReportFilterContext getBasicReadingReportFilter(ModuleBean modBean, String filterFieldName) throws Exception {
		ReportFilterContext filter = new ReportFilterContext();
		filter.setFilterFieldName(filterFieldName);
		
		FacilioField field = modBean.getField("id", FacilioConstants.ContextNames.ASSET);
		filter.setField(field.getModule(), field);
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
					filter = getBasicReadingReportFilter(modBean, "parentId");
					FacilioField categoryField = modBean.getField("category", FacilioConstants.ContextNames.ASSET);
					
					criteria = new Criteria();
					if(categoryId != null) {
						criteria.addAndCondition(CriteriaAPI.getCondition(categoryField, categoryId, PickListOperators.IS));
					}
					
					filter.setCriteria(criteria);
					return Collections.singletonList(filter);
				case SPECIFIC_ASSETS_OF_CATEGORY:
					List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
					if (parentIds == null || parentIds.isEmpty()) {
						throw new IllegalArgumentException("Atleast one asset has to be mentioned for this xCriteri mode");
					}
					
					modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					filter = getBasicReadingReportFilter(modBean, "parentId");
					filter.setDataFilter(true);
					filter.setFilterOperator(PickListOperators.IS);
					filter.setFilterValue(StringUtils.join(parentIds, ","));
					return Collections.singletonList(filter);
				case SPACE:
					modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					categoryId = (List<Long>) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
					List<Long> spaceId = (List<Long>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST);
					filter = getBasicReadingReportFilter(modBean, "parentId");
					categoryField = modBean.getField("category", FacilioConstants.ContextNames.ASSET);
					FacilioField spaceField = modBean.getField("space", FacilioConstants.ContextNames.ASSET);
					
					criteria = new Criteria();
					if(categoryId != null) {
						criteria.addAndCondition(CriteriaAPI.getCondition(categoryField, categoryId, PickListOperators.IS));
					}
					if(spaceId != null) {
						criteria.addAndCondition(CriteriaAPI.getCondition(spaceField, spaceId, BuildingOperator.BUILDING_IS));
					}
					
					filter.setCriteria(criteria);
					return Collections.singletonList(filter);
				case SITE:
					return null;
			}
		}
		return null;
	}
}
