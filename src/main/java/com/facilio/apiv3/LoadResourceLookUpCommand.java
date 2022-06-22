package com.facilio.apiv3;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadResourceLookUpCommand extends FacilioCommand {
    @Override
   public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        fetchLookupsList.add((SupplementRecord)fieldsAsMap.get("resource"));
        fetchLookupsList.add((SupplementRecord)fieldsAsMap.get("severity"));
        fetchLookupsList.add((SupplementRecord)fieldsAsMap.get("rule"));
        fetchLookupsList.add((SupplementRecord)fieldsAsMap.get("readingAlarmCategory"));
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}