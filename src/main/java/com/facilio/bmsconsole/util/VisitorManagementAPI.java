package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.PoAssociatedTermsContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.LookupFieldMeta;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.services.filestore.FacilioFileStore;
import com.facilio.services.filestore.FileStore;

public class VisitorManagementAPI {

	public static List<InviteVisitorRelContext> getEventInvitees(long eventId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<InviteVisitorRelContext> builder = new SelectRecordsBuilder<InviteVisitorRelContext>()
														.module(module)
														.beanClass(InviteVisitorRelContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("INVITE_ID", "inviteId", String.valueOf(eventId), NumberOperators.EQUALS))
														.fetchLookup((LookupField) fieldsAsMap.get("visitorId"))
														;
		
		
		List<InviteVisitorRelContext> records = builder.get();
		return records;
	
	}
	
	public static VisitorInviteContext getVisitorEvent(long eventId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE);
		SelectRecordsBuilder<VisitorInviteContext> builder = new SelectRecordsBuilder<VisitorInviteContext>()
														.module(module)
														.beanClass(VisitorInviteContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(eventId), NumberOperators.EQUALS))
														;
		
		
		VisitorInviteContext records = builder.fetchFirst();
		return records;
	
	}
	
	public static VisitorLoggingContext getVisitorLogging(long visitorId, boolean fetchActiveLog) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		SelectRecordsBuilder<VisitorLoggingContext> builder = new SelectRecordsBuilder<VisitorLoggingContext>()
														.module(module)
														.beanClass(VisitorLoggingContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("VISITOR", "visitorId", String.valueOf(visitorId), NumberOperators.EQUALS))
														;
		if(fetchActiveLog) {
			FacilioStatus checkedInStatus = TicketAPI.getStatus(module, "CheckedIn");
			builder.andCondition(CriteriaAPI.getCondition("MODULE_STATE", "moduleState", String.valueOf(checkedInStatus.getId()), NumberOperators.EQUALS));
		}
		
		VisitorLoggingContext records = builder.fetchFirst();
		return records;
	
	}
	
	public static VisitorLoggingContext getVisitorLogging(long logId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		SelectRecordsBuilder<VisitorLoggingContext> builder = new SelectRecordsBuilder<VisitorLoggingContext>()
														.module(module)
														.beanClass(VisitorLoggingContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getIdCondition(logId, module))
														;
		
		VisitorLoggingContext records = builder.fetchFirst();
		return records;
	
	}
	
	public static VisitorLoggingContext getVisitorLogging(long visitorId, long inviteId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		SelectRecordsBuilder<VisitorLoggingContext> builder = new SelectRecordsBuilder<VisitorLoggingContext>()
														.module(module)
														.beanClass(VisitorLoggingContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("VISITOR", "visitor", String.valueOf(visitorId), NumberOperators.EQUALS))
														;
		
		if(inviteId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("VISITOR_INVITE", "visitorInvite", String.valueOf(inviteId), NumberOperators.EQUALS));
			
		}
		VisitorLoggingContext records = builder.fetchFirst();
		return records;
	
	}
	
	public static InviteVisitorRelContext getInviteVisitorRel(long visitorId, long inviteId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
		SelectRecordsBuilder<InviteVisitorRelContext> builder = new SelectRecordsBuilder<InviteVisitorRelContext>()
														.module(module)
														.beanClass(InviteVisitorRelContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("VISITOR_ID", "visitorId", String.valueOf(visitorId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition("INVITE_ID", "inviteId", String.valueOf(inviteId), NumberOperators.EQUALS))
														
														;
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		LookupFieldMeta inviteField = new LookupFieldMeta((LookupField) fieldsAsMap.get("inviteId"));
		LookupField inviteHost = (LookupField) modBean.getField("inviteHost", FacilioConstants.ContextNames.VISITOR_INVITE);
		
		LookupFieldMeta visitorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("visitorId"));
		LookupField visitorLocation = (LookupField) modBean.getField("location", FacilioConstants.ContextNames.VISITOR);
		inviteField.addChildLookupFIeld(inviteHost);
		visitorField.addChildLookupFIeld(visitorLocation);
		
		List<LookupField> additionaLookups = new ArrayList<LookupField>();
		additionaLookups.add(inviteField);
		additionaLookups.add(visitorField);
		builder.fetchLookups(additionaLookups);
		InviteVisitorRelContext records = builder.fetchFirst();
		return records;
	
	}
	
	public static VisitorInviteContext getVisitorInvite(long inviteId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE);
		SelectRecordsBuilder<VisitorInviteContext> builder = new SelectRecordsBuilder<VisitorInviteContext>()
														.module(module)
														.beanClass(VisitorInviteContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getIdCondition(inviteId, module))
														;
		
		VisitorInviteContext record = builder.fetchFirst();
		if(record != null) {
			List<BusinessHoursContext> businessHoursList = BusinessHoursAPI.getBusinessHours(Collections.singletonList(record.getVisitingHoursId()));
			if(CollectionUtils.isNotEmpty(businessHoursList)) {
				record.setRecurringVisitTime(businessHoursList.get(0));
			}
		}
		return record;
	
	}
	
	public static InviteVisitorRelContext getVisitorInviteRel(long inviteId, long visitorId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
		SelectRecordsBuilder<InviteVisitorRelContext> builder = new SelectRecordsBuilder<InviteVisitorRelContext>()
														.module(module)
														.beanClass(InviteVisitorRelContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("INVITE_ID", "inviteId", String.valueOf(inviteId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition("VISITOR_ID", "visitorId", String.valueOf(visitorId), NumberOperators.EQUALS))
														;
		
		InviteVisitorRelContext record = builder.fetchFirst();
		return record;
	
	}
	public static VisitorContext getVisitor(long id, String phoneNumber) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR);
		SelectRecordsBuilder<VisitorContext> builder = new SelectRecordsBuilder<VisitorContext>()
														.module(module)
														.beanClass(VisitorContext.class)
														.select(fields)
														;
		
		if(StringUtils.isNotEmpty(phoneNumber)) {
			builder.andCondition(CriteriaAPI.getCondition("PHONE", "phone", String.valueOf(phoneNumber), StringOperators.IS));
		}
		if(id > 0) {
			builder.andCondition(CriteriaAPI.getIdCondition(id, module));
		}
		
		VisitorContext records = builder.fetchFirst();
		return records;
	
	}
	
	public static void updateVisitorInviteStateToArrived(long visitorId, long visitorInviteId, String status) throws Exception {
		
		if(visitorId > 0 && visitorInviteId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
			List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
			
			FacilioStatus requiredStatus = TicketAPI.getStatus(module, status);
			
			UpdateRecordBuilder<InviteVisitorRelContext> updateBuilder = new UpdateRecordBuilder<InviteVisitorRelContext>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getCondition("INVITE_ID", "inviteId", String.valueOf(visitorInviteId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("VISITOR_ID", "visitorId", String.valueOf(visitorId), NumberOperators.EQUALS))
				;
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField statusField = modBean.getField("moduleState", module.getName());
			updateMap.put("moduleState", FieldUtil.getAsProperties(requiredStatus));
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			updatedfields.add(statusField);
		
			updateBuilder.updateViaMap(updateMap);
		}
		
	}
	
	public static void updateVisitorLogCheckInCheckoutTime(VisitorLoggingContext vLog, boolean isCheckIn, long time) throws Exception {
		
		if(vLog.getId() > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			
			Map<String, Object> updateMap = new HashMap<>();
			if(!isCheckIn) {
				FacilioField checkOutTimeField = modBean.getField("checkOutTime", module.getName());
				updateMap.put("checkOutTime", time);
				updatedfields.add(checkOutTimeField);
			}
			else {
				FacilioField checkInTimeField = modBean.getField("checkInTime", module.getName());
				updatedfields.add(checkInTimeField);
				updateMap.put("checkInTime", time);
				if(vLog.getExpectedVisitDuration() > 0) {
					FacilioField expectedCheckOutTimeField = modBean.getField("expectedCheckOutTime", module.getName());
					updateMap.put("expectedCheckOutTime", time + vLog.getExpectedVisitDuration());
					updatedfields.add(expectedCheckOutTimeField);
				}
			}
		
			UpdateRecordBuilder<VisitorLoggingContext> updateBuilder = new UpdateRecordBuilder<VisitorLoggingContext>()
					.module(module)
					.fields(updatedfields)
					.andCondition(CriteriaAPI.getIdCondition(vLog.getId(), module))
				;
			
			updateBuilder.updateViaMap(updateMap);
		}
		
	}
	
	public static void checkOutVisitorLogging(String visitorPhoneNumber, FacilioContext context) throws Exception {
		
		if(StringUtils.isNotEmpty(visitorPhoneNumber)) {
			VisitorContext visitor = getVisitor(-1, visitorPhoneNumber);
			if(visitor == null) {
				throw new IllegalArgumentException("Invalid phone number");
			}
			VisitorLoggingContext activeLog = getVisitorLogging(visitor.getId(), true);
			if(activeLog == null) {
				throw new IllegalArgumentException("No active CheckIn Log found");
			}
			List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(activeLog.getStateFlowId(), activeLog.getModuleState().getId(), FacilioConstants.ContextNames.VISITOR_LOGGING, activeLog, context);
			activeLog.setCheckOutTime(System.currentTimeMillis());
			long nextTransitionId = nextStateRule.get(0).getId();
			context.put("nextTransitionId", nextTransitionId);
			context.put("visitorLogging", activeLog);
		}
		
	}
	
	public static void updateVisitorLastVisitRollUps(VisitorLoggingContext visitorLog) throws Exception {
		
		if(visitorLog != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField lastVisitedTime = modBean.getField("lastVisitedTime", module.getName());
			FacilioField lastVisitedSpace = modBean.getField("lastVisitedSpace", module.getName());
			FacilioField lastVisitedHost = modBean.getField("lastVisitedHost", module.getName());
			FacilioField lastVisitDuration = modBean.getField("lastVisitDuration", module.getName());
			
			long workDuration = visitorLog.getCheckOutTime() - visitorLog.getCheckInTime();
			updateMap.put("lastVisitedTime", visitorLog.getCheckInTime());
			updateMap.put("lastVisitedHost", FieldUtil.getAsProperties(visitorLog.getHost()));
			updateMap.put("lastVisitDuration", workDuration);
			
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			updatedfields.add(lastVisitedTime);
			updatedfields.add(lastVisitedHost);
			updatedfields.add(lastVisitDuration);
			
			if(visitorLog.getVisitedSpace() != null) {
				updateMap.put("lastVisitedSpace", visitorLog.getVisitedSpace());
				updatedfields.add(lastVisitedSpace);
			}
			updatevisitor(visitorLog.getVisitor().getId(),updatedfields, updateMap);
		}
			
		
	}
	
	public static void updateVisitorRollUps(VisitorLoggingContext visitorLog) throws Exception {
		
		if(visitorLog != null) {
			VisitorLoggingContext updatedVisitorLog = getVisitorLogging(visitorLog.getId());
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField avatarId = modBean.getField("avatar", module.getName());
			FacilioField visitorType = modBean.getField("visitorType", module.getName());
			
			updateMap.put("visitorType", FieldUtil.getAsProperties(updatedVisitorLog.getVisitorType()));
			
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			if(updatedVisitorLog.getAvatarId() > 0) {
				updatedfields.add(avatarId);
				updateMap.put("avatar", visitorLog.getAvatar());
			}
			
			updatedfields.add(visitorType);
			
			updatevisitor(visitorLog.getVisitor().getId(),updatedfields, updateMap);
		}
			
		
	}
	
	public static void updateVisitorLogNDA(long logId, long fileId) throws Exception {
		
		if(logId > 0) {
//			AttachmentContext attachment = new AttachmentContext();
//			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			FacilioModule attachmentsModule = modBean.getModule("visitorloggingattachments");
//			attachment.setParentId(logId);
//			attachment.setModuleId(attachmentsModule.getModuleId());
//			attachment.setFileId(fileId);
//			attachment.setCreatedTime(System.currentTimeMillis());
//			AttachmentsAPI.addAttachments(Collections.singletonList(attachment), "visitorloggingattachments");

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField ndaIdField = modBean.getField("nda", module.getName());
			updateMap.put("nda", fileId);
			updatedfields.add(ndaIdField);
			
			UpdateRecordBuilder<VisitorLoggingContext> updateBuilder = new UpdateRecordBuilder<VisitorLoggingContext>()
					.module(module)
					.fields(updatedfields)
					.andCondition(CriteriaAPI.getIdCondition(logId, module))
				;
			
			updateBuilder.updateViaMap(updateMap);
			
		}
		
	}
	
	public static void updatevisitor(long visitorId, List<FacilioField> fields, Map<String, Object> updateMap) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
		
		UpdateRecordBuilder<VisitorContext> updateBuilder = new UpdateRecordBuilder<VisitorContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(visitorId, module))
			;
		updateBuilder.updateViaMap(updateMap);
	
	}
	

	public static WatchListContext getBlockedWatchListRecordForPhoneNumber(String phoneNumber, String email) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WATCHLIST);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.WATCHLIST);
		SelectRecordsBuilder<WatchListContext> builder = new SelectRecordsBuilder<WatchListContext>()
														.module(module)
														.beanClass(WatchListContext.class)
														.select(fields)
														;
		Criteria criteria = new Criteria();
		if(StringUtils.isNotEmpty(phoneNumber)) {
			criteria.addAndCondition(CriteriaAPI.getCondition("PHONE", "phone", String.valueOf(phoneNumber), StringOperators.IS));
		}
		if(StringUtils.isNotEmpty(email)) {
			criteria.addOrCondition(CriteriaAPI.getCondition("EMAIL", "email", String.valueOf(email), StringOperators.IS));
		}
		
		builder.andCriteria(criteria);
		builder.andCondition(CriteriaAPI.getCondition("IS_BLOCKED", "isBlocked", "true", BooleanOperators.IS));
		
		WatchListContext record = builder.fetchFirst();
		return record;
				
		
	}
	
	public static void deleteVisitorInviteRel(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
		DeleteRecordBuilder<InviteVisitorRelContext> deleteTermsBuilder = new DeleteRecordBuilder<InviteVisitorRelContext>()
				.module(module)
				.andCondition(CriteriaAPI.getCondition("INVITE_ID", "inviteId", String.valueOf(id), NumberOperators.EQUALS));
		deleteTermsBuilder.delete();
		
		
	}
}
