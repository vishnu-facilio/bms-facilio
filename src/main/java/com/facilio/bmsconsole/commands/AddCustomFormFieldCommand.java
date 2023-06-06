package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;


import java.util.List;
import java.util.Map;


public class AddCustomFormFieldCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {


        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);

        FacilioUtil.throwIllegalArgumentException(fields.stream().anyMatch(field -> field.getFieldId() < 0), "Field is not defined");

        long nextFormFieldId = (long) context.get(FacilioConstants.FormContextNames.NEXT_FORM_FIELD_ID);
        long previousFormFieldId = (long) context.get(FacilioConstants.FormContextNames.PREVIOUS_FORM_FIELD_ID);
        long formSectionId = (long) context.get(FacilioConstants.FormContextNames.FORM_SECTION_ID);
        long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);

        double formFieldSequenceNumber = FormsAPI.getFormFieldSequenceNumber(previousFormFieldId, nextFormFieldId, formSectionId);

        for (FacilioField facilioField : fields) {
            if (facilioField.getFieldId() > 0) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                facilioField = modBean.getField(facilioField.getFieldId());
                FormField formField = FormsAPI.getFormFieldFromFacilioField(facilioField, 0);
                formField.setFormFieldSequenceNumber(formFieldSequenceNumber);
                formField.setSequenceNumber((int) formFieldSequenceNumber);
                formField.setId(-1);
                formField.setFormId(formId);
                formField.setSectionId(formSectionId);

                long systemTime = System.currentTimeMillis();
                User currentUser = AccountUtil.getCurrentUser();
                formField.setSysCreatedTime(systemTime);
                formField.setSysModifiedTime(systemTime);
                if (currentUser != null) {
                    formField.setSysCreatedBy(AccountUtil.getCurrentUser().getPeopleId());
                    formField.setSysModifiedBy(AccountUtil.getCurrentUser().getPeopleId());
                }

                Map<String, Object> props = FieldUtil.getAsProperties(formField);

                context.put(FacilioConstants.ContextNames.FORM_FIELD, props);

                GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getFormFieldsModule().getTableName())
                        .fields(FieldFactory.getFormFieldsFields());
                long formFieldId = insertRecordBuilder.insert(props);

            }
        }

        return false;
    }
}
