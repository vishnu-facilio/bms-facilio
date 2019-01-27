package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class SpaceAPI {
	
	private static Logger logger = Logger.getLogger(SpaceAPI.class.getName());
	
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
				.andCondition(CriteriaAPI.getCondition(module.getTableName()+".PARENT_SPACE", "basespaceId", baseSpaceId+"", NumberOperators.EQUALS));
		
		List<PhotosContext> photos = builder.get();
		
		return photos;
	}
	
	public static List<FacilioModule> getDefaultReadings(SpaceType type, boolean onlyReading) throws Exception {
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
		}
		
		List<FacilioModule> readings = null;
		if (onlyReading) {
			readings = modBean.getSubModules(moduleName, ModuleType.READING, ModuleType.SYSTEM_SCHEDULED_FORMULA);
		}
		else {
			readings = modBean.getSubModules(moduleName, ModuleType.READING, ModuleType.LIVE_FORMULA, ModuleType.SCHEDULED_FORMULA, ModuleType.SYSTEM_SCHEDULED_FORMULA);
		}
		return readings;
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
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(SiteContext.class)
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		List<SiteContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
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
	
	public static Long addDependentSpace(String spaceName, Long parentSpaceId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		SpaceContext spaceContext = new SpaceContext();
		spaceContext.setName(spaceName);
		SpaceContext parentSpace = SpaceAPI.getSpace(parentSpaceId);
		
		if(parentSpace.getSpaceId1() != -1) {
			spaceContext.setSpaceId1(parentSpace.getSpaceId1());
			if(parentSpace.getSpaceId2() != -1) {
				spaceContext.setSpaceId2(parentSpace.getSpaceId2());
				if(parentSpace.getSpaceId3() != -1) {
					spaceContext.setSpaceId3(parentSpace.getSpaceId3());
					if(parentSpace.getSpaceId4() != -1) {
						spaceContext.setSpaceId4(parentSpace.getSpaceId4());
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
		
		
		return spaceId;
		
	}
	public static BuildingContext getBuildingSpace(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(BuildingContext.class)
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
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
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		List<LocationContext> spaces = selectBuilder.get();
		
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
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		List<FloorContext> spaces = selectBuilder.get();
		
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
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		List<SpaceContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
	}
	
	public static BaseSpaceContext getBaseSpace(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(BaseSpaceContext.class)
																	.andCustomWhere(module.getTableName()+".ID = ?", id);
		
		List<BaseSpaceContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0);
		}
		return null;
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
				spaces.add(parentSpace);
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
							childSpaces = getSpaceChildren(parentSpace.getId());
							break;
					case ZONE:
							childSpaces = getZoneChildren(parentSpace.getId());
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
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("spaceId1"),spaceIDs , NumberOperators.EQUALS));
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("spaceId2"),spaceIDs , NumberOperators.EQUALS));
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("spaceId3"),spaceIDs , NumberOperators.EQUALS));
			
			
			SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																		.select(fields)
																		.table(module.getTableName())
																		.moduleName(module.getName())
																		.beanClass(BaseSpaceContext.class)
																		.andCriteria(criteria)
																		;
			List<BaseSpaceContext> spaces = selectBuilder.get();
			return spaces;
		}
		return null;
	}
	
	private static List<BaseSpaceContext> getSiteChildren(long siteId) throws Exception {
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
																		.andCustomWhere(ids.toString());
			List<BaseSpaceContext> spaces = selectBuilder.get();
			return spaces;
		}
		return null;
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
																	.andCustomWhere("BUILDING_ID =? AND SPACE_TYPE=?",buildingId,BaseSpaceContext.SpaceType.FLOOR.getIntVal());
		List<BaseSpaceContext> spaces = selectBuilder.get();
		return spaces;
	}
	
	public static List<BuildingContext> getSiteBuildings(long siteId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(BuildingContext.class)
																	.andCustomWhere("BaseSpace.SITE_ID =? AND SPACE_TYPE=?",siteId,BaseSpaceContext.SpaceType.BUILDING.getIntVal());
		
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(module.getName());
		if (scopeCriteria != null) {
			selectBuilder.andCriteria(scopeCriteria);
		}
		
		Criteria permissionCriteria = AccountUtil.getCurrentUser().getRole().permissionCriteria(module.getName(),"read");
		if (permissionCriteria != null) {
			selectBuilder.andCriteria(permissionCriteria);
		}
		
		List<BuildingContext> buildings = selectBuilder.get();
		return buildings;
	}
	
	
	public static List<BuildingContext> getAllBuildings() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(BuildingContext.class)
																	.andCustomWhere("SPACE_TYPE=?",BaseSpaceContext.SpaceType.BUILDING.getIntVal());
		
		
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(module.getName());
		if (scopeCriteria != null) {
			selectBuilder.andCriteria(scopeCriteria);
		}
		
		Criteria permissionCriteria = AccountUtil.getCurrentUser().getRole().permissionCriteria(module.getName(),"read");
		if (permissionCriteria != null) {
			selectBuilder.andCriteria(permissionCriteria);
		}
		
		List<BuildingContext> buildings = selectBuilder.get();
		return buildings;
	}
	
	public static List<FloorContext> getAllFloors() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FLOOR);
		
		SelectRecordsBuilder<FloorContext> selectBuilder = new SelectRecordsBuilder<FloorContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(FloorContext.class)
																	.andCustomWhere("SPACE_TYPE=?",BaseSpaceContext.SpaceType.FLOOR.getIntVal());
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
																	.andCustomWhere("SPACE_TYPE=?",BaseSpaceContext.SpaceType.ZONE.getIntVal());
		List<ZoneContext> zones = selectBuilder.get();
		return zones;
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
																	.andCustomWhere("SPACE_TYPE=?",BaseSpaceContext.SpaceType.SPACE.getIntVal());
		List<SpaceContext> spaces = selectBuilder.get();
		return spaces;
	}
	
	public static List<SiteContext> getAllSites(int lookupLevel) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(lookupLevel)
																	.beanClass(SiteContext.class);
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
																	.beanClass(SiteContext.class);
		List<SiteContext> sites = selectBuilder.get();
		return sites;
	}
	
	public static List<SiteContext> getAllSites() throws Exception {
		return getAllSites(0);
	}
	
	
public static long getSitesCount() throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Site")
				.andCustomWhere("Site.ORGID=?", orgId);
		
		List<Map<String, Object>> result = builder.get();
		if (result == null || result.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) result.get(0).get("count")).longValue();
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
																		.andCustomWhere(ids.toString());
			List<BaseSpaceContext> spaces = selectBuilder.get();
			return spaces;
		}
		return null;
	}
	
	public static List<BaseSpaceContext> getZoneChildren(long zoneId) throws Exception {
		List<Long> zoneIds = new ArrayList<>();
		zoneIds.add(zoneId);
		return getZoneChildren(zoneIds, true);
	}
	
	private static List<BaseSpaceContext> getZoneChildren(List<Long> zoneIds, Boolean isImmediate) throws Exception {
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
																		.andCustomWhere(ids.toString());
			if (isImmediate) {
				selectBuilder.andCustomWhere("Zone_Space.IS_IMMEDIATE = ?", isImmediate);
			}									
			List<BaseSpaceContext> spaces = selectBuilder.get();
			return spaces;
		}
		return null;
	}
	
	public static List<BaseSpaceContext> getBaseSpaces(List<Long> idList) throws Exception
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
	
	public static List<SpaceContext> getSpaceListOfCategory(List<Long> baseSpaceIds,long category) throws Exception
	{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		FacilioField categoryField= FieldFactory.getAsMap(fields).get("spaceCategory");
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(SpaceContext.class)
				.andCondition(CriteriaAPI.getCondition(categoryField, String.valueOf(category), PickListOperators.IS));
		
		if(baseSpaceIds != null && !baseSpaceIds.isEmpty()) {
			BaseSpaceContext basespace = getBaseSpace(baseSpaceIds.get(0));
			selectBuilder.andCustomWhere("BaseSpace."+basespace.getSpaceTypeEnum().getStringVal().toUpperCase()+"_ID in( ? )", StringUtils.join(baseSpaceIds, ","));
		}
		List<SpaceContext> spaces = selectBuilder.get();
		return spaces;
	}
	
	public static List<BaseSpaceContext> getAllBaseSpaces(Criteria filterCriteria, Criteria searchCriteria, String orderBy, JSONObject pagination, Boolean withReadings) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																	.select(fields)
																	.module(module)
																	.beanClass(BaseSpaceContext.class);
		if (filterCriteria != null) {
			selectBuilder.andCriteria(filterCriteria);
		}
		if (searchCriteria != null) {
			selectBuilder.andCriteria(searchCriteria);
		}
		// temp handling for service portal without Login
		if (AccountUtil.getCurrentUser() != null) {
			Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria("basespace");
			if(scopeCriteria != null) {
				selectBuilder.andCriteria(scopeCriteria);
			}
		}
		
		if (orderBy != null && !orderBy.isEmpty()) {
			selectBuilder.orderBy(orderBy);
		}
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
	
	public static long getIndependentSpacesCount(long siteId) throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Site")
				.innerJoin("BaseSpace")
				.on("Site.ID = BaseSpace.ID")
				.andCustomWhere("Site.ORGID=? AND BaseSpace.ORGID = ? AND BaseSpace.SITE_ID = ? AND BaseSpace.BUILDING_ID = -1 AND BaseSpace.FLOOR_ID = -1", orgId, orgId, siteId);
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) rs.get(0).get("count")).longValue();
		}
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
				.andCustomWhere("Building.ORGID=? AND BaseSpace.ORGID = ? AND BaseSpace.BUILDING_ID = ? AND BaseSpace.SPACE_TYPE = ?", orgId, orgId, buildingId, BaseSpaceContext.SpaceType.SPACE);
		
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
		System.out.println("builder -- "+builder);
		List<Long> spaceIds = new ArrayList<>();
		if (rs != null && !rs.isEmpty()) {
			for(Map<String, Object> prop:rs) {
				spaceIds.add((Long)prop.get("id")); 
			}
		}
		return spaceIds;
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

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("WorkOrders")
				.innerJoin("Tickets")
				.on("WorkOrders.ID = Tickets.ID")
				.innerJoin("TicketStatus")
				.on("Tickets.STATUS_ID = TicketStatus.ID")
				.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND TicketStatus.ORGID = ? AND TicketStatus.STATUS_TYPE = ?", orgId, orgId, orgId, TicketStatusContext.StatusType.OPEN.getIntVal())
				.andCondition(spaceCond);
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) rs.get(0).get("count")).longValue();
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
		FacilioField spaceIdFld = modBean.getField("spaceId", resourceModule.getName());

		Condition spaceCond = new Condition();
		spaceCond.setField(spaceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(spaceId+"");

		AccountUtil.getCurrentOrg().getOrgId();
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(assetModule.getTableName())
				.select(fields)
				.andCondition(spaceCond);
		
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
	
}
