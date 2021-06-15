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
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

public class FacilioInductionTemplateModuleFunctions extends FacilioModuleFunctionImpl {

	
	public void addInductions(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String inductionTemplateName = (String) objects.get(1);
		
		InductionResponseContext responseProps = null;
		if(objects.size() >=3) {
			Map<String,Object> props = (Map<String, Object>) objects.get(2);
			
			if(props != null) {
				responseProps = FieldUtil.getAsBeanFromMap(props, InductionResponseContext.class);
			}
		}
		
		Condition condition = CriteriaAPI.getNameCondition(inductionTemplateName, modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE));
		
		InductionTemplateContext template = InductionAPI.getInductionTemplate(condition);
		
		if(template == null) {
			throw new RuntimeException("Template does not exist :: "+inductionTemplateName);
		}
		
		List<InductionResponseContext> responses = InductionAPI.getInductionResponse(template);
		
		for(InductionResponseContext response :responses) {
			
			if(responseProps != null) {
				
				if(responseProps.getPeople() != null) {
					response.setPeople(responseProps.getPeople());
				}
				
				if(responseProps.getVendor() != null) {
					response.setVendor(responseProps.getVendor());
				}
				if(responseProps.getSiteId() > 0) {
					response.setSiteId(responseProps.getSiteId());
				}
			}
			
			response.setSourceType(InductionResponseContext.SourceType.WORKFLOW.getIndex());
		}
		
		InductionAPI.addInductionResponses(responses);
	}
}
