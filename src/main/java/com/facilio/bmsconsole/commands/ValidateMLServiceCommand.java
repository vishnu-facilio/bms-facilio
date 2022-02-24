package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidateMLServiceCommand extends FacilioCommand implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(ValidateMLServiceCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		V3MLServiceContext mlServiceContext = (V3MLServiceContext) context.get(MLServiceUtil.MLSERVICE_CONTEXT);
		try {
			if(mlServiceContext.getServiceType().equals("default")) {
				LOGGER.info("Duplicate check on DEFAULT ML Model");
				validateDuplicateProject(mlServiceContext);
			} else {
				LOGGER.info("Duplicate check on CUSTOM ML Model");
				validateDuplicateProject(mlServiceContext);
				validateAssetFields(mlServiceContext);
				validateDuplicateOnCustomModels(mlServiceContext);
			}
			MLServiceUtil.updateMLStatus(mlServiceContext, "Precheck validation completed successfully");
		} catch (Exception e) {
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, "Precheck validation failed", e);
		}
		return false;
	}

	private boolean validateDuplicateProject(V3MLServiceContext mlServiceContext) throws Exception {
		List<Map<String, Object>> successfullProjects = MLServiceUtil.getSuccessfullProjects(mlServiceContext);
		if(successfullProjects == null || successfullProjects.isEmpty()) {
			return false;
		}
		String errorMsg = "Given project name is already exists for default ML model.";
		throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errorMsg);
	}

	private void validateAssetFields(V3MLServiceContext mlServiceContext) throws Exception {
		if(CollectionUtils.isEmpty(mlServiceContext.getModelReadings())) {
			String errorMsg = "modelReadings is missing";
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errorMsg);
		}
		MLServiceUtil.extractAndValidateAssetFields(mlServiceContext);
	}

	private void validateDuplicateOnCustomModels(V3MLServiceContext mlServiceContext) throws Exception {
		List<String> scenarioFields = MLServiceUtil.getExistingReadingList(mlServiceContext);
		if(scenarioFields != null) {
			List<String> currentFields = mlServiceContext.getReadingVariables();
			if(currentFields.size()!=scenarioFields.size()) {
				String errorMsg = "Given project name ["+mlServiceContext.getProjectName()+"] is already exist with different set of readingVariables "+scenarioFields;
				errorMsg = errorMsg+". Please give different project name";
				throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errorMsg);
			}
			List<String> filterFields = scenarioFields.stream()
					.filter(fieldName -> !currentFields.contains(fieldName))
					.collect(Collectors.toList());
			if(filterFields.size()!=0) {
				String errorMsg = "Given project name ["+mlServiceContext.getProjectName()+"] is already exist with different set of readingVariables "+scenarioFields;
				errorMsg = errorMsg+". Please give different project name";
				throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, errorMsg);
			}
		}
	}

}
