package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetControllableAssetsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
    	
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());
		
		List<FacilioField> fields = Collections.singletonList(fieldMap.get("resourceId"));
		
    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingType"), ReadingDataMeta.ReadingType.WRITE.getValue()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("isControllable"), Boolean.TRUE.toString(), BooleanOperators.IS))
				.groupBy("RESOURCE_ID");	// move this criteria to Action
		
    	List<Map<String, Object>> props = builder.get();
    	
    	List<Long> assetIds = new ArrayList<Long>();
    	for(Map<String, Object> prop :props) {
    		assetIds.add((Long) prop.get("resourceId"));
    	}
    	List<AssetContext> assets = AssetsAPI.getAssetInfo(assetIds);
    	
    	context.put(ControlActionUtil.CONTROLLABLE_RESOURCES, assets);
    	
		return false;
	}

}
