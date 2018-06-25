package com.facilio.bmsconsole.criteria;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.transaction.FacilioConnectionPool;

public enum BuildingOperator implements Operator<String> {
	
	BUILDING_IS(38, "building_is") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			try {
				if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
					StringBuilder builder = new StringBuilder();
					builder.append(columnName)
							.append(" IN (");
					List<ResourceContext> resources = getAllResources(value);
					
					if(resources != null && !resources.isEmpty()) {
						boolean isFirst = true;
						for(ResourceContext space : resources) {
							if(isFirst) {
								isFirst = false;
							}
							else {
								builder.append(", ");
							}
							builder.append(space.getId());
						}
					}
					else {
						builder.append("-1");
					}
					
					builder.append(")");
					return builder.toString();
				}
			}
			catch(SQLException e) {
				log.info("Exception occurred ", e);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}
			return null;
		}
	};

	private static org.apache.log4j.Logger log = LogManager.getLogger(BuildingOperator.class.getName());

	@Override
	public abstract String getWhereClause(String columnName, String value);
	
	@Override
	public abstract FacilioModulePredicate getPredicate(String fieldName, String value);
	
	@Override
	public boolean isDynamicOperator() {
		return false;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private BuildingOperator(int operatorId, String operator) {
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
	
	private static List<ResourceContext> getAllResources(String value) throws NumberFormatException, Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			List<BaseSpaceContext> spaces = null;
			if(value.contains(",")) {
				List<Long> ids = new ArrayList<>();
				String[] values = value.trim().split("\\s*,\\s*");
				for(String val : values) {
					ids.add(Long.parseLong(val));
				}
				spaces = SpaceAPI.getBaseSpaceWithChildren(ids);
			}
			else {
				spaces = SpaceAPI.getBaseSpaceWithChildren(Long.parseLong(value));
			}
			
			if(spaces != null && !spaces.isEmpty()) {
				List<Long> spaceIds = spaces.stream()
											.map(BaseSpaceContext::getId)
											.collect(Collectors.toList());
				
				return ResourceAPI.getAllResourcesFromSpaces(spaceIds);
											
			}
			return null;
		}
		catch(SQLException e) {
			log.info("Exception occurred ", e);
			throw e;
		}
	}
}
