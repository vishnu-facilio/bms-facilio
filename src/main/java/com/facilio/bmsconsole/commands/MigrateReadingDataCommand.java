package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class MigrateReadingDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String, Object> oldData = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD);
		long oldFieldId = (long) oldData.get(FacilioConstants.ContextNames.FIELD_ID);
		long oldAssetId = (long) oldData.get("resourceId");
		long fieldId = (long) context.get(FacilioConstants.ContextNames.FIELD_ID);
		long assetId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField oldField = bean.getField(oldFieldId);
		FacilioModule oldModule = bean.getModule(oldField.getModuleId());
		
		List<FacilioField> oldModulefields = bean.getAllFields(oldModule.getName());
		Map<String, FacilioField> oldModulefieldMap = FieldFactory.getAsMap(oldModulefields);
		

		SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
				.module(oldModule)
				.beanClass(ReadingContext.class)
				.select(oldModulefields)
				.andCondition(CriteriaAPI.getCondition(oldModulefieldMap.get("parentId"), Collections.singletonList(oldAssetId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(oldField, CommonOperators.IS_NOT_EMPTY));
		
		List<ReadingContext> readingsList = builder.get();
		if (readingsList != null && !readingsList.isEmpty()) {
			
			addReading(assetId, fieldId, oldField.getName(), readingsList);
			
			ReadingsAPI.deleteReadings(oldAssetId, Collections.singletonList(oldField), oldModule, oldModulefields, oldModulefieldMap);
		}
		
		return false;
	}
	
	private void addReading (long parentId, long fieldId, String oldFieldName, List<ReadingContext> readingsList) throws Exception {
		
		List<ReadingContext> newList = new ArrayList<>();
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField newField = bean.getField(fieldId);
		FacilioModule newModule = bean.getModule(newField.getModuleId());
		
		readingsList.forEach(reading -> {
			ReadingContext newReading = new ReadingContext();
			newReading.setParentId(parentId);
			
			Object value = reading.getReading(oldFieldName);
			newReading.addReading(newField.getName(), value);
			
			newReading.setTtime(reading.getTtime());
			newList.add(newReading);
		});
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, newModule.getName());
		context.put(FacilioConstants.ContextNames.READINGS, newList);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		FacilioChain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		chain.execute(context);
	}

}
