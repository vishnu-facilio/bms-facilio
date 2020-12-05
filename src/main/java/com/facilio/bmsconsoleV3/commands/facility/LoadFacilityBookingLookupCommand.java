package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadFacilityBookingLookupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        SupplementRecord facility = (SupplementRecord) fieldsAsMap.get("facility");
        SupplementRecord reservedFor = (SupplementRecord) fieldsAsMap.get("reservedFor");
        SupplementRecord internalAttendees = (SupplementRecord) fieldsAsMap.get("internalAttendees");

        fetchLookupsList.add(facility);
        fetchLookupsList.add(reservedFor);
        fetchLookupsList.add(internalAttendees);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
