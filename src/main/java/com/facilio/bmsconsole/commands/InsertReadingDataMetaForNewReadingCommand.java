package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class InsertReadingDataMetaForNewReadingCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Long> parentIds = getParentIds(context);
		List<FacilioModule> modules = CommonCommandUtil.getModulesWithFields(context);

		if (parentIds != null && !parentIds.isEmpty() && modules != null && !modules.isEmpty()) {
			ReadingInputType moduleType = (ReadingInputType) context.get(FacilioConstants.ContextNames.READING_DATA_META_TYPE);
			long orgId=AccountUtil.getCurrentOrg().getOrgId();
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getReadingDataMetaModule().getTableName())
														.fields(FieldFactory.getReadingDataMetaFields());
			long timestamp=System.currentTimeMillis();
			for (FacilioModule module : modules) {
				if (module.getFields() != null && !module.getFields().isEmpty()) {
					ReadingInputType type = moduleType != null ? moduleType : ReadingsAPI.getRDMInputTypeFromModuleType(module.getTypeEnum());
					List<FacilioField> fields = module.getFields();
					List<FacilioField> dFields= FieldFactory.getDefaultReadingFields(module);
					fields.removeAll(dFields);
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
				}
			}
			builder.save();
		}
		
		// TODO Auto-generated method stub
		return false;
	}
	
	private List<Long> getParentIds(Context context) {
		List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
		if (parentIds == null) {
			Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			if (parentId != null) {
				parentIds = Collections.singletonList(parentId);
			}
		}
		return parentIds;
	}

}
