package com.facilio.bmsconsole.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
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
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.WorkflowContext;

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
	
	public static Boolean checkExistingVisitorLogging(long logId) throws Exception {
		long currenttime = System.currentTimeMillis();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		SelectRecordsBuilder<VisitorLoggingContext> builder = new SelectRecordsBuilder<VisitorLoggingContext>()
														.module(module)
														.beanClass(VisitorLoggingContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getIdCondition(logId, module))
													
														;
		FacilioStatus checkedOutStatus = TicketAPI.getStatus(module, "CheckedOut");
		
		VisitorLoggingContext records = builder.fetchFirst();
		if(records != null && records.getModuleState().getId() == checkedOutStatus.getId()) {
			if(currenttime >= records.getCheckInTime()) {
				if(records.getExpectedCheckOutTime() <= 0) {
					return true;
				}
				if(records.getExpectedCheckOutTime() > 0 && currenttime <= records.getExpectedCheckOutTime()) {
					return true;
				}
				return false;
			}
			return false;
		}
		return null;
	
	}
	
	public static List<VisitorLoggingContext> getRecurringVisitorLogs() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		Map<String, FacilioField> map = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<VisitorLoggingContext> builder = new SelectRecordsBuilder<VisitorLoggingContext>()
														.module(module)
														.beanClass(VisitorLoggingContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("IS_RECURRING", "isRecurring", "true", BooleanOperators.IS))
														.andCondition(CriteriaAPI.getCondition(map.get("parentLogId"),CommonOperators.IS_EMPTY))
														;
		
		List<VisitorLoggingContext> records = builder.get();
		return records;
	
	}
	
	public static VisitorLoggingContext getVisitorLoggingTriggers(long logId, String passCode, boolean fetchTriggers) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		SelectRecordsBuilder<VisitorLoggingContext> builder = new SelectRecordsBuilder<VisitorLoggingContext>()
														.module(module)
														.beanClass(VisitorLoggingContext.class)
														.select(fields)
														
														;
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		List<LookupField> additionaLookups = new ArrayList<LookupField>();
		
		if(logId > 0) {
			builder.andCondition(CriteriaAPI.getIdCondition(logId, module));
		}
		
		if(StringUtils.isNotEmpty(passCode)) {
			builder.andCondition(CriteriaAPI.getCondition("PASSCODE", "passcode", passCode, StringOperators.IS));
		}
		
		LookupField contactField = (LookupField) fieldsAsMap.get("visitor");
		LookupField hostField = (LookupField) fieldsAsMap.get("host");
		LookupField visitedSpacefield = (LookupField) fieldsAsMap.get("visitedSpace");
		
		additionaLookups.add(contactField);
		additionaLookups.add(hostField);
		additionaLookups.add(visitedSpacefield);
		
		builder.fetchLookups(additionaLookups);
		
		VisitorLoggingContext records = builder.fetchFirst();
		if(records != null && records.isRecurring() && fetchTriggers) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getVisitorLogTriggerFields())
					.table(ModuleFactory.getVisitorLogTriggersModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("VISITOR_LOG_ID", "pmId", String.valueOf(records.getId()), NumberOperators.EQUALS));
			List<Map<String, Object>> map = selectBuilder.get();
			if(CollectionUtils.isNotEmpty(map)) {
				records.setTrigger(FieldUtil.getAsBeanFromMap(map.get(0), PMTriggerContext.class));
			}
	
		}
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
	
	public static VisitorLoggingContext getValidChildLogForToday(long parentLogId, long currentTime, boolean fetchUpcoming, long visitorId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		FacilioStatus status = TicketAPI.getStatus(module, "Upcoming");		
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		SelectRecordsBuilder<VisitorLoggingContext> builder = new SelectRecordsBuilder<VisitorLoggingContext>()
														.module(module)
														.beanClass(VisitorLoggingContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("EXPECTED_CHECKIN_TIME", "expectedCheckInTime", String.valueOf(currentTime) , DateOperators.IS_BEFORE))
														.andCondition(CriteriaAPI.getCondition("EXPECTED_CHECKOUT_TIME", "expectedCheckOutnTime", String.valueOf(currentTime) , DateOperators.IS_AFTER));
		
		if(parentLogId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("PARENT_LOG_ID", "parentLogId", String.valueOf(parentLogId), NumberOperators.EQUALS));
		}
		if(visitorId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("VISITOR", "visitor", String.valueOf(visitorId), NumberOperators.EQUALS));
		}
		if(fetchUpcoming) {
			builder.andCondition(CriteriaAPI.getCondition("MODULE_STATE", "moduleState", String.valueOf(status.getId()), NumberOperators.EQUALS));
		}
		
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		LookupFieldMeta visitorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("visitor"));
		List<LookupField> additionaLookups = new ArrayList<LookupField>();
		additionaLookups.add(visitorField);
		builder.fetchLookups(additionaLookups);
		
		
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
		
		builder.orderBy("Visitor_Logging.SYS_CREATED_TIME DESC");
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
				//FacilioField actualVisitdurationField = modBean.getField("actualVisitDuration", module.getName());
				
				FacilioField overStayField = modBean.getField("isOverStay", module.getName());
				boolean isOverStay = false;
				if(time - vLog.getExpectedCheckOutTime() > 0) {
					isOverStay = true;
				}
				else {
					isOverStay = false;
				}
				long actualVisitDuration = time - vLog.getCheckInTime();
				updateMap.put("isOverStay", isOverStay);
				//updateMap.put("actualVisitDuration", actualVisitDuration);
				
				updatedfields.add(checkOutTimeField);
				updatedfields.add(overStayField);
			//	updatedfields.add(actualVisitdurationField);
				
				vLog.setCheckOutTime(time);
			//	vLog.setActualVisitDuration(actualVisitDuration);
				vLog.setIsOverStay(isOverStay);
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
	
	public static void updateVisitorLogInvitationStatus(VisitorLoggingContext vLog, boolean isInvitationSent) throws Exception {
		
		if(vLog.getId() > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField invitationSentField = modBean.getField("isInvitationSent", module.getName());
			updateMap.put("isInvitationSent", isInvitationSent);
			updatedfields.add(invitationSentField);
			
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
			FacilioField firstVisitedTime = modBean.getField("firstVisitedTime", module.getName());
			
			updateMap.put("lastVisitedTime", visitorLog.getCheckInTime());
			updateMap.put("lastVisitedHost", FieldUtil.getAsProperties(visitorLog.getHost()));
			
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			updatedfields.add(lastVisitedTime);
			updatedfields.add(lastVisitedHost);
			
			if(visitorLog.getVisitedSpace() != null) {
				updateMap.put("lastVisitedSpace", FieldUtil.getAsProperties(visitorLog.getVisitedSpace()));
				updatedfields.add(lastVisitedSpace);
			}
			VisitorContext visitor = VisitorManagementAPI.getVisitor(visitorLog.getVisitor().getId(), null);
			if(visitor.getFirstVisitedTime() <= 0) {
				updateMap.put("firstVisitedTime", visitorLog.getCheckInTime());
				updatedfields.add(firstVisitedTime);
			}
			updatevisitor(visitorLog.getVisitor().getId(),updatedfields, updateMap);
		}
			
		
	}
	
	public static void updateVisitorLastVisitDurationRollUp(VisitorLoggingContext visitorLog) throws Exception {
		
		if(visitorLog != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField lastVisitDuration = modBean.getField("lastVisitDuration", module.getName());
			
			long workDuration = visitorLog.getCheckOutTime() - visitorLog.getCheckInTime();
			updateMap.put("lastVisitDuration", workDuration);
			
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			updatedfields.add(lastVisitDuration);
			
			updatevisitor(visitorLog.getVisitor().getId(),updatedfields, updateMap);
		}
			
		
	}
	
	public static void updateVisitorRollUps(VisitorLoggingContext visitorLog, VisitorLoggingContext oldRecord) throws Exception {
		
		if(visitorLog != null) {
			VisitorLoggingContext updatedVisitorLog = getVisitorLoggingTriggers(visitorLog.getId(), null, false);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR);
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField avatarId = modBean.getField("avatar", module.getName());
			FacilioField visitorType = modBean.getField("visitorType", module.getName());
			
			updateMap.put("visitorType", FieldUtil.getAsProperties(updatedVisitorLog.getVisitorType()));
			
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			if(updatedVisitorLog.getAvatarId() > 0) {
				updatedfields.add(avatarId);
				updateMap.put("avatar", oldRecord.getAvatar());
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
		//builder.andCondition(CriteriaAPI.getCondition("IS_BLOCKED", "isBlocked", "true", BooleanOperators.IS));
		
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
		return new Preference("welcomeVisitor_MailNotification", "Welcome Visitor_Email", form, "Notify Visitors when they check-in") {
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
		return new Preference("welcomeVisitor_SmsNotification", "Welcome Visitor_SMS", form, "Notify Visitors when they check-in") {
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
	
	public static Preference getWelcomeWhatsappNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("welcomeVisitor_WhatsappNotification", "Welcome Visitor_Whatsapp", form, "Notify Visitors when they check-in") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveWelcomeWhatsappNotifications(map, module.getName());
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
		return new Preference("thankVisitor_MailNotification", "Thank Visitor_Email", form, "Notify Visitor when they check-out") {
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
	
	public static Preference getThanksSmsNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("thankVisitor_SmsNotification", "Thank Visitor_SMS", form, "Notify Visitor when they check-out") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveThanksSmsNotificationPrefs(map, module.getName());
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
	
	public static Preference getThanksWhatsappNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("thankVisitor_WhatsappNotification", "Thank Visitor_Whatsapp", form, "Notify Visitor when they check-out") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveThanksWhatsappNotificationPrefs(map, module.getName());
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
		return new Preference("inviteVisitor_MailNotification", "Invite Visitor_Email", form, "Notify invited visitors before the day of Visit") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
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
	
	public static Preference getInviteSmsNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("inviteVisitor_SmsNotification", "Invite Visitor_SMS", form, "Notify invited visitors before the day of Visit") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
				Long ruleId = saveInviteSmsNotificationPrefs(map, module.getName());
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
	
	public static Preference getInviteWhatsappNotificationsPref() {			
			FacilioForm form = new FacilioForm();
			return new Preference("inviteVisitor_WhatsappNotification", "Invite Visitor_Whatsapp", form, "Notify invited visitors before the day of Visit") {
				@Override
				public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
					Long ruleId = saveInviteWhatsappNotificationPrefs(map, module.getName());
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
		return new Preference("approveVisitor_MailNotification", "Approve Visitor_Email", form, "Notify Hosts requesting approval for their Visitors") {
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

	public static Preference getApprovalSmsNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("approveVisitor_SmsNotification", "Approve Visitor_SMS", form, "Notify Hosts requesting approval for their Visitors") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveApprovalSmsNotificationPrefs(map, module.getName());
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
	
	public static Preference getApprovalWhatsappNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("approveVisitor_WhatsappNotification", "Approve Visitor_Whatsapp", form, "Notify Hosts requesting approval for their Visitors") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveApprovalWhatsappNotificationPrefs(map, module.getName());
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
		return new Preference("notifyHost_MailNotification", "Notify Host_Email", form, "Automatically notify hosts when their visitors arrive") {
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
		return new Preference("notifyHost_SmsNotification", "Notify Host_SMS", form, "Automatically notify hosts when their visitors arrive") {
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
	
	public static Preference getHostWhatsappNotificationsPref() {
		
		FacilioForm form = new FacilioForm();
		return new Preference("notifyHost_WhatsappNotification", "Notify Host_Whatsapp", form, "Automatically notify hosts when their visitors arrive") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveHostWhatsappNotificationPrefs(map, module.getName());
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
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));
		
		criteria.setPattern("(1 and 2 and 3)");
		
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
		workflowRuleContext.setName("Host SMS Notification");
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
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));
		criteria.setPattern("(1 and 2 and 3)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext smsAction = new ActionContext();
		smsAction.setActionType(ActionType.SMS_NOTIFICATION);
		
		smsAction.setDefaultTemplateId(107);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(smsAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	
	public static Long saveHostWhatsappNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Host Whatsapp Notification");
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
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));
		criteria.setPattern("(1 and 2 and 3)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext whatsappAction = new ActionContext();
		whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);
		
		whatsappAction.setDefaultTemplateId(110);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
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
		condition.setColumnName("VisitorLogging.VISITOR");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		
		criteria.setPattern("(1 and 2)");
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));
		
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
		workflowRuleContext.setName("Welcome SMS Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.VISITOR");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		
		criteria.setPattern("(1 and 2)");
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.SMS_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(91);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}

	private static Long saveWelcomeWhatsappNotifications(Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Welcome Whatsapp Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.VISITOR");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		
		criteria.setPattern("(1 and 2)");
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedIn"));
		
		workflowRuleContext.setCriteria(criteria);		
		
		ActionContext whatsappAction = new ActionContext();
		whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);	
		whatsappAction.setDefaultTemplateId(109);
		
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
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
		workflowRuleContext.setActivityType(EventType.EDIT);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.VISITOR");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		
		criteria.setPattern("(1 and 2)");
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedOut"));
		
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
	
	public static Long saveThanksSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Thanks SMS Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.EDIT);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.VISITOR");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		
		criteria.setPattern("(1 and 2)");
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedOut"));
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.SMS_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(101);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Long saveThanksWhatsappNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Thanks Whatsapp Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.EDIT);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.VISITOR");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		
		criteria.setPattern("(1 and 2)");
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("CheckedOut"));
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext whatsappAction = new ActionContext();
		whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);
		
		whatsappAction.setDefaultTemplateId(111);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));
        
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
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("Visitor_Logging.VISITOR");
		
		Condition preregisteredCondition = new Condition();
		preregisteredCondition.setFieldName("isPreregistered");
		preregisteredCondition.setOperator(BooleanOperators.IS);
		preregisteredCondition.setValue("true");
		preregisteredCondition.setColumnName("Visitor_Logging.IS_PREREGISTERED");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(preregisteredCondition);
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Upcoming"));
		
		
		criteria.setPattern("(1 and 2 and 3)");
		
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
	
	
	public static Long saveInviteSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Invite SMS Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("Visitor_Logging.VISITOR");
		
		Condition preregisteredCondition = new Condition();
		preregisteredCondition.setFieldName("isPreregistered");
		preregisteredCondition.setOperator(BooleanOperators.IS);
		preregisteredCondition.setValue("true");
		preregisteredCondition.setColumnName("Visitor_Logging.IS_PREREGISTERED");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(preregisteredCondition);
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Upcoming"));
		
		
		criteria.setPattern("(1 and 2 and 3)");
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.SMS_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(106);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Long saveInviteWhatsappNotificationPrefs(Map<String, Object> map, String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
				
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Invite Whatsapp Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("visitor");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("Visitor_Logging.VISITOR");
		
		Condition preregisteredCondition = new Condition();
		preregisteredCondition.setFieldName("isPreregistered");
		preregisteredCondition.setOperator(BooleanOperators.IS);
		preregisteredCondition.setValue("true");
		preregisteredCondition.setColumnName("Visitor_Logging.IS_PREREGISTERED");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(preregisteredCondition);
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Upcoming"));
		
		
		criteria.setPattern("(1 and 2 and 3)");
		
		workflowRuleContext.setCriteria(criteria);
		
		ActionContext whatsappAction = new ActionContext();
		whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);
		whatsappAction.setDefaultTemplateId(112);

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));
        
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
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition condition = new Condition();
		condition.setFieldName("host");
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);
		condition.setColumnName("VisitorLogging.HOST");
		
		Condition isApprovalNeeded = new Condition();
		isApprovalNeeded.setFieldName("isApprovalNeeded");
		isApprovalNeeded.setOperator(BooleanOperators.IS);
		isApprovalNeeded.setValue("true");
		isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");
		
		Condition isPreregistered = new Condition();
		isPreregistered.setFieldName("isPreregistered");
		isPreregistered.setOperator(BooleanOperators.IS);
		isPreregistered.setValue("false");
		isPreregistered.setColumnName("VisitorLogging.IS_PREREGISTERED");
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(isApprovalNeeded);
		criteria.addConditionMap(isPreregistered);
		
		criteria.setPattern("(1 and 2 and 3 and 4)");
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Requested"));
		
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
	
	public static Long saveApprovalSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Approval SMS Notification");
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
		isApprovalNeeded.setValue("true");
		isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");
		
		Condition isPreregistered = new Condition();
		isPreregistered.setFieldName("isPreregistered");
		isPreregistered.setOperator(BooleanOperators.IS);
		isPreregistered.setValue("false");
		isPreregistered.setColumnName("VisitorLogging.IS_PREREGISTERED");
		
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(isApprovalNeeded);
		criteria.addConditionMap(isPreregistered);
		
		criteria.setPattern("(1 and 2 and 3 and 4)");
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Requested"));
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.SMS_NOTIFICATION);
		
		emailAction.setDefaultTemplateId(99);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	public static Long saveApprovalWhatsappNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Approval Whatsapp Notification");
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
		isApprovalNeeded.setValue("true");
		isApprovalNeeded.setColumnName("VisitorLogging.IS_APPROVAL_NEEDED");
		
		Condition isPreregistered = new Condition();
		isPreregistered.setFieldName("isPreregistered");
		isPreregistered.setOperator(BooleanOperators.IS);
		isPreregistered.setValue("false");
		isPreregistered.setColumnName("VisitorLogging.IS_PREREGISTERED");
				
		Criteria criteria = new Criteria();
		criteria.addConditionMap(condition);
		criteria.addConditionMap(isApprovalNeeded);
		criteria.addConditionMap(isPreregistered);
		
		criteria.setPattern("(1 and 2 and 3 and 4)");
		criteria.addConditionMap(ViewFactory.getVisitorLogStatusCriteria("Requested"));
		
		workflowRuleContext.setCriteria(criteria);
		
		
		ActionContext whatsappAction = new ActionContext();
		whatsappAction.setActionType(ActionType.WHATSAPP_MESSAGE);
		
		whatsappAction.setDefaultTemplateId(113);
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(whatsappAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static void scheduleVisitorLog(long visitorLogId, ScheduleActions action, long endTime) throws Exception {
		BmsJobUtil.deleteJobsWithProps(Collections.singletonList(visitorLogId), "ScheduleNewVisitorLogs");
	}
	
	public static long getEndTime(long startTime, List<PMTriggerContext> triggers) {
		Optional<PMTriggerContext> minTrigger = triggers.stream().min(Comparator.comparingInt(PMTriggerContext::getFrequency));

		int maxSchedulingDays = minTrigger.get().getFrequencyEnum().getMaxSchedulingDays();
		if (startTime == -1) {
			return DateTimeUtil.getDayStartTime(maxSchedulingDays, true) - 1;
		}

		return startTime + (maxSchedulingDays * 24 * 60 * 60);
	}
	
	public static long getStartTime(int action, VisitorLoggingContext log, PMTriggerContext trigger) {
		long startTime = -1;
		if (action == 1) {
			startTime = getStartTimeInSecond(log.getExpectedCheckInTime());
		} else if (action == 2) {
			startTime = log.getLogGeneratedUpto();
		}
		return startTime;
	}
	
	public static long getStartTimeInSecond(long startTime) {
		
		long startTimeInSecond = startTime / 1000;
		startTimeInSecond = startTimeInSecond - 300; //for calculating next execution time

		return startTimeInSecond;
	}

	public static void updateGeneratedUptoInLogAndAddChildren(PMTriggerContext trigger, VisitorLoggingContext parentLog, List<VisitorLoggingContext> children) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		RecordAPI.addRecord(true, children, module, fields);
		RecordAPI.updateRecord(parentLog, module, fields);
		updateTrigger(trigger);
	}
	
	public static void updateTrigger(PMTriggerContext trigger) throws Exception {
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getVisitorLogTriggersModule().getTableName())
				.fields(FieldFactory.getVisitorLogTriggerFields())
				.andCondition(CriteriaAPI.getIdCondition(trigger.getId(), ModuleFactory.getVisitorLogTriggersModule()));
		update.update(FieldUtil.getAsProperties(trigger));
	}
	
	public enum ScheduleActions {
		GENERATION,
		NIGHTLY;

		public int getVal() {
			return ordinal() + 1;
		}

		public static ScheduleActions getEnum(int val) {
			if (val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}

	public static void deleteUpcomingChildLogs(long parentLogId, long currentTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		DeleteRecordBuilder<VisitorLoggingContext> deleteBuilder = new DeleteRecordBuilder<VisitorLoggingContext>()
				.module(module)
				.andCondition(CriteriaAPI.getCondition("PARENT_LOG_ID", "parentLogId", String.valueOf(parentLogId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("EXPECTED_CHECKIN_TIME", "expectedCheckInTime", String.valueOf(currentTime), DateOperators.IS_AFTER));
		
		deleteBuilder.markAsDelete();
	
	}
	
	public static FacilioStatus getLogStatus(String statusString) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		FacilioStatus status = TicketAPI.getStatus(module, statusString);
		return status;
	
	}
	
	public static Map<Long, VisitorSettingsContext> getVisitorSettingsForType() throws Exception {
		FacilioModule module = ModuleFactory.getVisitorSettingsModule();
		List<FacilioField> fields = FieldFactory.getVisitorSettingsFields();
		Map<Long, VisitorSettingsContext> typeSettingsMap = new HashMap<Long, VisitorSettingsContext>();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName());
		List<Map<String, Object>> map = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(map)) {
			for(Map<String, Object> visitorSetting : map) {
				VisitorSettingsContext settingBean = FieldUtil.getAsBeanFromMap(visitorSetting, VisitorSettingsContext.class);
				typeSettingsMap.put(settingBean.getVisitorTypeId(), settingBean);
			}
		}
		return typeSettingsMap;

	}
	
	private static boolean passcodeExists(String passCode) throws Exception{
		
		VisitorLoggingContext vLog = getVisitorLoggingTriggers(-1, passCode, false);
		return !(vLog == null);
	}
	
	public static String generatePassCode() throws Exception {
		String passCode = null;
		Random random = new Random();
		while(true) {
			passCode = String.format("%04d", random.nextInt(10000));
			if(!passcodeExists(passCode)) {
				break;
			}
		}
		return passCode;
	} 
	
	public static Long saveBlockedVisitorMailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Blocked Visitor Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition isBlocked = new Condition();
		isBlocked.setFieldName("isBlocked");
		isBlocked.setOperator(BooleanOperators.IS);
		isBlocked.setValue("true");
		isBlocked.setColumnName("VisitorLogging.IS_BLOCKED");
		
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(isBlocked);
		
		criteria.setPattern("(1)");
		
		workflowRuleContext.setCriteria(criteria);
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		
		List<String> ouIdList = (List<String>)map.get("to");
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		
		StringJoiner userEmailStr = new StringJoiner(",");
		for(String ouId : ouIdList) {
			User user = userBean.getUser(Long.parseLong(ouId), false);
			if(user != null) {
				userEmailStr.add(user.getEmail());
			}
		}
		
		EMailTemplate temp = addTemplate("Blocked Email Template", userEmailStr.toString(), 114);
		emailAction.setTemplateId(temp.getId());
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	public static Long saveVipVisitorMailNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("VIP Visitor Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition isVip = new Condition();
		isVip.setFieldName("isVip");
		isVip.setOperator(BooleanOperators.IS);
		isVip.setValue("true");
		isVip.setColumnName("VisitorLogging.IS_VIP");
		
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(isVip);
		
		criteria.setPattern("(1)");
		
		workflowRuleContext.setCriteria(criteria);
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.EMAIL_NOTIFICATION);
		
		List<String> ouIdList = (List<String>)map.get("to");
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		
		StringJoiner userEmailStr = new StringJoiner(",");
		for(String ouId : ouIdList) {
			User user = userBean.getUser(Long.parseLong(ouId), false);
			if(user != null) {
				userEmailStr.add(user.getEmail());
			}
		}
		
		EMailTemplate temp = addTemplate("Vip Email Template", userEmailStr.toString(), 118);
		emailAction.setTemplateId(temp.getId());
		//add rule,action and job
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
        
		FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
		chain.execute(context);
	
		return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
	}
	
	
	public static Long saveBlockedVisitorSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("Blocked Visitor Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition isBlocked = new Condition();
		isBlocked.setFieldName("isBlocked");
		isBlocked.setOperator(BooleanOperators.IS);
		isBlocked.setValue("true");
		isBlocked.setColumnName("VisitorLogging.IS_BLOCKED");
		
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(isBlocked);
		
		criteria.setPattern("(1)");
		
		workflowRuleContext.setCriteria(criteria);
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.SMS_NOTIFICATION);
		
		List<String> ouIdList = (List<String>)map.get("to");
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		
		StringJoiner userEmailStr = new StringJoiner(",");
		for(String ouId : ouIdList) {
			User user = userBean.getUser(Long.parseLong(ouId), false);
			if(user != null && StringUtils.isNotEmpty(user.getMobile())) {
				userEmailStr.add(user.getMobile());
			}
		}
		if(!userEmailStr.isEmpty()) {
		
			SMSTemplate temp = addSmsTemplate("Blocked SMS Template", userEmailStr.toString(), 115);
			emailAction.setTemplateId(temp.getId());
			//add rule,action and job
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
			context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
	        
			FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
			chain.execute(context);
		
			return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		}
		return null;
	}
	
	
	public static Preference getBlockedMailNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Blocked Visitor Notification Preference");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User(s)", Required.REQUIRED,"users", 1, 1));
		
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		form.setFields(fields);
		form.setLabelPosition(LabelPosition.TOP);
		return new Preference("notifyBlocked_MailNotification", "Notify on Blocked visitor checkin_Email", form, "Define who needs to be notified when a visitor matches a blocked record in watchlist") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveBlockedVisitorMailNotificationPrefs(map, module.getName());
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
	
	public static Preference getVipVisitorMailNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("VIP Visitor Notification Preference");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User(s)", Required.REQUIRED,"users", 1, 1));
		
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		form.setFields(fields);
		form.setLabelPosition(LabelPosition.TOP);
		return new Preference("notifyVip_MailNotification", "Notify on VIP visitor checkin_Email", form, "Define who needs to be notified when a visitor matches a VIP record in watchlist") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveVipVisitorMailNotificationPrefs(map, module.getName());
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
	private static EMailTemplate addTemplate(String name, String email, int defaultTempId) throws Exception {
		EMailTemplate emailTemplate = new EMailTemplate();
		JSONObject blockedVisitorMailJson = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, defaultTempId).getOriginalTemplate(); 
		WorkflowContext wf = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, defaultTempId).getWorkflow();
		emailTemplate.setName("Name");
		emailTemplate.setType(Template.Type.EMAIL);
		emailTemplate.setFrom((String) blockedVisitorMailJson.get("sender"));
		emailTemplate.setTo(email);
		emailTemplate.setSubject((String) blockedVisitorMailJson.get("subject"));
		emailTemplate.setMessage((String) blockedVisitorMailJson.get("message"));
		emailTemplate.setFtl(true);
		emailTemplate.setWorkflow(wf);
		
		long id = TemplateAPI.addEmailTemplate(AccountUtil.getCurrentOrg().getOrgId(), emailTemplate);
		emailTemplate.setId(id);
		
		return emailTemplate;
	}
	
	private static SMSTemplate addSmsTemplate(String name, String phoneNumbers, int defaultTempId) throws Exception {
		SMSTemplate smsTemplate = new SMSTemplate();
		JSONObject blockedVisitorMailJson = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, defaultTempId).getOriginalTemplate(); 
		WorkflowContext wf = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, defaultTempId).getWorkflow();
		smsTemplate.setName("Name");
		smsTemplate.setType(Template.Type.SMS);
		smsTemplate.setFrom((String) blockedVisitorMailJson.get("sender"));
		smsTemplate.setTo(phoneNumbers);
		smsTemplate.setMessage((String) blockedVisitorMailJson.get("message"));
		smsTemplate.setFtl(true);
		smsTemplate.setWorkflow(wf);
		
		long id = TemplateAPI.addSMSTemplate(AccountUtil.getCurrentOrg().getOrgId(), smsTemplate);
		smsTemplate.setId(id);
		
		return smsTemplate;
	}
	
	public static Preference getBlockedSmsNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("Blocked Visitor SMS Notification Preference");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User(s)", Required.REQUIRED,"users", 1, 1));
		
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		form.setFields(fields);
		form.setLabelPosition(LabelPosition.TOP);
		return new Preference("notifyBlocked_SmsNotification", "Notify on Blocked visitor checkin_SMS", form, "Define who needs to be notified when a visitor matches a blocked record in watchlist") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveBlockedVisitorSmsNotificationPrefs(map, module.getName());
				if(ruleId != null) {
					List<Long> ruleIdList = new ArrayList<>();
					ruleIdList.add(ruleId);
					PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
				}
			}

			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}

		};
		
	}
	
	public static Preference getVipSmsNotificationPref() {
		FacilioForm form = new FacilioForm();
		List<FormSection> sections = new ArrayList<FormSection>();
		FormSection formSection = new FormSection();
		formSection.setName("VIP Visitor SMS Notification Preference");
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("to", FieldDisplayType.MULTI_USER_LIST, "Select User(s)", Required.REQUIRED,"users", 1, 1));
		
		formSection.setFields(fields);
		sections.add(formSection);
		form.setSections(sections);
		form.setFields(fields);
		form.setLabelPosition(LabelPosition.TOP);
		return new Preference("notifyVip_SmsNotification", "Notify on VIP visitor checkin_SMS", form, "Define who needs to be notified when a visitor matches a VIP record in watchlist") {
			@Override
			public void subsituteAndEnable(Map<String, Object> map, Long recordId, Long moduleId) throws Exception {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				Long ruleId = saveVipVisitorSmsNotificationPrefs(map, module.getName());
				if(ruleId != null) {
					List<Long> ruleIdList = new ArrayList<>();
					ruleIdList.add(ruleId);
					PreferenceRuleUtil.addPreferenceRule(moduleId, recordId, ruleIdList, getName());
				}
			}

			@Override
			public void disable(Long recordId, Long moduleId) throws Exception {
				// TODO Auto-generated method stub
				PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, getName());
			}

		};
		
	}
	

	public static Long saveVipVisitorSmsNotificationPrefs (Map<String, Object> map, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		
		
		WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
		workflowRuleContext.setName("VIP Visitor Notification");
		workflowRuleContext.setRuleType(RuleType.MODULE_RULE_NOTIFICATION);
		
		workflowRuleContext.setModuleName(module.getName());
		workflowRuleContext.setActivityType(EventType.CREATE);
		
		Condition isVip = new Condition();
		isVip.setFieldName("isVip");
		isVip.setOperator(BooleanOperators.IS);
		isVip.setValue("true");
		isVip.setColumnName("VisitorLogging.IS_VIP");
		
		
		Criteria criteria = new Criteria();
		criteria.addConditionMap(isVip);
		
		criteria.setPattern("(1)");
		
		workflowRuleContext.setCriteria(criteria);
		
		ActionContext emailAction = new ActionContext();
		emailAction.setActionType(ActionType.SMS_NOTIFICATION);
		
		List<String> ouIdList = (List<String>)map.get("to");
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		
		StringJoiner userEmailStr = new StringJoiner(",");
		for(String ouId : ouIdList) {
			User user = userBean.getUser(Long.parseLong(ouId), false);
			if(user != null && StringUtils.isNotEmpty(user.getMobile())) {
				userEmailStr.add(user.getMobile());
			}
		}
		if(!userEmailStr.isEmpty()) {
			SMSTemplate temp = addSmsTemplate("VIP SMS Template", userEmailStr.toString(), 117);
			emailAction.setTemplateId(temp.getId());
			//add rule,action and job
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRuleContext);
			context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(emailAction));
	        
			FacilioChain chain = TransactionChainFactory.addWorkflowRuleChain();
			chain.execute(context);
		
			return (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		}
		return null;
	}
	
	public static boolean checkForDuplicateVisitor(VisitorContext visitor) throws Exception {
		VisitorContext visitorExisiting = getVisitor(-1, visitor.getPhone());
		if(visitorExisiting != null && visitor.getId() != visitorExisiting.getId()) {
			return true;
		}
		return false;
	}
	
}
