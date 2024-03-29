package com.facilio.db.criteria.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.util.FacilioUtil;

public enum MultiFieldOperators implements Operator<String> {

	CONTAINS (90, "contains") {
		
		@Override
		public boolean isDefaultSelection() {
			return true;
		}
		
		@Override
		public String getWhereClause(String fieldName, String value) {
			return containValues(fieldName, value, true);
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				String[] module = fieldName.split("\\.");
				if(module.length > 1) {
					fieldName = module[1];
				}
				return new FacilioModulePredicate(fieldName, computePredicate(value));
			}
			return null;
		}
	},
	NOT_CONTAINS (91, "doesn't contain") {
		@Override
		public String getWhereClause(String fieldName, String value) {
			return containValues(fieldName, value, false);
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				String[] module = fieldName.split("\\.");
				if(module.length > 1) {
					fieldName = module[1];
				}
				return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(computePredicate(value)));
			}
			return null;
		}
	},
	IS_EMPTY (104, "is empty") {
		@Override
		public String getWhereClause(String fieldName, String value) {
			return isEmptyQuery(fieldName, true);
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			
			if(fieldName != null && !fieldName.isEmpty()) {
				if(fieldName.contains(".")) {
					String[] module = fieldName.split("\\.");
					if(module.length > 1) {
						fieldName = module[1];
					}
				}
				return new FacilioModulePredicate(fieldName, getIsEmptyPridicate());
			}
			return null;
		}

		@Override
		public boolean isValueNeeded () {
			return false;
		}
	},
	IS_NOT_EMPTY (105, "is not empty") {
		@Override
		public String getWhereClause(String fieldName, String value) {
			return isEmptyQuery(fieldName, false);
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			
			if(fieldName != null && !fieldName.isEmpty()) {
				if(fieldName.contains(".")) {
					String[] module = fieldName.split("\\.");
					if(module.length > 1) {
						fieldName = module[1];
					}
				}
				return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(getIsEmptyPridicate()));
			}
			return null;
		}

		@Override
		public boolean isValueNeeded () {
			return false;
		}
	},
	;

	private static final Logger LOGGER = LogManager.getLogger(MultiFieldOperators.class.getName());

	private MultiFieldOperators(int operatorId, String operator) {
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

	@Override
	public boolean isDynamicOperator() {
		return false;
	}

	@Override
	public boolean isValueNeeded() {
		return true;
	}

	@Override
	public FacilioModulePredicate getPredicate(String fieldName, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> computeValues(String value) {
		if (value != null && isPlaceholder(value)) {
			Object val = getResolvedVal(value);
			if (val != null) {
				return Collections.singletonList(val); 
			}
		}
		return null;
	}

	@Override
	public boolean updateFieldNameWithModule() {
		return true;
	}
	
	@Override
	public boolean useFieldName() {
		return true;
	}
	
	private static String isEmptyQuery(String fieldName, boolean isEmpty) {
		try {
			if(fieldName != null && !fieldName.isEmpty()) {
				String[] module = fieldName.split("\\.");
				if(module.length > 1) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField field = modBean.getField(module[1], module[0]);
					
					FacilioModule relModule = null;
					String parentName = null;
					if (field instanceof MultiLookupField) {
						MultiLookupField multiField = (MultiLookupField)field;
						relModule = multiField.getRelModule();
						parentName = modBean.getField(multiField.parentFieldName(), relModule.getName()).getColumnName();
					}
					else if (field instanceof MultiEnumField) {
						MultiEnumField multiField = (MultiEnumField)field;
						relModule = multiField.getRelModule();
						parentName = modBean.getField(multiField.PARENT_FIELD_NAME, relModule.getName()).getColumnName();
					}

					if(module != null) {
						StringBuilder builder = new StringBuilder();

						builder.append(field.getTableName()).append(".ID");
						if (isEmpty) {
							builder.append(" NOT IN");
						}
						else {
							builder.append(" IN");
						}
						builder.append(" (SELECT ")
						.append(parentName).append(" FROM ")
						.append(relModule.getTableName())
						.append(" WHERE")
						.append(" ORGID = ").append(AccountUtil.getCurrentOrg().getOrgId()).append(" AND")
						.append(" MODULEID = ").append(relModule.getModuleId())
						.append(" GROUP BY ").append(parentName)
						;

						builder.append(")");
						
						return builder.toString();
					}
				}
				
			}
		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
		}
		return null;
	}

	private static String containValues(String fieldName, String value, boolean contains) {
		try {
			if(fieldName != null && !fieldName.isEmpty() && StringUtils.isNotEmpty(value)) {
				String[] module = fieldName.split("\\.");
				if(module.length > 1) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField field = modBean.getField(module[1], module[0]);
					
					FacilioModule relModule = null;
					String parentName = null;
					String childName = null;
					if (field instanceof MultiLookupField) {
						MultiLookupField multiField = (MultiLookupField)field;
						relModule = multiField.getRelModule();
						parentName = modBean.getField(multiField.parentFieldName(), relModule.getName()).getColumnName();
						childName = modBean.getField(multiField.childFieldName(), relModule.getName()).getColumnName();
					}
					else if (field instanceof MultiEnumField) {
						MultiEnumField multiField = (MultiEnumField)field;
						relModule = multiField.getRelModule();
						parentName = modBean.getField(multiField.PARENT_FIELD_NAME, relModule.getName()).getColumnName();
						childName = modBean.getField(multiField.VALUE_FIELD_NAME, relModule.getName()).getColumnName();
					}

					if(module != null) {
						StringBuilder builder = new StringBuilder();
						if (!contains) {
							builder.append("NOT ");
						}
						builder.append(field.getTableName()).append(".ID IN (SELECT ")
						.append(parentName).append(" FROM ")
						.append(relModule.getTableName())
						.append(" WHERE")
						.append(" ORGID = ").append(AccountUtil.getCurrentOrg().getOrgId()).append(" AND")
						.append(" MODULEID = ").append(relModule.getModuleId()).append(" AND ")
						.append(childName)
						;

						if(value.contains(",")) {
							builder.append(" IN (");
							replaceLoggedUserInMultpleValues(builder, value);
							builder.append(")");
						}
						else {
							if(isPlaceholder(value.trim())) {
								value = "?";
							}
							builder.append(" = ")
							.append(value);
						}
						builder.append(")");

						return builder.toString();
					}
				}
				
			}
		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
		}
		return null;
	}
	
	private static Predicate computePredicate(String value) {
		if(value.contains(",")) {
			List<Predicate> containsPredicates = new ArrayList<>();
			String[] values = value.trim().split(FacilioUtil.COMMA_SPLIT_REGEX);
			for(String val : values) {
				containsPredicates.add(getContainsPredicate(val));
			}
			return PredicateUtils.anyPredicate(containsPredicates);
		}
		else {
			return getContainsPredicate(value);
		}
	}
	
	private static Predicate getIsEmptyPridicate() {
		
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				try {
					if(object == null) {
						return true;
					}
					else if(object instanceof String) {
						if(object.equals("[]")) {
							return true;
						}
						if(((String) object).contains(",")) {
							return false;
						}
						return ((String) object).isEmpty();
					}
					else if(object instanceof List) {
						return ((List)object).isEmpty();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		};
		
	}
	
	private static Predicate getContainsPredicate(String value) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				if(object != null && object instanceof List) {
					List<Long> values;
					if (((List) object).get(0) instanceof Map) {
						values = ((List<Map>) object).stream().map(val ->(long) val.get("id")).collect(Collectors.toList());
						if(isPlaceholder(value)) {
							return values.contains(FacilioUtil.parseLong(getResolvedVal(value)));
						}
					}
					else {
						values = (List) object;
					}
					return values.contains(FacilioUtil.parseLong(value));
				}
				return false;
			}
		};
	}
	
	private static void replaceLoggedUserInMultpleValues(StringBuilder builder, String value) {
		if(value.contains("${")) {
			String[] values = value.trim().split("\\s*,\\s*");
			for(int i=0; i<values.length; i++) {
				String val = values[i];
				if(isPlaceholder(val)) {
					val = "?";
				}
				if(i != 0) {
					builder.append(", ");
				}
				builder.append(val);
			}
		}
		else {
			builder.append(value);
		}
	}
	
	private static boolean isPlaceholder(String val) {
		return val.startsWith("${");
	}
	
	private static Object getResolvedVal(String value) {
		try {
			return FormsAPI.resolveDefaultValPlaceholder(value);
		} catch (Exception e) {
			LOGGER.error("Exception while computing values", e);
		}
		return null;
	}
	
}
