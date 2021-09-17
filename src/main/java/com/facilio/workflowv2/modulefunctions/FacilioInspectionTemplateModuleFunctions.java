package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.v3.context.Constants;
import com.facilio.workflowv2.annotation.ScriptModule;

@ScriptModule(moduleName = FacilioConstants.Inspection.INSPECTION_TEMPLATE)
public class FacilioInspectionTemplateModuleFunctions extends FacilioModuleFunctionImpl {
	
	public Object addInspections(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		String inspectionTemplateName = (String) objects.get(1);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder<InspectionTemplateContext> select = new SelectRecordsBuilder<InspectionTemplateContext>()
				.moduleName(FacilioConstants.Inspection.INSPECTION_TEMPLATE)
				.beanClass(InspectionTemplateContext.class)
				.select(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TEMPLATE))
				.andCondition(CriteriaAPI.getNameCondition(inspectionTemplateName, modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE)))
				;
		
		InspectionTemplateContext template = select.fetchFirst();
		
		if(template == null) {
			throw new RuntimeException("Template does not exist :: "+inspectionTemplateName);
		}
		
		FacilioChain executeTemplateChain = QAndATransactionChainFactory.executeTemplateChain();
        FacilioContext context = executeTemplateChain.getContext();
        Constants.setModuleName(context, FacilioConstants.Inspection.INSPECTION_TEMPLATE);
        Constants.setRecordId(context, template.getId());
        
        if(objects.size() >= 3) {
        	
        	List<Long> resourceIds = (List<Long>)objects.get(2);
        	
        	List<ResourceContext> resources = ResourceAPI.getResources(resourceIds, false);
        	
        	context.put(FacilioConstants.ContextNames.RESOURCE_LIST,resources);
        }
        
        executeTemplateChain.execute();
        
		return FieldUtil.getAsMapList((List<InspectionResponseContext>)context.get(FacilioConstants.QAndA.RESPONSE), InspectionResponseContext.class);
		
	}
}
