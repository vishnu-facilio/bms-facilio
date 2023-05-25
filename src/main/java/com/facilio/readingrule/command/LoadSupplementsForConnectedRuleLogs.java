package com.facilio.readingrule.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadSupplementsForConnectedRuleLogs extends FacilioCommand {
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
        supplementRecords.add((LargeTextField) fieldsAsMap.get("scriptLogs"));

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementRecords);
        return false;
    }
}
