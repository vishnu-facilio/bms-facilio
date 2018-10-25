package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.google.common.collect.ArrayListMultimap;

public class InsertReadingDataMetaForNewResourceCommand implements Command,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
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
						ReadingDataMeta dataMeta = new ReadingDataMeta();
						dataMeta.setOrgId(orgId);
						dataMeta.setResourceId(resourceId);
						dataMeta.setFieldId(field.getFieldId());
						dataMeta.setTtime(timestamp);
						dataMeta.setValue("-1");
						dataMeta.setInputType(type);
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
	}
			builder.save();
	}
		return false;
	}
	

}