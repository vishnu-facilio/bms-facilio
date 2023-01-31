package com.facilio.bmsconsoleV3.commands.shift;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddBreakSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);

        List<FacilioField> moduleFields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(moduleFields);

        List<SupplementRecord> supplementRecords = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (supplementRecords == null) {
            supplementRecords = new ArrayList<>();
        }
        supplementRecords.add((MultiLookupField) fieldsAsMap.get("shifts"));
        supplementRecords.add((LargeTextField) fieldsAsMap.get("description"));

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementRecords);

        return false;
    }
}
