package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LoadFacilityLookupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule amenitiesModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.AMENITY);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        SupplementRecord manager = (SupplementRecord) fieldsAsMap.get("manager");
        SupplementRecord location = (SupplementRecord) fieldsAsMap.get("location");

        MultiLookupMeta amenities = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("amenities"));

        FacilioField nameField = FieldFactory.getField("description", "DESCRIPTION", amenitiesModule, FieldType.STRING);
        amenities.setSelectFields(Collections.singletonList(nameField));

        fetchLookupsList.add(manager);
        fetchLookupsList.add(location);
        fetchLookupsList.add(amenities);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
