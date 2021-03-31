package com.facilio.bmsconsole.commands;

import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
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
		if(StringUtils.isNotEmpty(moduleName) && moduleName.equals(FacilioConstants.ContextNames.QUOTE)) {

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE);

				Criteria nonRevisedCriteria = new Criteria();
				Condition nonRevisedCondition = new Condition();
				nonRevisedCondition.setColumnName("IS_QUOTATION_REVISED");
				nonRevisedCondition.setFieldName("isQuotationRevised");
				nonRevisedCondition.setOperator(BooleanOperators.IS);
				nonRevisedCondition.setField(FieldFactory.getField("isQuotationRevised", "IS_QUOTATION_REVISED", module, FieldType.BOOLEAN));
				nonRevisedCondition.setValue(String.valueOf(false));

				nonRevisedCriteria.addAndCondition(nonRevisedCondition);
				if(clientFilterCriteria == null) {
					clientFilterCriteria = new Criteria();
				}
				clientFilterCriteria.andCriteria(nonRevisedCriteria);
		}
		
		return false;
	}

}
