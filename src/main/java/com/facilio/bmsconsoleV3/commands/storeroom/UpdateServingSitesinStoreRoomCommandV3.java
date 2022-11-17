package com.facilio.bmsconsoleV3.commands.storeroom;

import java.util.*;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ServingSitesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
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
		String moduleName = Constants.getModuleName(context);
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
		List<V3StoreRoomContext> storeRoomContexts = recordMap.get(moduleName);

		List<V3StoreRoomContext> storeRoomRecords = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(storeRoomContexts)) {
			for(V3StoreRoomContext storeRoom :storeRoomContexts)
			{
							List<V3ServingSitesContext> sites = storeRoom.getServingsites();
							Long locatedSiteId = storeRoom.getSite().getId();

							if(CollectionUtils.isEmpty(sites)) {
								if(locatedSiteId != null && locatedSiteId > 0) {
										sites = new ArrayList<>();
										V3ServingSitesContext locatedSite = new V3ServingSitesContext();
										locatedSite.setId(locatedSiteId);
										sites.add(locatedSite); //adding located site as one of the serving sites if no serving site is given
								}
							}else{
								boolean isLocatedSitePresent = false;
								for(V3ServingSitesContext site : sites){
									if(site.getId() == locatedSiteId){
										isLocatedSitePresent=true;
										break;
									}
								}
								if(!isLocatedSitePresent){
									V3ServingSitesContext locatedSite = new V3ServingSitesContext();
									locatedSite.setId(locatedSiteId);
									sites.add(locatedSite);
								}
							}

							if (sites != null && !sites.isEmpty()) 
							{
								storeRoom.setServingsites(sites);
								storeRoomRecords.add(storeRoom);
							}

				}
			recordMap.put(moduleName,storeRoomRecords);
		}
		return false;	
    }
}
