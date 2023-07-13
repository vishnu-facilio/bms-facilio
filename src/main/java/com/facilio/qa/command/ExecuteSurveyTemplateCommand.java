package com.facilio.qa.command;

import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.QAndAType;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;


import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ExecuteSurveyTemplateCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		String moduleName = Constants.getModuleName(context);
		FacilioModule module = Constants.getModBean().getModule(moduleName);
		V3Util.throwRestException(module == null || module.getTypeEnum() != FacilioModule.ModuleType.Q_AND_A, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid Q And A Module ({0}) specified while executing template", moduleName));
		Long id = Constants.getRecordId(context);
		QAndAType type = QAndAType.getQAndATypeFromTemplateModule(moduleName);

		FacilioContext summaryContext = V3Util.getSummary(moduleName, Collections.singletonList(id));

		SurveyTemplateContext template = (SurveyTemplateContext) Objects.requireNonNull(Constants.getRecordListFromContext(summaryContext, moduleName)).get(0);

		V3Util.throwRestException(template == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid id ({0}) specified while executing template", id));

		SurveyResponseContext response = template.constructResponse();

		if(response != null) {
			Long ruleId = (Long) context.get("ruleId");
			Boolean isRetake = (Boolean) context.get("isRetakeAllowed");
			Integer retakeExpiryDuration = (Integer) context.get("retakeExpiryDay");
			Integer expiryDay = (Integer) context.get("expiryDay");
			Map<String,Object> recordMap = (Map<String,Object>) context.get("recordMap");
			String currentModuleName = (String) context.get("currentModuleName");

			V3Context record = new V3Context();
			if (currentModuleName != null && currentModuleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
				record = FieldUtil.getAsBeanFromMap(recordMap, V3WorkOrderContext.class);
				response.setWorkOrderId((V3WorkOrderContext) record);
			}
			if (currentModuleName != null && currentModuleName.equals(FacilioConstants.ContextNames.SERVICE_REQUEST)) {
				record = FieldUtil.getAsBeanFromMap(recordMap, V3ServiceRequestContext.class);
				response.setServiceRequestId((V3ServiceRequestContext) record);
			}

			if (record != null && record.getSiteId() != -1){
				response.setSiteId(record.getSiteId());
			}

			long assignedTo = (long) context.get("assignedTo");
			response.setRuleId(ruleId);
			response.setIsRetakeAllowed(isRetake);
			if (expiryDay != null) {
				response.setExpiryDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(expiryDay));
			}
			response.setRetakeExpiryDuration(retakeExpiryDuration);
			String ruleName = (String) context.get("ruleName");
			response.setName(ruleName);
			response.setAssignedTo(PeopleAPI.getPeopleForId(assignedTo));
			List<SurveyResponseContext> res = new ArrayList<>();
			res.add(response);
			QAndAUtil.addRecordViaV3Chain(type.getResponseModule(), res);
			context.put(FacilioConstants.QAndA.RESPONSE, response);
		}

		return false;
	}
}
