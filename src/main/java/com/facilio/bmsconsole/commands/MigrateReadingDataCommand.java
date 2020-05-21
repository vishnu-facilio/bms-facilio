package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder.GenericBatchResult;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.SelectRecordsBuilder.BatchResult;
import com.facilio.modules.fields.FacilioField;

public class MigrateReadingDataCommand extends FacilioCommand {
	
	long fieldId = -1;
	long parentId = -1;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		fieldId = (long) context.get(FacilioConstants.ContextNames.FIELD_ID);
		parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField newField = bean.getField(fieldId);
		
		long oldFieldId = (long) context.getOrDefault(FacilioConstants.ContextNames.PREV_FIELD_ID, -1l);
		if (oldFieldId == -1) {
			migrateUnmodeledData(context, newField);
			return false;
		}
		
		long oldAssetId = (long) context.getOrDefault(FacilioConstants.ContextNames.PREV_PARENT_ID, -1l);
		
		
		FacilioField oldField = bean.getField(oldFieldId);
		FacilioModule oldModule = bean.getModule(oldField.getModuleId());
		
		Map<String, FacilioField> oldModulefieldMap = FieldFactory.getAsMap(bean.getAllFields(oldModule.getName()));
		List<FacilioField> oldModulefields = new ArrayList<>();
		oldModulefields.add(oldModulefieldMap.get("ttime"));
		oldModulefields.add(oldField);

		SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
				.module(oldModule)
				.beanClass(ReadingContext.class)
				.select(oldModulefields)
				.andCondition(CriteriaAPI.getCondition(oldModulefieldMap.get("parentId"), Collections.singletonList(oldAssetId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(oldField, CommonOperators.IS_NOT_EMPTY));
		
		BatchResult<ReadingContext> bs = builder.getInBatches(oldModulefieldMap.get("ttime").getCompleteColumnName(), 2);
		while (bs.hasNext()) {
			List<ReadingContext> readingsList = bs.get();
			if (readingsList != null && !readingsList.isEmpty()) {
				addReading(oldField.getName(), newField, readingsList);
				List<Long> readingDataIds = readingsList.stream().map(reading -> reading.getId()).collect(Collectors.toList());
				ReadingsAPI.deleteReadings(oldAssetId, Collections.singletonList(oldField), readingDataIds);
			}
		}
		
		
		return false;
	}
	
	private void migrateUnmodeledData(Context context, FacilioField field) throws Exception {
		List<FacilioField> fields= FieldFactory.getUnmodeledDataFields();
		fields.add(FieldFactory.getIdField());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioModule module = ModuleFactory.getUnmodeledDataModule();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("instanceId"), String.valueOf(context.get("id")), NumberOperators.EQUALS))
				;
		
		GenericBatchResult bs = builder.getInBatches(fieldMap.get("ttime").getCompleteColumnName(), 2);
		
		List<Long> dataIds = new ArrayList<>();
		while (bs.hasNext()) {
			List<Map<String, Object>> unmodelledData = bs.get();
			if (CollectionUtils.isNotEmpty(unmodelledData)) {
				
				List<ReadingContext> readings = new ArrayList<>();
				unmodelledData.forEach(data -> {
					dataIds.add((Long) data.get("id"));
					ReadingContext reading=new ReadingContext();
					reading.addReading(field.getName(), data.get("value"));
					reading.setParentId(parentId);
					reading.setTtime((long) data.get("ttime"));
					readings.add(reading);
				});
				
				addOrUpdateData(field, readings);
			}
		}
		
		if (!dataIds.isEmpty()) {
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					;
			
			deleteBuilder.batchDeleteById(dataIds);
		}
	}
	
	private void addReading (String oldFieldName, FacilioField newField, List<ReadingContext> readingsList) throws Exception {
		
		List<ReadingContext> newList = new ArrayList<>();
		
		readingsList.forEach(reading -> {
			ReadingContext newReading = new ReadingContext();
			newReading.setParentId(parentId);
			
			Object value = reading.getReading(oldFieldName);
			newReading.addReading(newField.getName(), value);
			
			newReading.setTtime(reading.getTtime());
			newList.add(newReading);
		});
		
		addOrUpdateData(newField, newList);
	}
	
	private void addOrUpdateData(FacilioField field, List<ReadingContext> readings) throws Exception {
		FacilioChain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, field.getModule().getName());
		context.put(FacilioConstants.ContextNames.READINGS, readings);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		context.put(FacilioConstants.ContextNames.HISTORY_READINGS, true);
		context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,false);
		chain.execute();
	}

}
