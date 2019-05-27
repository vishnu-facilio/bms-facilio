package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.criteria.*;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.criteria.*;
import com.facilio.db.criteria.*;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachmentsAPI {
	private static final Logger LOGGER = LogManager.getLogger(AttachmentsAPI.class.getName());
	
	public static final void addAttachments(List<AttachmentContext> attachments, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		if (AccountUtil.getCurrentOrg().getId() == 155) {
			LOGGER.info("Inserting rel for attachments : "+attachments);
		}
		
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
																		.innerJoin("FacilioFile")
																		.on("FacilioFile.FILE_ID = "+module.getTableName()+".FILE_ID")
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
				.innerJoin("FacilioFile")
				.on("FacilioFile.FILE_ID = "+module.getTableName()+".FILE_ID");
				
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
		if (moduleName.equals(FacilioConstants.ContextNames.ASSET_ATTACHMENTS) || moduleName.equals(FacilioConstants.ContextNames.BASE_SPACE_ATTACHMENTS) || moduleName.equals(FacilioConstants.ContextNames.INVENTORY_ATTACHMENTS)) {
			Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName, fieldMap.get("parentId"));
			if(scopeCriteria != null){
				selectBuilder.andCriteria(scopeCriteria);
			}
		}
		return selectBuilder;
	}
	
	public static void updateAttachmentCount(Collection<Long> list, String attachmentModuleName) throws Exception {
		if (StringUtils.isEmpty(attachmentModuleName)) {
			return;
		}
		
		String moduleName = null;
		if (attachmentModuleName == null) {
			return;
		}
		switch (attachmentModuleName) {
			case "ticketattachments":
				moduleName = "ticket";
				break;
				
			case "taskattachments":
				moduleName = "task";
				break;
		}
		
		if (list != null && !list.isEmpty() && StringUtils.isNotBlank(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			List<FacilioField> fields = new ArrayList<>();
			FacilioField parentTicketId = modBean.getField("parentId", attachmentModuleName);
			fields.add(parentTicketId);
			
			FacilioModule module = parentTicketId.getModule();
			
			FacilioField countField = new FacilioField();
			countField.setName("count");
			countField.setColumnName("COUNT(*)");
			countField.setDataType(FieldType.NUMBER);
			fields.add(countField);
			
			GenericSelectRecordBuilder recordBuilder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.groupBy(parentTicketId.getCompleteColumnName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(parentTicketId, list, NumberOperators.EQUALS));
			List<Map<String, Object>> rs = recordBuilder.get();
			
			if (!rs.isEmpty()) {
				FacilioModule ticketModule = modBean.getModule(moduleName);
				
				FacilioField attachmentField = new FacilioField();
				attachmentField.setName("noOfAttachments");
				attachmentField.setColumnName("NO_OF_ATTACHMENTS");
				attachmentField.setDataType(FieldType.NUMBER);
				
				List<FacilioField> updateFields = new ArrayList<>();
				updateFields.add(attachmentField);
				
				Condition idFieldCondition = CriteriaAPI.getCondition(FieldFactory.getIdField(ticketModule), NumberOperators.EQUALS);
				for (Map<String, Object> map : rs) {
					Long id = ((Number) map.get("parentId")).longValue();
					idFieldCondition.setValue(String.valueOf(id));
					
					GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
							.table(ticketModule.getTableName())
							.fields(updateFields)
							.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ticketModule))
							.andCondition(idFieldCondition);
					
					Map<String, Object> updateMap = new HashMap<String, Object>();
					updateMap.put("noOfAttachments", ((Number) map.get("count")).intValue());
					
					updateRecordBuilder.update(updateMap);
				}
			}
		}
	}
}
