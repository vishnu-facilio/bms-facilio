package com.facilio.bmsconsole.forms;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnUsedFormFields {

    public static List<FormField> getMetaFormFieldApprovals(List<FacilioField> allFields) throws Exception {
        List<FormField> fields = new ArrayList<>();

        List<FacilioField> facilioFields = new ArrayList();
        int i = 0;
        for(FacilioField fieldObject: allFields) {
            if(FieldFactory.Fields.APPROVAL_FORM_FIELDS.contains(fieldObject.getName()) || !fieldObject.isDefault()) {
                if (fieldObject.getName().equals("resource")) {
                    fields.add(new FormField(fieldObject.getFieldId(),fieldObject.getName(), FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.OPTIONAL, i++, 1));
                } else if (fieldObject.getName().equals("assignmentGroup")) {
                    fields.add(new FormField(fieldObject.getFieldId(), fieldObject.getName(), FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", FormField.Required.REQUIRED, ++i, 1));
                } else if (fieldObject.getName().equals("urgency")) {
                    fields.add(new FormField(fieldObject.getFieldId(), fieldObject.getName(), FacilioField.FieldDisplayType.URGENCY, fieldObject.getDisplayName(), FormField.Required.OPTIONAL, ++i, 1));
                } else {
                    facilioFields.add(fieldObject);
                }
            }
        }
        fields.add(new FormField("comment", FacilioField.FieldDisplayType.TICKETNOTES, "Comment", FormField.Required.OPTIONAL, "ticketnotes",6, 1));
        if (facilioFields.size() > 0) {
            fields.addAll(FormsAPI.getFormFieldsFromFacilioFields(facilioFields, i));
        }
        return Collections.unmodifiableList(fields);
    }

    public static List<FormField> getRequesterFormFields(boolean getRequesterDetails, boolean isMandatory) throws Exception {
        List<FormField> fields = new ArrayList<>();
        FormField.Required required = isMandatory ? FormField.Required.REQUIRED : FormField.Required.OPTIONAL;
        if (AccountUtil.getCurrentAccount().isFromMobile() || getRequesterDetails) {
            fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Requester Name", required, 1, 1));
            fields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Requester Email", required, 2, 1));
        }
        else {
            fields.add(new FormField("requester", FacilioField.FieldDisplayType.REQUESTER, "Requester", required, FacilioConstants.ContextNames.REQUESTER, 1, 1));
        }
        return fields;
    }
}
