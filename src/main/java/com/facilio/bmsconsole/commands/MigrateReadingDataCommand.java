package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder.GenericBatchResult;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder.BatchUpdateByIdContext;
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
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.jcraft.jsch.Logger;

import lombok.extern.log4j.Log4j;

@Log4j
public class MigrateReadingDataCommand extends FacilioCommand {
	
	long id = -1;
	long fieldId = -1;
	long parentId = -1;
	Unit unit = null;
	Unit siUnit = null;
	Unit prevUnit = null;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		id =  (long) context.get("id");
		fieldId = (long) context.get(ContextNames.FIELD_ID);
		parentId = (long) context.get(ContextNames.PARENT_ID);
		unit = (Unit) context.get(ContextNames.UNIT);
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField newField = bean.getField(fieldId);
		
		long oldFieldId = (long) context.getOrDefault(ContextNames.PREV_FIELD_ID, -1l);
		long oldAssetId = (long) context.getOrDefault(ContextNames.PREV_PARENT_ID, -1l);
		prevUnit = (Unit) context.get("prevUnit");
		if (prevUnit != null) {
			siUnit = Unit.valueOf(prevUnit.getMetric().getSiUnitId());
		}
		
		boolean unitOnlyChanged = false;
		if (oldFieldId == -1) {
			unitOnlyChanged =  (boolean) context.get("unitOnlyChanged");
			if (unitOnlyChanged) { // Only unit is being changed in commissioning
				oldFieldId= fieldId;
				oldAssetId = parentId;
			}
			else {	// New field commissioning
				migrateUnmodeledData(newField);
				return false;
			}
		}
		
		
		
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
		
		BatchResult<ReadingContext> bs = builder.getInBatches(oldModulefieldMap.get("ttime").getCompleteColumnName(), 5000);
		while (bs.hasNext()) {
			List<ReadingContext> readingsList = bs.get();
			if (readingsList != null && !readingsList.isEmpty()) {
				if (unitOnlyChanged) {
					migrateUnit(readingsList, oldField);
				}
				else {
					addReading(oldField.getName(), newField, readingsList);
					ReadingsAPI.deleteReadings(oldAssetId, Collections.singletonList(oldField), readingsList);
				}
			}
		}
		
		LOGGER.info("Migration finished for parent - " + parentId + ", field - " + fieldId);
		return false;
	}
	
	private void migrateUnmodeledData(FacilioField field) throws Exception {
		List<FacilioField> fields= FieldFactory.getUnmodeledDataFields();
		fields.add(FieldFactory.getIdField());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioModule module = ModuleFactory.getUnmodeledDataModule();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("instanceId"), String.valueOf(id), NumberOperators.EQUALS))
				;
		
		GenericBatchResult bs = builder.getInBatches(fieldMap.get("ttime").getCompleteColumnName(), 5000);
		
		Map<Long, Map<String, Integer>> readingInputValuesMap = ReadingsAPI.getReadingInputValuesMap(Collections.singletonList(id));
		Map<String, Integer> readingInputs = null;
		if (MapUtils.isNotEmpty(readingInputValuesMap)) {
			readingInputs = readingInputValuesMap.get(id);
		}
		
		List<Long> dataIds = new ArrayList<>();
		while (bs.hasNext()) {
			List<Map<String, Object>> unmodelledData = bs.get();
			if (CollectionUtils.isNotEmpty(unmodelledData)) {
				
				List<ReadingContext> readings = new ArrayList<>();
				for (Map<String, Object> data : unmodelledData) {
					Object value = data.get("value");
					if (readingInputs != null && readingInputs.containsKey(value)) {
						value = readingInputs.get(value);
					}
					
					dataIds.add((Long) data.get("id"));
					ReadingContext reading=new ReadingContext();
					reading.addReading(field.getName(), value);
					reading.setParentId(parentId);
					reading.setTtime((long) data.get("ttime"));
					readings.add(reading);
				}
				
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
			if (prevUnit != null) {
				value = UnitsUtil.convert(value, siUnit, prevUnit);
			}
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
	
	public void migrateUnit(List<ReadingContext> readings, FacilioField field) throws Exception {
		List<BatchUpdateByIdContext> batchUpdateList = new ArrayList<>();
		List<FacilioField> updateFields = Collections.singletonList(field);
		
		for (ReadingContext readingData : readings) {
			BatchUpdateByIdContext batchValue = new BatchUpdateByIdContext();
			batchValue.setWhereId(readingData.getId());
			
			Object value = readingData.getReading(field.getName());
			if (unit != null) {
				if (prevUnit != null) {
					value = UnitsUtil.convert(value, siUnit, prevUnit);
				}
				value = UnitsUtil.convertToSiUnit(value, unit);
			}
			
			batchValue.addUpdateValue(field.getName(), value);
			
			batchUpdateList.add(batchValue);
		}
		
		FacilioModule module = field.getModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(updateFields)
				;
		
		updateBuilder.batchUpdateById(batchUpdateList);
	}

}
