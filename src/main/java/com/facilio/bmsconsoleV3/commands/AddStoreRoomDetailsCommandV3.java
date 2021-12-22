package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.agent.AgentKeys;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class AddStoreRoomDetailsCommandV3 extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);

        Map < String, List > recordMap = (Map < String, List > ) context.get(Constants.RECORD_MAP);
        if(MapUtils.isNotEmpty(recordMap))
        {
        	List<V3StoreRoomContext> storeRooms = recordMap.get(moduleName);
        	for(V3StoreRoomContext storeRoom : storeRooms)
        	{
        		if (storeRoom != null && storeRoom.getId() > 0) {
    				if (storeRoom.getLocationId() != -1) {
    					Map<Long, LocationContext> spaceMap = LocationAPI.getLocationMap(Collections.singleton(storeRoom.getLocationId()));
    					storeRoom.setLocation(spaceMap.get(storeRoom.getLocationId()));
    				}
    				storeRoom.setSites(getSitesList(storeRoom.getId()));
    			}
        	}
        }
		return false;
	}
	
	public List<Long> getSitesList (long storeRoomId) throws Exception { 
        
		FacilioModule resourceModule = ModuleFactory.getResourceModule();
		
		Criteria criteria = new Criteria();
        criteria.addOrCondition(CriteriaAPI.getCondition(FieldFactory.getIsDeletedField(resourceModule), "NULL", CommonOperators.IS_EMPTY));
        criteria.addOrCondition(CriteriaAPI.getCondition(FieldFactory.getIsDeletedField(resourceModule), "0", NumberOperators.EQUALS));
        
        List<FacilioField> fields = FieldFactory.getSitesForStoreRoomFields();
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSitesForStoreRoomFields())
                .table(ModuleFactory.getSitesForStoreRoomModule().getTableName())
                .innerJoin(ModuleFactory.getResourceModule().getTableName())
                .on(ModuleFactory.getResourceModule().getTableName() + ".ID = " +  ModuleFactory.getSitesForStoreRoomModule().getTableName() + ".SITE_ID")
                .andCriteria(criteria)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("storeRoomId"),String.valueOf(storeRoomId), NumberOperators.EQUALS));


        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            List<Long> bsids = new ArrayList<>();
            for(Map<String, Object> prop : props) {
                bsids.add((Long) prop.get("siteId"));
            }
            return bsids;
        }
        return null;

	}
}
