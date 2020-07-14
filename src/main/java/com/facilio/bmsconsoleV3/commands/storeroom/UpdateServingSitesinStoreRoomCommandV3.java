package com.facilio.bmsconsoleV3.commands.storeroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;

public class UpdateServingSitesinStoreRoomCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
    	
		Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName != null && !moduleName.isEmpty() && recordMap != null && MapUtils.isNotEmpty(recordMap)) {
			List records = recordMap.get(moduleName);
			if(records != null && !records.isEmpty()) {
				for(Object record:records) 
				{		
					if(record != null && record instanceof ModuleBaseWithCustomFields && (ModuleBaseWithCustomFields)record != null)
					{					
						long storeRoomId = ((ModuleBaseWithCustomFields)record).getId();
						Map<String, Object> recordProps = FieldUtil.getAsProperties(record);	
						if(storeRoomId > 0) {
							GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
									.table(ModuleFactory.getSitesForStoreRoomModule().getTableName())
									.andCustomWhere("STORE_ROOM_ID = ?", storeRoomId);
							builder.delete();
						}
						if(storeRoomId > 0 && recordProps != null && recordProps.get(FacilioConstants.ContextNames.SITE_LIST) != null) 
						{			
							List<Long> sites = (List<Long>) recordProps.get(FacilioConstants.ContextNames.SITE_LIST);			
							if (sites != null && !sites.isEmpty()) 
							{	
								GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
										.table(ModuleFactory.getSitesForStoreRoomModule().getTableName())
										.fields(FieldFactory.getSitesForStoreRoomFields());

								List<Map<String, Object>> propsList = new ArrayList<>();
								for (Long siteId : sites) {
									Map<String, Object> props = new HashMap<>();
									props.put("storeRoomId", storeRoomId);
									props.put("siteId", siteId);
									propsList.add(props);
								}
								insertBuilder.addRecords(propsList);
								insertBuilder.save();
							}						
						}															
					}		
				}		
			}
		}
		return false;	
    }
}
