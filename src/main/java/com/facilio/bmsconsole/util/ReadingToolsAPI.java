/**
 * 
 */
package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.ReadingToolCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

/**
 * @author facilio
 *
 */
public class ReadingToolsAPI {

	private static final Logger LOGGER = LogManager.getLogger(ReadingToolCommand.class.getName());
		// TODO Auto-generated method stub

	public static Map<String, Object> getReadinToolData(long fieldId, long assetId, long fieldOptionType) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields= FieldFactory.getReadingToolsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getReadingToolsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"),String.valueOf(fieldId) ,StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetId"), String.valueOf(assetId) ,StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldOptionType"),String.valueOf(fieldOptionType) , StringOperators.IS))
				;
				
		List<Map<String, Object>> stats = builder.get();
		
		if(stats!=null && !stats.isEmpty()) {
			return stats.get(0);
		}
		return null;
	
}

	public static void updateReadingToolsJob (long id, long startTime, long endTime, String email) throws Exception {
		Map<String,Object> updateReadingTools = new HashMap<>();
		updateReadingTools.put("startTtime", startTime);
		updateReadingTools.put("endTtime", endTime);
		updateReadingTools.put("email", email);

		FacilioModule module = ModuleFactory.getReadingToolsModule();
		List<FacilioField> fields= FieldFactory.getReadingToolsFields();
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, module));

		builder.update(updateReadingTools);
	}
	

	public static long insertReadingTools(Map<String, Object> prop) throws SQLException, RuntimeException {
		// TODO Auto-generated method stub
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getReadingToolsFields())
				.table(ModuleFactory.getReadingToolsModule().getTableName())
				.addRecord(prop);
			insertBuilder.save();
			
		 long id = (long) prop.get("id");
		return id;
	}

	public static Map<String, Object> getDeltaCalculationResourceJob(long id) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = ModuleFactory.getReadingToolsModule();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getReadingToolsFields())
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				;

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;

	}

}
