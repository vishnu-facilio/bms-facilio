package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class InsertReadingDataMetaForNewResourceCommandDataImport implements Command {

	@Override
public boolean execute(Context context) throws Exception {
		
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		if(importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.INSERT.getValue() 
				|| importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.BOTH.getValue()  
				|| importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.BOTH_NOT_NULL.getValue()) {
			List<FacilioModule> readingModules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			List<Long> resourceIds= (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if(resourceIds == null) {
				Long resourceId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				if(resourceId == null) {
					return false;
				}
				else {
					resourceIds = new ArrayList<Long>();
					resourceIds.add(resourceId);
				}
			}
			
			long orgId=AccountUtil.getCurrentOrg().getOrgId();
			long timestamp=System.currentTimeMillis();
			if(readingModules == null || readingModules.isEmpty()) {
				return false;
			}

			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReadingDataMetaModule().getTableName())
					.fields(FieldFactory.getReadingDataMetaFields());
			
			for(Long resourceId : resourceIds) {
			for(FacilioModule module : readingModules) {
				ReadingInputType type = (ReadingInputType) context.get(FacilioConstants.ContextNames.READING_DATA_META_TYPE);
				if (type == null) {
					type = ReadingsAPI.getRDMInputTypeFromModuleType(module.getTypeEnum());
				}
				List<FacilioField> fieldsList= module.getFields();
				List<FacilioField> dFields= FieldFactory.getDefaultReadingFields(module);
				fieldsList.remove(dFields);
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
			}
		
		}
		return false;
	}
}
