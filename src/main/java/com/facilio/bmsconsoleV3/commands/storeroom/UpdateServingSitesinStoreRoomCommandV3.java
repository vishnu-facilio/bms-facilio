package com.facilio.bmsconsoleV3.commands.storeroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;

public class UpdateServingSitesinStoreRoomCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
    	
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
		
		if(moduleName != null && !moduleName.isEmpty() && recordMap != null && MapUtils.isNotEmpty(recordMap)) {
			List records = recordMap.get(moduleName);
			if(records != null && !records.isEmpty()) {
				for(Object record:records) 
				{		
					if(record != null && record instanceof ModuleBaseWithCustomFields && (ModuleBaseWithCustomFields)record != null)
					{			
						V3StoreRoomContext sr = (V3StoreRoomContext)record;
						Long storeRoomId = ((ModuleBaseWithCustomFields)record).getId();
						Map<String, Object> recordProps = FieldUtil.getAsProperties(record);	
						if(storeRoomId != null && storeRoomId > 0) {
							GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
									.table(ModuleFactory.getSitesForStoreRoomModule().getTableName())
									.andCustomWhere("STORE_ROOM_ID = ?", storeRoomId);
							builder.delete();
						}
						if(storeRoomId != null && storeRoomId > 0 && recordProps != null) 
						{			
							List<SiteContext> sites = (List<SiteContext>) sr.getSites();
							if(CollectionUtils.isEmpty(sites)) {
								Long locatedSiteId = ((ModuleBaseWithCustomFields)record).getSiteId();
								if(locatedSiteId != null && locatedSiteId > 0) {
									SiteContext locatedSite = new SiteContext();
									locatedSite.setId(locatedSiteId);
									sites.add(locatedSite); //adding located site as one of the serving sites if no serving site is given
								}
						     }
							
							if (sites != null && !sites.isEmpty()) 
							{	
								GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
										.table(ModuleFactory.getSitesForStoreRoomModule().getTableName())
										.fields(FieldFactory.getSitesForStoreRoomFields());

								List<Map<String, Object>> propsList = new ArrayList<>();
								for (SiteContext site : sites) {
									Map<String, Object> props = new HashMap<>();
									props.put("storeRoomId", storeRoomId);
									props.put("siteId", site.getId());
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
