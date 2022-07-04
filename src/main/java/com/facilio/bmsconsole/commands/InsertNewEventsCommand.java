package com.facilio.bmsconsole.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.util.DBConf;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BMSEventContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
import com.facilio.time.DateTimeUtil;

@Log4j
public class InsertNewEventsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<BaseEventContext> baseEvents = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		if (CollectionUtils.isNotEmpty(baseEvents)) {
			for (BaseEventContext baseEvent : baseEvents) {
				updateEventObject(baseEvent);
			}
		}
		return false;
	}

	private void updateEventObject(BaseEventContext baseEvent) throws Exception {
		if (baseEvent.getSeverity() == null) {
			baseEvent.setSeverity(AlarmAPI.getAlarmSeverity(baseEvent.getSeverityString()));
		}
		
		if (baseEvent.getCreatedTime() == -1) {
			baseEvent.setCreatedTime(DateTimeUtil.getCurrenTime());
		}

		if (baseEvent instanceof BMSEventContext) {
			if (baseEvent.getResource() == null && ((BMSEventContext) baseEvent).getSource() != null) {
				long controllerId = ((BMSEventContext) baseEvent).getController();
				long orgId = AccountUtil.getCurrentOrg().getId();
				long resourceId = EventAPI.getResourceFromSource(((BMSEventContext) baseEvent).getSource(), orgId, controllerId);
				if(resourceId != -1) {
					if (resourceId != 0) {
						ResourceContext resource = ResourceAPI.getResource(resourceId);
						baseEvent.setResource(resource);
						if (resource != null && resource.getSiteId() > 0) {
							baseEvent.setSiteId(resource.getSiteId());
						}
					}
				}
				else {
					EventAPI.addSourceToResourceMapping(((BMSEventContext) baseEvent).getSource(), orgId, controllerId,((BMSEventContext) baseEvent).getAgentId());
				}
			}
			if(((BMSEventContext)baseEvent).getSources() != null && !(((BMSEventContext)baseEvent).getSources().isEmpty())){
				EventAPI.addBulkSources(((BMSEventContext)baseEvent).getSources(),((BMSEventContext)baseEvent).getAgentId());
			}

			Map<String, Object> customFields = getCustomFieldsFromData(baseEvent);
			if(baseEvent.getData() != null) {
				baseEvent.getData().keySet().removeAll(customFields.keySet());
			}
			baseEvent.setCustomFields(customFields);
		}
		
		if (baseEvent.shouldIgnore()) {
			baseEvent.setSeverity(AlarmAPI.getAlarmSeverity("Info"));
			baseEvent.setEventState(EventContext.EventState.IGNORED);
		}
		else if (!baseEvent.isSuperCalled()) {
			throw new IllegalArgumentException("method shouldIgnore of BaseEvent is never called");
		}
	}

	public List<Map<String, Object>> getCustomFieldsOfModule(String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
				.table("Fields")
				.select(FieldFactory.getSelectFieldFields())
				.andCondition(CriteriaAPI.getOrgIdCondition(DBConf.getInstance().getCurrentOrgId(), ModuleFactory.getFieldsModule()))
				.andCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", String.valueOf(1), NumberOperators.NOT_EQUALS))
				.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

			return genericSelectRecordBuilder.get();
	}

	public ArrayList<String> getCustomFieldNames(List<Map<String, Object>> customFields){
		ArrayList customFieldNames = new ArrayList();
		for(Map<String, Object> customField : customFields){
			customFieldNames.add(customField.get("name"));
		}
		return customFieldNames;
	}

	public Map<String, Object> getCustomFieldsFromData(BaseEventContext baseEventContext) throws Exception {
		Map<String, Object> customFields = new HashMap<>();
		Map<String, Object> data = baseEventContext.getData(); //additional info
		List<String> incomingFieldNames = new ArrayList<>();
		ArrayList<String> customFieldsNames = getCustomFieldNames(getCustomFieldsOfModule(FacilioConstants.ContextNames.BMS_EVENT));

		if(data != null) {
			for (String key : data.keySet()) {
				incomingFieldNames.add(key);
			}
		}
		incomingFieldNames.retainAll(customFieldsNames);
		for(String incomingFieldName: incomingFieldNames){
			customFields.put(incomingFieldName, data.get(incomingFieldName));
		}
		return customFields;
	}

}
