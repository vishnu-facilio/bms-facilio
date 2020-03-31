package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class GenerateCriteriaFromClientCriteriaCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(GenerateCriteriaFromFilterCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		
		Criteria clientFilterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(clientFilterCriteria != null && moduleName != null) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			for (String key : clientFilterCriteria.getConditions().keySet()) {
				Condition condition = clientFilterCriteria.getConditions().get(key);
				FacilioField field = modBean.getField(condition.getFieldName(), moduleName);
				condition.setField(field);
			}
		}
		
		return false;
	}

}
