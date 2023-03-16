package com.facilio.qa.command;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.bmsconsoleV3.context.workordersurvey.WorkOrderSurveyResponseContext;
import com.facilio.bmsconsoleV3.context.workordersurvey.WorkOrderSurveyTemplateContext;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ExecuteSurveyTemplateCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		String moduleName = Constants.getModuleName(context);
		FacilioModule module = Constants.getModBean().getModule(moduleName);
		Map<String, Object> errorParams = new HashMap<>();
		errorParams.put("moduleName", moduleName);
		V3Util.throwRestException(module == null || module.getTypeEnum() != FacilioModule.ModuleType.Q_AND_A, ErrorCode.VALIDATION_ERROR, "errors.qa.executeSurveyTemplateCommand.moduleQACheck.msg",true,errorParams);
		//V3Util.throwRestException(module == null || module.getTypeEnum() != FacilioModule.ModuleType.Q_AND_A, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid Q And A Module ({0}) specified while executing template", moduleName),true,errorParams);
		Long id = Constants.getRecordId(context);
		QAndAType type = QAndAType.getQAndATypeFromTemplateModule(moduleName);

		FacilioContext summaryContext = V3Util.getSummary(moduleName, Collections.singletonList(id));

		WorkOrderSurveyTemplateContext template = (WorkOrderSurveyTemplateContext) Objects.requireNonNull(Constants.getRecordListFromContext(summaryContext, moduleName)).get(0);
		errorParams.put("id", id);
		V3Util.throwRestException(template == null, ErrorCode.VALIDATION_ERROR,"errors.qa.executeSurveyTemplateCommand.idTemplateCheck",true,errorParams);
		//V3Util.throwRestException(template == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid id ({0}) specified while executing template", id),true,errorParams);

		WorkOrderSurveyResponseContext response = template.constructResponse();

		if(response != null){
			Long ruleId = (Long) context.get("ruleId");
			Boolean isRetake = (Boolean) context.get("isRetakeAllowed");
			Integer retakeExpiryDuration = (Integer) context.get("retakeExpiryDay");
			Integer expiryDay = (Integer) context.get("expiryDay");
			Long parentId = (Long) context.get("parentId");
			long assignedTo = (long) context.get("assignedTo");
			response.setRuleId(ruleId);
			response.setIsRetakeAllowed(isRetake);
			if(expiryDay!=null) {response.setExpiryDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(expiryDay));}
			response.setRetakeExpiryDuration(retakeExpiryDuration);
			response.setParentId(WorkOrderAPI.getWorkOrder(parentId));
			response.setSiteId(response.getParentId().getSiteId());
			response.setAssignedTo(PeopleAPI.getPeopleForId(assignedTo));
			List<SurveyResponseContext> res = new ArrayList<>();
			res.add(response);
			QAndAUtil.addRecordViaV3Chain(type.getResponseModule(), res);
			context.put(FacilioConstants.QAndA.RESPONSE, response);
		}

		return false;
	}
}
