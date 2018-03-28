package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetAllSpaceTypeReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		//FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		
		String type = (String)context.get(FacilioConstants.ContextNames.SPACE_TYPE);
		List<Long> ids = new ArrayList();
		Map<Long, BaseSpaceContext> spaces = new HashMap<>();
		SpaceType spaceType = null;
		if(type.equalsIgnoreCase("Buildings"))
		{
			List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
			for(int i=0; i<buildings.size();i++)
			{
				BuildingContext building = buildings.get(i);
				ids.add(building.getId());
				spaces.put(building.getId(), building);
			}
			spaceType = SpaceType.BUILDING;
		}
		
		List<FacilioField> fields = FieldFactory.getBasespaceReadingsFields();
		FacilioField spaceField = FieldFactory.getAsMap(fields).get("spaceId");
		FacilioModule module = ModuleFactory.getBasespaceReadingsModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(spaceField, StringUtils.join(ids,","), PickListOperators.IS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			System.out.println(">>>>>>>>>>>> props : "+props);
			
			List<FacilioModule> readings = new ArrayList<>();
			Map<String,List<FacilioModule>> moduleMap = new HashMap<>();
			if(props != null && !props.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for(Map<String, Object> prop : props) {
					System.out.println(">>>>>>>>>>>> prop : "+prop.containsKey("readingId"));
					Long readingId = (Long) prop.get("readingId");
					FacilioModule readingModule = modBean.getModule(readingId);
					readings.add(readingModule);
					Long spaceId = (Long)prop.get("spaceId");
					BaseSpaceContext baseSpace = spaces.get(spaceId);
					String name = baseSpace.getName();
					List<FacilioModule> modList = moduleMap.get(name);
					if(modList == null) {
						modList = new ArrayList<>();
						System.out.print("#### Name : "+name+", modList : "+modList);
						moduleMap.put(name, modList);
					}
					modList.add(readingModule);
				}
			}
			List<FacilioModule> defaultReadings = SpaceAPI.getDefaultReadings(spaceType);
			if (defaultReadings != null) {
				moduleMap.put("All", defaultReadings);
				readings.addAll(defaultReadings);
			}
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
			context.put(FacilioConstants.ContextNames.MODULE_MAP, moduleMap);
			/*if(categoryReadingRelModule.getName().equals("spacecategoryreading")) {
				List<FacilioModule> defaultReadings = SpaceAPI.getDefaultReadings(SpaceType.SPACE);
				readings.addAll(defaultReadings);
				moduleMap.put(-1l, defaultReadings);
			}
			
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
			context.put(FacilioConstants.ContextNames.MODULE_MAP, moduleMap);
		
		else if(categoryReadingRelModule.getName().equals("spacecategoryreading")) {
			context.put(FacilioConstants.ContextNames.MODULE_LIST, SpaceAPI.getDefaultReadings(SpaceType.SPACE));
		}
		*/
		return false;
	}
}
