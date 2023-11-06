package com.facilio.fields.commands;

import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SummaryWidgetFieldsResponseCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fieldsList = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);

        if(CollectionUtils.isNotEmpty(fieldsList)) {
            List<SummaryWidgetGroupFields> fields = new ArrayList<>();

            for(FacilioField field : fieldsList) {
                SummaryWidgetGroupFields summaryWidgetGroupField = new SummaryWidgetGroupFields();
                summaryWidgetGroupField.setName(field.getName());
                summaryWidgetGroupField.setDisplayName(field.getDisplayName());
                summaryWidgetGroupField.setDisplayType(field.getDisplayType());
                summaryWidgetGroupField.setFieldId(field.getFieldId());
                summaryWidgetGroupField.setField(field);

                fields.add(summaryWidgetGroupField);
            }

            context.put(FacilioConstants.ContextNames.FIELDS, fields);
        }
        return false;
    }
}
