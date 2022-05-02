package com.facilio.bmsconsoleV3.signup.workordersurvey;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.ArrayList;
import java.util.List;

public class AddWorkOrderSurveyModules extends SignUpData {
	@Override
	public void addData () throws Exception {

		ModuleBean modBean = ( ModuleBean ) BeanFactory.lookup ("ModuleBean");

		List< FacilioModule > modules = new ArrayList<> ();

		FacilioModule workOrderSurvey = constructSurvey (modBean);
		modules.add (workOrderSurvey);
		FacilioModule surveyResponseModule = constructSurveyResponse (modBean, workOrderSurvey);
		modules.add (surveyResponseModule);

		FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain ();
		addModuleChain.getContext ().put (FacilioConstants.ContextNames.MODULE_LIST, modules);
		addModuleChain.execute ();
	}

	private FacilioModule constructSurveyResponse (ModuleBean modBean, FacilioModule workOrderSurvey) throws Exception {

		FacilioModule module = new FacilioModule (FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE,
				"WorkOrder Survey Responses",
				"WorkOrder_Survey_Responses",
				FacilioModule.ModuleType.Q_AND_A_RESPONSE,
				modBean.getModule (FacilioConstants.Survey.SURVEY_RESPONSE),
				true
		);

		List< FacilioField > fields = new ArrayList<> ();

		LookupField parentField = ( LookupField ) FieldFactory.getDefaultField ("parent", "Parent", "PARENT_ID", FieldType.LOOKUP, true);
		parentField.setLookupModule (modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		fields.add (parentField);

		module.setFields(fields);

		return module;
	}

	private FacilioModule constructSurvey (ModuleBean modBean) throws Exception {

		FacilioModule module = new FacilioModule (FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_TEMPLATE,
				"WorkOrder Survey Templates",
				"WorkOrder_Survey_Templates",
				FacilioModule.ModuleType.Q_AND_A,
				modBean.getModule (FacilioConstants.Survey.SURVEY_TEMPLATE),
				true
		);

		return module;
	}
}
