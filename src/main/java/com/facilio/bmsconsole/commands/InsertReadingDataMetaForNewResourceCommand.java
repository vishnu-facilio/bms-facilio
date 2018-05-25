package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class InsertReadingDataMetaForNewResourceCommand implements Command {

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
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
				.fields(FieldFactory.getReadingDataMetaFields());
		
		for(FacilioModule module : readingModules) {
			ReadingInputType type = null;
			switch (module.getTypeEnum()) {
				case SCHEDULED_FORMULA:
				case LIVE_FORMULA:
					type = ReadingInputType.FORMULA_FIELD;
					break;
				default:
					type = ReadingInputType.WEB;
					break;
			}
			
			List<FacilioField> fieldsList= module.getFields();
			for(FacilioField field : fieldsList) {
				ReadingDataMeta dataMeta = new ReadingDataMeta();
				dataMeta.setOrgId(orgId);
				dataMeta.setResourceId(resourceId);
				dataMeta.setFieldId(field.getFieldId());
				dataMeta.setTtime(timestamp);
				dataMeta.setValue("-1");
				dataMeta.setInputType(type);
				builder.addRecord(FieldUtil.getAsProperties(dataMeta));
			}
		}
		builder.save();
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
