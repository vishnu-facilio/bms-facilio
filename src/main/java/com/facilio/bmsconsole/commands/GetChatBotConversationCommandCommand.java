package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetChatBotConversationCommandCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = ModuleFactory.getCBSessionModule().getName();
		
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getCBSessionFields());
		
		Boolean fetchCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		List<FacilioField> fields = null;
		if (fetchCount != null && fetchCount) {
			FacilioField countFld = new FacilioField();
			countFld.setName("count");
			countFld.setColumnName("COUNT(CB_Session.ID)");
			countFld.setDataType(FieldType.NUMBER);
			fields = Collections.singletonList(countFld);
		}
		else {
			fields = FieldFactory.getCBSessionFields();
		}
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBSessionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userId"), AccountUtil.getCurrentUser().getId()+"", NumberOperators.EQUALS))
				.orderBy("START_TIME desc")
				;
		
		if(startTime > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("startTime"), startTime+"", NumberOperators.LESS_THAN));
		}
		
		
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			builder.andCriteria(view.getCriteria());
		}

		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}
		
		String criteriaIds = (String) context.get(FacilioConstants.ContextNames.CRITERIA_IDS);
		if (criteriaIds != null) {
			String[] ids = criteriaIds.split(",");
			for(int i = 0; i < ids.length; i++) {
				Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), Long.parseLong(ids[i]));
				builder.andCriteria(criteria);
			}
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			builder.offset(offset);
			builder.limit(perPage);
		}
		
		List<Map<String, Object>> props = builder.get();
		
		if(fetchCount == null || !fetchCount) {
			
			List<ChatBotSession> sessions = new ArrayList<>();
			List<Long> sessionIDs = new ArrayList<Long>();
			for(Map<String, Object> prop :props) {
				
				ChatBotSession chatBotSession = FieldUtil.getAsBeanFromMap(prop, ChatBotSession.class);
				sessionIDs.add(chatBotSession.getId());
				sessions.add(chatBotSession);
			}
			
			if(!sessionIDs.isEmpty()) {
				
				Map<Long, List<ChatBotSessionConversation>> sessionConversationMap = ChatBotUtil.getSessionConversationMap(sessionIDs);
				
				if(sessionConversationMap != null && !sessionConversationMap.isEmpty()) {
					
					for(ChatBotSession session : sessions) {
						if(sessionConversationMap.containsKey(session.getId())) {
							session.setChatBotSessionConversations(sessionConversationMap.get(session.getId()));
						}
					}
				}
			}
			
			context.put(ChatBotConstants.CHAT_BOT_SESSIONS, sessions);
		}
		else {
			context.put(ChatBotConstants.CHAT_BOT_SESSION_COUNT, props.get(0).get("count"));
		}
		
		return false;
		
	}

}
