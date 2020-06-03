package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.AccountsInterface;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;

public class RollUpField{

	private static final long serialVersionUID = 1L;
    
	private long id = -1;
	private long orgId;
    private String description;
	private long childFieldId;
    private long childModuleId;
    private long childCriteriaId;
    private int aggregateFunctionId;
    private long aggregateFieldId = -1;
    private long parentModuleId;
    private long parentRollUpFieldId;
    private Boolean isSystemRollUpField;
    
	private FacilioField childField;
    private FacilioModule childModule;
    private Criteria childCriteria;
    private AggregateOperator aggregateFunctionOperator;
	private FacilioField aggregateField;
    private FacilioModule parentModule;
    private FacilioField parentRollUpField;
    
	public RollUpField() {
		super();
	}
	public RollUpField(RollUpField rollUpField) {
		super();
		this.childFieldId = rollUpField.childFieldId;
		this.childModuleId = rollUpField.childModuleId;
		this.childCriteriaId = rollUpField.childCriteriaId;
		this.aggregateFunctionId = rollUpField.aggregateFunctionId;
		this.aggregateFieldId = rollUpField.aggregateFieldId;
		this.parentModuleId = rollUpField.parentModuleId;
		this.parentRollUpFieldId = rollUpField.parentRollUpFieldId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getChildFieldId() {
		return childFieldId;
	}

	public void setChildFieldId(long childFieldId) {
		this.childFieldId = childFieldId;
	}

	public long getChildModuleId() {
		return childModuleId;
	}

	public void setChildModuleId(long childModuleId) {
		this.childModuleId = childModuleId;
	}

	public long getChildCriteriaId() {
		return childCriteriaId;
	}

	public void setChildCriteriaId(long childCriteriaId) {
		this.childCriteriaId = childCriteriaId;
	}

	public int getAggregateFunctionId() {
		return aggregateFunctionId;
	}

	public void setAggregateFunctionId(int aggregateFunctionId) {
		this.aggregateFunctionId = aggregateFunctionId;
	}

	public AggregateOperator getAggregateFunctionOperator() {
		return aggregateFunctionOperator;
	}

	public void setAggregateFunctionOperator(AggregateOperator aggregateFunctionOperator) {
		this.aggregateFunctionOperator = aggregateFunctionOperator;
	}

	public long getAggregateFieldId() {
		return aggregateFieldId;
	}

	public void setAggregateFieldId(long aggregateFieldId) {
		this.aggregateFieldId = aggregateFieldId;
	}

	public long getParentModuleId() {
		return parentModuleId;
	}

	public void setParentModuleId(long parentModuleId) {
		this.parentModuleId = parentModuleId;
	}

	public long getParentRollUpFieldId() {
		return parentRollUpFieldId;
	}

	public void setParentRollUpFieldId(long parentRollUpFieldId) {
		this.parentRollUpFieldId = parentRollUpFieldId;
	}

	public FacilioModule getChildModule() {
		return childModule;
	}

	public void setChildModule(FacilioModule childModule) {
		this.childModule = childModule;
	}

	public Criteria getChildCriteria() {
		return childCriteria;
	}

	public void setChildCriteria(Criteria childCriteria) {
		this.childCriteria = childCriteria;
	}

	public FacilioField getAggregateField() {
		return aggregateField;
	}

	public void setAggregateField(FacilioField aggregateField) {
		this.aggregateField = aggregateField;
	}

	public FacilioModule getParentModule() {
		return parentModule;
	}

	public void setParentModule(FacilioModule parentModule) {
		this.parentModule = parentModule;
	}

	public FacilioField getParentRollUpField() {
		return parentRollUpField;
	}

	public void setParentRollUpField(FacilioField parentRollUpField) {
		this.parentRollUpField = parentRollUpField;
	}

	public FacilioField getChildField() {
		return childField;
	}

	public void setChildField(FacilioField childField) {
		this.childField = childField;
	}
	
	public Boolean getIsSystemRollUpField() {
		return isSystemRollUpField;
	}
	
	public void setIsSystemRollUpField(Boolean isSystemRollUpField) {
		this.isSystemRollUpField = isSystemRollUpField;
	}
	
	public boolean isSystemRollUpField() {
		if (isSystemRollUpField != null) {
			return isSystemRollUpField.booleanValue();
		}
		return false;
	}
		
	@Override
	public String toString() {
		
		StringBuilder builder =  new StringBuilder()
				.append(" RollUpField ID : ").append(id)
				.append(", ChildFieldId : ").append(childFieldId)
				.append(", ChildModuleId : ").append(childModuleId)
				.append(", ChildCriteriaId : ").append(childCriteriaId)
				.append(", aggregateFunctionId : "+aggregateFunctionId)
				.append(", AggregateFieldId : ").append(aggregateFieldId)
				.append(", ParentRollUpFieldId : ").append(parentRollUpFieldId)
				.append(", ParentModuleId : ").append(parentModuleId);
			
		return builder.toString();
	}
}
