package com.facilio.faults.bmsalarm;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.faults.GenericLoadSupplementsV3;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoadSupplementsForBMSAlarmCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SupplementRecord> fetchLookupsList = GenericLoadSupplementsV3.getLookupList(
                FacilioConstants.ContextNames.BMS_ALARM,
                Arrays.asList("resource", "severity")
        );
        fetchAdditionalFields(context);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
    private void fetchAdditionalFields(Context context) throws Exception{
        boolean fetchOnlyViewGroupColumn=(boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        FacilioView view=(FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        if(fetchOnlyViewGroupColumn && view!=null) {
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(allFields);
            List<FacilioField> extraBMSAlarmFields = new ArrayList<>();
            List<FacilioField> viewFields = view.getFields().stream().map(ViewField::getField).filter(viewField -> viewField != null).collect(Collectors.toList());
            Map<String, FacilioField> viewFieldsMap = FieldFactory.getAsMap(viewFields);
            if(viewFieldsMap.containsKey("acknowledgedBy")){
                extraBMSAlarmFields.add(allFieldsAsMap.get("acknowledged"));
            }
            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS, extraBMSAlarmFields);
        }
    }
}