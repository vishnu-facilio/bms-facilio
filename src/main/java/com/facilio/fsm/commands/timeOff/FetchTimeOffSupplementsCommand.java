package com.facilio.fsm.commands.timeOff;

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

public class FetchTimeOffSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        SupplementRecord people = (SupplementRecord) fieldsAsMap.get("people");
        SupplementRecord type = (SupplementRecord) fieldsAsMap.get("type");
        SupplementRecord moduleState = (SupplementRecord) fieldsAsMap.get("moduleState");
        SupplementRecord approvalStatus = (SupplementRecord) fieldsAsMap.get("approvalStatus");

        fetchLookupsList.add(people);
        fetchLookupsList.add(type);
        fetchLookupsList.add(moduleState);
        fetchLookupsList.add(approvalStatus);

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
