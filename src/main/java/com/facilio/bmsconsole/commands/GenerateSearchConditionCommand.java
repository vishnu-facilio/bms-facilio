package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GenerateSearchConditionCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(GenerateSearchConditionCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		JSONObject searchObj = (JSONObject) context.get(FacilioConstants.ContextNames.SEARCH);
		if(searchObj != null && !searchObj.isEmpty()) {
			List<Condition> conditionList = new ArrayList<>();
			
			String searchQuery = (String) searchObj.get("query");
			String fields = (String) searchObj.get("fields");
			String[] fieldsArray = fields.split(",");
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			Criteria criteria = new Criteria();
			for (String searchField : fieldsArray) {
				String[] module = searchField.split("\\.");
				
				FacilioField field = modBean.getField(module[1], module[0]);
				
				Condition condition = new Condition();
				condition.setField(field);
				condition.setOperator(field.getDataTypeEnum().getOperator("contains"));
				condition.setValue(searchQuery);
				
				criteria.addOrCondition(condition);
			}
			
			context.put(FacilioConstants.ContextNames.SEARCH_CRITERIA, criteria);
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		if (AccountUtil.getCurrentOrg().getId() == 114) {
			LOGGER.info("Time taken to execute GenerateSearchConditionCommand : "+timeTaken);
		}
		return false;
	}

}
