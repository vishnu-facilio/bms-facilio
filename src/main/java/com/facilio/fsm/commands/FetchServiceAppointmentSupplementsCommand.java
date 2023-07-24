package com.facilio.fsm.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.MultiLookupMeta;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchServiceAppointmentSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
            List<SupplementRecord> fetchLookupsList = new ArrayList<>();
            SupplementRecord site = (SupplementRecord) fieldsAsMap.get("site");
            SupplementRecord location = (SupplementRecord) fieldsAsMap.get("location");
            SupplementRecord serviceWorkorder = (SupplementRecord) fieldsAsMap.get("serviceOrder");
            SupplementRecord workorder = (SupplementRecord) fieldsAsMap.get("workorder");
            SupplementRecord inspection = (SupplementRecord) fieldsAsMap.get("inspection");
            SupplementRecord fieldAgent = (SupplementRecord) fieldsAsMap.get("fieldAgent");
            SupplementRecord territory = (SupplementRecord) fieldsAsMap.get("territory");

            MultiLookupMeta serviceTasks = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("serviceTasks"));


            fetchLookupsList.add(site);
            fetchLookupsList.add(location);
            fetchLookupsList.add(serviceWorkorder);
            fetchLookupsList.add(serviceTasks);
            fetchLookupsList.add(workorder);
            fetchLookupsList.add(inspection);
            fetchLookupsList.add(fieldAgent);
            fetchLookupsList.add(territory);

            context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);

        return false;
    }
}
