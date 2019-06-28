package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.FieldChangeFieldContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class AssociateFieldIdToStoreRuleTypeCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkflowRuleContext rule = (WorkflowRuleContext)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FieldChangeFieldContext> fieldsChanged = new ArrayList<FieldChangeFieldContext>();
		String modName = rule.getEvent().getModuleName();
		List<FacilioField> fields = modBean.getAllFields(modName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        
		if(rule.getRuleType() == RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE.getIntVal()) {
			if(fieldMap.containsKey("isUnderstocked")) {
            	FacilioField fieldChangeId = fieldMap.get("isUnderstocked");
            	FieldChangeFieldContext field = new FieldChangeFieldContext();
    			field.setFieldId(fieldChangeId.getFieldId());
    			fieldsChanged.add(field);
    			rule.setFields(fieldsChanged);
    		}
		}
		else if(rule.getRuleType() == RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE.getIntVal()) {
			if(modName.equals("item") && fieldMap.containsKey("quantity")) {
            	FacilioField fieldChangeId = fieldMap.get("quantity");
            	FieldChangeFieldContext field = new FieldChangeFieldContext();
    			field.setFieldId(fieldChangeId.getFieldId());
    			fieldsChanged.add(field);
    			rule.setFields(fieldsChanged);
    		}
			else if(modName.equals("tool") && fieldMap.containsKey("currentQuantity")) {
            	FacilioField fieldChangeId = fieldMap.get("currentQuantity");
            	FieldChangeFieldContext field = new FieldChangeFieldContext();
    			field.setFieldId(fieldChangeId.getFieldId());
    			fieldsChanged.add(field);
    			rule.setFields(fieldsChanged);
    		}
		}
		return false;
	}

}
