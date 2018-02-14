package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class SpaceAPI {
	
	private static Logger logger = Logger.getLogger(SpaceAPI.class.getName());
	
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
																	.andCondition(CriteriaAPI.getCondition(fieldName,fieldName, 
																			buildingList,NumberOperators.EQUALS));
		return selectBuilder.get();
		
		
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
																	.andCustomWhere("SITE_ID =? AND SPACE_TYPE=?",siteId,BaseSpaceContext.SpaceType.BUILDING.getIntVal());
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
		List<BuildingContext> buildings = selectBuilder.get();
		return buildings;
	}
	
	public static List<SiteContext> getAllSites() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.module(module)
																	.maxLevel(0)
																	.beanClass(SiteContext.class);
		List<SiteContext> sites = selectBuilder.get();
		return sites;
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
			return (long) result.get(0).get("count");
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
		return getZoneChildren(zoneIds);
	}
	
	private static List<BaseSpaceContext> getZoneChildren(List<Long> zoneIds) throws Exception {
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
	
	
	
	public static List<BaseSpaceContext> getAllBaseSpaces(Criteria filterCriteria, Criteria searchCriteria, String orderBy) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.beanClass(BaseSpaceContext.class);
		if (filterCriteria != null) {
			selectBuilder.andCriteria(filterCriteria);
		}
		if (searchCriteria != null) {
			selectBuilder.andCriteria(searchCriteria);
		}
		if (orderBy != null && !orderBy.isEmpty()) {
			selectBuilder.orderBy(orderBy);
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
			return (Long) rs.get(0).get("count");
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
			return (Long) rs.get(0).get("count");
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
			return (Long) rs.get(0).get("count");
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
			return (Long) rs.get(0).get("count");
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
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
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
			return (Long) rs.get(0).get("count");
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

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(assetModule.getTableName())
				.innerJoin(resourceModule.getTableName())
				.on(assetModule.getTableName()+".ID = "+resourceModule.getTableName()+".ID")
				.andCustomWhere("Assets.ORGID=?", orgId)
				.andCondition(spaceCond);
		
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return (Long) rs.get(0).get("count");
		}
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
