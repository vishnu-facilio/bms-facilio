package com.facilio.bmsconsole.commands;

import java.util.Collections;
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

public class InsertReadingDataMetaForNewReadingCommand implements Command {

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
		ReadingInputType type = (ReadingInputType) context.get(FacilioConstants.ContextNames.READING_DATA_META_TYPE);
		if (type == null) {
			type = ReadingInputType.WEB;
		}
		
		long orgId=AccountUtil.getCurrentOrg().getOrgId();
		long timestamp=System.currentTimeMillis();
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
				.fields(FieldFactory.getReadingDataMetaFields());

		for(Long parentId: parentIds) {
			for(FacilioField field : fields) {
				ReadingDataMeta dataMeta = new ReadingDataMeta();
				dataMeta.setOrgId(orgId);
				dataMeta.setResourceId(parentId);
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
