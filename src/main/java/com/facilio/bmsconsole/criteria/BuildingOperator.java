package com.facilio.bmsconsole.criteria;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanPredicate;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.transaction.FacilioConnectionPool;

public enum BuildingOperator implements Operator<String> {
	
	BUILDING_IS("building_is") {
		@Override
		public BeanPredicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			try {
				if(field.getColumnName() != null && value != null && !value.isEmpty()) {
					StringBuilder builder = new StringBuilder();
					builder.append(field.getExtendedModule().getTableName())
							.append(".")
							.append(field.getColumnName())
							.append(" IN (");
					List<BaseSpaceContext> allSpaces = getAllBuildingIds(value);
					
					if(allSpaces != null && !allSpaces.isEmpty()) {
						boolean isFirst = true;
						for(BaseSpaceContext space : allSpaces) {
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
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	};
	
	@Override
	public abstract String getWhereClause(FacilioField field, String value);
	
	@Override
	public abstract BeanPredicate getPredicate(FacilioField field, String value);
	
	@Override
	public String getDynamicParameter() {
		return null;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private BuildingOperator(String operator) {
		 this.operator = operator;
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
	
	private static List<BaseSpaceContext> getAllBuildingIds(String value) throws NumberFormatException, Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			if(value.contains(",")) {
				List<Long> ids = new ArrayList<>();
				String[] values = value.trim().split("\\s*,\\s*");
				for(String val : values) {
					ids.add(Long.parseLong(val));
				}
				return SpaceAPI.getBaseSpaceWithChildren(ids);
			}
			else {
				return SpaceAPI.getBaseSpaceWithChildren(Long.parseLong(value));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
