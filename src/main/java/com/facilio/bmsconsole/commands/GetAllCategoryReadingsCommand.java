package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.db.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllCategoryReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		if (categoryReadingRelModule.equals(ModuleFactory.getAssetCategoryReadingRelModule())) {
			assetCategoryRel(context);
		} else {
			relModuleImplementation(context, categoryReadingRelModule);
		}

		return false;
	}
	
	private void assetCategoryRel(Context context) throws Exception {
		List<Long> categoryIds = (List<Long>)context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(categoryIds != null) {
			FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
			SelectRecordsBuilder<AssetCategoryContext> builder = new SelectRecordsBuilder<AssetCategoryContext>()
					.beanClass(AssetCategoryContext.class)
					.module(assetCategoryModule)
					.select(modBean.getAllFields("assetcategory"))
					.andCondition(CriteriaAPI.getIdCondition(categoryIds, assetCategoryModule));
			
			List<AssetCategoryContext> list = builder.get();
			
			List<FacilioModule> readings = new ArrayList<>();
			Map<Long,List<FacilioModule>> moduleMap = new HashMap();
			
			Map<Long, Long> moduleIds = new HashMap<>();
			if (CollectionUtils.isNotEmpty(list)) {
				for (AssetCategoryContext assetCategoryContext : list) {
					moduleIds.put(assetCategoryContext.getAssetModuleID(), assetCategoryContext.getId());
				}
				
				List<FacilioField> subModuleRelFields = new ArrayList<>();
				subModuleRelFields.add(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER));
				subModuleRelFields.add(FieldFactory.getField("childModuleId", "CHILD_MODULE_ID", FieldType.NUMBER));
				
				GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
						.table("SubModulesRel")
						.select(subModuleRelFields)
						.andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", StringUtils.join(moduleIds.keySet(), ","), NumberOperators.EQUALS));
				List<Map<String, Object>> props = selectRecordBuilder.get();
				
				if (CollectionUtils.isNotEmpty(props)) {
					for(Map<String, Object> prop : props) {
						FacilioModule readingModule = modBean.getModule((long) prop.get("childModuleId"));
						readings.add(readingModule);
						Long categoryId = (Long)moduleIds.get(prop.get("parentModuleId"));
						List<FacilioModule> modList = moduleMap.get(categoryId);
						if(modList == null) {
							modList = new ArrayList<>();
							moduleMap.put(categoryId, modList);
						}
						modList.add(readingModule);
					}
				}
			}
			
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
			context.put(FacilioConstants.ContextNames.MODULE_MAP, moduleMap);
		}		
	}

	private void relModuleImplementation(Context context, FacilioModule categoryReadingRelModule) throws Exception {
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
	}

}
