package com.facilio.db.criteria.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Criteria;
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
	};

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
		if(value.contains(FacilioConstants.Criteria.LOGGED_IN_USER)) {
			List<Object> objs = new ArrayList<>();
			objs.add(AccountUtil.getCurrentUser().getId());
			return objs;
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

	private static String containValues(String fieldName, String value, boolean contains) {
		try {
			if(fieldName != null && !fieldName.isEmpty() && value != null) {
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
						parentName = multiField.parentColumnName();
						childName = multiField.childColumnName();
					}
					else if (field instanceof MultiEnumField) {
						MultiEnumField multiField = (MultiEnumField)field;
						relModule = multiField.getRelModule();
						parentName = multiField.parentColumnName();
						childName = multiField.valueColumnName();
					}

					if(module != null) {
						StringBuilder builder = new StringBuilder();
						if (!contains) {
							builder.append("NOT ");
						}
						builder.append("ID IN (SELECT ")
						.append(parentName).append(" FROM ")
						.append(relModule.getTableName())
						.append(" WHERE ")
						.append(childName)
						;

						if(value.contains(",")) {
							builder.append(" IN (");
							replaceLoggedUserInMultpleValues(builder, value);
							builder.append(")");
						}
						else {
							if(value.trim().equals(FacilioConstants.Criteria.LOGGED_IN_USER)) {
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
	
	private static Predicate getContainsPredicate(String value) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				if(object != null && object instanceof List) {
					List<Long> values;
					if (((List) object).get(0) instanceof Map) {
						values = ((List<Map>) object).stream().map(val ->(long) val.get("id")).collect(Collectors.toList());
						if(value.equals(Criteria.LOGGED_IN_USER)) {
							return values.contains(FacilioUtil.parseLong(AccountUtil.getCurrentUser().getId()));
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
		if(value.contains(Criteria.LOGGED_IN_USER)) {
			String[] values = value.trim().split("\\s*,\\s*");
			for(int i=0; i<values.length; i++) {
				String val = values[i];
				if(val.equals(Criteria.LOGGED_IN_USER)) {
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
}
