package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
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
		cards.add(getOpenWorkorderCard(assetCondition));
		cards.add(getOpenWorkorderCard(assetCondition));
		cards.add(getOpenWorkorderCard(assetCondition));
		cards.add(getFrequentlyAssignedUser(assetCondition));
		
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
	
	private Map<String, Object> getFrequentlyAssignedUser(Condition assetCondition) throws Exception {
		Map<String, Object>  card = new HashMap<String, Object>();
		card.put("name", "frequentlyAssigned");
		card.put("type", "user");
		card.put("userId", AccountUtil.getCurrentUser().getId());
		
		return card;
	}

}
