package com.facilio.report.context;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.report.ReportDynamicKpiContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

public class ReportDataPointContext implements  Cloneable{

	private long id = -1; 
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public Criteria getParentCriteriaFilter() {
		return parentCriteriaFilter;
	}

	public void setParentCriteriaFilter(Criteria parentCriteriaFilter) {
		this.parentCriteriaFilter = parentCriteriaFilter;
	}

	public Criteria parentCriteriaFilter;
	private long buildingId = -1;
	public boolean isRightInclusive() {
		return rightInclusive;
	}

	public void setRightInclusive(boolean rightInclusive) {
		this.rightInclusive = rightInclusive;
	}

	public boolean rightInclusive;
	public ReportDynamicKpiContext getDynamicKpi() {
		return dynamicKpi;
	}

	public void setDynamicKpi(ReportDynamicKpiContext dynamicKpi) {
		this.dynamicKpi = dynamicKpi;
	}

	public ReportDynamicKpiContext dynamicKpi;

	public String getConvertTounit() {
		return convertTounit;
	}

	public void setConvertTounit(String convertTounit) {
		this.convertTounit = convertTounit;
	}

	private String convertTounit;
	public long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
	}

	public FacilioModule getParentReadingModule() {
		return parentReadingModule;
	}

	public void setParentReadingModule(FacilioModule parentReadingModule) {
		this.parentReadingModule = parentReadingModule;
	}

	public FacilioModule parentReadingModule;
	public List<Long> getDp_parent_ids() {
		return dp_parent_ids;
	}

	public void setDp_parent_ids(List<Long> dp_parent_ids) {
		this.dp_parent_ids = dp_parent_ids;
	}

	public List<Long> dp_parent_ids;

	public String getRule_aggr_type() {
		return rule_aggr_type;
	}

	public void setRule_aggr_type(String rule_aggr_type) {
		this.rule_aggr_type = rule_aggr_type;
	}

	public String rule_aggr_type = "SUM";
	private String resourceName;
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	private long assetCategoryId=-1;
	public long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long reportId = -1;
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	 private String kpiType;

	public String getKpiType() {
		return kpiType;
	}

	public void setKpiType(String kpiType) {
		this.kpiType = kpiType;
	}

	private ReportFieldContext xAxis;
	public ReportFieldContext getxAxis() {
		return xAxis;
	}
	public void setxAxis(ReportFieldContext xAxis) {
		this.xAxis = xAxis;
	}

	private ReportYAxisContext yAxis;
	public ReportYAxisContext getyAxis() {
		return yAxis;
	}
	public void setyAxis(ReportYAxisContext yAxis) {
		this.yAxis = yAxis;
	}

	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public Criteria getV2Criteria() {
		return v2Criteria;
	}

	public int getCriteriaType() {
		return criteriaType;
	}

	public void setCriteriaType(int criteriaType) {
		this.criteriaType = criteriaType;
	}

	public int criteriaType;
	public void setV2Criteria(Criteria v2Criteria) {
		this.v2Criteria = v2Criteria;
	}

	private Criteria v2Criteria;
	private Criteria otherCriteria;
	public void setOtherCriteria(Criteria otherCriteria) {
		this.otherCriteria = otherCriteria;
	}
	public Criteria getOtherCriteria() {
		return otherCriteria;
	}
	
	@JsonIgnore
	public Criteria getAllCriteria() {
		if (this.criteria == null && this.otherCriteria == null && this.parentCriteriaFilter == null) {
			return null;
		}
		
		Criteria c = new Criteria();
		c.andCriteria(this.criteria);
		c.andCriteria(this.otherCriteria);
		if(this.parentCriteriaFilter != null ){
			c.andCriteria(this.parentCriteriaFilter);
		}
		return c;
	}

	private long transformCriteriaId = -1;
	public long getTransformCriteriaId() {
		return transformCriteriaId;
	}
	public void setTransformCriteriaId(long transformCriteriaId) {
		this.transformCriteriaId = transformCriteriaId;
	}
	
	private Criteria transformCriteria;
	public Criteria getTransformCriteria() {
		return transformCriteria;
	}
	public void setTransformCriteria(Criteria transformCriteria) {
		this.transformCriteria = transformCriteria;
	}
	
	private long transformWorkflowId = -1;
	public long getTransformWorkflowId() {
		return transformWorkflowId;
	}
	public void setTransformWorkflowId(long transformWorkflowId) {
		this.transformWorkflowId = transformWorkflowId;
	}
	
	private WorkflowContext transformWorkflow;
	public WorkflowContext getTransformWorkflow() {
		return transformWorkflow;
	}
	public void setTransformWorkflow(WorkflowContext transformWorkflow) {
		this.transformWorkflow = transformWorkflow;
	}
	
	private int limit = -1;
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	private List<String> orderBy;
	public List<String> getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(List<String> orderBy) {
		this.orderBy = orderBy;
	}

	private OrderByFunction orderByFunc;
	public OrderByFunction getOrderByFuncEnum() {
		return orderByFunc;
	}
	public void setOrderByFunc(OrderByFunction orderByFunc) {
		this.orderByFunc = orderByFunc;
	}
	public int getOrderByFunc() {
		if (orderByFunc != null) {
			return orderByFunc.getValue();
		}
		return -1;
	}
	public void setOrderByFunc(int orderByFunc) {
		this.orderByFunc = OrderByFunction.valueOf(orderByFunc);
	}
	
	private List<ReportGroupByField> groupByFields;
	public List<ReportGroupByField> getGroupByFields() {
		return groupByFields;
	}
	public void setGroupByFields(List<ReportGroupByField> groupByFields) {
		this.groupByFields = groupByFields;
	}

	private long dateFieldId = -1;
	public long getDateFieldId() {
		return dateFieldId;
	}
	public void setDateFieldId(long dateFieldId) {
		this.dateFieldId = dateFieldId;
	}

	private String dateFieldModuleName;
	public String getDateFieldModuleName() {
		return dateFieldModuleName;
	}
	public void setDateFieldModuleName(String dateFieldModuleName) {
		this.dateFieldModuleName = dateFieldModuleName;
	}
	
	private String dateFieldName;
	public String getDateFieldName() {
		return dateFieldName;
	}
	public void setDateFieldName(String dateFieldName) {
		this.dateFieldName = dateFieldName;
	}

	private List<ReportHavingContext> havingCriteria;
	public List<ReportHavingContext> getHavingCriteria() {
		return havingCriteria;
	}
	public void setHavingCriteria(List<ReportHavingContext> havingCriteria) {
		this.havingCriteria = havingCriteria;
	}

	private ReportFieldContext dateField;
	@JsonIgnore
	public ReportFieldContext getDateField() {
		return dateField;
	}

	public void setDateField(ReportFieldContext dateReportField) {
		if (dateReportField != null) {
			dateFieldId = dateReportField.getFieldId();
			dateFieldName = dateReportField.getFieldName();
			dateFieldModuleName = dateReportField.getModuleName();
		}
		this.dateField = dateReportField;
	}
	
	private Map<String, String> aliases; 
	public Map<String, String> getAliases() {
		return aliases;
	}
	public void setAliases(Map<String, String> aliases) {
		this.aliases = aliases;
	}
	
	private DataPointType type;
	public DataPointType getTypeEnum() {
		return type;
	}
	public void setType(DataPointType type) {
		this.type = type;
	}
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = DataPointType.valueOf(type);
	}
	
	private boolean handleEnum;
	
	@JsonIgnore
	public boolean isHandleEnum() {
		return handleEnum;
	}
	public void setHandleEnum(boolean handleEnum) {
		this.handleEnum = handleEnum;
	}
	
	private boolean aggrCalculated;

	@JsonIgnore
	public boolean isAggrCalculated() {
		return aggrCalculated;
	}
	public void setAggrCalculated(boolean aggrCalculated) {
		this.aggrCalculated = aggrCalculated;
	}

	private String expressionString;
	
	public String getExpressionString() {
		return expressionString;
	}
	public void setExpressionString(String expressionString) {
		this.expressionString = expressionString;
	}

	@Setter @Getter
	private boolean fetchMeters;
	@Setter @Getter
	private boolean fetchMetersWithResource;
	private boolean fetchResource;

	public boolean isFetchResource() {
		return fetchResource;
	}
	public void setFetchResource(boolean fetchResource) {
		this.fetchResource = fetchResource;
	}
	
	private boolean defaultSortPoint;
	public boolean isDefaultSortPoint() {
		return defaultSortPoint;
	}
	public void setDefaultSortPoint(boolean defaultSortPoint) {
		this.defaultSortPoint = defaultSortPoint;
	}
	
	public boolean xDataPoint;
	public boolean isxDataPoint() {
		return xDataPoint;
	}
	public void setxDataPoint(boolean xDataPoint) {
		this.xDataPoint = xDataPoint;
	}
	
	public boolean duplicateDataPoint;
	public boolean isduplicateDataPoint() {
		return duplicateDataPoint;
	}
	public void setduplicateDataPoint(boolean duplicateDataPoint) {
		this.duplicateDataPoint = duplicateDataPoint;
	}

	private Map<String, Object> metaData;
	public Map<String, Object> getMetaData() {
		return metaData;
	}
	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
	}
	public void addMeta (String key, Object value) {
		if (metaData == null) {
			metaData = new HashMap<>();
		}
		metaData.put(key, value);
	}
	public Object getMeta (String key) {
		if (metaData == null) {
			return null;
		}
		return metaData.get(key);
	}
	
	private String moduleName;
	public String getModuleName(){
		return this.moduleName;
	}
	public void setModuleName(String moduleName){
		this.moduleName = moduleName;
	}
	
	private Criteria dpCriteria;
	public Criteria getDpCriteria() {
		return this.dpCriteria;
	}
	public void setDpCriteria(Criteria criteria) {
		this.dpCriteria = criteria;
	}

	private int sortOrder;
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	private FacilioField sortField;
	public FacilioField getSortField() {
		return sortField;
	}
	public void setSortField(FacilioField sortField) {
		this.sortField = sortField;
	}

	public static enum OrderByFunction {
		NONE (null),
		ACCENDING ("ASC"),
		DESCENDING ("DESC");
		
		OrderByFunction(String stringValue) {
			this.stringValue = stringValue;
		}
		
		private String stringValue;
		public String getStringValue() {
			return stringValue;
		}
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static OrderByFunction valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	public static enum DataPointType {
		MODULE,
		DERIVATION,
		TRENDLINE,
		FIELD,		//Number Fields of Site, Building, Asset Modules
		FILTER,
		ADDITIONAL
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static DataPointType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder()
					.append("DataPoint [")
					.append("name : ").append(name).append(", ")
					.append("alias : ").append(name).append(", ")
					.append("xField : (").append(xAxis).append("), ")
					.append("yField : (").append(yAxis).append("), ")
					.append("]")
					.toString();
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Setter @Getter
	public Long relationship_id;
	@Setter @Getter
	public String parent_measure_alias;
}
