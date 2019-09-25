package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.forms.FacilioForm.LabelPosition;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.ScheduledRuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.ParameterContext;
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
	
	public static VisitorLoggingContext  getVisitorLogging(long logId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOGGING);
		SelectRecordsBuilder<VisitorLoggingContext> builder = new SelectRecordsBuilder<VisitorLoggingContext>()
														.module(module)
														.beanClass(VisitorLoggingContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(logId), NumberOperators.EQUALS))
														;
		
		
		VisitorLoggingContext records = builder.fetchFirst();
		return records;
	
	}
	
	public static void updateVisitorInviteStateToArrived(long visitorId, long visitorInviteId) throws Exception {
		
		if(visitorId > 0 && visitorInviteId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
			List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
			
			FacilioStatus arrivedStatus = TicketAPI.getStatus(module, "Arrived");
			
			UpdateRecordBuilder<InviteVisitorRelContext> updateBuilder = new UpdateRecordBuilder<InviteVisitorRelContext>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getCondition("INVITE_ID", "inviteId", String.valueOf(visitorInviteId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("VISITOR_ID", "visitorId", String.valueOf(visitorId), NumberOperators.EQUALS))
				;
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField statusField = modBean.getField("moduleState", module.getName());
			updateMap.put("moduleState", arrivedStatus);
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			updatedfields.add(statusField);
		
			updateBuilder.updateViaMap(updateMap);
		}
		
	}
	
	
}
