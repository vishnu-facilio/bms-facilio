package com.facilio.bmsconsole.commands;

import java.util.ArrayList;

import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class GenerateSearchConditionCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(GenerateSearchConditionCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		Object search = context.get(FacilioConstants.ContextNames.SEARCH);
		if (search instanceof JSONObject) {
			JSONObject searchObj = (JSONObject) context.get(FacilioConstants.ContextNames.SEARCH);
			if (searchObj != null && !searchObj.isEmpty()) {
				new ArrayList<>();

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
		}
		else if (search instanceof String && StringUtils.isNotEmpty((String) search)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			Criteria criteria = new Criteria();
			FacilioField primaryField = modBean.getPrimaryField(moduleName);
			criteria.addAndCondition(CriteriaAPI.getCondition(primaryField, (String) search, StringOperators.CONTAINS));
			context.put(FacilioConstants.ContextNames.SEARCH_CRITERIA, criteria);
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute GenerateSearchConditionCommand : "+timeTaken);
		return false;
	}

}
