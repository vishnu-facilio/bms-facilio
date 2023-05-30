package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class addAnnouncementDefaultFields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        List<FacilioField> moduleFields = Constants.getModBean().getAllFields(ModuleFactory.getPeopleAnnouncementModule().getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(moduleFields);
        fields.add(fieldMap.get("isRead"));
        fields.add(fieldMap.get("createdBy"));
        context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS,fields);
        return false;
    }
}
