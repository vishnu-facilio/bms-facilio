package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetCategoryReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		
		if (categoryReadingRelModule == null) {
			return false;
		}
		
		Boolean onlyReading = (Boolean) context.get(FacilioConstants.ContextNames.ONLY_READING);
		if (onlyReading == null) {
			onlyReading = false;
		}
		
		long parentCategoryId = (long) context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
		List<FacilioField> fields = FieldFactory.getCategoryReadingsFields(categoryReadingRelModule);
		
		if(parentCategoryId != -1) {
			if (categoryReadingRelModule.equals(ModuleFactory.getAssetCategoryReadingRelModule())) {
				assetCategoryRel(context, parentCategoryId);
			} else {
				relModuleImplementation(context, categoryReadingRelModule, fields, parentCategoryId);
			}
		}
		return false;
	}

	private void assetCategoryRel(Context context, long parentCategoryId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
		SelectRecordsBuilder<AssetCategoryContext> builder = new SelectRecordsBuilder<AssetCategoryContext>()
				.beanClass(AssetCategoryContext.class)
				.module(assetCategoryModule)
				.select(modBean.getAllFields("assetcategory"))
				.andCondition(CriteriaAPI.getIdCondition(parentCategoryId, assetCategoryModule));
		List<AssetCategoryContext> list = builder.get();

		List<FacilioModule> readings = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(list)) {
			AssetCategoryContext assetCategoryContext = list.get(0);
			
			List<FacilioField> subModuleRelFields = new ArrayList<>();
			subModuleRelFields.add(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER));
			subModuleRelFields.add(FieldFactory.getField("childModuleId", "CHILD_MODULE_ID", FieldType.NUMBER));
			
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
					.table("SubModulesRel")
					.select(subModuleRelFields)
					.andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", String.valueOf(assetCategoryContext.getAssetModuleID()), NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectRecordBuilder.get();
			
			if (CollectionUtils.isNotEmpty(props)) {
				for(Map<String, Object> prop : props) {
					FacilioModule readingModule = modBean.getModule((long) prop.get("childModuleId"));
					readings.add(readingModule);
				}
			}
		}
		
		List<FacilioModule> existingReadings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if (existingReadings == null) {
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
		}
		else {
			existingReadings.addAll(readings);
		}
	}

	private void relModuleImplementation(Context context, FacilioModule categoryReadingRelModule, List<FacilioField> fields, long parentCategoryId) throws Exception {
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
		
		List<FacilioModule> existingReadings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if (existingReadings == null) {
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
		}
		else {
			existingReadings.addAll(readings);
		}
	}
	
}
