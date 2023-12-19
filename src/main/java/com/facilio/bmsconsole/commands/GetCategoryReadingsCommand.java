package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;


public class GetCategoryReadingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule categoryReadingRelModule = (FacilioModule) context.getOrDefault(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, null);

		if (categoryReadingRelModule == null) {
			return false;
		}

		long parentCategoryId = (long) context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
		List<FacilioModule> childReadingModuleList = new ArrayList<>();

		if (parentCategoryId != -1) {
			if (categoryReadingRelModule.equals(ModuleFactory.getAssetCategoryReadingRelModule())) {
				childReadingModuleList = assetCategoryRel(context, parentCategoryId);
			} else {
				List<FacilioField> fields = FieldFactory.getCategoryReadingsFields(categoryReadingRelModule);
				childReadingModuleList = relModuleImplementation(context, categoryReadingRelModule, fields, parentCategoryId);
			}
		} else if (categoryReadingRelModule.equals(ModuleFactory.getSpaceCategoryReadingRelModule())) {
			//Assuming if parentId is passed, it will be handled in space specific readings command.
			long parentId = (long) context.getOrDefault(FacilioConstants.ContextNames.PARENT_ID, -1l);
			if (parentId == -1) {
				// This is only if space (type 4) readings are fetched with no category
				List<FacilioModule> defaultReadings = SpaceAPI.getDefaultReadings(SpaceType.SPACE, true);
				context.put(FacilioConstants.ContextNames.MODULE_LIST, defaultReadings);
			}
		}

		List<FacilioModule> existingReadings = (List<FacilioModule>) context.getOrDefault(FacilioConstants.ContextNames.MODULE_LIST, new ArrayList<>());
		existingReadings = existingReadings != null ? existingReadings : new ArrayList<>();
		existingReadings.addAll(childReadingModuleList);

		context.put(FacilioConstants.ContextNames.MODULE_LIST, existingReadings);
		return false;
	}

	private static List<FacilioModule> getReadingModulesFromSubModuleRel(Long parentModuleId, Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> readings = new ArrayList<>();

		List<FacilioField> subModuleRelFields = new ArrayList<>();
		subModuleRelFields.add(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER));
		subModuleRelFields.add(FieldFactory.getField("childModuleId", "CHILD_MODULE_ID", FieldType.NUMBER));

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table("SubModulesRel")
				.select(subModuleRelFields)
				.andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", String.valueOf(parentModuleId), NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectRecordBuilder.get();

		if (CollectionUtils.isNotEmpty(props)) {
			for (Map<String, Object> prop : props) {
				FacilioModule readingModule = modBean.getModule((long) prop.get("childModuleId"));
				readings.add(readingModule);
			}
		}

		return readings;

	}

	private List<FacilioModule> assetCategoryRel(Context context, long parentCategoryId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
		SelectRecordsBuilder<AssetCategoryContext> builder = new SelectRecordsBuilder<AssetCategoryContext>()
				.beanClass(AssetCategoryContext.class)
				.module(assetCategoryModule)
				.select(modBean.getAllFields("assetcategory"))
				.andCondition(CriteriaAPI.getIdCondition(parentCategoryId, assetCategoryModule));
		List<AssetCategoryContext> list = builder.get();


		if (CollectionUtils.isNotEmpty(list)) {
			AssetCategoryContext assetCategoryContext = list.get(0);
			return getReadingModulesFromSubModuleRel(assetCategoryContext.getAssetModuleID(), context);
		}
		return new ArrayList<>();
	}

	private List<FacilioModule> relModuleImplementation(Context context, FacilioModule categoryReadingRelModule, List<FacilioField> fields, long parentCategoryId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(categoryReadingRelModule.getTableName())
				.andCustomWhere("PARENT_CATEGORY_ID = ?", parentCategoryId);

		List<Map<String, Object>> props = selectBuilder.get();

		List<FacilioModule> readings = null;
		readings = new ArrayList<>();

		if(props != null && !props.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(Map<String, Object> prop : props) {
				readings.add(modBean.getModule((long) prop.get("readingModuleId")));
			}
		}

		return readings;
	}

}
