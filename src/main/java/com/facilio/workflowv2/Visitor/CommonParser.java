package com.facilio.workflowv2.Visitor;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.date.calenderandclock.CalenderAndClockInterface;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflowv2.autogens.WorkflowV2BaseVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Parser;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_aggrContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_fieldContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_field_criteriaContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_groupContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_group_byContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_limitContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_rangeContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_sortContext;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.google.api.client.util.DateTime;

public abstract class CommonParser<T> extends WorkflowV2BaseVisitor<Value> {
	
	public DBParamContext dbParamContext = null;
	
	public Criteria criteria = null;
	
	public Criteria fieldCriteria = null;
	
	long currentExecutionTime = -1;
	
	public long getCurrentExecutionTime() {
		if(currentExecutionTime < 0) {
			return DateTimeUtil.getCurrenTime();
		}
		return currentExecutionTime;
	}

	public void setCurrentExecutionTime(long currentExecutionTime) {
		this.currentExecutionTime = currentExecutionTime;
	}

	@Override 
    public Value visitCondition_atom(WorkflowV2Parser.Condition_atomContext ctx) {
    	
		Criteria currentCriteria = criteria != null ? criteria : fieldCriteria;
		
    	Condition condition = new Condition();
    	condition.setFieldName(ctx.VAR().getText());
    	
    	Operator operator = null;
    	
    	Value operatorValue = this.visit(ctx.expr());
    	switch(ctx.op.getText()) {
    	case "==" :
    		if(operatorValue.asObject() == null) {
    			operator = CommonOperators.IS_EMPTY;
    		}
    		else if(operatorValue.asObject() instanceof String) {
    			operator = StringOperators.IS;
    		}
    		else if(operatorValue.asObject() instanceof Boolean) {
    			operator = BooleanOperators.IS;
    		}
    		else if(operatorValue.asObject() instanceof DateRange) {
    			operator = DateOperators.BETWEEN;
    		}
    		else if(operatorValue.asObject() instanceof CalenderAndClockInterface) {
    			operator = DateOperators.CALENDER_AND_CLOCK;
    		}
    		else if (operatorValue.asObject() instanceof List) {
    			operator = StringOperators.IS;
    		}
    		else if (operatorValue.asObject() instanceof FacilioField) {
    			operator = FieldOperator.EQUAL;
    		}
    		else if (operatorValue.asObject() instanceof BaseSpaceContext) {
    			operator = BuildingOperator.BUILDING_IS;
    		}
    		else {
    			operator = NumberOperators.EQUALS;
    		}
    		break;
    	case "!=" :
    		if(operatorValue.asObject() == null) {
    			operator = CommonOperators.IS_NOT_EMPTY;
    		}
    		else if(operatorValue.asObject() instanceof String) {
    			operator = StringOperators.ISN_T;
    		}
    		else if (operatorValue.asObject() instanceof List) {
    			operator = StringOperators.ISN_T;
    		}
    		else if (operatorValue.asObject() instanceof FacilioField) {
    			operator = FieldOperator.NOT_EQUAL;
    		}
    		else {
    			operator = NumberOperators.NOT_EQUALS;
    		}
    		break;
    	default:
    		if (operatorValue.asObject() instanceof FacilioField) {
    			operator = FieldOperator.getAllOperators().get(ctx.op.getText());
    		}
    		else {
    			operator = NumberOperators.getAllOperators().get(ctx.op.getText());
    		}
    		break;
    	}
    	condition.setOperator(operator);
    	
    	String value = operatorValue.asString();
    	
    	if (operatorValue.asObject() instanceof List) {
    		value = StringUtils.join(operatorValue.asList(), ",");
		}
    	else if (operatorValue.asObject() instanceof FacilioField) {
    		FacilioField field = operatorValue.asField();
    		value = field.getModule().getName()+"."+field.getName();
		}
    	else if (operatorValue.asObject() instanceof BaseSpaceContext) {
    		BaseSpaceContext baseSpace = (BaseSpaceContext) operatorValue.asObject() ;
    		value = ""+baseSpace.getId();
		}
    	else if (operatorValue.asObject() instanceof CalenderAndClockInterface) {
    		CalenderAndClockInterface calenderAndClock = (CalenderAndClockInterface) operatorValue.asObject();
    		value = calenderAndClock.getFullName()+"."+getCurrentExecutionTime();
		}
    	
    	condition.setValue(value);
    	
    	int seq = currentCriteria.addConditionMap(condition);
    	
    	currentCriteria.setPattern(currentCriteria.getPattern().replaceFirst(Pattern.quote(ctx.getText()), String.valueOf(seq)));
    	
    	return visitChildren(ctx); 
    }
    
    @Override 
    public Value visitCriteria(WorkflowV2Parser.CriteriaContext ctx) {
    	
    	criteria = new Criteria(); 
    	
    	String criteriaSting = ctx.getText();
    	
    	criteria.setPattern(criteriaSting);
    	
    	this.visit(ctx.condition());
    	
    	criteria.setPattern(WorkflowV2Util.adjustCriteriaPattern(criteria.getPattern()));
    	
    	Value criteriaVal = new Value(criteria);
    	
    	criteria = null;
    	return criteriaVal;
    }
    
    @Override
	public Value visitDb_param_field_criteria(Db_param_field_criteriaContext ctx) {
		
    	fieldCriteria = new Criteria(); 
    	
    	String criteriaSting = ctx.getText();
    	
    	fieldCriteria.setPattern(criteriaSting);
    	
    	this.visit(ctx.criteria().condition());
    	
    	
    	fieldCriteria.setPattern(WorkflowV2Util.adjustCriteriaPattern(fieldCriteria.getPattern()));
    	
    	Value criteriaVal = new Value(fieldCriteria);
    	
    	dbParamContext.setFieldCriteria(criteriaVal.asCriteria());
    	fieldCriteria = null;
    	
    	return criteriaVal;
	}
	
	@Override
    public Value visitDb_param_field(Db_param_fieldContext ctx) {
    	if(dbParamContext.getFieldName() == null) {
    		Value fieldValue = this.visit(ctx.expr());
    		WorkflowV2Util.checkForNullAndThrowException(fieldValue, ctx.expr().getText());
    		dbParamContext.setFieldName(fieldValue.asString());
    	}
		return Value.VOID;
    }
	
    @Override
    public Value visitDb_param_aggr(Db_param_aggrContext ctx) {
    	
    	if(dbParamContext.getAggregateString() == null) {
    		Value aggrValue = this.visit(ctx.expr());
    		WorkflowV2Util.checkForNullAndThrowException(aggrValue, ctx.expr().getText());
    		String aggRes = aggrValue.asString();
    		
    		if(aggRes.contains(",")) {
    			String[] aggrValues = aggRes.split(",");
    			String aggrString = aggrValues[0].trim();
    			String aggrFieldName = aggrValues[1].trim();
    			
    			dbParamContext.setAggregateString(aggrString);
    			dbParamContext.setAggregateFieldName(aggrFieldName);
    		}
    		else {
    			dbParamContext.setAggregateString(aggRes);
    		}
    	}
    	
    	return Value.VOID;
    }
    
    @Override
    public Value visitDb_param_limit(Db_param_limitContext ctx) {
    	
    	if(dbParamContext.getLimit() <= 0) {
    		
    		Value limitValue = this.visit(ctx.expr());
    		WorkflowV2Util.checkForNullAndThrowException(limitValue, ctx.expr().getText());
    		dbParamContext.setLimit(limitValue.asInt());
    	}
    	
		return Value.VOID;
    }
    
    @Override
    public Value visitDb_param_range(Db_param_rangeContext ctx) {
    	if(dbParamContext.getRange() == null) {
    		
    		Value fromValue = this.visit(ctx.expr(0));
    		WorkflowV2Util.checkForNullAndThrowException(fromValue, ctx.expr(0).getText());
    		Value toValue = this.visit(ctx.expr(1));
    		WorkflowV2Util.checkForNullAndThrowException(toValue, ctx.expr(1).getText());
    		dbParamContext.setRange(Pair.of(fromValue.asInt(), toValue.asInt()));
    	}
    	
    	return Value.VOID;
    }
    
    @Override
    public Value visitDb_param_group_by(Db_param_group_byContext ctx) {
    	
    	if(dbParamContext.getGroupBy() == null) {
    		
    		Value fieldValue = this.visit(ctx.expr());
    		WorkflowV2Util.checkForNullAndThrowException(fieldValue, ctx.expr().getText());
    		dbParamContext.setGroupBy(fieldValue.asString());
    	}
    	
    	return Value.VOID;
    }
    
    @Override
    public Value visitDb_param_sort(Db_param_sortContext ctx) {
    	
    	if(dbParamContext.getSortOrder() == null) {
    		
    		Value sortByField = this.visit(ctx.expr());
    		WorkflowV2Util.checkForNullAndThrowException(sortByField, ctx.expr().getText());
    		dbParamContext.setSortByFieldName(sortByField.asString());
    		dbParamContext.setSortOrder(ctx.op.getText());
    	}
    	
    	return Value.VOID;
    }
    
	@Override
	public Value visitDb_param(WorkflowV2Parser.Db_paramContext ctx) {
		
		dbParamContext = new DBParamContext();
		
		Value criteriaValue = this.visit(ctx.db_param_criteria().expr());
		
		if(!(criteriaValue.asObject() instanceof Criteria)) {
			throw new RuntimeException(ctx.db_param_criteria().expr().getText()+" IS NOT A CRITERIA");
		}
		
		dbParamContext.setCriteria(criteriaValue.asCriteria());
		
		List<Db_param_groupContext> groups = ctx.db_param_group();
		for(Db_param_groupContext group :groups) {
			visit(group);
		}

		Value dbParamContextVal = new Value(dbParamContext);
    	
		dbParamContext = null;
    	return dbParamContextVal;
	}

}
