package com.facilio.bmsconsole.commands;

import java.util.Collections;
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

public class InsertLastReadingForNewReadingCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		//check whether it is space or asset..
		
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		FacilioModule module=  (FacilioModule)context.get(FacilioConstants.ContextNames.MODULE);
		List<FacilioField> dFields= FieldFactory.getDefaultReadingFields(module);
		fields.removeAll(dFields);
		
		List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
		if (parentIds == null) {
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			parentIds = Collections.singletonList(parentId);
		}
		
		long orgId=AccountUtil.getCurrentOrg().getOrgId();
		long timestamp=System.currentTimeMillis();
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getLastReadingModule().getTableName())
				.fields(FieldFactory.getLastReadingFields());

		for(Long parentId: parentIds) {

			for(FacilioField field : fields) {

				long fieldId=field.getFieldId();
				Map<String, Object> lastReading = new HashMap<String,Object>();
				lastReading.put("orgId", orgId);
				lastReading.put("resourceId", parentId);
				lastReading.put("fieldId", fieldId);
				lastReading.put("ttime", timestamp);
				lastReading.put("value", -1);
				builder.addRecord(lastReading);
			}
		}
		builder.save();
		
		// TODO Auto-generated method stub
		return false;
	}

}
