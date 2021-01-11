package com.facilio.bmsconsole.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;

public class ExecuteAllTriggersCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(ExecuteAllTriggersCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
		
		Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
		
		for (Map.Entry<String, List> entry : recordMap.entrySet()) {
			String moduleName = entry.getKey();
			if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
				LOGGER.log(Level.WARN, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+entry.getValue());
				continue;
			}
			List<EventType> activities = CommonCommandUtil.getEventTypes(context);
			if(activities != null) {
				Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap == null ? null : changeSetMap.get(moduleName);
				if (currentChangeSet != null && !currentChangeSet.isEmpty()) {
					activities.add(EventType.FIELD_CHANGE);
				}
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				
				List<BaseTriggerContext> triggers = TriggerUtil.getTriggers(module, activities, null, null);

				List records = new LinkedList<>(entry.getValue());
				Iterator it = records.iterator();
				
				while (it.hasNext()) {
					ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) it.next();	
					
					for(BaseTriggerContext trigger :triggers) {
						
						List<Long> resourceList = trigger.getActualResourceList();
						if(record.getData().containsKey("parentId") && resourceList != null && !resourceList.isEmpty()) {
							Long parentId = (Long) record.getData().get("parentId");
							if(!resourceList.contains(parentId)) {
								continue;
							}
						}
						
						if(trigger.getFieldId() > 0) {
							FacilioField field = modBean.getField(trigger.getFieldId());
							if(record.getData().containsKey(field.getName())) {
								TriggerUtil.executeTriggerActions(triggers, (FacilioContext) context);
							}
						}
						else {
							TriggerUtil.executeTriggerActions(triggers, (FacilioContext) context);
						}
					}
				}
			}
		}
		
		return false;
	}

}
