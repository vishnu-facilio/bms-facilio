package com.facilio.fsm.commands.timeSheet;

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

public class FetchTimeSheetSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        SupplementRecord fieldAgent = (SupplementRecord) fieldsAsMap.get("fieldAgent");
        SupplementRecord serviceAppointment = (SupplementRecord) fieldsAsMap.get("serviceAppointment");
        SupplementRecord serviceOrder = (SupplementRecord) fieldsAsMap.get("serviceOrder");
        MultiLookupMeta serviceTasks = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("serviceTasks"));

        fetchLookupsList.add(fieldAgent);
        fetchLookupsList.add(serviceAppointment);
        fetchLookupsList.add(serviceOrder);
        fetchLookupsList.add(serviceTasks);

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}
