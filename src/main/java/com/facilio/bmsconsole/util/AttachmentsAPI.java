package com.facilio.bmsconsole.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AttachmentsAPI {
	public static final void addAttachments(List<AttachmentContext> attachments, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		InsertRecordBuilder<AttachmentContext> attachmentBuilder = new InsertRecordBuilder<AttachmentContext>()
																	.module(module)
																	.fields(modBean.getAllFields(moduleName))
																	.addRecords(attachments);
		
		attachmentBuilder.save();
	}
	
	public static final List<AttachmentContext> getAttachments(String moduleName, long parentId, Boolean... fetchDeleted) throws Exception {
		return fetchAttachments(moduleName, parentId, fetchDeleted != null && fetchDeleted.length == 1 && fetchDeleted[0]);
	}
	
	public static final List<AttachmentContext> getAttachments(String moduleName, List<Long> attachmentIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = FieldFactory.getFileFields();
		fields.addAll(modBean.getAllFields(moduleName));
		
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(StringUtils.join(attachmentIds, ","));
		
		SelectRecordsBuilder<AttachmentContext> selectBuilder = new SelectRecordsBuilder<AttachmentContext>()
																		.beanClass(AttachmentContext.class)
																		.select(fields)
																		.module(module)
																		.innerJoin("File")
																		.on("File.FILE_ID = "+module.getTableName()+".FILE_ID")
																		.andCondition(idCondition);
		
		return selectBuilder.get();
	}
	
	@SuppressWarnings("unchecked")
	public static AttachmentContext fetchAttachment(String moduleName, Long parentId, Long fileId, Boolean... fetchDeleted) throws Exception {
		List<AttachmentContext> attachments = fetchAttachments(moduleName, parentId, fetchDeleted != null && fetchDeleted.length == 1 && fetchDeleted[0], Collections.singletonList(fileId));
		if (attachments != null && !attachments.isEmpty()) {
			return attachments.get(0);
		}
		return null;
	}
	
	public static long fetchAttachmentsCount(String moduleName, Long parentId, Boolean... fetchDeleted) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AttachmentContext> builder = getListBuilder(moduleName, parentId, fetchDeleted != null && fetchDeleted.length == 1 && fetchDeleted[0], null);
		builder.select(FieldFactory.getCountField(modBean.getModule(moduleName)));
		List<Map<String, Object>> props = builder.getAsProps();
		long count = 0;
		if (props != null && !props.isEmpty()) {
			count = (long) props.get(0).get("count");
		}
		return count;
	}
	
	private static List<AttachmentContext> fetchAttachments(String moduleName, Long parentId, boolean fetchDeleted, List<Long>... attachmentIds) throws Exception {
		return getListBuilder(moduleName, parentId, fetchDeleted, attachmentIds).get();
	}
	
	private static SelectRecordsBuilder<AttachmentContext> getListBuilder(String moduleName, Long parentId, boolean fetchDeleted, List<Long>... attachmentIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = FieldFactory.getFileFields();
		fields.addAll(modBean.getAllFields(moduleName));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<AttachmentContext> selectBuilder = new SelectRecordsBuilder<AttachmentContext>()
				.beanClass(AttachmentContext.class)
				.select(fields)
				.module(module)
				.innerJoin("File")
				.on("File.FILE_ID = "+module.getTableName()+".FILE_ID");
				
		if (!fetchDeleted) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("isDeleted"), String.valueOf(false), BooleanOperators.IS));
		}
		
		if (parentId != null && parentId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS));
		}
		
		if (attachmentIds != null && attachmentIds.length > 0 && !attachmentIds[0].isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("fileId"), attachmentIds[0], NumberOperators.EQUALS));
		}
		
		// TODO handle other attachments
		if (moduleName.equals(FacilioConstants.ContextNames.ASSET_ATTACHMENTS) || moduleName.equals(FacilioConstants.ContextNames.BASE_SPACE_ATTACHMENTS)) {
			Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName, fieldMap.get("parentId"));
			if(scopeCriteria != null){
				selectBuilder.andCriteria(scopeCriteria);
			}
		}
		return selectBuilder;
	}
}
