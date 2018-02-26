package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class InsertLastReadingForResourceCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		
		List<FacilioModule> readingModules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		long resourceId= (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		long orgId=AccountUtil.getCurrentOrg().getOrgId();
		long timestamp=System.currentTimeMillis();
		if(readingModules == null || readingModules.isEmpty()) {
			return false;
		}

		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getLastReadingModule().getTableName())
				.fields(FieldFactory.getLastReadingFields());
		
		for(FacilioModule module : readingModules) {
			List<FacilioField> fieldsList= module.getFields();
			for(FacilioField field : fieldsList) {

				long fieldId=field.getFieldId();
				Map<String, Object> lastReading = new HashMap<String,Object>();
				lastReading.put("orgId", orgId);
				lastReading.put("resourceId", resourceId);
				lastReading.put("fieldId", fieldId);
				lastReading.put("ttime", timestamp);
				lastReading.put("value", -1);

				builder.addRecord(lastReading);
				//orgid, fieldid, assetid, timestamp, value

			}
		}
		builder.save();
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
