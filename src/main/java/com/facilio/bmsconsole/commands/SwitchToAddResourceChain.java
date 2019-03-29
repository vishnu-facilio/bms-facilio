package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ImportAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class SwitchToAddResourceChain implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		List<ReadingContext> readingsContext = (List<ReadingContext>) context.get(ImportAPI.ImportProcessConstants.READINGS_LIST); 
		String moduleName = importProcessContext.getModule().getName();
		
//		switch(moduleName) {
//		
//		case FacilioConstants.ContextNames.ASSET:
//		{
//			Chain c = TransactionChainFactory.getAssetImportChain();
//			c.execute(context);
//			break;
//		}
//		case FacilioConstants.ContextNames.PURCHASED_ITEM:{
//			JSONObject importMeta = importProcessContext.getImportJobMetaJson();
//			Long StoreRoom = (Long)importMeta.get(FacilioConstants.ContextNames.STORE_ROOM);
//			
//			List<PurchasedItemContext> items = new ArrayList<PurchasedItemContext>();
//			for(ReadingContext readingContext : readingsContext) {
//				Map<String, Object> temp = readingContext.getData();
//				items.add(FieldUtil.getAsBeanFromMap(temp, PurchasedItemContext.class));
//				
//			}
//			
//			Chain c = TransactionChainFactory.getImportItemChain();
//			context.put(FacilioConstants.ContextNames.STORE_ROOM, StoreRoom);
//			context.put(FacilioConstants.ContextNames.RECORD_LIST, items);
//			c.execute(context);
//			break;
//		}
//		case FacilioConstants.ContextNames.PURCHASED_TOOL:{
//			List<PurchasedToolContext> tools = new ArrayList<PurchasedToolContext>();
//			for(ReadingContext readingContext: readingsContext) {
//				Map<String, Object> temp = readingContext.getData();
//				tools.add(FieldUtil.getAsBeanFromMap(temp, PurchasedToolContext.class));
//			}
//			
//			// call new chain
//			break;
//		}
//		
//		}
		return false;
	}
	
	

}
