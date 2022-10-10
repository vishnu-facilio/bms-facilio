package com.facilio.faults;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericLoadSupplementsV3 {
    public static List<SupplementRecord> getLookupList(String moduleName, List<String> lookupFields) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        lookupFields.stream().forEach(x-> fetchLookupsList.add((SupplementRecord)fieldsAsMap.get(x)));
        return fetchLookupsList;
    }
}
