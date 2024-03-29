package com.facilio.bmsconsoleV3.commands;

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

public class PMResourcePlannerSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = "pmResourcePlanner";
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (supplementFields == null) {
            supplementFields = new ArrayList<>();
            context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementFields);
        }

        supplementFields.add((SupplementRecord) fieldsAsMap.get("resource"));
        supplementFields.add((SupplementRecord) fieldsAsMap.get("jobPlan"));
        supplementFields.add((SupplementRecord) fieldsAsMap.get("assignedTo"));
        
        context.put(FacilioConstants.ContextNames.SORTING_QUERY,"ID");
        context.put(FacilioConstants.ContextNames.ORDER_TYPE,"asc");
        return false;
    }
}
