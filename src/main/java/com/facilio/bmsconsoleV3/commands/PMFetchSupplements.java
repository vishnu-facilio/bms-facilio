package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PMFetchSupplements extends FacilioCommand {
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
        supplementRecords.add((MultiLookupField) fieldsAsMap.get("sites"));
        for(FacilioField field : moduleFields){
            if(field instanceof LookupField){
                supplementRecords.add((LookupField)field);
            }
        }
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementRecords);
        return false;
    }
}
