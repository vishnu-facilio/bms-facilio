package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateImportPointsDataCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(UpdateImportPointsDataCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Map<String,Object>> finalList = (List<Map<String, Object>>) context.get("POINTS_LIST");
		long controllerId = (long) context.get("CONTROLLER_ID");
		
		try {
//			FacilioTransactionManager.INSTANCE.getTransactionManager().begin();

			for(Map<String,Object> prop:finalList) {
				if(prop.isEmpty()) {
					continue;
				}
				Map<String ,Object> map = new HashMap<String, Object>();
				String deviceName = (String) prop.get("Device");
				String instanceName = (String) prop.get("Instance");
				map.put("resourceId",prop.get("Assets"));
				map.put("categoryId", prop.get("Asset Category")); 
				map.put("fieldId", prop.get("Reading"));
				map.put("mappedTime", System.currentTimeMillis());
				List<FacilioField> field = FieldFactory.getPointsFields();
				Map<String, FacilioField> fieldMaps = FieldFactory.getAsMap(field);

				GenericUpdateRecordBuilder builderPoints = new GenericUpdateRecordBuilder()
						.fields(FieldFactory.getPointsFields())
						.table(ModuleFactory.getPointsModule().getTableName())
						.andCondition(CriteriaAPI.getCondition(fieldMaps.get("controllerId"), String.valueOf(controllerId), StringOperators.IS))
						.andCondition(CriteriaAPI.getCondition(fieldMaps.get("device"),deviceName, StringOperators.IS))
						.andCondition(CriteriaAPI.getCondition(fieldMaps.get("instance"),instanceName, StringOperators.IS))
						;
				builderPoints.update(map);
//				FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
			
			}
		}catch (Exception e) {
			LOGGER.error("Exception occured While updating Points Table   :", e);
//			FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
			throw e;
	}
		
		
		
		return false;
	}

}
