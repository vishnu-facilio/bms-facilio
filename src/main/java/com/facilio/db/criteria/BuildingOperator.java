package com.facilio.db.criteria;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public enum BuildingOperator implements Operator<String> {
	
	BUILDING_IS(38, "building_is") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				try {
					List<ResourceContext> resources = getAllResources(value);
					if (resources != null && !resources.isEmpty()) {
						List<Long> ids = resources.stream().map(resource -> resource.getId()).collect(Collectors.toList());
						return new FacilioModulePredicate(fieldName, computeBuildingIsPredicate(StringUtils.join(ids, ",")));
					}
				} catch (Exception e) {
					log.info("Exception occurred ", e);
				}
			}
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
	
	private static Predicate computeBuildingIsPredicate(String value) {
		if(value.contains(",")) {
			List<Predicate> buildingIsPredicates = new ArrayList<>();
			String[] values = value.trim().split("\\s*,\\s*");
			for(String val : values) {
				buildingIsPredicates.add(getBuildingIsPredicate(val));
			}
			return PredicateUtils.anyPredicate(buildingIsPredicates);
		}
		else {
			return getBuildingIsPredicate(value);
		}
	}
	
	private static Predicate getBuildingIsPredicate(String value) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				// TODO Auto-generated method stub
				if(object != null) {
					try {
						long currentId;
						if(object instanceof Long) {
							currentId = (long) object;
						}
						else if(PropertyUtils.isReadable(object, "id")) {
							currentId = (long) PropertyUtils.getProperty(object, "id");
						}
						else {
							return false;
						}
						long longVal = Long.parseLong(value);
						return currentId == longVal;
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						log.info("Exception occurred ", e);
					}
				}
				return false;
			}
		};
	}

	private static org.apache.log4j.Logger log = LogManager.getLogger(BuildingOperator.class.getName());

	@Override
	public abstract String getWhereClause(String columnName, String value);
	
	@Override
	public abstract FacilioModulePredicate getPredicate(String fieldName, String value);
	
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
}
