package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReorderFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        long nextFormFieldId = (long) context.get(FacilioConstants.FormContextNames.NEXT_FORM_FIELD_ID);
        long previousFormFieldId = (long) context.get(FacilioConstants.FormContextNames.PREVIOUS_FORM_FIELD_ID);
        long formFieldId = (long) context.get(FacilioConstants.ContextNames.FORM_FIELD_ID);
        long sectionId = (long) context.get(FacilioConstants.FormContextNames.FORM_SECTION_ID);
        double formFieldSequenceNumber = FormsAPI.getFormFieldSequenceNumber(previousFormFieldId, nextFormFieldId, sectionId);

        FormField formField = FormsAPI.getFormFieldFromId(formFieldId);
        context.put(FacilioConstants.ContextNames.FORM_FIELD, FieldUtil.getAsProperties(formField));
        if (formFieldSequenceNumber != 0) {

            FacilioModule formFieldModule = ModuleFactory.getFormFieldsModule();
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(formFieldModule.getTableName())
                    .fields(Collections.singletonList(FieldFactory.getField("formFieldSequenceNumber", "FIELD_SEQUENCE_NUMBER", formFieldModule, FieldType.NUMBER)))
                    .andCondition(CriteriaAPI.getIdCondition(formFieldId, formFieldModule));
            Map<String, Object> map = new HashMap<>();
            map.put("formFieldSequenceNumber", formFieldSequenceNumber);
            builder.update(map);

        }

        return false;
    }
}
