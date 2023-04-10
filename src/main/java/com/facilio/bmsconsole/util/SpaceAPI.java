package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3FloorContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SpaceAPI {

	private static Logger LOGGER = Logger.getLogger(SpaceAPI.class.getName());

	public static List<PhotosContext> getBaseSpacePhotos(Long baseSpaceId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE_PHOTOS);

		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE_PHOTOS);

		SelectRecordsBuilder<PhotosContext> builder = new SelectRecordsBuilder<PhotosContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(PhotosContext.class)
				.module(module)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(module.getTableName() + ".PARENT_SPACE", "basespaceId", baseSpaceId + "", NumberOperators.EQUALS));

		List<PhotosContext> photos = builder.get();

		return photos;
	}

	public static double computeTenantZoneArea(long zoneId) throws Exception {
		List<Long> ids = new ArrayList<Long>();
		ids.add(zoneId);
		double area = 0.0;
		List<BaseSpaceContext> children = SpaceAPI.getZoneChildren(ids);
		for (int i = 0; i < children.size(); i++) {
			area += children.get(i).getArea();
		}

		return area;
	}
	
	public static final List<String> WEATHER_NON_CURRENT_MODULES = Arrays.asList(ContextNames.WEATHER_HOURLY_FORECAST_READING, 
			ContextNames.WEATHER_DAILY_FORECAST_READING, ContextNames.WEATHER_DAILY_READING);
	
	public static List<FacilioModule> getDefaultReadings(SpaceType type, boolean onlyReading) throws Exception {
		return getDefaultReadings(type, onlyReading, false);
	}
	
	public static List<FacilioModule> getDefaultReadings(SpaceType type, boolean onlyReading, boolean excludeForecasts) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = null;
		switch(type) {
			case SITE:
				moduleName = FacilioConstants.ContextNames.SITE;
				break;
			case BUILDING:
				moduleName = FacilioConstants.ContextNames.BUILDING;
				break;
			case FLOOR:
				moduleName = FacilioConstants.ContextNames.FLOOR;
				break;
			case SPACE:
				moduleName = FacilioConstants.ContextNames.SPACE;
				break;
			case ZONE:
				moduleName = FacilioConstants.ContextNames.ZONE;
				break;
			case WEATHER_STATION:
				moduleName = FacilioConstants.ModuleNames.WEATHER_STATION;
		}
		
		List<FacilioModule> readings = null;
		if (onlyReading) {
			readings = modBean.getSubModules(moduleName, ModuleType.READING, ModuleType.SYSTEM_SCHEDULED_FORMULA);
		}
		else {
			readings = modBean.getSubModules(moduleName, ModuleType.READING, ModuleType.LIVE_FORMULA, ModuleType.SCHEDULED_FORMULA, ModuleType.SYSTEM_SCHEDULED_FORMULA);
		}
		if (readings != null && excludeForecasts && type == SpaceType.SITE) {
			return readings.stream().filter(module -> !WEATHER_NON_CURRENT_MODULES.contains(module.getName())).collect(Collectors.toList());
		}
		return readings;
	}

	public static void updateHelperFields(V3BaseSpaceContext space) throws Exception {
		BaseSpaceContext updateSpace = new BaseSpaceContext();
		switch(space.getSpaceTypeEnum()) {
			case SITE:
				updateSpace.setSiteId(space.getId());
				space.setSiteId(space.getId());
				break;
			case BUILDING:
				updateSpace.setBuildingId(space.getId());
				space.setBuildingId(space.getId());
				break;
			case FLOOR:
				updateSpace.setFloorId(space.getId());
				space.setFloorId(space.getId());
				break;
			case SPACE:
				updateSpace.setSpaceId(space.getId());
				space.setSpaceId(space.getId());
				break;
			default:
				break;
		}
		updateSpace.setSpaceId(space.getId());
		space.setSpaceId(space.getId());

		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = bean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		UpdateRecordBuilder<BaseSpaceContext> updateBuilder = new UpdateRecordBuilder<BaseSpaceContext>()
				.fields(bean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE))
				.module(module)
				.andCondition(CriteriaAPI.getIdCondition(space.getId(), module))
				;

		updateBuilder.update(updateSpace);
	}
	
	public static void updateHelperFields(BaseSpaceContext space) throws Exception {
		BaseSpaceContext updateSpace = new BaseSpaceContext();
		switch(space.getSpaceTypeEnum()) {
			case SITE:
				updateSpace.setSiteId(space.getId());
				space.setSiteId(space.getId());
				break;
			case BUILDING:
				updateSpace.setBuildingId(space.getId());
				space.setBuildingId(space.getId());
				break;
			case FLOOR:
				updateSpace.setFloorId(space.getId());
				space.setFloorId(space.getId());
				break;
			case SPACE:
				updateSpace.setSpaceId(space.getId());
				space.setSpaceId(space.getId());
				break;
			default:
				break;
		}
		updateSpace.setSpaceId(space.getId());
		space.setSpaceId(space.getId());
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = bean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		UpdateRecordBuilder<BaseSpaceContext> updateBuilder = new UpdateRecordBuilder<BaseSpaceContext>()
																	.fields(bean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE))
																	.module(module)
																	.andCondition(CriteriaAPI.getIdCondition(space.getId(), module))
																	;
		
		updateBuilder.update(updateSpace);
																
																	
	}
	
	public static SiteContext getSiteSpace(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(SiteContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		
		LookupField location = (LookupField) fieldsAsMap.get("location");
		selectBuilder.fetchSupplement(location);
		List<SiteContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
	}
	
	
	public static List<SiteContext> getSiteSpace(String fieldName,Collection<Long> values) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		FacilioField whereField= modBean.getField(fieldName, module.getName());
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(SiteContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCondition(CriteriaAPI.getCondition(whereField,values,NumberOperators.EQUALS));
		return selectBuilder.get();
	}
	
	public static List<SiteContext> getSiteSpace(String spaceList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(SiteContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCondition(CriteriaAPI.getIdCondition(spaceList, module));
		return selectBuilder.get();
	}
	
	public static Long getDependentSpaceId(String spaceName, Long parentSpaceId, Integer heirarchy) throws Exception {
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modbean.getAllFields(module.getName());
		String columnName = "SPACE_ID" + heirarchy + " = ?";
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>();
		selectBuilder.select(fields).table(module.getTableName()).moduleName(module.getName()).beanClass(SpaceContext.class).andCustomWhere("NAME = ?", spaceName)
		.andCustomWhere(columnName, parentSpaceId);
		List<SpaceContext> result = selectBuilder.get();
		
		if(!result.isEmpty()) {
			Long spaceId = result.get(0).getId();
			return spaceId;
		}
		else {
			return null;
		}
		
	}
	
	public static Long addDependentSpace(String spaceName, Long parentSpaceId, Long importId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		SpaceContext spaceContext = new SpaceContext();
		spaceContext.setName(spaceName);
		spaceContext.setSourceType(SourceType.IMPORT.getIndex());
		spaceContext.setSourceId(importId);
		SpaceContext parentSpace = SpaceAPI.getSpace(parentSpaceId);
		
		if(parentSpace.getSpaceId1() != -1) {
			spaceContext.setSpaceId1(parentSpace.getSpaceId1());
			if(parentSpace.getSpaceId2() != -1) {
				spaceContext.setSpaceId2(parentSpace.getSpaceId2());
				if(parentSpace.getSpaceId3() != -1) {
					spaceContext.setSpaceId3(parentSpace.getSpaceId3());
					if(parentSpace.getSpaceId4() != -1) {
						spaceContext.setSpaceId4(parentSpace.getSpaceId4());
						if (parentSpace.getSpaceId5() != -1) {
							spaceContext.setSpaceId5(parentSpace.getSpaceId5());
						}else {
							spaceContext.setSpaceId5(parentSpace.getId());
						}
					}else {
						spaceContext.setSpaceId4(parentSpace.getId());
					}
				}else {
					spaceContext.setSpaceId3(parentSpace.getId());
				}
			}else {
				spaceContext.setSpaceId2(parentSpace.getId());
			}
		}else {
			spaceContext.setSpaceId1(parentSpace.getId());
		}
		
		spaceContext.setSiteId(parentSpace.getSiteId());
		spaceContext.setBuildingId(parentSpace.getBuildingId());
		spaceContext.setFloorId(parentSpace.getFloorId());
		
		
		spaceContext.setSpaceType(BaseSpaceContext.SpaceType.SPACE);
		InsertRecordBuilder<SpaceContext> insertRecordBuilder = new InsertRecordBuilder<>();
		
		insertRecordBuilder.table(module.getTableName()).module(module).fields(fields);
		Long spaceId = insertRecordBuilder.insert(spaceContext);
		SpaceAPI.updateHelperFields(spaceContext);
		
		
		return spaceId;
		
	}
	
	public static BuildingContext getBuildingSpace(long id) throws Exception {
		return getBuildingSpace(id, false);
	}
	
	public static BuildingContext getBuildingSpace(long id, boolean fetchLocation) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(BuildingContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		LookupField location = (LookupField) fieldsAsMap.get("location");
		selectBuilder.fetchSupplement(location);
		List<BuildingContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
	}
	
	public static List<BuildingContext> getBuildingSpace(String  buildingList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		String tableName=module.getTableName();
		String fieldName=tableName+".ID";
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																	.select(fields)
																	.table(tableName)
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(BuildingContext.class)
																	.orderBy("LOCAL_ID")
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCondition(CriteriaAPI.getCondition(fieldName,fieldName, 
																			buildingList,NumberOperators.EQUALS));
		List<BuildingContext> props = selectBuilder.get();
		
		return props;
	}
	
	
	public static LocationContext getLocationSpace(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.LOCATION);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.LOCATION);
		
		SelectRecordsBuilder<LocationContext> selectBuilder = new SelectRecordsBuilder<LocationContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(LocationContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		List<LocationContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
	}

	public static V3FloorContext getV3FloorSpace(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FLOOR);

		SelectRecordsBuilder<V3FloorContext> selectBuilder = new SelectRecordsBuilder<V3FloorContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.maxLevel(0)
				.beanClass(V3FloorContext.class)
				.andCustomWhere(module.getTableName()+".ID = ?", id);
		List<V3FloorContext> spaces = selectBuilder.get();

		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
	}
	
	public static FloorContext getFloorSpace(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FLOOR);
		
		SelectRecordsBuilder<FloorContext> selectBuilder = new SelectRecordsBuilder<FloorContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(FloorContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		List<FloorContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
	}

	public static V3SpaceContext getV3Space(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);

		SelectRecordsBuilder<V3SpaceContext> selectBuilder = new SelectRecordsBuilder<V3SpaceContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(V3SpaceContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere(module.getTableName()+".ID = ?", id);
		List<V3SpaceContext> spaces = selectBuilder.get();

		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
	}
	
	public static SpaceContext getSpace(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(SpaceContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		List<SpaceContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
	}
	
	public static BaseSpaceContext getBaseSpace(long id) throws Exception {
		return getBaseSpace(id, false);
	}
	
	public static BaseSpaceContext getBaseSpace(long id, boolean fetchDeleted) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(BaseSpaceContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		if (fetchDeleted) {
			selectBuilder.fetchDeleted();
		}
		
		List<BaseSpaceContext> spaces = selectBuilder.get();
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
	}

	public static BaseSpaceContext getBaseSpaceWithAccessibleSpace(long id) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		List<Long> ids = AccountUtil.getUserBean().getAccessibleSpaceList(AccountUtil.getCurrentUser().getOuid());

		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(BaseSpaceContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id,module));

			if(CollectionUtils.isNotEmpty(ids))	{
				Criteria siteCriteria = new Criteria();
				siteCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(modBean.getModule(ContextNames.BASE_SPACE)), StringUtils.join(ids, ","), BuildingOperator.BUILDING_IS));

				Criteria buildingCriteria = new Criteria();

				buildingCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("building"), StringUtils.join(ids, ","), BuildingOperator.BUILDING_IS));
				selectBuilder.andCriteria(siteCriteria);
				selectBuilder.orCriteria(buildingCriteria);
			}



		List<BaseSpaceContext> spaces = selectBuilder.get();
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}

		return null;
	}

	public static BaseSpaceContext getBasespaceDetailsWithHierarchy(long id, boolean fetchDeleted) throws Exception {
		BaseSpaceContext baseSpace = getBaseSpace(id, fetchDeleted);
		baseSpace = fetchHierarchy(baseSpace);
		return baseSpace;
	}
	
	public static BaseSpaceContext fetchHierarchy(BaseSpaceContext space) throws Exception {
		if(space.getSiteId() > 0){
			space.setSite(getSiteSpace(space.getSiteId()));
		}
		if(space.getBuildingId() > 0){
			space.setBuilding(getBuildingSpace(space.getBuildingId()));
		}
		if(space.getFloorId() > 0){
			space.setFloor(getFloorSpace(space.getFloorId()));
		}
		if(space.getSpaceId() > 0){
			space.setSpace(getSpace(space.getSpaceId()));
		}
		if(space.getSpaceId1() > 0){
			space.setSpace1(getSpace(space.getSpaceId1()));
		}
		if(space.getSpaceId2() > 0){
			space.setSpace2(getSpace(space.getSpaceId2()));
		}
		if(space.getSpaceId3() > 0){
			space.setSpace3(getSpace(space.getSpaceId3()));
		}
		if(space.getSpaceId4() > 0){
			space.setSpace4(getSpace(space.getSpaceId4()));
		}
		if(space.getSpaceId5() > 0){
			space.setSpace5(getSpace(space.getSpaceId5()));
		}
		return space;
	}
	
	public static List<BaseSpaceContext> getBaseSpaceWithChildren(long id) throws Exception {
		List<Long> ids = new ArrayList<>();
		ids.add(id);
		return getBaseSpaceWithChildren(ids);
	}
	
	public static void addZoneChildren(ZoneContext zone, List<Long> childrenIds) throws SQLException, RuntimeException, Exception {
		List<Map<String, Object>> childProps = new ArrayList<>();

		for(long childId : childrenIds) {
			Map<String, Object> prop = new HashMap<>();
			prop.put("zoneId", zone.getId());
			prop.put("basespaceId", childId);
			prop.put("isImmediate", true);
			childProps.add(prop);
		}
		// Needs to open for Zone inside zone only
		/*  
		List<ZoneContext> parentSpaces = getAllZone();
		List<Long> zoneIds = null;
		if (parentSpaces != null) {
			zoneIds = parentSpaces.stream().map(existingZone -> existingZone.getId()).filter(id -> childrenIds.contains(id)).collect(Collectors.toList());
		}
		
		if (parentSpaces != null) {
			List<BaseSpaceContext> zoneChildren = getZoneChildren(zoneIds, false);
			if (zoneChildren != null) {
				for(BaseSpaceContext zoneChild : zoneChildren) {
					if (zoneChild.getSpaceTypeEnum() != SpaceType.ZONE) {
						Map<String, Object> prop = new HashMap<>();
						prop.put("zoneId", zone.getId());
						prop.put("basespaceId", zoneChild.getId());
						prop.put("isImmediate", false);
						childProps.add(prop);
					}
				}
			}
		}*/
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getZoneRelModule().getTableName())
														.fields(FieldFactory.getZoneRelFields())
														.addRecords(childProps);
		
		insertBuilder.save();
	}

	public static void addZoneChildren(long zoneId, List<Long> childrenIds) throws SQLException, RuntimeException, Exception {
		List<Map<String, Object>> childProps = new ArrayList<>();

		for(long childId : childrenIds) {
			Map<String, Object> prop = new HashMap<>();
			prop.put("zoneId", zoneId);
			prop.put("basespaceId", childId);
			prop.put("isImmediate", true);
			childProps.add(prop);
		}

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getZoneRelModule().getTableName())
														.fields(FieldFactory.getZoneRelFields())
														.addRecords(childProps);
		
		insertBuilder.save();
	}
	
	public static List<ZoneContext> getTenantZones() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("zone");
		FacilioModule tenantModule = modBean.getModule("tenant");
		
		FacilioField isDeletedField = FieldFactory.getIsDeletedField(tenantModule);
		
		Condition isDeletedCond = new Condition();
		isDeletedCond.setField(isDeletedField);
		isDeletedCond.setOperator(NumberOperators.NOT_EQUALS);
		isDeletedCond.setValue(""+1);

		
		List<FacilioField> fields = modBean.getAllFields("zone");
		
		SelectRecordsBuilder<ZoneContext> builder = new SelectRecordsBuilder<ZoneContext>()
													.table(module.getTableName())
													.moduleName("zone")
													.select(fields)
													.beanClass(ZoneContext.class)
													.innerJoin(tenantModule.getTableName())
													.on(tenantModule.getTableName()+".ZONE_ID = "+module.getTableName()+".ID")
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(isDeletedCond)
													.andCustomWhere(tenantModule.getTableName()+".STATUS = ?", 1)
													.andCustomWhere(module.getTableName()+".TENANT_ZONE = ?", 1)
													.orderBy("ID");
		List<ZoneContext> zoneList = builder.get();
		return zoneList;
	}
	public static void deleteZoneChildren(long zoneId) throws Exception {
		FacilioModule mod = ModuleFactory.getZoneRelModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(mod.getTableName())
				.andCondition(CriteriaAPI.getCondition("ZONE_ID", "zoneId", String.valueOf(zoneId), NumberOperators.EQUALS));
		
		deleteBuilder.delete();
	}
	
	public static void updateZoneInfo(ZoneContext zone, List<Long> childrenIds) throws Exception {
		List<BaseSpaceContext> children = SpaceAPI.getBaseSpaces(childrenIds);
		
		long siteId = -1, buildingId = -1, floorId = -1;
		boolean isFirst = true;
		for(BaseSpaceContext child : children) {
			if(isFirst) {
				siteId = child.getSiteId();
				isFirst = false;
			}
			else if(siteId != child.getSiteId()) {
				siteId = -1;
				break;
			}
		}
		
		isFirst = true;
		for(BaseSpaceContext child : children) {
			if(isFirst) {
				buildingId = child.getBuildingId();
				isFirst = false;
			}
			else if(siteId != child.getBuildingId()) {
				buildingId = -1;
				break;
			}
		}
		
		isFirst = true;
		for(BaseSpaceContext child : children) {
			if(isFirst) {
				floorId = child.getFloorId();
				isFirst = false;
			}
			else if(floorId != child.getFloorId()) {
				floorId = -1;
				break;
			}
		}
		
		if(siteId != -1) {
			zone.setSiteId(siteId);
		}
		if(buildingId != -1) {
			zone.setBuildingId(buildingId);
		}
		if(floorId != -1) {
			zone.setFloorId(floorId);
		}
	}
	
	public static List<BaseSpaceContext> getBaseSpaceWithChildren(List<Long> ids) throws Exception
	{
		List<BaseSpaceContext> parentSpaces = getBaseSpaces(ids);
		
		if(parentSpaces != null && !parentSpaces.isEmpty()) {
			List<BaseSpaceContext> spaces = new ArrayList<>();
			for(BaseSpaceContext parentSpace : parentSpaces) {
				SpaceType type = parentSpace.getSpaceTypeEnum();
				List<BaseSpaceContext> childSpaces = null;
				switch(type) {
					case SITE:
							childSpaces = getSiteChildren(parentSpace.getId());
							break;
					case BUILDING:
							childSpaces = getBuildingChildren(parentSpace.getId());
							break;
					case FLOOR:
							childSpaces = getFloorChildren(parentSpace.getId());
							break;
					case SPACE:
							spaces.add(parentSpace);
							childSpaces = getSpaceChildren(parentSpace.getId());
							break;
					case ZONE:
							List<Long> idList = new ArrayList<Long>();
							idList.add(parentSpace.getId());
							childSpaces = getZoneChildren(idList);
							break;
				}
				if(childSpaces != null && !childSpaces.isEmpty()) {
					spaces.addAll(childSpaces);
				}
			}
			return spaces;
		}
		return null;
	}
	private static List<BaseSpaceContext> getSpaceChildren(long spaceID) throws Exception {
		List<Long> spaceIDs = new ArrayList<>();
		spaceIDs.add(spaceID);
		return getSpaceChildren(spaceIDs);
	}
	
	
	private static List<BaseSpaceContext> getSpaceChildren(List<Long> spaceIDs) throws Exception {
		if(spaceIDs != null && !spaceIDs.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			Criteria criteria = new Criteria();
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space1"),spaceIDs , NumberOperators.EQUALS));
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space2"),spaceIDs , NumberOperators.EQUALS));
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space3"),spaceIDs , NumberOperators.EQUALS));
			
			
			SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																		.select(fields)
																		.table(module.getTableName())
																		.moduleName(module.getName())
																		.beanClass(BaseSpaceContext.class)
//																		.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																		.andCriteria(criteria)
																		;
			List<BaseSpaceContext> spaces = selectBuilder.get();
			return spaces;
		}
		return null;
	}
	
	public static List<BaseSpaceContext> getSiteChildren(long siteId) throws Exception {
		List<Long> siteIds = new ArrayList<>();
		siteIds.add(siteId);
		return getSiteChildren(siteIds);
	}
	
	private static List<BaseSpaceContext> getSiteChildren(List<Long> siteIds) throws Exception {
		if(siteIds != null && !siteIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
			
			StringBuilder ids = new StringBuilder();
			ids.append(module.getTableName())
								.append(".SITE_ID IN (");
			boolean isFirst = true;
			for(long id : siteIds) {
				if(isFirst) {
					isFirst = false;
				}
				else {
					ids.append(", ");
				}
				ids.append(id);
			}
			ids.append(")");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
			
			SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																		.select(fields)
																		.table(module.getTableName())
																		.moduleName(module.getName())
																		.beanClass(BaseSpaceContext.class)
//																		.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																		.andCustomWhere(ids.toString());
			List<BaseSpaceContext> spaces = selectBuilder.get();
			return spaces;
		}
		return null;
	}
	
	private static List<BaseSpaceContext> getBuildingChildren(long buildingId) throws Exception {
		List<Long> buildingIds = new ArrayList<>();
		buildingIds.add(buildingId);
		return getBuildingChildren(buildingIds);
	}
	
	private static List<BaseSpaceContext> getBuildingChildren(List<Long> buildingIds) throws Exception {
		if(buildingIds != null && !buildingIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
			
			StringBuilder ids = new StringBuilder();
			ids.append(module.getTableName())
								.append(".BUILDING_ID IN (");
			boolean isFirst = true;
			for(long id : buildingIds) {
				if(isFirst) {
					isFirst = false;
				}
				else {
					ids.append(", ");
				}
				ids.append(id);
			}
			ids.append(")");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
			
			SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																		.select(fields)
																		.table(module.getTableName())
																		.moduleName(module.getName())
																		.beanClass(BaseSpaceContext.class)
//																		.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																		.andCustomWhere(ids.toString());
			List<BaseSpaceContext> spaces = selectBuilder.get();
			return spaces;
		}
		return null;
	}
	
	public static List<BaseSpaceContext> getBuildingsWithFloors(long siteId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
				.select(fields)
				.module(module)
				.maxLevel(0)
				.beanClass(BaseSpaceContext.class)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("SITE_ID=? AND SPACE_TYPE=?",siteId,BaseSpaceContext.SpaceType.BUILDING.getIntVal());
		
		List<BaseSpaceContext> spaces = selectBuilder.get();
		return spaces;
	}

	public static long getBuildingsFloorsCount(long buildingId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);

		SelectRecordsBuilder<FloorContext> selectBuilder = new SelectRecordsBuilder<FloorContext>()
				.module(module)
				.beanClass(FloorContext.class)
				.andCondition(CriteriaAPI.getCondition("BUILDING_ID","building",String.valueOf(buildingId),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("SPACE_TYPE", "spaceType",String.valueOf(BaseSpaceContext.SpaceType.FLOOR.getIntVal()),NumberOperators.EQUALS));
		selectBuilder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));

		List<FloorContext> floors = selectBuilder.get();

		return floors.get(0).getId();
	}

	public static List<FloorContext> getBuildingsFloorsContext(long buildingId, Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FLOOR);
		SelectRecordsBuilder<FloorContext> selectBuilder = new SelectRecordsBuilder<FloorContext>()
				.module(module)
				.select(fields)
				.beanClass(FloorContext.class)
				.andCondition(CriteriaAPI.getCondition("BUILDING_ID", "building", String.valueOf(buildingId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("SPACE_TYPE", "spaceType", String.valueOf(BaseSpaceContext.SpaceType.FLOOR.getIntVal()), NumberOperators.EQUALS))
				.orderBy("Floor.FLOOR_LEVEL ASC,Resources.NAME ASC");
		boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
		if (skipModuleCriteria) {
			selectBuilder.skipModuleCriteria();
		}
		List<FloorContext> floors = selectBuilder.get();

		return floors;
	}

	// Returns buildings without floors as well; to be refactored with getSiteBuildings()
	public static List<BaseSpaceContext> getSiteBuildingsWithFloors(long sitedId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);

		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
				.select(fields)
				.module(module)
				.maxLevel(0)
				.beanClass(BaseSpaceContext.class)
				//	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("BaseSpace.SITE_ID =? AND BaseSpace.SPACE_TYPE=?", sitedId, BaseSpaceContext.SpaceType.BUILDING.getIntVal());
		List<BaseSpaceContext> spaces = selectBuilder.get();
		return spaces;
	}
	
	public static List<BaseSpaceContext> getBuildingFloors(long buildingId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(BaseSpaceContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere("BUILDING_ID =? AND SPACE_TYPE=?",buildingId,BaseSpaceContext.SpaceType.FLOOR.getIntVal());
		List<BaseSpaceContext> spaces = selectBuilder.get();
		return spaces;
	}

	public static List<BuildingContext> getSiteBuildings(long siteId) throws Exception {
		return getSiteBuildings(siteId, null);
	}

	public static List<BuildingContext> getSiteBuildings(long siteId, Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);


		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
				.select(fields)
				.module(module)
				.beanClass(BuildingContext.class)
				.andCustomWhere("BaseSpace.SITE_ID =? AND SPACE_TYPE=?", siteId, BaseSpaceContext.SpaceType.BUILDING.getIntVal())
				;
		if (fieldsMap.containsKey("sortingHelper")) {
			selectBuilder.orderBy(fieldsMap.get("sortingHelper").getTableName() + "." + fieldsMap.get("sortingHelper").getColumnName() + " ASC, Resources.NAME ASC");
		} else {
			selectBuilder.orderBy("Resources.NAME ASC");
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(module.getName());
		if (scopeCriteria != null) {
			selectBuilder.andCriteria(scopeCriteria);
		}
		
		Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria(module.getName(),"read");
		if (permissionCriteria != null) {
			selectBuilder.andCriteria(permissionCriteria);
		}
		if (context != null) {
			boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
			if (skipModuleCriteria) {
				selectBuilder.skipModuleCriteria();
			}
		}
		
		List<BuildingContext> buildings = selectBuilder.get();
		return buildings;
	}
	
	public static List<BuildingContext> getAllBuildings() throws Exception {
		return getAllBuildings(-1);
	}
	
	
	public static List<BuildingContext> getAllBuildings(long siteId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																	.select(fields)
																	.module(module)
																	.beanClass(BuildingContext.class)
																	.andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"), String.valueOf(BaseSpaceContext.SpaceType.BUILDING.getIntVal()), NumberOperators.EQUALS))
																	;
		
		if (siteId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("site"), String.valueOf(siteId), PickListOperators.IS));
		}
		
		if (AccountUtil.getCurrentUser() != null) {
			Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(module.getName());
			if (scopeCriteria != null) {
				selectBuilder.andCriteria(scopeCriteria);
			}

			if (AccountUtil.getCurrentUser().getRole() != null) {
				Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria(module.getName(),"read");
				if (permissionCriteria != null) {
					selectBuilder.andCriteria(permissionCriteria);
				}
			}

		}
		
		List<BuildingContext> buildings = selectBuilder.get();
		return buildings;
	}
	
	public static List<FloorContext> getAllFloors() throws Exception {
		return getAllFloors(-1);
	}
	
	public static List<FloorContext> getAllFloors(long siteId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FLOOR);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<FloorContext> selectBuilder = new SelectRecordsBuilder<FloorContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(FloorContext.class)
																	.andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"), String.valueOf(BaseSpaceContext.SpaceType.FLOOR.getIntVal()), NumberOperators.EQUALS))
																	;
		
		if (siteId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("site"), String.valueOf(siteId), PickListOperators.IS));
		}
		
		List<FloorContext> floors = selectBuilder.get();
		return floors;
	}
	public static List<ZoneContext> getAllZone() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ZONE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ZONE);
		
		SelectRecordsBuilder<ZoneContext> selectBuilder = new SelectRecordsBuilder<ZoneContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(ZoneContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere("SPACE_TYPE=?",BaseSpaceContext.SpaceType.ZONE.getIntVal());
		List<ZoneContext> zones = selectBuilder.get();
		return zones;
	}
	
	public static ZoneContext getZone(String zoneName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ZONE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ZONE);
		
		SelectRecordsBuilder<ZoneContext> selectBuilder = new SelectRecordsBuilder<ZoneContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(ZoneContext.class)
																	.andCustomWhere("SPACE_TYPE=? AND NAME=?",BaseSpaceContext.SpaceType.ZONE.getIntVal(),zoneName);
		
		return selectBuilder.fetchFirst();
	}
	
	public static BuildingContext getBuilding(String buildingName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(BuildingContext.class)
																	.andCustomWhere("SPACE_TYPE=? AND NAME=?",BaseSpaceContext.SpaceType.BUILDING.getIntVal(),buildingName);
		
		return selectBuilder.fetchFirst();
	}
	
	public static SiteContext getSite(String siteName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(SiteContext.class)
																	.andCustomWhere("SPACE_TYPE=? AND NAME=?",BaseSpaceContext.SpaceType.SITE.getIntVal(),siteName);
		
		return selectBuilder.fetchFirst();
	}
	
	public static List<SpaceContext> getAllSpaces() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(SpaceContext.class)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere("SPACE_TYPE=?",BaseSpaceContext.SpaceType.SPACE.getIntVal());
		List<SpaceContext> spaces = selectBuilder.get();
		return spaces;
	}
	
	public static List<SpaceContext> getAllSpaces(long floorId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
																	.select(fields)
																	.module(module)
																	.beanClass(SpaceContext.class)
																	.andCondition(CriteriaAPI.getCondition(fieldMap.get("floor"), floorId+"", NumberOperators.EQUALS))
																	.andCustomWhere("SPACE_TYPE=?",BaseSpaceContext.SpaceType.SPACE.getIntVal());
		List<SpaceContext> spaces = selectBuilder.get();
		return spaces;
	}

	public static List<SiteContext> getAllSites(boolean fetchLocation) throws Exception{
		return getAllSites(fetchLocation,null,null);
	}

	public static List<SiteContext> getAllSites(boolean fetchLocation,JSONObject pagination,String search) throws Exception {
		return getAllSites(fetchLocation, pagination, search, false );
	}

	public static List<SiteContext> getAllSites(boolean fetchLocation,JSONObject pagination,String search, boolean skipScoping) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.module(module)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.beanClass(SiteContext.class);
		if(skipScoping) {
			selectBuilder.skipScopeCriteria();
		}
		if (fetchLocation) {
			selectBuilder.fetchSupplement((SupplementRecord) FieldFactory.getAsMap(fields).get("location"));
		}
		if(StringUtils.isNotEmpty(search)){
			selectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", search, StringOperators.CONTAINS));
		}
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			int offset = ((page - 1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}
		List<SiteContext> sites = selectBuilder.get();
		return sites;
	}
	
	public static List<SiteContext> getAllSites(List<? extends LookupField> lookupFields) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.module(module)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.beanClass(SiteContext.class);
		
		if (CollectionUtils.isNotEmpty(lookupFields)) {
			selectBuilder.fetchSupplements(lookupFields);
		}
		
		List<SiteContext> sites = selectBuilder.get();
		return sites;
	}
	
	public static List<SiteContext> getAllSitesOfType(int siteType) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.module(module)
																	.andCustomWhere("SITE_TYPE = ?", siteType)
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.beanClass(SiteContext.class);
		List<SiteContext> sites = selectBuilder.get();
		return sites;
	}
	
	public static List<SiteContext> getAllSites() throws Exception {
		return getAllSites(false);
	}
	
	
public static long getSitesCount() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(SiteContext.class)
																	.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module))
																	;
		
		List<SiteContext> result = selectBuilder.get();
		if (result == null || result.isEmpty()) {
			return 0;
		}
		else {
			return result.get(0).getId();
		}
	}
	
	private static List<BaseSpaceContext> getFloorChildren(long floorId) throws Exception {
		List<Long> floorIds = new ArrayList<>();
		floorIds.add(floorId);
		return getFloorChildren(floorIds);
	}
	
	private static List<BaseSpaceContext> getFloorChildren(List<Long> floorIds) throws Exception {
		if(floorIds != null && !floorIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
			
			StringBuilder ids = new StringBuilder();
			ids.append(module.getTableName())
								.append(".FLOOR_ID IN (");
			boolean isFirst = true;
			for(long id : floorIds) {
				if(isFirst) {
					isFirst = false;
				}
				else {
					ids.append(", ");
				}
				ids.append(id);
			}
			ids.append(")");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
			
			SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																		.select(fields)
																		.table(module.getTableName())
																		.moduleName(module.getName())
																		.beanClass(BaseSpaceContext.class)
//																		.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																		.andCustomWhere(ids.toString());
			List<BaseSpaceContext> spaces = selectBuilder.get();
			return spaces;
		}
		return null;
	}
	
	public static List<BaseSpaceContext> getZoneChildren(List<Long> zoneIds) throws Exception {
		return getZoneChildren(zoneIds, true);
	}
	
	private static List<BaseSpaceContext> getZoneChildren(List<Long> zoneIds, Boolean isImmediate) throws Exception {
		List<BaseSpaceContext> spaces = new ArrayList<BaseSpaceContext>();
		if(zoneIds != null && !zoneIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
			
			StringBuilder ids = new StringBuilder();
			ids.append("Zone_Space.ZONE_ID IN (");
			boolean isFirst = true;
			for(long id : zoneIds) {
				if(isFirst) {
					isFirst = false;
				}
				else {
					ids.append(", ");
				}
				ids.append(id);
			}
			ids.append(")");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
			
			SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																		.select(fields)
																		.table(module.getTableName())
																		.innerJoin("Zone_Space")
																		.on("BaseSpace.ID = Zone_Space.BASE_SPACE_ID")
																		.moduleName(module.getName())
																		.beanClass(BaseSpaceContext.class)
//																		.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																		.andCustomWhere(ids.toString());
			if (isImmediate) {
				selectBuilder.andCustomWhere("Zone_Space.IS_IMMEDIATE = ?", isImmediate);
			}									
			spaces = selectBuilder.get();
			return spaces;
		}
		return spaces;
	}
	
	
	
	public static List<BaseSpaceContext> getBaseSpaces(Collection<Long> idList) throws Exception
	{
		if(idList != null && !idList.isEmpty()) {
			String list=StringUtils.join(idList, ",");
			return getBaseSpaces(list);
		}
		return null;
	}
	
	public static Map<Long, BaseSpaceContext> getBaseSpaceMap(Collection<Long> idList) throws Exception
	{
		if(idList == null || idList.isEmpty()) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(BaseSpaceContext.class)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(idList, module));
		return selectBuilder.getAsMap();
	}

	public static Map<Long, SpaceContext> getSpaceMap(Collection<Long> idList) throws Exception
	{
		if(CollectionUtils.isEmpty(idList)) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(SpaceContext.class)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(idList, module));
		return selectBuilder.getAsMap();
	}
	
	public static List<BaseSpaceContext> getBaseSpaces(String idList) throws Exception
	{
		if(idList == null || idList.isEmpty()) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(BaseSpaceContext.class)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(idList, module));
		List<BaseSpaceContext> spaces = selectBuilder.get();
		return spaces;
		
	}
	
	
	public static List<SpaceContext> getSpaceListOfCategory(long category) throws Exception
	{
		
		return getSpaceListOfCategory(null,category);
	}
	
	public static List<SpaceContext> getSpaceListOfCategory(long baseSpaceId,long category) throws Exception {
		return getSpaceListOfCategory(Collections.singletonList(baseSpaceId),category);
	}
	
	public static List<SpaceContext> getSpaceListOfCategory(List<Long> baseSpaceIds,long category) throws Exception {
		return getSpaceListOfCategory(baseSpaceIds, category, -1);
	}
	
	public static List<SpaceContext> getSpaceListOfCategory(List<Long> baseSpaceIds,long category, long siteId) throws Exception
	{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField categoryField= fieldMap.get("spaceCategory");
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(SpaceContext.class)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(categoryField, String.valueOf(category), PickListOperators.IS));
		
		if(baseSpaceIds != null && !baseSpaceIds.isEmpty()) {
			BaseSpaceContext basespace = getBaseSpace(baseSpaceIds.get(0));
			if (basespace.getSpaceTypeEnum() == SpaceType.SPACE) {
				List<Condition> conditions = new ArrayList<>();
				conditions.add(CriteriaAPI.getCondition("SPACE_ID1", "SPACE_ID1", StringUtils.join(baseSpaceIds, ","), NumberOperators.EQUALS));
				conditions.add(CriteriaAPI.getCondition("SPACE_ID2", "SPACE_ID2", StringUtils.join(baseSpaceIds, ","), NumberOperators.EQUALS));
				conditions.add(CriteriaAPI.getCondition("SPACE_ID3", "SPACE_ID3", StringUtils.join(baseSpaceIds, ","), NumberOperators.EQUALS));
				conditions.add(CriteriaAPI.getCondition("SPACE_ID4", "SPACE_ID4", StringUtils.join(baseSpaceIds, ","), NumberOperators.EQUALS));
				conditions.add(CriteriaAPI.getCondition("SPACE_ID5", "SPACE_ID5", StringUtils.join(baseSpaceIds, ","), NumberOperators.EQUALS));

				Criteria criteria = new Criteria();
				criteria.groupOrConditions(conditions);
				selectBuilder.andCriteria(criteria);
			} else {
				selectBuilder.andCustomWhere("BaseSpace."+basespace.getSpaceTypeEnum().getStringVal().toUpperCase()+"_ID in( ? )", StringUtils.join(baseSpaceIds, ","));
			}
		}
		
		if (siteId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("site"), String.valueOf(siteId), PickListOperators.IS));
		}
		
		List<SpaceContext> spaces = selectBuilder.get();
		return spaces;
	}
	
	public static List<BaseSpaceContext> getAllBaseSpaces(Criteria filterCriteria, Criteria searchCriteria, String orderBy, JSONObject pagination, Boolean withReadings, Boolean fetchHierarchy) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																	.select(fields)
																	.module(module)
//																	.select Resources.ID, Resources.NAME from BaseSpace 
//																	.innerJoin("Resources").on("BaseSpace.ID = Resources.ID")
																	.leftJoin("Floor").on("Floor.ID = BaseSpace.ID")
//																	.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																	.andCustomWhere("BaseSpace.ORGID = ?", AccountUtil.getCurrentOrg().getId())
																	.beanClass(BaseSpaceContext.class);
		if (filterCriteria != null) {
			selectBuilder.andCriteria(filterCriteria);
		}
		if (searchCriteria != null) {
			selectBuilder.andCriteria(searchCriteria);
		}
		// temp handling for service portal without Login
		if (AccountUtil.getCurrentUser() != null) {
			Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria("basespace");
			if(scopeCriteria != null) {
				selectBuilder.andCriteria(scopeCriteria);
			}
		}
		if (fetchHierarchy  != null && fetchHierarchy) {
			List<LookupField>lookUpfields = new ArrayList<>();
			List<FacilioField> baseSpaceFields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(baseSpaceFields);
			lookUpfields.add((LookupField) fieldsAsMap.get("building"));
			lookUpfields.add((LookupField) fieldsAsMap.get("floor"));
			selectBuilder.fetchSupplements(lookUpfields);
		}
		String orderByFloors = null;
		if (orderBy != null && !orderBy.isEmpty()) {
			orderByFloors = orderBy;
			orderByFloors += ",-Floor.FLOOR_LEVEL desc,Resources.ID asc";
		}
		else {
			orderByFloors = "-Floor.FLOOR_LEVEL desc,Resources.ID asc";
		}
		selectBuilder.orderBy(orderByFloors);
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}
		
		if (withReadings != null && withReadings) {
			FacilioModule rdmModule = ModuleFactory.getReadingDataMetaModule();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());
			
			selectBuilder.innerJoin(rdmModule.getTableName()).on(rdmModule.getTableName()+".RESOURCE_ID="+module.getTableName()+".ID");
			
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("value"), CommonOperators.IS_NOT_EMPTY));
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("value"),"-1", StringOperators.ISN_T));
			selectBuilder.andCriteria(criteria);
		}
		
		
		List<BaseSpaceContext> spaces = selectBuilder.get();
		return spaces;
	}

	
	public static long getSpacesCountForBuilding(long buildingId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Building")
				.innerJoin("BaseSpace")
				.on("Building.ID = BaseSpace.ID")
				.andCustomWhere("Building.ORGID=? AND BaseSpace.ORGID = ? AND BaseSpace.BUILDING_ID = ? AND BaseSpace.SPACE_TYPE = ?", orgId, orgId, buildingId, BaseSpaceContext.SpaceType.SPACE.getIntVal());
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) rs.get(0).get("count")).longValue();
		}
	}

	public static List<Long> getSpaceIdListForBuilding(long buildingId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("id");
		countFld.setColumnName("ID");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("BaseSpace")
				.andCustomWhere(" BaseSpace.ORGID = ? AND BaseSpace.BUILDING_ID = ?", orgId, buildingId);
		
		List<Map<String, Object>> rs = builder.get();
		List<Long> spaceIds = new ArrayList<>();
		if (rs != null && !rs.isEmpty()) {
			for(Map<String, Object> prop:rs) {
				spaceIds.add((Long)prop.get("id")); 
			}
		}
		return spaceIds;
	}
	
	public static List<SpaceContext> getSpaceListForBuilding(long buildingId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
				.moduleName(FacilioConstants.ContextNames.SPACE)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.SPACE))
				.beanClass(SpaceContext.class)
				.andCustomWhere(" BaseSpace.ORGID = ? AND BaseSpace.BUILDING_ID = ?", orgId, buildingId);
		
		List<SpaceContext> rs = builder.get();
		return rs;
	}
	
	public static long getSpacesCountForFloor(long floorId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Floor")
				.innerJoin("BaseSpace")
				.on("Floor.ID = BaseSpace.ID")
				.andCustomWhere("Building.ORGID=? AND BaseSpace.ORGID = ? AND BaseSpace.FLOOR_ID = ? AND BaseSpace.SPACE_TYPE = ?", orgId, orgId, floorId, BaseSpaceContext.SpaceType.SPACE);
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) rs.get(0).get("count")).longValue();
		}
	}

	public static long getWorkOrdersCount(long spaceId) throws Exception {
		return WorkOrderAPI.getOpenWoCountByResource(spaceId, BuildingOperator.BUILDING_IS);
	}
	
	public static long getV2AlarmCount (long spaceId) throws  Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getBaseAlarmModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		Criteria spaceCriteria = new Criteria();
		Condition spaceCond = new Condition();
		spaceCond.setField(resourceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(spaceId+"");
		spaceCriteria.groupOrConditions(Collections.singletonList(spaceCond));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(ViewFactory.getReadingAlarmSeverityCondition(FacilioConstants.Alarm.CLEAR_SEVERITY, false));
		Condition tillDateAlarm = ViewFactory.getOnlyTillDateAlarm();
		criteria.addAndCondition(tillDateAlarm);
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);

		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
				.module(module)
				.select(null)
				.beanClass(beanClassName)
				.andCriteria(spaceCriteria)
				.andCriteria(criteria)
				.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));

		List<? extends ModuleBaseWithCustomFields> records = builder.get();
		if (CollectionUtils.isEmpty(records)) {
			return 0;
		}
		else {
			return records.get(0).getId();
		}
	}

	public static long getV2BmsAlarmCount (long spaceId) throws  Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.BMS_ALARM);
		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getBaseAlarmModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		Criteria spaceCriteria = new Criteria();
		Condition spaceCond = new Condition();
		spaceCond.setField(resourceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(spaceId+"");
		spaceCriteria.groupOrConditions(Collections.singletonList(spaceCond));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(ViewFactory.getReadingAlarmSeverityCondition(FacilioConstants.Alarm.CLEAR_SEVERITY, false));
		Condition tillDateAlarm = ViewFactory.getOnlyTillDateAlarm();
		criteria.addAndCondition(tillDateAlarm);
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);

		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
				.module(module)
				.select(null)
				.beanClass(beanClassName)
				.andCriteria(spaceCriteria)
				.andCriteria(criteria)
				.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));

		List<? extends ModuleBaseWithCustomFields> records = builder.get();
		if (CollectionUtils.isEmpty(records)) {
			return 0;
		}
		else {
			return records.get(0).getId();
		}
	}

	public static long getFireAlarmsCount(long spaceId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getTicketsModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		Condition spaceCond = new Condition();
		spaceCond.setField(resourceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(spaceId+"");
		
		AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.andCondition(ViewFactory.getAlarmSeverityCondition(FacilioConstants.Alarm.CLEAR_SEVERITY, false))
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAlarmsModule()))
				.andCondition(spaceCond);
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) rs.get(0).get("count")).longValue();
		}
	}

	
	public static long getAssetsCount(long spaceId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		FacilioField spaceIdFld = modBean.getField("space", resourceModule.getName());

		Condition spaceCond = new Condition();
		spaceCond.setField(spaceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(spaceId+"");

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(assetModule.getTableName())
				.select(fields)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(assetModule))
				.andCondition(spaceCond)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getIsDeletedField(assetModule.getParentModule()), String.valueOf(false), BooleanOperators.IS));
		
		FacilioModule prevModule = assetModule;
		FacilioModule extendedModule = assetModule.getExtendModule();
		while(extendedModule != null) {
			select.innerJoin(extendedModule.getTableName())
					.on(prevModule.getTableName()+".ID = "+extendedModule.getTableName()+".ID");
			prevModule = extendedModule;
			extendedModule = extendedModule.getExtendModule();
		}
				
		List<Map<String, Object>> rs = select.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) rs.get(0).get("count")).longValue();
		}
	}
	
	public static List<Long> getSpaceCategoryIds(long baseSpaceID, Long buildingId) throws Exception {
		return getSpaceCategoryIds(Collections.singletonList(baseSpaceID), buildingId);
	}
	
	
	public static List<Long> getSpaceCategoryIds(List<Long> baseSpaceID, Long buildingId) throws Exception {
		
		SpaceType spacetype = null;
		if(baseSpaceID != null && !baseSpaceID.isEmpty()) {
			Long id = baseSpaceID.get(0);
			BaseSpaceContext basespace = SpaceAPI.getBaseSpace(id);
			spacetype = basespace.getSpaceTypeEnum();
		}
		List<Long> categoryIds = new ArrayList<>();
		
		if(spacetype != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);
			FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
			
			List<FacilioField> selectFields = new ArrayList<>();
			
			
			FacilioField spaceCategoryField = modBean.getField("spaceCategory", spaceModule.getName());
			FacilioField selectField = new FacilioField();
			selectField.setName(spaceCategoryField.getName());
			selectField.setDisplayName(spaceCategoryField.getDisplayName());
			selectField.setColumnName("DISTINCT("+spaceCategoryField.getColumnName()+")");
			
			selectFields.add(selectField);
			
			GenericSelectRecordBuilder newSelectBuilder = new GenericSelectRecordBuilder()
					.table(spaceModule.getTableName())
					.innerJoin(baseSpaceModule.getTableName())
					.on(spaceModule.getTableName()+".ID = "+baseSpaceModule.getTableName()+".ID")
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(spaceModule))
					.select(selectFields);
			
			if (buildingId != null && buildingId > 0) {
				newSelectBuilder.andCondition(CriteriaAPI.getCondition(baseSpaceModule.getTableName()+".BUILDING_ID", "BUILDING_ID", Long.toString(buildingId), NumberOperators.EQUALS));
			}
			
			if(spacetype.equals(SpaceType.SITE)) {
				newSelectBuilder.andCondition(CriteriaAPI.getCondition(baseSpaceModule.getTableName()+".SITE_ID", "SITE_ID", StringUtils.join(baseSpaceID, ","), NumberOperators.EQUALS));
			}
			else if(spacetype.equals(SpaceType.BUILDING)) {
				newSelectBuilder.andCondition(CriteriaAPI.getCondition(baseSpaceModule.getTableName()+".BUILDING_ID", "BUILDING_ID", StringUtils.join(baseSpaceID, ","), NumberOperators.EQUALS));
			}
			else if(spacetype.equals(SpaceType.FLOOR)) {
				newSelectBuilder.andCondition(CriteriaAPI.getCondition(baseSpaceModule.getTableName()+".FLOOR_ID", "FLOOR_ID", StringUtils.join(baseSpaceID, ","), NumberOperators.EQUALS));
			}
			else if(spacetype.equals(SpaceType.SPACE)) {
				List<Condition> conditions = new ArrayList<>();
				conditions.add(CriteriaAPI.getCondition("SPACE_ID1", "SPACE_ID1", StringUtils.join(baseSpaceID, ","), NumberOperators.EQUALS));
				conditions.add(CriteriaAPI.getCondition("SPACE_ID2", "SPACE_ID2", StringUtils.join(baseSpaceID, ","), NumberOperators.EQUALS));
				conditions.add(CriteriaAPI.getCondition("SPACE_ID3", "SPACE_ID3", StringUtils.join(baseSpaceID, ","), NumberOperators.EQUALS));
				conditions.add(CriteriaAPI.getCondition("SPACE_ID4", "SPACE_ID4", StringUtils.join(baseSpaceID, ","), NumberOperators.EQUALS));
				conditions.add(CriteriaAPI.getCondition("SPACE_ID5", "SPACE_ID5", StringUtils.join(baseSpaceID, ","), NumberOperators.EQUALS));

				Criteria criteria = new Criteria();
				criteria.groupOrConditions(conditions);
				newSelectBuilder.andCriteria(criteria);
			}
			 List<Map<String, Object>> props = newSelectBuilder.get();
			
			if(props != null) {
				for(Map<String, Object> prop :props) {
					categoryIds.add((Long)prop.get(selectField.getName()));
				}
			}
		}
		return categoryIds; 
	}
	
public static List<Map<String,Object>> getBuildingArea(String buildingList) throws Exception {
		
		FacilioField idFld = new FacilioField();
		idFld.setName("ID");
		idFld.setColumnName("ID");
		idFld.setDataType(FieldType.NUMBER);
		
		FacilioField areaFld = new FacilioField();
		areaFld.setName("AREA");
		areaFld.setColumnName("GROSS_FLOOR_AREA");
		areaFld.setDataType(FieldType.DECIMAL);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(idFld);
		fields.add(areaFld);
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Building")
				.andCustomWhere("ORGID=?", orgId)
				.andCondition(CriteriaAPI.getCondition("ID","ID", buildingList,NumberOperators.EQUALS));
		return builder.get();
	}

	public static long getIndependentSpacesCount(long baseSpaceId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder builder = new SelectRecordsBuilder()
				.select(new HashSet<>())
				.module(module)
				.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"), String.valueOf(SpaceType.SPACE.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space1"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space2"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space3"), CommonOperators.IS_EMPTY));
		;
		BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(baseSpaceId);

		if (baseSpace.getSpaceTypeEnum().getIntVal() == SpaceType.SITE.getIntVal()) {
			builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(baseSpaceId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("building"), CommonOperators.IS_EMPTY))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("floor"), CommonOperators.IS_EMPTY));
		} else if (baseSpace.getSpaceTypeEnum().getIntVal() == SpaceType.BUILDING.getIntVal()) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("building"), String.valueOf(baseSpaceId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("floor"), CommonOperators.IS_EMPTY));
		} else if (baseSpace.getSpaceTypeEnum().getIntVal() == SpaceType.FLOOR.getIntVal()) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("floor"), String.valueOf(baseSpaceId), NumberOperators.EQUALS));
		}


		List<Map<String, Object>> props = builder.getAsProps();
		long count = 0;
		if (org.apache.commons.collections.CollectionUtils.isNotEmpty(props)) {
			count = ((Number) props.get(0).get("id")).longValue();
		}
		return count;
	}

	public static List<SpaceContext> getIndependentSpaces(long baseSpaceId, Context context) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
				.select(fields)
				.module(module)
				.beanClass(SpaceContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"), String.valueOf(SpaceType.SPACE.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space1"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space2"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space3"), CommonOperators.IS_EMPTY))
				.orderBy("Resources.NAME ASC");
		;
		BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(baseSpaceId);

		if (baseSpace.getSpaceTypeEnum().getIntVal() == SpaceType.SITE.getIntVal()) {
			builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(baseSpaceId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("building"), CommonOperators.IS_EMPTY))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("floor"), CommonOperators.IS_EMPTY));
		} else if (baseSpace.getSpaceTypeEnum().getIntVal() == SpaceType.BUILDING.getIntVal()) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("building"), String.valueOf(baseSpaceId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("floor"), CommonOperators.IS_EMPTY));
		} else if (baseSpace.getSpaceTypeEnum().getIntVal() == SpaceType.FLOOR.getIntVal()) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("floor"), String.valueOf(baseSpaceId), NumberOperators.EQUALS));
		}
		boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
		if (skipModuleCriteria) {
			builder.skipModuleCriteria();
		}


		List<SpaceContext> spaceList = builder.get();
		if (CollectionUtils.isNotEmpty(spaceList)) {
			 return spaceList;
		}
		return null;
	}
	public static List<TenantUnitSpaceContext> getTenantUnitSpaceList(List<Long> idList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);

		SelectRecordsBuilder<TenantUnitSpaceContext> builder = new SelectRecordsBuilder<TenantUnitSpaceContext>()
				.module(module)
				.beanClass(TenantUnitSpaceContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), idList, NumberOperators.EQUALS));
		;

		List<TenantUnitSpaceContext> records = builder.get();
		return records;

	}

	public static long addSpaceExtentedModuleEntry(SpaceContext space, String moduleName) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		Map<String, Object> record = new HashMap<>();
		record.put("id", space.getId());
		record.put("siteId", space.getSiteId());
		record.put("moduleId", module.getModuleId());

		List<FacilioField> fieldsList = new ArrayList<>();
		fieldsList.add(FieldFactory.getIdField(module));
		fieldsList.add(FieldFactory.getSiteIdField(module));
		fieldsList.add(FieldFactory.getModuleIdField(module));
		GenericInsertRecordBuilder builder =new GenericInsertRecordBuilder()
				.fields(fieldsList).table(module.getTableName());
		return builder.insert(record);

	}

	public static SpaceCategoryContext getSpaceCategoryContext(String name) throws Exception
	{
		SpaceCategoryContext spaceCategory = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module  = modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE_CATEGORY);

		SelectRecordsBuilder<SpaceCategoryContext> builder = new SelectRecordsBuilder<SpaceCategoryContext>()
				.module(module)
				.beanClass(SpaceCategoryContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getNameCondition(name ,module));
		List<SpaceCategoryContext> spaceCategories = builder.get();
		if(spaceCategories.size() > 0)
		{
			spaceCategory = spaceCategories.get(0);
		}
		return spaceCategory;
	}
	public static SpaceCategoryContext getSpaceCategoryFromModule(long moduleId) throws Exception
	{
		SpaceCategoryContext spaceCategory = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module  = modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE_CATEGORY);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<SpaceCategoryContext> builder = new SelectRecordsBuilder<SpaceCategoryContext>()
				.module(module)
				.beanClass(SpaceCategoryContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("spaceModuleId"), String.valueOf(moduleId), NumberOperators.EQUALS));
		List<SpaceCategoryContext> spaceCategories = builder.get();
		if(CollectionUtils.isNotEmpty(spaceCategories))
		{
			spaceCategory = spaceCategories.get(0);
		}
		return spaceCategory;
	}

	public static SpaceContext getSpaceFromHierarchy(HashMap<String,Object> props, String name) throws Exception
	{
		SpaceContext space = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module  = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);


		SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
				.module(module)
				.beanClass(SpaceContext.class)
				.select(fields);
		List<String> propFields =  Arrays.asList("site","building","floor", "space1", "space2", "space3", "space4","space5");
		for (String propField: propFields) {
			if (props.containsKey(propField)) {
				HashMap<String, Object> value = (HashMap<String, Object>) props.get(propField);
				if (value != null && value.containsKey("id")) {
					Long id = (Long) value.get("id");
					builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(propField), String.valueOf(id), StringOperators.IS));
				}
			}
		}
		if (StringUtils.isNotEmpty(name)) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), name, StringOperators.IS));
		}

		List<SpaceContext> spaceList = builder.get();
		if(CollectionUtils.isNotEmpty(spaceList))
		{
			space = spaceList.get(0);
		}
		return space;
	}
	
	public static Set<Long> getBaseSpaceParentIds(Collection<Long> spaceIds) throws Exception {
		Set<Long> parentIds = new HashSet<>();
		List<BaseSpaceContext> baseSpaces = getBaseSpaces(spaceIds);
		for(BaseSpaceContext baseSpace: baseSpaces) {
			addParentIds(baseSpace, parentIds);
		}
		return parentIds;
	}
	
	public static void addParentIds(BaseSpaceContext space, Set<Long> parentIds) throws Exception {
		if(space.getSiteId() > 0 && space.getSpaceTypeEnum() != SpaceType.SITE){
			parentIds.add(space.getSiteId());
		}
		if(space.getBuildingId() > 0 && space.getSpaceTypeEnum() != SpaceType.BUILDING){
			parentIds.add(space.getBuildingId());
		}
		if(space.getFloorId() > 0 && space.getSpaceTypeEnum() != SpaceType.FLOOR){
			parentIds.add(space.getFloorId());
		}
		if(space.getSpaceId1() > 0){
			parentIds.add(space.getSpaceId1());
		}
		if(space.getSpaceId2() > 0){
			parentIds.add(space.getSpaceId2());
		}
		if(space.getSpaceId3() > 0){
			parentIds.add(space.getSpaceId3());
		}
		if(space.getSpaceId4() > 0){
			parentIds.add(space.getSpaceId4());
		}
		if(space.getSpaceId5() > 0){
			parentIds.add(space.getSpaceId5());
		}
	}
	public static void v3UpdateSiteAndBuildingId(V3SpaceContext space) throws Exception {
		boolean parentAvailable = false;
		if(space.getBuilding() != null && space.getBuilding().getId() > 0) {
			long buildingId = space.getBuilding().getId();
			BuildingContext building = SpaceAPI.getBuildingSpace(buildingId);
			space._setSiteId(building.getSiteId());
			parentAvailable = true;
		}
		if(space.getFloor() != null && space.getFloor().getId() > 0) {
			long floorId = space.getFloor().getId();
			V3FloorContext floor = SpaceAPI.getV3FloorSpace(floorId);
			space._setSiteId(floor.getSiteId());
			space.setBuilding(floor.getBuilding());
			parentAvailable = true;
		}
		if (space.getParentSpace() != null && space.getParentSpace().getId() > 0) {
			long spaceId = space.getParentSpace().getId();
			V3SpaceContext spaces = SpaceAPI.getV3Space(spaceId);
			space._setSiteId(spaces.getSiteId());
			space.setBuilding(spaces.getBuilding());
			space.setFloorId(spaces.getFloorId());

			if (spaces.getSpace4() != null && spaces.getSpaceId4() > 0) {
				space.setSpaceId5(spaceId);
				space.setSpaceId4(spaces.getSpaceId4());
				space.setSpaceId3(spaces.getSpaceId3());
				space.setSpaceId2(spaces.getSpaceId2());
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else if (spaces.getSpaceId3() != null && spaces.getSpaceId3() > 0) {
				space.setSpaceId4(spaceId);
				space.setSpaceId3(spaces.getSpaceId3());
				space.setSpaceId2(spaces.getSpaceId2());
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else if (spaces.getSpace2() != null && spaces.getSpaceId2() > 0) {
				space.setSpaceId3(spaceId);
				space.setSpaceId2(spaces.getSpaceId2());
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else if (spaces.getSpaceId1() != null && spaces.getSpaceId1() > 0) {
				space.setSpaceId2(spaceId);
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else {
				space.setSpaceId1(spaceId);
			}
			parentAvailable = true;
		}
		if (!parentAvailable && space.getSiteId() == -1) {
			throw new IllegalArgumentException("Parent Space not available. Please select parent space");
		}
	}
	
	public static void updateSiteAndBuildingId(SpaceContext space) throws Exception {
		boolean parentAvailable = false;
		if(space.getBuilding() != null && space.getBuilding().getId() > 0) {
			long buildingId = space.getBuilding().getId();
			BuildingContext building = SpaceAPI.getBuildingSpace(buildingId);
			space.setSiteId(building.getSiteId());
			parentAvailable = true;
		}
		if(space.getFloor().getId() > 0) {
			long floorId = space.getFloor().getId();
			FloorContext floor = SpaceAPI.getFloorSpace(floorId);
			space.setSiteId(floor.getSiteId());
			space.setBuilding(floor.getBuilding());
			parentAvailable = true;
		}
		if (space.getParentSpace() != null && space.getParentSpace().getId() > 0) {
			long spaceId = space.getParentSpace().getId();
			SpaceContext spaces = SpaceAPI.getSpace(spaceId);
			space.setSiteId(spaces.getSiteId());
			space.setBuilding(spaces.getBuilding());
			space.setFloorId(spaces.getFloorId());
			if (spaces.getSpaceId3() > 0) {
				space.setSpaceId4(spaceId);
				space.setSpaceId3(spaces.getSpaceId3());
				space.setSpaceId2(spaces.getSpaceId2());
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else if (spaces.getSpaceId2() > 0) {
				space.setSpaceId3(spaceId);
				space.setSpaceId2(spaces.getSpaceId2());
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else if (spaces.getSpaceId1() > 0) {
				space.setSpaceId2(spaceId);
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else {
				space.setSpaceId1(spaceId);
			}
			parentAvailable = true;
		}
		if (!parentAvailable) {
			if (space.getSite() != null && space.getSite().getId() > 0) {
				space.setSiteId(space.getSite().getId());
				return;
			}
			throw new IllegalArgumentException("Parent Space not available. Please select parent space");
		}
	}
	public static List<SiteContext> getSites(List<Long> siteIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);

		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.table(module.getTableName())
																	.select(fields)
																	.moduleName(module.getName())
																	.beanClass(SiteContext.class)
																	.andCondition(CriteriaAPI.getIdCondition(StringUtils.join(siteIds, ","),module));
		
		return (List<SiteContext>)selectBuilder.get();

	}
	public static List<SiteContext> getSitesWithoutScoping(List<Long> siteIds) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);

		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
				.table(module.getTableName())
				.select(fields)
				.moduleName(module.getName())
				.beanClass(SiteContext.class)
				.skipScopeCriteria()
				.andCondition(CriteriaAPI.getIdCondition(StringUtils.join(siteIds, ","),module));

		return (List<SiteContext>)selectBuilder.get();

	}
	public static List<SiteContext> getSitesByNameWithoutScoping(Collection<String> siteNames) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
				.table(module.getTableName())
				.select(fields)
				.moduleName(module.getName())
				.beanClass(SiteContext.class)
				.skipScopeCriteria()
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("spaceType"),Integer.valueOf(BaseSpaceContext.SpaceType.SITE.getIntVal()).toString(),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),StringUtils.join(siteNames,","),StringOperators.IS));

		return selectBuilder.get();

	}
	
}
