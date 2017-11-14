package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.AlarmTemplate;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRule;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateEventRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRule eventRule = (EventRule) context.get(EventConstants.EVENT_RULE);
		if(eventRule != null && eventRule.getEventRuleId() != -1) {
			long orgId =  OrgInfo.getCurrentOrgInfo().getOrgid();
			if(eventRule.getBaseCriteria() != null) {
				long criteriaId = CriteriaAPI.addCriteria(eventRule.getBaseCriteria(),orgId);
				eventRule.setBaseCriteriaId(criteriaId);
			}
			
			if(eventRule.getTransformCriteria() != null) {
				long criteriaId = CriteriaAPI.addCriteria(eventRule.getTransformCriteria(),orgId);
				eventRule.setTransformCriteriaId(criteriaId);
			}
			
			if(eventRule.getTransformTemplate() != null) {
				AlarmTemplate alarmTemplate = new AlarmTemplate();
				alarmTemplate.setContent(eventRule.getTransformTemplate().toJSONString());
				long alarmTemplateId = TemplateAPI.addAlarmTemplate(orgId, alarmTemplate);
				eventRule.setTransformAlertTemplateId(alarmTemplateId);
			}
			
			if(eventRule.getThresholdCriteria() != null) {
				long criteriaId = CriteriaAPI.addCriteria(eventRule.getThresholdCriteria(), orgId);
				eventRule.setThresholdCriteriaId(criteriaId);
			}
			
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(EventConstants.getEventRuleModule().getTableName())
															.fields(EventConstants.getEventRuleFields())
															.andCustomWhere("EVENT_RULE_ID = ?", eventRule.getEventRuleId());
			
			updateBuilder.update(FieldUtil.getAsProperties(eventRule));
		}
		return false;
	}

}
