package com.facilio.accounts.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.TemplateFileContext;
import com.facilio.bmsconsole.context.TemplateUrlContext;
import com.facilio.bmsconsole.context.TemplateFileFieldContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.filestore.FileStore;

public class EmailAttachmentAPI {
	
	public static final <E> void addAttachments(List<E> attachments, FacilioModule module, long templateId, Class<E> classObj, List<FacilioField> fields) throws Exception {
		
	    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);	
		if(attachments != null) {
	
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(fields);
			
			List<Map<String, Object>> props = FieldUtil.getAsMapList(attachments, classObj);
			insertBuilder.addRecords(props);
			insertBuilder.save();
			
		}	
	}
	
public static final <E> void deleteAttachments(List<Long> templateIds, FacilioModule module, Class<E> classObj, List<FacilioField> fields) throws Exception {
		
	    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);	
		if(templateIds != null && !templateIds.isEmpty()) {
	
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("templateId"), templateIds, NumberOperators.EQUALS));
			 builder.delete();
			
			
		}	
	}
	
	public static final List<TemplateFileContext> getAttachments(long templateId) throws Exception {
		
		FileStore.NamespaceConfig namespaceConfig = FileStore.getNamespace(FileStore.DEFAULT_NAMESPACE);
		
		FacilioModule module = ModuleFactory.getTemplateFileModule();
		List<FacilioField> fields = FieldFactory.getTemplateFileFields();
		fields.addAll(FieldFactory.getFileFields(namespaceConfig.getTableName()));
	    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
	    List<TemplateFileContext> attachmentList = new ArrayList<>();
				
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.innerJoin(namespaceConfig.getTableName())
				.on(MessageFormat.format("{0}.FILE_ID = {1}.FILE_ID", namespaceConfig.getTableName(), module.getTableName()))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("templateId"), ""+templateId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			 attachmentList = FieldUtil.getAsBeanListFromMapList(props, TemplateFileContext.class);
		}
		
		return attachmentList;
	}
	
public static final List<TemplateUrlContext> getUrlAttachments(long templateId) throws Exception {
		
		
		FacilioModule module = ModuleFactory.getTemplateUrlAttachmentModule();
		List<FacilioField> fields = FieldFactory.getTemplateUrlFields();
	    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
	    
	    List<TemplateUrlContext> attachmentList = new ArrayList<>();
				
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("templateId"), ""+templateId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			 attachmentList = FieldUtil.getAsBeanListFromMapList(props, TemplateUrlContext.class);
		}
		
		return attachmentList;
	}

public static final List<TemplateFileFieldContext> getFileFieldAttachments(long templateId) throws Exception {
	
	
	FacilioModule module = ModuleFactory.getTemplateFileFieldAttachmentModule();
	List<FacilioField> fields = FieldFactory.getTemplateFileFieldFields();
    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
    
    List<TemplateFileFieldContext> attachmentList = new ArrayList<>();
			
	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(fields)
			.table(module.getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("templateId"), ""+templateId, NumberOperators.EQUALS));
	
	List<Map<String, Object>> props = selectBuilder.get();
	if (props != null && !props.isEmpty()) {
		 attachmentList = FieldUtil.getAsBeanListFromMapList(props, TemplateFileFieldContext.class);
	}
	
	return attachmentList;
}
	

}
