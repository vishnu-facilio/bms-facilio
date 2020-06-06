package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwitchToAddResourceChain extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, List<ReadingContext>>groupedContext = (HashMap<String, List<ReadingContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		List<ReadingContext>readingsContext = new ArrayList<ReadingContext>();
		if(groupedContext != null) {
			for(List<ReadingContext> rContext: groupedContext.values()) {
				readingsContext.addAll(rContext);
			}
		}
		FacilioModule facilioModule = null;
		if(importProcessContext.getModule() !=null){
			String moduleName = importProcessContext.getModule().getName();
			facilioModule = bean.getModule(moduleName);
		}else{
			FacilioModule bimModule = ModuleFactory.getBimImportProcessMappingModule();
			List<FacilioField> bimFields = FieldFactory.getBimImportProcessMappingFields();
			
			BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(bimModule,bimFields,importProcessContext.getId());
			
			boolean isBim = (bimImport!=null);
			if(isBim){
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(importProcessContext.getImportJobMeta());
				String moduleName = ((JSONObject)json.get("moduleInfo")).get("module").toString();
				if(moduleName.equals("zonespacerel")){
					facilioModule = ModuleFactory.getZoneRelModule(); 
				}
			}
		}
		
		if(facilioModule.getName().equals(FacilioConstants.ContextNames.ASSET) ||
				(facilioModule.getExtendModule() != null &&  facilioModule.getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))
				) {

			if (!ImportAPI.isInsertImport(importProcessContext)) {
				FacilioChain c = TransactionChainFactory.getGenericImportChain();
				Long siteId = importProcessContext.getSiteId();
				c.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
				c.getContext().put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
				c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
				c.getContext().put(FacilioConstants.ContextNames.SITE, siteId);
				c.execute();
			} else {
				FacilioChain bulkAssetImportChain = TransactionChainFactory.getBulkAssetImportChain();
				bulkAssetImportChain.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
				bulkAssetImportChain.getContext().put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
				bulkAssetImportChain.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
				bulkAssetImportChain.execute();
			}
		}
		
		else if(facilioModule.getName().equals(FacilioConstants.ContextNames.SITE) 
				|| facilioModule.getName().equals(FacilioConstants.ContextNames.BUILDING) 
				|| facilioModule.getName().equals(FacilioConstants.ContextNames.FLOOR) 
				|| facilioModule.getName().equals(FacilioConstants.ContextNames.SPACE) 
				|| facilioModule.getExtendModule() != null && facilioModule.getExtendModule().getName().equals(FacilioConstants.ContextNames.BASE_SPACE)) {
			
				FacilioChain c = TransactionChainFactory.getGenericImportChain();
				c.execute(context);
		}
		
		else if(facilioModule.getName().equals(FacilioConstants.ContextNames.PURCHASED_ITEM)) {
			JSONObject importMeta = importProcessContext.getImportJobMetaJson();
			Long storeRoom;
			JSONObject moduleStaticFields = (JSONObject) importMeta.get(ImportAPI.ImportProcessConstants.MODULE_STATIC_FIELDS);
			if(moduleStaticFields != null && !moduleStaticFields.isEmpty()) {
				storeRoom = (Long)moduleStaticFields.get(FacilioConstants.ContextNames.STORE_ROOM);
				context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
			}
			
			
			List<PurchasedItemContext> items = new ArrayList<PurchasedItemContext>();
			for(ReadingContext readingContext : readingsContext) {
				Map<String, Object> temp = readingContext.getData();
				items.add(FieldUtil.getAsBeanFromMap(temp, PurchasedItemContext.class));
				
			}
			
			FacilioChain c = TransactionChainFactory.getImportItemChain();
			context.put(FacilioConstants.ContextNames.RECORD_LIST, items);
			c.execute(context);
		}
		
		else if(facilioModule.getName().equals(FacilioConstants.ContextNames.PURCHASED_TOOL)) {
			JSONObject importMeta = importProcessContext.getImportJobMetaJson();
			Long storeRoom;
			JSONObject moduleStaticFields = (JSONObject) importMeta.get(ImportAPI.ImportProcessConstants.MODULE_STATIC_FIELDS);
			if(moduleStaticFields != null && !moduleStaticFields.isEmpty()) {
				storeRoom = (Long)moduleStaticFields.get(FacilioConstants.ContextNames.STORE_ROOM);
				context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
			}
		
			List<PurchasedToolContext> tools = new ArrayList<PurchasedToolContext>();
			for(ReadingContext readingContext: readingsContext) {
				Map<String, Object> temp = readingContext.getData();
				tools.add(FieldUtil.getAsBeanFromMap(temp, PurchasedToolContext.class));
			}
			
			FacilioChain c = TransactionChainFactory.getImportToolChain();
			context.put(FacilioConstants.ContextNames.RECORD_LIST, tools);
			c.execute(context);
		} 
		else if(facilioModule.getName().equals(FacilioConstants.ContextNames.WORK_ORDER) && ImportAPI.isInsertImport(importProcessContext)) {
			
			context.put(ImportAPI.ImportProcessConstants.READINGS_CONTEXT_LIST, readingsContext);
			Long siteId = importProcessContext.getSiteId();
			context.put(FacilioConstants.ContextNames.SITE, siteId);
			FacilioChain c = TransactionChainFactory.getBulkWorkOrderImportChain();
			c.execute(context);

		} else {
			FacilioChain c = TransactionChainFactory.getGenericImportChain();
			c.execute(context);
		}
		
		return false;
	}
	
	}
