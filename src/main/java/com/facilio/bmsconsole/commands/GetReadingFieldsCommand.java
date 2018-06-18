package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReadingFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioModule> readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if(readings != null && !readings.isEmpty()) {
			List<Long> fieldIds = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(FacilioModule reading : readings) {
				Boolean excludeEmptyFields = (Boolean) context.get(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS);
				Long parentId = excludeEmptyFields != null && excludeEmptyFields ? (Long) context.get(FacilioConstants.ContextNames.PARENT_ID) : null;
				List<FacilioField> dataPoints = ReadingsAPI.excludeDefaultAndEmptyReadingFields(modBean.getAllFields(reading.getName()),parentId);
				reading.setFields(dataPoints);
				fieldIds.addAll(dataPoints.stream().map(FacilioField::getId).collect(Collectors.toList()));
			}
			context.put(FacilioConstants.ContextNames.READING_FIELDS, fieldIds);
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE, RuleType.VALIDATION_RULE);
		}
		return false;
	}
	
	

}
