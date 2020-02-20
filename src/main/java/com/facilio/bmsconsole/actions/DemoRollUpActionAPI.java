package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class DemoRollUpActionAPI {

	private static final String TABLE_NAME = "DEMO_ROLLUP_TOOL";
	private static final String ID = "id";
	private static final String START_TIME = "startTime";
	private static final FacilioModule MODULE = getDemoRollUpModule();
    private static final FacilioField ID_FIELD = FieldFactory.getIdField(ID, "ID", MODULE);
    private static final List<FacilioField> FIELDS = getDemoRollUpFields();
    
    private static FacilioModule getDemoRollUpModule() {
        FacilioModule module = new FacilioModule();
        module.setTableName(TABLE_NAME);
        module.setDisplayName(TABLE_NAME);
        module.setName(TABLE_NAME);
        return module;
    }

    private static List<FacilioField> getDemoRollUpFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(ID_FIELD);
        fields.add(FieldFactory.getDateField(START_TIME, "STARTTIME", MODULE));
        return fields;
    }

	public static List<Map<String,Object>> getData(long id) throws Exception{

    	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FIELDS);
    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FIELDS)
				.table(TABLE_NAME)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), String.valueOf(id),NumberOperators.EQUALS));
    	
    	List<Map<String, Object>> prop = builder.get();
    	if(CollectionUtils.isNotEmpty(prop)) {
    		return prop;
    	}
		return null;
	}

	public static long insertValue(Map<String, Object> props) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(TABLE_NAME).fields(FIELDS);
		long id = insertBuilder.insert(props);
		if (id != 0) {
			return id;
		}
		return 0;
	}

}
