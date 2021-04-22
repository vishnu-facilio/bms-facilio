package com.facilio.bmsconsole.commands.data;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

;

public class PopulateImportProcessCommand extends FacilioCommand {

	private static Logger LOGGER = LogManager.getLogger(PopulateImportProcessCommand.class.getName());

	@Override
	public boolean executeCommand(Context c) throws Exception{

		ImportProcessContext importProcessContext = (ImportProcessContext) c.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		c.get(ImportAPI.ImportProcessConstants.FIELDS_MAPPING);
		StringBuilder emailMessage = new StringBuilder();
		// ArrayListMultimap<String, ReadingContext> groupedReadingContext = (ArrayListMultimap<String, ReadingContext>) c.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		HashMap<String, List<ReadingContext>> groupedReadingContext = (HashMap<String, List<ReadingContext>>) c.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);

		ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) c.get(FacilioConstants.ContextNames.RECORD_LIST);
		String moduleName = "";
		if(importProcessContext.getModule() == null){
			FacilioModule bimModule = ModuleFactory.getBimImportProcessMappingModule();
			List<FacilioField> bimFields = FieldFactory.getBimImportProcessMappingFields();
			BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(bimModule,bimFields,importProcessContext.getId());
			boolean isBim = (bimImport!=null);
			if(isBim){
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(importProcessContext.getImportJobMeta());
				moduleName = ((JSONObject)json.get("moduleInfo")).get("module").toString();
			}
		}else{
			moduleName = importProcessContext.getModule().getName();
		}

		JSONObject meta = new JSONObject();
		Integer Setting = importProcessContext.getImportSetting();
		List<Long> listOfIds = new ArrayList<>();

		if(Setting == ImportProcessContext.ImportSetting.INSERT.getValue()) {
				Integer totalSize = 0;
				List<String> keys = new ArrayList(groupedReadingContext.keySet());

				for(int i=0; i<keys.size(); i++) {
					listOfIds = populateData(groupedReadingContext.get(keys.get(i)),keys.get(i),importProcessContext);
					totalSize =  totalSize + groupedReadingContext.get(keys.get(i)).size();
				}
				if(!listOfIds.isEmpty()) {
					for(Long id: listOfIds) {
						recordsList.put(moduleName, id);
					}
				}
				if(!importProcessContext.getImportJobMetaJson().isEmpty()) {
					meta = importProcessContext.getFieldMappingJSON();
					meta.put("Inserted", totalSize + "");
				}
				else {
					meta.put("Inserted", totalSize + "");
				}
				importProcessContext.setImportJobMeta(meta.toJSONString());
				emailMessage.append(",Inserted:" + groupedReadingContext.size() +"Updated:"+ 0 +",Skipped:" +0);
				c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
		}

		else if (Setting == ImportProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
			List<ReadingContext> readingsList = groupedReadingContext.get(moduleName);
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

			listOfIds = populateData(newItems,moduleName,importProcessContext);
			if(!listOfIds.isEmpty()) {
				for(Long id: listOfIds) {
					recordsList.put(moduleName, id);
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
			List<ReadingContext> readingsList = groupedReadingContext.get(moduleName);
			listOfIds = updateData(importProcessContext,readingsList);
			if (CollectionUtils.isNotEmpty(listOfIds)) {
				for (Long id : listOfIds) {
					recordsList.put(moduleName, id);
				}
			}
			c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
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
			List<ReadingContext> readingsList = groupedReadingContext.get(moduleName);
			updateNotNull(importProcessContext, readingsList);
			listOfIds = updateNotNull(importProcessContext,readingsList);
			if (CollectionUtils.isNotEmpty(listOfIds)) {
				for (Long id : listOfIds) {
					recordsList.put(moduleName, id);
				}
			}
			c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
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
			List<ReadingContext> readingsList = groupedReadingContext.get(moduleName);
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

			listOfIds = populateData(insertItems,moduleName,importProcessContext);
			if(!listOfIds.isEmpty()) {
				for(Long id: listOfIds) {
					recordsList.put(moduleName, id);
				}
			}
			listOfIds = updateData(importProcessContext,updateItems);
			if(CollectionUtils.isNotEmpty(listOfIds)) {
				for(Long id: listOfIds) {
					recordsList.put(moduleName, id);
				}
			}
			c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
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
			List<ReadingContext> readingsList = groupedReadingContext.get(moduleName);
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

			listOfIds = populateData(insertItems,moduleName,importProcessContext);
			if(!listOfIds.isEmpty()) {
				for(Long id: listOfIds) {
					recordsList.put(moduleName, id);
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
		// c.put(FacilioConstants.ContextNames.SPACE_TYPE, moduleName+"s");
		c.put(ImportAPI.ImportProcessConstants.EMAIL_MESSAGE, emailMessage);
//		ImportAPI.updateImportProcess(importProcessContext);

		return false;
	}

	public static List<Long> updateData(ImportProcessContext importProcessContext, List<ReadingContext> readingsEntireList) throws Exception {
		String moduleName = importProcessContext.getModule().getName();
		int insertLimit = 10000;
		int splitSize = (readingsEntireList.size()/insertLimit) +1;
		List<Long> listOfIds = new ArrayList<>();
		LOGGER.info("Update Data: splitSize ----- "+splitSize);
		for(int i=0 ; i < splitSize; i++) {

			int fromValue = i*insertLimit;
			int toValue = (i*insertLimit) + insertLimit;
			if(toValue >= readingsEntireList.size()) {
				toValue = readingsEntireList.size();
			}
			List<ReadingContext> readingsList = readingsEntireList.subList(fromValue , toValue);
			LOGGER.debug(MessageFormat.format("No. of records in this chunk -- {0}", readingsList == null ? "null" : readingsEntireList.size()));
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = bean.getModule(importProcessContext.getModuleId());
			ArrayList<String> updateFields = new ArrayList();
			JSONObject meta = importProcessContext.getImportJobMetaJson();
			FacilioField siteField = FieldFactory.getSiteIdField(importProcessContext.getModule());

			if(meta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS) != null) {
				updateFields = (ArrayList<String>) meta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS);
			}
			else {
				updateFields.add("name");
			}
			List<FacilioField> fields = bean.getAllFields(moduleName);
			LOGGER.debug(MessageFormat.format("Going to update the following fields \n {0}", fields));
			for(int j =0;j< readingsList.size();j++) {
				LOGGER.debug(MessageFormat.format("Update {1} -- {0}", j, moduleName));
				UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
						.table(module.getTableName())
						.moduleName(moduleName).
						fields(fields);
				if (importProcessContext.getSiteId() > 0) {
					updateBuilder.andCondition(CriteriaAPI.getCondition(siteField, String.valueOf(importProcessContext.getSiteId()), NumberOperators.EQUALS));
				}
				for(String updateField : updateFields) {
					String modulePlusFields[] = updateField.split("__");
					FacilioField facilioField = bean.getField(modulePlusFields[modulePlusFields.length - 1], moduleName);
					if(facilioField == null && modulePlusFields[modulePlusFields.length - 1].equals("site")) {
						facilioField = siteField;
					}
					updateBuilder = appendUpdateBuilderConditions(readingsList, j, updateBuilder, facilioField, module);
				}
				List<SupplementRecord> supplements = fields.stream().filter(f -> f.getDataTypeEnum().isMultiRecord())
						.map(f -> (SupplementRecord) f)
						.collect(Collectors.toList());
				if(CollectionUtils.isNotEmpty(supplements)) {
					updateBuilder.updateSupplements(supplements);
				}
				updateBuilder.update(readingsList.get(j));
				Map<Long, List<UpdateChangeSet>> recordChanges = updateBuilder.getChangeSet();
				if (MapUtils.isNotEmpty(recordChanges)) {
					recordChanges.forEach((key, value) -> listOfIds.add(key));
				}
			}
		}
		return listOfIds;
}

	private static UpdateRecordBuilder<ReadingContext> appendUpdateBuilderConditions(List<ReadingContext> readingsList, int j, UpdateRecordBuilder<ReadingContext> updateBuilder, FacilioField facilioField, FacilioModule module) {

		if(facilioField.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
			Map<String, Object> lookupField = (Map<String,Object>) readingsList.get(j).getData().get(facilioField.getName());
			Long lookupId = (Long)lookupField.get("id");
			updateBuilder.andCondition(CriteriaAPI.getCondition(facilioField,String.valueOf(lookupId), NumberOperators.EQUALS));
		}
		else if(facilioField.getDataType() == FieldType.ENUM.getTypeAsInt()) {
			String enumString = (String) readingsList.get(j).getData().get(facilioField.getName());
			EnumField enumField = (EnumField) facilioField;
			updateBuilder.andCondition(CriteriaAPI.getCondition(facilioField,String.valueOf(enumField.getIndex(enumString)), NumberOperators.EQUALS));
		} 
		else if (StringUtils.equals(facilioField.getName(), "id")) {
			updateBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), String.valueOf(readingsList.get(j).getId()), NumberOperators.EQUALS));
		} 
		else if (StringUtils.equals(facilioField.getName(), "localId")) {
			updateBuilder.andCondition(CriteriaAPI.getCondition(facilioField, String.valueOf(readingsList.get(j).getLocalId()), NumberOperators.EQUALS));
		} 
		else if (StringUtils.equals(facilioField.getName(), "siteId")) {
			updateBuilder.andCondition(CriteriaAPI.getCondition(facilioField, String.valueOf(readingsList.get(j).getSiteId()), NumberOperators.EQUALS));
		} 
		else if (facilioField.getDataType() == FieldType.NUMBER.getTypeAsInt()) {
			updateBuilder.andCondition(CriteriaAPI.getCondition(facilioField, String.valueOf(readingsList.get(j).getData().get(facilioField.getName())), NumberOperators.EQUALS));
		}
		else {
			updateBuilder.andCondition(CriteriaAPI.getCondition(facilioField, String.valueOf(readingsList.get(j).getData().get(facilioField.getName())), StringOperators.IS));
		}
		updateBuilder.withChangeSet(ReadingContext.class);
		return updateBuilder;
	}

	public static List<Long> updateNotNull(ImportProcessContext importProcessContext, List<ReadingContext> readingsEntireList ) throws Exception {
		int insertLimit = 10000;
		int splitSize = (readingsEntireList.size()/insertLimit) +1;
		List<Long> listOfIds = new ArrayList<>();
		LOGGER.info("splitSize ----- "+splitSize);
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
					updateBuilder = appendUpdateBuilderConditions(readingsList, j, updateBuilder, facilioField, module);
				}

				updateBuilder.table(module.getTableName())
					.module(module)
					.fields(tobeUpdated);
				updateBuilder.update(readingsList.get(j));
				Map<Long, List<UpdateChangeSet>> recordChanges = updateBuilder.getChangeSet();
				if (MapUtils.isNotEmpty(recordChanges)) {
					recordChanges.forEach((key, value) -> listOfIds.add(key));
				}

			}
			}
		return listOfIds;
	}

	public static List<Long> populateData(List<ReadingContext> readingsEntireList,String moduleName,ImportProcessContext importProcessContext) throws Exception {
		List<Long> listOfIds = new ArrayList<>();
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = bean.getModule(moduleName);

		FacilioModule bimModule = ModuleFactory.getBimImportProcessMappingModule();
		List<FacilioField> bimFields = FieldFactory.getBimImportProcessMappingFields();
		BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(bimModule,bimFields,importProcessContext.getId());
		boolean isBim = (bimImport!=null);

		if(module == null){

			if(isBim && moduleName.equals("zonespacerel")){
				for(ReadingContext reading:readingsEntireList){
					SpaceAPI.addZoneChildren(Long.parseLong(reading.getReading("zoneId").toString()), Collections.singletonList(Long.parseLong(reading.getReading("basespaceId").toString())));
				}
			}
		}else{

			if(isBim && moduleName.equals(FacilioConstants.ContextNames.ASSET_CATEGORY)){
				for(ReadingContext reading:readingsEntireList){
					AssetCategoryContext assetCategory = new AssetCategoryContext();
					assetCategory.setName(reading.getReading("name").toString());
					assetCategory.setType(Integer.parseInt(reading.getReading("type").toString()));

					if(assetCategory.getName() == null || assetCategory.getName().isEmpty()) {
						if(assetCategory.getDisplayName() != null && !assetCategory.getDisplayName().isEmpty()) {
							assetCategory.setName(assetCategory.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
						}
					}
					FacilioContext context = new FacilioContext();
					context.put(FacilioConstants.ContextNames.RECORD, assetCategory);
					FacilioChain addAssetCategoryChain = FacilioChainFactory.getAddAssetCategoryChain();
					addAssetCategoryChain.execute(context);
				}
			}else{

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
			FacilioChain importDataChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			importDataChain.execute(context);
		}
		else {

			int insertLimit = 10000;
			int splitSize = (readingsEntireList.size()/insertLimit) + 1;
			LOGGER.info("splitSize ----- "+splitSize);
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
						reading.addReading("sourceType", TicketContext.SourceType.WEB_ORDER.getIndex());
					}
				}
				String tableName = module.getTableName();
				List<FacilioField> fields =  bean.getAllFields(moduleName);
				InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>();
				readingBuilder.moduleName(moduleName)
						.table(tableName)
						.fields(fields)
						.addRecords(readingsList);

				List<SupplementRecord> supplements = fields.stream().filter(f -> f.getDataTypeEnum().isMultiRecord())
						.map(f -> (SupplementRecord) f)
						.collect(Collectors.toList());
				if(CollectionUtils.isNotEmpty(supplements)) {
					readingBuilder.insertSupplements(supplements);
				}

				if(ModuleLocalIdUtil.isModuleWithLocalId(module)) {
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
		if (field.getDataTypeEnum().equals(FieldType.LOOKUP)) {
			Map<String, Object> lookupField = (Map<String, Object>) readingContext.getData().get(field.getName());
			Long Id = (Long) lookupField.get("id");
			selectBuilder.andCondition(CriteriaAPI.getCondition(field, String.valueOf(Id), NumberOperators.EQUALS));
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