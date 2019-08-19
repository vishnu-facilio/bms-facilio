package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetControllableFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());
		
		List<FacilioField> fields = Collections.singletonList(fieldMap.get("fieldId"));
		
		Long resourceId = (Long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		
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
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	if(!fieldIDs.isEmpty()) {
    		context.put(ControlActionUtil.CONTROLLABLE_FIELDS, modBean.getFields(fieldIDs).values());
    	}
    	
		return false;
	}

}
