package com.facilio.db.criteria.operators;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

import lombok.extern.log4j.Log4j;

@Log4j
public enum PMPlannerOperator implements Operator<String> {

	PLANNER_RESOURCE_IS(114, "planner_resource_id") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String plannerId) {
			if(fieldName != null && !fieldName.isEmpty() && plannerId != null && !plannerId.isEmpty()) {
				try {
					List<Long> resourceList = getResourceList(Long.parseLong(plannerId));
					if(fieldName != null && !fieldName.isEmpty() && resourceList != null && !resourceList.isEmpty()) {
						return new FacilioModulePredicate(fieldName, computeEqualPredicate(StringUtils.join(resourceList, ",")));
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
			return null;
		}

		@Override
		public String getWhereClause(String columnName, String plannerId) {
			// TODO Auto-generated method stub
			try {
				if(columnName != null && !columnName.isEmpty() && plannerId != null && !plannerId.isEmpty()) {
					StringBuilder builder = new StringBuilder();
					builder.append(columnName)
							.append(" IN (");
					
					List<Long> resourceList = getResourceList(Long.parseLong(plannerId));
					
					if (CollectionUtils.isNotEmpty(resourceList)) {
						
						builder.append(StringUtils.join(resourceList, ","));
					}
					else {
						builder.append("-1");
					}
					builder.append(")");
					return builder.toString();
				}
			}
			catch (Exception e) {
				LOGGER.info("Exception occurred ", e);
			}
			return null;
		}
	},
	PLANNER_ASSIGNED_IS(115, "planner_assigned_id") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String plannerId) {
			if(fieldName != null && !fieldName.isEmpty() && plannerId != null && !plannerId.isEmpty()) {
				try {
					List<Long> assignedtoIds = getAssignedToUserList(Long.parseLong(plannerId));
					if(fieldName != null && !fieldName.isEmpty() && assignedtoIds != null && !assignedtoIds.isEmpty()) {
						return new FacilioModulePredicate(fieldName, computeEqualPredicate(StringUtils.join(assignedtoIds, ",")));
					}
				} catch (Exception e) {
					LOGGER.error("Exception occurred ", e);
				}
			}
			return null;
		}

		@Override
		public String getWhereClause(String columnName, String plannerId) {
			// TODO Auto-generated method stub
			try {
				if(columnName != null && !columnName.isEmpty() && plannerId != null && !plannerId.isEmpty()) {
					StringBuilder builder = new StringBuilder();
					builder.append(columnName)
							.append(" IN (");
					
					List<Long> assignedtoIds = getAssignedToUserList(Long.parseLong(plannerId));
					
					if (CollectionUtils.isNotEmpty(assignedtoIds)) {
						
						builder.append(StringUtils.join(assignedtoIds, ","));
					}
					else {
						builder.append("-1");
					}
					builder.append(")");
					return builder.toString();
				}
			}
			catch (Exception e) {
				LOGGER.info("Exception occurred ", e);
			}
			return null;
		}
	}
	;
	
	private static List<Long> getResourceList(Long plannerId) throws Exception {
		
		List<PMResourcePlanner> resourcePlanners = PlannedMaintenanceAPI.getResourcePlanners(plannerId);
		
		if(resourcePlanners != null && !resourcePlanners.isEmpty()) {
			List<Long> resourceIds = resourcePlanners.stream().filter((resPlanner)-> resPlanner.getResource() != null).map(PMResourcePlanner::getResourceId).distinct().collect(Collectors.toList());
			
			return resourceIds;
		}
		return null;
	}
	
	private static List<Long> getAssignedToUserList(Long plannerId) throws Exception {
		
		List<PMResourcePlanner> resourcePlanners = PlannedMaintenanceAPI.getResourcePlanners(plannerId);
		
		if(resourcePlanners != null && !resourcePlanners.isEmpty()) {
			List<Long> assignedToIds = resourcePlanners.stream().filter((resPlanner)-> resPlanner.getAssignedTo() != null).map(PMResourcePlanner::getAssignedToId).distinct().collect(Collectors.toList());
			
			return assignedToIds;
		}
		return null;
	}
	
	private static Predicate computeEqualPredicate(String value) {
		if(value.contains(",")) {
			List<Predicate> equalPredicates = new ArrayList<>();
			String[] values = value.trim().split(FacilioUtil.COMMA_SPLIT_REGEX);
			for(String val : values) {
				equalPredicates.add(getEqualPredicate(val));
			}
			return PredicateUtils.anyPredicate(equalPredicates);
		}
		else {
			return getEqualPredicate(value);
		}
	}
	
	private static Predicate getEqualPredicate(String value) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				// TODO Auto-generated method stub
				if(object != null) {
					double doubleVal = Double.parseDouble(value);
					double currentVal = Double.parseDouble(object.toString());
					return currentVal == doubleVal;
				}
				return false;
			}
		};
	}


	@Override
	public boolean isDynamicOperator() {
		return true;
	}
	
	@Override
	public boolean isValueNeeded() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private PMPlannerOperator(int operatorId, String operator) {
		this.operatorId = operatorId;
		this.operator = operator;
	}
	
	private int operatorId;
	@Override
	public int getOperatorId() {
		return operatorId;
	}
	
	private String operator;
	@Override
	public String getOperator() {
		return operator;
	}
	
	private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
	private static Map<String, Operator> initOperatorMap() {
		Map<String, Operator> operatorMap = new HashMap<>();
		for(Operator operator : values()) {
			operatorMap.put(operator.getOperator(), operator);
		}
		return operatorMap;
	}
	public static Map<String, Operator> getAllOperators() {
		return operatorMap;
	}
}
