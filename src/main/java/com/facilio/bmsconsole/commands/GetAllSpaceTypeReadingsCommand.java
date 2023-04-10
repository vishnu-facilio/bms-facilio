package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllSpaceTypeReadingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		//FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		
		String type = (String)context.get(FacilioConstants.ContextNames.SPACE_TYPE);
		List<Long> ids = new ArrayList();
		Map<Long, BaseSpaceContext> spaces = new HashMap<>();
		SpaceType spaceType = null;
		
		SpaceType typeEnum = (SpaceType)context.get(FacilioConstants.ContextNames.SPACE_TYPE_ENUM);
		
		if("Sites".equalsIgnoreCase(type) || (typeEnum != null && typeEnum == SpaceType.SITE))
		{
			List<SiteContext> sites = SpaceAPI.getAllSites();
			for(int i=0; i<sites.size();i++)
			{
				SiteContext site = sites.get(i);
				ids.add(site.getId());
				spaces.put(site.getId(), site);
			}
			spaceType = SpaceType.SITE;
		}
		else if("Buildings".equalsIgnoreCase(type) || (typeEnum != null && typeEnum == SpaceType.BUILDING))
		{
			List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
			List<Long> parentIdList = new ArrayList<>();
			for(int i=0; i<buildings.size();i++)
			{
				BuildingContext building = buildings.get(i);
				Long parentId = building.getSiteId();
				parentIdList.add(parentId);
			}
			Map <Long, BaseSpaceContext > parentObjList = SpaceAPI.getBaseSpaceMap(parentIdList);
			for(int i=0; i<buildings.size();i++)
			{
				BuildingContext building = buildings.get(i);
				SiteContext site = new SiteContext();
				site.setId(building.getSiteId());
				if (parentObjList.get(building.getSiteId()) != null) {
					String siteName = parentObjList.get(building.getSiteId()).getName();
					site.setName(siteName);
					building.setSite(site);
					ids.add(building.getId());
					spaces.put(building.getId(), building);
				}
			}
			spaceType = SpaceType.BUILDING;

		}
		else if("Floors".equalsIgnoreCase(type) || (typeEnum != null && typeEnum == SpaceType.FLOOR))
		{
			List<FloorContext> floors = SpaceAPI.getAllFloors();
			List<Long> parentIdList = new ArrayList<>();
			for(int i=0; i<floors.size();i++)
			{
				FloorContext floor = floors.get(i);
				Long parentId = floor.getBuildingId();
				parentIdList.add(parentId);
			}
			
			Map <Long, BaseSpaceContext > parentObjList = SpaceAPI.getBaseSpaceMap(parentIdList);
			for(int i=0; i<floors.size();i++)
			{
				FloorContext floor = floors.get(i);
				BuildingContext building = new BuildingContext();
				building.setId(floor.getBuildingId());

				if (parentObjList.get(floor.getBuildingId()) == null) {
					continue;
				}

				String buildingName = parentObjList.get(floor.getBuildingId()).getName();
				building.setName(buildingName);
				floor.setBuilding(building);
				ids.add(floor.getId());
				spaces.put(floor.getId(), floor);
			}
			spaceType = SpaceType.FLOOR;
		} else if ("spaces".equalsIgnoreCase(type) || (typeEnum != null && typeEnum == SpaceType.SPACE)) {
			List<SpaceContext> spaceList = SpaceAPI.getAllSpaces();
			for (int i = 0; i < spaceList.size(); i++) {
				SpaceContext space = spaceList.get(i);
				ids.add(space.getId());
				spaces.put(space.getId(), space);
			}
			spaceType = SpaceType.SPACE;
		} else if("Weather Station".equalsIgnoreCase(type) || (typeEnum != null && typeEnum == SpaceType.WEATHER_STATION)) {
			spaceType = SpaceType.WEATHER_STATION;
		}

		List<FacilioModule> readings = new ArrayList<>();
		Map<Long, List<FacilioModule>> moduleMap = new HashMap<>();
		if(!ids.isEmpty()) {
			List<FacilioField> fields = FieldFactory.getResourceReadingsFields();
			FacilioField resourceField = FieldFactory.getAsMap(fields).get("resourceId");
			FacilioModule module = ModuleFactory.getResourceReadingsModule();


			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCondition(resourceField, StringUtils.join(ids, ","), PickListOperators.IS));

			List<Map<String, Object>> props = selectBuilder.get();

			System.out.println(">>>>>>>>>>>> props : " + props);


			if (props != null && !props.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for (Map<String, Object> prop : props) {
					Long readingId = (Long) prop.get("readingId");
					FacilioModule readingModule = modBean.getModule(readingId);
					readings.add(readingModule);
					Long spaceId = (Long) prop.get("resourceId");
					BaseSpaceContext baseSpace = spaces.get(spaceId);
					//String name = baseSpace.getName();
					Long id = baseSpace.getId();
					List<FacilioModule> modList = moduleMap.get(id);
					if (modList == null) {
						modList = new ArrayList<>();
						System.out.println("#### Name : " + id + ", modList : " + modList);
						moduleMap.put(id, modList);
					}
					modList.add(readingModule);
				}
			}
		}
		List<FacilioModule> defaultReadings = SpaceAPI.getDefaultReadings(spaceType, true, true);
		if (defaultReadings != null) {
			//moduleMap.put("All", defaultReadings);
			moduleMap.put(-1L, defaultReadings);
			readings.addAll(defaultReadings);
		}
		context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
		context.put(FacilioConstants.ContextNames.MODULE_MAP, moduleMap);
		context.put(FacilioConstants.ContextNames.SPACES, spaces);

		return false;
	}
}
