package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllAssetSpecificReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		if (!ModuleFactory.getAssetCategoryReadingRelModule().equals(categoryReadingRelModule)) {
			return false;
		}
		List<FacilioField> relFields = FieldFactory.getResourceReadingsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(relFields);
		FacilioField resourceField = fieldMap.get("resourceId");
		FacilioModule module = ModuleFactory.getResourceReadingsModule();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String,FacilioField > assetFieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ASSET));
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		String assetTable = assetModule.getTableName();
		
		List<FacilioField> fields =  new ArrayList<>();
		fields.add(fieldMap.get("readingId"));
		fields.add(assetFieldsMap.get("category"));
		fields.add(FieldFactory.getField("count", "Count(Assets.Category)", FieldType.NUMBER));
		
		StringBuilder groupBy = new StringBuilder();
		groupBy.append(assetFieldsMap.get("category").getCompleteColumnName())
						.append(",")
						.append(fieldMap.get("readingId").getCompleteColumnName());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.innerJoin(assetTable)
														.on(module.getTableName()+"."+resourceField.getColumnName()+"="+assetTable+".ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(assetModule))
														.groupBy(groupBy.toString());

		List<Map<String, Object>> props = selectBuilder.get();
		
		Map<String, Long> countMap = null;
		Map<Long,List<FacilioModule>> moduleMap = (Map<Long, List<FacilioModule>>) context.get(FacilioConstants.ContextNames.MODULE_MAP);
		List<FacilioModule> readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				FacilioModule readingModule = modBean.getModule((long) prop.get("readingId"));
				readings.add(readingModule);
				Long categoryId = (Long)prop.get("category");
				if (countMap == null) {
					countMap = new HashMap<>();
				}
				countMap.put(readingModule.getName(), (Long)prop.get("count"));
				List<FacilioModule> modList = moduleMap.get(categoryId);
				if(modList == null) {
					modList = new ArrayList<>();
					moduleMap.put(categoryId, modList);
				}
				modList.add(readingModule);
			}
		}
		context.put(FacilioConstants.ContextNames.COUNT, countMap);
		
		return false;
	}

}
