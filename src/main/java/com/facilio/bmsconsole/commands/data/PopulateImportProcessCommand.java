package com.facilio.bmsconsole.commands.data;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PopulateImportProcessCommand implements Command {
	
	private static Logger LOGGER = Logger.getLogger(PopulateImportProcessCommand.class.getName());
	
	@Override
	public boolean execute(Context c) throws Exception{
		
		ImportProcessContext importProcessContext = (ImportProcessContext) c.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		c.get(ImportAPI.ImportProcessConstants.FIELDS_MAPPING);
		StringBuilder emailMessage = new StringBuilder();
		ArrayListMultimap<String,String> groupedFields = (ArrayListMultimap<String,String>) c.get(ImportAPI.ImportProcessConstants.GROUPED_FIELDS);
		ArrayListMultimap<String, ReadingContext> groupedReadingContext = (ArrayListMultimap<String, ReadingContext>) c.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) c.get(FacilioConstants.ContextNames.RECORD_LIST);
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		JSONObject meta = new JSONObject();	
		Integer Setting = importProcessContext.getImportSetting();
		List<Long> listOfIds = new ArrayList<>();
		
		if(Setting == ImportProcessContext.ImportSetting.INSERT.getValue()) {
				List<String> keys = new ArrayList(groupedFields.keySet());
				
				for(int i=0; i<keys.size(); i++) {
					listOfIds = populateData(groupedReadingContext.get(keys.get(i)),keys.get(i));
				}
				if(!listOfIds.isEmpty()) {
					for(Long id: listOfIds) {
						recordsList.put(importProcessContext.getModule().getName(), id);
					}
				}
				if(!importProcessContext.getImportJobMetaJson().isEmpty()) {
					meta = importProcessContext.getFieldMappingJSON();
					meta.put("Inserted", groupedReadingContext.size()+"");
				}
				else {
					meta.put("Inserted", groupedReadingContext.size()+"");
				}
				importProcessContext.setImportJobMeta(meta.toJSONString());
				emailMessage.append(",Inserted:" + groupedReadingContext.size() +"Updated:"+ 0 +",Skipped:" +0);
				c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
		}
		
		else if (Setting == ImportProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
			List<ReadingContext> readingsList = groupedReadingContext.get(importProcessContext.getModule().getName());
			List<ReadingContext> newItems = new ArrayList<ReadingContext>();
			
			meta = importProcessContext.getImportJobMetaJson();
			ArrayList<String> insertFields = (ArrayList<String>)meta.get(ImportAPI.ImportProcessConstants.INSERT_FIELDS);

			for(ReadingContext readingContext : readingsList) {
				ReadingContext inDataBase = getAssetNames(importProcessContext,insertFields, readingContext);
				if(inDataBase == null) {
					newItems.add(readingContext);
				}
				else {
					continue;
				}
			}
			
			listOfIds = populateData(newItems,importProcessContext.getModule().getName());
			if(!listOfIds.isEmpty()) {
				for(Long id: listOfIds) {
					recordsList.put(importProcessContext.getModule().getName(), id);
				}
			}
			c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
			Integer Skipped = readingsList.size()-newItems.size();
			
			if(!importProcessContext.getImportJobMetaJson().isEmpty()) {
				meta = importProcessContext.getFieldMappingJSON();
				meta.put("Inserted", newItems.size()+"");
				meta.put("Skipped", Skipped+"");
			}
			else {
				meta.put("Inserted", newItems.size()+"");
				meta.put("Skipped", Skipped+"");
			}
			importProcessContext.setImportJobMeta(meta.toJSONString());
			emailMessage.append(",Inserted:" + newItems.size() +"Updated:"+ 0 +",Skipped:" + Skipped);
		}
		
		else if(Setting == ImportProcessContext.ImportSetting.UPDATE.getValue()) {
			List<ReadingContext> readingsList = groupedReadingContext.get(importProcessContext.getModule().getName());
			updateData(importProcessContext, readingsList);
			if(!importProcessContext.getImportJobMetaJson().isEmpty()) {
				meta = importProcessContext.getImportJobMetaJson();
				meta.put("Updated", readingsList.size());
			}
			else {
				meta.put("Updated", readingsList.size());
			}
			importProcessContext.setImportJobMeta(meta.toJSONString());
			emailMessage.append(",Inserted:" + 0 +"Updated:"+ readingsList.size()+",Skipped:" +0);
			
		}
		else if(Setting  == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue()) {
			List<ReadingContext> readingsList = groupedReadingContext.get(importProcessContext.getModule().getName());
			updateNotNull(importProcessContext, readingsList);
			if(!importProcessContext.getImportJobMetaJson().isEmpty()) {
				meta = importProcessContext.getImportJobMetaJson();
				meta.put("Updated", readingsList.size());
			}
			else {
				meta.put("Updated", readingsList.size());
			}
			importProcessContext.setImportJobMeta(meta.toJSONString());
			emailMessage.append(",Inserted:" + 0 +"Updated:"+ readingsList.size()+",Skipped:" +0);
			
		}
		
		else if(Setting == ImportProcessContext.ImportSetting.BOTH.getValue()) {
			List<ReadingContext> readingsList = groupedReadingContext.get(importProcessContext.getModule().getName());
			List<ReadingContext> insertItems = new ArrayList<ReadingContext>();
			List<ReadingContext> updateItems = new ArrayList<ReadingContext>();
			JSONObject importMeta= importProcessContext.getImportJobMetaJson();
			ArrayList<String> updateFields = (ArrayList<String>) importMeta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS);
			
			for(ReadingContext readingContext : readingsList) {
				ReadingContext inDataBase = getAssetNames(importProcessContext,updateFields, readingContext);
				if(inDataBase == null) {
					insertItems.add(readingContext);
				}
				else {
					updateItems.add(readingContext);
				}
			}
			
			listOfIds = populateData(insertItems,importProcessContext.getModule().getName());
			if(!listOfIds.isEmpty()) {
				for(Long id: listOfIds) {
					recordsList.put(importProcessContext.getModule().getName(), id);
				}
			}
			c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
			
			updateData(importProcessContext,updateItems);
			if(!importProcessContext.getImportJobMetaJson().isEmpty()) {
				meta = importProcessContext.getImportJobMetaJson();
				meta.put("Inserted", insertItems.size());
				meta.put("Updated", updateItems.size());
			}
			else {
				meta.put("Inserted", insertItems.size());
				meta.put("Updated", updateItems.size());
			}
			importProcessContext.setImportJobMeta(meta.toJSONString());
			emailMessage.append(",Inserted:" + insertItems.size() +"Updated:"+ updateItems.size() +",Skipped:" +0);
		}
		
		else if(Setting == ImportProcessContext.ImportSetting.BOTH_NOT_NULL.getValue()) {
			List<ReadingContext> readingsList = groupedReadingContext.get(importProcessContext.getModule().getName());
			List<ReadingContext> insertItems = new ArrayList<ReadingContext>();
			List<ReadingContext> updateItems = new ArrayList<ReadingContext>();
			JSONObject importMeta = importProcessContext.getImportJobMetaJson();
			
			ArrayList<String> updateFields = (ArrayList<String>) importMeta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS);
			
			for(ReadingContext readingContext : readingsList) {
				ReadingContext inDataBase = getAssetNames(importProcessContext,updateFields, readingContext);
				if(inDataBase == null) {
					insertItems.add(readingContext);
				}
				else {
					updateItems.add(readingContext);
				}
			}
			
			listOfIds = populateData(insertItems,importProcessContext.getModule().getName());
			if(!listOfIds.isEmpty()) {
				for(Long id: listOfIds) {
					recordsList.put(importProcessContext.getModule().getName(), id);
				}
			}
			c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
			updateNotNull(importProcessContext,updateItems);
			if(!importProcessContext.getImportJobMetaJson().isEmpty()) {
				meta = importProcessContext.getImportJobMetaJson();
				meta.put("Inserted", insertItems.size());
				meta.put("Updated", updateItems.size());
			}
			else {
				meta.put("Inserted", insertItems.size());
				meta.put("Updated", updateItems.size());
			}
			importProcessContext.setImportJobMeta(meta.toJSONString());
			emailMessage.append(",Inserted:" + insertItems.size() +"Updated:"+ updateItems.size() +",Skipped:" +0);
		
		}
		// c.put(FacilioConstants.ContextNames.SPACE_TYPE, importProcessContext.getModule().getName()+"s");
		c.put(ImportAPI.ImportProcessConstants.EMAIL_MESSAGE, emailMessage);
//		ImportAPI.updateImportProcess(importProcessContext);
		
		return false;
	}
	
	public static void updateData(ImportProcessContext importProcessContext, List<ReadingContext> readingsEntireList) throws Exception {
		String moduleName = importProcessContext.getModule().getName();
		int insertLimit = 10000;
		int splitSize = (readingsEntireList.size()/insertLimit) +1;
		LOGGER.severe("splitSize ----- "+splitSize);
		for(int i=0 ; i < splitSize; i++) {
			
			int fromValue = i*insertLimit;
			int toValue = (i*insertLimit) + insertLimit;
			if(toValue >= readingsEntireList.size()) {
				toValue = readingsEntireList.size();
			}
			List<ReadingContext> readingsList = readingsEntireList.subList(fromValue , toValue);
			
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			bean.getModule(importProcessContext.getModuleId());
			ArrayList<String> updateFields = new ArrayList();
			JSONObject meta = importProcessContext.getImportJobMetaJson();
			
			if(meta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS) != null) {
				updateFields = (ArrayList<String>) meta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS);
			}
			else {
				updateFields.add("name");
			}
			
			for(int j =0;j< readingsList.size();j++) {
				UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
						.table(moduleName)
						.moduleName(moduleName).
						fields(bean.getAllFields(moduleName));
				for(String updateField : updateFields) {
					String modulePlusFields[] = updateField.split("__");
					FacilioField facilioField = bean.getField(modulePlusFields[modulePlusFields.length - 1], moduleName);
					String columnName = facilioField.getColumnName();
					
					if(facilioField.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
						Map<String, Object> lookupField = (Map<String,Object>) readingsList.get(j).getData().get(facilioField.getName());
						Long lookupId = (Long)lookupField.get("id");
						updateBuilder.andCustomWhere(columnName + "=?", lookupId);
					}
					else if(facilioField.getDataType() == FieldType.ENUM.getTypeAsInt()) {
						String enumString = (String) readingsList.get(j).getData().get(facilioField.getName());
						EnumField enumField = (EnumField) facilioField;
						updateBuilder.andCustomWhere(columnName + "=?", enumField.getIndex(enumString));
					}
					else {
						updateBuilder.andCustomWhere(columnName+"= ?", readingsList.get(j).getData().get(facilioField.getName()));
					}
			}
			updateBuilder.update(readingsList.get(j));
				}
		}
}
	
	public static void updateNotNull(ImportProcessContext importProcessContext, List<ReadingContext> readingsEntireList ) throws Exception {
		int insertLimit = 10000;
		int splitSize = (readingsEntireList.size()/insertLimit) +1;
		LOGGER.severe("splitSize ----- "+splitSize);
		for(int i=0 ; i < splitSize; i++) {
			
			int fromValue = i*insertLimit;
			int toValue = (i*insertLimit) + insertLimit;
			if(toValue >= readingsEntireList.size()) {
				toValue = readingsEntireList.size();
			}
			List<ReadingContext> readingsList = readingsEntireList.subList(fromValue , toValue);
			
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = bean.getModule(importProcessContext.getModuleId());
			List<FacilioField> facilioFields = bean.getAllFields(importProcessContext.getModule().getName());
			ArrayList<String> updateFields = new ArrayList<>();
			JSONObject meta = importProcessContext.getImportJobMetaJson();
			
			if(meta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS) != null) {
				updateFields = (ArrayList<String>) meta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS);
				
//				Map<String, String> map = importProcessContext.getFieldMapping();
//				Map<String, String> swapped = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
//
//				ArrayList<String> newUpdateList = new ArrayList<>();
//				for(String updateField :updateFields) {
//					newUpdateList.add(swapped.get(updateField).split("__")[1]);
//				}
//				updateFields = newUpdateList;
			}
			else {
				updateFields.add("name");
			}
			
			
			for(int j=0;j< readingsList.size();j++) {
				UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>();
				Map<String,Object> currentRow= readingsList.get(j).getData();
				List<FacilioField> tobeUpdated= new ArrayList<FacilioField>();
				
				for(int k=0; k<facilioFields.size();k++) {
					if(currentRow.get(facilioFields.get(k).getName())!= null) {
						tobeUpdated.add(facilioFields.get(k));
					}
					else {
						continue;
					} 
				}
				for(String field: updateFields) {
					String modulePlusFields[] = field.split("__");
					FacilioField facilioField = bean.getField(modulePlusFields[modulePlusFields.length - 1], importProcessContext.getModule().getName());
					String columnName = facilioField.getColumnName();
					
					if(facilioField.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
						Map<String, Object> lookupField = (Map<String,Object>) readingsList.get(j).getData().get(facilioField.getName());
						Long lookupId = (Long)lookupField.get("id");
						updateBuilder.andCustomWhere(columnName + "=?", lookupId);
					}
					else if(facilioField.getDataType() == FieldType.ENUM.getTypeAsInt()) {
						String enumString = (String) readingsList.get(j).getData().get(facilioField.getName());
						EnumField enumField = (EnumField) facilioField;
						updateBuilder.andCustomWhere(columnName + "=?", enumField.getIndex(enumString));
					}
					else {
						updateBuilder.andCustomWhere(columnName+"= ?", readingsList.get(j).getData().get(facilioField.getName()));
					}
					}
				
				updateBuilder.table(module.getTableName())
					.module(module)
					.fields(tobeUpdated);
				
				updateBuilder.update(readingsList.get(j));
				
			}
			}
		
	}
	
	public static List<Long> populateData(List<ReadingContext> readingsEntireList,String moduleName) throws Exception {
		List<Long> listOfIds = new ArrayList<>();
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = bean.getModule(moduleName);
		
		if(module.getTypeEnum() == ModuleType.READING 
		|| module.getTypeEnum() == ModuleType.SCHEDULED_FORMULA || 
		module.getTypeEnum() == ModuleType.LIVE_FORMULA 
		|| module.getTypeEnum() == ModuleType.SYSTEM_SCHEDULED_FORMULA) 
		{
			Map<String, List<ReadingContext>> readingMap= Collections.singletonMap(moduleName, readingsEntireList);
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.HISTORY_READINGS,true);
			context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,false);
			context.put(FacilioConstants.ContextNames.READINGS_MAP , readingMap);
			context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.IMPORT);
			Chain importDataChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			importDataChain.execute(context);	
		}
		else {
			
			int insertLimit = 10000;
			int splitSize = (readingsEntireList.size()/insertLimit) + 1;
			LOGGER.severe("splitSize ----- "+splitSize);
			List<ReadingContext> readingsList;
			for(int i=0 ; i < splitSize; i++) {
				
				int fromValue = i*insertLimit;
				int toValue = (i*insertLimit) + insertLimit;
				if(toValue >= readingsEntireList.size()) {
					toValue = readingsEntireList.size();
				}
				 readingsList = readingsEntireList.subList(fromValue , toValue);
				
				if(moduleName.equals(FacilioConstants.ContextNames.ENERGY_METER)) {
					for(ReadingContext reading :readingsList) {
						reading.addReading("resourceType", ResourceContext.ResourceType.ASSET.getValue());
					}
				}
				if(moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
					for(ReadingContext reading :readingsList) {
						reading.addReading("sourceType", TicketContext.SourceType.WEB_ORDER.getIntVal());
					}
				}
				String tableName = module.getTableName();
				
				InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>();
				readingBuilder.moduleName(moduleName)
						.table(tableName)
						.fields(bean.getAllFields(moduleName))
						.addRecords(readingsList);
				
				if(ModuleLocalIdUtil.isModuleWithLocalId(moduleName)) {
					readingBuilder.withLocalId();
				}
				readingBuilder.save();
				
				if(splitSize > 100) {
					Thread.sleep(10000L);
				}
				
				
				for( ReadingContext readingContext : readingsList) {
					Long id = readingContext.getId();
					listOfIds.add(id);
				}
				if(moduleName.equals(ImportAPI.ImportProcessConstants.SPACE_FIELD) 
						|| moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)
						|| moduleName.equals(FacilioConstants.ContextNames.ENERGY_METER)) {
					continue;
				}
				else {
				}
		
			}
		}
		return listOfIds;
	}
	
public static ReadingContext getAssetNames(ImportProcessContext importProcessContext,ArrayList<String>fieldList, ReadingContext readingContext) throws Exception {
	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	FacilioModule Module = modBean.getModule(importProcessContext.getModule().getName());

	List<FacilioField> fields = new ArrayList<>();
	for(String field : fieldList) {
		String modulePlusFields [] = field.split("__");
		FacilioField name = modBean.getField(modulePlusFields[modulePlusFields.length - 1],Module.getName());
		fields.add(name);
	}
	
	SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
			.beanClass(ReadingContext.class)
			.moduleName(Module.getName())
			.select(fields)
			.table(Module.getTableName());
	
	for(FacilioField field: fields) {
		String columnName = field.getColumnName();
		if(field.getDataTypeEnum().equals(FieldType.LOOKUP) ) {
			Map<String,Object> lookupField = (Map<String,Object>) readingContext.getData().get(field.getName());
			Long Id = (Long) lookupField.get("id");
			selectBuilder.andCustomWhere(columnName+"= ?", Id);
		}
		else {
		selectBuilder.andCustomWhere(columnName+"= ?", readingContext.getData().get(field.getName()));
		}
		}
	
	List<ReadingContext> context = selectBuilder.get();
	if(context.isEmpty()) {
		return null;
	}
	else {
		return context.get(0);
	}
}

}