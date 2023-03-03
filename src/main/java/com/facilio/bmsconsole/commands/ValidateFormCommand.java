package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext.WOUrgency;
import com.facilio.bmsconsole.context.WorkOrderRequestContext.WORUrgency;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;

public class ValidateFormCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		if (form == null) {
			return false;
		}
		
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
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		for (FormField f: form.getFields()) {
			if (f.getDisplayTypeEnum() == FieldDisplayType.ATTACHMENT) {
				continue;
			}
			
			Object value = null;

			boolean customFieldPresent = false;
			if (formObject instanceof ModuleBaseWithCustomFields) {
				ModuleBaseWithCustomFields obj = (ModuleBaseWithCustomFields) formObject;
				if (f.getField()!=null && (f.getField().getDefault() == null)) {
					if(obj.getData() != null) {
					value = obj.getData().get(f.getName());
					}
					else {
						value=null;
					}
					customFieldPresent = true;
				}
			} 
			
			if (!customFieldPresent) {
				value= FieldUtil.getProperty(formObject,f.getName());
			}
			
			if (f.isRequired()){
				if (value == null || (value instanceof String && ((String) value).isEmpty()) || ((value instanceof Integer) && ((Integer) value) == -1)) {
					throw new IllegalArgumentException(f.getDisplayName() + " is mandartory.");
				}
				
				if (f.getDisplayTypeEnum() == FieldDisplayType.REQUESTER) {
					User u = (User) value;
					if (u.getName() == null || u.getName().isEmpty()) {
						throw new IllegalArgumentException("User name should not be empty");
					}
					if (u.getEmail() == null || u.getEmail().isEmpty()) {
						throw new IllegalArgumentException("Email should not be empty");
					}
				}
			}
			
			switch (f.getDisplayTypeEnum()) {
			case LOOKUP_SIMPLE:
			case LOOKUP_POPUP:
				Map<Long, String> map = null;
				if (LookupSpecialTypeUtil.isSpecialType(f.getName())) {
					map = LookupSpecialTypeUtil.getPickList(f.getName());
				} else if (f.getName() == "siteId") {
					
				} else {
					Context c1 = new FacilioContext();
					LookupField field = (LookupField) modBean.getField(f.getFieldId());
					
					c1.put(FacilioConstants.ContextNames.MODULE_NAME, field.getLookupModule().getName());
					
					FacilioChain c = FacilioChainFactory.getPickListChain();
					c.execute(c1);
					
					map = (Map<Long, String>) c1.get(FacilioConstants.ContextNames.PICKLIST);
				}
				
				if (value != null) {
					if (value instanceof ModuleBaseWithCustomFields) {
						long id = ((ModuleBaseWithCustomFields) value).getId();
						if (id != -1) {
							if (!map.containsKey(id)) {
								throw new IllegalArgumentException(f.getDisplayName() + " is invalid.");
							}
						}
					}
				}
				break;
			case URGENCY:
				if (!(value instanceof Integer && (((Integer) value) == -1 || WORUrgency.getWORUrgency((Integer) value) != null || WOUrgency.valueOf((Integer) value) != null))) {
					throw new IllegalArgumentException(f.getDisplayName() + " is invalid.");
				}
			default:
			}
		}
		return false;
	}

}
