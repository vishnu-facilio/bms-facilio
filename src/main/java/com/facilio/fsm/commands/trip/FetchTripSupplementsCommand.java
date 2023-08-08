package com.facilio.fsm.commands.trip;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchTripSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        SupplementRecord people = (SupplementRecord) fieldsAsMap.get("people");
        SupplementRecord serviceAppointment = (SupplementRecord) fieldsAsMap.get("serviceAppointment");
        SupplementRecord startLocation = (SupplementRecord) fieldsAsMap.get("startLocation");
        SupplementRecord endLocation = (SupplementRecord) fieldsAsMap.get("endLocation");
        SupplementRecord serviceOrder = (SupplementRecord) fieldsAsMap.get("serviceOrder");
        SupplementRecord moduleState = (SupplementRecord) fieldsAsMap.get("moduleState");
        SupplementRecord approvalStatus = (SupplementRecord) fieldsAsMap.get("approvalStatus");

        fetchLookupsList.add(people);
        fetchLookupsList.add(serviceAppointment);
        fetchLookupsList.add(startLocation);
        fetchLookupsList.add(endLocation);
        fetchLookupsList.add(serviceOrder);
        fetchLookupsList.add(moduleState);
        fetchLookupsList.add(approvalStatus);


        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
