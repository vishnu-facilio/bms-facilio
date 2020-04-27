package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetLookupFieldsFromViewCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioView facilioView = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        if (facilioView != null) {
            List<ViewField> fields = facilioView.getFields();
            if (CollectionUtils.isNotEmpty(fields)) {
                List<LookupField> lookupFields = new ArrayList<>();
                for (ViewField field : fields) {
                    if (field.getField() instanceof LookupField) {
                        FacilioField viewFieldField = field.getField();
                        lookupFields.add((LookupField) viewFieldField);
                    }
                }
                context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, lookupFields);
            }
        }
        return false;
    }
}
