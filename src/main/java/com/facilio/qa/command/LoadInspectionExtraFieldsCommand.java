package com.facilio.qa.command;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoadInspectionExtraFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean fetchOnlyViewGroupColumn=(boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);

        if(fetchOnlyViewGroupColumn){
            List<FacilioField> allFields = Constants.getModBean().getAllFields(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
            Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(allFields);
            List<FacilioField> extraInspectionTemplateFields = new ArrayList<>();

            FacilioView view=(FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
            List<FacilioField> viewFields=null;

            if(view!=null && CollectionUtils.isNotEmpty(view.getFields())) {
                viewFields = view.getFields().stream().map(ViewField::getField).filter(viewFiled -> viewFiled != null).collect(Collectors.toList());
                Map<String, FacilioField> viewFieldsMap = FieldFactory.getAsMap(viewFields);
                if (!viewFieldsMap.containsKey("name")) {
                    FacilioField nameField = allFieldsAsMap.get("name");
                    extraInspectionTemplateFields.add(nameField);
                }
            }
            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS,extraInspectionTemplateFields);
        }
        return false;
    }
}
