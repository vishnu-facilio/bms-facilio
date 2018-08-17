package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.WorkOrderRequestContext.WORUrgency;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.modules.FacilioField.FieldDisplayType;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;

public class ValidateFormCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ArrayList<FacilioForm> forms = (ArrayList<FacilioForm>) context.get(FacilioConstants.ContextNames.FORMS);
		if (forms == null || forms.isEmpty()) {
			return false;
		}
		
		FacilioForm form = forms.get(0);
		Object formObject = context.get(FacilioConstants.ContextNames.FORM_OBJECT);
		
		try {
			PropertyUtils.setProperty(formObject, "formId", form.getId());
		} catch (IllegalAccessException e) {
			// Ignore field
		}
		
		if (formObject == null) {
			//Fix the message
			throw new IllegalArgumentException("Form Object is empty");
		}
		
		for (FormField f: form.getFields()) {
			if (f.getDisplayType() == FieldDisplayType.ATTACHMENT) {
				continue;
			}
			
			Object value = PropertyUtils.getProperty(formObject, f.getName());
			if (f.isRequired()){
				if (value == null || (value instanceof String && ((String) value).isEmpty())) {
					throw new IllegalArgumentException(f.getDisplayName() + " is mandartory.");
				}
				
				if (f.getDisplayType() == FieldDisplayType.REQUESTER) {
					User u = (User) value;
					if (u.getName() == null || u.getName().isEmpty()) {
						throw new IllegalArgumentException("User name should not be empty");
					}
					if (u.getEmail() == null || u.getEmail().isEmpty()) {
						throw new IllegalArgumentException("Email should not be empty");
					}
				}
			}
			
			switch (f.getDisplayType()) {
			case LOOKUP_SIMPLE:
			case LOOKUP_POPUP:
				Map<Long, String> map = null;
				if (LookupSpecialTypeUtil.isSpecialType(f.getName())) {
					map = LookupSpecialTypeUtil.getPickList(f.getName());
				} else {
					Context c1 = new FacilioContext();
					context.put(FacilioConstants.ContextNames.MODULE_NAME, f.getName());
					
					Chain c = FacilioChainFactory.getPickListChain();
					c.execute(c1);
					
					map = (Map<Long, String>) context.get(FacilioConstants.ContextNames.PICKLIST);
				}
				
				if (!(value instanceof Long && map.containsKey(value))) {
					throw new IllegalArgumentException(f.getDisplayName() + " is invalid.");
				}
			case URGENCY:
				if (!(value instanceof Integer && WORUrgency.getWORUrgency((Integer) value) != null)) {
					throw new IllegalArgumentException(f.getDisplayName() + " is invalid.");
				}
			default:
			}
		}
		return false;
	}

}
