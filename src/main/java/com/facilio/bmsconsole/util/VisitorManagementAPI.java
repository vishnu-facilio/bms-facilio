package com.facilio.bmsconsole.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.LookupFieldMeta;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

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
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		List<LookupField> additionaLookups = new ArrayList<LookupField>();
		LookupField contactField = (LookupField) fieldsAsMap.get("visitor");
		LookupField hostField = (LookupField) fieldsAsMap.get("host");
		
		additionaLookups.add(contactField);
		additionaLookups.add(hostField);
		
		builder.fetchLookups(additionaLookups);
		
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
	
	public static List<VisitorLoggingContext> getAllVisitorLogging(long visitorId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		SelectRecordsBuilder<VisitorLoggingContext> builder = new SelectRecordsBuilder<VisitorLoggingContext>()
														.module(module)
														.beanClass(VisitorLoggingContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("VISITOR", "visitor", String.valueOf(visitorId), NumberOperators.EQUALS))
														;
		
		List<VisitorLoggingContext> records = builder.get();
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
	
	public static InviteVisitorRelContext getInviteVisitorRel(long id) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
		SelectRecordsBuilder<InviteVisitorRelContext> builder = new SelectRecordsBuilder<InviteVisitorRelContext>()
														.module(module)
														.beanClass(InviteVisitorRelContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getIdCondition(id, module))
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
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		List<LookupField> additionaLookups = new ArrayList<LookupField>();
		LookupField inviteField = (LookupField) fieldsAsMap.get("inviteId");
		LookupField visitorField = (LookupField) fieldsAsMap.get("visitorId");
		
		additionaLookups.add(inviteField);
		additionaLookups.add(visitorField);
		
		builder.fetchLookups(additionaLookups);
		
		
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
				vLog.setCheckOutTime(time);
			}
			else {
				FacilioField checkInTimeField = modBean.getField("checkInTime", module.getName());
				updatedfields.add(checkInTimeField);
				updateMap.put("checkInTime", time);
				vLog.setCheckInTime(time);
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
	
	public static void updateVisitorLogNDA(long logId, File file) throws Exception {
		
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
			updateMap.put("nda", file);
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
	
	public static Preference getWelcomeMailNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("welcome_MailNotification", "Welcome Mail Notifications", form, "Welcome Mail Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveWelcomeMailNotifications(map, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}
	
			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}
	
		};
	
	}
	
	public static Preference getWelcomeSmsNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("welcome_SmsNotification", "Welcome SMS Notifications", form, "Welcome Mail Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveWelcomeSmsNotifications(map, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}
	
			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}
	
		};
	
	}
	

	public static Preference getThanksMailNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("thanks_MailNotification", "Thanks Mail Notifications", form, "Thanks Mail Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveThanksEmailNotificationPrefs(map, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}
	
			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}
	
		};
	
	}
	
	public static Preference getInviteMailNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("invite_MailNotification", "Invite Mail Notifications", form, "Invite Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
				Long ruleId = saveInviteEmailNotificationPrefs(map, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}
	
			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}
	
		};
	
	}

	
	public static Preference getApprovalMailNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("approval_MailNotification", "Approval Mail Notifications", form, "Approval Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveApprovalNotificationPrefs(map, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}
	
			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}
	
		};
	
	}

	
	public static Preference getHostMailNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("host_MailNotification", "Host Email Notifications", form, "Host Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveHostMailNotificationPrefs(map, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}
	
			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}
	
		};
	
	}
	
	public static Preference getHostSmsNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("host_SmsNotification", "Host Sms Notifications", form, "Host Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveHostSmsNotificationPrefs(map, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}
	
			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}
	
		};
	
	}
	
	public static Preference getHostWappNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("host_WappNotification", "Host Wapp Notifications", form, "Host Notifications") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveHostMailNotificationPrefs(map, module.getName());
				List<Long> ruleIdList = new ArrayList<>();
				ruleIdList.add(ruleId);
				PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
			}
	
			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}
	
		};
	
	}
	
	public static Long saveHostMailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Host Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("host");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.HOST");
		
		Condition isApprovalNeeded = new Condition();
		isApprovalNeeded.setFieldName("isApprovalNeeded");
		isApprovalNeeded.setOperator(BooleanOperators.IS);
		isApprovalNeeded.setValue("false");
		isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");
		
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(isApprovalNeeded);
		criteria.setPattern("(1 and 2)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(102);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	
	public static Long saveHostSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Host Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("host");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.HOST");
		
		Condition isApprovalNeeded = new Condition();
		isApprovalNeeded.setFieldName("isApprovalNeeded");
		isApprovalNeeded.setOperator(BooleanOperators.IS);
		isApprovalNeeded.setValue("false");
		isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(isApprovalNeeded);
		
		criteria.setPattern("(1 and 2)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.SMS_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(107);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	
	private static Long saveWelcomeMailNotifications(Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Welcome MAIL Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.VISITOR_ID");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		
		criteria.setPattern("(1)");
		criteria.andCriteria(getVisitorLogStatusCriteria("CheckedIn"));
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(103);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}

	private static Long saveWelcomeSmsNotifications(Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Welcome MAIL Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.VISITOR_ID");
		
		FacilioStatus status = TicketAPI.getStatus(module, "CheckedOut");
		Condition status_condition = new Condition();
		status_condition.setFieldName("moduleState");
		status_condition.setOperator(NumberOperators.EQUALS);
		status_condition.setValue(String.valueOf(status.getId()));
		status_condition.setColumnName("VisitorLogging.MODULE_STATE");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(status_condition);
		
		criteria.setPattern("(1 and 2)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.SMS_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(103);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}

	
	
	public static Long saveThanksEmailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Thanks Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.VISITOR_ID");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		
		criteria.setPattern("(1)");
		criteria.andCriteria(getVisitorLogStatusCriteria("CheckedOut"));
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(104);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Long saveInviteEmailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Invite Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);
		
		Condition condition = new Condition();
		condition.setFieldName("visitorId");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("Invite_Visitor_Rel.VISITOR_ID");
		
		Condition inviteCondition = new Condition();
		inviteCondition.setFieldName("inviteId");
		inviteCondition.setOperator(CommonOperators.IS_NOT_EMPTY);
		inviteCondition.setColumnName("Invite_Visitor_Rel.INVITE_ID");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(inviteCondition);
		
		criteria.setPattern("(1 and 2)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(105);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Long saveApprovalNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Approval Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE_OR_EDIT);
		
		Condition condition = new Condition();
		condition.setFieldName("host");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.HOST");
		
		Condition isApprovalNeeded = new Condition();
		isApprovalNeeded.setFieldName("isApprovalNeeded");
		isApprovalNeeded.setOperator(BooleanOperators.IS);
		isApprovalNeeded.setValue("true");
		isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");
		
		
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(isApprovalNeeded);
		
		criteria.setPattern("(1 and 3)");
		criteria.andCriteria(getVisitorLogStatusCriteria("Requested"));
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(106);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	private static Criteria getVisitorLogStatusCriteria(String status) {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.IS);
		statusCondition.setValue(status);

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getVisitorLoggingModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		return criteria;
	}

	

}
