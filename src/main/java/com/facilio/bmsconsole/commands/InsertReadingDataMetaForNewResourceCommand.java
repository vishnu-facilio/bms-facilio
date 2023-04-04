package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.facilio.bmsconsole.util.ReadingsAPI.getRDMContextForReadingAndResource;

public class InsertReadingDataMetaForNewResourceCommand extends FacilioCommand implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if(context.get(FacilioConstants.ContextNames.RECORD_LIST) instanceof  ArrayListMultimap) {
			ArrayListMultimap<String, Long>recordsList= (ArrayListMultimap<String, Long>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
			ArrayList<String> moduleNames = new ArrayList<>(recordsList.keySet());
			long orgId=AccountUtil.getCurrentOrg().getOrgId();
			long timestamp=System.currentTimeMillis();
			
			for(String moduleName : moduleNames) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				for(Long resourceId : recordsList.get(moduleName)) {
					
					GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getReadingDataMetaModule().getTableName())
							.fields(FieldFactory.getReadingDataMetaFields());
					
					ReadingInputType type = (ReadingInputType) context.get(FacilioConstants.ContextNames.READING_DATA_META_TYPE);
					if (type == null) {
						type = ReadingsAPI.getRDMInputTypeFromModuleType(module.getTypeEnum());
					}
					List<FacilioField> fieldsList= new ArrayList<>(modBean.getAllFields(moduleName));
					List<FacilioField> dFields= FieldFactory.getDefaultReadingFields(module);
					fieldsList.removeAll(dFields);
					for(FacilioField field : fieldsList) {
						ReadingDataMeta dataMeta = getRDMContextForReadingAndResource(orgId, timestamp, resourceId, type, field);
						builder.addRecord(FieldUtil.getAsProperties(dataMeta));
					}
					builder.save();
				}
				
			}
		}
		
		else {
			List<FacilioModule> readingModules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			
			List<Long> recordsList = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			if(recordsList == null) {
				Long resourceId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				if(resourceId == null) {
					return false;
				}
				else {
					recordsList = new ArrayList<Long>();
					recordsList.add(resourceId);
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
			
			
			for(FacilioModule module : readingModules) {
				for(Long resourceId : recordsList) {
					
				ReadingInputType type = (ReadingInputType) context.get(FacilioConstants.ContextNames.READING_DATA_META_TYPE);
				if (type == null) {
					type = ReadingsAPI.getRDMInputTypeFromModuleType(module.getTypeEnum());
				}
				List<FacilioField> fieldsList= module.getFields();
				List<FacilioField> dFields= FieldFactory.getDefaultReadingFields(module);
				fieldsList.removeAll(dFields);
				for(FacilioField field : fieldsList) {
					ReadingDataMeta dataMeta = getRDMContextForReadingAndResource(orgId, timestamp, resourceId, type, field);
					builder.addRecord(FieldUtil.getAsProperties(dataMeta));
				}
			}
	}
			builder.save();
	}
		return false;
	}

}