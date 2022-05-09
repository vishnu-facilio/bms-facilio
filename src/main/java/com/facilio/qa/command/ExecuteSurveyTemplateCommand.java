package com.facilio.qa.command;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QAndAType;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class ExecuteSurveyTemplateCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{
		String moduleName = Constants.getModuleName(context);
		FacilioModule module = Constants.getModBean().getModule(moduleName);
		V3Util.throwRestException(module == null || module.getTypeEnum() != FacilioModule.ModuleType.Q_AND_A, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid Q And A Module ({0}) specified while executing template", moduleName));
		Long id = Constants.getRecordId(context);
		QAndAType type = QAndAType.getQAndATypeFromTemplateModule(moduleName);

		FacilioContext summaryContext = V3Util.getSummary(moduleName, Collections.singletonList(id));

		SurveyTemplateContext template = (SurveyTemplateContext) Constants.getRecordListFromContext(summaryContext, moduleName).get(0);

		V3Util.throwRestException(template == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid id ({0}) specified while executing template", id));

		List<ResourceContext> resources = (List<ResourceContext>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);

		List< ? extends ResponseContext> response = template.constructResponses(resources);

		if(CollectionUtils.isNotEmpty(response)){
			Long ruleId = (Long) context.get("ruleId");
			Boolean isRetake = (Boolean) context.get("isRetakeAllowed");
			Integer retakeExpiryDuration = (Integer) context.get("retakeExpiryDuration");
			for(ResponseContext res : response){
				res.setRuleId(ruleId);
				res.setIsRetakeAllowed(isRetake);
				res.setRetakeExpiryDuration(retakeExpiryDuration);
			}
			QAndAUtil.addRecordViaV3Chain(type.getResponseModule(), response);
			context.put(FacilioConstants.QAndA.RESPONSE, response);
		}

		return false;
	}
}
