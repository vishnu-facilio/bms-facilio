package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.util.InductionAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

public class FacilioInductionTemplateModuleFunctions extends FacilioModuleFunctionImpl {

	
	public void addInspectionsforUser(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String inductionTemplateName = (String) objects.get(1);
		Long peopleId = (Long) objects.get(2);
		
		PeopleContext people = PeopleAPI.getPeopleForId(peopleId);
		
		if(people == null) {
			throw new RuntimeException("People does not exist :: "+peopleId);
		}
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TEMPLATE));
		
		SelectRecordsBuilder<InductionTemplateContext> select = new SelectRecordsBuilder<InductionTemplateContext>()
				.moduleName(FacilioConstants.Induction.INDUCTION_TEMPLATE)
				.beanClass(InductionTemplateContext.class)
				.select(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TEMPLATE))
				.andCondition(CriteriaAPI.getNameCondition(inductionTemplateName, modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE)))
				.fetchSupplement((SupplementRecord)fieldMap.get("sites"))
				;
		
		InductionTemplateContext template = select.fetchFirst();
		
		if(template == null) {
			throw new RuntimeException("Template does not exist :: "+inductionTemplateName);
		}
		
		List<InductionResponseContext> responses = InductionAPI.getInductionResponse(template, people);
		
		InductionAPI.addInductionResponses(responses);
	}
}
