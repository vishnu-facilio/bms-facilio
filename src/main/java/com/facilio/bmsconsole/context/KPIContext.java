package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter @Setter
public class KPIContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long kpiCategoryId = -1l;
	public long getKpiCategoryId() {
		return kpiCategoryId;
	}
	public void setKpiCategoryId(long kpiCategoryId) {
		this.kpiCategoryId = kpiCategoryId;
	}
	
	private KPICategoryContext kpiCategory;
	public KPICategoryContext getKpiCategory() {
		return kpiCategory;
	}
	public void setKpiCategory(KPICategoryContext kpiCategory) {
		this.kpiCategory = kpiCategory;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private WorkflowContext workflow;
	@JsonIgnore
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	
	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria;
	@JsonIgnore
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private FacilioModule module;
	@JSON(serialize = false)
	@JsonIgnore
	public FacilioModule getModule() {
		return module;
	}
	@JsonIgnore
	public void setModule(FacilioModule module) {
		this.module = module;
		setModuleName(module.getName());
	}
	
	private long metricId = -1;
	public long getMetricId() {
		return metricId;
	}
	public void setMetricId(long metricId) {
		this.metricId = metricId;
	}
	
	private String metricName;
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	
	private FacilioField metric;
	@JsonIgnore
	public FacilioField getMetric() {
		return metric;
	}
	@JsonIgnore
	public void setMetric(FacilioField metric) {
		this.metric = metric;
	}

	private Boolean active;
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isActive() {
		if (active != null) {
			return active.booleanValue();
		}
		return false;
	}
	
	private double minTarget = -1;
	public double getMinTarget() {
		return minTarget;
	}
	public void setMinTarget(double minTarget) {
		this.minTarget = minTarget;
	}

	private double target = -1;
	public double getTarget() {
		return target;
	}
	public void setTarget(double target) {
		this.target = target;
	}

	private Object currentValue = null;
	public Object getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(Object currentValue) {
		this.currentValue = currentValue;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long modifiedTime = -1;
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	private long dateFieldId = -1;
	public long getDateFieldId() {
		return dateFieldId;
	}
	public void setDateFieldId(long dateFieldId) {
		this.dateFieldId = dateFieldId;
	}
	
	private String dateFieldName;
	public String getDateFieldName() {
		return dateFieldName;
	}
	public void setDateFieldName(String dateFieldName) {
		this.dateFieldName = dateFieldName;
	}

	private FacilioField dateField;
	@JSON(serialize = false)
	@JsonIgnore
	public FacilioField getDateField() {
		return dateField;
	}
	@JsonIgnore
	public void setDateField(FacilioField dateField) {
		this.dateField = dateField;
		setDateFieldName(dateField.getName());
	}
	
	private DateOperators dateOperator;
	public int getDateOperator() {
		if (dateOperator != null) {
			return dateOperator.getOperatorId();
		}
		return -1;
	}
	public DateOperators getDateOperatorEnum() {
		return dateOperator;
	}
	public void setDateOperator(DateOperators dateOperator) {
		this.dateOperator = dateOperator;
	}
	public void setDateOperator(int dateOperatorId) {
		if (dateOperatorId > 0) {
			dateOperator = (DateOperators) Operator.getOperator(dateOperatorId);
		}
	}
	
	private String dateValue;
	public String getDateValue() {
		return dateValue;
	}
	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}

	private AggregateOperator aggr;
	public int getAggr() {
		if (aggr != null) {
			return aggr.getValue();
		}
		return -1;
	}
	public void setAggr(int aggr) {
		this.aggr = AggregateOperator.getAggregateOperator(aggr);
	}
	public AggregateOperator getAggrEnum() {
		return aggr;
	}
	public void setAggr(AggregateOperator aggr) {
		this.aggr = aggr;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder("KPI::")
									.append(id)
									.append("::")
									.append(name)
									;
		return builder.toString();
	}

	private Map<String,String> metricFieldObj;
	private Map<String,String> dateFieldObj;
	private String workFlowString;
	private String linkName;

}
