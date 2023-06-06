package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.json.simple.JSONObject;

public class UpdateFormFieldCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        FormField field = (FormField) context.get(FacilioConstants.ContextNames.FORM_FIELD);

        FacilioModule module = ModuleFactory.getFormFieldsModule();

		long systemTime = System.currentTimeMillis();
		User currentUser = AccountUtil.getCurrentUser();
		field.setSysModifiedTime(systemTime);
		if (currentUser != null) {
			field.setSysModifiedBy(AccountUtil.getCurrentUser().getPeopleId());
		}

		GenericUpdateRecordBuilder formUpdateBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getFormFieldsFields())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCondition(CriteriaAPI.getIdCondition(field.getId(), module));

        Map<String, Object> props = FieldUtil.getAsProperties(field);
        formUpdateBuilder.update(props);
        FormField formField = FormsAPI.getFormFieldFromId(field.getId());
        Map<String, Object> fieldProps = FieldUtil.getAsProperties(formField);
        context.put(FacilioConstants.ContextNames.FORM_FIELD, fieldProps);


        return false;
    }

}
