package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BulkPushAssetCommands implements Command {
	
	private static Logger LOGGER = Logger.getLogger(BulkPushAssetCommands.class.getName());

	@Override
	public boolean execute(Context context)throws Exception {
		LOGGER.severe("BulkPush Asset");
		List<FacilioModule> moduleList = new ArrayList<>();
		BeanFactory.lookup("ModuleBean");
		ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<Long> assetCategoryIds = new ArrayList<>();
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		ArrayListMultimap<String,ReadingContext> categoryBasedAsset = (ArrayListMultimap<String, ReadingContext>) context.get("categoryBasedAsset");
		List<String> assetCategoryNames = new ArrayList(categoryBasedAsset.keySet());
		HashMap<String,Map<String,String>> modulesInfo = (HashMap<String,Map<String,String>>) context.get(ImportAPI.ImportProcessConstants.MODULES_INFO);
		for(int i=0;i<assetCategoryNames.size();i++) {
			String assetCategoryName = assetCategoryNames.get(i);
			List<ReadingContext> readingsList= categoryBasedAsset.get(assetCategoryName);
			String moduleTableName = modulesInfo.get(assetCategoryName).get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			String moduleName = modulesInfo.get(assetCategoryName).get(FacilioConstants.ContextNames.MODULE_NAME);
			
			if(!assetCategoryName.equals(ImportAPI.ImportProcessConstants.NO_CATEGORY_DEFINED)) {
				AssetCategoryContext category = AssetsAPI.getCategory(assetCategoryName);
				assetCategoryIds.add(category.getId());
			}
			List<Long> temp = new ArrayList<>();
			temp = populateData(importProcessContext,readingsList,moduleName,moduleTableName);
			if(assetCategoryName.equals(ImportAPI.ImportProcessConstants.NO_CATEGORY_DEFINED)) {
				assetCategoryName = FacilioConstants.ContextNames.ASSET; 
			}
			else {
				assetCategoryName= moduleName;
			}
			for(Long id: temp) {
				recordsList.put(assetCategoryName, id);
			}
			
		}
		JSONObject meta = importProcessContext.getImportJobMetaJson();
		StringBuilder emailMessage = new StringBuilder();
		if(meta == null) {
			JSONObject newMeta = new JSONObject();
			newMeta.put("Inserted", categoryBasedAsset.size());
			importProcessContext.setImportJobMeta(newMeta.toJSONString());
		}
		else {
			meta.put("Inserted", categoryBasedAsset.size());
			importProcessContext.setImportJobMeta(meta.toJSONString());
		}
		
		emailMessage.append("Added assets: " + categoryBasedAsset.size());
		context.put(ImportAPI.ImportProcessConstants.EMAIL_MESSAGE, emailMessage);
//		FacilioModule module = ModuleFactory.getAssetCategoryReadingRelModule();
//		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
//		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS, assetCategoryIds);
		
		
		// context.put(FacilioConstants.ContextNames.MODULE_LIST, subModules);
		
		context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
		
		return false;
	}

public static List<Long> populateData(ImportProcessContext importProcessContext,List<ReadingContext> readingsEntireList,String moduleName,String moduleTableName ) throws Exception {
	
	List<Long> listOfIds = new ArrayList<>();
		
		int insertLimit = 10000;
		int splitSize = (readingsEntireList.size()/insertLimit) + 1;
		LOGGER.severe("splitSize ----- "+splitSize);
		for(int i=0 ; i < splitSize; i++) {
			
			int fromValue = i*insertLimit;
			int toValue = (i*insertLimit) + insertLimit;
			if(toValue >= readingsEntireList.size()) {
				toValue = readingsEntireList.size();
			}
			List<ReadingContext> readingsList = readingsEntireList.subList(fromValue , toValue);
			
				for(ReadingContext reading :readingsList) {
					reading.addReading("resourceType", ResourceContext.ResourceType.ASSET.getValue());
				}
				
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			bean.getAllFields(moduleName);
			InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
					.table(moduleTableName)
					.moduleName(moduleName)
					.fields(bean.getAllFields(moduleName))
					.addRecords(readingsList);
			
			if(ModuleLocalIdUtil.isModuleWithLocalId(moduleName)) {
				readingBuilder.withLocalId();
			}
			readingBuilder.save();
			
			Thread.sleep(10000L);
			
			for(ReadingContext readingContext : readingsList) {
				listOfIds.add(readingContext.getId());
			}
		}
	return listOfIds;
}
}