package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetAllCategoryReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		List<Long> categoryIds = (List<Long>)context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS);
		//long parentCategoryId = (long) context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
		List<FacilioField> fields = FieldFactory.getCategoryReadingsFields(categoryReadingRelModule);
		FacilioField parentCategoryField = FieldFactory.getAsMap(fields).get("parentCategoryId");
		
		if(categoryIds != null) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(categoryReadingRelModule.getTableName())
															.andCondition(CriteriaAPI.getCondition(parentCategoryField, categoryIds, PickListOperators.IS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			System.out.println(">>>>>>>>>>>> props : "+props);
			
			List<FacilioModule> readings = new ArrayList<>();
			Map<Long,List<FacilioModule>> moduleMap = new HashMap();
			if(props != null && !props.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for(Map<String, Object> prop : props) {
					FacilioModule readingModule = modBean.getModule((long) prop.get("readingModuleId"));
					readings.add(readingModule);
					Long categoryId = (Long)prop.get("parentCategoryId");
					List<FacilioModule> modList = moduleMap.get(categoryId);
					if(modList == null) {
						modList = new ArrayList<>();
						moduleMap.put(categoryId, modList);
					}
					modList.add(readingModule);
				}
			}
			if(categoryReadingRelModule.getName().equals("spacecategoryreading")) {
				List<FacilioModule> defaultReadings = SpaceAPI.getDefaultReadings(SpaceType.SPACE, true);
				readings.addAll(defaultReadings);
				moduleMap.put(-1l, defaultReadings);
			}
			
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
			context.put(FacilioConstants.ContextNames.MODULE_MAP, moduleMap);
		}
		else if(categoryReadingRelModule.getName().equals("spacecategoryreading")) {
			context.put(FacilioConstants.ContextNames.MODULE_LIST, SpaceAPI.getDefaultReadings(SpaceType.SPACE, true));
		}
		
		return false;
	}
	
}
