package com.facilio.bmsconsole.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.templates.TemplateAttachmentType;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.filestore.FileStore.NamespaceConfig;

public class TemplateAttachmentUtil {
	
	public static List<TemplateAttachment> fetchAttachments(long templateId) throws Exception {
		List<TemplateAttachment> attachments = new ArrayList<>();
		for (TemplateAttachmentType type : TemplateAttachmentType.values()) {
			List<? extends TemplateAttachment> attachmentRecords = fetchAttachments(templateId, type);
			if (attachmentRecords != null) {
				attachments.addAll(attachmentRecords);
			}
		}
		return attachments;
	}

	public static final List<? extends TemplateAttachment> fetchAttachments(long templateId, TemplateAttachmentType type) throws Exception {

		FacilioModule module = type.getModule();
		List<FacilioField> fields = type.getFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("templateId"), String.valueOf(templateId), NumberOperators.EQUALS));

		FacilioField fileField = fieldMap.get("fileId");
		// To get the file name
		if (fileField != null) {
			NamespaceConfig namespaceConfig = FileStore.getNamespace(FileStore.DEFAULT_NAMESPACE);
			Map<String, FacilioField> fileFieldMap = FieldFactory.getAsMap(FieldFactory.getFileFields(namespaceConfig.getTableName()));
			fields.add(fileFieldMap.get("fileName"));

			selectBuilder.innerJoin(namespaceConfig.getTableName())
				.on(MessageFormat.format("{0}.FILE_ID = {1}.FILE_ID", namespaceConfig.getTableName(), module.getTableName()))
			;
		}

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, type.getAttachmentClass());
		}

		return null;
	}

	public static final void addAttachments(long templateId,  List<TemplateAttachment> attachments) throws Exception {

		Map<TemplateAttachmentType, List<TemplateAttachment>> attachmentMap = attachments
				.stream().map(a -> a.setTemplateId(templateId))
				.collect(Collectors.groupingBy(TemplateAttachment::getType));
				
		for(Map.Entry<TemplateAttachmentType, List<TemplateAttachment>> entry : attachmentMap.entrySet()) {
			TemplateAttachmentType type = entry.getKey();

			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(type.getModule().getTableName())
					.fields(type.getFields());

			List<Map<String, Object>> props = FieldUtil.getAsMapList(entry.getValue(), type.getClass());
			insertBuilder.addRecords(props);
			insertBuilder.save();
		}

	}
}
