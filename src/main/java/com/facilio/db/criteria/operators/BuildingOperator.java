package com.facilio.db.criteria.operators;

import java.sql.SQLException;
import java.util.*;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.db.criteria.FacilioModulePredicate;

public enum BuildingOperator implements Operator<String> {
	
	BUILDING_IS(38, "building_is") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				try {
					List<BaseSpaceContext> spaces = SpaceAPI.getBaseSpaces(value);
					if (CollectionUtils.isNotEmpty(spaces)) {
						return new FacilioModulePredicate(fieldName, new BuildingIsPredicate(spaces));
					}
					else {
						return new FacilioModulePredicate(fieldName, PredicateUtils.falsePredicate());
					}
				} catch (Exception e) {
					log.error("Exception occurred ", e);
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

					List<BaseSpaceContext> spaces = SpaceAPI.getBaseSpaces(value);
					if (CollectionUtils.isNotEmpty(spaces)) {
						builder.append(constructResourceBuilder(spaces).constructQueryString());
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

	private static class BuildingIsPredicate implements Predicate {

		private List<BaseSpaceContext> spaces = null;

		private BuildingIsPredicate (List<BaseSpaceContext> spaces) {
			this.spaces = spaces;
		}

		@Override
		public boolean evaluate(Object object) {
			if(object != null && CollectionUtils.isNotEmpty(spaces)) {
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

					SelectRecordsBuilder<ResourceContext> resourceBuilder = constructResourceBuilder(spaces);

					ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule resourceModule = moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE);
					resourceBuilder.andCondition(CriteriaAPI.getIdCondition(currentId, resourceModule));
					List<ResourceContext> resources = resourceBuilder.get();

					return CollectionUtils.isNotEmpty(resources);

				} catch (Exception e) {
					log.error("Exception occurred in BuildingIs Predicate ", e);
				}
			}
			return false;
		}
	}

	private static SelectRecordsBuilder<ResourceContext> constructResourceBuilder(List<BaseSpaceContext> spaces) throws Exception {
		Map<BaseSpaceContext.SpaceType, List<Long>> typeWiseIds = new HashMap<>();
		for (BaseSpaceContext space : spaces) {
			List<Long> ids = typeWiseIds.get(space.getSpaceTypeEnum());
			if (ids == null) {
				ids = new ArrayList<>();
				typeWiseIds.put(space.getSpaceTypeEnum(), ids);
			}
			ids.add(space.getId());
		}

		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule resourceModule = moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		FacilioModule baseSpaceModule = moduleBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
				.select(Collections.singletonList(moduleBean.getField("id", resourceModule.getName())))
				.module(resourceModule)
				.setAggregation()
				.innerJoin(baseSpaceModule.getTableName()).on("Resources.SPACE_ID = BaseSpace.ID")
				.beanClass(ResourceContext.class)
				;

		List<FacilioField> spaceFields = moduleBean.getAllFields(baseSpaceModule.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(spaceFields);
		Map<String, FacilioField> resourceFieldMap = FieldFactory.getAsMap(moduleBean.getAllFields(resourceModule.getName()));
		for (Map.Entry<BaseSpaceContext.SpaceType, List<Long>> entry : typeWiseIds.entrySet()) {
			switch (entry.getKey()) {
				case SITE:
					FacilioField siteField = fieldMap.get("site");
					resourceBuilder.orCondition(CriteriaAPI.getCondition(siteField, entry.getValue(), PickListOperators.IS));
					break;
				case BUILDING:
					FacilioField buildingField = fieldMap.get("building");
					resourceBuilder.orCondition(CriteriaAPI.getCondition(buildingField, entry.getValue(), PickListOperators.IS));
					break;
				case FLOOR:
					FacilioField floorField = fieldMap.get("floor");
					resourceBuilder.orCondition(CriteriaAPI.getCondition(floorField, entry.getValue(), PickListOperators.IS));
					break;
				case SPACE:
					resourceBuilder.orCondition(CriteriaAPI.getCondition(resourceFieldMap.get("space"), entry.getValue(), PickListOperators.IS));
					for (int i = 1; i<=4; i++) {
						FacilioField spaceField = fieldMap.get("space"+i);
						resourceBuilder.orCondition(CriteriaAPI.getCondition(spaceField, entry.getValue(), PickListOperators.IS));
					}
					break;
				case ZONE:
					StringBuilder zoneBuilder = new StringBuilder()
							.append(baseSpaceModule.getTableName())
							.append(".ID IN (SELECT BASE_SPACE_ID FROM Zone_Space WHERE ORGID = ? AND ZONE_ID IN (")
							.append(StringUtils.join(entry.getValue(), ","))
							.append("))")
							;
					resourceBuilder.orCustomWhere(zoneBuilder.toString(), AccountUtil.getCurrentOrg().getId());
					break;
			}

		}
		return resourceBuilder;
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
}
