package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FieldFactory;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Alarm;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;

public class ConstructAssetReportCardsCommand implements Command{

	long assetId;
	
	@Override
	public boolean execute(Context context) throws Exception {
		assetId = (long) context.get(FacilioConstants.ContextNames.ASSET_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
		Condition assetCondition = CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(assetId), NumberOperators.EQUALS);
		
		List<Map<String, Object>> cards = new ArrayList<>();
		
		switch(moduleName) {
			case ContextNames.WORK_ORDER:
				cards.add(getOpenWorkorderCard(assetCondition));
				cards.add(getClosedWorkorderCard(assetCondition, fieldMap.get("actualWorkEnd")));
//				cards.add(getAvgWorkorderCard(assetCondition));
				cards.add(getTotalWorkorderCard(assetCondition));
				cards.add(getFrequentlyAssignedUser(assetCondition, fieldMap.get("assignedTo")));
				break;
				
			case ContextNames.ALARM:
				cards.add(getAlarmSeverityCard(assetCondition, Alarm.CLEAR_SEVERITY, false));
				cards.add(getAlarmSeverityCard(assetCondition, Alarm.CRITICAL_SEVERITY));
				cards.add(getUnacknowledgedAlarmCard(assetCondition));
				cards.add(getAlarmsThisMonth(assetCondition, fieldMap.get("createdTime")));
				break;
		}
		
		context.put(FacilioConstants.ContextNames.REPORT_CARDS, cards);
		
		return false;
	}
	
	private Map<String, Object> getOpenWorkorderCard(Condition assetCondition) throws Exception {
		
		Map<String, Object>  card = new HashMap<String, Object>();
		card.put("name", "open");
		card.put("moduleName", FacilioConstants.ContextNames.WORK_ORDER);
		Criteria criteria = new Criteria();
		criteria.addAndCondition(ViewFactory.getOpenStatusCondition());
		criteria.addAndCondition(assetCondition);
		card.put("criteria", criteria);
		
		card.put("secondaryName", "overdue");
		Criteria secondaryCriteria = ViewFactory.getAllOverdueWorkOrdersCriteria();
		secondaryCriteria.addAndCondition(assetCondition);
		card.put("secondaryCriteria", secondaryCriteria);
		
		return card;
	}
	
	private Map<String, Object> getClosedWorkorderCard(Condition assetCondition, FacilioField actualWorkEndField) throws Exception {
		
		Map<String, Object>  card = new HashMap<String, Object>();
		card.put("name", "closed");
		Criteria criteria = ViewFactory.getClosedTicketsCriteria();
		criteria.addAndCondition(assetCondition);
		criteria.addAndCondition(CriteriaAPI.getCondition(actualWorkEndField, DateOperators.CURRENT_MONTH));
		card.put("criteria", criteria);
		
		card.put("secondaryName", "thismonth");
		
		return card;
	}
	
	private Map<String, Object> getAvgWorkorderCard(Condition assetCondition) {
		Map<String, Object>  card = new HashMap<String, Object>();
		card.put("name", "avgworkorders");
		Criteria criteria = new Criteria();
		criteria.addAndCondition(assetCondition);
		card.put("criteria", criteria);
		
		card.put("secondaryName", "everymonth");
		return card;
	}
	
	private Map<String, Object> getTotalWorkorderCard(Condition assetCondition) {
		Map<String, Object>  card = new HashMap<String, Object>();
		card.put("name", "total");
		Criteria criteria = new Criteria();
		criteria.addAndCondition(assetCondition);
		card.put("criteria", criteria);
		
		return card;
	}
	
	private Map<String, Object> getFrequentlyAssignedUser(Condition assetCondition, FacilioField assignedToField) throws Exception {
		Map<String, Object>  card = new HashMap<String, Object>();
		card.put("name", "frequentlyAssigned");
		card.put("type", "user");
		Criteria criteria = new Criteria();
		criteria.addAndCondition(assetCondition);
		card.put("criteria", criteria);
		card.put("cardKey", "userId");
		Map<String, Object> builderInfo = new HashMap<>();
		builderInfo.put("field", Collections.singletonList(assignedToField));
		builderInfo.put("groupBy", assignedToField.getCompleteColumnName());
		builderInfo.put("orderBy", "count(" + assignedToField.getCompleteColumnName() + ") desc");
		builderInfo.put("limit", 1);
		card.put("builderInfo", builderInfo);
		
		return card;
	}
	
	private Map<String, Object> getAlarmSeverityCard(Condition assetCondition, String severity, Boolean...equals) {
		Map<String, Object>  card = new HashMap<String, Object>();
		card.put("name", severity.toLowerCase());
		
		boolean equalsCondition = equals != null && equals.length > 0 ? equals[0] : true;
		Condition alarmCondition = ViewFactory.getAlarmSeverityCondition(severity, equalsCondition);
		Criteria criteria = ViewFactory.getCommonAlarmCriteria();
		criteria.addAndCondition(assetCondition);
		criteria.addAndCondition(alarmCondition);
		card.put("criteria", criteria);
		
		return card;
	}
	
	private Map<String, Object> getUnacknowledgedAlarmCard(Condition assetCondition) {
		Map<String, Object>  card = new HashMap<String, Object>();
		card.put("name", "unacknowledged");
		
		Criteria criteria = ViewFactory.getUnacknowledgedAlarmCriteria();
		criteria.addAndCondition(assetCondition);
		card.put("criteria", criteria);
		
		return card;
	}
	
	private Map<String, Object> getAlarmsThisMonth(Condition assetCondition, FacilioField createdTimeField) {
		Map<String, Object>  card = new HashMap<String, Object>();
		card.put("name", "thismonth");
		
//		Condition alarmCondition = ViewFactory.getAlarmSeverityCondition(Alarm.CLEAR_SEVERITY, false);
		Criteria criteria = new Criteria();
		criteria.addAndCondition(assetCondition);
//		criteria.addAndCondition(alarmCondition);
		criteria.addAndCondition(CriteriaAPI.getCondition(createdTimeField, DateOperators.CURRENT_MONTH));
		card.put("criteria", criteria);
		
		return card;
	}

}
