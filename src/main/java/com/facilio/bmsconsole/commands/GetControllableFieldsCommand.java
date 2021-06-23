package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetControllableFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());
		
		List<FacilioField> fields = Collections.singletonList(fieldMap.get("fieldId"));
		
		Long resourceId = (Long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		Long assetCategoryId = (Long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
		
		if(resourceId != null && resourceId > 0) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(ModuleFactory.getReadingDataMetaModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", StringOperators.ISN_T))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingType"), ReadingDataMeta.ReadingType.WRITE.getValue()+"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("isControllable"), Boolean.TRUE.toString(), BooleanOperators.IS))
					.groupBy("FIELD_ID");	// move this criteria to Action
	    	
	    	if(resourceId != null && resourceId > 0) {
	    		builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), resourceId+"", NumberOperators.EQUALS));
	    	}
			
	    	List<Map<String, Object>> props = builder.get();
	    	
	    	List<Long> fieldIDs = new ArrayList<Long>();
	    	for(Map<String, Object> prop :props) {
	    		fieldIDs.add((Long) prop.get("fieldId"));
	    	}
	    	
	    	if(!fieldIDs.isEmpty()) {
	    		context.put(ControlActionUtil.CONTROLLABLE_FIELDS, modBean.getFields(fieldIDs));
	    	}
		}
		else if(assetCategoryId != null && assetCategoryId > 0) {
			
			FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
			FacilioModule assetCategoryModule = modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY);
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(ModuleFactory.getReadingDataMetaModule().getTableName())
					.innerJoin(assetModule.getTableName())
					.on("Reading_Data_Meta.RESOURCE_ID = Assets.ID")
					.innerJoin(assetCategoryModule.getTableName())
					.on("Assets.CATEGORY = Asset_Categories.ID")
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", StringOperators.ISN_T))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingType"), ReadingDataMeta.ReadingType.WRITE.getValue()+"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("isControllable"), Boolean.TRUE.toString(), BooleanOperators.IS))
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(assetCategoryModule), assetCategoryId+"", NumberOperators.EQUALS))
					.groupBy("FIELD_ID");
	    	
			
	    	List<Map<String, Object>> props = builder.get();
	    	
	    	List<Long> fieldIDs = new ArrayList<Long>();
	    	for(Map<String, Object> prop :props) {
	    		fieldIDs.add((Long) prop.get("fieldId"));
	    	}
	    	
	    	if(!fieldIDs.isEmpty()) {
	    		context.put(ControlActionUtil.CONTROLLABLE_FIELDS, modBean.getFields(fieldIDs));
	    	}
		}
    	
		return false;
	}

}
